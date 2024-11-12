package com.inswave.whive.branch.util;

import com.inswave.whive.branch.enums.PayloadMsgType;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BranchRestTempleteUtil {

    @Value("${whive.branch.id}")
    private String userId;

    @Value("${whive.branch.password}")
    private String builderPassword;

    private String bucket = "";

    AWSClientUtil awsClientUtil = new AWSClientUtil();

    private CookieStore cookies = new BasicCookieStore();

    SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

    public void getUrlProjectZipFileRestTemplete(String url, MultiValueMap<String, Object> reqToFileObj){

        getBuilderToLoginCheckURL(url, userId, builderPassword); // session 초기 인증시 필요한 메소드 세팅

        requestFactory.setBufferRequestBody(false);
        requestFactory.setOutputStreaming(false);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        List<HttpMessageConverter<?>> mc = restTemplate.getMessageConverters();
        mc.add(new MappingJackson2HttpMessageConverter());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        headers.add(HttpHeaders.COOKIE, "JSESSIONID="+cookies.getCookies().get(0).getValue());

        //log.info("getUrlRestTemplete reqToFileObj | {}",reqToFileObj.get("filename"));
        //log.info("getUrlRestTemplete reqToFileObj | {}",reqToFileObj.get("file"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(reqToFileObj,headers);
        String totalUrl = url+"/manager/build/project/export/upload";
        try {
            String response = restTemplate.postForObject(totalUrl, request, String.class);
            // log.info("getUrlRestTemplete response | {}",response);

        } catch (ResourceAccessException e){
            log.info("error urlprojectziprestTemplate : ", e.getMessage());
        }


    }

    // apk, ipa file Download 추후 method 이름 변경 예정.
    public boolean getUrlRestQRCodeUrl(String url, MultiValueMap<String, Object> reqToFileObj, String userID, String password){

        getBuilderToLoginCheckURL(url, userID, password);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.MULTIPART_FORM_DATA_VALUE));
        headers.add(HttpHeaders.COOKIE, "JSESSIONID="+cookies.getCookies().get(0).getValue());

        //log.info("getUrlRestTemplete reqToFileObj | {}",reqToFileObj.get("filename"));
        //log.info("getUrlRestTemplete reqToFileObj | {}",reqToFileObj.get("file"));

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(reqToFileObj,headers);
        String totalUrl = url+"/builder/build/history/uploadSetupFileToWebServer"; // 수정 해야함....
        try {
            String response = restTemplate.postForObject(totalUrl, request, String.class);
            log.info("getUrlRestTemplete response | {}",response);
            if(response == null){
                return false;
            }else if(response != null){
                return true;
            }else {
                return false;
            }


        } catch (ResourceAccessException e){
             
            return false;
        }


    }

    /**
     * method : getUrlRestQRCodeUrlToAwsS3
     * @param url
     * @param reqToFileObj
     * @param userID
     * @param password
     * @return
     */
    public boolean getUrlRestQRCodeUrlToAwsS3(String url, MultiValueMap<String, Object> reqToFileObj, String userID, String password){

        try {

            String AppFileDir = "AppFileDir/";
            String filename  = reqToFileObj.get("filename").get(0).toString();
            String filePath = reqToFileObj.get("filePath").get(0).toString() ;
            String platform = reqToFileObj.get(PayloadMsgType.platform.name()).get(0).toString();
            String projectDir = reqToFileObj.get("projectDir").get(0).toString();
            String nowString = reqToFileObj.get("nowString").get(0).toString();
            String bucket = reqToFileObj.get("bucket").get(0).toString();

            String parentDirString = AppFileDir + platform + "/" + projectDir + "/" + nowString;

            File file = new File(filePath );
            FileItem fileItem = new DiskFileItem(filename, Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);

            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            awsClientUtil.upload(multipartFile, parentDirString, bucket);

            return true;
        } catch (IOException ex) {
            // do something.
            log.info(ex.getMessage(),ex);
        }

        return false;
    }

    public boolean getUrlRestExportZipUrlToAwsS3(MultiValueMap<String, Object> reqToFileObj, String userID, String password){

        try {
            String filePath = reqToFileObj.get("filePath").get(0).toString() ;

            String AppFileDir = "AppFileDir/";
            String projectDir = reqToFileObj.get("projectDirName").get(0).toString();
            String nowString = reqToFileObj.get("nowString").get(0).toString();

            String filename  = reqToFileObj.get("filename").get(0).toString();

            String parentDirString = AppFileDir + projectDir + "/" + nowString;

            File file = new File(filePath );
            FileItem fileItem = new DiskFileItem(filename, Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
            InputStream input = new FileInputStream(file);
            OutputStream os = fileItem.getOutputStream();
            IOUtils.copy(input, os);

            MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

            awsClientUtil.upload(multipartFile, parentDirString, bucket);

            return true;
        } catch (IOException ex) {
            // do something.
            log.info(ex.getMessage(),ex);
        }

        return false;
    }

    public boolean getUrlRestQRCodeUrlToAwsS3Bak(String url, MultiValueMap<String, Object> reqToFileObj, String userID, String password){

        getBuilderToLoginCheckURL(url, userID, password);

        HttpPost post = new HttpPost(url+"/builder/build/history/uploadSetupFileToAwsS3Server");
        post.addHeader(HttpHeaders.COOKIE.toString(), cookies.getCookies().toString());

        //Creating CloseableHttpClient object
        CloseableHttpClient httpclient = HttpClients.createDefault();

        File file = new File(reqToFileObj.get("filePath").get(0).toString() +"/"+reqToFileObj.get("filename").get(0).toString());
        StringBody stringBody1 = new StringBody(reqToFileObj.get("filename").get(0).toString(), ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody2 = new StringBody(reqToFileObj.get("filePath").get(0).toString(), ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody3 = new StringBody(reqToFileObj.get(PayloadMsgType.platform.name()).get(0).toString(), ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody4 = new StringBody(reqToFileObj.get("projectDir").get(0).toString(), ContentType.MULTIPART_FORM_DATA);
        StringBody stringBody5 = new StringBody(reqToFileObj.get("nowString").get(0).toString(), ContentType.MULTIPART_FORM_DATA);

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

        builder.addBinaryBody("file", file, ContentType.MULTIPART_FORM_DATA, file.getName());
        builder.addPart("filename", stringBody1);
        builder.addPart("filePath", stringBody2);
        builder.addPart(PayloadMsgType.platform.name(), stringBody3);
        builder.addPart("projectDir", stringBody4);
        builder.addPart("nowString", stringBody5);
        org.apache.http.HttpEntity entity = builder.build();

        post.setEntity(entity);
        try(CloseableHttpClient client = httpclient) {

            client.execute(post, response -> {
                //do something with response
                log.info(response.toString());
                return true;
            });
        } catch (ResourceAccessException e){
            log.info(e.getMessage(),e);
            return false;
         }catch (HttpServerErrorException e){
            log.info(e.getMessage(),e);
            return false;
        } catch (IOException e) {
            log.info(e.getMessage(),e);
            return false;
        }


        return false;
    }

    public void getiOSPlistAndHTMLFileDataSetURLtoAws(String url, JSONObject jsonRequest){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        headers.add(HttpHeaders.COOKIE, "JSESSIONID="+cookies.getCookies().get(0).getValue());
        HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toJSONString(),headers);
        String totalUrl = url+"/builder/build/history/plistAndHTMLFileToWas"; // 수정 해야함....

        try {
            String response = restTemplate.postForObject(totalUrl, request, String.class);
            // log.info("getUrlRestTemplete response | {}",response);
            if(response == null){

            }else if(response != null){

            }else {

            }

        } catch (ResourceAccessException e){

        }

    }


    public void getiOSPlistAndHTMLFileDataSetURL(String url, JSONObject jsonRequest){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE));
        headers.add(HttpHeaders.COOKIE, "JSESSIONID="+cookies.getCookies().get(0).getValue());
        HttpEntity<String> request = new HttpEntity<String>(jsonRequest.toJSONString(),headers);
        String totalUrl = url+"/builder/build/history/plistAndHTMLFileToWas"; // 수정 해야함....

        try {
            String response = restTemplate.postForObject(totalUrl, request, String.class);
            // log.info("getUrlRestTemplete response | {}",response);
            if(response == null){

            }else if(response != null){

            }else {

            }

        } catch (ResourceAccessException e){

        }

    }

    public void testBuilderURL(String url){

        log.info("===  testBuilderURL === ");

        log.info("===  getBuilderToLoginCheckURL === ");

        // getBuilderToLoginCheckURL(url);

        log.info(" ==== end testBuilderURL === ");
    }

    private void getBuilderToLoginCheckURL(String url, String userID, String passwordTmp){

        // yaml value set 처리 하기
        String username = userID;
        String password = passwordTmp;

        try {
            CloseableHttpClient client = HttpClients.createDefault();

            HttpPost httpPost = new HttpPost(url + "/manager/branchSetting/builderLoginCheck");

            List <NameValuePair> paramList = new ArrayList<NameValuePair>();
            paramList.add(new BasicNameValuePair("userid", username));
            paramList.add(new BasicNameValuePair("pword", password));

//            ObjectMapper objectMapper = new ObjectMapper();
//            String requestBody = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(params);
//            BodyPublisher body = BodyPublishers.ofString(requestBody);

//            httpPost.setHeader("Content-Type" ,MediaType.APPLICATION_JSON_VALUE);
            httpPost.setEntity(new UrlEncodedFormEntity(paramList));


            HttpContext httpContext = new BasicHttpContext();
            httpContext.setAttribute(HttpClientContext.COOKIE_STORE, cookies);

            client.execute(httpPost, httpContext);
            CloseableHttpResponse response = client.execute(httpPost, httpContext);

            log.info(response.getHeaders("Cookie").toString());
            log.info(cookies.getCookies().toString());
            log.info(cookies.getCookies().get(0).getValue());
            log.info(response.getAllHeaders().toString());
            log.info(response.getEntity().getContent().toString());

        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
