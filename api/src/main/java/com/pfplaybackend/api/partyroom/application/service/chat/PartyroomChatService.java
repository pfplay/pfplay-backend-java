package com.pfplaybackend.api.partyroom.application.service.chat;

import com.pfplaybackend.api.partyroom.domain.enums.MessageTopic;
import com.pfplaybackend.api.partyroom.event.RedisMessagePublisher;
import com.pfplaybackend.api.partyroom.event.message.OutgoingGroupChatMessage;
import com.pfplaybackend.api.partyroom.exception.UnsupportedSocketRequestException;
import com.pfplaybackend.api.partyroom.presentation.dto.IncomingGroupChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomChatService {
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

    public void sendMessage(String sessionId, IncomingGroupChatMessage incomingGroupChatMessage) throws UnsupportedSocketRequestException {
//        String userIdUid = chatDto.getFromUser().getUserId().getUid().toString();
//        String partyroomId = chatDto.getFromUser().getPartyroomId();
//        if (isProhibitedSendChatUser(userIdUid, partyroomId)) {
//            throw new UnsupportedSocketRequestException("This user ggul user");
//        }
        // TODO Get sender's information from DBMS or Cache By sessionId(String)
        // TODO Create OutgoingChatMessage for publish
        OutgoingGroupChatMessage outgoingGroupChatMessage = new OutgoingGroupChatMessage();

        messagePublisher.publish(MessageTopic.CHAT, outgoingGroupChatMessage);
    }
}
