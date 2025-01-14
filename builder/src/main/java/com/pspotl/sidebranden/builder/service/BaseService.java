package com.pspotl.sidebranden.builder.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import java.io.*;
import java.net.URL;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BaseService{

    public String getClassPathResourcePath(String resourcePath){

        URL url = this.getClass().getClassLoader().getResource(resourcePath);
        log.info("url check {}", url.getPath());

        String shellscriptFileName = url.getPath();

        return shellscriptFileName;
    }

    public void executueCommonsCLIToResult(CommandLine commandLineParse, String commandType) throws IOException {

        PipedOutputStream pipedOutput = new PipedOutputStream();
        ByteArrayOutputStream stdoutResult = new ByteArrayOutputStream();
        ByteArrayOutputStream stdoutErr = new ByteArrayOutputStream();
        PumpStreamHandler handler = new PumpStreamHandler(pipedOutput, stdoutErr, null);

        DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

        DefaultExecutor executor = new DefaultExecutor();

        BufferedReader is = null;
        String tmp = "";

        try {
            handler.start();
            executor.setStreamHandler(handler);
            executor.execute(commandLineParse, resultHandler);

            is = new BufferedReader(new InputStreamReader(new PipedInputStream(pipedOutput)));

            while ((tmp = is.readLine()) != null) {
                log.info(" #### Gradle git clone CLI CommandLine log data ### : " + tmp);
            }

            resultHandler.waitFor();

            int exitCode = resultHandler.getExitValue();
            if(exitCode == 0){
                // send message type
                // projectCreateMessage(projectGitCloneMessage,"","DONE");

            }else if(exitCode == 1){

            }else {
//                log.info(" exitCode : {}", exitCode);
            }

            handler.stop();

        } catch (IOException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } catch (InterruptedException e) {
            log.warn(e.getMessage(), e); //error로그는 필요시 사용하는 곳 에서 catch후 사용하세요
        } finally {
            if(is != null) is.close();
        }
    }

    public byte[] convertIntToByteArray(int value) {
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(value >> 24);
        byteArray[1] = (byte)(value >> 16);
        byteArray[2] = (byte)(value >> 8);
        byteArray[3] = (byte)(value);
        return byteArray;
    }

    public <T> JsonObject getJsonKeyValueToObjectResult(JsonObject jsonRequest){

        JsonElement element = JsonParser.parseString(jsonRequest.toString());
        JsonObject obj = element.getAsJsonObject();
        JsonObject resultObj = null;
        Set<Map.Entry<String, JsonElement>> entries = obj.entrySet();

        for (Map.Entry<String, JsonElement> entry: entries) {
            log.info(entry.getKey());
            // log.info(String.valueOf(entry.getValue()));
            if(entry.getValue() instanceof JsonObject){
                log.info("json object key value ");
                log.info(String.valueOf(entry.getValue()));
                // return JsonObject..
                resultObj = entry.getValue().getAsJsonObject();
            }else {
                log.info("string object key value ");
                log.info(String.valueOf(entry.getValue()));
            }
        }
        log.info(resultObj.toString());
        return resultObj;
    }
}
