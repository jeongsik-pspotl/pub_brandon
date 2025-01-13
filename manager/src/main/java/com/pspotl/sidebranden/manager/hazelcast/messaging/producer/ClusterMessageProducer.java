package com.pspotl.sidebranden.manager.hazelcast.messaging.producer;

import com.hazelcast.topic.ITopic;
import com.pspotl.sidebranden.manager.handler.WHiveIdentity;
import com.pspotl.sidebranden.manager.hazelcast.HazelcastClusterManager;
import com.pspotl.sidebranden.manager.hazelcast.messaging.message.ClusterBroadcastBusMessage;
import com.pspotl.sidebranden.manager.hazelcast.messaging.message.ClusterBroadcastGetLogSetting;
import com.pspotl.sidebranden.manager.hazelcast.messaging.message.ClusterBroadcastLogBinaryList;
import com.pspotl.sidebranden.manager.hazelcast.messaging.message.ClusterBroadcastLogList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnExpression(value = "${whive.hazelcast.cluster.use}")
public class ClusterMessageProducer {

    public static void send(ClusterBroadcastBusMessage clusterBroadcastBusMessage) {
        ITopic<ClusterBroadcastBusMessage> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("ClusterBroadcastBusMessage");
        topic.publish(clusterBroadcastBusMessage);
    }

    /**
     * manager, builder 클래스에서 사용하는 sendWsMessage 메소드 string 데이터로 전송 (사용안함)
     * @param message
     */
    public static void sendWsMessage(String message) {
        ITopic<String> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("stringMessageWs");
        log.debug("WS Sended: {}", message);
        topic.publish(message);
    }

    /**
     * manager, builder 클래스에서 사용하는 sendWsMessage 메소드 wHubIdentity vo 내 있는 key 값을 기준으로 string 데이터로 전송
     * @param wHubIdentity
     * @param message
     */
    public static void sendWsMessage(WHiveIdentity wHubIdentity, Map<String, Object> message) {
        ITopic<Map<WHiveIdentity, Map<String, Object>>> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("stringMessageWsByIdentity");
        log.debug("WS Sended: {}", message);
        topic.publish(new HashMap<WHiveIdentity,  Map<String, Object>>() {
            {
                put(wHubIdentity, message);
            }
        });
    }

    /**
     * manager, builder 클래스에서 사용하는 sendWsMessage 메소드 wHubIdentity vo 내 있는 key 값을 기준으로 byte 배얼 데이터로 전송
     * @param wHubIdentity
     * @param message
     */
    public static void sendWsBinaryMessage(WHiveIdentity wHubIdentity, byte[] message) {
        ITopic<Map<WHiveIdentity, byte[]>> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("binaryMessageWsByIdentity");
        topic.publish(new HashMap<WHiveIdentity,  byte[]>() {
            {
                put(wHubIdentity, message);
            }
        });
    }

    public static void send(ClusterBroadcastGetLogSetting clusterBroadcastGetLogSettingl) {
        ITopic<ClusterBroadcastGetLogSetting> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("ClusterBroadcastGetLogSetting");
        topic.publish(clusterBroadcastGetLogSettingl);
    }

    public static void send(ClusterBroadcastLogList clusterBroadcastLogList) {
        ITopic<ClusterBroadcastLogList> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("ClusterBroadcastLogList");
        topic.publish(clusterBroadcastLogList);
    }

    public static void send(ClusterBroadcastLogBinaryList clusterBroadcastLogBinaryList) {
        ITopic<ClusterBroadcastLogBinaryList> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("ClusterBroadcastLogBinaryList");
        topic.publish(clusterBroadcastLogBinaryList);
    }

}
