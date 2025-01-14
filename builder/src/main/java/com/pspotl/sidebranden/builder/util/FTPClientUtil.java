package com.pspotl.sidebranden.builder.util;

import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
public class FTPClientUtil {

    // server config setting
    private String saveFilePath = "/var/www/html/ser-stor/webapps/ROOT/app/";

    private String serverIpTemp;
    private int serverPortTemp;
    private String userTemp;
    private String passwordTemp;


    // ftpSetting
    public boolean upload(InputStream insFileObj, String fileName, String platform, String projectPath, JSONObject ftpSettingObj, String bundleID) throws Exception {
        FileInputStream fis = null;
        FTPClient ftpClient = new FTPClient();

        String changeToFileName = "";
        String savePathAll;
        boolean appUpload = false;
        boolean plistUpload = false;
        boolean htmlUpload = false;

        // change list
        // serverIp, serverPort, user, password

        serverIpTemp = ftpSettingObj.get("ftpIP").toString(); // ftpUrl ???
        serverPortTemp = Integer.parseInt(ftpSettingObj.get("ftpPort").toString());
        userTemp = ftpSettingObj.get("ftpUserId").toString();
        passwordTemp = ftpSettingObj.get("ftpUserPwd").toString();

        changeToFileName = fileName;

        try {
            ftpClient.connect(serverIpTemp, serverPortTemp);  //ftp 연결
            ftpClient.setControlEncoding("utf-8");    //ftp 인코딩설정
            int reply = ftpClient.getReplyCode();     //응답코드받기

            if (!FTPReply.isPositiveCompletion(reply)) {  //응답이 false 라면 연결 해제 exception 발생
                ftpClient.disconnect();
                throw new Exception(serverIpTemp+" FTP 서버 연결 실패");
            }


            log.info("platform saveFilePath : {}",saveFilePath + platform);
            ftpClient.setSoTimeout(1000 * 10);   //timeout 설정
            ftpClient.login(userTemp, passwordTemp);     //ftp 로그인
            // changeWorkingDirectory whive -> workspace/projectname
            boolean change = ftpClient.changeWorkingDirectory(saveFilePath+platform+"/"+ projectPath +"/");
            log.info("changeWorkingDirectory change {}", change);

            // 1. 앱 파일 업로드
            if(change){
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE); //파일타입설정
                ftpClient.enterLocalPassiveMode();  //passive 모드 설정 yaml 설정 추가
                savePathAll = saveFilePath+platform + "/" + projectPath;
                appUpload =  ftpClient.storeFile(changeToFileName, insFileObj); //파일 업로드
                log.info("changeWorkingDirectory appUpload {}", appUpload);
            }else {
                ftpClient.changeWorkingDirectory(saveFilePath+platform);
                ftpClient.makeDirectory(projectPath);
                ftpClient.changeWorkingDirectory(saveFilePath+platform+"/"+projectPath+"/");
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE); //파일타입설정
                ftpClient.enterLocalPassiveMode();  //passive 모드 설정 yaml 설정 추가
                savePathAll = saveFilePath+platform + "/" + projectPath;
                appUpload =  ftpClient.storeFile(changeToFileName, insFileObj); //파일 업로드
                log.info("changeWorkingDirectory appUpload {}", appUpload);
            }

            // 2. plist 파일 업로드
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name()) && appUpload){
                plistUpload = createPlistFile(ftpClient, savePathAll, projectPath, changeToFileName, ftpSettingObj, bundleID);
            }

            // 3. html 파일 업로드
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name()) && appUpload && plistUpload){
                htmlUpload = createHtmlFile(ftpClient, savePathAll, projectPath, platform, changeToFileName);
                // log.info("changeWorkingDirectory htmlUpload {}", htmlUpload);
            }else if(platform.toLowerCase().equals(PayloadMsgType.android.name()) && appUpload){
                htmlUpload = createHtmlFile(ftpClient, savePathAll, projectPath, platform, changeToFileName);
                // log.info("changeWorkingDirectory htmlUpload {}", htmlUpload);
            }else if(platform.toLowerCase().equals(PayloadMsgType.windows.name())){
                // windows 업로드 완료 시 변수 세팅
                log.info("changeWorkingDirectory appUpload {}", appUpload);
                htmlUpload = appUpload;
            }

            return htmlUpload;

        } finally {
            if (ftpClient.isConnected()) {
                ftpClient.disconnect();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    // app name, app id, app version
    private boolean createPlistFile(FTPClient ftpClient, String path, String projectPath, String appName, JSONObject ftpSettingObj, String bundleID) throws IOException {
        File fplist = null;
        FileInputStream fisPlist = null;
        String buildAftermanifestPlist = path+"/manifest.plist";
        boolean result = false;
        try {
            fplist = File.createTempFile("manifest",".plist");
            FileWriter fplistStream = new FileWriter(fplist);
            BufferedWriter out = new BufferedWriter(fplistStream);

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
            out.append("<string>"+ftpSettingObj.get("ftpUrl").toString()+"app/iOS/" + projectPath +"/"+ appName + "</string>"+"\n"); // // {project}/{filename} 처리 과정 추가.. https://store.inswave.kr/
            out.append("</dict>"+"\n");
            out.append("</array>"+"\n");
            out.append("<key>metadata</key>"+"\n");
            out.append("<dict>"+"\n");
            out.append("<key>bundle-identifier</key>"+"\n");
            out.append("<string>"+bundleID+"</string>"+"\n"); // bundle id 수정
            out.append("<key>bundle-version</key>"+"\n");
            out.append("<string>1.3</string>"+"\n"); // version 수정 필요
            out.append("<key>kind</key>"+"\n");
            out.append("<string>software</string>"+"\n");
            out.append("<key>title</key>"+"\n");
            out.append("<string>"+appName+"</string>"+"\n"); // 더블유하이브리드
            out.append("</dict>"+"\n");
            out.append("</dict>"+"\n");
            out.append("</array>"+"\n");
            out.append("</dict>"+"\n");
            out.append("</plist>"+"\n");

            out.flush();
            out.close();

            fisPlist = new FileInputStream(fplist);
            ftpClient.changeWorkingDirectory(path);
            result = ftpClient.storeFile("manifest.plist", fisPlist);

        } catch (IOException e) {
                
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } finally {
            if(fisPlist != null) fisPlist.close();
            if(fisPlist != null) fplist.delete();
        }

        // 정상 Plist Upload 처리 확인
        if(result){
            return result;
        }else {
            return false;
        }

    }

    private boolean createHtmlFile(FTPClient ftpClient, String path, String project, String platform, String appName) throws IOException {
        File fTmp = null;
        FileInputStream fHTML = null;
        String buildAfterHTMLFile = "/var/www/html/ser-stor/webapps/ROOT";

        FileWriter fstream = null;
        boolean result = false;
        try {
            fTmp = File.createTempFile(project,".html");
            fstream = new FileWriter(fTmp);
            BufferedWriter out = new BufferedWriter(fstream);
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
            if(platform.toLowerCase().equals(PayloadMsgType.android.name())){
                out.append("<li><a href=\"app/Android/" + project + "/" + appName + "\">WHive Android</a></li>" + "\n");
            }else{
                out.append("<li> WHive Android </li>" + "\n");
            }

            // ios <a href> 세팅
            // url 동적 처리 하게 구현하기
            if(platform.toLowerCase().equals(PayloadMsgType.ios.name())){
                out.append("<li><a href=\"itms-services://?action=download-manifest&url=https://store.inswave.kr/app/iOS/"+ project +"/manifest.plist\">WHive iOS</a></li> " + "\n");
            }else {
                out.append("<li>WHive iOS</li> " + "\n");
            }

            out.append("</ul>" + "\n");
            out.append("</body> " + "\n");
            out.append("</html>" + "\n");

            out.flush();
            out.close();

            fHTML = new FileInputStream(fTmp);
            ftpClient.changeWorkingDirectory(buildAfterHTMLFile);
            result = ftpClient.storeFile(project+".html", fHTML);

        } catch (IOException e) {
                
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } finally {
            if(fHTML != null) fHTML.close();
            if(fTmp != null) fTmp.delete();
        }

        // 정상 HTML Upload 처리 확인
        if(result){
            return result;
        }else {
            return false;
        }
    }

}
