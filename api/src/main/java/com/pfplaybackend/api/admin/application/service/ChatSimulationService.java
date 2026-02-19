package com.pfplaybackend.api.admin.application.service;

import com.pfplaybackend.api.admin.domain.enums.ChatScriptType;
import com.pfplaybackend.api.common.config.redis.RedisMessagePublisher;
import com.pfplaybackend.api.party.adapter.in.listener.message.OutgoingGroupChatMessage;
import com.pfplaybackend.api.party.domain.entity.data.CrewData;
import com.pfplaybackend.api.party.domain.enums.MessageTopic;
import com.pfplaybackend.api.party.domain.value.PartyroomId;
import com.pfplaybackend.api.party.adapter.out.persistence.CrewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

/**
 * Service for simulating realistic chat conversations in partyrooms
 * Continuously sends messages from random crew members with realistic timing
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatSimulationService {

    private final CrewRepository crewRepository;
    private final RedisMessagePublisher messagePublisher;

    // ExecutorService for background chat simulation
    private final ScheduledExecutorService chatExecutor = Executors.newScheduledThreadPool(5);

    // Track active simulations
    private final Map<Long, ScheduledFuture<?>> activeSimulations = new ConcurrentHashMap<>();

    /**
     * CHILL script - Relaxed, laid-back music appreciation messages (50 messages)
     */
    private static final List<String> CHILL_SCRIPTS = Arrays.asList(
            "This song is so smooth",
            "Love the vibe here",
            "Perfect chill music",
            "DJ has great taste",
            "This is exactly what I needed",
            "So relaxing",
            "Beautiful melody",
            "This playlist is perfect",
            "Feeling so peaceful right now",
            "Great selection",
            "This track is amazing",
            "Love this atmosphere",
            "Such a good song",
            "DJ knows what's up",
            "This is my kind of music",
            "So mellow and nice",
            "Perfect background music",
            "Love discovering new songs here",
            "This beat is so smooth",
            "Vibing to this",
            "Great track choice",
            "This song hits different",
            "Love this artist",
            "Perfect for unwinding",
            "Such a calm vibe",
            "This is beautiful",
            "Great music selection",
            "Feeling so relaxed",
            "This song is perfect",
            "Love the flow",
            "DJ is killing it",
            "This playlist is fire",
            "So good",
            "Perfect evening music",
            "Love this track",
            "Great DJ",
            "This song is a gem",
            "Vibes are immaculate",
            "Love this room",
            "Perfect selection",
            "This is what I call good music",
            "So chill",
            "Love the atmosphere here",
            "This track is everything",
            "DJ has impeccable taste",
            "Feeling the music",
            "This is perfect",
            "Love every song so far",
            "Great vibes tonight",
            "This music is healing"
    );

    /**
     * HYPE script - High-energy, excited party messages (50 messages)
     */
    private static final List<String> HYPE_SCRIPTS = Arrays.asList(
            "LET'S GOOO!!!",
            "This song is FIRE!!!",
            "DJ IS KILLING IT!!!",
            "TURN IT UP!!!",
            "THIS IS MY JAM!!!",
            "YESSS!!!",
            "ABSOLUTE BANGER!!!",
            "OMG I LOVE THIS SONG!!!",
            "THE BEAT DROP THO!!!",
            "PARTY TIME!!!",
            "THIS GOES SO HARD!!!",
            "DJ YOU'RE AMAZING!!!",
            "CAN'T STOP DANCING!!!",
            "THIS IS INSANE!!!",
            "LEGENDARY TRACK!!!",
            "HANDS UP EVERYONE!!!",
            "BEST PARTY EVER!!!",
            "THIS HITS DIFFERENT!!!",
            "WHAT A VIBE!!!",
            "DJ IS ON FIRE!!!",
            "SCREAMING RN!!!",
            "THIS IS EVERYTHING!!!",
            "ABSOLUTE MASTERPIECE!!!",
            "I'M SO HYPED!!!",
            "DANCE WITH ME!!!",
            "THIS SONG SLAPS!!!",
            "GOOSEBUMPS!!!",
            "CAN'T SIT STILL!!!",
            "THIS IS INCREDIBLE!!!",
            "BEST DJ EVER!!!",
            "FEELING ALIVE!!!",
            "LET'S GET LOUD!!!",
            "THIS ROOM IS LIT!!!",
            "AMAZING SELECTION!!!",
            "I NEED MORE OF THIS!!!",
            "ENERGY IS UNREAL!!!",
            "THIS IS WHY I'M HERE!!!",
            "GIVE IT UP FOR THE DJ!!!",
            "PURE FIRE!!!",
            "THIS SONG IS PERFECT!!!",
            "LOSING MY MIND RN!!!",
            "BEST NIGHT EVER!!!",
            "DJ NEVER MISSES!!!",
            "THIS IS TOO GOOD!!!",
            "ABSOLUTELY INSANE!!!",
            "LOVING EVERY SECOND!!!",
            "THIS BEAT IS CRAZY!!!",
            "LET'S PARTY!!!",
            "VIBES ARE UNMATCHED!!!",
            "THIS IS EPIC!!!"
    );

    private static List<String> getScriptsByType(ChatScriptType type) {
        return switch (type) {
            case CHILL -> CHILL_SCRIPTS;
            case HYPE -> HYPE_SCRIPTS;
        };
    }

    public void startChatSimulation(Long partyroomId, ChatScriptType scriptType) {
        if (activeSimulations.containsKey(partyroomId)) {
            log.warn("Chat simulation already running for partyroom: {}", partyroomId);
            return;
        }

        List<CrewData> crewList = crewRepository.findByPartyroomDataIdAndIsActiveTrue(partyroomId);
        if (crewList.isEmpty()) {
            throw new IllegalStateException("No crew members in partyroom: " + partyroomId);
        }

        List<String> scripts = getScriptsByType(scriptType);

        log.info("Starting chat simulation for partyroom: {} with {} crew members, scriptType: {}",
                partyroomId, crewList.size(), scriptType);

        ScheduledFuture<?> future = chatExecutor.scheduleWithFixedDelay(
                new ChatSimulationTask(partyroomId, crewList, scripts),
                0,
                1,
                TimeUnit.SECONDS
        );

        activeSimulations.put(partyroomId, future);
    }

    public void stopChatSimulation(Long partyroomId) {
        ScheduledFuture<?> future = activeSimulations.remove(partyroomId);
        if (future != null) {
            future.cancel(false);
            log.info("Stopped chat simulation for partyroom: {}", partyroomId);
        } else {
            log.warn("No active chat simulation for partyroom: {}", partyroomId);
        }
    }

    public boolean isSimulationActive(Long partyroomId) {
        return activeSimulations.containsKey(partyroomId);
    }

    public Set<Long> getActiveSimulations() {
        return new HashSet<>(activeSimulations.keySet());
    }

    private class ChatSimulationTask implements Runnable {
        private final Long partyroomId;
        private final List<CrewData> crewList;
        private final List<String> scripts;
        private int currentIndex = 0;
        private long nextMessageTime = System.currentTimeMillis();

        public ChatSimulationTask(Long partyroomId, List<CrewData> crewList, List<String> scripts) {
            this.partyroomId = partyroomId;
            this.crewList = crewList;
            this.scripts = scripts;
        }

        @Override
        public void run() {
            try {
                long currentTime = System.currentTimeMillis();

                if (currentTime < nextMessageTime) {
                    return;
                }

                CrewData randomCrew = crewList.get(ThreadLocalRandom.current().nextInt(crewList.size()));
                String message = scripts.get(currentIndex);

                sendChatMessage(partyroomId, randomCrew.getId(), message);

                currentIndex = (currentIndex + 1) % scripts.size();

                int delaySeconds = ThreadLocalRandom.current().nextInt(2, 5);
                nextMessageTime = currentTime + (delaySeconds * 1000L);

                log.debug("Sent chat message in partyroom {}: '{}' by crewId={}, next in {}s",
                        partyroomId, message, randomCrew.getId(), delaySeconds);

            } catch (Exception e) {
                log.error("Error in chat simulation task for partyroom: {}", partyroomId, e);
            }
        }
    }

    private void sendChatMessage(Long partyroomId, Long crewId, String content) {
        Map<String, Object> crew = new HashMap<>();
        crew.put("crewId", crewId);

        Map<String, Object> message = new HashMap<>();
        message.put("messageId", System.currentTimeMillis() + ":" + crewId);
        message.put("content", content);

        OutgoingGroupChatMessage chatMessage = new OutgoingGroupChatMessage(
                new PartyroomId(partyroomId),
                MessageTopic.CHAT,
                crew,
                message
        );

        messagePublisher.publish(MessageTopic.CHAT, chatMessage);
    }
}
