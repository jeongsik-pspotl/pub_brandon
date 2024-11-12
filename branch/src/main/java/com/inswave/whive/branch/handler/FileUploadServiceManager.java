package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class FileUploadServiceManager {

    @Autowired
    AppleApiRestfulMessageHandler appleApiRestfulMessageHandler;

    @Autowired
    ProvioningProfileMessageHandler provioningProfileMessageHandler;

    @Autowired
    AppIconZipfileBinMessageHandler appIconZipfileBinMessageHandler;

    @Autowired
    KeyFileSendBinMessageHandler keyFileSendBinMessageHandler;

    @Autowired
    iOSAllKeyFileSendBinMsgHandler iOSAllKeyFileSendBinMsgHandler;

    @Autowired
    KeyFileUpdateBinMsgHandler keyFileUpdateBinMsgHandler;

    private static final Map<String, BranchBinaryHandle> branchBinaryHandleMap =
            Collections.synchronizedMap(new HashMap<String, BranchBinaryHandle>());

    @PostConstruct
    public void setFileUploadServiceManager(){
        // BIN_FILE_SIGNINGKEY_SEND_INFO
        branchBinaryHandleMap.put(BuildServiceType.BIN_FILE_SIGNINGKEY_SEND_INFO.name(), provioningProfileMessageHandler);

        // MV_MSG_BIN_SINGING_KEY_ADD_INFO
        branchBinaryHandleMap.put(BuildServiceType.MV_BIN_SINGING_KEY_ADD_INFO.name(), appleApiRestfulMessageHandler);

        // BIN_FILE_ZIPFILE_SEND_INFO
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_APP_ICON_APPEND_SEND_INFO.name(), appIconZipfileBinMessageHandler);

        // BIN_FILE_APP_ICON_UPDATE_SEND_INFO
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_APP_ICON_UPDATE_SEND_INFO.name(), appIconZipfileBinMessageHandler);

        // BIN_FILE_PROFILE_TEMPLATE_SEND_INFO
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_PROFILE_TEMPLATE_SEND_INFO.name(), keyFileSendBinMessageHandler);

        // BIN_FILE_PROFILE_TEMPLATE_UPDATE_SEND_INFO Android profile update
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_PROFILE_TEMPLATE_UPDATE_SEND_INFO.name(), keyFileUpdateBinMsgHandler);

        // BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO.name(), keyFileSendBinMessageHandler);

        // BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO.name(), keyFileSendBinMessageHandler);

        // BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO.name(), iOSAllKeyFileSendBinMsgHandler);

        // BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO ios profiles update
        branchBinaryHandleMap.put(ProjectServiceType.BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO.name(), iOSAllKeyFileSendBinMsgHandler);


    }

    // 내부 소스 수정 필요.
    public void handleBinaryRequest(WebSocketSession session, String msgType ,byte[] payload){

        BranchBinaryHandle branchBinaryHandle = branchBinaryHandleMap.get(msgType);

        if(branchBinaryHandle == null) {
            log.debug("Not registered command");
        } else {
            branchBinaryHandle.handleBinaryPayLoad(session, msgType, payload);
        }

    }


    public void handleBinaryRequest(WebSocketSession session, Map<String, Object> parseResult){
        BranchBinaryHandle branchBinaryHandle = branchBinaryHandleMap.get(parseResult.get(SessionType.MsgType.name()));

        if(branchBinaryHandle == null) {
            log.debug("Not registered command");
        } else {
            branchBinaryHandle.handleBinary(session, parseResult);
        }
    }


}
