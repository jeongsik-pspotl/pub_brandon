package com.inswave.whive.branch.controller;

import com.inswave.whive.branch.domain.AppleResultDTO;
import com.inswave.whive.branch.domain.SigningMode;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.service.AppleApiService;
import com.inswave.whive.branch.service.ProvisioningProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class SigningKeyController {

    @Autowired
    AppleApiService appleApiService;
    
    @Autowired
    ProvisioningProfileService provisioningProfileService;

    @PostMapping("/branch/signingkey/upload")
    public Map iOSSigningKeyUpload(@RequestParam("file") MultipartFile uploadfile, @RequestParam("platform") String platform) throws IOException {


        InputStream in = uploadfile.getInputStream();

        provisioningProfileService.tempToProvisioningProfileUploadIns(in, uploadfile.getOriginalFilename(), platform);

        return null;
    }

    @RequestMapping(value = "/branch/signingkey/create", method = RequestMethod.POST, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Map signingKeyUpload(HttpServletRequest request) throws IOException {
        Map <String,Object> signingKeyUpload = new HashMap<>();


        signingKeyUpload.put("file",request.getParameterMap().get("file")[0]);
        signingKeyUpload.put("filename",request.getParameterMap().get("filename")[0]);
        signingKeyUpload.put("bundleId",request.getParameterMap().get("bundleId")[0]);
        signingKeyUpload.put("devId","");
        signingKeyUpload.put("app_name",request.getParameterMap().get("app_name")[0]);
        signingKeyUpload.put("signing_path",request.getParameterMap().get("signing_path")[0]);
        signingKeyUpload.put("signing_issuer_id",request.getParameterMap().get("signing_issuer_id")[0]);
        signingKeyUpload.put("signing_key_id",request.getParameterMap().get("signing_key_id")[0]);
        signingKeyUpload.put("provisioning_profiles_name",request.getParameterMap().get("provisioning_profiles_name")[0]);

        String signingPath = request.getParameterMap().get("signing_path")[0];
        String signingPlatform = request.getParameterMap().get(PayloadMsgType.platform.name())[0];
        String bundleId = "";
        String cerId = "";
        AppleResultDTO appleResultDTOCertificates = null;
        AppleResultDTO appleResultDTOBundleId = null;

        //registerNewBundleId 실행
        appleResultDTOBundleId = appleApiService.registerNewBundleId(signingPath, signingPlatform, SigningMode.CREATE, signingKeyUpload);
        bundleId = appleResultDTOBundleId.getId();
        signingKeyUpload.replace("bundleId",bundleId);

        // insertCertificates 실행
        if(appleResultDTOBundleId != null){
            appleResultDTOCertificates = appleApiService.insertCertificates(signingPath, signingPlatform, SigningMode.CREATE, signingKeyUpload);
        }
        appleApiService.getNumberOfAvailableDevices(signingPath, signingPlatform, SigningMode.CREATE, signingKeyUpload);
        // getMobileprovision 메소드 실행
        if(appleResultDTOCertificates != null){
            // cerId 값 생성 및 세팅
            cerId = appleResultDTOCertificates.getId();
            signingKeyUpload.put("cerId",cerId);
            File file = appleApiService.getMobileprovision(signingPath, signingPlatform, SigningMode.CREATE, signingKeyUpload);
        }

        return null;

    }

}
