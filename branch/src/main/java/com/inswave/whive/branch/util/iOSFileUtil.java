package com.inswave.whive.branch.util;

import com.inswave.whive.branch.enums.PayloadKeyType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.*;

@Slf4j
@Component
public class iOSFileUtil {

//    @Value("${whive.distribution.deployQRCodeUrlPath}")
    private String qrCodeUploadDir;

    BranchRestTempleteUtil branchRestTempleteUtil = new BranchRestTempleteUtil();

    // app name, app id, app version
    public boolean createPlistFile(JSONObject jsonResult) throws IOException {
        boolean result = false;

        MultiValueMap<String, Object> reqToFileObj =
                new LinkedMultiValueMap<String, Object>();

        File fplist = null;
        BufferedWriter out = null;
        String buildAftermanifestPlist = "manifest.plist"; //jsonResult.get(PayloadKeyType.Project.name()).toString() +"/manifest.plist";
        log.info(buildAftermanifestPlist);
        log.info(jsonResult.toJSONString());
        String projectFilePath = jsonResult.get("plistPath").toString();

        String plistWasURL = projectFilePath + "/" + buildAftermanifestPlist;

        String plistWebURL = jsonResult.get("ftpUrl").toString() + "/AppFileDir/iOS/"
                + jsonResult.get(PayloadKeyType.Project.name()).toString() + "/"
                + jsonResult.get("createdDateTime").toString() + "/"
                + jsonResult.get(PayloadKeyType.AppName.name()).toString();

        try {
            fplist = new File(plistWasURL);
            // fplist = File.createTempFile("manifest",".plist"); // 수정해야함...
            FileWriter fplistStream = new FileWriter(fplist);
            out = new BufferedWriter(fplistStream);

            out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+"\n");
            out.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">"+"\n");
            out.append("<plist version=\"1.0\">"+"\n");
            out.append("<dict>"+"\n");
            out.append("<key>items</key>"+"\n");
            out.append("<array>"+"\n");
            out.append("<dict>"+"\n");
            out.append("<key>assets</key>"+"\n");
            out.append("<array>"+"\n");
            out.append("<dict>"+"\n");
            out.append("<key>kind</key>"+"\n");
            out.append("<string>software-package</string>"+"\n");
            out.append("<key>url</key>"+"\n");
            out.append("<string>" + plistWebURL + "</string>"+"\n"); // // {project}/{filename} 처리 과정 추가.. https://store.inswave.kr/
            out.append("</dict>"+"\n");
            out.append("</array>"+"\n");
            out.append("<key>metadata</key>"+"\n");
            out.append("<dict>"+"\n");
            out.append("<key>bundle-identifier</key>"+"\n");
            out.append("<string>" + jsonResult.get(PayloadKeyType.ApplicationID.name()).toString()  +" </string>"+"\n"); // bundle id 수정
            out.append("<key>bundle-version</key>"+"\n");
            out.append("<string>"+ jsonResult.get(PayloadKeyType.AppVersion.name()).toString()  +"</string>"+"\n"); // version 수정 필요
            out.append("<key>kind</key>"+"\n");
            out.append("<string>software</string>"+"\n");
            out.append("<key>title</key>"+"\n");
            out.append("<string>"+jsonResult.get(PayloadKeyType.AppName.name()).toString() +"</string>"+"\n"); // 더블유하이브리드
            out.append("</dict>"+"\n");
            out.append("</dict>"+"\n");
            out.append("</array>"+"\n");
            out.append("</dict>"+"\n");
            out.append("</plist>"+"\n");

            out.flush();

            // TODO set file parameter
            reqToFileObj.add("file",fplist);
            reqToFileObj.add("filePath", fplist.getPath());
//            reqToFileObj.add("plistPath", appfilesPath.getPath());
            reqToFileObj.add("filename", buildAftermanifestPlist);
            reqToFileObj.add(PayloadMsgType.platform.name(),"iOS");
            reqToFileObj.add("projectDir",jsonResult.get("Project").toString());
            reqToFileObj.add("nowString",jsonResult.get("nowString").toString());
            reqToFileObj.add("bucket",jsonResult.get("bucket").toString());

            //TODO send to aws s3 upload
            branchRestTempleteUtil.getUrlRestQRCodeUrlToAwsS3(jsonResult.get("ftpUrl").toString(), reqToFileObj, jsonResult.get("builderUserName").toString(), jsonResult.get("password").toString());

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        }finally {
            if (out != null) out.close();
        }

        // 정상 Plist Upload 처리 확인
        return result;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean createHtmlFile(JSONObject jsonResult) throws IOException {
        boolean result = false;

        File fTmp = null;
        FileInputStream fHTML = null;
        BufferedWriter out = null;
        FileWriter fstream = null;

        try {
            fTmp = new File(qrCodeUploadDir+"iOS/"+jsonResult.get(PayloadKeyType.Project.name()).toString(),jsonResult.get(PayloadKeyType.Project.name()).toString() + ".html"); File.createTempFile(jsonResult.get(PayloadKeyType.Project.name()).toString(),".html"); // 수정해야함...
            // fTmp = File.createTempFile(jsonResult.get("Project").toString(),".html"); // 수정해야함...
            fstream = new FileWriter(fTmp);
            out = new BufferedWriter(fstream);
            out.append("<!DOCTYPE HTML>" + "\n");
            out.append("<html>" + "\n");
            out.append("<head>" + "\n");
            out.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />" + "\n");
            out.append("<meta name=\"viewport\" content=\"user-scalable=no, width=device-width, initial-scale=1.0, maximum-scale=1.0\"/>" + "\n");
            out.append("<meta name=\"apple-mobile-web-app-capable\" content=\"yes\" />" + "\n");
            out.append(" <title>WHybrid to WHive OTA 설치 페이지</title>" + "\n");
            out.append("<style>" + "\n");
            out.append("li {margin: 10px;}" + "\n");
            out.append("</style>" + "\n");
            out.append("</head>" + "\n");
            out.append("<body>" + "\n");
            out.append("<ul>" + "\n");

            // android <a href> 세팅
            if(jsonResult.get(PayloadKeyType.platform.name()).toString().toLowerCase().equals(PayloadKeyType.android.name())){
                out.append("<li><a href=\"app/Android/" + jsonResult.get(PayloadKeyType.Project.name()).toString() + "/" + jsonResult.get(PayloadKeyType.AppName.name()).toString() + "\">WHive Android</a></li>" + "\n");
            }else{
                out.append("<li> WHive Android </li>" + "\n");
            }

            // ios <a href> 세팅
            // url 동적 처리 하게 구현하기
            if(jsonResult.get(PayloadKeyType.platform.name()).toString().toLowerCase().equals("ios")){
                // url 정보 받기
                out.append("<li><a href=\"itms-services://?action=download-manifest&url="+ jsonResult.get("ftpUrl").toString() +"/resource/upload/iOS/"+ jsonResult.get(PayloadKeyType.Project.name()).toString() +"/manifest.plist\">WHive iOS</a></li> " + "\n");
            }else {
                out.append("<li>WHive iOS</li> " + "\n");
            }

            out.append("</ul>" + "\n");
            out.append("</body> " + "\n");
            out.append("</html>" + "\n");

            out.flush();


            fHTML = new FileInputStream(fTmp);

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } finally {
            if (fHTML != null) fHTML.close();
            if (fTmp != null) fTmp.delete();
            if (out != null) out.close();
        }

        // 정상 HTML Upload 처리 확인
        return result;
    }

}
