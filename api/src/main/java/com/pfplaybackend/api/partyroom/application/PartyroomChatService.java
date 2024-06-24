package com.pfplaybackend.api.partyroom.application;

import com.pfplaybackend.api.partyroom.domain.entity.data.PartyroomPenaltyHistory;
import com.pfplaybackend.api.partyroom.exception.UnsupportedSocketRequestException;
import com.pfplaybackend.api.partyroom.presentation.dto.ChatDto;
import com.pfplaybackend.api.partyroom.repository.PartyroomPenaltyHistoryRepository;
import com.pfplaybackend.api.partyroom.repository.PartyroomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PartyroomChatService {
    private final RedisChatPublisherService redisChatPublisherService;
    private final PartyroomUserRepository partyroomUserRepository;
    private final PartyroomPenaltyHistoryRepository partyroomPenaltyHistoryRepository;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames="partyroomUser", key="#uid.toString()")
    public String getPartyroomId(UUID uid) {
        final String partyroomId = partyroomUserRepository.findPartyroomIdByUserIdUid(uid);
        return partyroomId;
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames="partyroomPenalty", key="#uid.toString()")
    public boolean isProhibitedSendChatUser(String userIdUid, String partyroomId) {
        final List<PartyroomPenaltyHistory> partyroomPenalties = partyroomPenaltyHistoryRepository.findPartyroomPenaltyHistoriesByUserIdUidAndPartyroomId(
                UUID.fromString(userIdUid), partyroomId
        );

        for (PartyroomPenaltyHistory penalty : partyroomPenalties) {
            if (penalty.getUserId().getUid().equals(userIdUid)
                    && penalty.getPartyroomId().equals(partyroomId)
                    && penalty.getPartyroomPenaltyType().getName().equals("GGUL")) {
                return true;
            }
        }

        return false;
    }

    public void sendMessage(ChatDto chatDto) throws UnsupportedSocketRequestException {
        String userIdUid = chatDto.getFromUser().getUserId().getUid().toString();
        String partyroomId = chatDto.getFromUser().getPartyroomId();
        if (isProhibitedSendChatUser(userIdUid, partyroomId)) {
            throw new UnsupportedSocketRequestException("This user ggul user");
        }

        sendToTopic(chatDto, "chat");
    }

    private void sendToTopic(ChatDto chatDto, String topic) {
        redisChatPublisherService.publish(new ChannelTopic(topic), chatDto);
    }
}
