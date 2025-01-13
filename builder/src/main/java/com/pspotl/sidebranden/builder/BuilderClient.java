package com.pspotl.sidebranden.builder;

import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.handler.BranchSocketHandler;
import com.pspotl.sidebranden.builder.service.BuilderReconnectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.concurrent.*;


@Slf4j
@ClientEndpoint
@Component
@Configuration
public class BuilderClient implements ApplicationRunner {

    private static BuilderReconnectService builderReconnectService = null;

    // wsClient session .
    public   WebSocketSession session;
    private  StandardWebSocketClient wsClient;
    private  WebSocketContainer container;
    private  WebSocketHttpHeaders headers;

    public ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public ScheduledFuture<?> scheduledFuture;

    private boolean status = false;

    @Value("${websocketaddress}")
    private String serverAddress;

    private String BuilderClientAddress;

    @Value("${server.port}")
    private Integer branchPort;
    private static String port;

    @Value("${whive.branch.name}")
    private String branchName;

    @Value("${whive.branch.id}")
    private String userId;

    @Value("${whive.branch.position}")
    private String branchPosition;


    public static BuilderClient createBuilderClient() {
        return new BuilderClient();
    }

    @PostConstruct
    public void setClient(){
//        builderReconnectService = new BuilderReconnectService();
        this.headers = new WebSocketHttpHeaders();
        try {
            BuilderClientAddress = InetAddress.getLocalHost().getHostAddress();
            port = branchPort.toString();
        } catch (UnknownHostException e) {

            log.error("builder message host exception ",e);
        }
        log.info("serverAddress data : {}",this.serverAddress);
        log.info("BuilderClientAddress : {}, branchPort : {}",BuilderClientAddress, port);

        scheduledFuture = scheduler.scheduleAtFixedRate(
                () -> connectStatus(session, status, userId),
                1, 1, TimeUnit.MINUTES
        );
    }

    @Bean
    public BranchSocketHandler getBranchSocketHandler(){
        return new BranchSocketHandler();
    }

    public boolean setStatus(boolean setStatus){
        status = setStatus;
        return status;
    }

    public BuilderClient(){
        container = ContainerProvider.getWebSocketContainer();
        this.container.setAsyncSendTimeout(30000);
        this.container.setDefaultMaxTextMessageBufferSize(67108864);
        this.container.setDefaultMaxBinaryMessageBufferSize(67108864);
        // this.container.connectToServer()

    }


    public void connect() {

        this.wsClient = new StandardWebSocketClient(this.container);

        if(wsClient == null){
            return;
        }

        String payload = "{\"MsgType\": \""+ BuildServiceType.HV_MSG_BRANCH_WEBSOCKET_CONNECTION_INFO.name() + "\""+
                ",\"sessType\":\"" + SessionType.BRANCH.name() +"\"" +
                ",\"name\": \"" + branchName +
                "\",\"position\": \"" + branchPosition +
                "\", \"userId\": \"" + userId + "\"}";

        boolean checkYn = connectToHeadquarter(wsClient, payload);
        setStatus(checkYn);

        log.info("this.serverAddress run method data check.. : {}",this.serverAddress);
    }


    private boolean connectToHeadquarter(StandardWebSocketClient wsClient, String payload){

        try {
            // add handshake
            session = wsClient.doHandshake(getBranchSocketHandler(), this.headers, new URI(this.serverAddress)).get();

            if(session != null){
                try {
                    synchronized(session) {
                        session.sendMessage(new TextMessage(payload));
                    }
                } catch (IOException ex) {

                    log.error("builder message connectToHeadquarter exception ",ex);
                }

            }

            log.info("BuilderClient |run| session data {} ", session);
        } catch (InterruptedException | ExecutionException | URISyntaxException ex) {
            log.debug("\"ExecutionException  check.. : {} \"", ex.getMessage());

            log.error("builder message connectToHeadquarter exception ",ex);
        }

        return true;
    }

    public void connectStatus(WebSocketSession session, boolean checkYn, String userId){

        // userId name
        String payload = "{\"MsgType\": \""+ BuildServiceType.HV_MSG_BRANCH_SESSID_INFO.name() + "\""+
                ",\"sessType\":\"" + SessionType.BRANCH.name() +"\",\"userId\":\""+userId+"\"}";

        try {
            if (session != null && session.isOpen()) {
                synchronized (session) {
                    log.info("check");
                    session.sendMessage(new TextMessage(payload));
                }
            } else {
                log.info("trying connect");
                connect();
            }
        } catch(Exception e) {
            connect();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        connect();
    }
}
