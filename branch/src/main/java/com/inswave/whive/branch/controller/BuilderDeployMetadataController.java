package com.inswave.whive.branch.controller;

import com.inswave.whive.branch.domain.BuildResponse;
import com.inswave.whive.branch.service.DeployMetadataImageService;
import com.inswave.whive.branch.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
public class BuilderDeployMetadataController {

    @Value("${whive.distribution.UserRootPath}")
    private String UserRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    @Autowired
    DeployMetadataImageService deployMetadataImageService;

    JSONParser parser = new JSONParser();


    // TODO : deploymetadata api 추가하기
    @RequestMapping(value = "/builder/project/deploy/metadataImageFile", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody DeferredResult<BuildResponse> deployMetadataScreenShotImageUpdate(HttpServletRequest request, @RequestParam("imageFiles") List<MultipartFile> imageFiles
            , @RequestParam("projectDeployJson") String projectDeployJson) {

//        Map<String, Object> parseResult = new HashMap<>();
        String sessionId = ServletUtil.getSession().getId();
        log.info(">> Build task add. session id : {}", sessionId);

        try {
            JSONObject jsonProjectDeployObj = (JSONObject) parser.parse(projectDeployJson);
            // TODO : deploy meata data 스크린샷 image file 전송 기능 구현 하기
            deployMetadataImageService.setDeployMetadataImageWrite(imageFiles, jsonProjectDeployObj);

            BuildResponse buildResponse = new BuildResponse();
            buildResponse.setResponseResult(BuildResponse.ResponseResult.SUCCESS);
            buildResponse.setbuildTaskId(sessionId);
            buildResponse.setSessionId(sessionId);

            final DeferredResult<BuildResponse> deferredResult = new DeferredResult<>(null);
            deferredResult.setResult(buildResponse);

            return deferredResult;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * api /builder/project/deploy/android/metadataImageFile
     * @param request
     * @param imageFiles
     * @param projectDeployJson
     * @return
     */
    @RequestMapping(value = "/builder/project/deploy/android/metadataImageFile", method = RequestMethod.POST, produces= MediaType.APPLICATION_JSON_VALUE, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public @ResponseBody DeferredResult<BuildResponse> deployAndroidMetadataScreenShotImageUpdate(HttpServletRequest request, @RequestParam("phoneImagefiles") List<MultipartFile> imageFiles, @RequestParam("sevenInchTabletImagefiles") List<MultipartFile> sevenInchTabletImagefiles,
                                                                                                  @RequestParam("tenInchTabletImagefiles") List<MultipartFile> tenInchTabletImagefiles,
                                                                                                  @RequestParam("projectDeployJson") String projectDeployJson) {

//        Map<String, Object> parseResult = new HashMap<>();
        String sessionId = ServletUtil.getSession().getId();
        log.info(">> Build task add. session id : {}", sessionId);

        try {
            JSONObject jsonProjectDeployObj = (JSONObject) parser.parse(projectDeployJson);
            deployMetadataImageService.setDeployMetadataImageWrite(imageFiles, jsonProjectDeployObj);
            deployMetadataImageService.setAndroid7InchTabletDeployMetadataImageWrite(sevenInchTabletImagefiles, jsonProjectDeployObj);
            deployMetadataImageService.setAndroid10InchTabletDeployMetadataImageWrite(tenInchTabletImagefiles, jsonProjectDeployObj);

            BuildResponse buildResponse = new BuildResponse();
            buildResponse.setResponseResult(BuildResponse.ResponseResult.SUCCESS);
            buildResponse.setbuildTaskId(sessionId);
            buildResponse.setSessionId(sessionId);

            final DeferredResult<BuildResponse> deferredResult = new DeferredResult<>(null);
            deferredResult.setResult(buildResponse);

            return deferredResult;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
