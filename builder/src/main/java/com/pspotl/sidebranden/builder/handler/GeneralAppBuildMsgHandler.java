package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.domain.BuildMode;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.BuildService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class GeneralAppBuildMsgHandler implements BranchHandlable {

    @Autowired
    BuildService buildService;
    JSONObject branchObj = null;
    JSONParser parser = new JSONParser();

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {
        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String platform = parseResult.get(PayloadMsgType.platform.name()).toString().toLowerCase();

        try {
            branchObj = (JSONObject) parser.parse(parseResult.get(PayloadMsgType.branchSettingObj.name()).toString());

            if (branchObj.get(PayloadMsgType.branchUserId.name()).toString() == null) {
                return;
            }

            if (branchObj.get(PayloadMsgType.branchName.name()).toString().equals("")) {
                return;
            }

            if (messageType.equals(BuildServiceType.HV_MSG_GENERAL_ANDROID_DEBUG_BUILD.name())) {
                buildService.generalAppStartBuild(session, parseResult, platform, BuildMode.DEBUG);
            } else if (messageType.equals(BuildServiceType.HV_MSG_GENERAL_ANDROID_RELEASE_BUILD.name())) {
                buildService.generalAppStartBuild(session, parseResult, platform, BuildMode.RELEASE);
            } else if (messageType.equals(BuildServiceType.HV_MSG_GENERAL_ANDROID_AAB_DEBUG_BUILD.name())) {
                buildService.generalAppStartBuild(session, parseResult, platform, BuildMode.AAB_DEBUG);
            } else if (messageType.equals(BuildServiceType.HV_MSG_GENERAL_ANDROID_AAB_RELEASE_BUILD.name())) {
                buildService.generalAppStartBuild(session, parseResult, platform, BuildMode.AAB_RELEASE);
            } else if (messageType.equals(BuildServiceType.HV_MSG_GENERAL_IOS_DEBUG_BUILD.name())) {
                buildService.generalAppStartBuild(session, parseResult, platform, BuildMode.DEBUG);
            } else if (messageType.equals(BuildServiceType.HV_MSG_GENERAL_IOS_RELEASE_BUILD.name())) {
                buildService.generalAppStartBuild(session, parseResult, platform, BuildMode.RELEASE);
            }
        } catch (Exception e) {
            log.error("GeneralAppBuildMsgHandler handle Error = {}", e.getMessage(), e);
        }
    }
}
