package com.inswave.whive.branch.service;

import com.inswave.whive.branch.util.BranchUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
public class ProvisioningProfileService {

    // deploy root path
    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Autowired
    private BranchUtil branchUtil;

    // temp dir to zip file 전송
    // @param byte provisioningFileObj
    public void tempToProvisioningProfileUpload(Map<String, Object> provisioningFileObj) throws IOException {


        try {

            String filename = provisioningFileObj.get("filename").toString();

            byte[] data = SerializationUtils.serialize((Serializable) provisioningFileObj.get("file"));

            byte[] payloadEncoding = Base64.getEncoder().encode(data);
            byte[] payloadDecoder = Base64.getDecoder().decode(payloadEncoding);

            File profile = new File(userRootPath+"tempZipDir/"+filename);
            profile.createNewFile();
            FileUtils.writeStringToFile(profile, new String(payloadDecoder), StandardCharsets.ISO_8859_1.toString());

        } catch (Throwable throwable) {

            log.error("builder profile service error ",throwable);
        } finally {

        }
    }

    //
    public void tempToProvisioningProfileUploadIns(InputStream in, String filename, String platform){
        // app icon upload start
        File profileTemp = new File(userRootPath+"tempZipDir/"+filename);

        try {
            FileUtils.copyInputStreamToFile(in, profileTemp);

        } catch (IOException e) {

            log.error("builder profile service error ",e);
        }
    }
}
