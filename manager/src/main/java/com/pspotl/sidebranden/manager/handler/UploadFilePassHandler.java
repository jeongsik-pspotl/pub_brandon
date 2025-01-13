package com.pspotl.sidebranden.manager.handler;


import com.pspotl.sidebranden.manager.client.ClientHandler;
import com.pspotl.sidebranden.manager.service.ClusterWebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class UploadFilePassHandler {

    @Autowired
    ClientHandler clientHandler;
    private static final Integer defaultOffset = 4;
    public void handle(WebSocketSession session, BinaryMessage websocketMessage) {

        try{
            byte[] payload = new byte[websocketMessage.getPayload().remaining()];
            websocketMessage.getPayload().get(payload);

            byte[] adminIdLengthBuf = new byte[defaultOffset];
            Integer offset = 0;
            System.arraycopy(payload, offset, adminIdLengthBuf, 0, defaultOffset);
            Integer adminIdLength = byteArrayToInt(adminIdLengthBuf);
            offset = offset + defaultOffset;

            byte[] adminIdBuf = new byte[adminIdLength];
            System.arraycopy(payload, offset, adminIdBuf, 0, adminIdLength);
            String adminId = new String(adminIdBuf, Charset.forName("UTF-8"));
            byte[] adminIdBytes = adminId.getBytes(StandardCharsets.UTF_8);
            Integer adminLength = adminIdBytes.length;
            offset = offset + adminLength;

            // hqKey
            byte[] hqKeyLengthBuf =  new byte[defaultOffset];
            System.arraycopy(payload, offset, hqKeyLengthBuf, 0, defaultOffset);
            Integer hqKeyIntLength = byteArrayToInt(hqKeyLengthBuf);
            offset = offset + defaultOffset;

            byte[] hqKeyBuf = new byte[hqKeyIntLength];
            System.arraycopy(payload, offset, hqKeyBuf, 0, hqKeyIntLength);
            String hqKey = new String(hqKeyBuf, Charset.forName("UTF-8"));


            WHiveIdentity wHiveIdentity = ClusterWebSocketService.getIdentityManager(hqKey);
            if (wHiveIdentity != null) {
                ClusterWebSocketService.sendBinaryLocal(wHiveIdentity, payload);
            }

        }catch (ArrayIndexOutOfBoundsException e){

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }
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

    public int byteArrayToInt(byte [] b) {
        return (b[0] << 24) + ((b[1] & 0xFF) << 16) + ((b[2] & 0xFF) << 8) + (b[3] & 0xFF);
    }

}
