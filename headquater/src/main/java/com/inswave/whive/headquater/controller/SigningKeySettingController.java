package com.inswave.whive.headquater.controller;

import com.inswave.whive.common.branchsetting.BranchSetting;
import com.inswave.whive.common.branchsetting.BranchSettingService;
import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import com.inswave.whive.common.enums.MessageString;
import com.inswave.whive.common.error.WHiveInvliadRequestException;
import com.inswave.whive.common.member.MemberDetail;
import com.inswave.whive.common.member.MemberLogin;
import com.inswave.whive.common.member.MemberService;
import com.inswave.whive.common.signingkeysetting.*;
import com.inswave.whive.headquater.SessionConstants;
import com.inswave.whive.headquater.enums.ClientMessageType;
import com.inswave.whive.headquater.enums.PayloadKeyType;
import com.inswave.whive.headquater.enums.SessionKeyContents;
import com.inswave.whive.headquater.enums.SessionType;
import com.inswave.whive.headquater.handler.WHiveIdentity;
import com.inswave.whive.headquater.handler.WHiveWebSocketHandler;
import com.inswave.whive.headquater.model.LoginSessionData;
import com.inswave.whive.headquater.service.ClusterWebSocketBuilderService;
import com.inswave.whive.headquater.util.BaseUtility;
import com.inswave.whive.headquater.util.ResponseUtility;
import com.inswave.whive.headquater.util.Utility;
import com.inswave.whive.headquater.util.common.Common;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class SigningKeySettingController extends BaseUtility {

    @Autowired
    SigningKeySettingService signingKeySettingService;

    @Autowired
    BranchSettingService branchSettingService;

    @Autowired
    MemberService memberService;

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Autowired
    Common common;

    @Autowired
    private ResponseUtility responseUtility;

    @Value("${whive.distribution.deployProvisionfilePath}")
    private String provisionFilePath;

    @Value("${whive.distribution.macPassword}")
    private String macPassword;

    WebSocketSession websocketSession;

    JSONObject entity = new JSONObject();
    JSONParser parser = new JSONParser();

    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/mCert/android/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> createKeySettingAndroidInsert(HttpServletRequest request, @RequestParam("file") MultipartFile file, @RequestParam("builder_id") String builder_id
            , @RequestParam("key_name") String key_name, @RequestParam("platform") String platform, @RequestParam("key_type") String key_type, @RequestParam("key_password") String key_password, @RequestParam("key_alias") String key_alias
            , @RequestParam("store_key_password") String store_key_password, @RequestParam(value = "deployFile", required = false) MultipartFile deployFile) throws IOException {

        BranchSetting branchSetting;
        String hqKey;
        String domain_id;
        String admin_id;
        String deployDataYn = "N";

        // platform 기준으로 분리해서 vo 설정 변경하기
        KeyAndroidSetting keyAndroidSetting = new KeyAndroidSetting();
        keyAndroidSetting.setKey_name(key_name);
        keyAndroidSetting.setAndroid_key_alias(key_alias); //
        keyAndroidSetting.setPlatform(platform);
        keyAndroidSetting.setAndroid_key_store_password(store_key_password); //
        keyAndroidSetting.setAndroid_key_password(key_password);
        keyAndroidSetting.setAndroid_key_type(key_type); //
        keyAndroidSetting.setAndroid_key_path("");
        keyAndroidSetting.setAndroid_deploy_key_path("");
        keyAndroidSetting.setBuilder_id(Integer.parseInt(builder_id));



        branchSetting = branchSettingService.findbyID(new Long(builder_id));

        /**
         *  TODO : build requeat quere 관리 기능 구현하기
         */
        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());

        // apiHandler.handleRequest(request);
        if(builderQueueManaged.getEtc_queue_cnt() > builderQueueManaged.getEtc_queue_status_cnt()){
            builderQueueManaged.setEtc_queue_status_cnt(builderQueueManaged.getEtc_queue_status_cnt() + 1);
            builderQueueManagedService.etcUpdate(builderQueueManaged);

        }else {

            // apiHandler.processQueue();
            entity.put(PayloadKeyType.error.name(), MessageString.BUILDER_QUEUE_IS_FULL.getMessage());
            return responseUtility.checkFailedResponse(entity);
        }


        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            // memberLogin = memberService.findByUserLoginID(memberLogin.getUser_login_id()); // superadmin 일경우 해당 service 단 처리 하지 않기
            hqKey = memberLogin.getUser_login_id();
            domain_id = memberLogin.getDomain_id();
            admin_id = String.valueOf(memberLogin.getUser_id());
        }else {
            return null;
        }

        if(file.isEmpty()){
            return responseUtility.checkFailedResponse();
        }

        if(file.getOriginalFilename() == null){
            return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
        }

        keyAndroidSetting.setAdmin_id(memberLogin.getUser_id());



        int message_length = 0;
        int offset = 0;
        byte[] byte_message = null;
        byte[] byte_domain_id = null;
        byte[] byte_domain_id_length = null;
        byte[] byte_admin_id = null;
        byte[] byte_admin_id_length = null;

        byte[] byte_deploy_keyfile_name = null;
        byte[] byte_deploy_keyfile_name_length = null;
        byte[] byte_deploy_keyfile = null;
        byte[] byte_deploy_keyfile_length = null;

        try{



        if( keyAndroidSetting.getKey_name() == null  || keyAndroidSetting.getKey_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        if(deployFile == null || deployFile.isEmpty()){
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        // 해당 구간 소스 코드 수정
        int signingkeyLastCount = signingKeySettingService.insertToAndroid(keyAndroidSetting);

        // user id
        byte[] byte_builder_id = branchSetting.getBuilder_user_id().getBytes(StandardCharsets.UTF_8); // send to branch id
        message_length += byte_builder_id.length;
        byte[] byte_builder_id_length = convertIntToByteArray(byte_builder_id.length);
        message_length += byte_builder_id_length.length;

        // bin type
        byte[] byte_bin_type = ClientMessageType.BIN_FILE_PROFILE_TEMPLATE_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
        message_length += byte_bin_type.length;

        // bin type length
        byte[] byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
        message_length += byte_bin_type_length.length;

        // hqKey
        byte[] byte_hq_key = hqKey.getBytes(StandardCharsets.UTF_8);
        message_length += byte_hq_key.length;

        // hqKey length
        byte[] byte_hq_key_length = convertIntToByteArray(byte_hq_key.length);
        message_length += byte_hq_key_length.length;

        // domain 다시 추가하기
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_domain_id = domain_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }else {
            byte_domain_id = memberLogin.getDomain_id().getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }

        // admin_id
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_admin_id = admin_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }else {
            byte_admin_id = memberLogin.getUser_id().toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }

        // platform
        byte[] byte_platform = platform.getBytes(StandardCharsets.UTF_8);
        message_length += byte_platform.length;

        byte[] byte_platform_length = convertIntToByteArray(byte_platform.length);
        message_length += byte_platform_length.length;

        // key_type
        byte[] byte_key_type = key_type.getBytes(StandardCharsets.UTF_8);
        message_length += byte_key_type.length;

        byte[] byte_key_type_length = convertIntToByteArray(byte_key_type.length);
        message_length += byte_key_type_length.length;

        // key_id
        byte[] byte_key_id = String.valueOf(signingkeyLastCount).getBytes(StandardCharsets.UTF_8);
        message_length += byte_key_id.length;

        byte[] byte_key_id_length = convertIntToByteArray(byte_key_id.length);
        message_length += byte_key_id_length.length;

        byte[] byte_keyfile_name = file.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
        message_length += byte_keyfile_name.length;

        // keystore file name length
        byte[] byte_keyfile_name_length = convertIntToByteArray(byte_keyfile_name.length);
        message_length += byte_keyfile_name_length.length;

        // keystore file
        byte[] byte_keyfile = file.getBytes();
        message_length += byte_keyfile.length;

        // keystore file length
        byte[] byte_keyfile_length = convertIntToByteArray(byte_keyfile.length);
        message_length += byte_keyfile_length.length;

        byte[] byte_deploy_yn = deployDataYn.getBytes(StandardCharsets.UTF_8);
        message_length += byte_deploy_yn.length;

        byte[] byte_deploy_yn_length = convertIntToByteArray(byte_deploy_yn.length);
        message_length += byte_deploy_yn_length.length;

        // android deploy 인증서 파일
        // null 여부 확인 하기



        // deploy yn 변수값 바이너리  값으로 세팅하기 1
        if(deployDataYn.equals("Y")){
            // deploy
            byte_deploy_keyfile_name = deployFile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_deploy_keyfile_name.length;

            // deploy file name length
            byte_deploy_keyfile_name_length = convertIntToByteArray(byte_deploy_keyfile_name.length);
            message_length += byte_deploy_keyfile_name_length.length;

            // deploy file
            byte_deploy_keyfile = deployFile.getBytes();
            message_length += byte_deploy_keyfile.length;

            // deploy file length
            byte_deploy_keyfile_length = convertIntToByteArray(byte_deploy_keyfile.length);
            message_length += byte_deploy_keyfile_length.length;

        }

        byte_message = new byte[message_length];

        System.arraycopy(byte_builder_id_length, 0, byte_message, offset, byte_builder_id_length.length);
        offset += byte_builder_id_length.length;
        System.arraycopy(byte_builder_id, 0, byte_message, offset, byte_builder_id.length);
        offset += byte_builder_id.length;

        System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
        offset += byte_bin_type_length.length;
        System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
        offset += byte_bin_type.length;

        // hqkey 추가
        System.arraycopy(byte_hq_key_length, 0, byte_message, offset, byte_hq_key_length.length);
        offset += byte_hq_key_length.length;
        System.arraycopy(byte_hq_key, 0, byte_message, offset, byte_hq_key.length);
        offset += byte_hq_key.length;

        // domain
        System.arraycopy(byte_domain_id_length, 0, byte_message, offset, byte_domain_id_length.length);
        offset += byte_domain_id_length.length;
        System.arraycopy(byte_domain_id, 0, byte_message, offset, byte_domain_id.length);
        offset += byte_domain_id.length;

        // admin
        System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
        offset += byte_admin_id_length.length;
        System.arraycopy(byte_admin_id, 0, byte_message, offset, byte_admin_id.length);
        offset += byte_admin_id.length;

        // platform
        System.arraycopy(byte_platform_length, 0, byte_message, offset, byte_platform_length.length);
        offset += byte_platform_length.length;
        System.arraycopy(byte_platform, 0, byte_message, offset, byte_platform.length);
        offset += byte_platform.length;

        //key_type
        System.arraycopy(byte_key_type_length, 0, byte_message, offset, byte_key_type_length.length);
        offset += byte_key_type_length.length;
        System.arraycopy(byte_key_type, 0, byte_message, offset, byte_key_type.length);
        offset += byte_key_type.length;

        // key_id
        System.arraycopy(byte_key_id_length, 0, byte_message, offset, byte_key_id_length.length);
        offset += byte_key_id_length.length;
        System.arraycopy(byte_key_id, 0, byte_message, offset, byte_key_id.length);
        offset += byte_key_id.length;

        System.arraycopy(byte_keyfile_name_length, 0, byte_message, offset, byte_keyfile_name_length.length);
        offset += byte_keyfile_name_length.length;
        System.arraycopy(byte_keyfile_name, 0, byte_message, offset, byte_keyfile_name.length);
        offset += byte_keyfile_name.length;

        System.arraycopy(byte_keyfile_length, 0, byte_message, offset, byte_keyfile_length.length);
        offset += byte_keyfile_length.length;
        System.arraycopy(byte_keyfile, 0, byte_message, offset, byte_keyfile.length);
        offset += byte_keyfile.length;

        // deploy yn 변수값 바이너리  값으로 세팅하기 2
        System.arraycopy(byte_deploy_yn_length, 0, byte_message, offset, byte_deploy_yn_length.length);
        offset += byte_deploy_yn_length.length;
        System.arraycopy(byte_deploy_yn, 0, byte_message, offset, byte_deploy_yn.length);
        offset += byte_deploy_yn.length;

        // deployfile
        if(deployDataYn.equals("Y")){
            System.arraycopy(byte_deploy_keyfile_name_length, 0, byte_message, offset, byte_deploy_keyfile_name_length.length);
            offset += byte_deploy_keyfile_name_length.length;
            System.arraycopy(byte_deploy_keyfile_name, 0, byte_message, offset, byte_deploy_keyfile_name.length);
            offset += byte_deploy_keyfile_name.length;

            System.arraycopy(byte_deploy_keyfile_length, 0, byte_message, offset, byte_deploy_keyfile_length.length);
            offset += byte_deploy_keyfile_length.length;
            System.arraycopy(byte_deploy_keyfile, 0, byte_message, offset, byte_deploy_keyfile.length);

        }

        // 해당 구간에 provisioning file, signingkey file 전송 기능 추가하기 ...
        // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.

        // BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendBinaryMessage(wHiveIdentity, byte_message);

//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new BinaryMessage(byte_message));
//                }
//            } catch (IOException ex) {
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//
//            }
//
//        }

        entity.put("signingkeyCnt",signingkeyLastCount);

        }catch (Exception e){
            log.warn(e.getMessage(),e);
        }finally {


        }


        return responseUtility.makeSuccessResponse(entity);
    }


    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/mCert/android/update", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> createKeySettingAndroidUpdate(HttpServletRequest request, @RequestParam(value = "file", required = false) MultipartFile file, @RequestParam("key_id") Integer key_id, @RequestParam("builder_id") String builder_id
            , @RequestParam("key_name") String key_name, @RequestParam("platform") String platform, @RequestParam("key_type") String key_type, @RequestParam("key_password") String key_password, @RequestParam("key_alias") String key_alias
            , @RequestParam("store_key_password") String store_key_password, @RequestParam(value = "deployFile", required = false) MultipartFile deployFile) throws IOException {

        BranchSetting branchSetting;
        MemberLogin memberLogin;
        String hqKey;
        String domain_id;
        String admin_id;
        String deployDataYn = "N";
        String buildDataYn = "N";

        // platform 기준으로 분리해서 vo 설정 변경하기
        KeyAndroidSetting keyAndroidSetting = new KeyAndroidSetting();
        keyAndroidSetting.setKey_id(key_id);
        keyAndroidSetting.setKey_name(key_name);
        keyAndroidSetting.setAndroid_key_alias(key_alias); //
        keyAndroidSetting.setPlatform(platform);
        keyAndroidSetting.setAndroid_key_store_password(store_key_password); //
        keyAndroidSetting.setAndroid_key_password(key_password);
        keyAndroidSetting.setAndroid_key_type(key_type); //
        keyAndroidSetting.setAndroid_key_path("");
        keyAndroidSetting.setAndroid_deploy_key_path("");
        keyAndroidSetting.setBuilder_id(Integer.parseInt(builder_id));



        branchSetting = branchSettingService.findbyID(new Long(builder_id));

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            // memberLogin = memberService.findByUserLoginID(loginData.getUserLoginId()); // superadmin 일경우 해당 service 단 처리 하지 않기
            hqKey = memberLogin.getUser_login_id();
            domain_id = memberLogin.getDomain_id();
            admin_id = String.valueOf(memberLogin.getUser_id());
        }else {
            return null;
        }

        if(file == null || file.isEmpty()){
            buildDataYn = "N";
        }else {
            buildDataYn = "Y";
        }



        int message_length = 0;
        int offset = 0;
        byte[] byte_message = null;
        byte[] byte_domain_id = null;
        byte[] byte_domain_id_length = null;
        byte[] byte_admin_id = null;
        byte[] byte_admin_id_length = null;

        byte[] byte_deploy_keyfile_name = null;
        byte[] byte_deploy_keyfile_name_length = null;
        byte[] byte_deploy_keyfile = null;
        byte[] byte_deploy_keyfile_length = null;

        if( keyAndroidSetting.getKey_name() == null  || keyAndroidSetting.getKey_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        if(deployFile == null || deployFile.isEmpty()){
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        // 해당 구간 소스 코드 수정 .. update 값 처리 기능으로 전환하기 ..  아니면 조회 하는 기능으로 전환하기 ...
        // int signingkeyLastCount = signingKeySettingService.insertToAndroid(keyAndroidSetting);

        if(deployDataYn.equals("N") && buildDataYn.equals("N")){
            keyAndroidSetting.setAdmin_id(Long.valueOf(admin_id));
            signingKeySettingService.udpateByTODetailAndroid(keyAndroidSetting);
            entity.put("signingkeyCnt",keyAndroidSetting.getKey_id());
            entity.put("signingKeyUpdate","Y");
            return responseUtility.makeSuccessResponse(entity);

        }else {

        }

        // user id
        byte[] byte_builder_id = branchSetting.getBuilder_user_id().getBytes(StandardCharsets.UTF_8); // send to branch id
        message_length += byte_builder_id.length;
        byte[] byte_builder_id_length = convertIntToByteArray(byte_builder_id.length);
        message_length += byte_builder_id_length.length;

        // bin type
        byte[] byte_bin_type = ClientMessageType.BIN_FILE_PROFILE_TEMPLATE_UPDATE_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
        message_length += byte_bin_type.length;

        // bin type length
        byte[] byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
        message_length += byte_bin_type_length.length;

        // hqKey
        byte[] byte_hq_key = hqKey.getBytes(StandardCharsets.UTF_8);
        message_length += byte_hq_key.length;

        // hqKey length
        byte[] byte_hq_key_length = convertIntToByteArray(byte_hq_key.length);
        message_length += byte_hq_key_length.length;

        // domain 다시 추가하기
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_domain_id = domain_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }else {
            byte_domain_id = memberLogin.getDomain_id().getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }

        // admin_id
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_admin_id = admin_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }else {
            byte_admin_id = memberLogin.getUser_id().toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }

        // platform
        byte[] byte_platform = platform.getBytes(StandardCharsets.UTF_8);
        message_length += byte_platform.length;

        byte[] byte_platform_length = convertIntToByteArray(byte_platform.length);
        message_length += byte_platform_length.length;

        // key_type
        byte[] byte_key_type = key_type.getBytes(StandardCharsets.UTF_8);
        message_length += byte_key_type.length;

        byte[] byte_key_type_length = convertIntToByteArray(byte_key_type.length);
        message_length += byte_key_type_length.length;

        // key_id
        byte[] byte_key_id = String.valueOf(keyAndroidSetting.getKey_id()).getBytes(StandardCharsets.UTF_8);
        message_length += byte_key_id.length;

        byte[] byte_key_id_length = convertIntToByteArray(byte_key_id.length);
        message_length += byte_key_id_length.length;

        byte[] byte_keyfile_name = file.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
        message_length += byte_keyfile_name.length;

        // keystore file name length
        byte[] byte_keyfile_name_length = convertIntToByteArray(byte_keyfile_name.length);
        message_length += byte_keyfile_name_length.length;

        // keystore file
        byte[] byte_keyfile = file.getBytes();
        message_length += byte_keyfile.length;

        // keystore file length
        byte[] byte_keyfile_length = convertIntToByteArray(byte_keyfile.length);
        message_length += byte_keyfile_length.length;

        byte[] byte_deploy_yn = deployDataYn.getBytes(StandardCharsets.UTF_8);
        message_length += byte_deploy_yn.length;

        byte[] byte_deploy_yn_length = convertIntToByteArray(byte_deploy_yn.length);
        message_length += byte_deploy_yn_length.length;

        // android deploy 인증서 파일
        // null 여부 확인 하기



        // deploy yn 변수값 바이너리  값으로 세팅하기 1
        if(deployDataYn.equals("Y")){
            // deploy
            byte_deploy_keyfile_name = deployFile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_deploy_keyfile_name.length;

            // deploy file name length
            byte_deploy_keyfile_name_length = convertIntToByteArray(byte_deploy_keyfile_name.length);
            message_length += byte_deploy_keyfile_name_length.length;

            // deploy file
            byte_deploy_keyfile = deployFile.getBytes();
            message_length += byte_deploy_keyfile.length;

            // deploy file length
            byte_deploy_keyfile_length = convertIntToByteArray(byte_deploy_keyfile.length);
            message_length += byte_deploy_keyfile_length.length;

        }else {

        }




        byte_message = new byte[message_length];

        System.arraycopy(byte_builder_id_length, 0, byte_message, offset, byte_builder_id_length.length);
        offset += byte_builder_id_length.length;
        System.arraycopy(byte_builder_id, 0, byte_message, offset, byte_builder_id.length);
        offset += byte_builder_id.length;

        System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
        offset += byte_bin_type_length.length;
        System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
        offset += byte_bin_type.length;

        // hqkey 추가
        System.arraycopy(byte_hq_key_length, 0, byte_message, offset, byte_hq_key_length.length);
        offset += byte_hq_key_length.length;
        System.arraycopy(byte_hq_key, 0, byte_message, offset, byte_hq_key.length);
        offset += byte_hq_key.length;

        // domain
        System.arraycopy(byte_domain_id_length, 0, byte_message, offset, byte_domain_id_length.length);
        offset += byte_domain_id_length.length;
        System.arraycopy(byte_domain_id, 0, byte_message, offset, byte_domain_id.length);
        offset += byte_domain_id.length;

        // admin
        System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
        offset += byte_admin_id_length.length;
        System.arraycopy(byte_admin_id, 0, byte_message, offset, byte_admin_id.length);
        offset += byte_admin_id.length;

        // platform
        System.arraycopy(byte_platform_length, 0, byte_message, offset, byte_platform_length.length);
        offset += byte_platform_length.length;
        System.arraycopy(byte_platform, 0, byte_message, offset, byte_platform.length);
        offset += byte_platform.length;

        //key_type
        System.arraycopy(byte_key_type_length, 0, byte_message, offset, byte_key_type_length.length);
        offset += byte_key_type_length.length;
        System.arraycopy(byte_key_type, 0, byte_message, offset, byte_key_type.length);
        offset += byte_key_type.length;

        // key_id
        System.arraycopy(byte_key_id_length, 0, byte_message, offset, byte_key_id_length.length);
        offset += byte_key_id_length.length;
        System.arraycopy(byte_key_id, 0, byte_message, offset, byte_key_id.length);
        offset += byte_key_id.length;

        System.arraycopy(byte_keyfile_name_length, 0, byte_message, offset, byte_keyfile_name_length.length);
        offset += byte_keyfile_name_length.length;
        System.arraycopy(byte_keyfile_name, 0, byte_message, offset, byte_keyfile_name.length);
        offset += byte_keyfile_name.length;

        System.arraycopy(byte_keyfile_length, 0, byte_message, offset, byte_keyfile_length.length);
        offset += byte_keyfile_length.length;
        System.arraycopy(byte_keyfile, 0, byte_message, offset, byte_keyfile.length);
        offset += byte_keyfile.length;

        // deploy yn 변수값 바이너리  값으로 세팅하기 2
        System.arraycopy(byte_deploy_yn_length, 0, byte_message, offset, byte_deploy_yn_length.length);
        offset += byte_deploy_yn_length.length;
        System.arraycopy(byte_deploy_yn, 0, byte_message, offset, byte_deploy_yn.length);
        offset += byte_deploy_yn.length;

        // deployfile
        if(deployDataYn.equals("Y")){
            System.arraycopy(byte_deploy_keyfile_name_length, 0, byte_message, offset, byte_deploy_keyfile_name_length.length);
            offset += byte_deploy_keyfile_name_length.length;
            System.arraycopy(byte_deploy_keyfile_name, 0, byte_message, offset, byte_deploy_keyfile_name.length);
            offset += byte_deploy_keyfile_name.length;

            System.arraycopy(byte_deploy_keyfile_length, 0, byte_message, offset, byte_deploy_keyfile_length.length);
            offset += byte_deploy_keyfile_length.length;
            System.arraycopy(byte_deploy_keyfile, 0, byte_message, offset, byte_deploy_keyfile.length);

        }else {

        }



        // 해당 구간에 provisioning file, signingkey file 전송 기능 추가하기 ...
        // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendBinaryMessage(wHiveIdentity, byte_message);

//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new BinaryMessage(byte_message));
//                }
//            } catch (IOException ex) {
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//
//            }
//
//        }

        entity.put("signingkeyCnt",keyAndroidSetting.getKey_id());
        entity.put("signingKeyUpdate","N");
        return responseUtility.makeSuccessResponse(entity);
    }

    @RequestMapping(value = "/manager/mCert/iOS/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> createKeySettingiOSInsert(HttpServletRequest request, @RequestParam("keyfile") MultipartFile keyfile, @RequestParam("debugprofile") MultipartFile debugprofile, @RequestParam("releaseprofile") MultipartFile releaseprofile, @RequestParam("builder_id") String builder_id
            , @RequestParam("key_name") String key_name, @RequestParam("platform") String platform, @RequestParam("key_type") String key_type, @RequestParam("key_password") String key_password
            , @RequestParam("release_type") String release_type, @RequestParam("unlock_keychain_password") String unlock_keychain_password) throws Exception {

        BranchSetting branchSetting;
        MemberLogin memberLogin;
        KeyiOSSetting keyiOSSetting = new KeyiOSSetting();
        String hqKey;
        String domain_id;
        String admin_id;
            // platform 기준으로 분리해서 vo 설정 변경하기
            // android 먼저 구현 하고 ios 진행하기

            keyiOSSetting.setBuilder_id(Integer.parseInt(builder_id));
            keyiOSSetting.setKey_name(key_name);
            keyiOSSetting.setPlatform(platform);
            keyiOSSetting.setIos_key_type(key_type); //
            keyiOSSetting.setIos_release_type(release_type);
            keyiOSSetting.setIos_key_password(key_password);
            keyiOSSetting.setIos_key_path("");
            keyiOSSetting.setIos_debug_profile_path("");
            keyiOSSetting.setIos_release_profile_path("");

            keyiOSSetting.setIos_unlock_keychain_password(aes256Encrypt(macPassword)); // unlock_keychain_password 값 저장 기능 >> 2022/03/08 macPassword 로 config 값으로 받아서 처리하기 로 함

            branchSetting = branchSettingService.findbyID(new Long(builder_id));

            // if member_role 조건 검색
            // memberLogin = memberService.findByEmailDetail(hqKey); // superadmin 일경우 해당 service 단 처리 하지 않기
            // memberLogin = memberService.findByUserLoginID(hqKey); // superadmin 일경우 해당 service 단 처리 하지 않기

            String userId = common.getTokenToRealName(request);
            memberLogin = memberService.findByUserLoginID(userId);

            if(memberLogin != null){

                // memberLogin = memberService.findByUserLoginID(memberLogin.getUserLoginId()); // superadmin 일경우 해당 service 단 처리 하지 않기
                hqKey = memberLogin.getUser_login_id();
                domain_id = memberLogin.getDomain_id();
                admin_id = String.valueOf(memberLogin.getUser_id());
            }else {
                return null;
            }

            if(keyfile.isEmpty()){
                return responseUtility.checkFailedResponse();
            }

            if(debugprofile.isEmpty()){
                return responseUtility.checkFailedResponse();
            }

            if(releaseprofile.isEmpty()){
                return responseUtility.checkFailedResponse();
            }

            if(keyfile.getOriginalFilename() == null){
                return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
            }

            if(debugprofile.getOriginalFilename() == null){
                return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
            }

            if(releaseprofile.getOriginalFilename() == null){
                return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
            }

            if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
                keyiOSSetting.setAdmin_id(Long.valueOf(admin_id));
            }else if (memberLogin.getUser_role().toLowerCase().equals("admin")){
                keyiOSSetting.setAdmin_id(memberLogin.getUser_id());
            }

            int message_length = 0;
            int offset = 0;
            byte[] byte_bin_type = null;
            byte[] byte_message = null;
            byte[] byte_domain_id = null;
            byte[] byte_domain_id_length = null;
            byte[] byte_admin_id = null;
            byte[] byte_admin_id_length = null;

            byte[] byte_debug_profile_name = null;
            byte[] byte_debug_profile_name_length = null;
            byte[] byte_debug_profile = null;
            byte[] byte_debug_profile_length = null;

            byte[] byte_release_profile_name = null;
            byte[] byte_release_profile_name_length = null;
            byte[] byte_release_profile = null;
            byte[] byte_release_profile_length = null;
            byte[] byte_bin_type_length = null;

            if( keyiOSSetting.getKey_name() == null  || keyiOSSetting.getKey_name().equals("") ) {
                throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
            }

            // 해당 구간 소스 코드 수정
            int signingkeyLastCount = signingKeySettingService.insertToiOS(keyiOSSetting);

            // user id
            byte[] byte_branch_id = branchSetting.getBuilder_id().toString().getBytes(StandardCharsets.UTF_8); // send to branch id
            message_length += byte_branch_id.length;
            byte[] byte_branch_id_length = convertIntToByteArray(byte_branch_id.length);
            message_length += byte_branch_id_length.length;

            // bin type
            // bin type , key type(build/dpeloy)에 따라 bin type 을 변경해서 처리해야함...
            if (key_type.toLowerCase().equals(PayloadKeyType.build.name())){
                byte_bin_type = ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
                message_length += byte_bin_type.length;
            }else if (key_type.toLowerCase().equals("deploy")){
                byte_bin_type = ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
                message_length += byte_bin_type.length;
            }

            if(byte_bin_type != null) {
                // bin type length
                byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
                message_length += byte_bin_type_length.length;
            }


            // hqKey
            byte[] byte_hq_key = hqKey.getBytes(StandardCharsets.UTF_8);
            message_length += byte_hq_key.length;

            // hqKey length
            byte[] byte_hq_key_length = convertIntToByteArray(byte_hq_key.length);
            message_length += byte_hq_key_length.length;

            // domain 다시 추가하기
            if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
                byte_domain_id = domain_id.getBytes(StandardCharsets.UTF_8);
                message_length += byte_domain_id.length;

                byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
                message_length += byte_domain_id_length.length;

            }else {
                byte_domain_id = memberLogin.getDomain_id().getBytes(StandardCharsets.UTF_8);
                message_length += byte_domain_id.length;

                byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
                message_length += byte_domain_id_length.length;

            }

            // admin_id
            if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
                byte_admin_id = admin_id.getBytes(StandardCharsets.UTF_8);
                message_length += byte_admin_id.length;

                byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
                message_length += byte_admin_id_length.length;

            }else {
                byte_admin_id = memberLogin.getUser_id().toString().getBytes(StandardCharsets.UTF_8);
                message_length += byte_admin_id.length;

                byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
                message_length += byte_admin_id_length.length;

            }

            // platform
            byte[] byte_platform = platform.getBytes(StandardCharsets.UTF_8);
            message_length += byte_platform.length;

            byte[] byte_platform_length = convertIntToByteArray(byte_platform.length);
            message_length += byte_platform_length.length;

            // signingkey_type
            byte[] byte_signingkey_type = key_type.getBytes(StandardCharsets.UTF_8);
            message_length += byte_signingkey_type.length;

            byte[] byte_signingkey_type_length = convertIntToByteArray(byte_signingkey_type.length);
            message_length += byte_signingkey_type_length.length;

            // signingkey_id
            byte[] byte_signingkey_id = String.valueOf(signingkeyLastCount).getBytes(StandardCharsets.UTF_8);
            message_length += byte_signingkey_id.length;

            byte[] byte_signingkey_id_length = convertIntToByteArray(byte_signingkey_id.length);
            message_length += byte_signingkey_id_length.length;

            byte[] byte_keyfile_name = keyfile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_keyfile_name.length;

            // key file name length
            byte[] byte_keyfile_name_length = convertIntToByteArray(byte_keyfile_name.length);
            message_length += byte_keyfile_name_length.length;


            byte[] byte_keyfile = keyfile.getBytes();
            message_length += byte_keyfile.length;
            log.info("byte_keyfile.length : {}", byte_keyfile.length);

            // key file length
            byte[] byte_keyfile_length = convertIntToByteArray(byte_keyfile.length);
            message_length += byte_keyfile_length.length;


            if (key_type.toLowerCase().equals(PayloadKeyType.build.name())){
                byte_debug_profile_name = debugprofile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
                message_length += byte_debug_profile_name.length;

                // debug profile name length
                byte_debug_profile_name_length = convertIntToByteArray(byte_debug_profile_name.length);
                message_length += byte_debug_profile_name_length.length;


                byte_debug_profile = debugprofile.getBytes();
                message_length += byte_debug_profile.length;

                // debug profile file length
                byte_debug_profile_length = convertIntToByteArray(byte_debug_profile.length);
                message_length += byte_debug_profile_length.length;

                byte_release_profile_name = releaseprofile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
                message_length += byte_release_profile_name.length;

                // release profile name length
                byte_release_profile_name_length = convertIntToByteArray(byte_release_profile_name.length);
                message_length += byte_release_profile_name_length.length;


                byte_release_profile = releaseprofile.getBytes();
                message_length += byte_release_profile.length;

                // release profile file length
                byte_release_profile_length = convertIntToByteArray(byte_release_profile.length);
                message_length += byte_release_profile_length.length;
            }

            byte_message = new byte[message_length];
            log.info("message length : {}", message_length);

            System.arraycopy(byte_branch_id_length, 0, byte_message, offset, byte_branch_id_length.length);
            offset += byte_branch_id_length.length;
            System.arraycopy(byte_branch_id, 0, byte_message, offset, byte_branch_id.length);
            offset += byte_branch_id.length;

            if(byte_bin_type_length != null){
                System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
                offset += byte_bin_type_length.length;
            }

            if(byte_bin_type != null){
                System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
                offset += byte_bin_type.length;
            }

            // hqkey
            System.arraycopy(byte_hq_key_length, 0, byte_message, offset, byte_hq_key_length.length);
            offset += byte_hq_key_length.length;
            System.arraycopy(byte_hq_key, 0, byte_message, offset, byte_hq_key.length);
            offset += byte_hq_key.length;

            // domain
            System.arraycopy(byte_domain_id_length, 0, byte_message, offset, byte_domain_id_length.length);
            offset += byte_domain_id_length.length;
            System.arraycopy(byte_domain_id, 0, byte_message, offset, byte_domain_id.length);
            offset += byte_domain_id.length;

            // admin
            System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
            offset += byte_admin_id_length.length;
            System.arraycopy(byte_admin_id, 0, byte_message, offset, byte_admin_id.length);
            offset += byte_admin_id.length;

            // platform
            System.arraycopy(byte_platform_length, 0, byte_message, offset, byte_platform_length.length);
            offset += byte_platform_length.length;
            System.arraycopy(byte_platform, 0, byte_message, offset, byte_platform.length);
            offset += byte_platform.length;

            //signingkey_type
            System.arraycopy(byte_signingkey_type_length, 0, byte_message, offset, byte_signingkey_type_length.length);
            offset += byte_signingkey_type_length.length;
            System.arraycopy(byte_signingkey_type, 0, byte_message, offset, byte_signingkey_type.length);
            offset += byte_signingkey_type.length;

            System.arraycopy(byte_signingkey_id_length, 0, byte_message, offset, byte_signingkey_id_length.length);
            offset += byte_signingkey_id_length.length;
            System.arraycopy(byte_signingkey_id, 0, byte_message, offset, byte_signingkey_id.length);
            offset += byte_signingkey_id.length;

            // signingkey
            System.arraycopy(byte_keyfile_name_length, 0, byte_message, offset, byte_keyfile_name_length.length);
            offset += byte_keyfile_name_length.length;
            System.arraycopy(byte_keyfile_name, 0, byte_message, offset, byte_keyfile_name.length);
            offset += byte_keyfile_name.length;

            System.arraycopy(byte_keyfile_length, 0, byte_message, offset, byte_keyfile_length.length);
            offset += byte_keyfile_length.length;
            System.arraycopy(byte_keyfile, 0, byte_message, offset, byte_keyfile.length);
            offset += byte_keyfile.length;

            if (key_type.toLowerCase().equals(PayloadKeyType.build.name())){
                // debug profile

                if(byte_debug_profile_name_length != null){
                    System.arraycopy(byte_debug_profile_name_length, 0, byte_message, offset, byte_debug_profile_name_length.length);
                    offset += byte_debug_profile_name_length.length;
                }

                if(byte_debug_profile_name != null){
                    System.arraycopy(byte_debug_profile_name, 0, byte_message, offset, byte_debug_profile_name.length);
                    offset += byte_debug_profile_name.length;
                }

                if(byte_debug_profile_length != null){
                    System.arraycopy(byte_debug_profile_length, 0, byte_message, offset, byte_debug_profile_length.length);
                    offset += byte_debug_profile_length.length;
                }

                System.arraycopy(byte_debug_profile, 0, byte_message, offset, byte_debug_profile.length);
                offset += byte_debug_profile.length;


                // release profile
                System.arraycopy(byte_release_profile_name_length, 0, byte_message, offset, byte_release_profile_name_length.length);
                offset += byte_release_profile_name_length.length;
                System.arraycopy(byte_release_profile_name, 0, byte_message, offset, byte_release_profile_name.length);
                offset += byte_release_profile_name.length;

                System.arraycopy(byte_release_profile_length, 0, byte_message, offset, byte_release_profile_length.length);
                offset += byte_release_profile_length.length;
                System.arraycopy(byte_release_profile, 0, byte_message, offset, byte_release_profile.length);
            }



            // 해당 구간에 provisioning file, signingkey file 전송 기능 추가하기 ...
            // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
            // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.

            BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
            WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
            // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

            ClusterWebSocketBuilderService.sendBinaryMessage(wHiveIdentity, byte_message);

//            if(websocketSession != null){
//                try {
//                    synchronized(websocketSession) {
//                        websocketSession.sendMessage(new BinaryMessage(byte_message));
//                    }
//                } catch (IOException ex) {
//                    log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//
//                }
//
//            }

            entity.put("signingkeyCnt",signingkeyLastCount);
            return responseUtility.makeSuccessResponse(entity);

    }

    @RequestMapping(value = "/manager/mCert/iOS/deploy/create", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> createKeySettingDeployiOSInsert(HttpServletRequest request, @RequestParam("keyfile") MultipartFile keyfile, @RequestParam("builder_id") String builder_id, @RequestParam("key_name") String key_name
            , @RequestParam("platform") String platform, @RequestParam("key_type") String key_type, @RequestParam("key_password") String key_password
            , @RequestParam("ios_issuer_id") String is_issuer_id, @RequestParam("ios_key_id") String ios_key_id) throws IOException {

        BranchSetting branchSetting;
        MemberLogin memberLogin;
        String hqKey;
        String domain_id;
        String admin_id;

        // platform 기준으로 분리해서 vo 설정 변경하기
        // android 먼저 구현 하고 ios 진행하기
        KeyiOSSetting keyiOSSetting = new KeyiOSSetting();
        keyiOSSetting.setBuilder_id(Integer.parseInt(builder_id));
        keyiOSSetting.setKey_name(key_name);
        keyiOSSetting.setPlatform(platform);
        keyiOSSetting.setIos_key_type(key_type); //
        keyiOSSetting.setIos_key_password(key_password);
        keyiOSSetting.setIos_issuer_id(is_issuer_id);
        keyiOSSetting.setIos_key_id(ios_key_id);
        keyiOSSetting.setIos_key_path("");
        keyiOSSetting.setIos_debug_profile_path("");
        keyiOSSetting.setIos_release_profile_path("");
        keyiOSSetting.setIos_release_type("");

        branchSetting = branchSettingService.findbyID(new Long(builder_id));

        // if member_role 조건 검색
        // memberLogin = memberService.findByEmailDetail(hqKey); // superadmin 일경우 해당 service 단 처리 하지 않기


        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            // memberLogin = memberService.findByUserLoginID(memberLogin.getUserLoginId()); // superadmin 일경우 해당 service 단 처리 하지 않기
            hqKey = memberLogin.getUser_login_id();
            domain_id = memberLogin.getDomain_id();
            admin_id = String.valueOf(memberLogin.getUser_id());
        }else {
            return null;
        }

        if(keyfile.isEmpty()){
            return responseUtility.checkFailedResponse();
        }

        if(keyfile.getOriginalFilename() == null){
            return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
        }


        keyiOSSetting.setAdmin_id(memberLogin.getUser_id());



        int message_length = 0;
        int offset = 0;
        byte[] byte_bin_type = null;
        byte[] byte_message = null;
        byte[] byte_domain_id = null;
        byte[] byte_domain_id_length = null;
        byte[] byte_admin_id = null;
        byte[] byte_admin_id_length = null;
        byte[] byte_bin_type_length = null;

        if( keyiOSSetting.getKey_name() == null  || keyiOSSetting.getKey_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }

        // 해당 구간 소스 코드 수정
        int signingkeyLastCount = signingKeySettingService.insertToiOS(keyiOSSetting);

        // user id
        byte[] byte_branch_id = branchSetting.getBuilder_id().toString().getBytes(StandardCharsets.UTF_8); // send to branch id
        message_length += byte_branch_id.length;
        byte[] byte_branch_id_length = convertIntToByteArray(byte_branch_id.length);
        message_length += byte_branch_id_length.length;

        // bin type
        // bin type , key type(build/dpeloy)에 따라 bin type 을 변경해서 처리해야함...
        if (key_type.toLowerCase().equals(PayloadKeyType.build.name())){
            byte_bin_type = ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;
        }else if (key_type.toLowerCase().equals("deploy")){
            byte_bin_type = ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
            message_length += byte_bin_type.length;
        }

        if(byte_bin_type != null){
            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;
        }


        // hqKey
        byte[] byte_hq_key = hqKey.getBytes(StandardCharsets.UTF_8);
        message_length += byte_hq_key.length;

        // hqKey length
        byte[] byte_hq_key_length = convertIntToByteArray(byte_hq_key.length);
        message_length += byte_hq_key_length.length;

        // domain 다시 추가하기
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_domain_id = domain_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }else {
            byte_domain_id = memberLogin.getDomain_id().getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }

        // admin_id
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_admin_id = admin_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }else {
            byte_admin_id = memberLogin.getUser_id().toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }

        // platform
        byte[] byte_platform = platform.getBytes(StandardCharsets.UTF_8);
        message_length += byte_platform.length;

        byte[] byte_platform_length = convertIntToByteArray(byte_platform.length);
        message_length += byte_platform_length.length;

        // signingkey_type
        byte[] byte_signingkey_type = key_type.getBytes(StandardCharsets.UTF_8);
        message_length += byte_signingkey_type.length;

        byte[] byte_signingkey_type_length = convertIntToByteArray(byte_signingkey_type.length);
        message_length += byte_signingkey_type_length.length;

        // signingkey_id
        byte[] byte_signingkey_id = String.valueOf(signingkeyLastCount).getBytes(StandardCharsets.UTF_8);
        message_length += byte_signingkey_id.length;

        byte[] byte_signingkey_id_length = convertIntToByteArray(byte_signingkey_id.length);
        message_length += byte_signingkey_id_length.length;

        byte[] byte_keyfile_name = keyfile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
        message_length += byte_keyfile_name.length;

        // key file name length
        byte[] byte_keyfile_name_length = convertIntToByteArray(byte_keyfile_name.length);
        message_length += byte_keyfile_name_length.length;


        byte[] byte_keyfile = keyfile.getBytes();
        message_length += byte_keyfile.length;
        log.info("byte_keyfile.length : {}", byte_keyfile.length);

        // key file length
        byte[] byte_keyfile_length = convertIntToByteArray(byte_keyfile.length);
        message_length += byte_keyfile_length.length;

        byte_message = new byte[message_length];

        System.arraycopy(byte_branch_id_length, 0, byte_message, offset, byte_branch_id_length.length);
        offset += byte_branch_id_length.length;
        System.arraycopy(byte_branch_id, 0, byte_message, offset, byte_branch_id.length);
        offset += byte_branch_id.length;

        if(byte_bin_type_length != null) {
            System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
            offset += byte_bin_type_length.length;

        }

        System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
        offset += byte_bin_type.length;

        // hqkey
        System.arraycopy(byte_hq_key_length, 0, byte_message, offset, byte_hq_key_length.length);
        offset += byte_hq_key_length.length;
        System.arraycopy(byte_hq_key, 0, byte_message, offset, byte_hq_key.length);
        offset += byte_hq_key.length;

        // domain
        System.arraycopy(byte_domain_id_length, 0, byte_message, offset, byte_domain_id_length.length);
        offset += byte_domain_id_length.length;
        System.arraycopy(byte_domain_id, 0, byte_message, offset, byte_domain_id.length);
        offset += byte_domain_id.length;

        // admin
        System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
        offset += byte_admin_id_length.length;
        System.arraycopy(byte_admin_id, 0, byte_message, offset, byte_admin_id.length);
        offset += byte_admin_id.length;

        // platform
        System.arraycopy(byte_platform_length, 0, byte_message, offset, byte_platform_length.length);
        offset += byte_platform_length.length;
        System.arraycopy(byte_platform, 0, byte_message, offset, byte_platform.length);
        offset += byte_platform.length;

        //signingkey_type
        System.arraycopy(byte_signingkey_type_length, 0, byte_message, offset, byte_signingkey_type_length.length);
        offset += byte_signingkey_type_length.length;
        System.arraycopy(byte_signingkey_type, 0, byte_message, offset, byte_signingkey_type.length);
        offset += byte_signingkey_type.length;

        System.arraycopy(byte_signingkey_id_length, 0, byte_message, offset, byte_signingkey_id_length.length);
        offset += byte_signingkey_id_length.length;
        System.arraycopy(byte_signingkey_id, 0, byte_message, offset, byte_signingkey_id.length);
        offset += byte_signingkey_id.length;

        // signingkey
        System.arraycopy(byte_keyfile_name_length, 0, byte_message, offset, byte_keyfile_name_length.length);
        offset += byte_keyfile_name_length.length;
        System.arraycopy(byte_keyfile_name, 0, byte_message, offset, byte_keyfile_name.length);
        offset += byte_keyfile_name.length;

        System.arraycopy(byte_keyfile_length, 0, byte_message, offset, byte_keyfile_length.length);
        offset += byte_keyfile_length.length;
        System.arraycopy(byte_keyfile, 0, byte_message, offset, byte_keyfile.length);
        offset += byte_keyfile.length;



        // 해당 구간에 provisioning file, signingkey file 전송 기능 추가하기 ...
        // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendBinaryMessage(wHiveIdentity, byte_message);

//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new BinaryMessage(byte_message));
//                }
//            } catch (IOException ex) {
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//
//            }
//
//        }

        entity.put("signingkeyCnt",signingkeyLastCount);
        return responseUtility.makeSuccessResponse(entity);
    }

    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/mCert/iOS/AllCreate", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> createAllKeySettingiOSInsert(HttpServletRequest request, @RequestParam("profiles") List<MultipartFile> profiles, @RequestParam("certificates") List<MultipartFile> certificates
            , @RequestParam(value = "deployFile", required = false) MultipartFile deployFile, @RequestParam("builder_id") String builder_id
            , @RequestParam("key_name") String key_name, @RequestParam("platform") String platform
            , @RequestParam("jsonProfiles[]") String jsonProfiles, @RequestParam("jsonCertificates[]") String jsonCertificates
            , @RequestParam("ios_issuer_id") String ios_issuer_id, @RequestParam("ios_key_id") String ios_key_id) throws Exception {

        BranchSetting branchSetting;
        MemberLogin memberLogin;
        KeyiOSSetting keyiOSSetting = new KeyiOSSetting();
        String hqKey;
        String domain_id;
        String admin_id;
        String deployDataYn = "N";
        // platform 기준으로 분리해서 vo 설정 변경하기
        // android 먼저 구현 하고 ios 진행하기

        keyiOSSetting.setBuilder_id(Integer.parseInt(builder_id));
        keyiOSSetting.setKey_name(key_name);
        keyiOSSetting.setPlatform(platform);
        keyiOSSetting.setIos_key_type("all");
        //keyiOSSetting.setIos_release_type(release_type);
        //keyiOSSetting.setIos_key_password(key_password);
        keyiOSSetting.setIos_key_path("");
        keyiOSSetting.setIos_debug_profile_path("");
        keyiOSSetting.setIos_release_profile_path("");

        // ios_issuer_id, ios_key_id 값이  null 일 경우 vo에 세팅 하지 않기
        // deploy yn 값 확인 하는 변수 값 세팅 하기
        if(ios_issuer_id.equals("") || ios_issuer_id == null ){
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        if(ios_key_id.equals("") || ios_key_id == null) {
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        if(deployFile == null || deployFile.isEmpty()){
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        keyiOSSetting.setIos_issuer_id(ios_issuer_id);
        keyiOSSetting.setIos_key_id(ios_key_id);
        keyiOSSetting.setIos_profiles_json(jsonProfiles);
        keyiOSSetting.setIos_certificates_json(jsonCertificates);

        keyiOSSetting.setIos_unlock_keychain_password(aes256Encrypt(macPassword)); // unlock_keychain_password 값 저장 기능 >> 2022/03/08 macPassword 로 config 값으로 받아서 처리하기 로 함

        branchSetting = branchSettingService.findbyID(new Long(builder_id));

        // if member_role 조건 검색
        // memberLogin = memberService.findByEmailDetail(hqKey); // superadmin 일경우 해당 service 단 처리 하지 않기
        // memberLogin = memberService.findByUserLoginID(hqKey); // superadmin 일경우 해당 service 단 처리 하지 않기

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            // memberLogin = memberService.findByUserLoginID(loginData.getUserLoginId()); // superadmin 일경우 해당 service 단 처리 하지 않기
            hqKey = memberLogin.getUser_login_id();
            domain_id = memberLogin.getDomain_id();
            admin_id = String.valueOf(memberLogin.getUser_id());
        }else {
            return null;
        }

//        if(keyfile.isEmpty()){
//            return responseUtility.checkFailedResponse();
//        }
//
//        if(debugprofile.isEmpty()){
//            return responseUtility.checkFailedResponse();
//        }
//
//        if(releaseprofile.isEmpty()){
//            return responseUtility.checkFailedResponse();
//        }
//
//        if(keyfile.getOriginalFilename() == null){
//            return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
//        }
//
//        if(debugprofile.getOriginalFilename() == null){
//            return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
//        }
//
//        if(releaseprofile.getOriginalFilename() == null){
//            return new ResponseEntity<>("Filename is not Found", HttpStatus.BAD_REQUEST);
//        }

        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            keyiOSSetting.setAdmin_id(Long.valueOf(admin_id));
        }else if (memberLogin.getUser_role().toLowerCase().equals("admin")){
            keyiOSSetting.setAdmin_id(memberLogin.getUser_id());
        }

        int message_length = 0;
        int offset = 0;
        int profiles_length = profiles.size();
        int certificate_length = certificates.size();

        byte[] byte_bin_type = null;
        byte[] byte_message = null;
        byte[] byte_domain_id = null;
        byte[] byte_domain_id_length = null;
        byte[] byte_admin_id = null;
        byte[] byte_admin_id_length = null;

        byte[][] byte_profiles_key_name = null;
        byte[][] byte_profiles_key_name_length = null;
        byte[][] byte_profiles_build_type = null;
        byte[][] byte_profiles_build_type_length = null;
        byte[][] byte_profiles_name = null;
        byte[][] byte_profiles_name_length = null;
        byte[][] byte_profiles = null;
        byte[][] byte_profiles_length = null;

        byte[][] byte_certificates_key_name = null;
        byte[][] byte_certificates_key_name_length = null;
        byte[][] byte_certificates_password = null;
        byte[][] byte_certificates_password_length = null;
        byte[][] byte_certificates_name = null;
        byte[][] byte_certificates_name_length = null;
        byte[][] byte_certificates = null;
        byte[][] byte_certificates_length = null;

        byte[] byte_deployfile_name = null;
        byte[] byte_deployfile_name_length = null;
        byte[] byte_deployfile = null;
        byte[] byte_deployfile_length = null;

        byte[] byte_bin_type_length = null;

        if( keyiOSSetting.getKey_name() == null  || keyiOSSetting.getKey_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        JSONObject testObect = new JSONObject();
        String profilesList = jsonProfiles;
        profilesList = profilesList.replace("[{","");
        profilesList = profilesList.replace("}]","");
        log.info(profilesList);

        String[] profileItems = profilesList.split("\\},\\{");
        log.info(String.valueOf(profileItems.length));

        String certificateList = jsonCertificates;
        certificateList = certificateList.replace("[{","");
        certificateList = certificateList.replace("}]","");

        String[] certificateItems = certificateList.split("\\},\\{");
        log.info(String.valueOf(certificateItems.length));



        log.info(jsonProfiles);
        log.info(jsonCertificates);


        // 해당 구간 소스 코드 수정
         int signingkeyLastCount = signingKeySettingService.insertToiOS(keyiOSSetting);


        // user id
        byte[] byte_branch_id = branchSetting.getBuilder_id().toString().getBytes(StandardCharsets.UTF_8); // send to branch id
        message_length += byte_branch_id.length;
        byte[] byte_branch_id_length = convertIntToByteArray(byte_branch_id.length);
        message_length += byte_branch_id_length.length;

        // bin type
        // bin type , key type(build/dpeloy)에 따라 bin type 을 변경해서 처리해야함...
        byte_bin_type = ClientMessageType.BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
        message_length += byte_bin_type.length;


        if(byte_bin_type != null) {
            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;
        }


        // hqKey
        byte[] byte_hq_key = hqKey.getBytes(StandardCharsets.UTF_8);
        message_length += byte_hq_key.length;

        // hqKey length
        byte[] byte_hq_key_length = convertIntToByteArray(byte_hq_key.length);
        message_length += byte_hq_key_length.length;

        // domain 다시 추가하기
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_domain_id = domain_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }else {
            byte_domain_id = memberLogin.getDomain_id().getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }

        // admin_id
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_admin_id = admin_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }else {
            byte_admin_id = memberLogin.getUser_id().toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }

        // platform
        byte[] byte_platform = platform.getBytes(StandardCharsets.UTF_8);
        message_length += byte_platform.length;

        byte[] byte_platform_length = convertIntToByteArray(byte_platform.length);
        message_length += byte_platform_length.length;

        // signingkey_type

        // signingkey_id
        byte[] byte_signingkey_id = String.valueOf(signingkeyLastCount).getBytes(StandardCharsets.UTF_8);
        message_length += byte_signingkey_id.length;

        byte[] byte_signingkey_id_length = convertIntToByteArray(byte_signingkey_id.length);
        message_length += byte_signingkey_id_length.length;

        // profile cnt
        byte[] byte_profile_cnt = String.valueOf(profiles_length).getBytes(StandardCharsets.UTF_8);
        message_length += byte_profile_cnt.length;

        byte[] byte_profile_cnt_length = convertIntToByteArray(byte_profile_cnt.length);
        message_length += byte_profile_cnt_length.length;

        // certificate cnt
        byte[] byte_certificate_cnt = String.valueOf(certificate_length).getBytes(StandardCharsets.UTF_8);
        message_length += byte_certificate_cnt.length;

        byte[] byte_certificate_cnt_length = convertIntToByteArray(byte_certificate_cnt.length);
        message_length += byte_certificate_cnt_length.length;

        byte[] byte_deploy_yn = deployDataYn.getBytes(StandardCharsets.UTF_8);
        message_length += byte_deploy_yn.length;

        byte[] byte_deploy_yn_length = convertIntToByteArray(byte_deploy_yn.length);
        message_length += byte_deploy_yn_length.length;

        // deploy yn 변수값 바이너리  값으로 세팅하기 1
        if(deployDataYn.equals("Y")){
            // deploy
            byte_deployfile_name = deployFile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_deployfile_name.length;

            byte_deployfile_name_length = convertIntToByteArray(byte_deployfile_name.length);
            message_length += byte_deployfile_name_length.length;

            byte_deployfile = deployFile.getBytes();
            message_length += byte_deployfile.length;

            byte_deployfile_length = convertIntToByteArray(byte_deployfile.length);
            message_length += byte_deployfile_length.length;
        }else {

        }


        // certificate
        byte_certificates_key_name = new byte[certificate_length][];
        byte_certificates_key_name_length = new byte[certificate_length][];
        byte_certificates_password = new byte[certificate_length][];
        byte_certificates_password_length = new byte[certificate_length][];
        byte_certificates_name = new byte[certificate_length][];
        byte_certificates_name_length = new byte[certificate_length][];
        byte_certificates = new byte[certificate_length][];
        byte_certificates_length = new byte[certificate_length][];

        for(int i = 0; i < certificate_length;i++){

            certificateItems[i] = "{"+certificateItems[i] +"}";
            JSONObject jsonCertificatesOne = (JSONObject)  parser.parse(certificateItems[i].toString());
            byte_certificates_key_name[i] = jsonCertificatesOne.get("certificate_name").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_certificates_key_name[i].length;

            byte_certificates_key_name_length[i] = convertIntToByteArray(byte_certificates_key_name[i].length);
            message_length += byte_certificates_key_name_length[i].length;

            byte_certificates_password[i] = jsonCertificatesOne.get("certificate_password").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_certificates_password[i].length;

            byte_certificates_password_length[i] = convertIntToByteArray(byte_certificates_password[i].length);
            message_length += byte_certificates_password_length[i].length;

            byte_certificates_name[i] = certificates.get(i).getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_certificates_name[i].length;

            byte_certificates_name_length[i] = convertIntToByteArray(byte_certificates_name[i].length);
            message_length += byte_certificates_name_length[i].length;

            MultipartFile file = certificates.get(i);
            byte_certificates[i] = file.getBytes();
            log.info(String.valueOf(byte_certificates[i].length));
            log.info(String.valueOf(file.getBytes()));
            message_length += byte_certificates[i].length;

            byte_certificates_length[i] = convertIntToByteArray(byte_certificates[i].length);
            message_length += byte_certificates_length[i].length;

        }

        //profile
        byte_profiles_key_name = new byte[profiles_length][];
        byte_profiles_key_name_length = new byte[profiles_length][];
        byte_profiles_build_type = new byte[profiles_length][];
        byte_profiles_build_type_length = new byte[profiles_length][];
        byte_profiles_name = new byte[profiles_length][];
        byte_profiles_name_length = new byte[profiles_length][];
        byte_profiles = new byte[profiles_length][];
        byte_profiles_length = new byte[profiles_length][];

        for(int j =0 ; j < profiles_length; j++){

            profileItems[j] = "{" + profileItems[j] + "}";
            JSONObject jsonProfils = (JSONObject) parser.parse(profileItems[j].toString());
            byte_profiles_key_name[j] = jsonProfils.get("profile_name").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_profiles_key_name[j].length;

            byte_profiles_key_name_length[j] = convertIntToByteArray(byte_profiles_key_name[j].length);
            message_length += byte_profiles_key_name_length[j].length;

            byte_profiles_build_type[j] = jsonProfils.get("profile_type").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_profiles_build_type[j].length;

            byte_profiles_build_type_length[j] = convertIntToByteArray(byte_profiles_build_type[j].length);
            message_length += byte_profiles_build_type_length[j].length;

            byte_profiles_name[j] = profiles.get(j).getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_profiles_name[j].length;

            byte_profiles_name_length[j] = convertIntToByteArray(byte_profiles_name[j].length);
            message_length += byte_profiles_name_length[j].length;

            byte_profiles[j] = profiles.get(j).getBytes();
            log.info(String.valueOf(byte_profiles[j].length));
            log.info(String.valueOf(profiles.get(j).getBytes()));
            message_length += byte_profiles[j].length;

            byte_profiles_length[j] = convertIntToByteArray(byte_profiles[j].length);
            message_length += byte_profiles_length[j].length;


        }

        byte_message = new byte[message_length];
        log.info("message length : {}", message_length);
        log.info("byte_message length : {}", byte_message.length);

        System.arraycopy(byte_branch_id_length, 0, byte_message, offset, byte_branch_id_length.length);
        offset += byte_branch_id_length.length;
        System.arraycopy(byte_branch_id, 0, byte_message, offset, byte_branch_id.length);
        offset += byte_branch_id.length;

        if(byte_bin_type_length != null){
            System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
            offset += byte_bin_type_length.length;
        }

        if(byte_bin_type != null){
            System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
            offset += byte_bin_type.length;
        }

        // hqkey
        System.arraycopy(byte_hq_key_length, 0, byte_message, offset, byte_hq_key_length.length);
        offset += byte_hq_key_length.length;
        System.arraycopy(byte_hq_key, 0, byte_message, offset, byte_hq_key.length);
        offset += byte_hq_key.length;

        // domain
        System.arraycopy(byte_domain_id_length, 0, byte_message, offset, byte_domain_id_length.length);
        offset += byte_domain_id_length.length;
        System.arraycopy(byte_domain_id, 0, byte_message, offset, byte_domain_id.length);
        offset += byte_domain_id.length;

        // admin
        System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
        offset += byte_admin_id_length.length;
        System.arraycopy(byte_admin_id, 0, byte_message, offset, byte_admin_id.length);
        offset += byte_admin_id.length;

        // platform
        System.arraycopy(byte_platform_length, 0, byte_message, offset, byte_platform_length.length);
        offset += byte_platform_length.length;
        System.arraycopy(byte_platform, 0, byte_message, offset, byte_platform.length);
        offset += byte_platform.length;

        //signingkey_id
        System.arraycopy(byte_signingkey_id_length, 0, byte_message, offset, byte_signingkey_id_length.length);
        offset += byte_signingkey_id_length.length;
        System.arraycopy(byte_signingkey_id, 0, byte_message, offset, byte_signingkey_id.length);
        offset += byte_signingkey_id.length;

        // profile cnt
        System.arraycopy(byte_profile_cnt_length, 0, byte_message, offset, byte_profile_cnt_length.length);
        offset += byte_profile_cnt_length.length;
        System.arraycopy(byte_profile_cnt, 0, byte_message, offset, byte_profile_cnt.length);
        offset += byte_profile_cnt.length;

        // certificate cnt
        System.arraycopy(byte_certificate_cnt_length, 0, byte_message, offset, byte_certificate_cnt_length.length);
        offset += byte_certificate_cnt_length.length;
        System.arraycopy(byte_certificate_cnt, 0, byte_message, offset, byte_certificate_cnt.length);
        offset += byte_certificate_cnt.length;

        // deploy yn 변수값 바이너리  값으로 세팅하기 2
        System.arraycopy(byte_deploy_yn_length, 0, byte_message, offset, byte_deploy_yn_length.length);
        offset += byte_deploy_yn_length.length;
        System.arraycopy(byte_deploy_yn, 0, byte_message, offset, byte_deploy_yn.length);
        offset += byte_deploy_yn.length;

        // deployfile
        if(deployDataYn.equals("Y")){
            System.arraycopy(byte_deployfile_name_length, 0, byte_message, offset, byte_deployfile_name_length.length);
            offset += byte_deployfile_name_length.length;
            System.arraycopy(byte_deployfile_name, 0, byte_message, offset, byte_deployfile_name.length);
            offset += byte_deployfile_name.length;

            System.arraycopy(byte_deployfile_length, 0, byte_message, offset, byte_deployfile_length.length);
            offset += byte_deployfile_length.length;
            System.arraycopy(byte_deployfile, 0, byte_message, offset, byte_deployfile.length);
            offset += byte_deployfile.length;

        }else {

        }


        // certificate
        for(int k = 0; k < certificate_length; k++){

            System.arraycopy(byte_certificates_key_name_length[k], 0, byte_message, offset, byte_certificates_key_name_length[k].length);
            offset += byte_certificates_key_name_length[k].length;

            System.arraycopy(byte_certificates_key_name[k], 0, byte_message, offset, byte_certificates_key_name[k].length);
            offset += byte_certificates_key_name[k].length;

            System.arraycopy(byte_certificates_password_length[k], 0, byte_message, offset, byte_certificates_password_length[k].length);
            offset += byte_certificates_password_length[k].length;

            System.arraycopy(byte_certificates_password[k], 0, byte_message, offset, byte_certificates_password[k].length);
            offset += byte_certificates_password[k].length;

            System.arraycopy(byte_certificates_name_length[k], 0, byte_message, offset, byte_certificates_name_length[k].length);
            offset += byte_certificates_name_length[k].length;

            System.arraycopy(byte_certificates_name[k], 0, byte_message, offset, byte_certificates_name[k].length);
            offset += byte_certificates_name[k].length;

            System.arraycopy(byte_certificates_length[k],0,byte_message, offset, byte_certificates_length[k].length);
            offset += byte_certificates_length[k].length;

            System.arraycopy(byte_certificates[k], 0, byte_message, offset, byte_certificates[k].length);
            offset += byte_certificates[k].length;

        }

        // profile
        for(int h = 0; h < profiles_length; h++){

            System.arraycopy(byte_profiles_key_name_length[h], 0, byte_message, offset, byte_profiles_key_name_length[h].length);
            offset += byte_profiles_key_name_length[h].length;

            System.arraycopy(byte_profiles_key_name[h], 0, byte_message, offset, byte_profiles_key_name[h].length);
            offset += byte_profiles_key_name[h].length;

            System.arraycopy(byte_profiles_build_type_length[h], 0, byte_message, offset, byte_profiles_build_type_length[h].length);
            offset += byte_profiles_build_type_length[h].length;

            System.arraycopy(byte_profiles_build_type[h], 0, byte_message, offset, byte_profiles_build_type[h].length);
            offset += byte_profiles_build_type[h].length;

            System.arraycopy(byte_profiles_name_length[h], 0, byte_message, offset, byte_profiles_name_length[h].length);
            offset += byte_profiles_name_length[h].length;

            System.arraycopy(byte_profiles_name[h], 0, byte_message, offset, byte_profiles_name[h].length);
            offset += byte_profiles_name[h].length;

            System.arraycopy(byte_profiles_length[h],0,byte_message, offset, byte_profiles_length[h].length);
            offset += byte_profiles_length[h].length;

            System.arraycopy(byte_profiles[h], 0, byte_message, offset, byte_profiles[h].length);
            offset += byte_profiles[h].length;


        }
        log.info(String.valueOf(offset));



        // 해당 구간에 provisioning file, signingkey file 전송 기능 추가하기 ...
        // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendBinaryMessage(wHiveIdentity, byte_message);

//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new BinaryMessage(byte_message));
//                }
//            } catch (IOException ex) {
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//
//            }
//
//        }

        entity.put("signingkeyCnt",signingkeyLastCount);
        return responseUtility.makeSuccessResponse(entity);

    }

    // TODO: builder 큐 관리 기능 추가
    @RequestMapping(value = "/manager/mCert/iOS/AllUpdate", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> createAllKeySettingiOSUpdate(HttpServletRequest request, @RequestParam("profiles") List<MultipartFile> profiles, @RequestParam("certificates") List<MultipartFile> certificates
            , @RequestParam(value = "deployFile", required = false) MultipartFile deployFile, @RequestParam("builder_id") String builder_id, @RequestParam("key_id") Integer key_id
            , @RequestParam("key_name") String key_name, @RequestParam("platform") String platform
            , @RequestParam("jsonProfiles[]") String jsonProfiles, @RequestParam("jsonCertificates[]") String jsonCertificates
            , @RequestParam("ios_issuer_id") String ios_issuer_id, @RequestParam("ios_key_id") String ios_key_id) throws Exception {

        BranchSetting branchSetting;
        MemberLogin memberLogin;
        KeyiOSSetting keyiOSSetting = new KeyiOSSetting();
        String hqKey;
        String domain_id;
        String admin_id;
        String deployDataYn = "N";
        // platform 기준으로 분리해서 vo 설정 변경하기
        // android 먼저 구현 하고 ios 진행하기

        keyiOSSetting.setKey_id(key_id);
        keyiOSSetting.setBuilder_id(Integer.parseInt(builder_id));
        keyiOSSetting.setKey_name(key_name);
        keyiOSSetting.setPlatform(platform);
        keyiOSSetting.setIos_key_type("build");
        //keyiOSSetting.setIos_release_type(release_type);
        //keyiOSSetting.setIos_key_password(key_password);
        keyiOSSetting.setIos_key_path("");
        keyiOSSetting.setIos_debug_profile_path("");
        keyiOSSetting.setIos_release_profile_path("");

        // ios_issuer_id, ios_key_id 값이  null 일 경우 vo에 세팅 하지 않기
        // deploy yn 값 확인 하는 변수 값 세팅 하기
        if(ios_issuer_id.equals("") || ios_issuer_id == null ){
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        if(ios_key_id.equals("") || ios_key_id == null) {
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        if(deployFile == null || deployFile.isEmpty()){
            deployDataYn = "N";
        }else {
            deployDataYn = "Y";
        }

        keyiOSSetting.setIos_issuer_id(ios_issuer_id);
        keyiOSSetting.setIos_key_id(ios_key_id);
        keyiOSSetting.setIos_profiles_json(jsonProfiles);
        keyiOSSetting.setIos_certificates_json(jsonCertificates);

        keyiOSSetting.setIos_unlock_keychain_password(aes256Encrypt(macPassword)); // unlock_keychain_password 값 저장 기능 >> 2022/03/08 macPassword 로 config 값으로 받아서 처리하기 로 함

        branchSetting = branchSettingService.findbyID(new Long(builder_id));

        // if member_role 조건 검색
        // memberLogin = memberService.findByEmailDetail(hqKey); // superadmin 일경우 해당 service 단 처리 하지 않기
        // memberLogin = memberService.findByUserLoginID(hqKey); // superadmin 일경우 해당 service 단 처리 하지 않기

        String userId = common.getTokenToRealName(request);
        memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            // memberLogin = memberService.findByUserLoginID(loginData.getUserLoginId()); // superadmin 일경우 해당 service 단 처리 하지 않기
            hqKey = memberLogin.getUser_login_id();
            domain_id = memberLogin.getDomain_id();
            admin_id = String.valueOf(memberLogin.getUser_id());
        }else {
            return null;
        }


        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            keyiOSSetting.setAdmin_id(Long.valueOf(admin_id));
        }else if (memberLogin.getUser_role().toLowerCase().equals("admin")){
            keyiOSSetting.setAdmin_id(memberLogin.getUser_id());
        }

        int message_length = 0;
        int offset = 0;
        int profiles_length = profiles.size();
        int certificate_length = certificates.size();

        byte[] byte_bin_type = null;
        byte[] byte_message = null;
        byte[] byte_domain_id = null;
        byte[] byte_domain_id_length = null;
        byte[] byte_admin_id = null;
        byte[] byte_admin_id_length = null;

        byte[][] byte_profiles_key_name = null;
        byte[][] byte_profiles_key_name_length = null;
        byte[][] byte_profiles_build_type = null;
        byte[][] byte_profiles_build_type_length = null;
        byte[][] byte_profiles_name = null;
        byte[][] byte_profiles_name_length = null;
        byte[][] byte_profiles = null;
        byte[][] byte_profiles_length = null;

        byte[][] byte_certificates_key_name = null;
        byte[][] byte_certificates_key_name_length = null;
        byte[][] byte_certificates_password = null;
        byte[][] byte_certificates_password_length = null;
        byte[][] byte_certificates_name = null;
        byte[][] byte_certificates_name_length = null;
        byte[][] byte_certificates = null;
        byte[][] byte_certificates_length = null;

        byte[] byte_deployfile_name = null;
        byte[] byte_deployfile_name_length = null;
        byte[] byte_deployfile = null;
        byte[] byte_deployfile_length = null;

        byte[] byte_bin_type_length = null;

        if( keyiOSSetting.getKey_name() == null  || keyiOSSetting.getKey_name().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        JSONObject testObect = new JSONObject();
        String profilesList = jsonProfiles;
        profilesList = profilesList.replace("[{","");
        profilesList = profilesList.replace("}]","");

        String[] profileItems = profilesList.split("\\},\\{");

        String certificateList = jsonCertificates;
        certificateList = certificateList.replace("[{","");
        certificateList = certificateList.replace("}]","");

        String[] certificateItems = certificateList.split("\\},\\{");


        // 해당 구간 소스 코드 수정 insert >> update dao로 수정
        signingKeySettingService.updateByToDetailiOS(keyiOSSetting);


        // user id
        byte[] byte_branch_id = branchSetting.getBuilder_id().toString().getBytes(StandardCharsets.UTF_8); // send to branch id
        message_length += byte_branch_id.length;
        byte[] byte_branch_id_length = convertIntToByteArray(byte_branch_id.length);
        message_length += byte_branch_id_length.length;

        // bin type
        // bin type , key type(build/dpeloy)에 따라 bin type 을 변경해서 처리해야함...
        byte_bin_type = ClientMessageType.BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO.name().getBytes(StandardCharsets.UTF_8);
        message_length += byte_bin_type.length;


        if(byte_bin_type != null) {
            // bin type length
            byte_bin_type_length = convertIntToByteArray(byte_bin_type.length);
            message_length += byte_bin_type_length.length;
        }


        // hqKey
        byte[] byte_hq_key = hqKey.getBytes(StandardCharsets.UTF_8);
        message_length += byte_hq_key.length;

        // hqKey length
        byte[] byte_hq_key_length = convertIntToByteArray(byte_hq_key.length);
        message_length += byte_hq_key_length.length;

        // domain 다시 추가하기
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_domain_id = domain_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }else {
            byte_domain_id = memberLogin.getDomain_id().getBytes(StandardCharsets.UTF_8);
            message_length += byte_domain_id.length;

            byte_domain_id_length = convertIntToByteArray(byte_domain_id.length);
            message_length += byte_domain_id_length.length;

        }

        // admin_id
        if(memberLogin.getUser_role().toLowerCase().equals(PayloadKeyType.superadmin.name())){
            byte_admin_id = admin_id.getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }else {
            byte_admin_id = memberLogin.getUser_id().toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_admin_id.length;

            byte_admin_id_length = convertIntToByteArray(byte_admin_id.length);
            message_length += byte_admin_id_length.length;

        }

        // platform
        byte[] byte_platform = platform.getBytes(StandardCharsets.UTF_8);
        message_length += byte_platform.length;

        byte[] byte_platform_length = convertIntToByteArray(byte_platform.length);
        message_length += byte_platform_length.length;

        // signingkey_type

        // signingkey_id
        byte[] byte_signingkey_id = String.valueOf(key_id).getBytes(StandardCharsets.UTF_8);
        message_length += byte_signingkey_id.length;

        byte[] byte_signingkey_id_length = convertIntToByteArray(byte_signingkey_id.length);
        message_length += byte_signingkey_id_length.length;

        // profile cnt
        byte[] byte_profile_cnt = String.valueOf(profiles_length).getBytes(StandardCharsets.UTF_8);
        message_length += byte_profile_cnt.length;

        byte[] byte_profile_cnt_length = convertIntToByteArray(byte_profile_cnt.length);
        message_length += byte_profile_cnt_length.length;

        // certificate cnt
        byte[] byte_certificate_cnt = String.valueOf(certificate_length).getBytes(StandardCharsets.UTF_8);
        message_length += byte_certificate_cnt.length;

        byte[] byte_certificate_cnt_length = convertIntToByteArray(byte_certificate_cnt.length);
        message_length += byte_certificate_cnt_length.length;

        byte[] byte_deploy_yn = deployDataYn.getBytes(StandardCharsets.UTF_8);
        message_length += byte_deploy_yn.length;

        byte[] byte_deploy_yn_length = convertIntToByteArray(byte_deploy_yn.length);
        message_length += byte_deploy_yn_length.length;

        // deploy yn 변수값 바이너리  값으로 세팅하기 1
        if(deployDataYn.equals("Y")){
            // deploy
            byte_deployfile_name = deployFile.getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_deployfile_name.length;

            byte_deployfile_name_length = convertIntToByteArray(byte_deployfile_name.length);
            message_length += byte_deployfile_name_length.length;

            byte_deployfile = deployFile.getBytes();
            message_length += byte_deployfile.length;

            byte_deployfile_length = convertIntToByteArray(byte_deployfile.length);
            message_length += byte_deployfile_length.length;
        }else {

        }


        // certificate
        byte_certificates_key_name = new byte[certificate_length][];
        byte_certificates_key_name_length = new byte[certificate_length][];
        byte_certificates_password = new byte[certificate_length][];
        byte_certificates_password_length = new byte[certificate_length][];
        byte_certificates_name = new byte[certificate_length][];
        byte_certificates_name_length = new byte[certificate_length][];
        byte_certificates = new byte[certificate_length][];
        byte_certificates_length = new byte[certificate_length][];

        for(int i = 0; i < certificate_length;i++){

            certificateItems[i] = "{"+certificateItems[i] +"}";
            JSONObject jsonCertificatesOne = (JSONObject)  parser.parse(certificateItems[i].toString());
            byte_certificates_key_name[i] = jsonCertificatesOne.get("certificate_name").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_certificates_key_name[i].length;

            byte_certificates_key_name_length[i] = convertIntToByteArray(byte_certificates_key_name[i].length);
            message_length += byte_certificates_key_name_length[i].length;

            byte_certificates_password[i] = jsonCertificatesOne.get("certificate_password").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_certificates_password[i].length;

            byte_certificates_password_length[i] = convertIntToByteArray(byte_certificates_password[i].length);
            message_length += byte_certificates_password_length[i].length;

            byte_certificates_name[i] = certificates.get(i).getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_certificates_name[i].length;

            byte_certificates_name_length[i] = convertIntToByteArray(byte_certificates_name[i].length);
            message_length += byte_certificates_name_length[i].length;

            MultipartFile file = certificates.get(i);
            byte_certificates[i] = file.getBytes();
            log.info(String.valueOf(byte_certificates[i].length));
            log.info(String.valueOf(file.getBytes()));
            message_length += byte_certificates[i].length;

            byte_certificates_length[i] = convertIntToByteArray(byte_certificates[i].length);
            message_length += byte_certificates_length[i].length;

        }

        //profile
        byte_profiles_key_name = new byte[profiles_length][];
        byte_profiles_key_name_length = new byte[profiles_length][];
        byte_profiles_build_type = new byte[profiles_length][];
        byte_profiles_build_type_length = new byte[profiles_length][];
        byte_profiles_name = new byte[profiles_length][];
        byte_profiles_name_length = new byte[profiles_length][];
        byte_profiles = new byte[profiles_length][];
        byte_profiles_length = new byte[profiles_length][];

        for(int j =0 ; j < profiles_length; j++){

            profileItems[j] = "{" + profileItems[j] + "}";
            JSONObject jsonProfils = (JSONObject) parser.parse(profileItems[j].toString());
            byte_profiles_key_name[j] = jsonProfils.get("profile_name").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_profiles_key_name[j].length;

            byte_profiles_key_name_length[j] = convertIntToByteArray(byte_profiles_key_name[j].length);
            message_length += byte_profiles_key_name_length[j].length;

            byte_profiles_build_type[j] = jsonProfils.get("profile_type").toString().getBytes(StandardCharsets.UTF_8);
            message_length += byte_profiles_build_type[j].length;

            byte_profiles_build_type_length[j] = convertIntToByteArray(byte_profiles_build_type[j].length);
            message_length += byte_profiles_build_type_length[j].length;

            byte_profiles_name[j] = profiles.get(j).getOriginalFilename().getBytes(StandardCharsets.ISO_8859_1);
            message_length += byte_profiles_name[j].length;

            byte_profiles_name_length[j] = convertIntToByteArray(byte_profiles_name[j].length);
            message_length += byte_profiles_name_length[j].length;

            byte_profiles[j] = profiles.get(j).getBytes();
            log.info(String.valueOf(byte_profiles[j].length));
            log.info(String.valueOf(profiles.get(j).getBytes()));
            message_length += byte_profiles[j].length;

            byte_profiles_length[j] = convertIntToByteArray(byte_profiles[j].length);
            message_length += byte_profiles_length[j].length;


        }

        byte_message = new byte[message_length];

        System.arraycopy(byte_branch_id_length, 0, byte_message, offset, byte_branch_id_length.length);
        offset += byte_branch_id_length.length;
        System.arraycopy(byte_branch_id, 0, byte_message, offset, byte_branch_id.length);
        offset += byte_branch_id.length;

        if(byte_bin_type_length != null){
            System.arraycopy(byte_bin_type_length, 0, byte_message, offset, byte_bin_type_length.length);
            offset += byte_bin_type_length.length;
        }

        if(byte_bin_type != null){
            System.arraycopy(byte_bin_type, 0, byte_message, offset, byte_bin_type.length);
            offset += byte_bin_type.length;
        }

        // hqkey
        System.arraycopy(byte_hq_key_length, 0, byte_message, offset, byte_hq_key_length.length);
        offset += byte_hq_key_length.length;
        System.arraycopy(byte_hq_key, 0, byte_message, offset, byte_hq_key.length);
        offset += byte_hq_key.length;

        // domain
        System.arraycopy(byte_domain_id_length, 0, byte_message, offset, byte_domain_id_length.length);
        offset += byte_domain_id_length.length;
        System.arraycopy(byte_domain_id, 0, byte_message, offset, byte_domain_id.length);
        offset += byte_domain_id.length;

        // admin
        System.arraycopy(byte_admin_id_length, 0, byte_message, offset, byte_admin_id_length.length);
        offset += byte_admin_id_length.length;
        System.arraycopy(byte_admin_id, 0, byte_message, offset, byte_admin_id.length);
        offset += byte_admin_id.length;

        // platform
        System.arraycopy(byte_platform_length, 0, byte_message, offset, byte_platform_length.length);
        offset += byte_platform_length.length;
        System.arraycopy(byte_platform, 0, byte_message, offset, byte_platform.length);
        offset += byte_platform.length;

        //signingkey_id
        System.arraycopy(byte_signingkey_id_length, 0, byte_message, offset, byte_signingkey_id_length.length);
        offset += byte_signingkey_id_length.length;
        System.arraycopy(byte_signingkey_id, 0, byte_message, offset, byte_signingkey_id.length);
        offset += byte_signingkey_id.length;

        // profile cnt
        System.arraycopy(byte_profile_cnt_length, 0, byte_message, offset, byte_profile_cnt_length.length);
        offset += byte_profile_cnt_length.length;
        System.arraycopy(byte_profile_cnt, 0, byte_message, offset, byte_profile_cnt.length);
        offset += byte_profile_cnt.length;

        // certificate cnt
        System.arraycopy(byte_certificate_cnt_length, 0, byte_message, offset, byte_certificate_cnt_length.length);
        offset += byte_certificate_cnt_length.length;
        System.arraycopy(byte_certificate_cnt, 0, byte_message, offset, byte_certificate_cnt.length);
        offset += byte_certificate_cnt.length;

        // deploy yn 변수값 바이너리  값으로 세팅하기 2
        System.arraycopy(byte_deploy_yn_length, 0, byte_message, offset, byte_deploy_yn_length.length);
        offset += byte_deploy_yn_length.length;
        System.arraycopy(byte_deploy_yn, 0, byte_message, offset, byte_deploy_yn.length);
        offset += byte_deploy_yn.length;

        // deployfile
        if(deployDataYn.equals("Y")){
            System.arraycopy(byte_deployfile_name_length, 0, byte_message, offset, byte_deployfile_name_length.length);
            offset += byte_deployfile_name_length.length;
            System.arraycopy(byte_deployfile_name, 0, byte_message, offset, byte_deployfile_name.length);
            offset += byte_deployfile_name.length;

            System.arraycopy(byte_deployfile_length, 0, byte_message, offset, byte_deployfile_length.length);
            offset += byte_deployfile_length.length;
            System.arraycopy(byte_deployfile, 0, byte_message, offset, byte_deployfile.length);
            offset += byte_deployfile.length;

        }else {

        }


        // certificate
        for(int k = 0; k < certificate_length; k++){

            System.arraycopy(byte_certificates_key_name_length[k], 0, byte_message, offset, byte_certificates_key_name_length[k].length);
            offset += byte_certificates_key_name_length[k].length;

            System.arraycopy(byte_certificates_key_name[k], 0, byte_message, offset, byte_certificates_key_name[k].length);
            offset += byte_certificates_key_name[k].length;

            System.arraycopy(byte_certificates_password_length[k], 0, byte_message, offset, byte_certificates_password_length[k].length);
            offset += byte_certificates_password_length[k].length;

            System.arraycopy(byte_certificates_password[k], 0, byte_message, offset, byte_certificates_password[k].length);
            offset += byte_certificates_password[k].length;

            System.arraycopy(byte_certificates_name_length[k], 0, byte_message, offset, byte_certificates_name_length[k].length);
            offset += byte_certificates_name_length[k].length;

            System.arraycopy(byte_certificates_name[k], 0, byte_message, offset, byte_certificates_name[k].length);
            offset += byte_certificates_name[k].length;

            System.arraycopy(byte_certificates_length[k],0,byte_message, offset, byte_certificates_length[k].length);
            offset += byte_certificates_length[k].length;

            System.arraycopy(byte_certificates[k], 0, byte_message, offset, byte_certificates[k].length);
            offset += byte_certificates[k].length;

        }

        // profile
        for(int h = 0; h < profiles_length; h++){

            System.arraycopy(byte_profiles_key_name_length[h], 0, byte_message, offset, byte_profiles_key_name_length[h].length);
            offset += byte_profiles_key_name_length[h].length;

            System.arraycopy(byte_profiles_key_name[h], 0, byte_message, offset, byte_profiles_key_name[h].length);
            offset += byte_profiles_key_name[h].length;

            System.arraycopy(byte_profiles_build_type_length[h], 0, byte_message, offset, byte_profiles_build_type_length[h].length);
            offset += byte_profiles_build_type_length[h].length;

            System.arraycopy(byte_profiles_build_type[h], 0, byte_message, offset, byte_profiles_build_type[h].length);
            offset += byte_profiles_build_type[h].length;

            System.arraycopy(byte_profiles_name_length[h], 0, byte_message, offset, byte_profiles_name_length[h].length);
            offset += byte_profiles_name_length[h].length;

            System.arraycopy(byte_profiles_name[h], 0, byte_message, offset, byte_profiles_name[h].length);
            offset += byte_profiles_name[h].length;

            System.arraycopy(byte_profiles_length[h],0,byte_message, offset, byte_profiles_length[h].length);
            offset += byte_profiles_length[h].length;

            System.arraycopy(byte_profiles[h], 0, byte_message, offset, byte_profiles[h].length);
            offset += byte_profiles[h].length;


        }
        log.info(String.valueOf(offset));



        // 해당 구간에 provisioning file, signingkey file 전송 기능 추가하기 ...
        // websocketSession = WHiveWebSocketHandler.getSessionByIdentity(branchSetting.getBuilder_user_id(), SessionType.BRANCH);
        // 해당 Branch는 세션 연결이 되지 않았습니다. message 예외처리 추가 해야함.

        BuilderQueueManaged builderQueueManaged = builderQueueManagedService.findByID(branchSetting.getBuilder_id());
        WHiveIdentity wHiveIdentity = ClusterWebSocketBuilderService.getIdentityBuilder(builderQueueManaged.getQueue_etc_1());
        // msg.put(PayloadKeyType.managerClusterId.name(), builderQueueManaged.getQueue_etc_1());

        ClusterWebSocketBuilderService.sendBinaryMessage(wHiveIdentity, byte_message);

//        if(websocketSession != null){
//            try {
//                synchronized(websocketSession) {
//                    websocketSession.sendMessage(new BinaryMessage(byte_message));
//                }
//            } catch (IOException ex) {
//                log.warn(ex.getMessage(), ex); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
//
//            }
//
//        }

        entity.put("signingkeyCnt",key_id);
        return responseUtility.makeSuccessResponse(entity);

    }

    // 사용하지 않는 api
    @RequestMapping(value = "/api/signingkeysetting/updatekeyfile", method = RequestMethod.PUT)
    public ResponseEntity<Object> setUpdateToKeyFilePath(@RequestBody Map<String, Object> payload){

        String signingkey_id = payload.get("signingkey_id").toString();
        String signingkey_path = payload.get(PayloadKeyType.signingkey_path.name()).toString();
        log.info(" {}, {}", signingkey_id, signingkey_path);
        // signingKeySettingService.updateByKeyfilePath(Integer.parseInt(signingkey_id), signingkey_path);


        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/api/keysetting/ios/updatekeyfile", method = RequestMethod.PUT)
    public ResponseEntity<Object> setUpdateToiOSKeyFilePath(@RequestBody Map<String, Object> payload){

        String key_id = payload.get("key_id").toString();
        String ios_key_path = payload.get("ios_key_path").toString();
        String ios_debug_profile_path = payload.get("ios_debug_profile_path").toString();
        String ios_release_profile_path = payload.get("ios_release_profile_path").toString();
        log.info(" {}, {}, {}, {}", key_id, ios_key_path, ios_debug_profile_path, ios_release_profile_path);
        signingKeySettingService.updateByiOSKeyfilePath(Integer.parseInt(key_id), ios_key_path, ios_debug_profile_path, ios_release_profile_path);


        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/api/keysetting/ios/deploy/updatekeyfile", method = RequestMethod.PUT)
    public ResponseEntity<Object> setUpdateToiOSDeployKeyFilePath(@RequestBody Map<String, Object> payload){

        String key_id = payload.get("key_id").toString();
        String ios_key_path = payload.get("ios_key_path").toString();
        signingKeySettingService.updateByiOSDeployKeyfilePath(Integer.parseInt(key_id), ios_key_path);


        return responseUtility.makeSuccessResponse();
    }

    @RequestMapping(value = "/manager/mCert/common/selectAll", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<KeySetting> findAll(HttpServletRequest request) {
        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());

        }else {
            return null;
        }

        Long domain_id = memberDetail.getDomain_id();
        Long member_id = memberDetail.getUser_id();

        if(memberDetail.getUser_role().toString().equals("SUPERADMIN")){
            return signingKeySettingService.findAll();
        }else if(memberDetail.getUser_role().toString().equals("ADMIN")){
            return signingKeySettingService.findAdminAll(domain_id, member_id);
        }else {
            return null;
        }

    }

    @RequestMapping(value = "/manager/mCert/common/selectAllByAdmin", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<KeySetting> findByAdminOneAll(HttpServletRequest request, @RequestBody Map<String, Object> payload) {

        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if (memberLogin != null) {
//            if(payload.get(PayloadKeyType.sampleProjectYn.name()).toString().equals("N")) {
                memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());
                return signingKeySettingService.findByAdminOneAll(memberDetail.getUser_id(), payload.get(PayloadKeyType.platform.name()).toString());
//            }else {
//                memberDetail = memberService.findByIdDetail(1L);
//                return signingKeySettingService.findByAdminOneAll(memberDetail.getUser_id(), payload.get(PayloadKeyType.platform.name()).toString());
//            }

        } else {
            return null;
        }

    }

    @RequestMapping(value = "/manager/mCert/common/selectiOSProfilesKeyName", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    Object findiOSProfilesKeyName(HttpServletRequest request, @RequestBody Map<String, Object> payload) {
        MemberDetail memberDetail;

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            memberDetail = memberService.findByIdDetail(memberLogin.getUser_id());

        }else {
            return null;
        }

        Long domain_id = memberDetail.getDomain_id();
        Long member_id = memberDetail.getUser_id();

        if(memberDetail.getUser_role().toString().equals("SUPERADMIN")){
            return null;
        }else if(memberDetail.getUser_role().toString().equals("ADMIN")){
            KeyiOSSetting keyiOSSetting = signingKeySettingService.findByiOSProfilesKeyName(payload.get("key_id").toString());
            return keyiOSSetting.getIos_profiles_json();
        }else {
            return null;
        }

    }

    @RequestMapping(value = "/manager/mCert/android/search/profile/{key_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    KeyAndroidSetting findByAndroidID(@PathVariable("key_id") Long key_id) {

        return signingKeySettingService.findByAndroidID(key_id);
    }

    @RequestMapping(value = "/manager/mCert/iOS/search/profile/{key_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    KeyiOSSetting findByiOSID(@PathVariable("key_id") Long key_id) {

        return signingKeySettingService.findByiOSKeyID(key_id);
    }


    @RequestMapping(value = "/api/signingkeysetting/selectBySelectBoxList/{platform}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<SigningKeySelectBoxList> findBySelectList(@PathVariable("platform") String platform){
        return signingKeySettingService.findBySelectPlatformList(platform);
    }

    @RequestMapping(value = "/api/signingkeysetting/selectBySelectBoxListKeyType/{platform}/{signingkey_type}/{builder_id}/{domain_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<SigningKeySelectBoxList> findBySelectListByKeyType(@PathVariable("platform") String platform, @PathVariable("signingkey_type") String signingkey_type
            , @PathVariable("builder_id") Long builder_id, @PathVariable("domain_id") Long domain_id){
        return signingKeySettingService.findByKeyType(platform, signingkey_type, builder_id, domain_id);
    }

    @RequestMapping(value = "/api/signingkeysetting/selectBySelectBoxListAndroidKeyType/{platform}/{key_type}/{builder_id}/{role_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<SigningKeySelectBoxList> findBySelectListByAndroidKeyType(@PathVariable("platform") String platform, @PathVariable("key_type") String key_type
            , @PathVariable("builder_id") Long builder_id, @PathVariable("role_id") Long role_id){
        return signingKeySettingService.findByKeyType(platform, key_type, builder_id, role_id);
    }

    // findByKeyTypeRE
    @RequestMapping(value = "/manager/mCert/common/search/profileList", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<KeySettingList> findBySelectListByKeyTypeRE(HttpServletRequest request){

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){

            return signingKeySettingService.findByKeyTypeRE(memberLogin.getRole_id());
        }else {
            return null;
        }


    }

    @RequestMapping(value = "/api/signingkeysetting/selectBySelectBoxListBuildType/{platform}/{build_type}/{provisioning}/{builder_id}/{domain_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<SigningKeySelectBoxList> findBySelectListByBuildType(@PathVariable("platform") String platform, @PathVariable("build_type") String build_type
            , @PathVariable("provisioning") String provisioning, @PathVariable("builder_id") Long builder_id, @PathVariable("domain_id") Long domain_id){
        return signingKeySettingService.findByBuildType(platform, build_type, provisioning, builder_id, domain_id);
    }

    // findByDeployTypeList
    // /api/signingkeysetting/SelectListDeployKeyType
    // /manager/mCert/common/search/storeDeployKeyType
    @RequestMapping(value = "/manager/mCert/common/search/storeDeployKeyType", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<KeyDeploySettingList> findBySelectListDeployKeyType(HttpServletRequest request){
        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){


            return signingKeySettingService.findByDeployTypeList(memberLogin.getRole_id());
        }else {
            return null;
        }


    }

    // findByDeployTypeList
    @RequestMapping(value = "/manager/mCert/common/search/storeDeployKey/{key_id}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody KeyDeploySettingList findBySelectListDeployKeyTypeOne(@PathVariable("key_id") int key_id){
        return signingKeySettingService.findByDeployTypeOne(key_id);
    }

    // findByDeployTypeList Service Admin
    @RequestMapping(value = "/manager/mCert/common/search/storeDeployKeyByPlatform/{platform}", method = RequestMethod.GET, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody List<KeySetting> findBySelectListDeployKeyTypeService(HttpServletRequest request, @PathVariable("platform") String platform){

        String userId = common.getTokenToRealName(request);
        MemberLogin memberLogin = memberService.findByUserLoginID(userId);

        if(memberLogin != null){
            return signingKeySettingService.findByDeployTypeServiceList(memberLogin.getUser_id(), platform);
        }else{
            return null;
        }

    }


    @RequestMapping(value = "/api/signingkeysetting/filetemp", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public ResponseEntity<Object> getProvisionFileTemp(
            @RequestParam("file") MultipartFile file,
            @RequestParam("platform") String platform){

        String getFilename = file.getOriginalFilename();
        // ios android 구분해서 처리 dir 도 나누기
        File provisionfile = new File(provisionFilePath, getFilename);

        try {
            file.transferTo(provisionfile);

        } catch (IOException e) {
             
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }

        return responseUtility.makeSuccessResponse();
    }


    @RequestMapping(value = "/manager/mCert/common/search/checkProfileName", method = RequestMethod.POST, produces = {"application/json"})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> findBySigningkeyName(@RequestBody Map<String, Object> payload){
        JSONObject obj = new JSONObject();

        if( payload.get(PayloadKeyType.key_name.name()).toString() == null  || payload.get(PayloadKeyType.key_name.name()).toString().equals("") ) {
            throw new WHiveInvliadRequestException(MessageString.REQUIRED_BUILD_PROJECT_NAME.getMessage());
        }
        List<KeySetting> keySetting = signingKeySettingService.findByKeyName(payload.get(PayloadKeyType.key_name.name()).toString());

        if(!keySetting.toString().equals("[]")){
            obj.put("keyNameCheck","no");
            return responseUtility.makeSuccessResponse(obj);

        } else if(keySetting.toString().equals("[]")){
            obj.put("keyNameCheck","yes");
            return responseUtility.makeSuccessResponse(obj);

        }else {
            obj.put("keyNameCheck","yes");
            return responseUtility.makeSuccessResponse(obj);

        }
    }


    // int to byteArray 변환
    private byte[] convertIntToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value >> 24);
        byteArray[1] = (byte)(value >> 16);
        byteArray[2] = (byte)(value >> 8);
        byteArray[3] = (byte)(value);
        return byteArray;
    }

}
