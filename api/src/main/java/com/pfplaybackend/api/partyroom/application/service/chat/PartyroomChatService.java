package com.pfplaybackend.api.partyroom.application.service.chat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.OutgoingGroupChatMessage;
import com.pfplaybackend.api.partyroom.presentation.dto.IncomingGroupChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PartyroomChatService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisMessagePublisher messagePublisher;

//    @Transactional(readOnly = true)
//    @Cacheable(cacheNames="partyroomUser", key="#uid.toString()")
//    public String getPartyroomId(UUID uid) {
//        final String partyroomId = partyroomUserRepository.findPartyroomIdByUserIdUid(uid);
//        return partyroomId;
//    }
//
//    @Transactional(readOnly = true)
//    @Cacheable(cacheNames="partyroomPenalty", key="#uid.toString()")
//    public boolean isProhibitedSendChatUser(String userIdUid, String partyroomId) {
//        final List<PartyroomPenaltyHistory> partyroomPenalties = partyroomPenaltyHistoryRepository.findPartyroomPenaltyHistoriesByUserIdUidAndPartyroomId(
//                UUID.fromString(userIdUid), partyroomId
//        );
//
//        for (PartyroomPenaltyHistory penalty : partyroomPenalties) {
//            if (penalty.getUserId().getUid().equals(userIdUid)
//                    && penalty.getPartyroomId().equals(partyroomId)
//                    && penalty.getPartyroomPenaltyType().getName().equals("GGUL")) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    public void sendMessage(String sessionId, IncomingGroupChatMessage incomingGroupChatMessage) {
//        String userIdUid = chatDto.getFromUser().getUserId().getUid().toString();
//        String partyroomId = chatDto.getFromUser().getPartyroomId();
//        if (isProhibitedSendChatUser(userIdUid, partyroomId)) {
//            throw new UnsupportedSocketRequestException("This user ggul user");
//        }
        // TODO Get sender's information from DBMS or Cache By sessionId(String)
        // TODO Create OutgoingChatMessage for publish
        Object object = redisTemplate.opsForValue().get(sessionId);
        if (object instanceof Map) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> sessionData = objectMapper.convertValue(object, new TypeReference<Map<String, Object>>(){});
                OutgoingGroupChatMessage outgoingGroupChatMessage = OutgoingGroupChatMessage.from(sessionData, incomingGroupChatMessage.getMessage());
                messagePublisher.publish(MessageTopic.CHAT, outgoingGroupChatMessage);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        // Map<String, Object> sessionData = (Map<String, Object>) redisTemplate.opsForValue().get(sessionId);
    }
}
