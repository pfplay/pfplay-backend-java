package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.presentation.dto.request.AdminCreatePartyroomRequest;
import com.pfplaybackend.api.admin.presentation.dto.request.BulkPreviewEnvironmentRequest;
import com.pfplaybackend.api.admin.presentation.dto.response.AdminPartyroomResponse;
import com.pfplaybackend.api.admin.presentation.dto.response.BulkPreviewEnvironmentResponse;
import com.pfplaybackend.api.admin.presentation.dto.response.SimulateReactionsResponse;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.application.service.PlaybackInfoService;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Crew;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Playback;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Admin service for managing partyrooms
 * Provides admin-specific partyroom operations including bulk creation
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPartyroomService {

    private final PartyroomRepository partyroomRepository;
    private final PartyroomConverter partyroomConverter;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomAccessService partyroomAccessService;
    private final AdminUserService adminUserService;
    private final PlaybackInfoService playbackInfoService;
    private final ReactionSimulationService reactionSimulationService;

    // ExecutorService for async reaction simulation
    private final ExecutorService reactionExecutor = Executors.newFixedThreadPool(10);

    /**
     * Create a single partyroom with designated host
     * Admin can specify which user will be the HOST
     *
     * @param request Admin partyroom creation request with hostUserId
     * @return Created partyroom information
     */
    @Transactional
    public AdminPartyroomResponse createPartyroomWithHost(AdminCreatePartyroomRequest request) {
        // 1. Parse and validate host user ID
        UserId hostUserId = parseUserId(request.getHostUserId());

        // 2. Validate link domain uniqueness
        String linkDomain = request.getLinkDomain();
        if (linkDomain == null || linkDomain.isEmpty()) {
            linkDomain = generateUniqueLinkDomain();
        } else {
            partyroomDomainService.checkIsLinkAddressDuplicated(linkDomain);
        }

        // 3. Create partyroom request
        CreatePartyroomRequest createRequest = new CreatePartyroomRequest(
                request.getTitle(),
                request.getIntroduction() != null ? request.getIntroduction() : "",
                linkDomain,
                request.getPlaybackTimeLimit()
        );

        // 4. Create partyroom
        Partyroom partyroom = createPartyroom(createRequest, StageType.GENERAL, hostUserId);

        // 5. Enter host into partyroom
        partyroomAccessService.enterByHost(hostUserId, partyroom);

        log.info("Admin created partyroom: partyroomId={}, hostUserId={}, title={}",
                partyroom.getPartyroomId().getId(), hostUserId.getUid(), partyroom.getTitle());

        return AdminPartyroomResponse.from(partyroom, hostUserId.getUid().toString());
    }

    /**
     * Create bulk preview environment for testing/demo
     * Creates multiple partyrooms with virtual members
     *
     * @param request Bulk creation request
     * @return Summary of created environment
     */
    @Transactional
    public BulkPreviewEnvironmentResponse createBulkPreviewEnvironment(BulkPreviewEnvironmentRequest request) {
        long startTime = System.currentTimeMillis();

        List<BulkPreviewEnvironmentResponse.PartyroomSummary> partyroomSummaries = new ArrayList<>();
        int totalVirtualMembers = 0;

        log.info("Starting bulk preview environment creation: {} partyrooms with {} users each",
                request.getPartyroomCount(), request.getUsersPerRoom());

        // Create each partyroom with virtual members
        for (int i = 1; i <= request.getPartyroomCount(); i++) {
            // 1. Generate partyroom details
            String title = String.format("%s %d", request.getTitlePrefix(), i);
            String linkDomain = generateLinkDomainForBulk(request.getLinkDomainPrefix(), i);

            // 2. Create virtual members for this partyroom
            List<Member> virtualMembers = new ArrayList<>();
            for (int j = 0; j < request.getUsersPerRoom(); j++) {
                String nickname = String.format("Virtual_%d_%d", i, j + 1);
                Member member = adminUserService.createVirtualMember(nickname, null, null);
                virtualMembers.add(member);
                totalVirtualMembers++;
            }

            // 3. First member becomes HOST
            UserId hostUserId = virtualMembers.get(0).getUserId();

            // 4. Create partyroom
            CreatePartyroomRequest createRequest = new CreatePartyroomRequest(
                    title,
                    request.getIntroduction() != null ? request.getIntroduction() : "Preview environment room",
                    linkDomain,
                    request.getPlaybackTimeLimit()
            );

            Partyroom partyroom = createPartyroom(createRequest, StageType.GENERAL, hostUserId);

            // 5. Enter HOST
            partyroomAccessService.enterByHost(hostUserId, partyroom);

            // 6. Enter other members as regular crew
            for (int j = 1; j < virtualMembers.size(); j++) {
                Member member = virtualMembers.get(j);
                enterMemberAsRegularCrew(partyroom, member.getUserId());
            }

            // 7. Build summary
            List<String> crewUserIds = virtualMembers.stream()
                    .map(m -> m.getUserId().getUid().toString())
                    .collect(Collectors.toList());

            BulkPreviewEnvironmentResponse.PartyroomSummary summary =
                    BulkPreviewEnvironmentResponse.PartyroomSummary.builder()
                            .partyroomId(partyroom.getPartyroomId().getId())
                            .title(partyroom.getTitle())
                            .linkDomain(partyroom.getLinkDomain())
                            .hostUserId(hostUserId.getUid().toString())
                            .crewCount(virtualMembers.size())
                            .crewUserIds(crewUserIds)
                            .build();

            partyroomSummaries.add(summary);

            log.info("Created partyroom {}/{}: partyroomId={}, hostUserId={}, crewCount={}",
                    i, request.getPartyroomCount(),
                    partyroom.getPartyroomId().getId(),
                    hostUserId.getUid(),
                    virtualMembers.size());
        }

        long executionTime = System.currentTimeMillis() - startTime;

        log.info("Bulk preview environment created: {} partyrooms, {} virtual members, {}ms",
                request.getPartyroomCount(), totalVirtualMembers, executionTime);

        return BulkPreviewEnvironmentResponse.builder()
                .totalPartyrooms(request.getPartyroomCount())
                .totalVirtualMembers(totalVirtualMembers)
                .executionTimeMs(executionTime)
                .partyrooms(partyroomSummaries)
                .build();
    }

    /**
     * Create partyroom (internal method)
     */
    private Partyroom createPartyroom(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        Partyroom partyroom = Partyroom.create(request, stageType, hostId);
        PartyroomData partyroomData = partyroomConverter.toData(partyroom);
        PartyroomData savedPartyroomData = partyroomRepository.save(partyroomData);
        return partyroomConverter.toDomain(savedPartyroomData);
    }

    /**
     * Enter a member as regular crew (not HOST)
     */
    private void enterMemberAsRegularCrew(Partyroom partyroom, UserId userId) {
        // Load the latest partyroom state
        PartyroomData partyroomData = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                .orElseThrow();
        Partyroom loadedPartyroom = partyroomConverter.toDomain(partyroomData);

        // Add as regular crew with LISTENER grade
        Partyroom updatedPartyroom = loadedPartyroom.addNewCrew(userId, AuthorityTier.FM, GradeType.LISTENER);

        partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));
    }

    /**
     * Generate unique link domain
     */
    private String generateUniqueLinkDomain() {
        String linkDomain;
        do {
            linkDomain = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        } while (isLinkDomainDuplicated(linkDomain));
        return linkDomain;
    }

    /**
     * Generate link domain for bulk creation
     */
    private String generateLinkDomainForBulk(String prefix, int index) {
        if (prefix == null || prefix.isEmpty()) {
            return String.format("preview_%d_%s", index,
                    UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8));
        }
        return String.format("%s_%d", prefix, index);
    }

    /**
     * Check if link domain is duplicated
     */
    private boolean isLinkDomainDuplicated(String linkDomain) {
        return partyroomRepository.findByLinkDomain(linkDomain).isPresent();
    }

    /**
     * Parse user ID from string
     */
    private UserId parseUserId(String userIdString) {
        try {
            Long id = Long.parseLong(userIdString);
            return new UserId(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format: " + userIdString, e);
        }
    }

    /**
     * Simulate reactions in a partyroom with realistic timing
     * Selects ~70% of crew members (excluding DJ) and makes them react:
     * - 50% of selected: LIKE reaction with 0-5s random delay
     * - 50% of selected: GRAB reaction with 0-5s random delay
     *
     * @param partyroomId Partyroom ID
     * @return Simulation result with reaction details
     */
    public SimulateReactionsResponse simulateReactions(Long partyroomId) {
        // 1. Load partyroom (in separate transaction to avoid long-running transaction)
        PartyroomData partyroomData = partyroomRepository.findById(partyroomId)
                .orElseThrow(() -> new IllegalArgumentException("Partyroom not found: " + partyroomId));
        Partyroom partyroom = partyroomConverter.toDomain(partyroomData);

        // 2. Get current playback
        PlaybackId playbackId = partyroom.getCurrentPlaybackId();
        if (playbackId == null) {
            throw new IllegalStateException("No active playback in partyroom: " + partyroomId);
        }

        Playback playback = playbackInfoService.getPlaybackById(playbackId);
        UserId djUserId = playback.getUserId();

        // 3. Get crew members excluding DJ
        List<Crew> eligibleCrew = partyroom.getCrewSet().stream()
                .filter(crew -> !crew.getUserId().equals(djUserId))
                .collect(Collectors.toList());

        if (eligibleCrew.isEmpty()) {
            throw new IllegalStateException("No crew members available (excluding DJ)");
        }

        // 4. Select ~70% of eligible crew randomly
        Collections.shuffle(eligibleCrew);
        int selectionCount = Math.max(1, (int) Math.ceil(eligibleCrew.size() * 0.7));
        List<Crew> selectedCrew = eligibleCrew.subList(0, selectionCount);

        log.info("Selected {}/{} crew members for reaction simulation", selectionCount, eligibleCrew.size());

        // 5. Split selected crew 50:50 into LIKE and GRAB groups
        Collections.shuffle(selectedCrew);
        int halfCount = selectedCrew.size() / 2;
        List<Crew> likeGroup = selectedCrew.subList(0, halfCount);
        List<Crew> grabGroup = selectedCrew.subList(halfCount, selectedCrew.size());

        // 6. Create async tasks for all reactions
        List<CompletableFuture<SimulateReactionsResponse.SimulatedReaction>> reactionFutures = new ArrayList<>();

        // LIKE reactions
        for (Crew crew : likeGroup) {
            CompletableFuture<SimulateReactionsResponse.SimulatedReaction> future = CompletableFuture.supplyAsync(() -> {
                int delayMs = ThreadLocalRandom.current().nextInt(0, 5001); // 0-5000ms
                try {
                    TimeUnit.MILLISECONDS.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Reaction simulation interrupted", e);
                }
                return reactionSimulationService.simulateReaction(
                        crew.getUserId(),
                        new CrewId(crew.getId()),
                        playbackId,
                        new PartyroomId(partyroomId),
                        ReactionType.LIKE,
                        delayMs
                );
            }, reactionExecutor);
            reactionFutures.add(future);
        }

        // GRAB reactions
        for (Crew crew : grabGroup) {
            CompletableFuture<SimulateReactionsResponse.SimulatedReaction> future = CompletableFuture.supplyAsync(() -> {
                int delayMs = ThreadLocalRandom.current().nextInt(0, 5001); // 0-5000ms
                try {
                    TimeUnit.MILLISECONDS.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Reaction simulation interrupted", e);
                }
                return reactionSimulationService.simulateReaction(
                        crew.getUserId(),
                        new CrewId(crew.getId()),
                        playbackId,
                        new PartyroomId(partyroomId),
                        ReactionType.GRAB,
                        delayMs
                );
            }, reactionExecutor);
            reactionFutures.add(future);
        }

        // 7. Wait for all reactions to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                reactionFutures.toArray(new CompletableFuture[0])
        );

        try {
            allFutures.get(10, TimeUnit.SECONDS); // Max 10 seconds wait
        } catch (Exception e) {
            log.error("Error waiting for reaction simulation to complete", e);
            throw new RuntimeException("Reaction simulation failed", e);
        }

        // 8. Collect results
        List<SimulateReactionsResponse.SimulatedReaction> reactions = reactionFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        // 9. Get updated playback aggregation
        Playback updatedPlayback = playbackInfoService.getPlaybackById(playbackId);

        log.info("Simulated reactions completed: partyroomId={}, playbackId={}, reactions={}, likes={}, grabs={}",
                partyroomId, playbackId.getId(), reactions.size(), likeGroup.size(), grabGroup.size());

        return SimulateReactionsResponse.builder()
                .partyroomId(partyroomId)
                .playbackId(playbackId.getId())
                .reactions(reactions)
                .aggregation(SimulateReactionsResponse.AggregationCounts.builder()
                        .likeCount(updatedPlayback.getLikeCount())
                        .dislikeCount(updatedPlayback.getDislikeCount())
                        .grabCount(updatedPlayback.getGrabCount())
                        .build())
                .build();
    }
}
