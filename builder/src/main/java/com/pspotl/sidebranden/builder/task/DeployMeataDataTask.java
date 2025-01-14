package com.pspotl.sidebranden.builder.task;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Map;

@Slf4j
@Component
public class DeployMeataDataTask {


    @Value("${whive.distribution.UserRootPath}")
    private String rootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    private JSONParser parser = new JSONParser();

    // TODO : deploy metadata update method


    // TODO : deploy metadata search method
    public JSONObject getDeployMetaDataTextSearch(Map<String, Object> parseResult, String builderPath){
        /**
         * TODO : deploy metadata text search 기능 수행
         ** text file 별로 read 처리 하기
         * android/ ios 분기 처리하기
         */
        String platform = parseResult.get("platform").toString();
        JSONObject resultDeployMetaDataTextObj = new JSONObject();
        if(platform.toLowerCase().equals("android")){
            // android /fastlane/metadata/android/ko-KR/
            // full_description
            // short_description
            // title
            // video
            // 각 파일 별로 read 처리하고
            // 각 키 값으로  변수 값 저장 하고
            // JSONobject 로 리턴 하기

            String AndroidTitle = getFileStringSearch(parseResult, builderPath, "title");
            String AndroidFullDescription = getFileStringSearch(parseResult, builderPath, "full_description");
            String AndroidShortDescription = getFileStringSearch(parseResult, builderPath, "short_description");
            String AndroidVideo = getFileStringSearch(parseResult, builderPath, "video");

            resultDeployMetaDataTextObj.put("title", AndroidTitle);
            resultDeployMetaDataTextObj.put("full_description", AndroidFullDescription);
            resultDeployMetaDataTextObj.put("short_description", AndroidShortDescription);
            resultDeployMetaDataTextObj.put("video", AndroidVideo);


        }else if(platform.toLowerCase().equals("ios")){

            // metadata
            String iOSPrimaryCategory = getiOSMetadataFileStringSearch(parseResult, builderPath, "primary_category");
            String iOSCopyright = getiOSMetadataFileStringSearch(parseResult, builderPath, "copyright");
            String iOSPrimaryFirstSubCategory = getiOSMetadataFileStringSearch(parseResult, builderPath, "primary_first_sub_category");
            String iOSPrimarySecondSubCategory = getiOSMetadataFileStringSearch(parseResult, builderPath, "primary_second_sub_category");
            String iOSSecondaryCategory = getiOSMetadataFileStringSearch(parseResult, builderPath, "secondary_category");
            String iOSSecondaryFirstSubCategory = getiOSMetadataFileStringSearch(parseResult, builderPath, "secondary_first_sub_category");
            String iOSSecondarySecondSubCategory = getiOSMetadataFileStringSearch(parseResult, builderPath, "secondary_second_sub_category");

            resultDeployMetaDataTextObj.put("primary_category", iOSPrimaryCategory);
            resultDeployMetaDataTextObj.put("copyright", iOSCopyright);
            resultDeployMetaDataTextObj.put("primary_first_sub_category", iOSPrimaryFirstSubCategory);
            resultDeployMetaDataTextObj.put("primary_second_sub_category", iOSPrimarySecondSubCategory);
            resultDeployMetaDataTextObj.put("secondary_category", iOSSecondaryCategory);
            resultDeployMetaDataTextObj.put("secondary_first_sub_category", iOSSecondaryFirstSubCategory);
            resultDeployMetaDataTextObj.put("secondary_second_sub_category", iOSSecondarySecondSubCategory);

            // national
            String iOSname = getiOSMetadataGlobalLangsFileStringSearch(parseResult,builderPath, "name", "ko");
            String iOSKeyworkds = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "keywords", "ko");
            String iOSAppleTvPrivacyPlicy = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "apple_tv_privacy_policy", "ko");
            String iOSdescription = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "description", "ko");
            String iOSMarketingUrl = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "marketing_url", "ko");
            String iOSPrivacyUrl = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "privacy_url", "ko");
            String iOSPromotionalText = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "promotional_text", "ko");
            String iOSReleaseNotes = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "release_notes", "ko");
            String iOSSubtitle = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "subtitle", "ko");
            String iOSSupportUrl = getiOSMetadataGlobalLangsFileStringSearch(parseResult, builderPath, "support_url", "ko");

            resultDeployMetaDataTextObj.put("name", iOSname);
            resultDeployMetaDataTextObj.put("keywords", iOSKeyworkds);
            resultDeployMetaDataTextObj.put("apple_tv_privacy_policy", iOSAppleTvPrivacyPlicy);
            resultDeployMetaDataTextObj.put("description", iOSdescription);
            resultDeployMetaDataTextObj.put("marketing_url", iOSMarketingUrl);
            resultDeployMetaDataTextObj.put("privacy_url", iOSSecondaryFirstSubCategory);
            resultDeployMetaDataTextObj.put("secondary_second_sub_category", iOSPrivacyUrl);
            resultDeployMetaDataTextObj.put("promotional_text", iOSPromotionalText);
            resultDeployMetaDataTextObj.put("release_notes", iOSReleaseNotes);
            resultDeployMetaDataTextObj.put("subtitle", iOSSubtitle);
            resultDeployMetaDataTextObj.put("support_url", iOSSupportUrl);

            String iOSDemoPassword = getiOSMetadataRevieInfoFileStringSearch(parseResult, builderPath, "demo_password");
            String iOSDemoUser = getiOSMetadataRevieInfoFileStringSearch(parseResult, builderPath, "demo_user");
            String iOSEmailAaddress = getiOSMetadataRevieInfoFileStringSearch(parseResult, builderPath, "email_address");
            String iOSFirstName = getiOSMetadataRevieInfoFileStringSearch(parseResult, builderPath, "first_name");
            String iOSLastName = getiOSMetadataRevieInfoFileStringSearch(parseResult, builderPath, "last_name");
            String iOSNotes = getiOSMetadataRevieInfoFileStringSearch(parseResult, builderPath, "notes");
            String iOSPhoneNumber = getiOSMetadataRevieInfoFileStringSearch(parseResult, builderPath, "phone_number");

            resultDeployMetaDataTextObj.put("demo_password", iOSDemoPassword);
            resultDeployMetaDataTextObj.put("demo_user", iOSDemoUser);
            resultDeployMetaDataTextObj.put("email_address", iOSEmailAaddress);
            resultDeployMetaDataTextObj.put("first_name", iOSFirstName);
            resultDeployMetaDataTextObj.put("last_name", iOSLastName);
            resultDeployMetaDataTextObj.put("notes", iOSNotes);
            resultDeployMetaDataTextObj.put("phone_number", iOSPhoneNumber);


        }

        return resultDeployMetaDataTextObj;


    }


    // TODO : deploy metadata text read method

    /**
     * Android getFileStringSearch
     * @param parseResult
     * @param builderPath
     * @param filename
     * @return
     */
    private String getFileStringSearch(Map<String, Object> parseResult, String builderPath, String filename){

        // log.info(systemUserHomePath + rootPath + builderPath);
        String readLine = null;
        String resultStr = "";
        // TODO 특정 경로 read
        try(
                FileReader rw = new FileReader(systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/android/ko-KR/"+filename+".txt");
                BufferedReader br = new BufferedReader( rw );
        ){

            //읽을 라인이 없을 경우 br은 null을 리턴한다.

            while( ( readLine =  br.readLine()) != null ){
                // read 된 key string 값 매핑
                // readLine.replace("\n","");
                // log.info(Arrays.toString(envStr));
                resultStr += readLine;
                // System.out.println(readLine);
                // TODO .env 파일 수정 정보 확인하기
            }

        }catch ( IOException e ) {
            log.warn(e.getMessage(), e);
            // System.out.println(e);
        }

        // 리턴 객체 방식은 json
        return resultStr;

    }

    /**
     * getiOSMetadataFileStringSearch
     * @param parseResult
     * @param builderPath
     * @param filename
     * @return
     */
    private String getiOSMetadataFileStringSearch(Map<String, Object> parseResult, String builderPath, String filename){

        // log.info(systemUserHomePath + rootPath + builderPath);
        String readLine = null;
        String resultStr = "";
        // TODO 특정 경로 read
        try(
                FileReader rw = new FileReader(systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/"+filename+".txt");
                BufferedReader br = new BufferedReader( rw );
        ){

            //읽을 라인이 없을 경우 br은 null을 리턴한다.

            while( ( readLine =  br.readLine()) != null ){
                // read 된 key string 값 매핑
                // readLine.replace("\n","");
                // log.info(Arrays.toString(envStr));
                resultStr += readLine;
                // System.out.println(readLine);
                // TODO .env 파일 수정 정보 확인하기
            }

        }catch ( IOException e ) {
            log.warn(e.getMessage(), e);
            // System.out.println(e);
        }

        // 리턴 객체 방식은 json
        return resultStr;

    }

    private String getiOSMetadataGlobalLangsFileStringSearch(Map<String, Object> parseResult, String builderPath, String filename, String national){

        // log.info(systemUserHomePath + rootPath + builderPath);
        String readLine = null;
        String resultStr = "";
        // TODO 특정 경로 read
        try(
                FileReader rw = new FileReader(systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/"+national+"/"+filename+".txt");

                BufferedReader br = new BufferedReader( rw );
        ){

            //읽을 라인이 없을 경우 br은 null을 리턴한다.

            while( ( readLine =  br.readLine()) != null ){
                // read 된 key string 값 매핑
                // readLine.replace("\n","");
                // log.info(Arrays.toString(envStr));
                resultStr += readLine;
                // System.out.println(readLine);
                // TODO .env 파일 수정 정보 확인하기
            }

        }catch ( IOException e ) {
            log.warn(e.getMessage(), e);
            // System.out.println(e);
        }

        // 리턴 객체 방식은 json
        return resultStr;

    }

    private String getiOSMetadataRevieInfoFileStringSearch(Map<String, Object> parseResult, String builderPath, String filename){

        // log.info(systemUserHomePath + rootPath + builderPath);
        String readLine = null;
        String resultStr = "";
        // TODO 특정 경로 read
        try(
                FileReader rw = new FileReader(systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/review_information/"+filename+".txt");

                BufferedReader br = new BufferedReader( rw );
        ){

            //읽을 라인이 없을 경우 br은 null을 리턴한다.

            while( ( readLine =  br.readLine()) != null ){
                // read 된 key string 값 매핑
                // readLine.replace("\n","");
                // log.info(Arrays.toString(envStr));
                resultStr += readLine;
                // System.out.println(readLine);
                // TODO .env 파일 수정 정보 확인하기
            }

        }catch ( IOException e ) {
            log.warn(e.getMessage(), e);
            // System.out.println(e);
        }

        // 리턴 객체 방식은 json
        return resultStr;

    }


    // TODO :  deploy metadata text write method
    public void setAndroidMetadataFileStringWtire(Map<String, Object> parseResult, String builderPath, String filename){

        String buildAfterLogFile = systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/android/ko-KR/"+filename+".txt";
        File f = new File(buildAfterLogFile);

        try {

            String jsonDeployMetadataSetJsonStr = parseResult.get("jsonDeployMetadataSetJson").toString();
            JSONObject jsonDeployMetadataSetJson = (JSONObject) parser.parse(jsonDeployMetadataSetJsonStr);
            // log.info(jsonDeployMetadataSetJson.toJSONString());

            try(FileWriter fstream = new FileWriter(f);
                BufferedWriter out = new BufferedWriter(fstream);) {

                /**
                 * file name 이 json key 값과 동일함
                 */
                out.append(jsonDeployMetadataSetJson.get(filename).toString()+"\n");

                out.flush();
                out.close();

            }catch ( IOException e ) {
                log.warn(e.getMessage(), e);

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


    public void setiOSMetadataFileStringWrite(Map<String, Object> parseResult, String builderPath, String filename){

        String buildAfterLogFile = systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/"+filename+".txt";
        File f = new File(buildAfterLogFile);

        try {

            String jsonDeployMetadataSetJsonStr = parseResult.get("jsonDeployMetadataSetJson").toString();
            JSONObject jsonDeployMetadataSetJson = (JSONObject) parser.parse(jsonDeployMetadataSetJsonStr);
            // log.info(jsonDeployMetadataSetJson.toJSONString());

            try(FileWriter fstream = new FileWriter(f);
                BufferedWriter out = new BufferedWriter(fstream);) {

                /**
                 * file name 이 json key 값과 동일함
                 */
                out.append(jsonDeployMetadataSetJson.get(filename).toString()+"\n");

                out.flush();
                out.close();

            }catch ( IOException e ) {
                log.warn(e.getMessage(), e);

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void setiOSMetadataGlobalLangsFileSringWrite(Map<String, Object> parseResult, String builderPath, String filename, String national){

        String buildAfterLogFile = systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/"+national+"/"+filename+".txt";
        File f = new File(buildAfterLogFile);

        try {

            String jsonDeployMetadataSetJsonStr = parseResult.get("jsonDeployMetadataSetJson").toString();
            JSONObject jsonDeployMetadataSetJson = (JSONObject) parser.parse(jsonDeployMetadataSetJsonStr);
            // log.info(jsonDeployMetadataSetJson.toJSONString());

            try(FileWriter fstream = new FileWriter(f);
                BufferedWriter out = new BufferedWriter(fstream);) {

                /**
                 * file name 이 json key 값과 동일함
                 */
                out.append(jsonDeployMetadataSetJson.get(filename).toString()+"\n");

                out.flush();
                out.close();

            }catch ( IOException e ) {
                log.warn(e.getMessage(), e);

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    public void setiOSMetadataRevieInfoFileStringWrite(Map<String, Object> parseResult, String builderPath, String filename){

        String buildAfterLogFile = systemUserHomePath + rootPath + builderPath+"/fastlane/metadata/review_information/"+filename+".txt";
        File f = new File(buildAfterLogFile);

        try {

            String jsonDeployMetadataSetJsonStr = parseResult.get("jsonDeployMetadataSetJson").toString();
            JSONObject jsonDeployMetadataSetJson = (JSONObject) parser.parse(jsonDeployMetadataSetJsonStr);
            // log.info(jsonDeployMetadataSetJson.toJSONString());

            try(FileWriter fstream = new FileWriter(f);
                BufferedWriter out = new BufferedWriter(fstream);) {

                /**
                 * file name 이 json key 값과 동일함
                 */
                out.append(jsonDeployMetadataSetJson.get(filename).toString()+"\n");

                out.flush();
                out.close();

            }catch ( IOException e ) {
                log.warn(e.getMessage(), e);

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


    // TODO : deploy metadata image file read method


    // TODO : deploy metadata image file append method


    // TODO : deploy metadata ios app store connect api key file read
    public String getiOSAppStoreConnectAPIKeyDataListSearch(Map<String, Object> parseResult, String builderPath){
        String jsoniOSData = "";

        // log.info(systemUserHomePath + rootPath + builderPath);
        try {
            JSONObject jsonDeployObj = (JSONObject) parser.parse(parseResult.get("jsonDeployObj").toString());

            String iOSAppStoreConnectAPIFilePath = jsonDeployObj.get("apple_app_store_connect_api_key").toString();
            // TODO 특정 경로 read
            try(
                    FileReader rw = new FileReader(iOSAppStoreConnectAPIFilePath);
                    BufferedReader br = new BufferedReader( rw );
            ){

                //읽을 라인이 없을 경우 br은 null을 리턴한다.
                String str;
                while ((str = br.readLine()) != null) {
                    log.info(str);
                    jsoniOSData += str;
                    log.info(jsoniOSData);

                }

                jsoniOSData = jsoniOSData.replace("-----BEGIN PRIVATE KEY-----","-----BEGIN PRIVATE KEY-----\\n");
                jsoniOSData = jsoniOSData.replace("-----END PRIVATE KEY-----","\\n-----END PRIVATE KEY-----");
                jsoniOSData = jsoniOSData.trim();
                br.close();

            }catch ( IOException e ) {
                log.warn(e.getMessage(), e);
                // System.out.println(e);
            }catch (NullPointerException e){
                log.warn(e.getMessage(), e);
            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // 리턴 객체 방식은 json
        return jsoniOSData;

    }

    // TODO : deploy metadata ios-file.json file witre
    public void setiOSJSONfileDataListWrite(Map<String, Object> parseResult, String builderPath) {

        /**
         *  get Env 만든 기반으로
         *  set Env 기능 구현 하기 ..
         */

        String buildAfterLogFile = builderPath+"/fastlane/ios-file.json";
        File f = new File(buildAfterLogFile);

        JSONObject jsonEnvData = new JSONObject();

        try {
            /**
             *  json signingkey ios data setting
             */
            log.info(buildAfterLogFile);
            if(f.createNewFile()){
                log.info("file created..");
            }else {
                log.info("file created..");
            }

            jsonEnvData = (JSONObject) parser.parse(parseResult.get("jsonDeployObj").toString());

            // log.info(jsonEnvData.toJSONString());

            try(FileWriter fstream = new FileWriter(f);
                BufferedWriter out = new BufferedWriter(fstream);) {

                out.append("{\"key_id\":\""+jsonEnvData.get("apple_key_id").toString()+"\",\n");
                out.append("\"issuer_id\":\""+jsonEnvData.get("apple_issuer_id").toString()+"\",\n");
                out.append("\"key\":\""+parseResult.get("iosAppStoreConnectAPIKey").toString()+"\",\n");
                out.append("\"duration\":"+1200+",\n");
                out.append("\"in_house:\":"+false+"}\n");

                out.flush();
                out.close();

            }catch ( IOException e ) {
                log.warn(e.getMessage(), e);

            }

        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
