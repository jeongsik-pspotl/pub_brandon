package com.inswave.whive.headquater.hazelcast.messaging.consumer;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import com.inswave.whive.headquater.handler.WHiveIdentity;
import com.inswave.whive.headquater.hazelcast.HazelcastClusterManager;
import com.inswave.whive.headquater.hazelcast.messaging.message.*;
import com.inswave.whive.headquater.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnExpression(value = "${whive.hazelcast.cluster.use}")
public class ClusterMessageConsumer {
    /**
     * 클러스터 중에 토픽으로 설정 된 (?) 키 값을 기준으로 세션 분기처리하는 구간이다
     * 이부분은 나도 잘 이해하기 어렵고 다 이해하지 못한 구간이다.
     * 실제로 쓰고 있는 토픽 키값은 stringMessageWsByIdentity, binaryMessageWsByIdentity 이다.
     */
    @PostConstruct
    public void postConstruct() {
        HazelcastInstance hz = HazelcastClusterManager.getHazelcastInstance();
        ITopic<ClusterUnicastWHubMessage> topicClusterUnicastWHubIdentityMessage = hz.getTopic("WS-" + HazelcastClusterManager.getLocalUuid());
        topicClusterUnicastWHubIdentityMessage.addMessageListener(message -> {
            ClusterUnicastWHubMessage clusterUnicastMessage = message.getMessageObject();
            switch (clusterUnicastMessage.getProcessingType()) {
            case SEND_TO_DEVICE:
//                log.debug("SEND_TO_DEVICE : {}", clusterUnicastMessage);
//                ClusterWebSocketService.sendMessageLocal(clusterUnicastMessage.getWHiveIdentity().getIdentity(),
//                                                         clusterUnicastMessage.getContent());
                break;
            }
        });

        ITopic<ClusterUnicastSseMessage> topicClusterUnicastSseMessage = hz.getTopic("SSE-" + HazelcastClusterManager.getLocalUuid());
        topicClusterUnicastSseMessage.addMessageListener(message -> {
            ClusterUnicastSseMessage clusterUnicastSseMessage = message.getMessageObject();
            switch (clusterUnicastSseMessage.getProcessingType()) {
            case SEND_TO_SUBSCRIBER:
//                log.debug("SEND_TO_SUBSCRIBER : {}", clusterUnicastSseMessage);
//                ClusterSseEmitterService.sendMessageLocal(clusterUnicastSseMessage.getSseIdentity(),
//                                                          clusterUnicastSseMessage.getContent());
                break;
            }
        });

        ITopic<ClusterBroadcastBusMessage> topicClusterBroadcastBusMessage = hz.getTopic("ClusterBroadcastBusMessage");
        topicClusterBroadcastBusMessage.addMessageListener(new MessageListenerImpl());

        ITopic<ClusterBroadcastLogList> topicLogList = hz.getTopic("ClusterBroadcastLogList");
        topicLogList.addMessageListener(new LogListMessageListenerImpl());

        ITopic<ClusterBroadcastLogBinaryList> topicLogBinaryList = hz.getTopic("ClusterBroadcastLogBinaryList");
        topicLogBinaryList.addMessageListener(new LogBinaryListMessageListenerImpl());

        ITopic<String> topicStringMessageWs = hz.getTopic("stringMessageWs");
        topicStringMessageWs.addMessageListener(new MessageListenerStringImpl());

        ITopic<Map<WHiveIdentity, HashMap<String, Object>>> topicMapMessageWs = hz.getTopic("stringMessageWsByIdentity");
        topicMapMessageWs.addMessageListener(new MessageListenerMapImpl());

        ITopic<Map<WHiveIdentity, byte[]>> topicMapBinaryWs = HazelcastClusterManager.getHazelcastInstance().getTopic("binaryMessageWsByIdentity");
        topicMapBinaryWs.addMessageListener(new BinaryListenerMapImpl());
    }

    private static class MessageListenerImpl implements MessageListener<ClusterBroadcastBusMessage> {
        public void onMessage(Message<ClusterBroadcastBusMessage> message) {
            log.debug("Received: {}", message.getMessageObject());
            log.info("[BusMessage] Received: {}", message.getMessageObject());
            message.getMessageObject().processAfterTransfer();
        }
    }

    private static class LogListMessageListenerImpl implements MessageListener<ClusterBroadcastLogList> {
        public void onMessage(Message<ClusterBroadcastLogList> message) {
            log.info("[LogList] Received: {}", message.getMessageObject());
            message.getMessageObject().processAfterTransfer();
        }
    }

    private static class LogBinaryListMessageListenerImpl implements MessageListener<ClusterBroadcastLogBinaryList> {
        public void onMessage(Message<ClusterBroadcastLogBinaryList> message) {
            log.info("[LogBinaryList] Received: {}", message.getMessageObject());
            message.getMessageObject().processAfterTransfer();
        }
    }

    private static class MessageListenerStringImpl implements MessageListener<String> {
        public void onMessage(Message<String> message) {
            log.debug("WS Received: {}", message.getMessageObject());
            ClusterWebSocketService.sendMessageLocal(message.getMessageObject());
        }
    }

    private static class MessageListenerMapImpl implements MessageListener<Map<WHiveIdentity, HashMap<String, Object>>> {
        public void onMessage(Message<Map<WHiveIdentity, HashMap<String, Object>>> message) {
            log.debug("WS Received: {}", message.getMessageObject());
            Map<WHiveIdentity, HashMap<String, Object>>map = message.getMessageObject();
            map.entrySet()
               .stream()
               .forEach(entry -> {
                   ClusterWebSocketService.sendMessageLocal(entry.getKey(), entry.getValue());
               });
        }
    }

    private static class BinaryListenerMapImpl implements MessageListener<Map<WHiveIdentity, byte[]>> {
        public void onMessage(Message<Map<WHiveIdentity, byte[]>> message) {
            log.debug("WS Received: {}", message.getMessageObject());
            Map<WHiveIdentity, byte[]>map = message.getMessageObject();
            map.entrySet()
                    .stream()
                    .forEach(entry -> {
                        ClusterWebSocketService.sendBinaryLocal(entry.getKey(), entry.getValue());
                    });
        }
    }
}
