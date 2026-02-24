package com.pfplaybackend.api.common;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Redis Pub/Sub 통합 테스트")
class RedisPublishSubscribeIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private RedisMessageListenerContainer container;

    @AfterEach
    void tearDown() throws Exception {
        if (container != null) {
            container.stop();
            container.destroy();
        }
    }

    @Test
    @DisplayName("발행된 메시지를 구독자가 정상적으로 수신한다")
    void publishSubscribeReceivesMessage() throws InterruptedException {
        // given
        String topic = "test:pubsub:single";
        String message = "hello-redis";
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> received = new AtomicReference<>();

        container = createListenerContainer((msg, pattern) -> {
            String body = deserializeValue(msg.getBody());
            received.set(body);
            latch.countDown();
        }, topic);

        Thread.sleep(200); // 구독 등록 대기

        // when
        redisTemplate.convertAndSend(topic, message);

        // then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(received.get()).isEqualTo(message);
    }

    @Test
    @DisplayName("여러 구독자가 동시에 메시지를 수신한다 (fan-out)")
    void publishMultipleSubscribersAllReceive() throws InterruptedException {
        // given
        String topic = "test:pubsub:fanout";
        String message = "broadcast-message";
        CountDownLatch latch = new CountDownLatch(2);
        AtomicReference<String> received1 = new AtomicReference<>();
        AtomicReference<String> received2 = new AtomicReference<>();

        container = createListenerContainer(topic,
                (msg, pattern) -> {
                    received1.set(deserializeValue(msg.getBody()));
                    latch.countDown();
                },
                (msg, pattern) -> {
                    received2.set(deserializeValue(msg.getBody()));
                    latch.countDown();
                });

        Thread.sleep(200);

        // when
        redisTemplate.convertAndSend(topic, message);

        // then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        assertThat(received1.get()).isEqualTo(message);
        assertThat(received2.get()).isEqualTo(message);
    }

    @Test
    @DisplayName("JSON 직렬화된 객체를 역직렬화하여 수신한다")
    void publishSerializedObjectDeserializesCorrectly() throws InterruptedException {
        // given
        String topic = "test:pubsub:object";
        Map<String, Object> payload = Map.of("type", "TEST_EVENT", "value", 42);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<String> received = new AtomicReference<>();

        container = createListenerContainer((msg, pattern) -> {
            received.set(new String(msg.getBody()));
            latch.countDown();
        }, topic);

        Thread.sleep(200);

        // when
        redisTemplate.convertAndSend(topic, payload);

        // then
        assertThat(latch.await(5, TimeUnit.SECONDS)).isTrue();
        String body = received.get();
        assertThat(body).contains("TEST_EVENT");
        assertThat(body).contains("42");
    }

    @Test
    @DisplayName("구독자가 없어도 발행이 블로킹되지 않는다")
    void publishNoSubscriberDoesNotBlock() {
        // given
        String topic = "test:pubsub:nosub";
        String message = "orphan-message";

        // when & then — 예외 없이 즉시 반환
        redisTemplate.convertAndSend(topic, message);
    }

    // --- helpers ---

    private RedisMessageListenerContainer createListenerContainer(MessageListener listener, String topic) {
        RedisMessageListenerContainer c = new RedisMessageListenerContainer();
        c.setConnectionFactory(redisTemplate.getConnectionFactory());
        c.addMessageListener(listener, new ChannelTopic(topic));
        c.afterPropertiesSet();
        c.start();
        return c;
    }

    private RedisMessageListenerContainer createListenerContainer(String topic, MessageListener... listeners) {
        RedisMessageListenerContainer c = new RedisMessageListenerContainer();
        c.setConnectionFactory(redisTemplate.getConnectionFactory());
        ChannelTopic channelTopic = new ChannelTopic(topic);
        for (MessageListener listener : listeners) {
            c.addMessageListener(listener, channelTopic);
        }
        c.afterPropertiesSet();
        c.start();
        return c;
    }

    private String deserializeValue(byte[] body) {
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        Object deserialized = serializer.deserialize(body);
        return deserialized != null ? deserialized.toString() : null;
    }
}
