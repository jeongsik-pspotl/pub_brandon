package com.inswave.whive.branch.controller;


import com.inswave.whive.branch.domain.*;
import com.inswave.whive.branch.service.BuildService;
import com.inswave.whive.branch.util.ServletUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


@RestController
public class BuildController {

    private static final Logger logger = LoggerFactory.getLogger(BuildController.class);

    @Autowired
    private BuildService buildService;

    // tag :: async
    @PostMapping("/build")
    @ResponseBody
    public DeferredResult<BuildResponse> addRequest(@RequestBody BuildParam param) {
        String sessionId = ServletUtil.getSession().getId();
        logger.info(">> Build task add. session id : {}", sessionId);

        final DeferredResult<BuildResponse> deferredResult = new DeferredResult<>(null);

        return deferredResult;
    }

    @GetMapping("/cancel")
    @ResponseBody
    public ResponseEntity<Void> cancelRequest() {
        String sessionId = ServletUtil.getSession().getId();
        logger.info(">> Cancel request. session id : {}", sessionId);

        final BuildRequest user = new BuildRequest(sessionId);
        buildService.cancelBuildProj(user);

        return ResponseEntity.ok().build();
    }

    // -- tag :: async

    // tag :: websocket stomp
    @MessageMapping("/topic/build/{buildTaskId}")
    public void sendMessage(@DestinationVariable("buildTaskId") String buildTaskId, @Payload BuildMessage buildMessage) {
        logger.info("Request message. build task id : {} | build message : {} | principal : {}", buildTaskId, buildMessage);
        if (!StringUtils.hasText(buildTaskId) || buildMessage == null) {
            return;
        }

        if (buildMessage.getMessageType() == MessageType.BUILD) {
            buildService.sendMessage(buildTaskId, buildMessage);
        }
    }

}
