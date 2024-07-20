package com.pfplaybackend.api.partyroom.event.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfplaybackend.api.config.websocket.SimpMessageSender;
import com.pfplaybackend.api.partyroom.event.message.SampleMessage;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

@AllArgsConstructor
public class SampleTopicListener implements MessageListener {

    private SimpMessageSender messageSender;
    private ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String jsonstring = new String(message.getBody());

        try {
            SampleMessage sampleMessage = objectMapper.readValue(jsonstring, SampleMessage.class);
            // Map<String, String> receivedMessage = objectMapper.readValue(messageBody, TypeFactory.mapType(HashMap.class, String.class, String.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
//        Map<String, String> receivedMessage = null;
//        try {
//            receivedMessage = objectMapper.readValue(messageBody, new TypeReference<LinkedHashMap<String, String>>() {});
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//        receivedMessage.forEach((key, value) ->
//                System.out.println("Subscriber1 received message: Key=" + key + ", Value=" + value)
//        );

        System.out.println("SampleTopicListener received a message: " + new String(message.getBody()));
    }
}
