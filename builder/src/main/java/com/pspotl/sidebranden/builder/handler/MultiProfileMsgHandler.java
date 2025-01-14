package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.*;
import com.pspotl.sidebranden.builder.service.MultiProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Component
public class MultiProfileMsgHandler implements BranchHandlable{

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    @Autowired
    MultiProfileService multiProfileService;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        if(messageType.equals(BuildServiceType.HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO.name())){

            // 새로운 서비스 클래스 생성 해야함 ...
            // multi profile 관련 기능 처리 하는 클래스 생성 하기
            multiProfileService.getAllMultiProfileList(session, parseResult);

        }



    }


}
