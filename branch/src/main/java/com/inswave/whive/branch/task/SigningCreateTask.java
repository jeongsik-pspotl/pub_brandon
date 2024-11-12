package com.inswave.whive.branch.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.BuildStatusMessage;
import com.inswave.whive.branch.domain.CreateSiginingMessage;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.service.SigningKeyService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Data
@Component
public class SigningCreateTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BuildTask.class);

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    private Process process;
    private String buildLogs;
    private String signingCreateTaskId;
    private SigningKeyService signingKeyService;

    @Override
    public void run() {
        ObjectMapper Mapper = new ObjectMapper();

        try {
            logger.info("### SigningKey Create START ###");
            BufferedReader stdout = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
            CreateSiginingMessage createSiginingMessage = new CreateSiginingMessage();
            createSiginingMessage.setSessType(PayloadMsgType.HEADQUATER.name());
            createSiginingMessage.setMsgType(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name());
            createSiginingMessage.setStatus("sigining");
            createSiginingMessage.setMessage("CREATING");
            Map<String, Object> parseResult = Mapper.convertValue(createSiginingMessage, Map.class);
            headQuaterClientHandler.sendMessage(parseResult);

            while ((buildLogs = stdout.readLine()) != null) {

                logger.info(" #### SigningCreateTask log data ### : " + buildLogs);

            }
            this.process.getInputStream().close();
            this.process.getOutputStream().close();
            this.process.getErrorStream().close();
            int exitCode = this.process.exitValue();

            if(exitCode == 0){
                logger.info("### SigningKey Create END ### {}", exitCode);
                createSiginingMessage.setSessType(PayloadMsgType.HEADQUATER.name());
                createSiginingMessage.setMsgType(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name());
                createSiginingMessage.setStatus("sigining");
                createSiginingMessage.setMessage(PayloadMsgType.SUCCESSFUL.name());

                parseResult = Mapper.convertValue(createSiginingMessage, Map.class);
                headQuaterClientHandler.sendMessage(parseResult);
            } else if(exitCode == 1){
                logger.info("### SigningKey Create END ### {}", exitCode);
                createSiginingMessage.setSessType(PayloadMsgType.HEADQUATER.name());
                createSiginingMessage.setMsgType(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name());
                createSiginingMessage.setStatus("sigining");
                createSiginingMessage.setMessage(PayloadMsgType.FAILED.name());

                parseResult = Mapper.convertValue(createSiginingMessage, Map.class);
                headQuaterClientHandler.sendMessage(parseResult);
            } else {
                logger.info("### SigningKey Create END ### {}", exitCode);
                createSiginingMessage.setSessType(PayloadMsgType.HEADQUATER.name());
                createSiginingMessage.setMsgType(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name());
                createSiginingMessage.setStatus("sigining");
                createSiginingMessage.setMessage(PayloadMsgType.FAILED.name());

                parseResult = Mapper.convertValue(createSiginingMessage, Map.class);
                headQuaterClientHandler.sendMessage(parseResult);
            }

            this.process.destroy();
            logger.info("### SigningKey Create END ###");
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }


}
