package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.domain.DeployMode;
import com.inswave.whive.branch.enums.BuilderDirectoryType;
import com.inswave.whive.branch.enums.DeployServiceType;
import com.inswave.whive.branch.enums.PayloadMsgType;
import com.inswave.whive.branch.enums.SessionType;
import com.inswave.whive.branch.service.DeployService;
import com.inswave.whive.branch.service.DeploySettingInitService;
import com.inswave.whive.branch.service.MobileTemplateConfigService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;

@Slf4j
@Component
public class DeployCLIMessageHandler implements BranchHandlable {

    @Autowired
    DeployService deployService;

    @Autowired
    DeploySettingInitService deploySettingInitService;

    @Autowired
    private MobileTemplateConfigService mobileTemplateConfigService;

    @Value("${whive.distribution.UserRootPath}")
    private String userRootPath;

    private String systemUserHomePath = System.getProperty("user.home");

    JSONObject branchObj = null;
    JSONObject configObj = null;
    JSONParser branchParser = new JSONParser();

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String deployPlatform = parseResult.get(PayloadMsgType.platform.name()).toString();

        String domainPath = parseResult.get(PayloadMsgType.domain_id.name()).toString();
        String userPath = parseResult.get(PayloadMsgType.user_id.name()).toString();
        String workspacePath = parseResult.get(PayloadMsgType.workspace_id.name()).toString();
        String projectPath = parseResult.get(PayloadMsgType.project_id.name()).toString();
        String workspaceName = parseResult.get("workspaceName").toString();
        String buildToPath = systemUserHomePath + userRootPath + "builder_main/" + BuilderDirectoryType.DOMAIN_ + domainPath + "/" + BuilderDirectoryType.USER_ + userPath + "/"
                + BuilderDirectoryType.WORKSPACE_W + workspacePath + "/" + BuilderDirectoryType.PROJECT_P + projectPath + "/" + BuilderDirectoryType.PROJECT_P + projectPath;

        // 배포 전, 배포용 인증서를 나중에 등록한 사용자를 위해서
        // 배포설정을 한번 더 해준다
        deploySettingInitService.deploySettingInit(session, parseResult, buildToPath);

        if(messageType.equals(DeployServiceType.HV_MSG_DEPLOY_ALPHA_INFO.name())){

            // 동적 처리 로 수정하기
            // android ios 둘다 변경하기
            // domain user workspace project 기준으로 수정하기
            String projectName = "Web_Top";
            String jsonBranchObj = parseResult.get(PayloadMsgType.branchSettingObj.name()).toString();
            String projectDir = "";// parseResult.get("buildProjectdir").toString();
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();
            String hqKey = parseResult.get(PayloadMsgType.hqKey.name()).toString();
            String appVersionCode = parseResult.get(PayloadMsgType.AppVersionCode.name()).toString();
            String debugPlatform = parseResult.get(PayloadMsgType.platform.name()).toString();

            try {

                // branch id, name 체크
                Object bObj = branchParser.parse(jsonBranchObj);
                branchObj = (JSONObject) bObj;

                // setConfig JSON Data set
                configObj = (JSONObject) branchParser.parse(jsonApplyConfig);

                log.info("======================== HV_MSG_DEPLOY_ALPHA_INFO  ======================");

                if(branchObj.get(PayloadMsgType.branchUserId.name()).toString() == null){
                    return;
                }

                if(branchObj.get(PayloadMsgType.branchName.name()).toString() == null){
                    return;
                }

                if ((workspaceName != null && projectName != null ) && !deployPlatform.toLowerCase().equals("windows")) {

                    // template version setting
                    if(deployPlatform.toLowerCase().equals(PayloadMsgType.android.name())){
                        // version code 변경 작업 진행...
                         // mobileTemplateConfigService.templateConfigCLI(debugPlatform, buildToPath, appVersionCode);

                        // apk파일 설정 service 생성하기
                        deploySettingInitService.deployDotENVConfigSettingCLI(session,parseResult,buildToPath);
                    } else {
                        // ios version code 수정 할때 전체 값을 받아서 처리해야함.
                         // mobileTemplateConfigService.setiOSBuildBeforeAppVersionCodeSetValueCLI(session, parseResult, debugPlatform, buildToPath, BuilderDirectoryType.PROJECT_P + projectPath, configObj);

                        // ipa 파일 설정 service 생성하기
                        deploySettingInitService.deployDotENVConfigSettingCLI(session,parseResult,buildToPath);
                    }

                    deployService.startBuild(session, parseResult, DeployMode.ALPHA);
                }


            } catch (ParseException e) {
                    
            }

        } else if(messageType.equals(DeployServiceType.HV_MSG_DEPLOY_BETA_INFO.name())) {
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();

            if(deployPlatform.toLowerCase().equals(PayloadMsgType.android.name())) {

                deploySettingInitService.deployDotENVConfigSettingCLI(session,parseResult,buildToPath);

            } else {

                deploySettingInitService.deployDotENVConfigSettingCLI(session,parseResult,buildToPath);

            }

            deployService.startBuild(session, parseResult, DeployMode.BETA);

        } else if(messageType.equals(DeployServiceType.HV_MSG_DEPLOY_REAL_DEPLOY_INFO.name())) {
            String jsonApplyConfig = parseResult.get("jsonApplyConfig").toString();

            if (deployPlatform.toLowerCase().equals(PayloadMsgType.android.name())){

                deploySettingInitService.deployDotENVConfigSettingCLI(session,parseResult,buildToPath);

            } else {

                deploySettingInitService.deployDotENVConfigSettingCLI(session,parseResult,buildToPath);
            }
            deployService.startBuild(session, parseResult, DeployMode.DEPLOY);
        }
    }
}
