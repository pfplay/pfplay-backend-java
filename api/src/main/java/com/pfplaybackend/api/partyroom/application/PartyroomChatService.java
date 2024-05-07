package com.pfplaybackend.api.partyroom.application;

import com.pfplaybackend.api.common.enums.PartyroomGrade;
import com.pfplaybackend.api.partyroom.exception.UnsupportedChatMessageTypeException;
import com.pfplaybackend.api.partyroom.exception.UnsupportedChatRequestException;
import com.pfplaybackend.api.partyroom.model.value.PromoteInfo;
import com.pfplaybackend.api.partyroom.presentation.dto.ChatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartyroomChatService {
    private final RedisChatPublisherService redisChatPublisherService;
    public void sendChat(ChatDto chatDto) throws UnsupportedChatMessageTypeException, UnsupportedChatRequestException {
        if (chatDto.getMessageType().equals(ChatDto.MessageType.CHAT)) {
            sendToBroker(chatDto);
            return;
        }

        if (chatDto.getMessageType().equals(ChatDto.MessageType.PENALTY)) {
            if (chatDto.getToUser() != null && chatDto.getPenaltyInfo() != null) {
                isAvailablePenaltyRequest(
                        chatDto.getFromUser().getPartyroomGrade(),
                        chatDto.getToUser().getPartyroomGrade()
                );
                sendToBroker(chatDto);
            }
            return;
        }

        if (chatDto.getMessageType().equals(ChatDto.MessageType.PROMOTE)) {
            if (chatDto.getToUser() != null && chatDto.getPromoteInfo() != null
            ) {
                isAvailablePromoteRequest(
                        chatDto.getFromUser().getPartyroomGrade(),
                        chatDto.getToUser().getPartyroomGrade(),
                        chatDto.getPromoteInfo()
                );
                sendToBroker(chatDto);
            }
            return;
        }

        throw new UnsupportedChatMessageTypeException("Unsupported ChatMessageType request");
    }

    private void sendToBroker(ChatDto chatDto) {
        redisChatPublisherService.publish(new ChannelTopic("chat"), chatDto);
    }

    private void isAvailablePenaltyRequest(PartyroomGrade fromUserPartyroomGrade, PartyroomGrade toUserPartyroomGrade) throws UnsupportedChatRequestException {
        if (fromUserPartyroomGrade.getPriority() <= toUserPartyroomGrade.getPriority()) {
            throw new UnsupportedChatRequestException("The penalty request not supported, the requester's party room grade must be higher than the target's party room grade.");
        }
    }

    private void isAvailablePromoteRequest(PartyroomGrade fromUserPartyroomGrade, PartyroomGrade toUserPartyroomGrade, PromoteInfo promoteInfo) throws UnsupportedChatRequestException {
        if (fromUserPartyroomGrade.getPriority() <= toUserPartyroomGrade.getPriority()) {
            throw new UnsupportedChatRequestException("You cannot assign a grade higher than your own");
        }

        if (fromUserPartyroomGrade.getPriority() <= promoteInfo.getPromoteNextGrade().getPriority()) {
            throw new UnsupportedChatRequestException("The penalty request not supported, the requester's party room grade must be higher than the target's party room grade.");
        }
    }
}
