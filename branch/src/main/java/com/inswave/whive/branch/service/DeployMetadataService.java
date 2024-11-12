package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.DeploySettingMsg;
import com.inswave.whive.branch.enums.DeployServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.task.DeployMeataDataTask;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class DeployMetadataService {

    @Value("${whive.distribution.UserRootPath}")
    private String rootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    DeployMeataDataTask deployMeataDataTask;

    private ObjectMapper Mapper = new ObjectMapper();


    // TODO : deploy metadata update method
    @Async("asyncThreadPool")
    public void setDeployMetaDataTextUpdate(WebSocketSession session, Map<String, Object> parseResult, String builderPath){
        /**
         *  TODO: deploy metadata text setting 설정 기능 수행
         **  text file 별로 write 처리하기
         */
        DeploySettingMsg deploySettingMsg = new DeploySettingMsg();
        String hqKey = parseResult.get("hqKey").toString();
        String platform = parseResult.get("platform").toString();
        deploySettingMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());

        deploySetMetadataSendMessage(session, deploySettingMsg, hqKey, "setMetadata", "");

        if(platform.toLowerCase().equals("android")){
            deployMeataDataTask.setAndroidMetadataFileStringWtire(parseResult, builderPath, "full_description");
            deployMeataDataTask.setAndroidMetadataFileStringWtire(parseResult, builderPath, "short_description");
            deployMeataDataTask.setAndroidMetadataFileStringWtire(parseResult, builderPath, "title");
            deployMeataDataTask.setAndroidMetadataFileStringWtire(parseResult, builderPath, "video");

        }else if(platform.toLowerCase().equals("ios")){

            /** metadata file list*/
            deployMeataDataTask.setiOSMetadataFileStringWrite(parseResult, builderPath, "primary_category");
            deployMeataDataTask.setiOSMetadataFileStringWrite(parseResult, builderPath, "copyright");
            deployMeataDataTask.setiOSMetadataFileStringWrite(parseResult, builderPath, "primary_first_sub_category");
            deployMeataDataTask.setiOSMetadataFileStringWrite(parseResult, builderPath, "primary_second_sub_category");
            deployMeataDataTask.setiOSMetadataFileStringWrite(parseResult, builderPath, "secondary_category");
            deployMeataDataTask.setiOSMetadataFileStringWrite(parseResult, builderPath, "secondary_first_sub_category");
            deployMeataDataTask.setiOSMetadataFileStringWrite(parseResult, builderPath, "secondary_second_sub_category");

            /** global langs file list*/
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "name", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "keywords", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "apple_tv_privacy_policy", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "description", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "marketing_url", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "privacy_url", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "promotional_text", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "release_notes", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "subtitle", "ko");
            deployMeataDataTask.setiOSMetadataGlobalLangsFileSringWrite(parseResult, builderPath, "support_url", "ko");

            /** review_information file list*/
            deployMeataDataTask.setiOSMetadataRevieInfoFileStringWrite(parseResult, builderPath, "demo_password");
            deployMeataDataTask.setiOSMetadataRevieInfoFileStringWrite(parseResult, builderPath, "demo_user");
            deployMeataDataTask.setiOSMetadataRevieInfoFileStringWrite(parseResult, builderPath, "email_address");
            deployMeataDataTask.setiOSMetadataRevieInfoFileStringWrite(parseResult, builderPath, "first_name");
            deployMeataDataTask.setiOSMetadataRevieInfoFileStringWrite(parseResult, builderPath, "last_name");
            deployMeataDataTask.setiOSMetadataRevieInfoFileStringWrite(parseResult, builderPath, "notes");
            deployMeataDataTask.setiOSMetadataRevieInfoFileStringWrite(parseResult, builderPath, "phone_number");


        }


        deploySetMetadataSendMessage(session, deploySettingMsg, hqKey, "DONE", "");


    }




    private void deploySetMetadataSendMessage(WebSocketSession session, DeploySettingMsg deploySettingMsg, String hqKeyTemp, String deployStatus, String logMessage){

        deploySettingMsg.setMsgType(DeployServiceType.HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO.name());
        deploySettingMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        deploySettingMsg.setHqKey(hqKeyTemp); // hqKey User 아이디

        deploySettingMsg.setStatus(deployStatus);

        deploySettingMsg.setMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(deploySettingMsg, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);


    }





}
