package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.adapter.in.web.dto.request.AdminCreatePartyroomRequest;
import com.pfplaybackend.api.admin.adapter.in.web.dto.request.BulkPreviewEnvironmentRequest;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.AdminPartyroomResponse;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.BulkPreviewEnvironmentResponse;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.SimulateReactionsResponse;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.application.service.PlaybackInfoService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.ReactionType;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.service.PartyroomDomainService;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.domain.value.PlaybackId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.common.domain.value.UserId;
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
    private final CrewRepository crewRepository;
    private final PartyroomDomainService partyroomDomainService;
    private final PartyroomAccessService partyroomAccessService;
    private final AdminUserService adminUserService;
    private final PlaybackInfoService playbackInfoService;
    private final ReactionSimulationService reactionSimulationService;

    // ExecutorService for async reaction simulation
    private final ExecutorService reactionExecutor = Executors.newFixedThreadPool(10);

    @Transactional
    public AdminPartyroomResponse createPartyroomWithHost(AdminCreatePartyroomRequest request) {
        UserId hostUserId = parseUserId(request.getHostUserId());

        String linkDomain = request.getLinkDomain();
        if (linkDomain == null || linkDomain.isEmpty()) {
            linkDomain = generateUniqueLinkDomain();
        } else {
            partyroomDomainService.checkIsLinkAddressDuplicated(linkDomain);
        }

        CreatePartyroomRequest createRequest = new CreatePartyroomRequest(
                request.getTitle(),
                request.getIntroduction() != null ? request.getIntroduction() : "",
                linkDomain,
                request.getPlaybackTimeLimit()
        );

        PartyroomData partyroom = createPartyroom(createRequest, StageType.GENERAL, hostUserId);
        partyroomAccessService.enterByHost(hostUserId, partyroom);

        log.info("Admin created partyroom: partyroomId={}, hostUserId={}, title={}",
                partyroom.getPartyroomId().getId(), hostUserId.getUid(), partyroom.getTitle());

        return AdminPartyroomResponse.from(partyroom, hostUserId.getUid().toString());
    }

    @Transactional
    public BulkPreviewEnvironmentResponse createBulkPreviewEnvironment(BulkPreviewEnvironmentRequest request) {
        long startTime = System.currentTimeMillis();

        List<BulkPreviewEnvironmentResponse.PartyroomSummary> partyroomSummaries = new ArrayList<>();
        int totalVirtualMembers = 0;

        log.info("Starting bulk preview environment creation: {} partyrooms with {} users each",
                request.getPartyroomCount(), request.getUsersPerRoom());

        for (int i = 1; i <= request.getPartyroomCount(); i++) {
            String title = String.format("%s %d", request.getTitlePrefix(), i);
            String linkDomain = generateLinkDomainForBulk(request.getLinkDomainPrefix(), i);

            List<MemberData> virtualMembers = new ArrayList<>();
            for (int j = 0; j < request.getUsersPerRoom(); j++) {
                String nickname = String.format("Virtual_%d_%d", i, j + 1);
                MemberData member = adminUserService.createVirtualMember(nickname, null, null);
                virtualMembers.add(member);
                totalVirtualMembers++;
            }

            UserId hostUserId = virtualMembers.get(0).getUserId();

            CreatePartyroomRequest createRequest = new CreatePartyroomRequest(
                    title,
                    request.getIntroduction() != null ? request.getIntroduction() : "Preview environment room",
                    linkDomain,
                    request.getPlaybackTimeLimit()
            );

            PartyroomData partyroom = createPartyroom(createRequest, StageType.GENERAL, hostUserId);
            partyroomAccessService.enterByHost(hostUserId, partyroom);

            for (int j = 1; j < virtualMembers.size(); j++) {
                MemberData member = virtualMembers.get(j);
                enterMemberAsRegularCrew(partyroom, member.getUserId());
            }

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

    private PartyroomData createPartyroom(CreatePartyroomRequest request, StageType stageType, UserId hostId) {
        PartyroomData partyroom = PartyroomData.create(request, stageType, hostId);
        return partyroomRepository.save(partyroom);
    }

    private void enterMemberAsRegularCrew(PartyroomData partyroom, UserId userId) {
        PartyroomData loadedPartyroom = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                .orElseThrow();

        CrewData crew = CrewData.create(loadedPartyroom, userId, AuthorityTier.FM, GradeType.LISTENER);
        crewRepository.save(crew);
    }

    private String generateUniqueLinkDomain() {
        String linkDomain;
        do {
            linkDomain = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        } while (isLinkDomainDuplicated(linkDomain));
        return linkDomain;
    }

    private String generateLinkDomainForBulk(String prefix, int index) {
        if (prefix == null || prefix.isEmpty()) {
            return String.format("preview_%d_%s", index,
                    UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8));
        }
        return String.format("%s_%d", prefix, index);
    }

    private boolean isLinkDomainDuplicated(String linkDomain) {
        return partyroomRepository.findByLinkDomain(linkDomain).isPresent();
    }

    private UserId parseUserId(String userIdString) {
        try {
            Long id = Long.parseLong(userIdString);
            return new UserId(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format: " + userIdString, e);
        }
    }

    public SimulateReactionsResponse simulateReactions(Long partyroomId) {
        PartyroomData partyroom = partyroomRepository.findById(partyroomId)
                .orElseThrow(() -> new IllegalArgumentException("Partyroom not found: " + partyroomId));

        PlaybackId playbackId = partyroom.getCurrentPlaybackId();
        if (playbackId == null) {
            throw new IllegalStateException("No active playback in partyroom: " + partyroomId);
        }

        PlaybackData playback = playbackInfoService.getPlaybackById(playbackId);
        UserId djUserId = playback.getUserId();

        List<CrewData> eligibleCrew = crewRepository.findByPartyroomDataIdAndIsActiveTrue(partyroomId).stream()
                .filter(crew -> !crew.getUserId().equals(djUserId))
                .collect(Collectors.toList());

        if (eligibleCrew.isEmpty()) {
            throw new IllegalStateException("No crew members available (excluding DJ)");
        }

        Collections.shuffle(eligibleCrew);
        int selectionCount = Math.max(1, (int) Math.ceil(eligibleCrew.size() * 0.7));
        List<CrewData> selectedCrew = eligibleCrew.subList(0, selectionCount);

        log.info("Selected {}/{} crew members for reaction simulation", selectionCount, eligibleCrew.size());

        Collections.shuffle(selectedCrew);
        int halfCount = selectedCrew.size() / 2;
        List<CrewData> likeGroup = selectedCrew.subList(0, halfCount);
        List<CrewData> grabGroup = selectedCrew.subList(halfCount, selectedCrew.size());

        List<CompletableFuture<SimulateReactionsResponse.SimulatedReaction>> reactionFutures = new ArrayList<>();

        for (CrewData crew : likeGroup) {
            CompletableFuture<SimulateReactionsResponse.SimulatedReaction> future = CompletableFuture.supplyAsync(() -> {
                int delayMs = ThreadLocalRandom.current().nextInt(0, 5001);
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

        for (CrewData crew : grabGroup) {
            CompletableFuture<SimulateReactionsResponse.SimulatedReaction> future = CompletableFuture.supplyAsync(() -> {
                int delayMs = ThreadLocalRandom.current().nextInt(0, 5001);
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

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                reactionFutures.toArray(new CompletableFuture[0])
        );

        try {
            allFutures.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error waiting for reaction simulation to complete", e);
            throw new RuntimeException("Reaction simulation failed", e);
        }

        List<SimulateReactionsResponse.SimulatedReaction> reactions = reactionFutures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        PlaybackData updatedPlayback = playbackInfoService.getPlaybackById(playbackId);

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
