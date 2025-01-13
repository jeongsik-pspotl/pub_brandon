package com.pspotl.sidebranden.manager.handler;

import com.pspotl.sidebranden.manager.enums.ClientMessageType;
import com.pspotl.sidebranden.manager.enums.SessionType;
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

@Slf4j
@Component
public class HandlerManager {

    @Autowired
    UserLoginSessionHandler userLoginSessionHandler;

    @Autowired
    LogMessageHandler logMessageHandler;

    @Autowired
    UploadFilePassHandler uploadFilePassHandler;

    @Autowired
    BranchSessionHandler branchSessionHandler;

    @Autowired
    FileSendInfoHandler fileSendInfoHandler;

    @Autowired
    BranchDebugBuildHandler branchDebugBuildHandler;

    @Autowired
    BuildStatusInfoHandler buildStatusInfoHandler;

    @Autowired
    DisconnectHandler disconnectHandler;

    @Autowired
    BranchPluginListHandler branchPluginListHandler;

    @Autowired
    BranchPluginAddHandler branchPluginAddHandler;

    @Autowired
    BranchPluginRemoveHandler branchPluginRemoveHandler;

    @Autowired
    BranchSigingKeyAddHandler branchSigingKeyAddHandler;

    @Autowired
    BranchCreateProjectHandler branchCreateProjectHandler;

    @Autowired
    BranchUrlConnectionHandler branchUrlConnectionHandler;

    @Autowired
    LogFileDownloadHandler logFileDownloadHandler;

    @Autowired
    WindowsConfigListHandler windowsConfigListHandler;

    @Autowired
    BranchUpdateProjectHandler branchUpdateProjectHandler;

    @Autowired
    DeployStatusInfoHandler deployStatusInfoHandler;

    @Autowired
    DeploySettingInitMsgHandler deploySettingInitMsgHandler;

    @Autowired
    HeadquarterProjectExportHandler headquarterProjectExportHandler;

    @Autowired
    HeadquarterProjectTemplateVersionListHandler headquarterProjectTemplateVersionListHandler;

    @Autowired
    HeadquarterProjectTemplateCreateStatusHandler headquarterProjectTemplateCreateStatusHandler;

    @Autowired
    BuilderKeyfileTemplateHandler builderKeyfileTemplateHandler;

    @Autowired
    BuilderServerConfigListHandler builderServerConfigListHandler;

    @Autowired
    BuilderAppConfigListHandler builderAppConfigListHandler;

    @Autowired
    ManagerMultiProfileMsgHandler managerMultiProfileMsgHandler;

    @Autowired
    ManagerGetAllConfigListMsgHandler managerGetAllConfigListMsgHandler;

    @Autowired
    BuilderAppIconStatusHandler builderAppIconStatusHandler;

    @Autowired
    ManagerProjectimportHandler managerProjectimportHandler;

    @Autowired
    ManagerDeployTaskSetingMsgHandler managerDeployTaskSetingMsgHandler;

    @Autowired
    ManagerDeployMetadataSettingMsgHandler managerDeployMetadataSettingMsgHandler;

    @Autowired
    GeneralAppProjectCreateStatusHandler generalAppProjectCreateStatusHandler;

    @Autowired
    GeneralIOSAppGetInfoHandler generalAppGetInfoHandler;

    private static JsonParser parser = JsonParserFactory.getJsonParser();

    private static final Map<String, Handlable> handlerMap =
            Collections.synchronizedMap(new HashMap<String, Handlable>());

    @PostConstruct
    public void setHandler() {

        /*user login 데이터 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_USER_LOGIN_SESSION_INFO.name(), userLoginSessionHandler);

        /*branch webclient session connection 메시지  */
        handlerMap.put(ClientMessageType.HV_MSG_BRANCH_WEBSOCKET_CONNECTION_INFO.name(), branchSessionHandler);

        /* log 데이터 메시지*/
        handlerMap.put(ClientMessageType.HV_MSG_LOG_INFO.name(), logMessageHandler);
        handlerMap.put(ClientMessageType.HV_MSG_LOG_INFO_FROM_BRANCH.toString(), logMessageHandler);

        /* branch, headquater session_id 기준 obj 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_BRANCH_SESSID_INFO.name(), logMessageHandler);
        handlerMap.put(ClientMessageType.HV_MSG_HEADQUATER_SESSID_INFO.name(), logMessageHandler);
        handlerMap.put(ClientMessageType.HV_MSG_BRANCH_SESSID_INFO_FROM_BRANCH.toString(), logMessageHandler);

        handlerMap.put(ClientMessageType.HV_MSG_DISCONNECT.toString(), disconnectHandler);
        handlerMap.put(ClientMessageType.HV_MSG_DISCONNECT_FROM_BRANCH.toString(), disconnectHandler);

        /* plugin list 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_PLUGIN_LIST_INFO.name(), branchPluginListHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PLUGIN_LIST_INFO_FROM_HEADQUATER.name(), branchPluginListHandler);

        /* plugin add 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_PLUGIN_ADD_LIST_INFO.name(), branchPluginAddHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PLUGIN_ADD_LIST_INFO_FROM_HEADQUATER.name(), branchPluginAddHandler);

        /* plugin remove 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_PLUGIN_REMOVE_LIST_INFO.name(), branchPluginRemoveHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PLUGIN_REMOVE_LIST_INFO_FROM_HEADQUATER.name(), branchPluginRemoveHandler);

        handlerMap.put(ClientMessageType.HV_MSG_BUILD_STATUS_INFO_FROM_HEADQUATER.name(), buildStatusInfoHandler);

        /* signing key add cli 호출 메시지 */
        handlerMap.put(ClientMessageType.MV_MSG_SIGNIN_KEY_ADD_INFO.name(), branchSigingKeyAddHandler);
        handlerMap.put(ClientMessageType.MV_MSG_SIGNIN_KEY_ADD_INFO_FROM_HEADQUATER.name(), branchSigingKeyAddHandler);


        handlerMap.put(ClientMessageType.HV_MSG_SEND_FILE_INFO.name(), fileSendInfoHandler);

        /* debug build, release build 스크립트 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_DEBUG_BUILD.toString(), branchDebugBuildHandler);
        handlerMap.put(ClientMessageType.HV_MSG_RELEASE_BUILD.toString(), branchDebugBuildHandler);

        /* project create action 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_CREATE_INFO.name(), branchCreateProjectHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_CREATE_INFO_FROM_HEADQUATER.name(), branchCreateProjectHandler);
        handlerMap.put(ClientMessageType.HV_MSG_WORKSPACE_CREATE_INFO.name(), branchCreateProjectHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO.name(), branchCreateProjectHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_CREATE_GITHUB_CLONE_INFO_FROM_HEADQUATER.name(), branchCreateProjectHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_APP_CONFIG_IMAGE_LIST_INFO_FROM_HEADQUATER.name(), branchCreateProjectHandler);

        /* project export action 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_EXPORT_STATUS_INFO_FROM_HEADQUATER.name(), headquarterProjectExportHandler);
        handlerMap.put(ClientMessageType.MV_HSG_PROJECT_EXPORT_ZIP_DOWNLOAD_INFO_FROM_HEADQUATER.name(), headquarterProjectExportHandler);
        /* project template version list action 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_TEMPLATE_LIST_INFO_FROM_HEADQUATER.name(), headquarterProjectTemplateVersionListHandler);

        /* proejct template create status action 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_TEMPLATE_STATUS_INFO_FROM_HEADQUATER.name(), headquarterProjectTemplateCreateStatusHandler);

        // HV_MSG_PROJECT_PULL_GITHUB_CLONE_INFO
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_PULL_GITHUB_CLONE_INFO_FROM_HEADQUATER.name(), branchUpdateProjectHandler);

        /* branch connect action 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_CONNETCION_CHECK_INFO.name(), branchUrlConnectionHandler);
        handlerMap.put(ClientMessageType.HV_MSG_CONNETCION_CHECK_INFO_FROM_HEADQUATER.name(), branchUrlConnectionHandler);

        /* branch log file action 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_LOGFILE_DOWNLOAD_INFO.name(), logFileDownloadHandler);

        /* builder app icon status 호출 메시지 */
        handlerMap.put(ClientMessageType.BIN_FILE_APP_ICON_APPEND_SEND_INFO_FROM_HEADQUATER.name(), builderAppIconStatusHandler);

        /* HV_MSG_APPFILE_DOWNLOAD_INFO */

        /* HV_MSG_WINDOWS_CONFIG_LIST_INFO */
        handlerMap.put(ClientMessageType.HV_MSG_WINDOWS_CONFIG_LIST_INFO.name(), windowsConfigListHandler);
        handlerMap.put(ClientMessageType.HV_MSG_WINDOWS_CONFIG_LIST_INFO_FROM_HEADQUATER.name(), windowsConfigListHandler);

        /* builderKeyfileTemplateHandler */
        handlerMap.put(ClientMessageType.BIN_FILE_PROFILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER.name(), builderKeyfileTemplateHandler);
        handlerMap.put(ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_SEND_INFO_FROM_HEADQUATER.name(), builderKeyfileTemplateHandler);
        handlerMap.put(ClientMessageType.BIN_FILE_IOS_KEY_FILE_TEMPLATE_DEPLOY_SEND_INFO_FROM_HEADQUATER.name(), builderKeyfileTemplateHandler);
        handlerMap.put(ClientMessageType.BIN_FILE_IOS_ALL_KEY_FILE_SEND_INFO_FROM_HEADQUATER.name(), builderKeyfileTemplateHandler);
        handlerMap.put(ClientMessageType.BIN_FILE_PROFILE_TEMPLATE_UPDATE_SEND_INFO_FROM_HEADQUATER.name(), builderKeyfileTemplateHandler);
        handlerMap.put(ClientMessageType.BIN_FILE_IOS_ALL_KEY_FILE_UPDATE_SEND_INFO_FROM_HEADQUATER.name(), builderKeyfileTemplateHandler);

        /* builder template server config  */
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_SERVER_CONFIG_LIST_INFO_FROM_HEADQUATER.name(), builderServerConfigListHandler);
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_SERVER_CONFIG_UPDATE_STATUS_INFO_FROM_HEADQUATER.name(), builderServerConfigListHandler);

        /* deploy status 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_DEPLOY_STATUS_INFO_FROM_HEADQUATER.name(), deployStatusInfoHandler);

        /* deploy status 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_DEPLOY_STATUS_INFO_FROM_HEADQUATER.name(), deployStatusInfoHandler);

        /* deploy init status 호출 메시지 */
        handlerMap.put(ClientMessageType.HV_MSG_DEPLOY_SETTING_STATUS_INFO_FROM_HEADQUATER.name(), deploySettingInitMsgHandler);

        // HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_iOS_APPCONFIG_LIST_INFO_FROM_HEADQUATER.name(), builderAppConfigListHandler);

        // HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_IOS_ALL_GETINFORMATION_LIST_INFO_FROM_HEADQUATER.name(), builderAppConfigListHandler);

        // HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_BUILD_ALL_MULTI_PROFILE_LIST_INTO_FROM_HEADQUATER.name(), managerMultiProfileMsgHandler);

        // HV_MSG_BUILD_SET_ACTIVE_PROFILE_INFO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_BUILD_SET_ACTIVE_PROFILE_INFO_FROM_HEADQUATER.name(), managerMultiProfileMsgHandler);
        // HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_UPDATE_MULTI_PROFILE_CONFIG_UPDATE_INFO_FROM_HEADQUATER.name(), managerMultiProfileMsgHandler);

        // HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_BUILD_GET_MULTI_PROFILE_APPCONFIG_LIST_INFO_FROM_HEADQUATER.name(), managerMultiProfileMsgHandler);

        // HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_ANDROID_ALL_GETCONFIG_LIST_INFO_FROM_HEADQUATER.name(), managerGetAllConfigListMsgHandler);

        /** project import status : managerProjectimportHandler */
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_IMPORT_STATUS_INFO_FROM_HEADQUATER.name(), managerProjectimportHandler);

        /**  project deploy task setting : HV_MSG_DEPLOY_TASK_SEARCH_STATUS_INFO_FROM_HEADQUATER */
        handlerMap.put(ClientMessageType.HV_MSG_DEPLOY_TASK_SEARCH_STATUS_INFO_FROM_HEADQUATER.name(), managerDeployTaskSetingMsgHandler);


        /** project deploy set task : HV_MSG_DEPLOY_TASK_UPDATE_STATUS_INFO_FROM_HEADQUATER */
        handlerMap.put(ClientMessageType.HV_MSG_DEPLOY_TASK_UPDATE_STATUS_INFO_FROM_HEADQUATER.name(), managerDeployTaskSetingMsgHandler);

        /** project deploy set metadata : HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO_FROM_HEADQUATER */
        handlerMap.put(ClientMessageType.HV_MSG_DEPLOY_METADATA_UPDATE_STATUS_INFO_FROM_HEADQUATER.name(), managerDeployMetadataSettingMsgHandler);

        /** project deploy image metadata : HV_MSG_DEPLOY_METADATA_IMAGE_STATUS_INFO */
        handlerMap.put(ClientMessageType.HV_MSG_DEPLOY_METADATA_IMAGE_STATUS_INFO_FROM_HEADQUATER.name(), managerDeployMetadataSettingMsgHandler);

        //HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_PROJECT_GENERAL_APP_CREATE_INFO_FROM_HEADQUATER.name(), generalAppProjectCreateStatusHandler);

        //HV_MSG_BUILD_GENERAL_APP_INFO_FROM_HEADQUATER
        handlerMap.put(ClientMessageType.HV_MSG_BUILD_GENERAL_APP_INFO_FROM_HEADQUATER.name(), generalAppGetInfoHandler);
    }

    public void handleTextRequest(WebSocketSession session, TextMessage message) {
        Map<String, Object> parseResult = parser.parseMap(message.getPayload());

        Handlable handler = handlerMap.get(parseResult.get(SessionType.MsgType.name()));
        if(handler == null) {
            log.debug("Not registered command");
        } else {
            handler.handle(session, parseResult);
        }
    }

    public void handleBinaryRequest(WebSocketSession session, BinaryMessage websocketMessage) {
        uploadFilePassHandler.handle(session, websocketMessage);
    }

}
