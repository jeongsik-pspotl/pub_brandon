package com.inswave.whive.branch.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class BranchUploadFilePassHandler {

    @Autowired
    FileUploadServiceManager fileUploadServiceManager;

    private final Integer defaultOffset = 4;

    public void handle(WebSocketSession session, BinaryMessage websocketMessage) {

        fileUploadOutputMethod(session, websocketMessage);

    }

    private void fileUploadOutputMethod(WebSocketSession session,BinaryMessage websocketMessage){

        File profile = null;

        try{
            byte[] payload = new byte[websocketMessage.getPayload().remaining()];
            websocketMessage.getPayload().get(payload);

            byte[] adminIdLengthBuf = new byte[defaultOffset];

            // branch id length
            Integer offset = 0;
            System.arraycopy(payload, offset, adminIdLengthBuf, 0, defaultOffset);
            Integer adminIdIntLength = byteArrayToInt(adminIdLengthBuf);
            offset = offset + defaultOffset;

            // branch id
            byte[] adminIdBuf = new byte[adminIdIntLength];
            System.arraycopy(payload, offset, adminIdBuf, 0, adminIdIntLength);
            String adminId = new String(adminIdBuf, Charset.forName(StandardCharsets.UTF_8.name()));
            byte[] adminIdBytes = adminId.getBytes(StandardCharsets.UTF_8);
            Integer adminLength = adminIdBytes.length;
            offset = offset + adminLength;


            // bin type length
            byte[] binTypeLengthBuf = new byte[defaultOffset];
            System.arraycopy(payload, offset, binTypeLengthBuf, 0, defaultOffset);
            Integer binTypeIntLength = byteArrayToInt(binTypeLengthBuf);
            offset = offset + defaultOffset;

            // profile name
            byte[] binTypeBuf = new byte[binTypeIntLength];
            System.arraycopy(payload, offset, binTypeBuf, 0, binTypeIntLength);
            String binType = new String(binTypeBuf, Charset.forName(StandardCharsets.UTF_8.name()));

            fileUploadServiceManager.handleBinaryRequest(session, binType, payload);
            // BIN_FILE_SIGNINGKEY_SEND_INFO message
            // 서비스 및 메소드 분기 처리 FileUploadServiceManager 분기 처리 해서
            // session, payload 두개의 파라미터 전달


        }catch (ArrayIndexOutOfBoundsException e){
                
            log.error("builder file upload error ",e);

        }


    }

    public int byteArrayToInt(byte [] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

    private Integer convertByteToInt(byte[] b){
        int MASK = 0xFF;
        int result = 0;
        result = b[0] & MASK;
        result = result + ((b[1] & MASK) << 8);
        result = result + ((b[2] & MASK) << 16);
        result = result + ((b[3] & MASK) << 24);
        return result;
    }

}
