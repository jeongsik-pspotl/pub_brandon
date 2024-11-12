package com.inswave.whive.branch.service;

import com.inswave.whive.branch.enums.BinaryServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.util.BranchRestTempleteUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class LogFileDownloadService extends BaseService {

    @Value("${whive.distribution.deployLogPath}")
    private String buildLogs;

    @Value("${whive.server.target}")
    private String headquaterUrl;

    @Value("${whive.branch.id}")
    private String userId;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    BranchRestTempleteUtil branchRestTempleteUtil;

    BinaryMessage websocketMessage;

    WebSocketSession _session;

    private Integer defaultOffset = 4;
    private String _hqKey;

    public void logFileDownloadFileToHeadquater(WebSocketSession session, String filePath, String fileName, String hqKey){
        _session = session;
        _hqKey = hqKey;

        SendToHeadquaterFileObj(filePath, fileName);
    }

    private void SendToHeadquaterFileObj(String filePath, String fileName){

        // byte length 변수 선언
        byte[] byte_amdin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_hqKey = null;
        byte[] byte_hqKey_length = null;
        byte[] byte_bin_type = null;
        byte[] byte_bin_type_length = null;
        byte[] byte_logfile_name = null;
        byte[] byte_logfile_name_length = null;
        byte[] byte_logfile = null;
        byte[] byte_logfile_length = null;
        byte[] byte_message = null;
        int message_length = 0;
        int offset = 0;


        File file = new File(filePath + fileName);

        try {

            // user id
            byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
            message_length += byte_amdin_id.length;

            // user id length
            byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
            message_length += byte_admin_id_length.length;

            //hqKey
            byte_hqKey = _hqKey.getBytes(StandardCharsets.UTF_8);
            message_length += byte_hqKey.length;

            //hqKey length
            byte_hqKey_length = convertIntToByteArray(byte_hqKey.length);
            message_length += byte_hqKey_length.length;

            // bin typee
            byte_bin_type = BinaryServiceType.HV_BIN_APP_ICON_READ.toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;

            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;

            // logfile name
            byte_logfile_name = file.getName().getBytes(StandardCharsets.UTF_8);
            message_length += byte_logfile_name.length;

            // logfile name length
            byte_logfile_name_length = convertIntToByteArray(byte_logfile_name.length);
            message_length += byte_logfile_name_length.length;

            // logfile
            byte_logfile = FileUtils.readFileToByteArray(file);
            message_length += byte_logfile.length;

            // logfile length
            byte_logfile_length = convertIntToByteArray(byte_logfile.length);
            message_length += byte_logfile_length.length;

            byte_message = new byte[message_length];

            // admin id
            System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
            offset += byte_admin_id_length.length;
            System.arraycopy(byte_amdin_id, 0, byte_message, offset, byte_amdin_id.length);
            offset += byte_amdin_id.length;

            // hqKey
            System.arraycopy(byte_hqKey_length, 0, byte_message, offset, byte_hqKey_length.length);
            offset += byte_hqKey_length.length;
            System.arraycopy(byte_hqKey, 0, byte_message, offset, byte_hqKey.length);
            offset += byte_hqKey.length;

            // bin type
            System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
            offset += byte_bin_type_length.length;
            System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
            offset += byte_bin_type.length;

            // log file name
            System.arraycopy(byte_logfile_name_length, 0, byte_message, offset, byte_logfile_name_length.length);
            offset += byte_logfile_name_length.length;
            System.arraycopy(byte_logfile_name, 0, byte_message, offset, byte_logfile_name.length);
            offset += byte_logfile_name.length;

            // log file
            System.arraycopy(byte_logfile_length, 0, byte_message, offset, byte_logfile_length.length);
            offset += byte_logfile_length.length;
            System.arraycopy(byte_logfile, 0, byte_message, offset, byte_logfile.length);

            // send to headquater
            // session && hqkey 추가
            headQuaterClientHandler.sendMessage(_session, new BinaryMessage(byte_message));

            // System.arraycopy(payload, offset, adminIdBuf, 0, adminIdLength);
            // String adminId = new String(byte_message, Charset.forName(StandardCharsets.UTF_8.name()));

        } catch (IOException e) {

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

    // int to byteArray 변환
//    private byte[] convertIntToByteArray(int value) {
//        byte[] byteArray = new byte[4];
//        byteArray[0] = (byte)(value >> 24);
//        byteArray[1] = (byte)(value >> 16);
//        byteArray[2] = (byte)(value >> 8);
//        byteArray[3] = (byte)(value);
//        return byteArray;
//    }



}
