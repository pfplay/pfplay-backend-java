package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.domain.DemoTrackConstants;
import com.pfplaybackend.api.admin.adapter.in.web.dto.request.InitializeDemoEnvironmentRequest;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.DemoEnvironmentResponse;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.AdminPartyroomListResponse;
import com.pfplaybackend.api.admin.adapter.in.web.dto.response.DemoEnvironmentStatusResponse;
import com.pfplaybackend.api.admin.domain.exception.AdminException;
import com.pfplaybackend.api.admin.application.util.NicknameGenerator;
import com.pfplaybackend.api.common.exception.ExceptionCreator;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarBodyResourceRepository;
import com.pfplaybackend.api.user.adapter.out.persistence.AvatarFaceResourceRepository;
import com.pfplaybackend.api.common.domain.value.Duration;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.application.service.PlaybackManagementService;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.entity.data.DjData;
import com.pfplaybackend.api.party.domain.entity.data.DjQueueData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomPlaybackData;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.value.LinkDomain;
import com.pfplaybackend.api.party.domain.value.PlaybackTimeLimit;
import com.pfplaybackend.api.party.domain.value.CrewId;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjQueueRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.DjRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomPlaybackRepository;
import com.pfplaybackend.api.party.adapter.out.persistence.PartyroomRepository;
import com.pfplaybackend.api.party.adapter.in.web.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.adapter.out.persistence.PlaylistRepository;
import com.pfplaybackend.api.playlist.adapter.out.persistence.TrackRepository;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.adapter.out.persistence.MemberRepository;
import com.pfplaybackend.api.user.domain.entity.data.MemberData;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.common.domain.value.UserId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Admin service for initializing complete demo environment
 * <p>
 * Creates full demo setup with:
 * - 410 virtual members (13 special + 397 regular)
 * - 12 general partyrooms (main stage already exists from app initialization)
 * - Automatic room entry and DJ queue registration
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminDemoService {

    private final AdminUserService adminUserService;
    private final MemberRepository memberRepository;
    private final PartyroomRepository partyroomRepository;
    private final PartyroomPlaybackRepository partyroomPlaybackRepository;
    private final DjQueueRepository djQueueRepository;
    private final CrewRepository crewRepository;
    private final DjRepository djRepository;
    private final PartyroomAccessService partyroomAccessService;
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final AvatarBodyResourceRepository avatarBodyResourceRepository;
    private final AvatarFaceResourceRepository avatarFaceResourceRepository;
    private final PlaybackManagementService playbackManagementService;

    private static final int TOTAL_MEMBERS = 400;
    private static final int SPECIAL_MEMBERS = 13;  // 1 main stage DJ + 12 general room hosts
    private static final int MAIN_STAGE_CREW = 40;
    private static final int GENERAL_ROOM_CREW = 30;
    private static final int GENERAL_ROOMS_COUNT = 12;

    private final Random random = new Random();

    @Transactional
    public DemoEnvironmentResponse initializeDemoEnvironment(InitializeDemoEnvironmentRequest request) {
        long startTime = System.currentTimeMillis();

        log.info("Starting demo environment initialization...");

        // Step 0: Find existing main stage
        log.info("Step 0: Finding existing main stage...");
        PartyroomData mainStage = findMainStage();

        // Get available avatar resources
        List<AvatarBodyResourceData> avatarBodies = avatarBodyResourceRepository.findAll();
        List<AvatarFaceResourceData> avatarFaces = avatarFaceResourceRepository.findAll();

        if (avatarBodies.isEmpty() || avatarFaces.isEmpty()) {
            throw ExceptionCreator.create(AdminException.AVATAR_RESOURCES_NOT_INITIALIZED);
        }

        // Step 1: Create virtual members
        log.info("Step 1: Creating {} virtual members...", TOTAL_MEMBERS);
        List<MemberData> specialMembers = new ArrayList<>();
        List<MemberData> regularMembers = new ArrayList<>();

        createVirtualMembers(specialMembers, regularMembers, avatarBodies, avatarFaces);

        // Step 2: Create general partyrooms only (main stage already exists)
        log.info("Step 2: Creating {} general partyrooms...", GENERAL_ROOMS_COUNT);
        List<PartyroomData> generalRooms = createGeneralRooms(request, specialMembers);

        // Step 3: Enter members into rooms
        log.info("Step 3: Entering members into rooms...");
        enterMembersIntoRooms(mainStage, generalRooms, specialMembers, regularMembers);

        // Step 4: Register DJs (optional)
        int djsRegistered = 0;
        if (request.getRegisterDjs()) {
            log.info("Step 4: Registering DJs in queues...");
            djsRegistered = registerDjsInQueues(mainStage, generalRooms, specialMembers);
        }

        long executionTime = System.currentTimeMillis() - startTime;

        log.info("Demo environment initialized successfully in {}ms", executionTime);
        log.info("Created: {} members, {} general partyrooms, {} DJs",
                TOTAL_MEMBERS, GENERAL_ROOMS_COUNT, djsRegistered);

        return buildResponse(mainStage, generalRooms, specialMembers, djsRegistered, executionTime);
    }

    private void createVirtualMembers(
            List<MemberData> specialMembers,
            List<MemberData> regularMembers,
            List<AvatarBodyResourceData> avatarBodies,
            List<AvatarFaceResourceData> avatarFaces) {

        for (int i = 0; i < TOTAL_MEMBERS; i++) {
            String nickname = NicknameGenerator.generateUnique(i + 1);

            AvatarBodyResourceData randomBody = avatarBodies.get(random.nextInt(avatarBodies.size()));
            AvatarBodyUri avatarBody = new AvatarBodyUri(randomBody.getResourceUri());

            AvatarFaceUri avatarFace;
            if (randomBody.isCombinable()) {
                avatarFace = generateRandomNftFaceUri();
            } else {
                avatarFace = new AvatarFaceUri();
            }

            MemberData member = adminUserService.createVirtualMember(nickname, avatarBody, avatarFace);

            if (i < SPECIAL_MEMBERS) {
                createPlaylistAndTrack(member.getUserId());
                specialMembers.add(member);
                log.debug("Created special member {}/{}: {} (combinable: {})",
                        i + 1, SPECIAL_MEMBERS, nickname, randomBody.isCombinable());
            } else {
                regularMembers.add(member);
            }

            if ((i + 1) % 50 == 0) {
                log.info("Created {}/{} members...", i + 1, TOTAL_MEMBERS);
            }
        }

        log.info("Created {} special members and {} regular members",
                specialMembers.size(), regularMembers.size());
    }

    private PartyroomData findMainStage() {
        PartyroomData mainStage = partyroomRepository.findAll().stream()
                .filter(p -> p.getStageType() == StageType.MAIN)
                .findFirst()
                .orElseThrow(() -> ExceptionCreator.create(AdminException.MAIN_STAGE_NOT_FOUND));

        log.info("Found existing main stage: partyroomId={}", mainStage.getId());
        return mainStage;
    }

    private void createPlaylistAndTrack(UserId userId) {
        PlaylistData playlist = PlaylistData.create(1, "DJ Playlist", PlaylistType.PLAYLIST, userId);
        PlaylistData savedPlaylist = playlistRepository.save(playlist);

        DemoTrackConstants.TrackInfo track = DemoTrackConstants.getRandomTrack();
        TrackData trackData = TrackData.builder()
                .playlistData(savedPlaylist)
                .name(track.getName())
                .linkId(track.getLinkId())
                .duration(Duration.fromString(track.getDuration()))
                .thumbnailImage(track.getThumbnailImage())
                .orderNumber(1)
                .build();

        trackRepository.save(trackData);
    }

    private List<PartyroomData> createGeneralRooms(
            InitializeDemoEnvironmentRequest request,
            List<MemberData> specialMembers) {

        List<PartyroomData> rooms = new ArrayList<>();

        for (int i = 0; i < GENERAL_ROOMS_COUNT; i++) {
            UserId hostUserId = specialMembers.get(i + 1).getUserId();
            String title = String.format("%s %d", request.getTitlePrefix(), i + 1);
            String linkDomain = String.format("demo-room-%d", i + 1);

            CreatePartyroomRequest createRequest = new CreatePartyroomRequest(
                    title,
                    request.getIntroduction(),
                    linkDomain,
                    request.getPlaybackTimeLimit()
            );

            PartyroomData partyroom = PartyroomData.create(
                    createRequest.getTitle(), createRequest.getIntroduction(),
                    LinkDomain.of(createRequest.getLinkDomain()),
                    PlaybackTimeLimit.ofMinutes(createRequest.getPlaybackTimeLimit()),
                    StageType.GENERAL, hostUserId);
            PartyroomData savedPartyroom = partyroomRepository.save(partyroom);
            partyroomPlaybackRepository.save(PartyroomPlaybackData.createFor(savedPartyroom.getId()));
            djQueueRepository.save(DjQueueData.createFor(savedPartyroom.getId()));

            // Enter host
            partyroomAccessService.enterByHost(hostUserId, savedPartyroom);

            rooms.add(savedPartyroom);
            log.info("Created general room {}/{}: partyroomId={}, title={}, host={}",
                    i + 1, GENERAL_ROOMS_COUNT, savedPartyroom.getId(), title, hostUserId.getUid());
        }

        return rooms;
    }

    private void enterMembersIntoRooms(
            PartyroomData mainStage,
            List<PartyroomData> generalRooms,
            List<MemberData> specialMembers,
            List<MemberData> regularMembers) {

        int memberIndex = 0;

        MemberData mainStageDj = specialMembers.get(0);
        enterMemberAsRegularCrew(mainStage, mainStageDj.getUserId());

        for (int i = 0; i < MAIN_STAGE_CREW - 1 && memberIndex < regularMembers.size(); i++) {
            MemberData member = regularMembers.get(memberIndex++);
            enterMemberAsRegularCrew(mainStage, member.getUserId());
        }
        log.info("Entered {} crew into main stage (including 1 special member as DJ)", MAIN_STAGE_CREW);

        for (int roomIdx = 0; roomIdx < generalRooms.size(); roomIdx++) {
            PartyroomData room = generalRooms.get(roomIdx);

            for (int i = 0; i < GENERAL_ROOM_CREW - 1 && memberIndex < regularMembers.size(); i++) {
                MemberData member = regularMembers.get(memberIndex++);
                enterMemberAsRegularCrew(room, member.getUserId());
            }
            log.info("Entered {} crew into room {}/{}", GENERAL_ROOM_CREW, roomIdx + 1, GENERAL_ROOMS_COUNT);
        }

        log.info("Total members entered: {} (used {} regular members)",
                MAIN_STAGE_CREW + (GENERAL_ROOM_CREW * GENERAL_ROOMS_COUNT), memberIndex);
    }

    private void enterMemberAsRegularCrew(PartyroomData partyroom, UserId userId) {
        PartyroomData loadedPartyroom = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                .orElseThrow();

        CrewData crew = CrewData.create(loadedPartyroom, userId, AuthorityTier.FM, GradeType.LISTENER);
        crewRepository.save(crew);
    }

    private int registerDjsInQueues(
            PartyroomData mainStage,
            List<PartyroomData> generalRooms,
            List<MemberData> specialMembers) {

        int djCount = 0;

        registerDjInRoom(mainStage, specialMembers.get(0).getUserId());
        djCount++;

        for (int i = 0; i < generalRooms.size(); i++) {
            PartyroomData room = generalRooms.get(i);
            UserId hostUserId = specialMembers.get(i + 1).getUserId();
            registerDjInRoom(room, hostUserId);
            djCount++;
        }

        log.info("Registered {} DJs in queues", djCount);
        return djCount;
    }

    private void registerDjInRoom(PartyroomData partyroom, UserId userId) {
        Optional<PlaylistData> playlistOpt = playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(
                userId, PlaylistType.PLAYLIST).stream().findFirst();

        if (playlistOpt.isEmpty()) {
            log.warn("No playlist found for user {}, skipping DJ registration", userId.getUid());
            return;
        }

        PlaylistData playlist = playlistOpt.get();

        PartyroomData loadedPartyroom = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                .orElseThrow();
        PartyroomPlaybackData playbackState = partyroomPlaybackRepository.findById(loadedPartyroom.getId()).orElseThrow();

        boolean isPostActivationProcessingRequired = !playbackState.isActivated();

        // Find crew
        CrewData crew = crewRepository.findByPartyroomDataIdAndUserId(loadedPartyroom.getId(), userId)
                .orElseThrow();
        CrewId crewId = new CrewId(crew.getId());

        // Check if already registered
        if (djRepository.existsByPartyroomDataIdAndCrewId(loadedPartyroom.getId(), crewId)) {
            log.warn("DJ already registered for user {}, skipping", userId.getUid());
            return;
        }

        // Calculate next order number
        List<DjData> queuedDjs = djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(loadedPartyroom.getId());
        int nextOrder = queuedDjs.size() + 1;

        // Create and save DJ
        DjData dj = DjData.create(loadedPartyroom, new PlaylistId(playlist.getId()), crewId, nextOrder);
        djRepository.save(dj);

        playbackState.activate(null, null);
        partyroomPlaybackRepository.save(playbackState);

        // Start playback if this is the first DJ
        if (isPostActivationProcessingRequired) {
            playbackManagementService.start(loadedPartyroom);
            log.info("Started playback for partyroom: partyroomId={}, djUserId={}",
                    partyroom.getPartyroomId().getId(), userId.getUid());
        }

        log.debug("Registered DJ: userId={}, playlistId={}, partyroomId={}",
                userId.getUid(), playlist.getId(), partyroom.getPartyroomId().getId());
    }

    private DemoEnvironmentResponse buildResponse(
            PartyroomData mainStage,
            List<PartyroomData> generalRooms,
            List<MemberData> specialMembers,
            int djsRegistered,
            long executionTime) {

        UserId mainStageDjUserId = specialMembers.get(0).getUserId();
        Long mainStagePlaylistId = findPlaylistId(mainStageDjUserId);

        DemoEnvironmentResponse.PartyroomDetail mainStageDetail = DemoEnvironmentResponse.PartyroomDetail.builder()
                .partyroomId(mainStage.getPartyroomId().getId())
                .stageType(mainStage.getStageType().name())
                .title(mainStage.getTitle())
                .linkDomain(mainStage.getLinkDomain().getValue())
                .hostUserId(null)
                .totalCrewCount(MAIN_STAGE_CREW)
                .djUserId(mainStageDjUserId.getUid().toString())
                .playlistId(mainStagePlaylistId)
                .build();

        List<DemoEnvironmentResponse.PartyroomDetail> generalRoomDetails = new ArrayList<>();
        for (int i = 0; i < generalRooms.size(); i++) {
            PartyroomData room = generalRooms.get(i);
            UserId hostUserId = specialMembers.get(i + 1).getUserId();
            Long playlistId = findPlaylistId(hostUserId);

            DemoEnvironmentResponse.PartyroomDetail detail = DemoEnvironmentResponse.PartyroomDetail.builder()
                    .partyroomId(room.getPartyroomId().getId())
                    .stageType(room.getStageType().name())
                    .title(room.getTitle())
                    .linkDomain(room.getLinkDomain().getValue())
                    .hostUserId(hostUserId.getUid().toString())
                    .totalCrewCount(GENERAL_ROOM_CREW)
                    .djUserId(hostUserId.getUid().toString())
                    .playlistId(playlistId)
                    .build();

            generalRoomDetails.add(detail);
        }

        return DemoEnvironmentResponse.builder()
                .totalMembers(TOTAL_MEMBERS)
                .specialMembers(SPECIAL_MEMBERS)
                .totalPartyrooms(1 + GENERAL_ROOMS_COUNT)
                .totalDjsRegistered(djsRegistered)
                .executionTimeMs(executionTime)
                .mainStage(mainStageDetail)
                .generalRooms(generalRoomDetails)
                .build();
    }

    private Long findPlaylistId(UserId userId) {
        Optional<PlaylistData> playlistOpt = playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(
                userId, PlaylistType.PLAYLIST).stream().findFirst();
        return playlistOpt.map(PlaylistData::getId).orElse(null);
    }

    private AvatarFaceUri generateRandomNftFaceUri() {
        int faceNumber = random.nextInt(30) + 1;
        String faceFileName = String.format("ava_nft_tmp_%03d.png", faceNumber);
        String faceUri = "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/" +
                "ava_nft_tmp%2F" + faceFileName + "?alt=media";
        return new AvatarFaceUri(faceUri);
    }

    @Transactional(readOnly = true)
    public DemoEnvironmentStatusResponse getDemoEnvironmentStatus() {
        long virtualMemberCount = memberRepository.countByProviderType(ProviderType.ADMIN);
        long generalRoomCount = partyroomRepository.findAll().stream()
                .filter(p -> !p.isTerminated() && p.getStageType() == StageType.GENERAL)
                .count();

        boolean initialized = virtualMemberCount > 0;

        return DemoEnvironmentStatusResponse.builder()
                .initialized(initialized)
                .virtualMemberCount(virtualMemberCount)
                .generalRoomCount(generalRoomCount)
                .build();
    }

    @Transactional(readOnly = true)
    public AdminPartyroomListResponse getPartyrooms() {
        List<AdminPartyroomListResponse.PartyroomItem> items = partyroomRepository.findAll().stream()
                .filter(p -> !p.isTerminated())
                .map(p -> {
                    int crewCount = (int) crewRepository.countByPartyroomDataIdAndIsActiveTrue(p.getId());
                    int djCount = djRepository.findByPartyroomDataIdOrderByOrderNumberAsc(p.getId()).size();
                    boolean isPlaybackActivated = partyroomPlaybackRepository.findById(p.getId())
                            .map(PartyroomPlaybackData::isActivated).orElse(false);
                    return AdminPartyroomListResponse.PartyroomItem.builder()
                            .partyroomId(p.getId())
                            .stageType(p.getStageType().name())
                            .title(p.getTitle())
                            .linkDomain(p.getLinkDomain().getValue())
                            .crewCount(crewCount)
                            .djCount(djCount)
                            .isPlaybackActivated(isPlaybackActivated)
                            .build();
                })
                .toList();

        return AdminPartyroomListResponse.builder()
                .partyrooms(items)
                .build();
    }
}
