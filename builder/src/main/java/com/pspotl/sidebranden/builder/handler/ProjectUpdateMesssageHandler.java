package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.ProjectServiceType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.UpdateProjectService;
import com.pspotl.sidebranden.builder.task.GitTask;
import com.pspotl.sidebranden.builder.task.SvnTask;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
@Component
public class ProjectUpdateMesssageHandler implements BranchHandlable{

    @Autowired
    UpdateProjectService upateProjectService;

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();

        JSONParser parser = new JSONParser();
        Object obj = null;
        JSONObject repositoryObj = null;

        if(messageType.equals(ProjectServiceType.HV_MSG_PROEJCT_PULL_INFO.name())){

            String platform = parseResult.get(PayloadMsgType.platform.name()).toString();
            // string to json 변환
            String jsonRepository = parseResult.get("jsonRepository").toString();

            try {
                obj = parser.parse(jsonRepository);
                repositoryObj = (JSONObject) obj;
                if (repositoryObj.get("vcsType").equals("git") || repositoryObj.get("vcsType").equals("localgit")) {
                    gitTask.gitPull(parseResult, "", repositoryObj);
                } else if (repositoryObj.get("vcsType").equals("localsvn")) {
                    String localRepo = System.getProperty("user.home") + "/w-hive/builder/builder_main/DOMAIN_" + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/USER_" + parseResult.get(PayloadMsgType.userID.name()).toString() + "/WORKSPACE_W"
                            + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString();
                    svnTask.svnUpdate(new URI(localRepo));
                }
            } catch (ParseException | URISyntaxException e) {
                log.error("Git pull, SVN Update Error = {}", e.getLocalizedMessage(), e);
            }
        }

        if(messageType.equals(ProjectServiceType.HV_MSG_PROJECT_UPDATE_PUSH_GITHUB_INFO.name())){


        }


    }
}
