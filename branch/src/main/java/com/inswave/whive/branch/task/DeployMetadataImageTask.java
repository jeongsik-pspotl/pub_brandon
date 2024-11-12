package com.inswave.whive.branch.task;

import com.inswave.whive.branch.enums.BinaryServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.service.BaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Component
public class DeployMetadataImageTask extends BaseService {

    @Value("${whive.branch.id}")
    private String userId;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    // TODO : android screen shot 디바이스 기준으로 분할 전송 기능 구현 하기
    public synchronized boolean deployMetadataAndroidAllImageFileSend(WebSocketSession session, Map<String, Object> parseResult, String builderPath, String msgTye){

        String hqKey = parseResult.get("hqKey").toString();

        byte[] byte_amdin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_hqKey = null;
        byte[] byte_hqKey_length = null;
        byte[] byte_bin_type = null;
        byte[] byte_bin_type_length = null;
        byte[] byte_imagefile_list_size = null;
        byte[] byte_imagefile_list_length = null;
        byte[][] byte_imagefile_name = null;
        byte[][] byte_imagefile_name_length = null;
        byte[][] byte_imagefile = null;
        byte[][] byte_imagefile_length = null;
        byte[] byte_message = null;
        int message_length = 0;
        int offset = 0;


        File dir = new File(builderPath);
        File files[] = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String imageFileName = "";
                boolean imagefilenameYn = false;
                // TODO : ios file name 규칙에 맞춰서 file list flietr 기능 구현..
                if(name.contains("_ko-KR")){
                    log.info(name);
                    imageFileName = name;
                    imagefilenameYn = imageFileName.toLowerCase().endsWith(".png");
                }
                return imagefilenameYn;

            }
        });

        Arrays.sort(files); // TODO : sort arrays 기능 구현

        try {

            // user id
            byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
            message_length += byte_amdin_id.length;

            // user id length
            byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
            message_length += byte_admin_id_length.length;

            //hqKey
            byte_hqKey = hqKey.getBytes(StandardCharsets.UTF_8);
            message_length += byte_hqKey.length;

            //hqKey length
            byte_hqKey_length = convertIntToByteArray(byte_hqKey.length);
            message_length += byte_hqKey_length.length;

            // bin type TODO : file list filter 뿐만 아니라 bin msg type 기준도 분기 처리할 수 있게 구현해야함.
            byte_bin_type = msgTye.getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;

            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;

            byte_imagefile_list_size =  String.valueOf(files.length).getBytes(StandardCharsets.UTF_8);
            message_length += byte_imagefile_list_size.length;

            byte_imagefile_list_length = convertIntToByteArray(byte_imagefile_list_size.length);
            message_length += byte_imagefile_list_length.length;

            byte_imagefile_name = new byte[files.length][];
            byte_imagefile_name_length = new byte[files.length][];
            byte_imagefile = new byte[files.length][];
            byte_imagefile_length = new byte[files.length][];

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {

                } else {
                    // fileone = file;
                    log.info("file name {}, {}",i ,file.getName());
                    // image name
                    byte_imagefile_name[i] = file.getName().getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile_name[i].length;

                    // image name length
                    byte_imagefile_name_length[i] = convertIntToByteArray(byte_imagefile_name[i].length);
                    message_length += byte_imagefile_name_length[i] .length;

                    // image file to base64
                    byte_imagefile[i] = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)).getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile[i].length;

                    // logfile length
                    byte_imagefile_length[i] = convertIntToByteArray(byte_imagefile[i].length);
                    message_length += byte_imagefile_length[i].length;

                }
            }

            // image file length ...
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

            // image file list size
            System.arraycopy(byte_imagefile_list_length, 0, byte_message, offset, byte_imagefile_list_length.length);
            offset += byte_imagefile_list_length.length;
            System.arraycopy(byte_imagefile_list_size, 0, byte_message, offset, byte_imagefile_list_size.length);
            offset += byte_imagefile_list_size.length;

            // image file name list
            for(int k = 0; k < files.length; k++){

                System.arraycopy(byte_imagefile_name_length[k], 0, byte_message, offset, byte_imagefile_name_length[k].length);
                offset += byte_imagefile_name_length[k].length;
                System.arraycopy(byte_imagefile_name[k], 0, byte_message, offset, byte_imagefile_name[k].length);
                offset += byte_imagefile_name[k].length;

                // image file list
                System.arraycopy(byte_imagefile_length[k], 0, byte_message, offset, byte_imagefile_length[k].length);
                offset += byte_imagefile_length[k].length;
                System.arraycopy(byte_imagefile[k], 0, byte_message, offset, byte_imagefile[k].length);
                offset += byte_imagefile[k].length;

            }

            // send to headquater
            // session && hqkey 추가
            // log.info("{}", byte_message);
            headQuaterClientHandler.sendMessage(session, new BinaryMessage(byte_message));

        } catch (IOException e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

        return true;
    }



    // TODO : ios screen shot 디바이스 기준으로 분할 전송 기능 구현 하기
    public synchronized boolean deployMetadataImageFileSend(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        String hqKey = parseResult.get("hqKey").toString();

        byte[] byte_amdin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_hqKey = null;
        byte[] byte_hqKey_length = null;
        byte[] byte_bin_type = null;
        byte[] byte_bin_type_length = null;
        byte[] byte_imagefile_list_size = null;
        byte[] byte_imagefile_list_length = null;
        byte[][] byte_imagefile_name = null;
        byte[][] byte_imagefile_name_length = null;
        byte[][] byte_imagefile = null;
        byte[][] byte_imagefile_length = null;
        byte[] byte_message = null;
        int message_length = 0;
        int offset = 0;


        File dir = new File(builderPath);
        File files[] = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String imageFileName = "";
                boolean imagefilenameYn = false;
                // TODO : ios file name 규칙에 맞춰서 file list flietr 기능 구현..
                if(name.contains("_APP_IPHONE_65_")){
                    log.info(name);
                    imageFileName = name;
                    imagefilenameYn = imageFileName.toLowerCase().endsWith(".png");
                }
                return imagefilenameYn;

            }
        });

        Arrays.sort(files); // TODO : sort arrays 기능 구현

        try {

            // user id
            byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
            message_length += byte_amdin_id.length;

            // user id length
            byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
            message_length += byte_admin_id_length.length;

            //hqKey
            byte_hqKey = hqKey.getBytes(StandardCharsets.UTF_8);
            message_length += byte_hqKey.length;

            //hqKey length
            byte_hqKey_length = convertIntToByteArray(byte_hqKey.length);
            message_length += byte_hqKey_length.length;

            // bin type TODO : file list filter 뿐만 아니라 bin msg type 기준도 분기 처리할 수 있게 구현해야함.
            byte_bin_type = BinaryServiceType.HV_BIN_DEPLOY_METADATA_IMAGE_READ.toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;

            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;

            byte_imagefile_list_size =  String.valueOf(files.length).getBytes(StandardCharsets.UTF_8);
            message_length += byte_imagefile_list_size.length;

            byte_imagefile_list_length = convertIntToByteArray(byte_imagefile_list_size.length);
            message_length += byte_imagefile_list_length.length;

            byte_imagefile_name = new byte[files.length][];
            byte_imagefile_name_length = new byte[files.length][];
            byte_imagefile = new byte[files.length][];
            byte_imagefile_length = new byte[files.length][];

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {

                } else {
                    // fileone = file;
                    log.info("file name {}, {}",i ,file.getName());
                    // image name
                    byte_imagefile_name[i] = file.getName().getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile_name[i].length;

                    // image name length
                    byte_imagefile_name_length[i] = convertIntToByteArray(byte_imagefile_name[i].length);
                    message_length += byte_imagefile_name_length[i] .length;

                    // image file to base64
                    byte_imagefile[i] = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)).getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile[i].length;

                    // logfile length
                    byte_imagefile_length[i] = convertIntToByteArray(byte_imagefile[i].length);
                    message_length += byte_imagefile_length[i].length;

                }
            }

            // image file length ...
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

            // image file list size
            System.arraycopy(byte_imagefile_list_length, 0, byte_message, offset, byte_imagefile_list_length.length);
            offset += byte_imagefile_list_length.length;
            System.arraycopy(byte_imagefile_list_size, 0, byte_message, offset, byte_imagefile_list_size.length);
            offset += byte_imagefile_list_size.length;

            // image file name list
            for(int k = 0; k < files.length; k++){

                System.arraycopy(byte_imagefile_name_length[k], 0, byte_message, offset, byte_imagefile_name_length[k].length);
                offset += byte_imagefile_name_length[k].length;
                System.arraycopy(byte_imagefile_name[k], 0, byte_message, offset, byte_imagefile_name[k].length);
                offset += byte_imagefile_name[k].length;

                // image file list
                System.arraycopy(byte_imagefile_length[k], 0, byte_message, offset, byte_imagefile_length[k].length);
                offset += byte_imagefile_length[k].length;
                System.arraycopy(byte_imagefile[k], 0, byte_message, offset, byte_imagefile[k].length);
                offset += byte_imagefile[k].length;

            }

            // send to headquater
            // session && hqkey 추가
            // log.info("{}", byte_message);
            headQuaterClientHandler.sendMessage(session, new BinaryMessage(byte_message));

            // System.arraycopy(payload, offset, adminIdBuf, 0, adminIdLength);
            // String adminId = new String(byte_message, Charset.forName(StandardCharsets.UTF_8.name()));

        } catch (IOException e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

        return true;
    }

    public synchronized void deployMetadataPhoneDeivceImageFileSend(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        String hqKey = parseResult.get("hqKey").toString();

        byte[] byte_amdin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_hqKey = null;
        byte[] byte_hqKey_length = null;
        byte[] byte_bin_type = null;
        byte[] byte_bin_type_length = null;
        byte[] byte_imagefile_list_size = null;
        byte[] byte_imagefile_list_length = null;
        byte[][] byte_imagefile_name = null;
        byte[][] byte_imagefile_name_length = null;
        byte[][] byte_imagefile = null;
        byte[][] byte_imagefile_length = null;
        byte[] byte_message = null;
        int message_length = 0;
        int offset = 0;


        File dir = new File(builderPath);
        File files[] = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String imageFileName = "";
                boolean imagefilenameYn = false;
                // TODO : ios file name 규칙에 맞춰서 file list flietr 기능 구현..
                if(name.contains("_APP_IPHONE_55_")){
                    log.info(name);
                    imageFileName = name;
                    imagefilenameYn = imageFileName.toLowerCase().endsWith(".png");
                }
                return imagefilenameYn;
            }
        });

        Arrays.sort(files); // TODO : sort arrays 기능 구현

        try {

            // user id
            byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
            message_length += byte_amdin_id.length;

            // user id length
            byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
            message_length += byte_admin_id_length.length;

            //hqKey
            byte_hqKey = hqKey.getBytes(StandardCharsets.UTF_8);
            message_length += byte_hqKey.length;

            //hqKey length
            byte_hqKey_length = convertIntToByteArray(byte_hqKey.length);
            message_length += byte_hqKey_length.length;

            // bin type TODO : file list filter 뿐만 아니라 bin msg type 기준도 분기 처리할 수 있게 구현해야함.
            byte_bin_type = BinaryServiceType.HV_BIN_DEPLOY_METADATA_PHONE_IMAGE_READ.toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;

            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;

            byte_imagefile_list_size =  String.valueOf(files.length).getBytes(StandardCharsets.UTF_8);
            message_length += byte_imagefile_list_size.length;

            byte_imagefile_list_length = convertIntToByteArray(byte_imagefile_list_size.length);
            message_length += byte_imagefile_list_length.length;

            byte_imagefile_name = new byte[files.length][];
            byte_imagefile_name_length = new byte[files.length][];
            byte_imagefile = new byte[files.length][];
            byte_imagefile_length = new byte[files.length][];

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {

                } else {
                    // fileone = file;
                    log.info("file name {}, {}",i ,file.getName());
                    // image name
                    byte_imagefile_name[i] = file.getName().getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile_name[i].length;

                    // image name length
                    byte_imagefile_name_length[i] = convertIntToByteArray(byte_imagefile_name[i].length);
                    message_length += byte_imagefile_name_length[i] .length;

                    // image file to base64
                    byte_imagefile[i] = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)).getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile[i].length;

                    // logfile length
                    byte_imagefile_length[i] = convertIntToByteArray(byte_imagefile[i].length);
                    message_length += byte_imagefile_length[i].length;

                }
            }

            // image file length ...
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

            // image file list size
            System.arraycopy(byte_imagefile_list_length, 0, byte_message, offset, byte_imagefile_list_length.length);
            offset += byte_imagefile_list_length.length;
            System.arraycopy(byte_imagefile_list_size, 0, byte_message, offset, byte_imagefile_list_size.length);
            offset += byte_imagefile_list_size.length;

            // image file name list
            for(int k = 0; k < files.length; k++){

                System.arraycopy(byte_imagefile_name_length[k], 0, byte_message, offset, byte_imagefile_name_length[k].length);
                offset += byte_imagefile_name_length[k].length;
                System.arraycopy(byte_imagefile_name[k], 0, byte_message, offset, byte_imagefile_name[k].length);
                offset += byte_imagefile_name[k].length;

                // image file list
                System.arraycopy(byte_imagefile_length[k], 0, byte_message, offset, byte_imagefile_length[k].length);
                offset += byte_imagefile_length[k].length;
                System.arraycopy(byte_imagefile[k], 0, byte_message, offset, byte_imagefile[k].length);
                offset += byte_imagefile[k].length;

            }

            // send to headquater
            // session && hqkey 추가
            // log.info("{}", byte_message);
            headQuaterClientHandler.sendMessage(session, new BinaryMessage(byte_message));

            // System.arraycopy(payload, offset, adminIdBuf, 0, adminIdLength);
            // String adminId = new String(byte_message, Charset.forName(StandardCharsets.UTF_8.name()));

        } catch (IOException e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }



    }

    public synchronized void deployMetadataTabletSmallInchImageFileSend(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        String hqKey = parseResult.get("hqKey").toString();

        byte[] byte_amdin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_hqKey = null;
        byte[] byte_hqKey_length = null;
        byte[] byte_bin_type = null;
        byte[] byte_bin_type_length = null;
        byte[] byte_imagefile_list_size = null;
        byte[] byte_imagefile_list_length = null;
        byte[][] byte_imagefile_name = null;
        byte[][] byte_imagefile_name_length = null;
        byte[][] byte_imagefile = null;
        byte[][] byte_imagefile_length = null;
        byte[] byte_message = null;
        int message_length = 0;
        int offset = 0;


        File dir = new File(builderPath);
        File files[] = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String imageFileName = "";
                boolean imagefilenameYn = false;
                // TODO : ios file name 규칙에 맞춰서 file list flietr 기능 구현..
                if(name.contains("_APP_IPAD_PRO_129_")){
                    log.info(name);
                    imageFileName = name;
                    imagefilenameYn = imageFileName.toLowerCase().endsWith(".png");
                }
                return imagefilenameYn;
            }
        });

        Arrays.sort(files); // TODO : sort arrays 기능 구현

        try {

            // user id
            byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
            message_length += byte_amdin_id.length;

            // user id length
            byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
            message_length += byte_admin_id_length.length;

            //hqKey
            byte_hqKey = hqKey.getBytes(StandardCharsets.UTF_8);
            message_length += byte_hqKey.length;

            //hqKey length
            byte_hqKey_length = convertIntToByteArray(byte_hqKey.length);
            message_length += byte_hqKey_length.length;

            // bin type TODO : file list filter 뿐만 아니라 bin msg type 기준도 분기 처리할 수 있게 구현해야함.
            byte_bin_type = BinaryServiceType.HV_BIN_DEPLOY_METADATA_TABLET_SMALL_IMAGE_READ.toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;

            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;

            byte_imagefile_list_size =  String.valueOf(files.length).getBytes(StandardCharsets.UTF_8);
            message_length += byte_imagefile_list_size.length;

            byte_imagefile_list_length = convertIntToByteArray(byte_imagefile_list_size.length);
            message_length += byte_imagefile_list_length.length;

            byte_imagefile_name = new byte[files.length][];
            byte_imagefile_name_length = new byte[files.length][];
            byte_imagefile = new byte[files.length][];
            byte_imagefile_length = new byte[files.length][];

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {

                } else {
                    // fileone = file;
                    log.info("file name {}, {}",i ,file.getName());
                    // image name
                    byte_imagefile_name[i] = file.getName().getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile_name[i].length;

                    // image name length
                    byte_imagefile_name_length[i] = convertIntToByteArray(byte_imagefile_name[i].length);
                    message_length += byte_imagefile_name_length[i] .length;

                    // image file to base64
                    byte_imagefile[i] = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)).getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile[i].length;

                    // logfile length
                    byte_imagefile_length[i] = convertIntToByteArray(byte_imagefile[i].length);
                    message_length += byte_imagefile_length[i].length;

                }
            }

            // image file length ...
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

            // image file list size
            System.arraycopy(byte_imagefile_list_length, 0, byte_message, offset, byte_imagefile_list_length.length);
            offset += byte_imagefile_list_length.length;
            System.arraycopy(byte_imagefile_list_size, 0, byte_message, offset, byte_imagefile_list_size.length);
            offset += byte_imagefile_list_size.length;

            // image file name list
            for(int k = 0; k < files.length; k++){

                System.arraycopy(byte_imagefile_name_length[k], 0, byte_message, offset, byte_imagefile_name_length[k].length);
                offset += byte_imagefile_name_length[k].length;
                System.arraycopy(byte_imagefile_name[k], 0, byte_message, offset, byte_imagefile_name[k].length);
                offset += byte_imagefile_name[k].length;

                // image file list
                System.arraycopy(byte_imagefile_length[k], 0, byte_message, offset, byte_imagefile_length[k].length);
                offset += byte_imagefile_length[k].length;
                System.arraycopy(byte_imagefile[k], 0, byte_message, offset, byte_imagefile[k].length);
                offset += byte_imagefile[k].length;

            }

            // send to headquater
            // session && hqkey 추가
            // log.info("{}", byte_message);
            headQuaterClientHandler.sendMessage(session, new BinaryMessage(byte_message));

            // System.arraycopy(payload, offset, adminIdBuf, 0, adminIdLength);
            // String adminId = new String(byte_message, Charset.forName(StandardCharsets.UTF_8.name()));

        } catch (IOException e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }



    }

    public synchronized void deployMetadataTabletLargeInchImageFileSend(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        String hqKey = parseResult.get("hqKey").toString();

        byte[] byte_amdin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_hqKey = null;
        byte[] byte_hqKey_length = null;
        byte[] byte_bin_type = null;
        byte[] byte_bin_type_length = null;
        byte[] byte_imagefile_list_size = null;
        byte[] byte_imagefile_list_length = null;
        byte[][] byte_imagefile_name = null;
        byte[][] byte_imagefile_name_length = null;
        byte[][] byte_imagefile = null;
        byte[][] byte_imagefile_length = null;
        byte[] byte_message = null;
        int message_length = 0;
        int offset = 0;


        File dir = new File(builderPath);
        File files[] = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String imageFileName = "";
                boolean imagefilenameYn = false;
                // TODO : ios file name 규칙에 맞춰서 file list flietr 기능 구현..
                if(name.contains("_APP_IPAD_PRO_3GEN_129_")){
                    log.info(name);
                    imageFileName = name;
                    imagefilenameYn = imageFileName.toLowerCase().endsWith(".png");
                }
                return imagefilenameYn;
            }
        });

        Arrays.sort(files); // TODO : sort arrays 기능 구현

        try {

            // user id
            byte_amdin_id = userId.getBytes(StandardCharsets.UTF_8);
            message_length += byte_amdin_id.length;

            // user id length
            byte_admin_id_length = convertIntToByteArray(byte_amdin_id.length);
            message_length += byte_admin_id_length.length;

            //hqKey
            byte_hqKey = hqKey.getBytes(StandardCharsets.UTF_8);
            message_length += byte_hqKey.length;

            //hqKey length
            byte_hqKey_length = convertIntToByteArray(byte_hqKey.length);
            message_length += byte_hqKey_length.length;

            // bin type TODO : file list filter 뿐만 아니라 bin msg type 기준도 분기 처리할 수 있게 구현해야함.
            byte_bin_type = BinaryServiceType.HV_BIN_DEPLOY_METADATA_TABLET_LARGE_IMAGE_READ.toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;

            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;

            byte_imagefile_list_size =  String.valueOf(files.length).getBytes(StandardCharsets.UTF_8);
            message_length += byte_imagefile_list_size.length;

            byte_imagefile_list_length = convertIntToByteArray(byte_imagefile_list_size.length);
            message_length += byte_imagefile_list_length.length;

            byte_imagefile_name = new byte[files.length][];
            byte_imagefile_name_length = new byte[files.length][];
            byte_imagefile = new byte[files.length][];
            byte_imagefile_length = new byte[files.length][];

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {

                } else {
                    // fileone = file;
                    log.info("file name {}, {}",i ,file.getName());
                    // image name
                    byte_imagefile_name[i] = file.getName().getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile_name[i].length;

                    // image name length
                    byte_imagefile_name_length[i] = convertIntToByteArray(byte_imagefile_name[i].length);
                    message_length += byte_imagefile_name_length[i] .length;

                    // image file to base64
                    byte_imagefile[i] = Base64.getEncoder().encodeToString(FileUtils.readFileToByteArray(file)).getBytes(StandardCharsets.UTF_8);
                    message_length += byte_imagefile[i].length;

                    // logfile length
                    byte_imagefile_length[i] = convertIntToByteArray(byte_imagefile[i].length);
                    message_length += byte_imagefile_length[i].length;

                }
            }

            // image file length ...
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

            // image file list size
            System.arraycopy(byte_imagefile_list_length, 0, byte_message, offset, byte_imagefile_list_length.length);
            offset += byte_imagefile_list_length.length;
            System.arraycopy(byte_imagefile_list_size, 0, byte_message, offset, byte_imagefile_list_size.length);
            offset += byte_imagefile_list_size.length;

            // image file name list
            for(int k = 0; k < files.length; k++){

                System.arraycopy(byte_imagefile_name_length[k], 0, byte_message, offset, byte_imagefile_name_length[k].length);
                offset += byte_imagefile_name_length[k].length;
                System.arraycopy(byte_imagefile_name[k], 0, byte_message, offset, byte_imagefile_name[k].length);
                offset += byte_imagefile_name[k].length;

                // image file list
                System.arraycopy(byte_imagefile_length[k], 0, byte_message, offset, byte_imagefile_length[k].length);
                offset += byte_imagefile_length[k].length;
                System.arraycopy(byte_imagefile[k], 0, byte_message, offset, byte_imagefile[k].length);
                offset += byte_imagefile[k].length;

            }

            // send to headquater
            // session && hqkey 추가
            // log.info("{}", byte_message);
            headQuaterClientHandler.sendMessage(session, new BinaryMessage(byte_message));

            // System.arraycopy(payload, offset, adminIdBuf, 0, adminIdLength);
            // String adminId = new String(byte_message, Charset.forName(StandardCharsets.UTF_8.name()));

        } catch (IOException e) {

            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }



    }


}
