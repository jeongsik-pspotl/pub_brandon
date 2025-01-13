package com.pspotl.sidebranden.builder.handler;


import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.LogFileDownloadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class LogFileDownloagMsgHandler implements BranchHandlable {

    @Autowired
    LogFileDownloadService logFileDownloadService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        try {

            String messageType = parseResult.get(SessionType.MsgType.name()).toString();
            String filePath = parseResult.get("filePath").toString();
            String fileName = parseResult.get("fileName").toString();
            String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();

            // parameter list build history : filepath, filename
            // HV_MSG_APP_DOWNLOAD_ACTION_INFO
            if(messageType.equals(BuildServiceType.HV_MSG_LOGFILE_DOWNLOAD_INFO.name()) || messageType.equals(BuildServiceType.HV_MSG_LOGFILE_DOWNLOAD_INFO_FROM_BRANCH.toString())){
                logFileDownloadService.logFileDownloadFileToHeadquater(session, filePath, fileName, hqKey);

            }

        } catch (NullPointerException e) {
            log.warn(e.getMessage(), e);
        }

    }

}
