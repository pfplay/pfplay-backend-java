package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.domain.DemoTrackConstants;
import com.pfplaybackend.api.admin.presentation.dto.request.InitializeDemoEnvironmentRequest;
import com.pfplaybackend.api.admin.presentation.dto.response.DemoEnvironmentResponse;
import com.pfplaybackend.api.admin.presentation.dto.response.AdminPartyroomListResponse;
import com.pfplaybackend.api.admin.presentation.dto.response.DemoEnvironmentStatusResponse;
import com.pfplaybackend.api.admin.util.NicknameGenerator;
import com.pfplaybackend.api.avatarresource.repository.AvatarBodyResourceRepository;
import com.pfplaybackend.api.avatarresource.repository.AvatarFaceResourceRepository;
import com.pfplaybackend.api.common.config.security.enums.ProviderType;
import com.pfplaybackend.api.common.enums.AuthorityTier;
import com.pfplaybackend.api.party.application.service.PartyroomAccessService;
import com.pfplaybackend.api.party.application.service.PlaybackManagementService;
import com.pfplaybackend.api.party.domain.entity.converter.PartyroomConverter;
import com.pfplaybackend.api.party.domain.entity.data.PartyroomData;
import com.pfplaybackend.api.party.domain.entity.domainmodel.Partyroom;
import com.pfplaybackend.api.party.domain.enums.GradeType;
import com.pfplaybackend.api.party.domain.enums.StageType;
import com.pfplaybackend.api.party.domain.value.PlaylistId;
import com.pfplaybackend.api.party.infrastructure.repository.PartyroomRepository;
import com.pfplaybackend.api.party.interfaces.api.rest.payload.request.management.CreatePartyroomRequest;
import com.pfplaybackend.api.playlist.domain.entity.data.PlaylistData;
import com.pfplaybackend.api.playlist.domain.entity.data.TrackData;
import com.pfplaybackend.api.playlist.domain.entity.domainmodel.Playlist;
import com.pfplaybackend.api.playlist.domain.enums.PlaylistType;
import com.pfplaybackend.api.playlist.repository.PlaylistRepository;
import com.pfplaybackend.api.playlist.repository.TrackRepository;
import com.pfplaybackend.api.user.domain.entity.data.AvatarBodyResourceData;
import com.pfplaybackend.api.user.domain.entity.data.AvatarFaceResourceData;
import com.pfplaybackend.api.user.repository.MemberRepository;
import com.pfplaybackend.api.user.domain.entity.domainmodel.Member;
import com.pfplaybackend.api.user.domain.value.AvatarBodyUri;
import com.pfplaybackend.api.user.domain.value.AvatarFaceUri;
import com.pfplaybackend.api.user.domain.value.UserId;
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
    private final PartyroomConverter partyroomConverter;
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

    /**
     * Initialize complete demo environment
     * <p>
     * Process:
     * 1. Find existing main stage (created during app initialization)
     * 2. Create 400 virtual members (13 special with playlists)
     * 3. Create 12 general partyrooms
     * 4. Enter members into rooms
     * 5. Register DJs in queue (optional)
     *
     * @param request Demo initialization request
     * @return Demo environment response with created details
     */
    @Transactional
    public DemoEnvironmentResponse initializeDemoEnvironment(InitializeDemoEnvironmentRequest request) {
        long startTime = System.currentTimeMillis();

        log.info("Starting demo environment initialization...");

        // Step 0: Find existing main stage
        log.info("Step 0: Finding existing main stage...");
        Partyroom mainStage = findMainStage();

        // Get available avatar resources
        List<AvatarBodyResourceData> avatarBodies = avatarBodyResourceRepository.findAll();
        List<AvatarFaceResourceData> avatarFaces = avatarFaceResourceRepository.findAll();

        if (avatarBodies.isEmpty() || avatarFaces.isEmpty()) {
            throw new IllegalStateException("Avatar resources not initialized. Please run avatar initialization first.");
        }

        // Step 1: Create virtual members
        log.info("Step 1: Creating {} virtual members...", TOTAL_MEMBERS);
        List<Member> specialMembers = new ArrayList<>();
        List<Member> regularMembers = new ArrayList<>();

        createVirtualMembers(specialMembers, regularMembers, avatarBodies, avatarFaces);

        // Step 2: Create general partyrooms only (main stage already exists)
        log.info("Step 2: Creating {} general partyrooms...", GENERAL_ROOMS_COUNT);
        List<Partyroom> generalRooms = createGeneralRooms(request, specialMembers);

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

    /**
     * Step 1: Create virtual members
     */
    private void createVirtualMembers(
            List<Member> specialMembers,
            List<Member> regularMembers,
            List<AvatarBodyResourceData> avatarBodies,
            List<AvatarFaceResourceData> avatarFaces) {

        for (int i = 0; i < TOTAL_MEMBERS; i++) {
            String nickname = NicknameGenerator.generateUnique(i + 1);

            // Select random body
            AvatarBodyResourceData randomBody = avatarBodies.get(random.nextInt(avatarBodies.size()));
            AvatarBodyUri avatarBody = new AvatarBodyUri(randomBody.getResourceUri());

            // Check isCombinable to determine face assignment
            AvatarFaceUri avatarFace;
            if (randomBody.isCombinable()) {
                // Combinable body: use NFT face (ava_nft_tmp pattern)
                avatarFace = generateRandomNftFaceUri();
            } else {
                // Non-combinable body: no face (SINGLE_BODY)
                avatarFace = new AvatarFaceUri();  // Empty
            }

            Member member = adminUserService.createVirtualMember(nickname, avatarBody, avatarFace);

            if (i < SPECIAL_MEMBERS) {
                // Special members: create playlist and track
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

    /**
     * Find existing main stage
     * Main stage is created during application initialization
     */
    private Partyroom findMainStage() {
        Optional<PartyroomData> mainStageOpt = partyroomRepository.findAll().stream()
                .filter(p -> p.getStageType() == StageType.MAIN)
                .findFirst();

        if (mainStageOpt.isEmpty()) {
            throw new IllegalStateException("Main stage not found. It should be created during application initialization.");
        }

        PartyroomData mainStageData = mainStageOpt.get();
        Partyroom mainStage = partyroomConverter.toDomain(mainStageData);
        log.info("Found existing main stage: partyroomId={}", mainStageData.getId());
        return mainStage;
    }

    /**
     * Create playlist and track for special member
     */
    private void createPlaylistAndTrack(UserId userId) {
        // Create playlist
        Playlist playlist = Playlist.create(1, "DJ Playlist", PlaylistType.PLAYLIST, userId);
        PlaylistData savedPlaylist = playlistRepository.save(playlist.toData());

        // Add random track
        DemoTrackConstants.TrackInfo track = DemoTrackConstants.getRandomTrack();
        TrackData trackData = TrackData.builder()
                .playlistData(savedPlaylist)
                .name(track.getName())
                .linkId(track.getLinkId())
                .duration(track.getDuration())
                .thumbnailImage(track.getThumbnailImage())
                .orderNumber(1)
                .build();

        trackRepository.save(trackData);
    }

    /**
     * Step 2: Create general rooms
     * First special member is for main stage DJ, so hosts start from index 1
     */
    private List<Partyroom> createGeneralRooms(
            InitializeDemoEnvironmentRequest request,
            List<Member> specialMembers) {

        List<Partyroom> rooms = new ArrayList<>();

        for (int i = 0; i < GENERAL_ROOMS_COUNT; i++) {
            // First special member (index 0) is main stage DJ
            // General room hosts start from index 1
            UserId hostUserId = specialMembers.get(i + 1).getUserId();
            String title = String.format("%s %d", request.getTitlePrefix(), i + 1);
            String linkDomain = String.format("demo-room-%d", i + 1);

            CreatePartyroomRequest createRequest = new CreatePartyroomRequest(
                    title,
                    request.getIntroduction(),
                    linkDomain,
                    request.getPlaybackTimeLimit()
            );

            Partyroom partyroom = Partyroom.create(createRequest, StageType.GENERAL, hostUserId);
            PartyroomData savedData = partyroomRepository.save(partyroomConverter.toData(partyroom));
            Partyroom savedPartyroom = partyroomConverter.toDomain(savedData);

            // Enter host
            partyroomAccessService.enterByHost(hostUserId, savedPartyroom);

            rooms.add(savedPartyroom);
            log.info("Created general room {}/{}: partyroomId={}, title={}, host={}",
                    i + 1, GENERAL_ROOMS_COUNT, savedData.getId(), title, hostUserId.getUid());
        }

        return rooms;
    }

    /**
     * Step 3: Enter members into rooms
     */
    private void enterMembersIntoRooms(
            Partyroom mainStage,
            List<Partyroom> generalRooms,
            List<Member> specialMembers,
            List<Member> regularMembers) {

        int memberIndex = 0;

        // Enter members into main stage (50 total, no host since it's pre-created)
        // Include first special member (main stage DJ) as regular crew
        Member mainStageDj = specialMembers.get(0);
        enterMemberAsRegularCrew(mainStage, mainStageDj.getUserId());

        for (int i = 0; i < MAIN_STAGE_CREW - 1 && memberIndex < regularMembers.size(); i++) {
            Member member = regularMembers.get(memberIndex++);
            enterMemberAsRegularCrew(mainStage, member.getUserId());
        }
        log.info("Entered {} crew into main stage (including 1 special member as DJ)", MAIN_STAGE_CREW);

        // Enter members into general rooms (30 each, host already entered, so 29 more each)
        for (int roomIdx = 0; roomIdx < generalRooms.size(); roomIdx++) {
            Partyroom room = generalRooms.get(roomIdx);

            for (int i = 0; i < GENERAL_ROOM_CREW - 1 && memberIndex < regularMembers.size(); i++) {
                Member member = regularMembers.get(memberIndex++);
                enterMemberAsRegularCrew(room, member.getUserId());
            }
            log.info("Entered {} crew into room {}/{}", GENERAL_ROOM_CREW, roomIdx + 1, GENERAL_ROOMS_COUNT);
        }

        log.info("Total members entered: {} (used {} regular members)",
                MAIN_STAGE_CREW + (GENERAL_ROOM_CREW * GENERAL_ROOMS_COUNT), memberIndex);
    }

    /**
     * Enter a member as regular crew (not HOST)
     */
    private void enterMemberAsRegularCrew(Partyroom partyroom, UserId userId) {
        PartyroomData partyroomData = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                .orElseThrow();
        Partyroom loadedPartyroom = partyroomConverter.toDomain(partyroomData);

        Partyroom updatedPartyroom = loadedPartyroom.addNewCrew(userId, AuthorityTier.FM, GradeType.LISTENER);
        partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));
    }

    /**
     * Step 4: Register DJs in queues
     */
    private int registerDjsInQueues(
            Partyroom mainStage,
            List<Partyroom> generalRooms,
            List<Member> specialMembers) {

        int djCount = 0;

        // Register DJ in main stage (first special member)
        registerDjInRoom(mainStage, specialMembers.get(0).getUserId());
        djCount++;

        // Register DJs in general rooms
        for (int i = 0; i < generalRooms.size(); i++) {
            Partyroom room = generalRooms.get(i);
            UserId hostUserId = specialMembers.get(i + 1).getUserId();
            registerDjInRoom(room, hostUserId);
            djCount++;
        }

        log.info("Registered {} DJs in queues", djCount);
        return djCount;
    }

    /**
     * Register DJ in room queue
     * Mimics DjManagementService.enqueueDj() logic
     */
    private void registerDjInRoom(Partyroom partyroom, UserId userId) {
        // Find user's playlist
        Optional<PlaylistData> playlistOpt = playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(
                userId, PlaylistType.PLAYLIST).stream().findFirst();

        if (playlistOpt.isEmpty()) {
            log.warn("No playlist found for user {}, skipping DJ registration", userId.getUid());
            return;
        }

        PlaylistData playlist = playlistOpt.get();

        // Load latest partyroom state
        PartyroomData partyroomData = partyroomRepository.findById(partyroom.getPartyroomId().getId())
                .orElseThrow();
        Partyroom loadedPartyroom = partyroomConverter.toDomain(partyroomData);

        // Check if playback activation is required (first DJ)
        boolean isPostActivationProcessingRequired = !loadedPartyroom.isPlaybackActivated();

        // Create and add DJ
        Partyroom updatedPartyroom = loadedPartyroom
                .createAndAddDj(new PlaylistId(playlist.getId()), userId)
                .applyActivation();

        PartyroomData savedPartyroomData = partyroomRepository.save(partyroomConverter.toData(updatedPartyroom));

        // Start playback if this is the first DJ
        if (isPostActivationProcessingRequired) {
            playbackManagementService.start(partyroomConverter.toDomain(savedPartyroomData));
            log.info("Started playback for partyroom: partyroomId={}, djUserId={}",
                    partyroom.getPartyroomId().getId(), userId.getUid());
        }

        log.debug("Registered DJ: userId={}, playlistId={}, partyroomId={}",
                userId.getUid(), playlist.getId(), partyroom.getPartyroomId().getId());
    }

    /**
     * Build response
     */
    private DemoEnvironmentResponse buildResponse(
            Partyroom mainStage,
            List<Partyroom> generalRooms,
            List<Member> specialMembers,
            int djsRegistered,
            long executionTime) {

        // Build main stage detail (no host, only DJ)
        UserId mainStageDjUserId = specialMembers.get(0).getUserId();
        Long mainStagePlaylistId = findPlaylistId(mainStageDjUserId);

        DemoEnvironmentResponse.PartyroomDetail mainStageDetail = DemoEnvironmentResponse.PartyroomDetail.builder()
                .partyroomId(mainStage.getPartyroomId().getId())
                .stageType(mainStage.getStageType().name())
                .title(mainStage.getTitle())
                .linkDomain(mainStage.getLinkDomain())
                .hostUserId(null)  // Main stage has no designated host
                .totalCrewCount(MAIN_STAGE_CREW)
                .djUserId(mainStageDjUserId.getUid().toString())
                .playlistId(mainStagePlaylistId)
                .build();

        // Build general rooms details (each has host who is also DJ)
        List<DemoEnvironmentResponse.PartyroomDetail> generalRoomDetails = new ArrayList<>();
        for (int i = 0; i < generalRooms.size(); i++) {
            Partyroom room = generalRooms.get(i);
            UserId hostUserId = specialMembers.get(i + 1).getUserId();
            Long playlistId = findPlaylistId(hostUserId);

            DemoEnvironmentResponse.PartyroomDetail detail = DemoEnvironmentResponse.PartyroomDetail.builder()
                    .partyroomId(room.getPartyroomId().getId())
                    .stageType(room.getStageType().name())
                    .title(room.getTitle())
                    .linkDomain(room.getLinkDomain())
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

    /**
     * Find playlist ID for user
     */
    private Long findPlaylistId(UserId userId) {
        Optional<PlaylistData> playlistOpt = playlistRepository.findByOwnerIdAndTypeOrderByOrderNumberDesc(
                userId, PlaylistType.PLAYLIST).stream().findFirst();
        return playlistOpt.map(PlaylistData::getId).orElse(null);
    }

    /**
     * Generate random NFT face URI
     * Pattern: ava_nft_tmp_001.png to ava_nft_tmp_030.png
     *
     * @return AvatarFaceUri with random NFT face URI
     */
    private AvatarFaceUri generateRandomNftFaceUri() {
        int faceNumber = random.nextInt(30) + 1;  // 1 to 30
        String faceFileName = String.format("ava_nft_tmp_%03d.png", faceNumber);
        String faceUri = "https://firebasestorage.googleapis.com/v0/b/pfplay-firebase.appspot.com/o/" +
                "ava_nft_tmp%2F" + faceFileName + "?alt=media";
        return new AvatarFaceUri(faceUri);
    }

    /**
     * Check if demo environment is initialized
     */
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

    /**
     * Get all active partyrooms for admin
     */
    @Transactional(readOnly = true)
    public AdminPartyroomListResponse getPartyrooms() {
        List<AdminPartyroomListResponse.PartyroomItem> items = partyroomRepository.findAll().stream()
                .filter(p -> !p.isTerminated())
                .map(p -> AdminPartyroomListResponse.PartyroomItem.builder()
                        .partyroomId(p.getId())
                        .stageType(p.getStageType().name())
                        .title(p.getTitle())
                        .linkDomain(p.getLinkDomain())
                        .crewCount(p.getCrewDataSet() != null ? p.getCrewDataSet().size() : 0)
                        .djCount(p.getDjDataSet() != null ? p.getDjDataSet().size() : 0)
                        .isPlaybackActivated(p.isPlaybackActivated())
                        .build())
                .toList();

        return AdminPartyroomListResponse.builder()
                .partyrooms(items)
                .build();
    }
}
