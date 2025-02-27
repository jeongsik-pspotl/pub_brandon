package com.pspotl.sidebranden.builder.service;

import com.pspotl.sidebranden.builder.BuilderClient;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BuilderReconnectService {

    @Autowired
    BuilderClient builderClient;
    private boolean status = false;


    @Async
    public void connectStatus(WebSocketSession session, boolean checkYn, String userId){

        // userId name
        String payload = "{\"MsgType\": \""+ BuildServiceType.HV_MSG_BRANCH_SESSID_INFO.name() + "\""+
                ",\"sessType\":\"" + SessionType.BRANCH.name() +"\",\"userId\":\""+userId+"\"}";

        status = checkYn;
        while( status ) {
            try {
                if(session != null && session.isOpen()){
                    TimeUnit.MINUTES.sleep((long)1); // 1분간격으로 통신 상태 확인 하기
                    synchronized (session) {
                        session.sendMessage(new TextMessage(payload));
                    }
                }else{

                    builderClient.scheduledFuture.cancel(true); // 작업 취소

                    builderClient.scheduler = Executors.newScheduledThreadPool(1);
                    // 새로운 스케줄링 작업 예제
                    builderClient.scheduledFuture = builderClient.scheduler.scheduleAtFixedRate(
                            () -> connectStatus(session, status, userId),
                            1, 1, TimeUnit.MINUTES
                    );
                }

            } catch(IllegalStateException e){
                log.info(e.getMessage(),e);
                return;
            } catch (InterruptedException e) {
                log.info(e.getMessage(),e);
                // log.error("builder message connectStatus exception ",e);
            } catch (IOException e) {
                log.info(e.getMessage(),e);
                // log.error("builder message connectStatus exception ",e);
            } catch (NullPointerException e){
                log.info(e.getMessage(),e);
            }

        }
        
    }


}
