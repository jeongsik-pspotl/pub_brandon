package com.inswave.whive.branch.task;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.BuildStatusMessage;
import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import com.inswave.whive.branch.service.BuildService;
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
public class BuildTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(BuildTask.class);

    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;

    private Process process;
    private String buildLogs;
    private String buildTaskId;
    private BuildService buildService;

    @Override
    public void run() {
        try {
            logger.info("### BUILD START ###");
            BufferedReader stdout = new BufferedReader(new InputStreamReader(this.process.getInputStream()));
            while ((buildLogs = stdout.readLine()) != null) {

                ObjectMapper Mapper = new ObjectMapper();

                BuildStatusMessage buildStatusMessage = new BuildStatusMessage();
                buildStatusMessage.setMsgType(BuildServiceType.HV_MSG_BUILD_STATUS_INFO.name());
                buildStatusMessage.setSessType(PayloadMsgType.HEADQUATER.name());
                buildStatusMessage.setStatus("building");
                buildStatusMessage.setMessage(buildLogs);

                logger.info(" #### BuildTask log data ### : " + buildLogs);
                Map<String, Object> parseResult = Mapper.convertValue(buildStatusMessage, Map.class);
                headQuaterClientHandler.sendMessage(parseResult);

            }
            this.process.getInputStream().close();
            this.process.getOutputStream().close();
            this.process.getErrorStream().close();
            this.process.destroy();
            logger.info("### BUILD END ###");
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

}