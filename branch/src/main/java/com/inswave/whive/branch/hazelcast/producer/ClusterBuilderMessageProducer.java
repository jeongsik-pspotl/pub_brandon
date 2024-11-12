package com.inswave.whive.branch.hazelcast.producer;

import com.hazelcast.topic.ITopic;
import com.inswave.whive.branch.hazelcast.HazelcastClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ClusterBuilderMessageProducer {

    public static void sendWsMessage(String message) {
        ITopic<String> topic = HazelcastClientBuilder.getHazelcastInstance().getTopic("stringMessageWs");
        log.debug("WS Sended: {}", message);
        topic.publish(message);
    }

    // TODO : wHiveIdentity 값을 어떻게 잘 처리해서 manager 로 전달할지가 고민...
    public static void sendWsMessage(String wHiveIdentity, Map<String, Object> message) {
        log.debug("WS Sended: {}", message);
        ITopic<Map<String, Map<String, Object>>> topic = HazelcastClientBuilder.getHazelcastInstance().getTopic("stringMessageWsByIdentity");
        log.debug("WS Sended: {}", message);
        topic.publish(new HashMap<String,  Map<String, Object>>() {
            {
                put(wHiveIdentity, message);
            }
        });
    }

}
