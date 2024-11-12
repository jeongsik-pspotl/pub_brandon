package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.DeployMetadataImageMsg;
import com.inswave.whive.branch.domain.ProjectGitCloneMessage;
import com.inswave.whive.branch.enums.*;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.task.DeployMetadataImageTask;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.socket.WebSocketSession;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class DeployMetadataImageService {

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    DeployMetadataImageTask deployMetadataImageTask;

    @Value("${whive.distribution.UserRootPath}")
    private String UserRootPath;

    private ObjectMapper Mapper = new ObjectMapper();

    private String systemUserHomePath = System.getProperty("user.home");

    private String metadataScreenShotDir = "/fastlane/metadata/android/ko-KR/images/phoneScreenshots/"; // TODO : android screen shot image phone, 테블릿 별로 디렉토리 분기처리 ... 추가로 국가별 로도 처리 해야함.

    private String matadataScreentShotAndroid7InchTablet = "/fastlane/metadata/android/ko-KR/images/sevenInchScreenshots/";

    private String matadataScreentShotAndroid10InchTablet = "/fastlane/metadata/android/ko-KR/images/tenInchScreenshots/";

    private String matadataScreentShotiOS = "/fastlane/screenshots/ko/";

    // android/ios 분기로 image read/ update 기능 구현하기

    // TODO : deploy metadata 스크린샷 image 파일 전달 하기
    public void getDeployMetadataImageRead(WebSocketSession session, Map<String, Object> parseResult, String builderPath ){

        String platform = parseResult.get("platform").toString();
        String hqKey = parseResult.get("hqKey").toString();
        DeployMetadataImageMsg deployMetadataImageMsg = new DeployMetadataImageMsg();

        deployMetadataImageMsg.setHqKey(hqKey);
        deployMetadataImageMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());

        deployMetadataStatusMsg(session, deployMetadataImageMsg, "", "IMAGEREADING");
        if(platform.toLowerCase().equals("android")){
            String TenInchTabletDir = systemUserHomePath + UserRootPath + builderPath + matadataScreentShotAndroid10InchTablet;
            String sevenInchTabletDir = systemUserHomePath + UserRootPath + builderPath + matadataScreentShotAndroid7InchTablet;
            String phoneDir = systemUserHomePath + UserRootPath + builderPath + metadataScreenShotDir;

            deployMetadataImageTask.deployMetadataAndroidAllImageFileSend(session, parseResult, phoneDir, BinaryServiceType.HV_BIN_DEPLOY_ANDROID_METADATA_PHONE_IMAGE_READ.toString() );
            deployMetadataImageTask.deployMetadataAndroidAllImageFileSend(session, parseResult, TenInchTabletDir, BinaryServiceType.HV_BIN_DEPLOY_ANDROID_METADATA_TABLET_10INCH_IMAGE_READ.toString() );
            deployMetadataImageTask.deployMetadataAndroidAllImageFileSend(session, parseResult, sevenInchTabletDir, BinaryServiceType.HV_BIN_DEPLOY_ANDROID_METADATA_TABLET_7INCH_IMAGE_READ.toString() );

        }else if(platform.toLowerCase().equals("ios")){
            builderPath = systemUserHomePath + UserRootPath + builderPath + matadataScreentShotiOS;
            deployMetadataImageTask.deployMetadataImageFileSend(session, parseResult, builderPath);
            deployMetadataImageTask.deployMetadataPhoneDeivceImageFileSend(session, parseResult, builderPath);
            deployMetadataImageTask.deployMetadataTabletLargeInchImageFileSend(session, parseResult, builderPath);
            deployMetadataImageTask.deployMetadataTabletSmallInchImageFileSend(session, parseResult, builderPath);
        }

        deployMetadataStatusMsg(session, deployMetadataImageMsg, "", "DONE");



    }

    @Async
    public void setDeployMetadataImageWrite(List<MultipartFile> imageFiles, JSONObject jsonProjectDeployObj){

        String UserProjectPath;

        String domainName = BuilderDirectoryType.DOMAIN_.toString() + jsonProjectDeployObj.get(PayloadMsgType.domain_id.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + jsonProjectDeployObj.get(PayloadMsgType.user_id.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + jsonProjectDeployObj.get(PayloadMsgType.workspace_id.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString(); //

        String platform = jsonProjectDeployObj.get(PayloadMsgType.platform.name()).toString();
        String build_id = jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString();

        UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + jsonProjectDeployObj.get(PayloadMsgType.domain_id.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + jsonProjectDeployObj.get(PayloadMsgType.user_id.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +jsonProjectDeployObj.get(PayloadMsgType.workspace_id.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString();

        log.info(jsonProjectDeployObj.toJSONString());

        // TODO : image file name 동적으로 규칙성 있게 이름을 넣어야함...
        for(int i = 0; i< imageFiles.size(); i++){
            MultipartFile multipartFileImageOne = imageFiles.get(i);
            if(platform.toLowerCase().equals("android")){
                File imageFile = new File(UserProjectPath + metadataScreenShotDir + multipartFileImageOne.getOriginalFilename());

                try {
                    multipartFileImageOne.transferTo(imageFile);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }else if(platform.toLowerCase().equals("ios")){
                File imageFile = new File(UserProjectPath + matadataScreentShotiOS + multipartFileImageOne.getOriginalFilename());

                try {
                    multipartFileImageOne.transferTo(imageFile);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


            }


        }


    }

    @Async
    public void setAndroid7InchTabletDeployMetadataImageWrite(List<MultipartFile> imageFiles, JSONObject jsonProjectDeployObj){

        String UserProjectPath;

        String domainName = BuilderDirectoryType.DOMAIN_.toString() + jsonProjectDeployObj.get(PayloadMsgType.domain_id.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + jsonProjectDeployObj.get(PayloadMsgType.user_id.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + jsonProjectDeployObj.get(PayloadMsgType.workspace_id.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString(); //

        String platform = jsonProjectDeployObj.get(PayloadMsgType.platform.name()).toString();
        String build_id = jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString();

        UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + jsonProjectDeployObj.get(PayloadMsgType.domain_id.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + jsonProjectDeployObj.get(PayloadMsgType.user_id.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +jsonProjectDeployObj.get(PayloadMsgType.workspace_id.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString();

        log.info(jsonProjectDeployObj.toJSONString());

        // TODO : image file name 동적으로 규칙성 있게 이름을 넣어야함...
        for(int i = 0; i< imageFiles.size(); i++){
            MultipartFile multipartFileImageOne = imageFiles.get(i);
            if(platform.toLowerCase().equals("android")){
                File imageFile = new File(UserProjectPath + matadataScreentShotAndroid7InchTablet + multipartFileImageOne.getOriginalFilename());

                try {
                    multipartFileImageOne.transferTo(imageFile);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }


    }

    @Async
    public void setAndroid10InchTabletDeployMetadataImageWrite(List<MultipartFile> imageFiles, JSONObject jsonProjectDeployObj){

        String UserProjectPath;

        String domainName = BuilderDirectoryType.DOMAIN_.toString() + jsonProjectDeployObj.get(PayloadMsgType.domain_id.name()).toString();
        String userName = BuilderDirectoryType.USER_.toString() + jsonProjectDeployObj.get(PayloadMsgType.user_id.name()).toString();
        String workspaceName = BuilderDirectoryType.WORKSPACE_W.toString() + jsonProjectDeployObj.get(PayloadMsgType.workspace_id.name()).toString();
        String projectName = BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString(); //

        String platform = jsonProjectDeployObj.get(PayloadMsgType.platform.name()).toString();
        String build_id = jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString();

        UserProjectPath = systemUserHomePath + UserRootPath + "builder_main/"+BuilderDirectoryType.DOMAIN_.toString() + jsonProjectDeployObj.get(PayloadMsgType.domain_id.name()).toString() +"/" + BuilderDirectoryType.USER_.toString() + jsonProjectDeployObj.get(PayloadMsgType.user_id.name()).toString() +
                "/" + BuilderDirectoryType.WORKSPACE_W.toString() +jsonProjectDeployObj.get(PayloadMsgType.workspace_id.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString() + "/" + BuilderDirectoryType.PROJECT_P.toString() + jsonProjectDeployObj.get(PayloadMsgType.projectID.name()).toString();

        log.info(jsonProjectDeployObj.toJSONString());

        // TODO : image file name 동적으로 규칙성 있게 이름을 넣어야함...
        for(int i = 0; i< imageFiles.size(); i++){
            MultipartFile multipartFileImageOne = imageFiles.get(i);
            if(platform.toLowerCase().equals("android")){
                File imageFile = new File(UserProjectPath + matadataScreentShotAndroid10InchTablet + multipartFileImageOne.getOriginalFilename());

                try {
                    multipartFileImageOne.transferTo(imageFile);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }


        }


    }

    private void deployMetadataStatusMsg(WebSocketSession session, DeployMetadataImageMsg deployMetadataImageMsg, String logMessage, String status){

        deployMetadataImageMsg.setMsgType(DeployServiceType.HV_MSG_DEPLOY_METADATA_IMAGE_STATUS_INFO.name());
        deployMetadataImageMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        // projectGitCloneMessage.setHqKey(hqKeyTemp); // hqKey 브라우저 아이디


        deployMetadataImageMsg.setMessage(logMessage);
        deployMetadataImageMsg.setStatus(status);

        Map<String, Object> parseResult = Mapper.convertValue(deployMetadataImageMsg, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);
    }

}
