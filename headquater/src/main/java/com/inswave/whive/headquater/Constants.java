package com.inswave.whive.headquater;

public class Constants {

    public static final String RUN_MODE1         = "runMode1";
    public static final String RUN_MODE1_DEV     = "dev";
    public static final String RUN_MODE1_TEST    = "test";
    public static final String RUN_MODE1_PRODUCT = "product";
    public static final String RUN_MODE1_SERVICE = "service";

    public static final String RUN_MODE2                = "runMode2";
    public static final String RUN_MODE2_MANAGER        = "manager";
    public static final String RUN_MODE2_TRANSAVER      = "transaver";
    public static final String RUN_MODE2_LOG            = "log";
    public static final String RUN_MODE2_DAEMON         = "daemon"; // daemon 검색엔진..   기타 다른 서버들..
    public static final String RUN_MODE2_WINDOWS_UPDATE = "windowsUpdate"; // 대량 파일 처리. (윈도우 업데이트 파일)

    public static final String RUN_MODE3                 = "runMode3";
    public static final String RUN_MODE3_INSWAVE         = "inswave";
    public static final String RUN_MODE3_SHINHANBANKATOP = "shinhanbankatop";
    public static final String RUN_MODE3_SHINHANBANKNEXT = "shinhanbanknext";

    public static final String TAG_LICENSE_TYPE_LITE         = "lite";
    public static final String TAG_LICENSE_TYPE_STANDARD     = "standard";
    public static final String TAG_LICENSE_TYPE_PROFESSIONAL = "professional";
    public static final String TAG_LICENSE_TYPE_ENTERPRISE   = "enterprise";

    public static final int    SCHEDULER_POOL_SIZE          = 10;
    public static final String SCHEDULER_THREAD_PREFIX_NAME = "SchedulerThread-";

    public static final String CLASS_OF_RDBDAO_STANDARD_SERVICE                                              = "com.inswave.appplatform.service.rdbdao.StandardService";
    public static final String CLASS_OF_CONFIG_INITIAL_BATCH_SERVICE                                         = "com.inswave.appplatform.service.batch.ConfigInitial";
    public static final String CLASS_OF_APPLICATION_LOG_FILE_BATCH_SERVICE                                   = "com.inswave.appplatform.service.batch.ApplicationLogFile";
    public static final String CLASS_OF_DB_BACKUP_BATCH_SERVICE                                              = "com.inswave.appplatform.service.batch.DbBackup";
    public static final String CLASS_OF_DISK_CHECK_BATCH_SERVICE                                             = "com.inswave.appplatform.service.batch.DiskCheck";
    public static final String METHOD_OF_INTERNAL_EXCUTE_SERVICE                                             = "excute";
    public static final String METHOD_OF_INTERNAL_SEND_LOG_SERVICE                                           = "sendLog";
    public static final String CLASS_OF_LOG_TABLEENTITYPROVIDER                                              = "com.inswave.appplatform.log.domain.TableEntityProvider";
    public static final String METHOD_OF_LOG_TABLEENTITYPROVIDER                                             = "getTableEntity";
    public static final String METHOD_OF_CHECK_SHINHANBANKATOP_PROCESSCREATIONLOG_IDLEBETWEENPOLLSMS_SERVICE = "checkShinhanbankAtopCreationLogIdleBetweenPollsMs";
    public static final String METHOD_OF_START_SHINHANBANKATOP_PROCESSCREATIONLOG_IDLEBETWEENPOLLSMS_SERVICE = "startShinhanbankAtopCreationLogIdleBetweenPollsMs";
    public static final String METHOD_OF_STOP_SHINHANBANKATOP_PROCESSCREATIONLOG_IDLEBETWEENPOLLSMS_SERVICE  = "stopShinhanbankAtopCreationLogIdleBetweenPollsMs";

    public static final String PACKATE_OF_SERVICE                  = "com.inswave.appplatform.service.";
    public static final String PACKATE_OF_MANAGER_EXTERNAL_SERVICE = "com.inswave.appplatform.external.service.";
    public static final String PACKATE_OF_MANAGER_SERVICE          = "com.inswave.appplatform.wedgemanager.service.";
    public static final String PACKATE_OF_MANAGERTS_SERVICE        = "com.inswave.appplatform.wedgemanagerts.service.";
    public static final String PACKATE_OF_DEPLOYER_SERVICE         = "com.inswave.appplatform.deployer.service.";
    public static final String PACKATE_OF_WEBSOCKET_SERVICE        = "com.inswave.appplatform.";
    public static final String PACKATE_OF_LOG_SERVICE              = "com.inswave.appplatform.log.service.";

    public static final String TAG_HEADER               = "header";
    public static final String TAG_HEADER_MSGID         = "msgId";
    public static final String TAG_HEADER_SOURCE        = "source";
    public static final String TAG_HEADER_SERVICE       = "service";
    public static final String TAG_HEADER_DESTINATION   = "destination";
    public static final String TAG_HEADER_DAO_OPERATION = "daoOperation";
    public static final String TAG_HEADER_DEVICEID      = "deviceId";
    public static final String TAG_HEADER_APPID         = "appId";
    public static final String TAG_HEADER_OSTYPE        = "osType";
    public static final String TAG_HEADER_SITE_ID       = "siteId";
    public static final String TAG_HEADER_INSTALL_ID    = "installId";
    public static final String TAG_HEADER_SITE_NAME     = "siteName";
    public static final String TAG_HEADER_USER_ID       = "userId";
    public static final String TAG_HEADER_TERM_NO       = "termNo";
    public static final String TAG_HEADER_SSO_BR_NO     = "ssoBrNo";
    public static final String TAG_HEADER_BR_NO         = "brNo";
    public static final String TAG_HEADER_DEPT_NAME     = "deptName";
    public static final String TAG_HEADER_HWN_NO        = "hwnNo";
    public static final String TAG_HEADER_USER_NAME     = "userName";
    public static final String TAG_HEADER_SSO_TYPE      = "ssoType";
    public static final String TAG_HEADER_PC_NAME       = "pcName";
    public static final String TAG_HEADER_HOST_NAME     = "hostName";
    public static final String TAG_HEADER_IP            = "ip";

    public static final String TAG_BODY = "body";

    public static final Integer RESULT_SUCESS = 1;
    public static final Integer RESULT_FAIL   = -1;

    public static final String TAG_RESULT            = "result";
    public static final String TAG_RESULT_MSG        = "resultMessage";
    public static final String TAG_TYPE              = "type";
    public static final String TAG_TYPE1             = "type1";
    public static final String TAG_TYPE2             = "type2";
    public static final String TAG_ERROR             = "error";
    public static final String TAG_ERROR_DESCRIPTION = "errorDescription";

    public static final Integer ERROR_HEADER_CODE = -1;
    public static final String  ERROR_HEADER      = "errorHeader";

    public static final Integer ERROR_SERVICE_PROCESS_CODE = -2;
    public static final String  ERROR_SERVICE_PROCESS      = "errorServiceProcess";

    public static final Integer ERROR_SERVICE_NOTALLOWD_CODE = -3;
    public static final String  ERROR_SERVICE_NOTALLOWD      = "errorServiceNotallowd";

    public static final Integer ERROR_DOMAIN_FORMAT_CODE = -4;
    public static final String  ERROR_DOMAIN_FORMAT      = "errorDomainFormat";

    public static final Integer ERROR_RDB_QUERY_CODE = -5;
    public static final String  ERROR_RDB_QUERY      = "errorRdbQuery";

    public static final Integer ERROR_RDB_SELECT_QUERY_CODE = -6;
    public static final String  ERROR_RDB_SELECT_QUERY      = "errorRdbSelectQuery";

    public static final Integer RESULT_REPOSITORY_NOT_FOUND = -7;

    public static final Integer RESULT_ID_NOT_FOUND = 0;
    public static final String  ERROR_ID_NOT_FOUND  = "errorIdNotFound";

    public static final Integer RESULT_DATA_NOT_FOUND = 0;
    public static final String  ERROR_DATA_NOT_FOUND  = "errorDataNotFound";

    //public static final String TAG_JWT_SIGNING_KEY = "jwtSignKey";
    public static final String TAG_JWT_SECURET_KEY        = "ijso2o4sk93208kdj82025";
    public static final String TAG_JWT_ISSUER             = "W-EdgeManager";
    public static final String TAG_JWT_CLAIM_USER_EXPIRED = "expired";
    //public static final Long   TAG_JWT_KEY_EXPIRED_ADD_MILLIS = 1000L*60*1440; // 영구키는 24시간으로 해서 발행한다.
    public static final Long   TAG_JWT_EXPIRED_ADD_MILLIS = 1000L * 60 * 10; // 10분만 토큰 유효 ?? wgear는 ??????????????
    public static final String TAG_AUTH_TOKEN             = "AUTH-TOKEN";

    public static final String TAG_COUNT_LOGIN_FAIL_LOCK  = "countLoginFailLock";
    public static final String TAG_MINUTE_LOGIN_FAIL_LOCK = "minuteLoginFailLock";
    public static final String TAG_USER_PASSWORD_DAY      = "userPasswordDay";
    public static final String TAG_IS_SAVE_UI_LOG         = "isSaveUiLog";
    public static final String TAG_IS_CHECK_IP            = "isCheckIp";

    public static final String TAG_INFOMATION                               = "infomation";
    public static final String TAG_DAY_SAVE_LOG                             = "daySaveLog";
    public static final String TAG_IS_SEND_LOG_TO_COLLECTOR                 = "isSendLogToCollector";
    public static final String TAG_LOG_INFOMATION_KEY                       = ". ";
    public static final String TAG_LOG_INTEGRITYLOG_SENDCHECKERRORCOUNT_KEY = "integritylogSendCheckErrorCount";

    public static final String TAG_SITE_ID                   = "siteId";
    public static final String TAG_SITE_NAME                 = "siteName";
    public static final String TAG_NAME                      = "name";
    public static final String TAG_INSTALL_ID                = "installId";
    public static final String TAG_LICENSE_ID                = "licenseId";
    public static final String TAG_LICENSE_NAME              = "licenseName";
    public static final String TAG_LICENSE_DESCRIPTION       = "description";
    public static final String TAG_LICENSE_COUNT             = "count";
    public static final String TAG_ADMIN_SERVER_URLS         = "adminServerUrls";
    public static final String TAG_MANAGER_SERVER_URLS       = "managerServerUrls";
    public static final String TAG_ELASTICSEARCH_SERVER_URLS = "elasticsearchServerUrls";
    public static final String TAG_LOG_MANAGER_SERVER_URLS   = "logManagerServerUrls";

    public static final String TAG_PAGE_ROW_COUNT = "totalRowCount";    // 전체 데이터 수
    public static final String TAG_PAGE_NUMBER    = "pageNumber";                // 현재 페이지 번호
    public static final String TAG_PAGE_SIZE      = "pageSize";                    // 페이지 크기
    public static final String TAG_PAGE_COUNT     = "pageCount";                // 전체 페이지 수

    public static final String TAG_WRONG_COUNT              = "wrongCount";
    public static final String TAG_TABLE_ENTITY_ROWS        = "Rows";
    public static final String TAG_DOCUMENT_ENTITY_LOG_ROWS = "LogRows";
    public static final String TAG_DOCUMENT_ENTITY_ROWS     = "Rows";
    public static final String TAG_SUCESS_COUNT             = "sucessCount";
    public static final String TAG_INSERTED_COUNT           = "insertedCount";
    public static final String TAG_INSERT_REQUEST_COUNT     = "insertRequestCount";
    public static final String TAG_DELETED_COUNT            = "deletedCount";
    public static final String TAG_UPDATED_COUNT            = "updatedCount";
    public static final String TAG_QUERY_RESULT             = "queryResult";
    public static final String TAG_INSERTED_ROWS            = "insertedRows";
    public static final String TAG_DELETED_ROWS             = "deletedRows";
    public static final String TAG_UPDATED_ROWS             = "updatedRows";

    public static final String TAG_LOGIN_ID     = "loginId";
    public static final String TAG_PASSWORD     = "password";
    public static final String TAG_NEW_PASSWORD = "newPassword";

    public static final Integer TYPE_OF_FIRST_PASSWORD = 1;
    public static final Integer TYPE_OF_RESET_PASSWORD = 2;

    public static final String TAG_SMTP_HOST                                = "smtpHost";
    public static final String TAG_SMTP_PORT                                = "smtpPort";
    public static final String TAG_SMTP_ID                                  = "smtpId";
    public static final String TAG_SMTP_PASSWORD                            = "smtpPassword";
    public static final String TAG_SMTP_NAME                                = "smtpName";
    public static final String TAG_URL                                      = "url";
    public static final String SHINHANBANKATOP_INTEGRITY_ALERT_URL          = "shinhanbankatopIntegrityAlertUrl";
    public static final String SHINHANBANKATOP_LOGIN_URL                    = "shinhanbankatopLoginUrl";
    public static final String TAG_DATE_PATTERN_YYYY_MM_DD_HH_MM_SS         = "yyyy-MM-dd hh:mm:ss";
    public static final String TAG_DATE_PATTERN_OF_ADMIN_DISPLAY            = "yyyy-MM-dd hh:mm:ss";
    public static final String TAG_DATE_PATTERN_YYYY_MM_DD_T_HH_MM_SS_SSS_Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String TAG_DATE_PATTERN_YYYY_MM_DD                  = "yyyy-MM-dd";
    public static final String TAG_OBJECT_MAPPER_TIME_ZONE                  = "Asia/Seoul";
    public static final String TAG_DATE_PATTERN_YYYY_MM_DD_T_HH_MM_SS       = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String TAG_DATE_PATTERN_OF_ELASTICSEARCH_INDEX      = "yyyyMMdd";
    public static final String TAG_HEADER_COUNTRY_ID                        = "countryId";
    public static final Long   LANGUAGEPACK_COUNTRY_ID                      = 1L;

    public static final Integer RESULT_PASSWORD_MISMATCH = 2;
    public static final Integer RESULT_IS_FIRST_PASSWORD = 3;
    public static final Integer RESULT_IS_OVER_DUE_DATE  = 4;

    public static final Integer RESULT_NAME_MISMATCH = 3;

    public static final String URL_SPLIT_STRING = ",";

    public static final String TAG_SERVER_RESOURCE_LOG               = "ServerResourceLog";
    public static final String TAG_SEND_TYPE                         = "sendType";
    public static final String TAG_DESCRIPTION                       = "description";
    public static final Long   TYPE_OF_STOP_SERVER_RESOURCE_LOG_SEND = 0L;
    public static final long   TIME_OUT_FOR_HTTP_REQUEST             = 3000L;
    public static final String TAG_DESTINATION_FLUME_KAFKA           = "flume.kafka";
    public static final String TAG_CURRENT_DATE                      = "currentDate";

    public static final String TAG_CPU_USAGE        = "cpuUsage";
    public static final String TAG_MEMORY_AVAILABLE = "memoryAvailable";
    public static final String TAG_MEMORY_TOTAL     = "memoryTotal";
    public static final String TAG_MEMORY_USED      = "memoryUsed";
    public static final String TAG_MEMORY_USAGE     = "memoryUsage";
    public static final String TAG_DISK_TOTAL       = "diskTotal";
    public static final String TAG_DISK_USED        = "diskUsed";
    public static final String TAG_DISK_USAGE       = "diskUsage";
    public static final String TAG_NETWORK_SENT     = "networkSent";
    public static final String TAG_NETWORK_RECEIVED = "networkReceived";
    public static final String TAG_TIME_CREATED     = "timeCreated";
    public static final String TAG_NETWORK_RX       = "networkRx";
    public static final String TAG_NETWORK_TX       = "networkTx";

    public static final String TAG_MODULE_NAME   = "moduleName";
    public static final String TAG_MODULE_STATUS = "moduleStatus";

    //DB 관련
    public static final String TAG_DB_LOG_CONFIG_KEY_SERVERRESOURCELOG           = "ServerResourceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTRESOURCELOG           = "ClientResourceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_WINDOWSEVENTSYSTEMERRORALL  = "WindowsEventSystemErrorAllLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTUSERTERMMONITORINGLOG = "ClientUserTermMonitorLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTPROCESSCREATIONLOG    = "ClientProcessCreationLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_HWERRORLOG                  = "HWErrorLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_DEPLOYLOG                   = "DeployLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_WINDOWSEVENTLOG             = "WindowEventLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_WINDOWUPDATELOG             = "WindowUpdateLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_WINDOWKERNALLOG             = "WindowKernelLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_WINDOWOFFICEUPDATELOG       = "WindowOfficeUpdateLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_WINDOWOSERRORLOG            = "WindowOsErrorLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_WINDOWBLUESCREENLOG         = "WindowsBlueScreenLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CONNECTLOG                  = "ConnectLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_SCREENLOG                   = "ScreenLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_DEVICEERRORLOG              = "DeviceErrorLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_APPLICATIONERRORLOG         = "ApplicationErrorLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTPERFORMANCELOG        = "ClientPerformanceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_INTEGRITYLOG                = "IntegrityLog";

    public static final String TAG_TOP = "top";
    public static final String TAG_IP  = "ip";

    //add
    public static final String TAG_DB_LOG_CONFIG_KEY_PCONOFFEVNETLOG                 = "PcOnOffEventLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTMBPRESOURCELOG            = "ClientMBRResourceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTDEFRAGANALYSISRESOURCELOG = "ClientDefragAnalysisResourceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTPROGRAMLISTRESOURCELOG    = "ClientProgramListResourceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTWINDOWSUPDATELOG          = "ClientWindowsUpdateLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTHWINFORESOURCELOG         = "ClientHWInfoResourceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTACTIVEPORTLISTRESOURCELOG = "ClientActivePortListResourceLog";
    public static final String TAG_DB_LOG_CONFIG_KEY_CLIENTCONTROLPROCESSRESOURCELOG = "ClientControlProcessResourceLog";

    // W-Deployer
    public static final Integer TAG_DEPLOYER_STATUS_READY              = 0;    // Ready
    public static final Integer TAG_DEPLOYER_STATUS_SENDING            = 1;    // Sending
    public static final Integer TAG_DEPLOYER_STATUS_STOPPING           = -1;   // Stopping
    public static final Integer TAG_DEPLOYER_CHUNK_BYTE_SIZE           = 60 * 1024; // 60KB
    public static final Integer TAG_DEPLOYER_MISSED_SEGMENT_FILL_LIMIT = 10; // 누락패킷 요청 시 최대 회신 개수

    //policy
    public static final String DEPLOY_INTEGRITY = "DeployIntegrity";
    public static final String INTEGRITY        = "Integrity";
    public static final String COLLECTION       = "Collection";

    //dist
    public static final String DIST_TYPE_ONE                      = "ONE";
    public static final String DIST_TYPE_ALL_PC                   = "ALL_PC";
    public static final String DIST_TYPE_ALL_AUTO                 = "ALL_AUTO";
    public static final String DIST_SERVICE_NAME_NEW_INTEGRITY    = "newInfoIntegrity";
    public static final String DIST_SERVICE_NAME_EMERGENCY_DEPLOY = "emergencyDeployInfo";
    public static final String DIST_SERVICE_NAME_NOTIFICATION     = "notification";
    public static final String DIST_SERVICE_NAME_EXECUTE_COMMAND  = "executeCommand";
    public static final String DIST_SERVICE_NAME_COMMAND          = "command";
    public static final String DIST_SERVICE_NAME_LOGFILE          = "getLogFile";
    public static final String DIST_SERVICE_NAME_LOGDOWNLOAD      = "LogDownload";

    public static final String DIST_SERVICE_NAME_GETWGEARCONFIG     = "getWGearConfig";
    public static final String DIST_SERVICE_NAME_WGEARCONFINGUPDATE = "updateWGearConfig";

    public static final String TAG_EXCEPTION_COUNT              = "exceptionCount";
    public static final String TAG_RUNNING_TIME                 = "runningTime";
    public static final String TAG_ROW_SIZE                     = "rowSize";
    public static final String TAG_CURRENT_ROW_SIZE             = "currentRowSize";
    public static final String TAG_DAILY_ROW_SIZE               = "dailyRowSize";
    public static final String TAG_OBJECT_CONVERT_TIME          = "objectConvertTime";
    public static final String TAG_ELASTICSEARCH_CURRENT_INSERT = "elasticsearchCurrentInsert";
    public static final String TAG_ELASTICSEARCH_DAILY_INSERT   = "elasticsearchDailyInsert";
    public static final String TAG_GROUP_ID                     = "groupId";
    public static final String TAG_KEY                          = "key";
    public static final String TAG_VALUE                        = "value";
    public static final String TAG_SET_IDLE_BETWEEN_POLLS       = "setIdleBetweenPolls";
    public static final String TAG_METHOD_NAME                  = "methodName";
    public static final String TAG_HEADER_PHONE_NO              = "phoneNo";

    public static final String TAG_PATH = "path";

    //	EDGE
    public static final String TAG_HEADER_EDGE_TOKEN          = "edge-token";
    public static final String TAG_HEADER_EDGE_ID             = "edge-id";
    public static final String TAG_EDGE_ID                    = "edgeId";
    public static final String TAG_EDGE_TOKEN                 = "edgeToken";
    public static final String TAG_EDGE_TOKEN_AVAILABLE_TIME  = "edgeTokenAvailableTime";
    public static final String TAG_EDGE_INFO                  = "edgeInfo";
    public static final String TAG_CONNECTION                 = "connection";
    public static final String TAG_SQL_USERPARAM_PREFIX       = ":UPARAM_";
    public static final String TAG_EDGE_AGENT_ID_PREFIX       = "EDGE_AGENT_ID=";
    public static final String TAG_EDGE_STMT_BODY_OPERATION   = "operation";
    public static final String TAG_EDGE_STMT_BODY_REQUESTDATA = "requestData";
    public static final String TAG_IOS                        = "iOS";
    public static final String TAG_OS_ANDRIOD                 = "Android";

}
