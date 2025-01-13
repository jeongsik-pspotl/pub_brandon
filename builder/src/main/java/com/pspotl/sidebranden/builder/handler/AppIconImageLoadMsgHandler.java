package com.pspotl.sidebranden.builder.handler;


import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.AppIconService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class AppIconImageLoadMsgHandler implements BranchHandlable {

    @Autowired
    AppIconService appIconService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        // parameter list build history : filepath, filename
        // HV_MSG_APP_DOWNLOAD_ACTION_INFO
        if(messageType.equals(ProjectServiceType.HV_MSG_APPICON_IMAGE_LOAD_INFO.name()) || messageType.equals(ProjectServiceType.HV_MSG_APPICON_IMAGE_LOAD_INFO_FROM_BRANCH.toString())){

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();


            // workspace, project
            log.info("HV_MSG_APPICON_IMAGE_LOAD_INFO parseResult : {}",parseResult);

            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                // parseResult
                appIconService.sendAppIconImageToBase64(session, parseResult, hqKey);
            }else if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                appIconService.sendAppIconImageToBase64(session, parseResult, hqKey);
            }


        }


    }


}
