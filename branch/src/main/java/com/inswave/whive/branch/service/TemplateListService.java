package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.BuildStatusMessage;
import com.inswave.whive.branch.domain.TemplateMessage;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class TemplateListService extends BaseService {

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    // String 대신 추가 vo 객체로 받아서 처리하는 방식으로 구현해보기
    private static Map<WebSocketSession, String> websocketSessions = new ConcurrentHashMap<WebSocketSession, String>();

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    private WebSocketSession sessionTemp = null;

    // 1. parameter 받기
    @Async("asyncThreadPool")
    public void startTemplateList(WebSocketSession session,Map<String, Object> parseResult){

        // platform 값 받아서 처리하기
        // product type 기준으로 path 변수 값 변경하기
        String path;
        if(parseResult.get("product_type").toString().toLowerCase().equals("wmatrix")){
            path = "template/wmatrix/" + parseResult.get(PayloadMsgType.platform.name()).toString();
        }else if(parseResult.get("product_type").toString().toLowerCase().equals("whybrid")){
            path = "template/whybrid/" + parseResult.get(PayloadMsgType.platform.name()).toString();
        }else {
            path = "";
        }

        String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();
        log.info(" startTemplateList session id {}, hqKey {} ",session,hqKey);
        // session key 저장
        sessionTemp = session;

        // mac cli 명령어 shell script 실행, userRootPath+/template 저장 path
        // 아래의 cli 호출 소스 코드
        String shellscriptFileName = getClassPathResourcePath("projectTemplate.sh");
        log.info(shellscriptFileName);
        // 2. commons cli 변수 처리
        CommandLine commandLineTemplateList = CommandLine.parse(shellscriptFileName);
        commandLineTemplateList.addArgument("getTemplateVersion");
        commandLineTemplateList.addArgument(systemUserHomePath + userRootPath + path);

        excuteCopyCommandAction(commandLineTemplateList, hqKey);

    }

    // 3. exec 호출 및 처리
    private void excuteCopyCommandAction(CommandLine commandLineTemplateList, String hqKey){
        TemplateMessage templateMessage = new TemplateMessage();

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        BufferedReader is = null;

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineTemplateList, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            String tmp = null;


            // result filename 결과물 처리 받고
            JSONObject templateVersionObj = new JSONObject();
            ArrayList<String> templateListVector = new ArrayList<>();
            ArrayList<String> templateListVersionTemp = new ArrayList<>();

            // template 버전만 스캐닝 하기
            while ((tmp = is.readLine()) != null)
            {
                log.info(" #### Gradle Clean Build CommandLine log data ### : " + tmp);
                String[] templateList = tmp.split("\\s");

                for ( int a = 0;a < templateList.length;a++){
                  String[] templateVersion = templateList[a].split("\\]\\[");
                  for (int b = 0; b < templateVersion.length; b++){
                      String resultTemplate = "";
                      if(b == 0){
                          resultTemplate = templateVersion[b];
                          resultTemplate = resultTemplate.replace("WHybridTemplate[","");
                          resultTemplate = resultTemplate.replace("WHybrid_Template[","");
                          resultTemplate = resultTemplate.replace("WMatrix_Template[","");
                          resultTemplate = resultTemplate.replace("WMatrixTemplate[","");
                          templateListVector.add(resultTemplate);

                      }

                      if(b == 3 || b == 2){
                          resultTemplate = templateVersion[b];
                          resultTemplate = resultTemplate.replace("]","");
                      }

                  }

                }

                for(String templateVersion: templateListVector){
                    if(!templateListVersionTemp.contains(templateVersion)){
                        templateListVersionTemp.add(templateVersion);
                    }
                }

                templateVersionObj.put("templateList",templateListVersionTemp);

            }
            resultHandler.waitFor();
            is.close();
            int exitCode = resultHandler.getExitValue();

            log.info(" exitCode : {}", exitCode);

            if(exitCode == 0){
                // 4. 처리 완료 이후 list 값 가지고 send message 처리
                tempalateVersionMessageHandler(templateMessage, "", PayloadMsgType.SUCCESSFUL.name(), hqKey, templateVersionObj);

            } else if(exitCode == 1){

            } else {

            }

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            try {
                throw new Exception(e.getMessage(), e);
            } catch (Exception exception) {

            }
        }
    }


    // [buildLogsValue, resultURLObj, resultBuildFileObj] json obj 묶기.
    // session 파리미터 추가하기
    private void tempalateVersionMessageHandler(TemplateMessage templateMessage, String status, String messageValue, String hqKey, JSONObject templateVersionObj){
        ObjectMapper Mapper = new ObjectMapper();

        templateMessage.setSessType(PayloadMsgType.HEADQUATER.name());
        templateMessage.setMsgType(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_LIST_INFO.name());
        templateMessage.setStatus(status);
        templateMessage.setMessage(messageValue);
        templateMessage.setHqKey(hqKey);

        // 세션 관리 기능 추가

        // build log file
        if(messageValue.equals(PayloadMsgType.SUCCESSFUL.name()) || messageValue.equals(PayloadMsgType.FAILED.name())){
            templateMessage.setTemplateVersionList(templateVersionObj);
        }

        Map<String, Object> parseResult = Mapper.convertValue(templateMessage, Map.class);
        headQuaterClientHandler.sendMessage(sessionTemp, parseResult);
    }

    private static WebSocketSession getHqKeyToSession(String hqKey) {
        WebSocketSession session = null;

        for(WebSocketSession _session : websocketSessions.keySet()) {

            log.info("check WebSocketSession session key {} ",  _session);
            String hqKeyTemp = websocketSessions.get(_session);
            if(hqKeyTemp == null) {
                continue;
            }else if(hqKeyTemp.equals(hqKey)){
                session = _session;

            }

        }

        return session;

    }
    // 부가처리 상태 값 처리가 필요하거나 시간이 좀 걸리면 status 메시지 송신 처리기능 추가..

}
