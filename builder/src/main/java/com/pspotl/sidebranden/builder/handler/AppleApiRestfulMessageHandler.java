package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.domain.AppleResultDTO;
import com.pspotl.sidebranden.builder.domain.SigningMode;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.AppleApiService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AppleApiRestfulMessageHandler implements BranchBinaryHandle{

    @Autowired
    AppleApiService appleApiService;

    @Override
    public void handleBinary(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        Map <String,Object> signingKeyMap = new HashMap<>();
        log.info("AppleApiRestfulMessageHandler handleBinary messageType : {} ", messageType);
        log.info("AppleApiRestfulMessageHandler handleBinary jsonObj : {} ", parseResult.get("jsonObj"));

        JSONObject resultObj = (JSONObject) parseResult.get("jsonObj");;

        signingKeyMap.put("file",parseResult.get("file"));
        signingKeyMap.put("filename",resultObj.get("filename"));
        signingKeyMap.put("bundleId",resultObj.get("bundleId"));
        signingKeyMap.put("devId","");
        signingKeyMap.put("app_name",resultObj.get("app_name"));
        signingKeyMap.put("signing_path",resultObj.get("signing_path"));
        signingKeyMap.put("signing_issuer_id",resultObj.get("signing_issuer_id"));
        signingKeyMap.put("signing_key_id",resultObj.get("signing_key_id"));
        signingKeyMap.put("provisioning_profiles_name",resultObj.get("provisioning_profiles_name"));

        String signingPath = (String) resultObj.get("signing_path");
        String signingPlatform = (String) resultObj.get(PayloadMsgType.platform.name());
        String bundleId = "";
        String cerId = "";

        AppleResultDTO appleResultDTOCertificates = null;
        AppleResultDTO appleResultDTOBundleId = null;

        if(messageType.equals(BuildServiceType.MV_BIN_SINGING_KEY_ADD_INFO.name())){
            //registerNewBundleId 실행
            appleResultDTOBundleId = appleApiService.registerNewBundleId(signingPath, signingPlatform, SigningMode.CREATE, signingKeyMap);
            bundleId = appleResultDTOBundleId.getId();
            signingKeyMap.replace("bundleId",bundleId);

            // insertCertificates 실행
            if(appleResultDTOBundleId != null){
                appleResultDTOCertificates = appleApiService.insertCertificates(signingPath, signingPlatform, SigningMode.CREATE, signingKeyMap);
            }
            appleApiService.getNumberOfAvailableDevices(signingPath, signingPlatform, SigningMode.CREATE, signingKeyMap);
            // getMobileprovision 메소드 실행
            if(appleResultDTOCertificates != null){
                // cerId 값 생성 및 세팅
                cerId = appleResultDTOCertificates.getId();
                signingKeyMap.put("cerId",cerId);
                File file = appleApiService.getMobileprovision(signingPath, signingPlatform, SigningMode.CREATE, signingKeyMap);
            }
        }

    }

    @Override
    public void handleBinaryPayLoad(WebSocketSession session, String msgType, byte[] payload) {

    }
}
