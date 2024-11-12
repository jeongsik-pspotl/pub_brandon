package com.inswave.whive.headquater.util.common;

import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.hazelcast.HazelcastClusterManager;
import com.inswave.whive.headquater.hazelcast.messaging.message.ClusterBroadcastBusMessage;
import com.inswave.whive.headquater.hazelcast.messaging.producer.ClusterMessageProducer;
import com.inswave.whive.headquater.security.jwt.TokenProvider;
import com.inswave.whive.headquater.util.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Component
public class Common {

    @Autowired
    ClusterMessageProducer clusterMessageProducer;

    @Autowired
    TokenProvider tokenProvider;

    /**
     * TODO : send hazelcast messsage 클러스터링 처리 기능 구현하기 ...
     ** 추가 기능 구현시 모든 send message 처리 기능 목록들 정리하기 ...
     * @param targets
     * @param targetType
     * @param serviceName
     * @param isClustering
     * @param url
     * @param commandId
     * @param appId
     */
    public void clientSendMsg(List<String> targets, String targetType, String serviceName, boolean isClustering, String url, String commandId, String appId) {
        for(String target : targets) {

            if(target.equals("000000")) {

            }else {
                if (targetType != null) {
                    if (targetType.equals("G")) {

                    }else{

                    }
                } else {

                }
            }


        }

        if(Config.getInstance().ismUseKafka() && isClustering == false) {

        }

        if (Config.getInstance().ismUseHazelcast() && isClustering == false) {  // TODO. hazecast로 메세지 전송 분기
            SendBusHazelcastMsg(serviceName, null, appId, null, targets, targetType, url, commandId);
        }
    }


    /**
     * 토큰 값을 복호화 해서 user name 값 리턴 하는 메소드
     *
     * @Methoid : common.getTokenToRealName
     * @Param HttpServletRequest request
     * return realName
     *
     */
    public String getTokenToRealName(HttpServletRequest request){
        String token = tokenProvider.resolveToken(request);
        String userId = tokenProvider.getRealName(token);
        return userId;
    }

    /**
     * 토큰 값을 복호화 해서 토큰 값 expired 처리 하는 메소드
     * @param request
     * @return
     */
    public boolean getExpired(HttpServletRequest request){

        String token = tokenProvider.resolveToken(request);
        return tokenProvider.getExpired(token);

    }

    /**
     * TODO : send hazelcast messsage 클러스터링 처리 기능 구현하기 ...
     * 추가 기능 구현시 모든 send message 처리 기능 목록들 정리하기 ...
     * @param targets
     * @param targetType
     * @param serviceName
     * @param appId
     * @param url
     * @param isClustering
     * @param commandId
     */
    public void clientSendMsgDeviceATM(List<String> targets, String targetType, String serviceName, String appId, String url, boolean isClustering, String commandId) {
        boolean isDevice = true;
        for(String target : targets) {

            if(target.equals("000000")) {

            }else {
                if (targetType != null) {
                    if (targetType.equals("G")) {

                    } else {

                    }
                } else {

                }
            }

        }

        if (Config.getInstance().ismUseHazelcast() && isClustering == false) {  // TODO. hazecast로 메세지 전송 분기
            // TODO: MSG TYPE 값을 동적으로 받아서 처리하는 기능 적용하기 ..
            SendBusHazelcastMsg(serviceName, ClientMessageType.HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER.name(), appId, null, targets, targetType, url, commandId);
        }
    }
    /**
     * 엣지스퀘어는 사용하고 있었던 메소드였지만 whive 에서는 필요가 없음 무시하면됨
     */
    private void SendBusHazelcastMsg(String serviceName, String type, String appId, String deviceId, List<String> targets, String targetType, String url, String commandId) {
        try {
            clusterMessageProducer.send(ClusterBroadcastBusMessage.builder()
                    .serviceName(serviceName)
                    .type(type)
                    .appId(appId)
                    .deviceId(deviceId)
                    .url(url)
                    .commandId(commandId)
                    .targetType(targetType)
                    .targets(targets)
                    .clusterUuid(HazelcastClusterManager.getLocalUuid())
                    .build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
