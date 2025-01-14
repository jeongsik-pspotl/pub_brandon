package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.domain.SigningMode;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.AppleApiService;
import com.pspotl.sidebranden.builder.service.SigningKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class SigningKeyAddMessageHandler implements BranchHandlable {

    private static JsonParser parser = JsonParserFactory.getJsonParser();

    @Autowired
    SigningKeyService signingKeyService;

    // ios app store connect api 호출 전용 AppleApiService
    @Autowired
    AppleApiService appleApiService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String pluginPath = parseResult.get("path").toString();
        String pluginPlatform = parseResult.get(PayloadMsgType.platform.name()).toString();
        Map<String, Object> signingResult = parser.parseMap((String) parseResult.get("keytooldata"));


        // MV_MSG_SIGNIN_KEY_ADD_INFO
        if(messageType.equals(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name()) && pluginPlatform.toLowerCase().equals(PayloadMsgType.android.name())){
             signingKeyService.startSigninKeyAdd(pluginPath, pluginPlatform, SigningMode.CREATE, signingResult);
        }else if (messageType.equals(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name()) && pluginPlatform.toLowerCase().equals(PayloadMsgType.ios.name())){
            appleApiService.insertCertificates(pluginPath, pluginPlatform, SigningMode.CREATE, signingResult);
        }
    }

}
