package com.inswave.whive.branch.handler;

import com.inswave.whive.branch.enums.BuildServiceType;
import com.inswave.whive.branch.enums.DeployServiceType;
import com.inswave.whive.branch.enums.ProjectServiceType;
import com.inswave.whive.branch.enums.SessionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/*
* BuildServiceManager
* Branch Handler 관리 객체
* Msg Type 기준으로, handler 호출
*
*
*
 */

@Slf4j
@Component
public class BuildServiceManager {

    @Autowired
    DebugBuildMessageHandler debugBuildMessageHandler;

    @Autowired
    PluginModeMessageHandler pluginModeMessageHandler;

    @Autowired
    PluginAddMessageHandler pluginAddMessageHandler;

    @Autowired
    PluginRemoveMessageHandler pluginRemoveMessageHandler;

    @Autowired
    SigningKeyAddMessageHandler signingKeyAddMessageHandler;

    @Autowired
    BranchUploadFilePassHandler branchUploadFilePassHandler;

    @Autowired
    BranchUrlConnectionMessageHandler branchUrlConnectionMessageHandler;

    @Autowired
    LogFileDownloagMsgHandler logFileDownloagMsgHandler;

    @Autowired
    ProjectImportMessageHandler projectImportMessageHandler;

    @Autowired
    ProjetCreateMsgHandler projetCreateMsgHandler;

    @Autowired
    AppIconImageLoadMsgHandler appIconImageLoadMsgHandler;

    @Autowired
    WindowsConfigListMessageHandler windowsConfigListMessageHandler;

    @Autowired
    ProjectUpdateMesssageHandler projectUpdateMesssageHandler;

    @Autowired
    DeployCLIMessageHandler deployCLIMessageHandler;

    @Autowired
    ProjectExportSourceFileMessageHandler projectExportSourceFileMessageHandler;

    @Autowired
    ProjectTemplateVersionListMsgHandler projectTemplateVersionListMsgHandler;

    @Autowired
    ProjectTemplateCreateMsgHandler projectTemplateCreateMsgHandler;

    @Autowired
    TemplatePluginListMsgHandler templatePluginListMsgHandler;

    @Autowired
    TemplatePluginAddMsgHandler templatePluginAddMsgHandler;

    @Autowired
    TemplatePluginRemoveMsgHandler templatePluginRemoveMsgHandler;

    @Autowired
    TemplatePluginsMsgHandler templatePluginsMsgHandler;

    @Autowired
    ProjectServerConfigListMsgHandler projectServerConfigListMsgHandler;

    @Autowired
    ProjectiOSAppConfigInfoMsgHandler projectiOSAppConfigInfoMsgHandler;

    @Autowired
    DeploySettingInitMsgHandler deploySettingInitMsgHandler;

    @Autowired
    ProjectAllGetInformationMsgHandler projectAllGetInformationMsgHandler;

    @Autowired
    MultiProfileMsgHandler multiProfileMsgHandler;

    @Autowired
    DeployTaskMsgHandler deployTaskMsgHandler;

    @Autowired
    DeployMetadataMsgHandler deployMetadataMsgHandler;

    @Autowired
    GeneralAppProjectCreateMsgHandler generalAppProjectCreateMsgHandler;

    @Autowired
    GeneralAppInfoGetMsgHandler generalAppInfoGetMsgHandler;

    @Autowired
    GeneralAppBuildMsgHandler generalAppBuildMsgHandler;

    private static JsonParser parser = JsonParserFactory.getJsonParser();

    private static final Map<String, BranchHandlable> branchHandlableMap =
            Collections.synchronizedMap(new HashMap<String, BranchHandlable>());

    @PostConstruct
    public void setServiceManager(){
        /* branch build script 실행 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_DEBUG_BUILD.toString(), debugBuildMessageHandler);
        branchHandlableMap.put(BuildServiceType.HV_MSG_RELEASE_BUILD.toString(), debugBuildMessageHandler);
        branchHandlableMap.put(BuildServiceType.HV_MSG_RELEASE_BUILD_FROM_BRANCH.toString(), debugBuildMessageHandler);
        branchHandlableMap.put(BuildServiceType.HV_MSG_AAB_DEBUG_BUILD.toString(), debugBuildMessageHandler);
        branchHandlableMap.put(BuildServiceType.HV_MSG_AAB_RELEASE_BUILD.toString(), debugBuildMessageHandler);
        branchHandlableMap.put(BuildServiceType.HV_MSG_MULTI_PROFILE_IOS_BUILD.toString(), debugBuildMessageHandler);
        // HV_MSG_MULTI_PROFILE_IOS_BUILD

        /* plugin list cli 실행 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_PLUGIN_LIST_INFO.name(), pluginModeMessageHandler);
        /* plugin add cli 실행 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_PLUGIN_ADD_LIST_INFO.name(), pluginAddMessageHandler);
        /* plugin remove cli 실행 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_PLUGIN_REMOVE_LIST_INFO.name(), pluginRemoveMessageHandler);

        /* signin key add cli 실행 메시지 */
        branchHandlableMap.put(BuildServiceType.MV_MSG_SIGNIN_KEY_ADD_INFO.name(), signingKeyAddMessageHandler);

        /* project import zip file cli 실행 메시지 */
        // branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_IMPORT_START_INFO.name(), projectImportMessageHandler);

        /* project create mkdir cli 실행 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_CREATE_INFO_FROM_BRANCH.toString(), projetCreateMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO_FROM_BRANCH.toString(), projetCreateMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name(), projetCreateMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_WORKSPACE_CREATE_INFO_FROM_BRANCH.toString(), projetCreateMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_IMPORT_ZIP_UPLOAD_INFO.name(), projetCreateMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_APP_CONFIG_INFO.name(), projetCreateMsgHandler);
        /* builder multi profile project create mkdir cli 실행 메시지*/
        // HV_MSG_PROJECT_MULTI_PROFILE_VCS_CLONE_INFO
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_MULTI_PROFILE_VCS_CLONE_INFO.name(), projetCreateMsgHandler);

        /* project update pull cli 실행 메시지   */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROEJCT_PULL_INFO_FROM_BRANCH.toString(), projectUpdateMesssageHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROEJCT_PULL_INFO.name(), projectUpdateMesssageHandler);

        /* branch connect action 호출 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_CONNETCION_CHECK_INFO.name(), branchUrlConnectionMessageHandler);

        /* branch log file download 호출 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_LOGFILE_DOWNLOAD_INFO.name(), logFileDownloagMsgHandler);
        branchHandlableMap.put(BuildServiceType.HV_MSG_LOGFILE_DOWNLOAD_INFO_FROM_BRANCH.toString(), logFileDownloagMsgHandler);

        /* branch app icon image load 명령 호출 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_APPICON_IMAGE_LOAD_INFO.name(), appIconImageLoadMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_APPICON_IMAGE_LOAD_INFO_FROM_BRANCH.toString(), appIconImageLoadMsgHandler);

        /* HV_MSG_WINDOWS_CONFIG_LIST_INFO */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_WINDOWS_CONFIG_LIST_INFO.name(), windowsConfigListMessageHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_WINDOWS_CONFIG_LIST_INFO_FROM_BRANCH.toString(), windowsConfigListMessageHandler);

        /* HV_MSG_DEPLOY_ALPHA_INFO */
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_ALPHA_INFO.name(), deployCLIMessageHandler);
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_BETA_INFO.name(), deployCLIMessageHandler);
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_REAL_DEPLOY_INFO.name(), deployCLIMessageHandler);
        // projectExportSourceFileMessageHandler
        branchHandlableMap.put(ProjectServiceType.MV_MSG_PROJECT_EXPORT_ZIP_REQUEST_INFO_BRNACH.toString(), projectExportSourceFileMessageHandler);
        /* branch template version list 조회 호출 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_LIST_INFO.name(), projectTemplateVersionListMsgHandler);
        /* builder template projecet create 호출 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_CREATE_INFO.name(), projectTemplateCreateMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_MULTI_PROFILE_TEMPLATE_CREATE_INFO.name(), projectTemplateCreateMsgHandler);

        /* builder general app project create 호출 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO.name(), generalAppProjectCreateMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_BUILD_GENERAL_APP_INFO.name(), generalAppInfoGetMsgHandler);

        /* general app build */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_GENERAL_ANDROID_DEBUG_BUILD.name(), generalAppBuildMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_GENERAL_ANDROID_RELEASE_BUILD.name(), generalAppBuildMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_GENERAL_ANDROID_AAB_DEBUG_BUILD.name(), generalAppBuildMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_GENERAL_ANDROID_AAB_RELEASE_BUILD.name(), generalAppBuildMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_GENERAL_IOS_DEBUG_BUILD.name(), generalAppBuildMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_GENERAL_IOS_RELEASE_BUILD.name(), generalAppBuildMsgHandler);

        /* branch template plugin list 조회 호출 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_TEMPLATE_PLUGIN_LIST_INFO.name(), templatePluginListMsgHandler);
        /*branch template plugin add 호출 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_TEMPLATE_PLUGIN_ADD_INFO.name(), templatePluginAddMsgHandler);
        /*branch template plugin remove 호출 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_TEMPLATE_PLUGIN_REMOVE_INFO.name(), templatePluginRemoveMsgHandler);
        /* builder template plugin 명령어 호출 메시지 (통합전용) */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_TEMPLATE_PLUGINS_INFO.name(), templatePluginsMsgHandler);

        /* builder template project get Server Config 조회 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO.name(), projectServerConfigListMsgHandler);
        /* builder template project set Server Config 설정 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_TEMPLATE_UPDATE_INFO.name(), projectServerConfigListMsgHandler);
        /* builder get config multiprofile 호출 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO.name(), projectServerConfigListMsgHandler);
        /* builder set config multiprofile 호출 메시지 */
        branchHandlableMap.put(BuildServiceType.HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO.name(), projectServerConfigListMsgHandler);

        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_UPDATE_VCS_MULTI_PROFILE_CONFIG_UPDATE_INFO.name(), projectServerConfigListMsgHandler);

        /* deploy fastlane init cli 실행 handler  */
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_SETTING_INIT_INFO.name(), deploySettingInitMsgHandler);

        /* builder iOS App config info project 조회 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO.name(), projectiOSAppConfigInfoMsgHandler);

        /* builder Android/iOS All GetInformation project 조회 메시지 */
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO.name(), projectAllGetInformationMsgHandler);
        branchHandlableMap.put(ProjectServiceType.HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO.name(), projectAllGetInformationMsgHandler);

        // HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO
        branchHandlableMap.put(BuildServiceType.HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO.name(), multiProfileMsgHandler);

        // deploy task update 호출 메시지
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_TASK_UPDATE_STATUS_INFO.name(), deployTaskMsgHandler);
        // deploy task list 조회 호출 메시지
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_TASK_SEARCH_STATUS_INFO.name(), deployTaskMsgHandler);

        // deploy metadata update 호출 메시지
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO.name(), deployMetadataMsgHandler);

        // deploy metadata image read 호출 메시지 HV_MSG_DEPLOY_METADATA_SEARCH_STATUS_INFO
        branchHandlableMap.put(DeployServiceType.HV_MSG_DEPLOY_METADATA_SEARCH_STATUS_INFO.name(), deployMetadataMsgHandler);

    }

    public void handleTextRequest(WebSocketSession session, TextMessage message) {
        Map<String, Object> parseResult = parser.parseMap(message.getPayload());

        // log.info(String.format("[msgType] : %s", parseResult.get(SessionType.MsgType.name())));

        BranchHandlable branchHandlable = branchHandlableMap.get(parseResult.get(SessionType.MsgType.name()));

        if(branchHandlable == null) {
             log.debug("Not registered command");
        } else {
             branchHandlable.handle(session, parseResult);
        }
    }

    public void handleBinaryRequest(WebSocketSession session, BinaryMessage websocketMessage) {
        log.info(String.format("[ handleBinaryRequest ] [session] : %s", session));
        branchUploadFilePassHandler.handle(session, websocketMessage);
    }
}
