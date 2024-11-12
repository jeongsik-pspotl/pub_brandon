package com.inswave.whive.headquater.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Random;

public class Config {

	private static final Logger logger = LoggerFactory.getLogger(Class.class);
	public static Config config;

	private Manager manager = null;
	private Log log = null;

	// application.yml에서 정의한 값.
	private String runMode1 = "dev"; // dev test product, service
	private String runMode2 = "manager"; // manager log transaver daemon
	private String runMode3 = "inswave"; // appliance일 경우 사이트 별로 구분하기 위한 mode ex) inswave,shinhanbanknext, ...

	private String country = "korea";

	private String[] adminServerUrls = {};
	private String[] elasticsearchServerUrls = {};
	private String[] logmanagerServerUrls = {};
	private String[] managerServerUrls = {};

	private Long configInitialServiceSleepTime = 600000L; // config init sleep time

	private String[] adminManagerAllowdStandardDaoDestination = {"Config","AdminUser","Config","ConfigManager","Role","Country","Menu","MenuRole"};
	private String[] adminLogAllowdStandardDaoDestination = {"Config","ConfigLog","Rule","RuleReceiver","RuleTarget","RuleLevel"};

	private String smtpHost="";
	private String smtpPort="";
	private String smtpId="";
	private String smtpPassword="";
	private String smtpName="";

	private String url="";
	private String urlData="";

	private String shinhanbankatopLoginUrl="";

	private String shinhanbankatopIntegrityAlertUrl="";
	private String shinhanbankatopIntegrityAlertUrlData="";

	private String svnPathBinSvnserve;
	private String svnPathBinSvnadmin;
	private String svnPathBinJavahl;
	private String svnServerRepo;
	private int    svnServerPort = 3690;
	private String svnServerAdminUsername;
	private String svnServerAdminPassword;
	private String deployerRepoPath;
	private String wemDatabaseVendor = "";

	private String mIocalIpAddress;
	private String mPort;
	private String mProtocol;
	private String[] mDomainName;
	private int mDomainCount;
	private String mDeployTemp;
	private String mDeployStorage;
	private String mDeployResource;
	private boolean mUseKafka;
	private boolean mUseHazelcast;

	private String contextPath="";
	private String menuJsonSystemPath="";

	public static Config getInstance() {
		if (config == null)
			config = new Config();
		return config;
	}

	private Config() {
		manager = new Manager();
		log = new Log();
	}

	public String getRunMode1() {
		return runMode1;
	}

	public void setRunMode1(String runMode1) {
		this.runMode1 = runMode1;
	}

	public String getRunMode2() {
		return runMode2;
	}

	public void setRunMode2(String runMode2) {
		this.runMode2 = runMode2;
	}

	public String getRunMode3() {
		return runMode3;
	}

	public void setRunMode3(String runMode3) {	this.runMode3 = runMode3;	}

	public Manager getManager() {	return manager;	}

	public void setManager(Manager manager) {	this.manager = manager;	}

	public Log getLog() {	return log;	}

	public void setLog(Log log) {	this.log = log;	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public String getMenuJsonSystemPath() {
		return menuJsonSystemPath;
	}

	public void setMenuJsonSystemPath(String menuJsonSystemPath) {
		this.menuJsonSystemPath = menuJsonSystemPath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrlData() {
		return urlData;
	}

	public void setUrlData(String urlData) {
		this.urlData = urlData;
	}

	public String getShinhanbankatopIntegrityAlertUrl() {
		return shinhanbankatopIntegrityAlertUrl;
	}

	public void setShinhanbankatopIntegrityAlertUrl(String shinhanbankatopIntegrityAlertUrl) {
		this.shinhanbankatopIntegrityAlertUrl = shinhanbankatopIntegrityAlertUrl;
	}

	public String getShinhanbankatopIntegrityAlertUrlData() {
		return shinhanbankatopIntegrityAlertUrlData;
	}

	public void setShinhanbankatopIntegrityAlertUrlData(String shinhanbankatopIntegrityAlertUrlData) {
		this.shinhanbankatopIntegrityAlertUrlData = shinhanbankatopIntegrityAlertUrlData;
	}

	public String getShinhanbankatopLoginUrl() {
		return shinhanbankatopLoginUrl;
	}

	public void setShinhanbankatopLoginUrl(String shinhanbankatopLoginUrl) {
		this.shinhanbankatopLoginUrl = shinhanbankatopLoginUrl;
	}

	public String[] getAdminServerUrls() {
		return adminServerUrls;
	}

	public void setAdminServerUrls(String[] adminServerUrls) {
		this.adminServerUrls = adminServerUrls;
	}

	public String[] getElasticsearchServerUrls() {
		return elasticsearchServerUrls;
	}

	public void setElasticsearchServerUrls(String[] elasticsearchServerUrls) {
		this.elasticsearchServerUrls = elasticsearchServerUrls;
	}

	public String[] getLogmanagerServerUrls() {
		return logmanagerServerUrls;
	}

	public void setLogmanagerServerUrls(String[] logmanagerServerUrls) {
		this.logmanagerServerUrls = logmanagerServerUrls;
	}

	public String[] getManagerServerUrls() {
		return managerServerUrls;
	}

	public void setManagerServerUrls(String[] managerServerUrls) {
		this.managerServerUrls = managerServerUrls;
	}

	public String[] getAdminManagerAllowdStandardDaoDestination() {		return adminManagerAllowdStandardDaoDestination;	}

	public String[] getAdminLogAllowdStandardDaoDestination() {		return adminLogAllowdStandardDaoDestination;	}

	public Long getConfigInitialServiceSleepTime() {		return configInitialServiceSleepTime;	}

	public void setConfigInitialServiceSleepTime(Long configInitialServiceSleepTime) {		this.configInitialServiceSleepTime = configInitialServiceSleepTime;	}

	public String getSmtpHost() {		return smtpHost;	}

	public void setSmtpHost(String smtpHost) {		this.smtpHost = smtpHost;	}

	public String getSmtpPort() {		return smtpPort;	}

	public void setSmtpPort(String smtpPort) {		this.smtpPort = smtpPort;	}

	public String getSmtpId() {		return smtpId;	}

	public void setSmtpId(String smtpId) {		this.smtpId = smtpId;	}

	public String getSmtpPassword() {		return smtpPassword;	}

	public void setSmtpPassword(String smtpPassword) {		this.smtpPassword = smtpPassword;	}

	public String getSmtpName() {		return smtpName;	}

	public void setSmtpName(String smtpName) {		this.smtpName = smtpName;	}

	public String getSvnPathBinSvnserve() {return svnPathBinSvnserve;}

	public void setSvnPathBinSvnserve(String svnPathBinSvnserve) {this.svnPathBinSvnserve = svnPathBinSvnserve;}

	public String getSvnPathBinSvnadmin() {return svnPathBinSvnadmin;}

	public void setSvnPathBinSvnadmin(String svnPathBinSvnadmin) {this.svnPathBinSvnadmin = svnPathBinSvnadmin;}

	public String getSvnPathBinJavahl() {return svnPathBinJavahl;}

	public void setSvnPathBinJavahl(String svnPathBinJavahl) {this.svnPathBinJavahl = svnPathBinJavahl;}

	public String getSvnServerRepo() {return svnServerRepo;}

	public void setSvnServerRepo(String svnServerRepo) {this.svnServerRepo = svnServerRepo;}

	public int getSvnServerPort() {return svnServerPort;}

	public void setSvnServerPort(int svnServerPort) {this.svnServerPort = svnServerPort;}

	public String getSvnServerAdminUsername() {return svnServerAdminUsername;}

	public void setSvnServerAdminUsername(String svnServerAdminUsername) {this.svnServerAdminUsername = svnServerAdminUsername;}

	public String getSvnServerAdminPassword() {return svnServerAdminPassword;}

	public void setSvnServerAdminPassword(String svnServerAdminPassword) {this.svnServerAdminPassword = svnServerAdminPassword;}

	public String getDeployerRepoPath() {return deployerRepoPath;}

	public void setDeployerRepoPath(String deployerRepoPath) {this.deployerRepoPath = deployerRepoPath;}

	public String getCountry() {		return country;	}

	public void setCountry(String country) {		this.country = country;	}

	public Logger getLogger() {		return logger;	}

	public String getWemDatabaseVendor() {
		return wemDatabaseVendor;
	}

	public void setWemDatabaseVendor(String wemDatabaseVendor) {
		this.wemDatabaseVendor = wemDatabaseVendor;
	}


	public String getmIocalIpAddress() {
		return mIocalIpAddress;
	}

	public void setmIocalIpAddress(String mIocalIpAddress) {
		this.mIocalIpAddress = mIocalIpAddress;
	}

	public String getmPort() {
		return mPort;
	}

	public void setmPort(String mPort) {
		this.mPort = mPort;
	}

	public String getmProtocol() {
		return mProtocol;
	}

	public void setmProtocol(String mProtocol) {
		this.mProtocol = mProtocol;
	}

	public String[] getmDomainName() {
		return mDomainName;
	}

	public void setmDomainName(String[] mDomainName) {
		this.mDomainName = mDomainName;
	}

	public int getmDomainCount() {
		return mDomainCount;
	}

	public void setmDomainCount(int mDomainCount) {
		this.mDomainCount = mDomainCount;
	}

	public String getmDeployTemp() {
		return mDeployTemp;
	}

	public void setmDeployTemp(String mDeployTemp) {
		this.mDeployTemp = mDeployTemp;
	}

	public String getmDeployStorage() {
		return mDeployStorage;
	}

	public void setmDeployStorage(String mDeployStorage) {
		this.mDeployStorage = mDeployStorage;
	}

	public String getmDeployResource() {
		return mDeployResource;
	}

	public void setmDeployResource(String mDeployResource) {
		this.mDeployResource = mDeployResource;
	}

	public boolean ismUseKafka() {
		return mUseKafka;
	}

	public void setmUseKafka(boolean mUseKafka) {
		this.mUseKafka = mUseKafka;
	}

	public boolean ismUseHazelcast() {
		return mUseHazelcast;
	}

	public void setmUseHazelcast(boolean mUseHazelcast) {
		this.mUseHazelcast = mUseHazelcast;
	}

	public class Manager {

		private Integer countLoginFailLock=5; // 관리자 로그인 실패 lock count
		private Integer minuteLoginFailLock=5; // 관리자 로그인 실패 시 lock minute
		private Integer userPasswordDay=0; // 관리자 패스워드 사용일
		private Boolean isSaveUiLog=true; // 관리자 로그 저장
		private Boolean isCheckIp=false; // 관리자 접속 IP제한

		public Manager() {}

		public Integer getCountLoginFailLock() {
			return countLoginFailLock;
		}

		public void setCountLoginFailLock(Integer countLoginFailLock) {
			this.countLoginFailLock = countLoginFailLock;
		}

		public Integer getMinuteLoginFailLock() {
			return minuteLoginFailLock;
		}

		public void setMinuteLoginFailLock(Integer minuteLoginFailLock) {
			this.minuteLoginFailLock = minuteLoginFailLock;
		}

		public Integer getUserPasswordDay() {
			return userPasswordDay;
		}

		public void setUserPasswordDay(Integer userPasswordDay) {
			this.userPasswordDay = userPasswordDay;
		}

		public Boolean getSaveUiLog() {
			return isSaveUiLog;
		}

		public void setSaveUiLog(Boolean saveUiLog) {
			isSaveUiLog = saveUiLog;
		}

		public Boolean getCheckIp() {
			return isCheckIp;
		}

		public void setCheckIp(Boolean checkIp) {
			isCheckIp = checkIp;
		}

	};


	public class Log {

		private String instanceId=""; // instance별 uniqueId

		private Integer daySaveLog=0; // 로그 저장 일..
		private Boolean isSendLogToCollector=false;

		private Long serverResourceLogSendType = 10000L; // 초기에 10초만 sleep
		private String serverResourceLogDescription = "";
		private String serverResourceLogUrl = "";
		private String kafkaServers="";
		private String[] elasticsearchSearchServer;
		private String[] elasticsearchDataServer;
		private String wasId;
		private String clientPerformanceLogRealtimeGroupId;
		private String clientProcessResourceLogRealtimeGroupId;
		private String clientMBRResourceLogRealtimeGroupId;
		private String serverResourceLogRealtimeGroupId;
		private String windowsEventSystemErrorAllLogRealtimeGroupId;
		private String clientDefragAnalysisResourceLogRealtimeGroupId;
		private String applicationErrorLogRealtimeGroupId;
		private String clientHWInfoResourceLogRealtimeGroupId;
		private String clientWindowsUpdateListResourceLogRealtimeGroupId;
		private String clientProgramListResourceLogRealtimeGroupId;
		private String deviceErrorLogRealtimeGroupId;
		private String hWErrorLogRealtimeGroupId;
		private String integrityLogRealtimeGroupId;
		private String pcOnOffEventLogRealtimeGroupId;
		private String windowsBlueScreenLogRealtimeGroupId;
		private String clientUserTermMonitorLogGroupId;

		private int indexNumberOfShards;
		private int indexNumberOfReplicas;

		private String statisticsValue = "yyyyMMddHH";

		private boolean isJavamelodyErrorPrint=true;
		private boolean isSaveRequestLogData=true;
		private String saveRequestLogDataDir = "/home/inswave/message/";
		private String logFileDatePattern = "yyyyMMddHH";
		private boolean isSaveExceptionLogData=true;
		private String saveExceptionLogDir = "/home/inswave/message/exception/";

		private boolean isRunServerresourcelogObserver = false;
		private int serverresourcelogObserverCapacityElasticsearchDataDisk = -1;
		private String serverresourcelogObserverDeleteIndexNames = "";
		private String serverresourcelogObserverCheckSourceName = "";

		private int indexDailylogApplicationErrorLogSavedDay=10;
		private int indexDailylogClientActiveportListResourceLogSavedDay=10;
		private int indexDailylogClientControlProcessResourceLogSavedDay=10;
		private int indexDailylogClientDefraganAlysisResourceLogSavedDay=10;
		private int indexDailylogClientHwInfoResourceLogSavedDay=10;
		private int indexDailylogClientMbrResourceLogSavedDay=10;
		private int indexDailylogClientPerformanceLogSavedDay=10;
		private int indexDailylogClientProcessCreationLogSavedDay=10;
		private int indexDailylogClientProcessResourceLogSavedDay=10;
		private int indexDailylogClientProgramListResourceLogSavedDay=10;
		private int indexDailylogClientUserTermMonitorLogSavedDay=10;
		private int indexDailylogClientwindowsUpdateListResourceLogSavedDay=10;
		private int indexDailylogDeviceerrorLogSavedDay=10;
		private int indexDailylogHwErrorLogSavedDay=10;
		private int indexDailylogIntegrityLogSavedDay=10;
		private int indexDailylogPcOnOffEventLogSavedDay=10;
		private int indexDailylogServerResourceLogSavedDay=10;
		private int indexDailylogModuleMonitoringLogSavedDay=10;
		private int indexDailylogWindowsBlueScreenLogSavedDay=10;
		private int indexDailylogWindowsEventSystemErrorAllLogSavedDay=10;
		private int indexDailylogRuleAlertLogSavedDay=10;
		private int indexDailylogRuleAlertSendLogSavedDay=10;

		private String indexDailylogApplicationErrorLogIndexName="";
		private String indexDailylogClientActiveportListResourceLogIndexName="";
		private String indexDailylogClientControlProcessResourceLogIndexName="";
		private String indexDailylogClientDefraganAlysisResourceLogIndexName="";
		private String indexDailylogClientHwInfoResourceLogIndexName="";
		private String indexDailylogClientMbrResourceLogIndexName="";
		private String indexDailylogClientPerformanceLogIndexName="";
		private String indexDailylogClientProcessCreationLogIndexName="";
		private String indexDailylogClientProcessResourceLogIndexName="";
		private String indexDailylogClientProgramListResourceLogIndexName="";
		private String indexDailylogClientUserTermMonitorLogIndexName="";
		private String indexDailylogClientwindowsUpdateListResourceLogIndexName="";
		private String indexDailylogDeviceerrorLogIndexName="";
		private String indexDailylogHwErrorLogIndexName="";
		private String indexDailylogIntegrityLogIndexName="";
		private String indexDailylogPcOnOffEventLogIndexName="";
		private String indexDailylogServerResourceLogIndexName="";
		private String indexDailylogModuleMonitoringLogIndexName="";
		private String indexDailylogWindowsBlueScreenLogIndexName="";
		private String indexDailylogWindowsEventSystemErrorAllLogIndexName="";
		private String indexDailylogRuleAlertLogIndexName="";
		private String indexDailylogRuleAlertSendLogIndexName="";

		private Boolean isUseModuleMonitoring=false;

		public Boolean getIsUseModuleMonitoring() {
			return this.isUseModuleMonitoring;
		}

		public void setIsUseModuleMonitoring(Boolean isUseModuleMonitoring) {
			this.isUseModuleMonitoring = isUseModuleMonitoring;
		}

		public boolean getIsRunServerresourcelogObserver() {
			return isRunServerresourcelogObserver;
		}

		public void setIsRunServerresourcelogObserver(boolean isRunServerresourcelogObserver) {
			this.isRunServerresourcelogObserver = isRunServerresourcelogObserver;
		}

		public int getServerresourcelogObserverCapacityElasticsearchDataDisk() {
			return serverresourcelogObserverCapacityElasticsearchDataDisk;
		}

		public void setServerresourcelogObserverCapacityElasticsearchDataDisk(int serverresourcelogObserverCapacityElasticsearchDataDisk) {
			this.serverresourcelogObserverCapacityElasticsearchDataDisk = serverresourcelogObserverCapacityElasticsearchDataDisk;
		}

		public String getServerresourcelogObserverDeleteIndexNames() {
			return serverresourcelogObserverDeleteIndexNames;
		}

		public void setServerresourcelogObserverDeleteIndexNames(String serverresourcelogObserverDeleteIndexNames) {
			this.serverresourcelogObserverDeleteIndexNames = serverresourcelogObserverDeleteIndexNames;
		}

		public String getServerresourcelogObserverCheckSourceName() {
			return serverresourcelogObserverCheckSourceName;
		}

		public void setServerresourcelogObserverCheckSourceName(String serverresourcelogObserverCheckSourceName) {
			this.serverresourcelogObserverCheckSourceName = serverresourcelogObserverCheckSourceName;
		}

		public String getIndexDailylogApplicationErrorLogIndexName() {
			return indexDailylogApplicationErrorLogIndexName;
		}

		public void setIndexDailylogApplicationErrorLogIndexName(String indexDailylogApplicationErrorLogIndexName) {
			this.indexDailylogApplicationErrorLogIndexName = indexDailylogApplicationErrorLogIndexName;
		}

		public String getIndexDailylogClientActiveportListResourceLogIndexName() {
			return indexDailylogClientActiveportListResourceLogIndexName;
		}

		public void setIndexDailylogClientActiveportListResourceLogIndexName(String indexDailylogClientActiveportListResourceLogIndexName) {
			this.indexDailylogClientActiveportListResourceLogIndexName = indexDailylogClientActiveportListResourceLogIndexName;
		}

		public String getIndexDailylogClientControlProcessResourceLogIndexName() {
			return indexDailylogClientControlProcessResourceLogIndexName;
		}

		public void setIndexDailylogClientControlProcessResourceLogIndexName(String indexDailylogClientControlProcessResourceLogIndexName) {
			this.indexDailylogClientControlProcessResourceLogIndexName = indexDailylogClientControlProcessResourceLogIndexName;
		}

		public String getIndexDailylogClientDefraganAlysisResourceLogIndexName() {
			return indexDailylogClientDefraganAlysisResourceLogIndexName;
		}

		public void setIndexDailylogClientDefraganAlysisResourceLogIndexName(String indexDailylogClientDefraganAlysisResourceLogIndexName) {
			this.indexDailylogClientDefraganAlysisResourceLogIndexName = indexDailylogClientDefraganAlysisResourceLogIndexName;
		}

		public String getIndexDailylogClientHwInfoResourceLogIndexName() {
			return indexDailylogClientHwInfoResourceLogIndexName;
		}

		public void setIndexDailylogClientHwInfoResourceLogIndexName(String indexDailylogClientHwInfoResourceLogIndexName) {
			this.indexDailylogClientHwInfoResourceLogIndexName = indexDailylogClientHwInfoResourceLogIndexName;
		}

		public String getIndexDailylogClientMbrResourceLogIndexName() {
			return indexDailylogClientMbrResourceLogIndexName;
		}

		public void setIndexDailylogClientMbrResourceLogIndexName(String indexDailylogClientMbrResourceLogIndexName) {
			this.indexDailylogClientMbrResourceLogIndexName = indexDailylogClientMbrResourceLogIndexName;
		}

		public String getIndexDailylogClientPerformanceLogIndexName() {
			return indexDailylogClientPerformanceLogIndexName;
		}

		public void setIndexDailylogClientPerformanceLogIndexName(String indexDailylogClientPerformanceLogIndexName) {
			this.indexDailylogClientPerformanceLogIndexName = indexDailylogClientPerformanceLogIndexName;
		}

		public String getIndexDailylogClientProcessCreationLogIndexName() {
			return indexDailylogClientProcessCreationLogIndexName;
		}

		public void setIndexDailylogClientProcessCreationLogIndexName(String indexDailylogClientProcessCreationLogIndexName) {
			this.indexDailylogClientProcessCreationLogIndexName = indexDailylogClientProcessCreationLogIndexName;
		}

		public String getIndexDailylogClientProcessResourceLogIndexName() {
			return indexDailylogClientProcessResourceLogIndexName;
		}

		public void setIndexDailylogClientProcessResourceLogIndexName(String indexDailylogClientProcessResourceLogIndexName) {
			this.indexDailylogClientProcessResourceLogIndexName = indexDailylogClientProcessResourceLogIndexName;
		}

		public String getIndexDailylogClientProgramListResourceLogIndexName() {
			return indexDailylogClientProgramListResourceLogIndexName;
		}

		public void setIndexDailylogClientProgramListResourceLogIndexName(String indexDailylogClientProgramListResourceLogIndexName) {
			this.indexDailylogClientProgramListResourceLogIndexName = indexDailylogClientProgramListResourceLogIndexName;
		}

		public String getIndexDailylogClientUserTermMonitorLogIndexName() {
			return indexDailylogClientUserTermMonitorLogIndexName;
		}

		public void setIndexDailylogClientUserTermMonitorLogIndexName(String indexDailylogClientUserTermMonitorLogIndexName) {
			this.indexDailylogClientUserTermMonitorLogIndexName = indexDailylogClientUserTermMonitorLogIndexName;
		}

		public String getIndexDailylogClientwindowsUpdateListResourceLogIndexName() {
			return indexDailylogClientwindowsUpdateListResourceLogIndexName;
		}

		public void setIndexDailylogClientwindowsUpdateListResourceLogIndexName(String indexDailylogClientwindowsUpdateListResourceLogIndexName) {
			this.indexDailylogClientwindowsUpdateListResourceLogIndexName = indexDailylogClientwindowsUpdateListResourceLogIndexName;
		}

		public String getIndexDailylogDeviceerrorLogIndexName() {
			return indexDailylogDeviceerrorLogIndexName;
		}

		public void setIndexDailylogDeviceerrorLogIndexName(String indexDailylogDeviceerrorLogIndexName) {
			this.indexDailylogDeviceerrorLogIndexName = indexDailylogDeviceerrorLogIndexName;
		}

		public String getIndexDailylogHwErrorLogIndexName() {
			return indexDailylogHwErrorLogIndexName;
		}

		public void setIndexDailylogHwErrorLogIndexName(String indexDailylogHwErrorLogIndexName) {
			this.indexDailylogHwErrorLogIndexName = indexDailylogHwErrorLogIndexName;
		}

		public String getIndexDailylogIntegrityLogIndexName() {
			return indexDailylogIntegrityLogIndexName;
		}

		public void setIndexDailylogIntegrityLogIndexName(String indexDailylogIntegrityLogIndexName) {
			this.indexDailylogIntegrityLogIndexName = indexDailylogIntegrityLogIndexName;
		}

		public String getIndexDailylogPcOnOffEventLogIndexName() {
			return indexDailylogPcOnOffEventLogIndexName;
		}

		public void setIndexDailylogPcOnOffEventLogIndexName(String indexDailylogPcOnOffEventLogIndexName) {
			this.indexDailylogPcOnOffEventLogIndexName = indexDailylogPcOnOffEventLogIndexName;
		}

		public String getIndexDailylogServerResourceLogIndexName() {
			return indexDailylogServerResourceLogIndexName;
		}

		public void setIndexDailylogServerResourceLogIndexName(String indexDailylogServerResourceLogIndexName) {
			this.indexDailylogServerResourceLogIndexName = indexDailylogServerResourceLogIndexName;
		}

		public String getIndexDailylogModuleMonitoringLogIndexName() {
			return indexDailylogModuleMonitoringLogIndexName;
		}

		public void setIndexDailylogModuleMonitoringLogIndexName(String indexDailylogModuleMonitoringLogIndexName) {
			this.indexDailylogModuleMonitoringLogIndexName = indexDailylogModuleMonitoringLogIndexName;
		}

		public String getIndexDailylogWindowsBlueScreenLogIndexName() {
			return indexDailylogWindowsBlueScreenLogIndexName;
		}

		public void setIndexDailylogWindowsBlueScreenLogIndexName(String indexDailylogWindowsBlueScreenLogIndexName) {
			this.indexDailylogWindowsBlueScreenLogIndexName = indexDailylogWindowsBlueScreenLogIndexName;
		}

		public String getIndexDailylogWindowsEventSystemErrorAllLogIndexName() {
			return indexDailylogWindowsEventSystemErrorAllLogIndexName;
		}

		public void setIndexDailylogWindowsEventSystemErrorAllLogIndexName(String indexDailylogWindowsEventSystemErrorAllLogIndexName) {
			this.indexDailylogWindowsEventSystemErrorAllLogIndexName = indexDailylogWindowsEventSystemErrorAllLogIndexName;
		}

		public String getIndexDailylogRuleAlertLogIndexName() {
			return indexDailylogRuleAlertLogIndexName;
		}

		public void setIndexDailylogRuleAlertLogIndexName(String indexDailylogRuleAlertLogIndexName) {
			this.indexDailylogRuleAlertLogIndexName = indexDailylogRuleAlertLogIndexName;
		}

		public String getIndexDailylogRuleAlertSendLogIndexName() {
			return indexDailylogRuleAlertSendLogIndexName;
		}

		public void setIndexDailylogRuleAlertSendLogIndexName(String indexDailylogRuleAlertSendLogIndexName) {
			this.indexDailylogRuleAlertSendLogIndexName = indexDailylogRuleAlertSendLogIndexName;
		}

		public int getIndexDailylogRuleAlertSendLogSavedDay() {
			return indexDailylogRuleAlertSendLogSavedDay;
		}

		public void setIndexDailylogRuleAlertSendLogSavedDay(int indexDailylogRuleAlertSendLogSavedDay) {
			this.indexDailylogRuleAlertSendLogSavedDay = indexDailylogRuleAlertSendLogSavedDay;
		}

		public int getIndexDailylogRuleAlertLogSavedDay() {
			return indexDailylogRuleAlertLogSavedDay;
		}

		public void setIndexDailylogRuleAlertLogSavedDay(int indexDailylogRuleAlertLogSavedDay) {
			this.indexDailylogRuleAlertLogSavedDay = indexDailylogRuleAlertLogSavedDay;
		}

		public int getIndexDailylogModuleMonitoringLogSavedDay() {
			return indexDailylogModuleMonitoringLogSavedDay;
		}

		public void setIndexDailylogModuleMonitoringLogSavedDay(int indexDailylogModuleMonitoringLogSavedDay) {
			this.indexDailylogModuleMonitoringLogSavedDay = indexDailylogModuleMonitoringLogSavedDay;
		}

		public int getIndexDailylogApplicationErrorLogSavedDay() {
			return indexDailylogApplicationErrorLogSavedDay;
		}

		public void setIndexDailylogApplicationErrorLogSavedDay(int indexDailylogApplicationErrorLogSavedDay) {
			this.indexDailylogApplicationErrorLogSavedDay = indexDailylogApplicationErrorLogSavedDay;
		}

		public int getIndexDailylogClientActiveportListResourceLogSavedDay() {
			return indexDailylogClientActiveportListResourceLogSavedDay;
		}

		public void setIndexDailylogClientActiveportListResourceLogSavedDay(int indexDailylogClientActiveportListResourceLogSavedDay) {
			this.indexDailylogClientActiveportListResourceLogSavedDay = indexDailylogClientActiveportListResourceLogSavedDay;
		}

		public int getIndexDailylogClientControlProcessResourceLogSavedDay() {
			return indexDailylogClientControlProcessResourceLogSavedDay;
		}

		public void setIndexDailylogClientControlProcessResourceLogSavedDay(int indexDailylogClientControlProcessResourceLogSavedDay) {
			this.indexDailylogClientControlProcessResourceLogSavedDay = indexDailylogClientControlProcessResourceLogSavedDay;
		}

		public int getIndexDailylogClientDefraganAlysisResourceLogSavedDay() {
			return indexDailylogClientDefraganAlysisResourceLogSavedDay;
		}

		public void setIndexDailylogClientDefraganAlysisResourceLogSavedDay(int indexDailylogClientDefraganAlysisResourceLogSavedDay) {
			this.indexDailylogClientDefraganAlysisResourceLogSavedDay = indexDailylogClientDefraganAlysisResourceLogSavedDay;
		}

		public int getIndexDailylogClientHwInfoResourceLogSavedDay() {
			return indexDailylogClientHwInfoResourceLogSavedDay;
		}

		public void setIndexDailylogClientHwInfoResourceLogSavedDay(int indexDailylogClientHwInfoResourceLogSavedDay) {
			this.indexDailylogClientHwInfoResourceLogSavedDay = indexDailylogClientHwInfoResourceLogSavedDay;
		}

		public int getIndexDailylogClientMbrResourceLogSavedDay() {
			return indexDailylogClientMbrResourceLogSavedDay;
		}

		public void setIndexDailylogClientMbrResourceLogSavedDay(int indexDailylogClientMbrResourceLogSavedDay) {
			this.indexDailylogClientMbrResourceLogSavedDay = indexDailylogClientMbrResourceLogSavedDay;
		}

		public int getIndexDailylogClientPerformanceLogSavedDay() {
			return indexDailylogClientPerformanceLogSavedDay;
		}

		public void setIndexDailylogClientPerformanceLogSavedDay(int indexDailylogClientPerformanceLogSavedDay) {
			this.indexDailylogClientPerformanceLogSavedDay = indexDailylogClientPerformanceLogSavedDay;
		}

		public int getIndexDailylogClientProcessCreationLogSavedDay() {
			return indexDailylogClientProcessCreationLogSavedDay;
		}

		public void setIndexDailylogClientProcessCreationLogSavedDay(int indexDailylogClientProcessCreationLogSavedDay) {
			this.indexDailylogClientProcessCreationLogSavedDay = indexDailylogClientProcessCreationLogSavedDay;
		}

		public int getIndexDailylogClientProcessResourceLogSavedDay() {
			return indexDailylogClientProcessResourceLogSavedDay;
		}

		public void setIndexDailylogClientProcessResourceLogSavedDay(int indexDailylogClientProcessResourceLogSavedDay) {
			this.indexDailylogClientProcessResourceLogSavedDay = indexDailylogClientProcessResourceLogSavedDay;
		}

		public int getIndexDailylogClientProgramListResourceLogSavedDay() {
			return indexDailylogClientProgramListResourceLogSavedDay;
		}

		public void setIndexDailylogClientProgramListResourceLogSavedDay(int indexDailylogClientProgramListResourceLogSavedDay) {
			this.indexDailylogClientProgramListResourceLogSavedDay = indexDailylogClientProgramListResourceLogSavedDay;
		}

		public int getIndexDailylogClientUserTermMonitorLogSavedDay() {
			return indexDailylogClientUserTermMonitorLogSavedDay;
		}

		public void setIndexDailylogClientUserTermMonitorLogSavedDay(int indexDailylogClientUserTermMonitorLogSavedDay) {
			this.indexDailylogClientUserTermMonitorLogSavedDay = indexDailylogClientUserTermMonitorLogSavedDay;
		}

		public int getIndexDailylogClientwindowsUpdateListResourceLogSavedDay() {
			return indexDailylogClientwindowsUpdateListResourceLogSavedDay;
		}

		public void setIndexDailylogClientwindowsUpdateListResourceLogSavedDay(int indexDailylogClientwindowsUpdateListResourceLogSavedDay) {
			this.indexDailylogClientwindowsUpdateListResourceLogSavedDay = indexDailylogClientwindowsUpdateListResourceLogSavedDay;
		}

		public int getIndexDailylogDeviceerrorLogSavedDay() {
			return indexDailylogDeviceerrorLogSavedDay;
		}

		public void setIndexDailylogDeviceerrorLogSavedDay(int indexDailylogDeviceerrorLogSavedDay) {
			this.indexDailylogDeviceerrorLogSavedDay = indexDailylogDeviceerrorLogSavedDay;
		}

		public int getIndexDailylogHwErrorLogSavedDay() {
			return indexDailylogHwErrorLogSavedDay;
		}

		public void setIndexDailylogHwErrorLogSavedDay(int indexDailylogHwErrorLogSavedDay) {
			this.indexDailylogHwErrorLogSavedDay = indexDailylogHwErrorLogSavedDay;
		}

		public int getIndexDailylogIntegrityLogSavedDay() {
			return indexDailylogIntegrityLogSavedDay;
		}

		public void setIndexDailylogIntegrityLogSavedDay(int indexDailylogIntegrityLogSavedDay) {
			this.indexDailylogIntegrityLogSavedDay = indexDailylogIntegrityLogSavedDay;
		}

		public int getIndexDailylogPcOnOffEventLogSavedDay() {
			return indexDailylogPcOnOffEventLogSavedDay;
		}

		public void setIndexDailylogPcOnOffEventLogSavedDay(int indexDailylogPcOnOffEventLogSavedDay) {
			this.indexDailylogPcOnOffEventLogSavedDay = indexDailylogPcOnOffEventLogSavedDay;
		}

		public int getIndexDailylogServerResourceLogSavedDay() {
			return indexDailylogServerResourceLogSavedDay;
		}

		public void setIndexDailylogServerResourceLogSavedDay(int indexDailylogServerResourceLogSavedDay) {
			this.indexDailylogServerResourceLogSavedDay = indexDailylogServerResourceLogSavedDay;
		}

		public int getIndexDailylogWindowsBlueScreenLogSavedDay() {
			return indexDailylogWindowsBlueScreenLogSavedDay;
		}

		public void setIndexDailylogWindowsBlueScreenLogSavedDay(int indexDailylogWindowsBlueScreenLogSavedDay) {
			this.indexDailylogWindowsBlueScreenLogSavedDay = indexDailylogWindowsBlueScreenLogSavedDay;
		}

		public int getIndexDailylogWindowsEventSystemErrorAllLogSavedDay() {
			return indexDailylogWindowsEventSystemErrorAllLogSavedDay;
		}

		public void setIndexDailylogWindowsEventSystemErrorAllLogSavedDay(int indexDailylogWindowsEventSystemErrorAllLogSavedDay) {
			this.indexDailylogWindowsEventSystemErrorAllLogSavedDay = indexDailylogWindowsEventSystemErrorAllLogSavedDay;
		}

		public String getInstanceId() {	return instanceId;	}

		public void setInstanceId(String instanceId) {	this.instanceId = instanceId;	}

		public String getLogFileDatePattern() {
			return logFileDatePattern;
		}

		public void setLogFileDatePattern(String logFileDatePattern) {
			this.logFileDatePattern = logFileDatePattern;
		}

		public boolean isJavamelodyErrorPrint() {
			return isJavamelodyErrorPrint;
		}

		public void setJavamelodyErrorPrint(boolean javamelodyErrorPrint) {
			isJavamelodyErrorPrint = javamelodyErrorPrint;
		}

		public boolean isSaveRequestLogData() {
			return isSaveRequestLogData;
		}

		public void setSaveRequestLogData(boolean saveRequestLogData) {
			isSaveRequestLogData = saveRequestLogData;
		}

		public String getSaveRequestLogDataDir() {
			return saveRequestLogDataDir;
		}

		public void setSaveRequestLogDataDir(String saveRequestLogDataDir) {
			this.saveRequestLogDataDir = saveRequestLogDataDir;
		}

		public int getIndexNumberOfShards() {
			return indexNumberOfShards;
		}

		public void setIndexNumberOfShards(int indexNumberOfShards) {
			this.indexNumberOfShards = indexNumberOfShards;
		}

		public int getIndexNumberOfReplicas() {
			return indexNumberOfReplicas;
		}

		public void setIndexNumberOfReplicas(int indexNumberOfReplicas) {
			this.indexNumberOfReplicas = indexNumberOfReplicas;
		}

		public String[] getElasticsearchSearchServer() {			return elasticsearchSearchServer;		}

		public String getElasticsearchSearchServerRandom() {
			Random random = new Random();
			return elasticsearchSearchServer[random.nextInt(elasticsearchSearchServer.length)];
		}

		public void setElasticsearchSearchServer(String elasticsearchSearchServer) {			this.elasticsearchSearchServer = elasticsearchSearchServer.split(",");		}

		public String[] getElasticsearchDataServer() {			return elasticsearchDataServer;		}

		public String getElasticsearchDataServerRandom() {
			Random random = new Random();
			return elasticsearchDataServer[random.nextInt(elasticsearchDataServer.length)];
		}

		public void setElasticsearchDataServer(String elasticsearchDataServer) {			this.elasticsearchDataServer = elasticsearchDataServer.split(",");		}

		public String getKafkaServers() {			return kafkaServers;		}

		public void setKafkaServers(String kafkaServers) {			this.kafkaServers = kafkaServers;		}

		public Long getServerResourceLogSendType() {		return this.serverResourceLogSendType;	}

		public void setServerResourceLogSendType(Long serverResourceLogSendType) {
			this.serverResourceLogSendType = serverResourceLogSendType;
		}

		public String getServerResourceLogDescription() {		return this.serverResourceLogDescription;	}

		public void setServerResourceLogDescription(String serverResourceLogDescription) {		this.serverResourceLogDescription = serverResourceLogDescription;	}

		public String getServerResourceLogUrl() {		return this.serverResourceLogUrl;	}

		public void setServerResourceLogUrl(String serverResourceLogUrl) {		this.serverResourceLogUrl = serverResourceLogUrl;	}

		public Integer getDaySaveLog() {
			return daySaveLog;
		}

		public void setDaySaveLog(Integer daySaveLog) {
			this.daySaveLog = daySaveLog;
		}

		public Boolean getSendLogToCollector() {
			return isSendLogToCollector;
		}

		public void setSendLogToCollector(Boolean sendLogToCollector) {
			isSendLogToCollector = sendLogToCollector;
		}

        public void setWasId(String wasId) {	this.wasId=wasId;        }

		public String getWasId() {	return this.wasId;        }


		public String getClientPerformanceLogRealtimeGroupId() {
			return clientPerformanceLogRealtimeGroupId;
		}

		public void setClientPerformanceLogRealtimeGroupId(String clientPerformanceLogRealtimeGroupId) {
			this.clientPerformanceLogRealtimeGroupId = clientPerformanceLogRealtimeGroupId;
		}

		public String getClientProcessResourceLogRealtimeGroupId() {
			return clientProcessResourceLogRealtimeGroupId;
		}

		public void setClientProcessResourceLogRealtimeGroupId(String clientProcessResourceLogRealtimeGroupId) {
			this.clientProcessResourceLogRealtimeGroupId = clientProcessResourceLogRealtimeGroupId;
		}

		public String getClientMBRResourceLogRealtimeGroupId() {
			return clientMBRResourceLogRealtimeGroupId;
		}

		public void setClientMBRResourceLogRealtimeGroupId(String clientMBRResourceLogRealtimeGroupId) {
			this.clientMBRResourceLogRealtimeGroupId = clientMBRResourceLogRealtimeGroupId;
		}

		public String getServerResourceLogRealtimeGroupId() {
			return serverResourceLogRealtimeGroupId;
		}

		public void setServerResourceLogRealtimeGroupId(String serverResourceLogRealtimeGroupId) {
			this.serverResourceLogRealtimeGroupId = serverResourceLogRealtimeGroupId;
		}

		public String getWindowsEventSystemErrorAllLogRealtimeGroupId() {
			return windowsEventSystemErrorAllLogRealtimeGroupId;
		}

		public void setWindowsEventSystemErrorAllLogRealtimeGroupId(String windowsEventSystemErrorAllLogRealtimeGroupId) {
			this.windowsEventSystemErrorAllLogRealtimeGroupId = windowsEventSystemErrorAllLogRealtimeGroupId;
		}

		public String getClientDefragAnalysisResourceLogRealtimeGroupId() {
			return clientDefragAnalysisResourceLogRealtimeGroupId;
		}

		public void setClientDefragAnalysisResourceLogRealtimeGroupId(String clientDefragAnalysisResourceLogRealtimeGroupId) {
			this.clientDefragAnalysisResourceLogRealtimeGroupId = clientDefragAnalysisResourceLogRealtimeGroupId;
		}

		public String getApplicationErrorLogRealtimeGroupId() {
			return applicationErrorLogRealtimeGroupId;
		}

		public void setApplicationErrorLogRealtimeGroupId(String applicationErrorLogRealtimeGroupId) {
			this.applicationErrorLogRealtimeGroupId = applicationErrorLogRealtimeGroupId;
		}

		public String getClientHWInfoResourceLogRealtimeGroupId() {
			return clientHWInfoResourceLogRealtimeGroupId;
		}

		public void setClientHWInfoResourceLogRealtimeGroupId(String clientHWInfoResourceLogRealtimeGroupId) {
			this.clientHWInfoResourceLogRealtimeGroupId = clientHWInfoResourceLogRealtimeGroupId;
		}

		public String getClientWindowsUpdateListResourceLogRealtimeGroupId() {
			return clientWindowsUpdateListResourceLogRealtimeGroupId;
		}

		public void setClientWindowsUpdateListResourceLogRealtimeGroupId(String clientWindowsUpdateListResourceLogRealtimeGroupId) {
			this.clientWindowsUpdateListResourceLogRealtimeGroupId = clientWindowsUpdateListResourceLogRealtimeGroupId;
		}

		public String getClientProgramListResourceLogRealtimeGroupId() {
			return clientProgramListResourceLogRealtimeGroupId;
		}

		public void setClientProgramListResourceLogRealtimeGroupId(String clientProgramListResourceLogRealtimeGroupId) {
			this.clientProgramListResourceLogRealtimeGroupId = clientProgramListResourceLogRealtimeGroupId;
		}

		public String getDeviceErrorLogRealtimeGroupId() {
			return deviceErrorLogRealtimeGroupId;
		}

		public void setDeviceErrorLogRealtimeGroupId(String deviceErrorLogRealtimeGroupId) {
			this.deviceErrorLogRealtimeGroupId = deviceErrorLogRealtimeGroupId;
		}

		public String getHWErrorLogRealtimeGroupId() {
			return hWErrorLogRealtimeGroupId;
		}

		public void setHWErrorLogRealtimeGroupId(String hWErrorLogRealtimeGroupId) {
			this.hWErrorLogRealtimeGroupId = hWErrorLogRealtimeGroupId;
		}

		public String getIntegrityLogRealtimeGroupId() {
			return integrityLogRealtimeGroupId;
		}

		public void setIntegrityLogRealtimeGroupId(String integrityLogRealtimeGroupId) {
			this.integrityLogRealtimeGroupId = integrityLogRealtimeGroupId;
		}

		public String getPcOnOffEventLogRealtimeGroupId() {
			return pcOnOffEventLogRealtimeGroupId;
		}

		public void setPcOnOffEventLogRealtimeGroupId(String pcOnOffEventLogRealtimeGroupId) {
			this.pcOnOffEventLogRealtimeGroupId = pcOnOffEventLogRealtimeGroupId;
		}

		public String getWindowsBlueScreenLogRealtimeGroupId() {
			return windowsBlueScreenLogRealtimeGroupId;
		}

		public void setWindowsBlueScreenLogRealtimeGroupId(String windowsBlueScreenLogRealtimeGroupId) {
			this.windowsBlueScreenLogRealtimeGroupId = windowsBlueScreenLogRealtimeGroupId;
		}

		public String getClientUserTermMonitorLogGroupId() {
			return clientUserTermMonitorLogGroupId;
		}

		public void setClientUserTermMonitorLogGroupId(String clientUserTermMonitorLogGroupId) {
			this.clientUserTermMonitorLogGroupId = clientUserTermMonitorLogGroupId;
		}

		public void setStatisticsValue(String statisticsValue) {
			this.statisticsValue = statisticsValue;
		}

		public String getStatisticsValue() {
			return this.statisticsValue;
		}

		public boolean isSaveExceptionLogData() {
			return isSaveExceptionLogData;
		}

		public void setSaveExceptionLogData(boolean saveExceptionLogData) {
			isSaveExceptionLogData = saveExceptionLogData;
		}

		public String getSaveExceptionLogDir() {
			return saveExceptionLogDir;
		}

		public void setSaveExceptionLogDir(String saveExceptionLogDir) {
			this.saveExceptionLogDir = saveExceptionLogDir;
		}

	};

}
