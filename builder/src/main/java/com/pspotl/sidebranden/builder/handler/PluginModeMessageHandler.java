package com.pspotl.sidebranden.builder.handler;

import com.pspotl.sidebranden.builder.domain.PluginMode;
import com.pspotl.sidebranden.builder.enums.BuildServiceType;
import com.pspotl.sidebranden.builder.enums.PayloadMsgType;
import com.pspotl.sidebranden.builder.enums.SessionType;
import com.pspotl.sidebranden.builder.service.PluginService;
import com.pspotl.sidebranden.builder.task.GitTask;
import com.pspotl.sidebranden.builder.task.SvnTask;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class PluginModeMessageHandler implements BranchHandlable{

    @Autowired
    PluginService pluginService;

    @Autowired
    private GitTask gitTask;

    @Autowired
    private SvnTask svnTask;

    @Override
    public void handle(WebSocketSession session, Map<String, Object> parseResult) {

        String messageType = parseResult.get(SessionType.MsgType.name()).toString();
        String pluginPlatform = parseResult.get(PayloadMsgType.platform.name()).toString();

        JSONParser parser = new JSONParser();
        Object obj = null;
        JSONObject repositoryObj = null;

        if(pluginPlatform.toLowerCase().equals(PayloadMsgType.android.name())){

            // git pull obj
            String jsonRepository = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();

            if(messageType.equals(BuildServiceType.HV_MSG_PLUGIN_LIST_INFO.name())) {

                String platform = parseResult.get(PayloadMsgType.platform.name()).toString();

                try {
                    obj = parser.parse(jsonRepository);
                    repositoryObj = (JSONObject) obj;

                    if(repositoryObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("git")){
                        // git pull
                        //updateProjectService.updateGitPullCLI(session, platform, parseResult, repositoryObj); // 석재
                        gitTask.gitPull(parseResult, "", repositoryObj);

                    }else if(repositoryObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("svn")){
                        // updateProjectService.updateSVNUpdateCLI(session, platform, parseResult, repositoryObj); // 삭제
                        String localRepo = System.getProperty("user.home") + "/w-hive/builder/builder_main/DOMAIN_" + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/USER_" + parseResult.get(PayloadMsgType.userID.name()).toString() + "/WORKSPACE_W"
                                + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString();
                        try {
                            svnTask.svnUpdate(new URI(localRepo));
                        } catch (Exception e) {
                            log.error("SVN Update Error = {}", e.getLocalizedMessage(), e);
                        }
                    }


                    pluginService.startPluginList(session, parseResult, PluginMode.PULGIN_LIST);

                } catch (ParseException e) {

                    log.error("builder plugin mode msg hanlder ",e);
                }
            }

        } else if (pluginPlatform.toLowerCase().equals(PayloadMsgType.repositoryVcsType.name())){
            if(messageType.equals(BuildServiceType.HV_MSG_PLUGIN_LIST_INFO.name())){
                pluginService.startWindowsPluginList(session, parseResult, PluginMode.PULGIN_LIST);
            }

        } else {

            // git pull obj
            String jsonRepository = parseResult.get(PayloadMsgType.repositoryObj.name()).toString();

            if(messageType.equals(BuildServiceType.HV_MSG_PLUGIN_LIST_INFO.name())){
                String platform = parseResult.get(PayloadMsgType.platform.name()).toString();

                try {
                    obj = parser.parse(jsonRepository);
                    repositoryObj = (JSONObject) obj;

                    if(repositoryObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("git")){
                        // git pull
                        //updateProjectService.updateGitPullCLI(session, platform, parseResult, repositoryObj);
                        gitTask.gitPull(parseResult, "", repositoryObj);

                    }else if(repositoryObj.get(PayloadMsgType.repositoryVcsType.name()).toString().equals("svn")){
                        // svn update
                        // updateProjectService.updateSVNUpdateCLI(session, platform, workspacePath, projectPath, repositoryObj); // 삭제
                        String localRepo = System.getProperty("user.home") + "/w-hive/builder/builder_main/DOMAIN_" + parseResult.get(PayloadMsgType.domainID.name()).toString() + "/USER_" + parseResult.get(PayloadMsgType.userID.name()).toString() + "/WORKSPACE_W"
                                + parseResult.get(PayloadMsgType.workspaceID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString() + "/PROJECT_P" + parseResult.get(PayloadMsgType.projectID.name()).toString();
                        try {
                            svnTask.svnUpdate(new URI(localRepo));
                        } catch (Exception e) {
                            log.error("SVN Update Error = {}", e.getLocalizedMessage(), e);
                        }
                    }

                    pluginService.startPluginList(session, parseResult, PluginMode.PULGIN_LIST);

                } catch (ParseException e) {

                    log.error("builder plugin mode msg hanlder ",e);
                }
            }
        }



    }


}
