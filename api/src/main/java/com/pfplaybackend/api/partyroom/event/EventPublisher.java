package com.pfplaybackend.api.partyroom.event;

import com.pfplaybackend.api.partyroom.domain.model.enums.MessageTopic;

public interface EventPublisher {

    public void publish(MessageTopic topicType, Object object);
}
