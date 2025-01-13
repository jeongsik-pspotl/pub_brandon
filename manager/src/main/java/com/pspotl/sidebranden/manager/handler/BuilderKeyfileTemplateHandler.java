package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.common.branchsetting.BranchSetting;
import com.pspotl.sidebranden.common.branchsetting.BranchSettingService;
import com.pspotl.sidebranden.common.build.BuildProjectService;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManaged;
import com.pspotl.sidebranden.common.builderqueue.BuilderQueueManagedService;
import com.pspotl.sidebranden.common.signingkeysetting.KeySetting;
import com.pspotl.sidebranden.common.signingkeysetting.SigningKeySettingService;
import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.PayloadKeyType;
import com.pspotl.sidebranden.manager.enums.SessionType;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class BuilderKeyfileTemplateHandler implements Handlable{

    @Autowired
    SigningKeySettingService signingKeySettingService;

    WebSocketSession headQuaterWebsocketSession = null;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    BuildProjectService buildProjectService;

    @Autowired
    BranchSettingService branchSettingService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = (String) parseResult.get(SessionType.MsgType.name());

        if(messageType.equals(ClientMessageType.BIN_FILE_PROFILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER.name())){

            String signingkey_id = parseResult.get(PayloadKeyType.signingkeyID.name()).toString();
            String signingkey_path = parseResult.get("keyfilePath").toString();
            String deployfilePath = parseResult.get("deployfilePath").toString();

            signingKeySettingService.updateByKeyfilePath(Integer.parseInt(signingkey_id), signingkey_path, deployfilePath);

            //TODO : builder 큐 관리 상태 값 업데이트 -1 적용
            BranchSetting branchSetting = branchSettingService.findByUserID(parseResult.get("builderID").toString());
            BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
            builderQueueManagedService.etcUpdate(builderQueueManaged);

            TextMessage message = null;
            try {

                message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

//                headQuaterWebsocketSession = WHiveWebSocketHandler.getSessionByIdentity(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
//                    headQuaterWebsocketSession.sendMessage(message);
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }

            } catch (IOException e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }


        }else if(messageType.equals(ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER.name())){

            String key_id = parseResult.get(PayloadKeyType.signingkeyID.name()).toString();
            String ios_key_path = parseResult.get("keyfilePath").toString();
            String ios_debug_profile_path = parseResult.get("debugProfilePath").toString();
            String ios_release_profile_path = parseResult.get("releaseProfilePath").toString();

            signingKeySettingService.updateByiOSKeyfilePath(Integer.parseInt(key_id), ios_key_path, ios_debug_profile_path, ios_release_profile_path);

            TextMessage message = null;
            try {

                message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

//                headQuaterWebsocketSession = WHiveWebSocketHandler.getSessionByIdentity(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
//                    headQuaterWebsocketSession.sendMessage(message);
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }

            } catch (IOException e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        }else if (messageType.equals(ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO_FROM_HEADQUATER.name())){

            String key_id = parseResult.get(PayloadKeyType.signingkeyID.name()).toString();
            String ios_key_path = parseResult.get("keyfilePath").toString();
            signingKeySettingService.updateByiOSDeployKeyfilePath(Integer.parseInt(key_id), ios_key_path);

            TextMessage message = null;
            try {

                message = new TextMessage(new ObjectMapper().writeValueAsString(parseResult));

//                headQuaterWebsocketSession = WHiveWebSocketHandler.getSessionByIdentity(parseResult.get(PayloadKeyType.hqKey.name()).toString(), SessionType.HEADQUATER);
                WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
                if(wHiveIdentity != null){
//                    headQuaterWebsocketSession.sendMessage(message);
                    ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
                }

            } catch (IOException e) {

                log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            }

        } else if (messageType.equals(ClientMessageType.BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO_FROM_HEADQUATER.name())){
            log.info(parseResult.toString());
            String key_id = parseResult.get(PayloadKeyType.signingkeyID.name()).toString();

            String profilesListStr = parseResult.get("profilesList").toString();
            String certificateListStr = parseResult.get("certificatesList").toString();
            String deployKeyFilePath = parseResult.get("keyfilePath").toString();

            profilesListStr = profilesListStr.replaceAll(" ","");
            profilesListStr = profilesListStr.replaceAll("\\{","{\"");
            profilesListStr = profilesListStr.replaceAll("\\}","\"}");
            profilesListStr = profilesListStr.replaceAll("=","\":\"");
            profilesListStr = profilesListStr.replaceAll(",","\",\"");
            profilesListStr = profilesListStr.replaceAll("\\}\",\"\\{","},{");
            // profilesListStr = profilesListStr.replaceAll("\\[\\{","[{\"");
            // profilesListStr = profilesListStr.replaceAll("\\}\\],","\"}]");

            certificateListStr = certificateListStr.replaceAll(" ","");
            certificateListStr = certificateListStr.replaceAll("\\{","{\"");
            certificateListStr = certificateListStr.replaceAll("\\}","\"}");
            certificateListStr = certificateListStr.replaceAll("=","\":\"");
            certificateListStr = certificateListStr.replaceAll(",","\",\"");
            certificateListStr = certificateListStr.replaceAll("\\}\",\"\\{","},{");
            // certificateListStr = certificateListStr.replaceAll("\\[\\{","[{\"");
            // scertificateListStr = certificateListStr.replaceAll("\\}\\],","\"}]");



            log.info(profilesListStr);
            log.info(certificateListStr);

            // List<Object> profilesListObj = Collections.singletonList(parseResult.get("profilesList").toString());
            // List<Object> certificateListObj = Collections.singletonList(parseResult.get("certificatesList").toString());
            signingKeySettingService.updateByAlliOSKeyFileObj(Integer.parseInt(key_id), profilesListStr, certificateListStr, deployKeyFilePath);

//            //TODO : builder 큐 관리 상태 값 업데이트 -1 적용
            KeySetting keySetting = signingKeySettingService.findByID(Integer.parseInt(key_id));

            BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(Long.valueOf(keySetting.getBuilder_id()));
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
            builderQueueManagedService.etcUpdate(builderQueueManaged);

            TextMessage message = null;


            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        } else if(messageType.equals(ClientMessageType.BIN_FILE_PROFILE_TEMPLATE_UPDATE_SEND_INFO_FROM_HEADQUATER.name())){

            String signingkey_id = parseResult.get(PayloadKeyType.signingkeyID.name()).toString();
            String signingkey_path = parseResult.get("keyfilePath").toString();
            String deployfilePath = parseResult.get("deployfilePath").toString();

            signingKeySettingService.updateByKeyfilePath(Integer.parseInt(signingkey_id), signingkey_path, deployfilePath);

            //TODO : builder 큐 관리 상태 값 업데이트 -1 적용
            BranchSetting branchSetting = branchSettingService.findByUserID(parseResult.get("builderID").toString());
            BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
            builderQueueManagedService.etcUpdate(builderQueueManaged);

            TextMessage message = null;


            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);
            }

        } else if(messageType.equals(ClientMessageType.BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO_FROM_HEADQUATER.name())){

            String key_id = parseResult.get(PayloadKeyType.signingkeyID.name()).toString();

            String profilesListStr = parseResult.get("profilesList").toString();
            String certificateListStr = parseResult.get("certificatesList").toString();
            String deployKeyFilePath = parseResult.get("keyfilePath").toString();

            profilesListStr = profilesListStr.replaceAll(" ","");
            profilesListStr = profilesListStr.replaceAll("\\{","{\"");
            profilesListStr = profilesListStr.replaceAll("\\}","\"}");
            profilesListStr = profilesListStr.replaceAll("=","\":\"");
            profilesListStr = profilesListStr.replaceAll(",","\",\"");
            profilesListStr = profilesListStr.replaceAll("\\}\",\"\\{","},{");
            // profilesListStr = profilesListStr.replaceAll("\\[\\{","[{\"");
            // profilesListStr = profilesListStr.replaceAll("\\}\\],","\"}]");

            certificateListStr = certificateListStr.replaceAll(" ","");
            certificateListStr = certificateListStr.replaceAll("\\{","{\"");
            certificateListStr = certificateListStr.replaceAll("\\}","\"}");
            certificateListStr = certificateListStr.replaceAll("=","\":\"");
            certificateListStr = certificateListStr.replaceAll(",","\",\"");
            certificateListStr = certificateListStr.replaceAll("\\}\",\"\\{","},{");
            // certificateListStr = certificateListStr.replaceAll("\\[\\{","[{\"");
            // scertificateListStr = certificateListStr.replaceAll("\\}\\],","\"}]");



            log.info(profilesListStr);
            log.info(certificateListStr);

            // List<Object> profilesListObj = Collections.singletonList(parseResult.get("profilesList").toString());
            // List<Object> certificateListObj = Collections.singletonList(parseResult.get("certificatesList").toString());
            signingKeySettingService.updateByAlliOSKeyFileObj(Integer.parseInt(key_id), profilesListStr, certificateListStr, deployKeyFilePath);

            //TODO : builder 큐 관리 상태 값 업데이트 -1 적용
            KeySetting keySetting = signingKeySettingService.findByID(Integer.parseInt(key_id));
            BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID((long) keySetting.getBuilder_id());
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() - 1);
            builderQueueManagedService.etcUpdate(builderQueueManaged);


            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(parseResult.get(PayloadKeyType.hqKey.name()).toString());
            if(wHiveIdentity != null){
                ClusterWebSocketService.sendMessage(wHiveIdentity, parseResult);

            }

        }


    }
}
