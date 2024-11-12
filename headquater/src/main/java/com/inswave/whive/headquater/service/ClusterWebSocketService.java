package com.inswave.whive.headquater.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.MapEvent;
import com.hazelcast.replicatedmap.ReplicatedMap;
import com.hazelcast.topic.ITopic;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.handler.WHiveIdentity;
import com.inswave.whive.headquater.handler.WHiveWebSocketHandler;
import com.inswave.whive.headquater.hazelcast.HazelcastClusterManager;
import com.inswave.whive.headquater.hazelcast.messaging.message.ClusterUnicastWHubMessage;
import com.inswave.whive.headquater.hazelcast.messaging.message.ProcessingType;
import com.inswave.whive.headquater.hazelcast.messaging.producer.ClusterMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@DependsOn("hazelcastClusterManager")
public class ClusterWebSocketService {
    private static HazelcastInstance hazelcastInstance;
    private static String            KEY_REPLICATED_MAP = "";

    @PostConstruct
    public void postConstruct() {
        KEY_REPLICATED_MAP = this.getClass().getSimpleName();
        hazelcastInstance = HazelcastClusterManager.getHazelcastInstance();
        ReplicatedMap<String, WHiveIdentity> map = hazelcastInstance.getReplicatedMap(KEY_REPLICATED_MAP);
        map.addEntryListener(new EntryListener<String, WHiveIdentity>() {
            @Override public void entryAdded(EntryEvent<String, WHiveIdentity> entryEvent) {
                log.info("entryAdded:" + entryEvent);
            }

            @Override public void entryEvicted(EntryEvent<String, WHiveIdentity> entryEvent) {
                log.info("entryEvicted:" + entryEvent);
            }

            @Override public void entryExpired(EntryEvent<String, WHiveIdentity> entryEvent) {
                log.info("entryExpired:" + entryEvent);
            }

            @Override public void entryRemoved(EntryEvent<String, WHiveIdentity> entryEvent) {
                log.info("entryRemoved:" + entryEvent);
            }

            @Override public void entryUpdated(EntryEvent<String, WHiveIdentity> entryEvent) {
                log.info("entryUpdated:" + entryEvent);
            }

            @Override public void mapCleared(MapEvent mapEvent) {
                log.info("mapCleared:" + mapEvent);
            }

            @Override public void mapEvicted(MapEvent mapEvent) {
                log.info("mapEvicted:" + mapEvent);
            }
        });
    }

    public static Map<String, WHiveIdentity> getAllIdentities() {
        ReplicatedMap<String, WHiveIdentity> rmap = HazelcastClusterManager.getHazelcastInstance().getReplicatedMap(KEY_REPLICATED_MAP);
        Map<String, WHiveIdentity> map = new HashMap<>();
        rmap.forEach((identity, wHubIdentity) -> {
            map.put(identity, wHubIdentity);
        });
        return map;
    }

    public static WHiveIdentity getIdentityBuilder(String identity) {
        ReplicatedMap<String, WHiveIdentity> map = hazelcastInstance.getReplicatedMap(KEY_REPLICATED_MAP);
        WHiveIdentity wHiveIdentity = map.get(identity);
        if(wHiveIdentity.getSessionType().equals(SessionType.BRANCH)) {
            return wHiveIdentity;
        }else {
            return new WHiveIdentity();
        }

    }

    public static WHiveIdentity getIdentityManager(String identity) {
        ReplicatedMap<String, WHiveIdentity> map = hazelcastInstance.getReplicatedMap(KEY_REPLICATED_MAP);
        WHiveIdentity wHiveIdentity = map.get(identity);
        if(wHiveIdentity == null){
            return new WHiveIdentity();
        }else if(wHiveIdentity.getSessionType().equals(SessionType.HEADQUATER)){
            return wHiveIdentity;
        }else {
            return new WHiveIdentity();
        }

    }

    public static WHiveIdentity getIdentity(WHiveIdentity wHubIdentity) {
        ReplicatedMap<String, WHiveIdentity> map = hazelcastInstance.getReplicatedMap(KEY_REPLICATED_MAP);
        return map.get(wHubIdentity.getUserId());
    }

    public static WHiveIdentity put(WHiveIdentity wHubIdentity) {
        ReplicatedMap<String, WHiveIdentity> map = hazelcastInstance.getReplicatedMap(KEY_REPLICATED_MAP);
        return map.put(wHubIdentity.getUserId(), wHubIdentity);
    }

    public static WHiveIdentity remove(WHiveIdentity wHubIdentity) {
        ReplicatedMap<WebSocketSession, WHiveIdentity> map = hazelcastInstance.getReplicatedMap(KEY_REPLICATED_MAP);
        if(wHubIdentity == null){
            return null;
//        }else if(wHubIdentity.getSessionType().equals(SessionType.BRANCH)){
//            return null;
        }else {
            return map.remove(wHubIdentity.getUserId());
        }

    }

    /*메세지 전송 : 클러서터 내 특정세션*/
    public static void sendMessage(WHiveIdentity wHubIdentity, Map<String, Object> message) {
        ClusterMessageProducer.sendWsMessage(wHubIdentity, message);
    }

    /*메세지 전송 : 클러서터 내 특정세션*/
    public static boolean sendMessage(String identity, String message) {
        ReplicatedMap<String, WHiveIdentity> map = HazelcastClusterManager.getHazelcastInstance().getReplicatedMap(KEY_REPLICATED_MAP);
        WHiveIdentity wHubIdentity = map.get(identity);
        if (wHubIdentity != null) {
            ITopic<ClusterUnicastWHubMessage> topic = HazelcastClusterManager.getHazelcastInstance().getTopic("WS-" + wHubIdentity.getClusterUuid());
            topic.publish(ClusterUnicastWHubMessage.builder()
                                                   .processingType(ProcessingType.SEND_TO_DEVICE)
                                                   .wHiveIdentity(wHubIdentity)
                                                   .content(message)
                                                   .build());
            return true;
        }
        return false;
    }

    /*메세지 전송 : 현재 서버 내 특정세션*/
    public static void sendMessageLocal(WHiveIdentity wHiveIdentity, HashMap<String, Object> message) {
        sendMessageLocalDetail(wHiveIdentity, message);
    }

    public static void sendBinaryLocal(WHiveIdentity wHiveIdentity, byte[] byteMessage) {
        sendBinaryLocalDetail(wHiveIdentity, byteMessage);
    }

    /*메세지 전송 : 현재 서버 내 특정세션*/
    public static void sendMessageLocalDetail(WHiveIdentity identity, HashMap<String, Object> message) {
        WebSocketSession session =  WHiveWebSocketHandler.getIdentityBySessionIdAndSessionType(identity); //getLocalSession(identity); // whive session 적용...

        if (session != null) {
            try {
                synchronized (session) {
                    if(session.isOpen()){
                        //session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
                        session.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
                    }

                }

            } catch (IOException e) {
                log.info(e.getMessage(), e);

            }
        }
    }

    public static void sendBinaryLocalDetail(WHiveIdentity identity, byte[] byteMessage) {
        WebSocketSession session =  WHiveWebSocketHandler.getIdentityBySessionIdAndSessionType(identity); //getLocalSession(identity); // whive session 적용...

        if (session != null) {
            try {
                synchronized (session) {
                    if(session.isOpen()){
                        session.sendMessage(new BinaryMessage(byteMessage));
                    }
                }
            } catch (IOException e) {
                log.info(e.getMessage(), e);

            }
        }
    }

    /*메세지 전송 : 클러서터 내 전체세션*/
    public static void sendMessage(String message) {
        ClusterMessageProducer.sendWsMessage(message);
    }

    /*메세지 전송 : 현재 서버 내 전체세션*/
    public static void sendMessageLocal(String message) {
        Set<WebSocketSession> sessions = getAllLocalSessions();
//        sessions.forEach((webSocketSession, wHubIdentity) -> {
//            // log.info("WS : {} / {}", webSocketSession.getId(), wHubIdentity.getIdentity());
//            try {
//                webSocketSession.sendMessage(new TextMessage(message));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
    }

    public static WebSocketSession getLocalSession(String identity) {
//        Optional<Map.Entry<WebSocketSession, WHiveIdentity>> webSocketSessionWHubIdentityEntry = getAllLocalSessions().entrySet()
//                                                                                                                     .stream()
//                                                                                                                     .filter(entry -> entry.getValue().getIdentity().equals(identity))
//                                                                                                                     .findFirst();

//        if (webSocketSessionWHubIdentityEntry.isPresent()) {
//            return webSocketSessionWHubIdentityEntry.get().getKey();
//
//        }

        return null;
    }

    public static WebSocketSession getLocalSession(WHiveIdentity identity) {
//        Optional<Map.Entry<WebSocketSession, WHiveIdentity>> webSocketSessionWHubIdentityEntry = getAllLocalSessions().entrySet()
//                                                                                                                     .stream()
//                                                                                                                     .filter(entry -> entry.getValue().getIdentity().equals(identity.getIdentity()))
//                                                                                                                     .findFirst();

//        if (webSocketSessionWHubIdentityEntry.isPresent()) {
//            return webSocketSessionWHubIdentityEntry.get().getKey();
//
//        }

        return null;
    }

    public static Set<WebSocketSession> getAllLocalSessions() {
        return WHiveWebSocketHandler.getAllSession();
    }
}
