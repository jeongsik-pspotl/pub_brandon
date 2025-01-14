package com.pspotl.sidebranden.builder.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pspotl.sidebranden.builder.domain.BuildRequest;
import com.pspotl.sidebranden.builder.domain.BuildResponse;
import com.pspotl.sidebranden.builder.domain.DeploySettingMsg;
import com.pspotl.sidebranden.builder.enums.DeployServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.handler.HeadQuaterClientHandler;
import com.pspotl.sidebranden.builder.task.DeployMeataDataTask;
import com.pspotl.sidebranden.builder.task.DeployTask;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
@Service
public class DeployTaskService {

    private Map<BuildRequest, DeferredResult<BuildResponse>> waitingQueue;
    private Map<String, String> taskList;
    private ReentrantReadWriteLock lock;
    private ReentrantLock reentrantLock = new ReentrantLock();

    @Autowired
    DeployTask deployTask;

    @Autowired
    DeployMeataDataTask deployMeataDataTask;

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    private ObjectMapper Mapper = new ObjectMapper();

    @PostConstruct
    private void setUp() {
        this.waitingQueue = new LinkedHashMap<>();
        this.lock = new ReentrantReadWriteLock();
        this.taskList = new ConcurrentHashMap<>();
        log.info("## execute build project request. setUp :  {}[{}]", Thread.currentThread().getName(), Thread.currentThread().getId());
    }


    // TODO Deploy Task 호출 기능 추가
    @Async("asyncThreadPool")
    public void setDeployTaskSetting(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        try {
            log.debug("Current waiting Task : " + waitingQueue.size());
            lock.readLock().lock();
            String platform = parseResult.get("platform").toString();

            // platform 기준 분기 처리
            if(platform.toLowerCase().equals("ios")){
                DeploySettingMsg deploySettingMsg = new DeploySettingMsg();
                deploySettingMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());
                String hqKey = parseResult.get("hqKey").toString();
                deploySetTaskSendMessage(session, deploySettingMsg, hqKey, "setEnv", "");
                JSONObject jsonEnvData = deployTask.getDotEnvDataListSearch(session, parseResult, builderPath);
                log.info(jsonEnvData.toJSONString());
                parseResult.put("jsonEnvData", jsonEnvData.toJSONString());

                deployTask.setDotEnvDataListWrite(session, parseResult, builderPath);

                JSONObject jsonEnvResultData = deployTask.getDotEnvDataListSearch(session, parseResult, builderPath);

                // TODO git push soorin 소스 코드 사용하기
                /**
                 * 추후에 수린씨 git, svn task 작업 왼료 되면 머지 하기...
                 */
                deploySettingMsg.setJsonDeploy(jsonEnvResultData);

                // TODO send msg
                deploySetTaskSendMessage(session, deploySettingMsg, hqKey, "DONE", "");

            }else if(platform.toLowerCase().equals("android")){
                DeploySettingMsg deploySettingMsg = new DeploySettingMsg();
                deploySettingMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());
                String hqKey = parseResult.get("hqKey").toString();
                // TODO send msg
                deploySetTaskSendMessage(session, deploySettingMsg, hqKey, "setEnv", "");
                JSONObject jsonEnvData = deployTask.getDotEnvDataListSearch(session, parseResult, builderPath);
                log.info(jsonEnvData.toJSONString());
                parseResult.put("jsonEnvData", jsonEnvData.toJSONString());

                deployTask.setAndroidDotEnvDataListWrite(session, parseResult, builderPath);

                JSONObject jsonEnvResultData = deployTask.getDotEnvDataListSearch(session, parseResult, builderPath);

                // TODO git push soorin 소스 코드 사용하기
                /**
                 * 추후에 수린씨 git, svn task 작업 왼료 되면 머지 하기...
                 */
                deploySettingMsg.setJsonDeploy(jsonEnvResultData);

                // TODO send msg
                deploySetTaskSendMessage(session, deploySettingMsg, hqKey, "DONE", "");
            }


        } catch (Exception e) {
            log.warn("Exception occur while checking waiting queue", e);
        } finally {
            lock.readLock().unlock();
        }

    }

    @Async("asyncThreadPool")
    public void getDeployTaskDataSearch(WebSocketSession session, Map<String, Object> parseResult, String builderPath){

        DeploySettingMsg deploySettingMsg = new DeploySettingMsg();
        deploySettingMsg.setBuild_id(parseResult.get(PayloadMsgType.projectID.name()).toString());

        String hqKey = parseResult.get("hqKey").toString();
        deployGetTaskSendMessage(session, deploySettingMsg, hqKey, "readdata", "");
        JSONObject jsonEnvData = deployTask.getDotEnvDataListSearch(session, parseResult, builderPath);
        deploySettingMsg.setJsonDeploy(jsonEnvData);
        // TODO : deploy metatdata send msg

        /**
         *  객체 생성 및 vo 객체 세팅 하기
         */
        JSONObject jsonMetaData = deployMeataDataTask.getDeployMetaDataTextSearch(parseResult, builderPath);
        log.info(jsonMetaData.toJSONString());
        deploySettingMsg.setJsonDeployMetaData(jsonMetaData);


        // TODO send msg
        deployGetTaskSendMessage(session, deploySettingMsg, hqKey, "DONE", "");

    }

    private void deployGetTaskSendMessage(WebSocketSession session, DeploySettingMsg deploySettingMsg, String hqKeyTemp, String deployStatus, String logMessage){

        deploySettingMsg.setMsgType(DeployServiceType.HV_MSG_DEPLOY_TASK_SEARCH_STATUS_INFO.name());
        deploySettingMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        deploySettingMsg.setHqKey(hqKeyTemp); // hqKey User 아이디

        deploySettingMsg.setStatus(deployStatus);

        deploySettingMsg.setMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(deploySettingMsg, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);


    }

    private void deploySetTaskSendMessage(WebSocketSession session, DeploySettingMsg deploySettingMsg, String hqKeyTemp, String deployStatus, String logMessage){

        deploySettingMsg.setMsgType(DeployServiceType.HV_MSG_DEPLOY_TASK_UPDATE_STATUS_INFO.name());
        deploySettingMsg.setSessType(PayloadMsgType.HEADQUATER.name());
        deploySettingMsg.setHqKey(hqKeyTemp); // hqKey User 아이디

        deploySettingMsg.setStatus(deployStatus);

        deploySettingMsg.setMessage(logMessage);

        Map<String, Object> parseResult = Mapper.convertValue(deploySettingMsg, Map.class);
        headQuaterClientHandler.sendMessage(session, parseResult);


    }

}
