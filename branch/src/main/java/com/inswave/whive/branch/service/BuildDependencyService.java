package com.inswave.whive.branch.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Service;

import java.io.*;

@Slf4j
@Service
public class BuildDependencyService extends BaseService {

    /**
     *  ios pod lib dependency 파일 자동 설치 기능
     */
    public void validatePodFileInstall(String path){
        getValidatePodfileAutoinstall(path);
    }

    private void getValidatePodfileAutoinstall(String path) {

        /**
         *  pod 파일 체크 기능 추가하기
         */

        File file = new File(path+"/"+"Podfile");
        if(file.exists()){
            if(!file.isDirectory()){
                String cmdStr = "cd " + path;
                switch (System.getProperty("os.arch").toLowerCase()) {
                    case "x86_64":
                        cmdStr += " && arch -X86_64 pod install --repo-update";
                        break;
                    case "aarch64":
                        cmdStr += " && pod install --repo-update";
                        break;
                    default:
                        cmdStr += " && pod install --repo-update";
                        break;
                }

                CommandLine commandLineiOSValidatePodFile = null;
                commandLineiOSValidatePodFile = CommandLine.parse("/bin/sh");
                commandLineiOSValidatePodFile.addArgument("-c");
                commandLineiOSValidatePodFile.addArgument(cmdStr, false);

                try {
                    log.info(cmdStr + "l | ios cli start ");
                    executeCommonsExecSetExportOptions(commandLineiOSValidatePodFile);

                } catch (Exception e) {
                    log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
                }
            }

        }else {
            log.info("no such pod file ");
        }

    }

    private void executeCommonsExecSetExportOptions(CommandLine commandLineParse) throws Exception {
        ByteArrayOutputStream stdoutOut = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(stdoutOut, stdoutOut, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);
        try {
            handler.start();

            executor.execute(commandLineParse, resultHandler);
            resultHandler.waitFor();

            Reader reader = new InputStreamReader(new ByteArrayInputStream(stdoutOut.toByteArray()));
            BufferedReader r = new BufferedReader(reader);
            String tmp = null;

            while ((tmp = r.readLine()) != null)
            {
                log.info(" #### Signingkey.. CommandLine log data ### : " + tmp);

            }
            r.close();
            resultHandler.getExitValue();

            handler.stop();

        } catch (Exception e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
            throw new Exception(e.getMessage(), e);
        }

    }

}
