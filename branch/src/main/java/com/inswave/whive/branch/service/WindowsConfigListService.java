package com.inswave.whive.branch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inswave.whive.branch.domain.WindowsConfigListMessage;
import com.inswave.whive.branch.enums.PlatformType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.enums.SessionType;
import com.inswave.whive.branch.handler.HeadQuaterClientHandler;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class WindowsConfigListService {

    List<String> list = new ArrayList<String>();
    JSONParser parser = new JSONParser();
    Object obj = null;
    JSONObject resultPublicListJson = null;
    WindowsConfigListMessage windowsConfigListMessage = new WindowsConfigListMessage();

    // 어노테이션
    @Autowired
    HeadQuaterClientHandler headQuaterClientHandler;


    public void getJsonTextPublic(){

        Path path = Paths.get("C:\\W-Matrix\\rel_inswave_dev\\cli\\config\\public.json");
        String resultPublicJson = "";
        try {
            list = Files.readAllLines(path);
        } catch (IOException e) {

        }

        // test public json
        for(String readline : list){
            log.info(readline+"\n");
            resultPublicJson += readline;
        }

        resultPublicJson = resultPublicJson.replaceAll(" ","");
        log.info(resultPublicJson);


        try {
            obj = parser.parse(resultPublicJson);
            resultPublicListJson = (JSONObject) obj;

            windowsConfigListMessageHandler(windowsConfigListMessage, resultPublicListJson);
        } catch (ParseException e) {

        }

    }

    private void windowsConfigListMessageHandler (WindowsConfigListMessage windowsConfigListMessage, JSONObject resultPublicListJson){
        ObjectMapper Mapper = new ObjectMapper();

        windowsConfigListMessage.setMsgType(ProjectServiceType.HV_MSG_WINDOWS_CONFIG_LIST_INFO.name());
        windowsConfigListMessage.setSessType(SessionType.HEADQUARTER.toString());
        windowsConfigListMessage.setPlatform(PlatformType.Windows.toString());
        windowsConfigListMessage.setResultConfigListObj(resultPublicListJson);

        Map<String, Object> parseResult = Mapper.convertValue(windowsConfigListMessage, Map.class);
        headQuaterClientHandler.sendMessage(parseResult);
    }

}
