package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.GeneralAppStatusMsg;
import com.pspotl.sidebranden.builder.domain.ServerConfigListStatusMsg;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.io.*;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
public class GeneraliOSAppService extends BaseService {
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    @Async("asyncThreadPool")
    public void getGeneralIOSAppInfoCLI(WebSocketSession session, String projectPath, Map<String, Object> parseResult) {
        try {
            lock.readLock().lock();

            String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();
            String projectID = parseResult.get(PayloadMsgType.projectID.name()).toString();

            executeGeneralIOSAPPInfoCLI(session, projectPath, hqKey, projectID);
        } catch (Exception e) {
            log.error("Get General iOS App Info CLI Error = {}", e.getMessage(), e);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void executeGeneralIOSAPPInfoCLI(WebSocketSession session, String projectPath, String hqKey, String projectID) {
        String cmd = "wmatrixmanager getconfig --c -p " + projectPath;
        CommandLine commandLine = CommandLine.parse("/bin/sh");
        commandLine.addArgument("-c");
        commandLine.addArgument(cmd, false);

        executeCommonsExec(session, commandLine, hqKey, projectID);
    }

    private void executeCommonsExec(WebSocketSession session, CommandLine cmd, String hqKey, String projectID) {
        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);
        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();
        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is;

        generalAppMessageHandler(session, new GeneralAppStatusMsg(), null, "GETTING INFO", hqKey, "");

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(cmd, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));
            resultHandler.waitFor();
            int exitCode = resultHandler.getExitValue();

            if (exitCode == 0) {

                String iOSSchemeInfo = is.readLine();

                generalAppMessageHandler(session, new GeneralAppStatusMsg(), iOSSchemeInfo, "DONE", hqKey, projectID);
            }

            handler.stop();

        } catch (Exception e) {
            log.error("Get General iOS APP INFO Error in executeCommonsExec = {}", e.getMessage(), e);
        }
    }

    public void generalAppMessageHandler(WebSocketSession session, GeneralAppStatusMsg generalAppStatusMsg, Object data, String msgValue, String hqKey, String projectID) {
        ObjectMapper Mapper = new ObjectMapper();

        generalAppStatusMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        generalAppStatusMsg.setMsgType(ProjectServiceType.HV_MSG_BUILD_GENERAL_APP_INFO.name());
        generalAppStatusMsg.setMessage(msgValue);
        generalAppStatusMsg.setHqKey(hqKey);
        generalAppStatusMsg.setData(data);
        generalAppStatusMsg.setProjectID(projectID);

        Map<String, Object> parseResult = Mapper.convertValue(generalAppStatusMsg, Map.class);

        headQuaterClientHandler.sendMessage(session, parseResult);
    }
}
