package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.*;
import com.pspotl.sidebranden.builder.enums.AppleApiEnum;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AppleApiService implements AppleApi {

    private static final Logger logger = LoggerFactory.getLogger(AppleApiService.class);

    @Value("${whive.distribution.deployLogPath}")
    private String rootPath; // 상대 경로 수정

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Autowired
    private RestTemplate restTemplate = new RestTemplate();

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }

    public void getSigningKeyToToken(String path, String platform, SigningMode mode, Map<String, Object> appStoreApiResult){
        registerNewBundleId(path, platform, mode, appStoreApiResult);

    }

    public List<AppleResultDTO> getNumberOfAvailableDevices(String path, String platform, SigningMode mode, Map<String, Object> appStoreApiResult) {
        HttpHeaders headers = getHeaderToToken(path, platform, mode, appStoreApiResult);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        String url = AppleApiEnum.LIST_DEVICE_API.getApiPath();
        ResponseEntity<AppleApiResult<List<AppleResultDTO>>> response = restTemplate.exchange(url,AppleApiEnum.LIST_DEVICE_API.getHttpMethod(),httpEntity,
                new ParameterizedTypeReference<AppleApiResult<List<AppleResultDTO>>>(){});
        AppleApiResult<List<AppleResultDTO>> responseBody = response.getBody();
        logger.info("registerNewBundleId response {}",response.getBody().getData());
        return responseBody.getData();
    }

    public AppleResultDTO registerNewBundleId(String path, String platform, SigningMode mode, Map<String, Object> appStoreApiResult){
        HttpHeaders headers = getHeaderToToken(path, platform, mode, appStoreApiResult);
        // app_id
        String identifier = appStoreApiResult.get("bundleId").toString();
        String app_name = appStoreApiResult.get("app_name").toString();

        if(identifier.equals("") || identifier == null){
            return null;
        }

        if(app_name.equals("") || app_name == null){
            return null;
        }

        logger.info(" registerNewBundleId headers {}",headers);
        AppleReqBody attributes = AppleReqBody.init().add("identifier", identifier).add("name", app_name).add(PayloadMsgType.platform.name(), "IOS");
        AppleReqBody body = AppleReqBody.init().add("type", "bundleIds").add("attributes", attributes);
        AppleReqBody data = AppleReqBody.init().add("data",body);
        HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(data,headers);
        String url = AppleApiEnum.REGISTER_NEW_BUNDLEID_API.getApiPath();
        ResponseEntity<AppleApiResult<AppleResultDTO>> response = restTemplate.exchange(url,AppleApiEnum.REGISTER_NEW_BUNDLEID_API.getHttpMethod(),httpEntity,
                new ParameterizedTypeReference<AppleApiResult<AppleResultDTO>>(){});
        logger.info("registerNewBundleId response {}",response.getBody().getData());
        return response.getBody().getData();

    }

    public AppleResultDTO insertCertificates(String path, String platform, SigningMode mode, Map<String, Object> appStoreApiResult){
        // header token 생성
        HttpHeaders headers = getHeaderToToken(path, platform, mode, appStoreApiResult);
        logger.info(" headers check : {}" ,headers);

        AppleReqBody attributes = AppleReqBody.init().add("csrContent", appStoreApiResult.get("file")).add("certificateType", "IOS_DEVELOPMENT");
        AppleReqBody body = AppleReqBody.init().add("type", "certificates").add("attributes", attributes);
        AppleReqBody data = AppleReqBody.init().add("data",body);
        HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(data,headers);
        String url = AppleApiEnum.NEW_CERTIFICATES_API.getApiPath();
        ResponseEntity<AppleApiResult<AppleResultDTO>> response = restTemplate.exchange(url,AppleApiEnum.NEW_CERTIFICATES_API.getHttpMethod(),httpEntity,
                new ParameterizedTypeReference<AppleApiResult<AppleResultDTO>>(){});

        logger.info("insertCertificates response {}",response.getBody().getData());
        return response.getBody().getData();
    }

    public File getMobileprovision(String path, String platform, SigningMode mode, Map<String, Object> appStoreApiResult){

        HttpHeaders headers = getHeaderToToken(path, platform, mode, appStoreApiResult);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String bundleId = appStoreApiResult.get("bundleId").toString();
        String cerId = appStoreApiResult.get("cerId").toString();
        //String devId = appStoreApiResult.get("devId").toString();
        String provisioningProfilesName = appStoreApiResult.get("provisioning_profiles_name").toString();

        AppleReqBody attributes = AppleReqBody.init().add("name", provisioningProfilesName).add("profileType", "IOS_APP_DEVELOPMENT");
        AppleReqBody relationships = getProfileRelationships(bundleId, cerId);
        AppleReqBody body = AppleReqBody.init().add("attributes", attributes).add("relationships", relationships).add("type","profiles");
        AppleReqBody data = AppleReqBody.init().add("data",body);

        HttpEntity<Map<String,Object>> httpEntity = new HttpEntity<>(data,headers);
        //logger.info(" getMobileprovision httpEntity All {}",httpEntity);
        //logger.info(" getMobileprovision httpEntity getHeaders {}",httpEntity.getHeaders());
        //logger.info(" getMobileprovision httpEntity getBody {}",httpEntity.getBody());
        String url = AppleApiEnum.CREATE_PROFILE_API.getApiPath();

        ResponseEntity<AppleApiResult<AppleResultDTO>> response = restTemplate.exchange(url,AppleApiEnum.CREATE_PROFILE_API.getHttpMethod(),httpEntity,
                new ParameterizedTypeReference<AppleApiResult<AppleResultDTO>>(){});
        logger.info(" getMobileprovision getData {}",response.getBody().getData());
        File file = null;
        return file;
    }

    private AppleReqBody getProfileRelationships(String bundleId,String cerId){

        AppleReqBody bundle = AppleReqBody.init().add("id", bundleId).add("type", "bundleIds");
        AppleReqBody bundleIds = AppleReqBody.init().add("data", bundle);

        AppleReqBody certificate = AppleReqBody.init().add("id", cerId).add("type", "certificates");
        List<AppleReqBody> certificatesList = new ArrayList<>();
        certificatesList.add(certificate);
        AppleReqBody certificates = AppleReqBody.init().add("data", certificatesList);

        List<AppleReqBody> deviceList = new ArrayList<>();
        // devices id data hard code setting
        AppleReqBody device = AppleReqBody.init().add("id", "23T4CDXY55").add("type", "devices");
        deviceList.add(device);
        AppleReqBody devices = AppleReqBody.init().add("data", deviceList);

        AppleReqBody relationships = AppleReqBody.init().add("bundleId", bundleIds).add("certificates", certificates)
                .add("devices", devices);
        return relationships;
    }

    private HttpHeaders getHeaderToToken(String path, String platform, SigningMode mode, Map<String, Object> appStoreApiResult){
        HttpHeaders headers = null;
        //logger.info("registerNewBundleId, AppleResultDTO | appStoreApiResult {} ",appStoreApiResult);
        if (platform.toLowerCase().equals(PayloadMsgType.ios.name())) {
            if (mode.equals(SigningMode.CREATE)) {
                CommandLine commandLine = CommandLine.parse("ruby");
                commandLine.addArgument("-C");
                commandLine.addArgument(appStoreApiResult.get("signing_path").toString());
                commandLine.addArgument("signingkey_ios.rb");
                commandLine.addArgument("/Users/jeongsikkim/Documents");
                commandLine.addArgument(appStoreApiResult.get("signing_issuer_id").toString());
                commandLine.addArgument(appStoreApiResult.get("signing_key_id").toString());

                try {
                    headers =  executeCommonsSigningCreate(commandLine, path, platform, appStoreApiResult);
                    return headers;
                } catch (Exception e) {

                }

            }

        }

        return headers;
    }

    /* commons_exec 라이브러리 executeCommonsExecBuild method   */
    private HttpHeaders executeCommonsSigningCreate(CommandLine commandLineParse, String path, String platform, Map<String, Object> appStoreApiResult) throws Exception {
        HttpHeaders headers = null;
        String token;

        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

        try {
            handler.start();
            CreateSiginingMessage createSiginingMessage = new CreateSiginingMessage();
            //iOSSigningKeyAddMessageHandler(createSiginingMessage, "CREATING");

            //headQuaterClientHandler.sendMessage(parseResult);

            executor.execute(commandLineParse, resultHandler);
            resultHandler.waitFor();
            LocalDateTime date = LocalDateTime.now();
            String buildAfterLogFile = rootPath+platform+"_log"+date+".txt";

            Reader reader = new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray()));
            BufferedReader r = new BufferedReader(reader);
            File f = new File(buildAfterLogFile);
            FileWriter fstream = new FileWriter(f);
            BufferedWriter out = new BufferedWriter(fstream);


            while ((token = r.readLine()) != null)
            {
                out.write(token+"\n");
                logger.info(" #### Build CommandLine log data ### : " + token);
                headers = getToken(token);
            }

            int exitCode = resultHandler.getExitValue();

            if(exitCode == 0){
                logger.info(" exitCode : {}", exitCode);
                logger.info(" headers : {}", headers);
                //iOSSigningKeyAddMessageHandler(createSiginingMessage, "SUCCESSFUL");

                //headQuaterClientHandler.sendMessage(parseResult);

                return headers;
            } else if(exitCode == 1){
                logger.info(" exitCode : {}", exitCode);
                //iOSSigningKeyAddMessageHandler(createSiginingMessage, "FAILED");

                //headQuaterClientHandler.sendMessage(parseResult);
                return headers;
            } else {
                logger.info(" exitCode : {}", exitCode);
                //iOSSigningKeyAddMessageHandler(createSiginingMessage, "FAILED");

                //headQuaterClientHandler.sendMessage(parseResult);

            }
            r.close();
            out.flush();
            handler.stop();

            return headers;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            throw new Exception(e.getMessage(), e);
        }

    }

    private void iOSSigningKeyAddMessageHandler(CreateSiginingMessage createSiginingMessage, String messageValue){
        ObjectMapper Mapper = new ObjectMapper();

        createSiginingMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        createSiginingMessage.setMsgType(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name());
        createSiginingMessage.setStatus("sigining");
        createSiginingMessage.setMessage(messageValue);

        Map<String, Object> parseResult = Mapper.convertValue(createSiginingMessage, Map.class);
        headQuaterClientHandler.sendMessage(parseResult);
    }

}
