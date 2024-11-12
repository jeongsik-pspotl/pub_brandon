package com.inswave.whive.headquater.hazelcast;

import com.hazelcast.cluster.Cluster;
import com.hazelcast.cluster.Member;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.inswave.whive.headquater.hazelcast.messaging.producer.ClusterMessageProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * HazelcastClusterManager 클래스로 핵심이 되는 클래스다.
 * hazelcast 설정 하는 구간으로 중요한 클래스다.
 *
 * hazelcastInstance 인스턴스 객체를 다른 클래스에서 조회 할때 필요하다.
 * 해당 클래스로 호출해서 웹소켓 클러스터링 객체를 호출한다.
 */
@Slf4j
@Component
@ConditionalOnExpression(value = "${whive.hazelcast.cluster.use}")
public class HazelcastClusterManager {
    @Getter
    private static String            localUuid;
    @Getter
    private static HazelcastInstance hazelcastInstance;

    private static ClusterMessageProducer clusterMessageProducer;
    private static String                 logSettingKey = "HazelLogSettingMap";

    public HazelcastClusterManager(ClusterMessageProducer clusterMessageProducer) {
        this.clusterMessageProducer = clusterMessageProducer;
    }

    @PostConstruct
    public void postConstruct() {
        hazelcastInstance = Hazelcast.getHazelcastInstanceByName("wem-default-insts");
        localUuid = hazelcastInstance.getLocalEndpoint().getUuid().toString();
    }

    public Member getMemberMaster() {
        Cluster cluster = hazelcastInstance.getCluster();
        Set<Member> members = cluster.getMembers();
        Member master = members.iterator().next();
        return master;
    }

    public boolean isRootNode() {
        String masterUuid = getMemberMaster().getUuid().toString();
        return localUuid.equals(masterUuid);
    }

    public static boolean isSame(String remoteUuid) {
        return localUuid.equals(remoteUuid);
    }

    public <E> ITopic<E> getTopic(String topicName) {
        return hazelcastInstance.getTopic(topicName);
    }

//    public static void putLogSettingMap(String commandId, LogSettingDTO logSettingDTO) {
//        Map<String, LogSettingDTO> logSettingDTOMap = hazelcastInstance.getMap(logSettingKey);
//        logSettingDTOMap.put(commandId, logSettingDTO);
//    }
//
//    public static LogSettingDTO getLogSettingMap(String commandId) {
//        Map<String, LogSettingDTO> logSettingDTOMap = hazelcastInstance.getMap(logSettingKey);
//        return logSettingDTOMap.get(commandId);
//    }
//
//    public static void removeLogSettingMap(String commandId) {
//        Map<String, LogSettingDTO> logSettingDTOMap = hazelcastInstance.getMap(logSettingKey);
//        logSettingDTOMap.remove(commandId);
//    }
}

