package com.pspotl.sidebranden.builder.task;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.Map;

@Slf4j
@Component
public class DeployTask {

    // 필수 yaml 값 가지고 오기 ...
    @Value("${whive.distribution.UserRootPath}")
    private String rootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    private JSONParser parser = new JSONParser();

    // TODO fastlaen .env 값 조회 및 설정 기능 추가
    // TODO : Deploy task setting 조회 기능 추가...
    public JSONObject getDotEnvDataListSearch(WebSocketSession session, Map<String, Object> parseResult, String builderPath){
        JSONObject jsonEnvData = new JSONObject();

        // log.info(systemUserHomePath + rootPath + builderPath);
        // TODO 특정 경로 read
        try(
                FileReader rw = new FileReader(systemUserHomePath + rootPath + builderPath+"/fastlane/.env");
                BufferedReader br = new BufferedReader( rw );
        ){

            //읽을 라인이 없을 경우 br은 null을 리턴한다.
            String readLine = null ;
            while( ( readLine =  br.readLine()) != null ){
                // read 된 key string 값 매핑
                // readLine.replace("\n","");
                String[] envStr = readLine.split("=");
                // log.info(Arrays.toString(envStr));
                if(envStr.length == 1){
                    jsonEnvData.put(envStr[0], "");
                }else {
                    jsonEnvData.put(envStr[0], envStr[1]);
                }

                // System.out.println(readLine);
                // TODO .env 파일 수정 정보 확인하기
            }

        }catch ( IOException e ) {
            log.warn(e.getMessage(), e);
            // System.out.println(e);
        }

        // 리턴 객체 방식은 json
        return jsonEnvData;

    }

    // TODO file write 기능 및 read 처리 이후
    public void setDotEnvDataListWrite(WebSocketSession session, Map<String, Object> parseResult, String builderPath) {

        /**
         *  get Env 만든 기반으로
         *  set Env 기능 구현 하기 ..
         */

        String buildAfterLogFile = systemUserHomePath + rootPath + builderPath+"/fastlane/.env";
        File f = new File(buildAfterLogFile);

        JSONObject jsonEnvData = new JSONObject();

        try {
            jsonEnvData = (JSONObject) parser.parse(parseResult.get("jsonEnvData").toString());

        String jsonDeployTaskSetJsonStr = parseResult.get("jsonDeployTaskSetJson").toString();
        JSONObject jsonDeployTaskSetJson = (JSONObject) parser.parse(jsonDeployTaskSetJsonStr);
        // log.info(jsonEnvData.toJSONString());

           try(FileWriter fstream = new FileWriter(f);
               BufferedWriter out = new BufferedWriter(fstream);) {

               out.append("ASC_ISSUER_ID="+jsonEnvData.get("ASC_ISSUER_ID").toString()+"\n"); // 고정
               out.append("ASC_KEY_ID="+jsonEnvData.get("ASC_KEY_ID").toString()+"\n"); // 고정
               out.append("ASC_KEY_FILEPATH="+jsonEnvData.get("ASC_KEY_FILEPATH").toString()+"\n"); // 고정
               out.append("BUILD_APP_FILE_PATH="+jsonEnvData.get("BUILD_APP_FILE_PATH").toString()+"\n"); // 고정
               out.append("BUILD_APP_BUILD_NUMBER="+jsonEnvData.get("BUILD_APP_BUILD_NUMBER").toString()+"\n"); // 고정

               out.append("SKIP_SUBMISSION="+jsonDeployTaskSetJson.get("skip_submission").toString()+"\n");
               out.append("SKIP_WAITING_FOR_BUILD_PROCESSING="+jsonDeployTaskSetJson.get("skip_waiting_for_build_processing").toString()+"\n");
               out.append("DISTRIBUTE_ONLY="+jsonDeployTaskSetJson.get("distribute_only").toString()+"\n");
               out.append("USES_NON_EXEMPT_ENCRYPTION="+jsonDeployTaskSetJson.get("uses_non_exempt_encryption").toString()+"\n");
               out.append("DISTRIBUTE_EXTERNAL="+jsonDeployTaskSetJson.get("distribute_external").toString()+"\n");
               out.append("EXPIRE_PREVIOUS_BUILDS="+jsonDeployTaskSetJson.get("expire_previous_builds").toString()+"\n");
               out.append("REJECT_BUILD_WAITING_FOR_REVIEW="+jsonDeployTaskSetJson.get("reject_build_waiting_for_review").toString()+"\n");
               out.append("SUBMIT_BETA_REVIEW="+jsonDeployTaskSetJson.get("submit_beta_review").toString()+"\n");

               out.flush();
               out.close();

           }catch ( IOException e ) {
               log.warn(e.getMessage(), e);

           }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


    public void setAndroidDotEnvDataListWrite(WebSocketSession session, Map<String, Object> parseResult, String builderPath) {

        /**
         *  get Env 만든 기반으로
         *  set Env 기능 구현 하기 ..
         */

        String buildAfterLogFile = systemUserHomePath + rootPath + builderPath+"/fastlane/.env";
        File f = new File(buildAfterLogFile);

        JSONObject jsonEnvData = new JSONObject();

        try {
            jsonEnvData = (JSONObject) parser.parse(parseResult.get("jsonEnvData").toString());

            String jsonDeployTaskSetJsonStr = parseResult.get("jsonDeployTaskSetJson").toString();
            JSONObject jsonDeployTaskSetJson = (JSONObject) parser.parse(jsonDeployTaskSetJsonStr);
            // log.info(jsonEnvData.toJSONString());

            try(FileWriter fstream = new FileWriter(f);
                BufferedWriter out = new BufferedWriter(fstream);) {

                out.append("BUILD_APP_FILE_PATH="+jsonEnvData.get("BUILD_APP_FILE_PATH").toString()+"\n");
                out.append("BUILD_APP_VERSION_CODE="+jsonEnvData.get("BUILD_APP_VERSION_CODE").toString()+"\n");

                out.append("SKIP_UPLOAD_AAB="+jsonDeployTaskSetJson.get("skip_upload_aab").toString()+"\n");
                out.append("SKIP_UPLOAD_METADATA="+jsonDeployTaskSetJson.get("skip_upload_metadata").toString()+"\n");
                out.append("SKIP_UPLOAD_CHANGELOGS="+jsonDeployTaskSetJson.get("skip_upload_changelogs").toString()+"\n");
                out.append("SKIP_UPLOAD_IMAGES="+jsonDeployTaskSetJson.get("skip_upload_images").toString()+"\n");
                out.append("SKIP_UPLOAD_SCREENSHOTS="+jsonDeployTaskSetJson.get("skip_upload_screenshots").toString()+"\n");
                out.append("VALIDATE_ONLY="+jsonDeployTaskSetJson.get("validate_only").toString()+"\n");
                out.append("CHANGES_NOT_SENT_FOR_REVIEW="+jsonDeployTaskSetJson.get("changes_not_sent_for_review").toString()+"\n");
                out.append("RESCUE_CHANGES_NOT_SENT_FOR_REVIEW="+jsonDeployTaskSetJson.get("rescue_changes_not_sent_for_review").toString()+"\n");
                out.append("ACK_BUNDLE_INSTALLATION_WARNING="+jsonDeployTaskSetJson.get("ack_bundle_installation_warning").toString()+"\n");

                out.flush();
                out.close();

            }catch ( IOException e ) {
                log.warn(e.getMessage(), e);

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

}
