package com.inswave.whive.headquater.util.system;

import com.inswave.whive.headquater.util.SystemLicenseInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import websquare.system.license.manager.IPv4CIDR;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public abstract class AbstractWebSquareLicense implements WebSquareLicense {

    // 인스턴스-고유 변수들
    // 라이센스아이디 : 고객아이디 : 고객이름 : 라이센스타입 : 호스트네임 : 맥어드레스 : 제품아이디 : 만료일 : CPU갯수 : 호스트명 체크여부 : 2 : 웹스퀘어 플랫폼 : 웹스퀘어  : ... : 제품N 명
    private int CPU; // CPU 갯수

    private int customerID; // 고객 아이디
    private String Name; // 고객이름
    // private String customerName; // 고객 이름
    private int licenseID; // 라이센스 아이디
    private int licenseType; // 라이센스 타입(0 : 데모 1 : 실버전)
    private int productID; // 제품 아이디
    private Date expiredDate; // 만료일
    private String hostName;
    private String ip; // 맥어드레스
    private boolean checkHostname = false;
    private int softwareCount;	// 제품 개수.
    private String[] softwareNames;	// 제품명 목록.
    private int androidAppIDCount;
    private int iOSAppIDCount;
    private int platformTypesCount;
    private String[] appIDs; //appID 목록.
    private String[] androidAppIDs; // android appID 목록
    private String[] iOSAppIDs; // iOS app ID 목록
    private String[] platformTypes; // platform 목록
    /**
     * 객체에 있는 데이터의 초기 상태를 나태낸다. 라이선스 스트링의 암호가 해독되지 않는 한 거짓이다.
     */
    private boolean isDataLoaded = false;
    private String encipheredLicenseString;

    private static final Logger logger = LoggerFactory.getLogger(AbstractWebSquareLicense.class);

    /**
     * 라이센스 파일로부터 라이선스 스트링을 추출한다. * 추상 decipherLicense를 호출하여 라이선스의 암호를 해독한다.
     *
     * @param encLicenseString
     *            BASE64 형식으로 암호화된 라이선스 스트링. 암호 해독시 아래의 포맷을 따라야 한다. <BR>
     *            <CODE> [licenseID]:[customerID]:[License Expire Date (yyyyMMdd)]:[host name]:[ip]:[cpu number]:[process number(N)]:[process name1]:...:[process nameN]
     * [client name]</CODE>
     */
    protected AbstractWebSquareLicense(String encLicenseString) throws InvalidLicenseException {

        encipheredLicenseString = encLicenseString;

    }

    /**
     * 라이선스 키의 암호를 해독하고, 클래스의 프라이빗 멤버로 데이터 값을 로드한다. 로드가 앞에서 발생하였다는 것을 서브 클래스가 명확하게 요청하지 않으면 로드는 천천히 발생할 것이다.
     *
     * @throws InvalidLicenseException
     *             라이선스 스트링 암호 해독이 제대로 이루어지지 않은 경우
     */
    protected final String loadData(String licenseString) throws Exception, InvalidLicenseException {
        String licenseInfoString = "";
        // 주의 : 명료성을 위해 NumberFormatException와 다른 분석 예외들이 생략되었음을 처리
        try {
            String OS = System.getProperty("java.vm.vendor");
            if (OS.startsWith("IBM")) {
                // IBMJCE Provider를 등록한다.
                // Install IBMJCE provider
                java.security.Provider IBMJce = java.security.Security.getProvider("IBMJCE");
                if (IBMJce == null) {
                    Class c = Class.forName("com.ibm.crypto.provider.IBMJCE");
                    IBMJce = (java.security.Provider) c.newInstance();
                    java.security.Security.addProvider(IBMJce);
                }
            } else {
                // SunJCE Provider를 등록한다.
                // Install SunJCE provider
                java.security.Provider sunJce = java.security.Security.getProvider("SunJCE");
                if (sunJce == null) {
                    Class c = Class.forName("com.sun.crypto.provider.SunJCE");
                    sunJce = (java.security.Provider) c.newInstance();
                    java.security.Security.addProvider(sunJce);
                }
            }
        } catch (Throwable ex) {
            System.err.println("Provider registration failed. Use default Provider.");
               
            try {
                // 현재 제공되고 있는 Provider를 찍는다.
                System.err.println("******************** PROVIDERS INSTALLED *********************************");
                java.security.Provider[] providers = java.security.Security.getProviders();
                for (int i = 0; i < providers.length; i++) {
                    System.err.println("[" + i + "] (" + providers[i].getName() + ") : " + providers[i].getInfo());
                }
                System.err.println("******************** PROVIDERS INSTALLED *********************************");
            } catch (Exception ex1) {

                logger.warn(ex1.getMessage(), ex1);
            }
        }

        // 평문으로 된 라이선스 정보 얻기
        String clearLicense = decipherLicense(encipheredLicenseString);
        // 스트링 분석
        StringTokenizer tok = new StringTokenizer(clearLicense, ":");

        try {

            licenseID = Integer.parseInt(tok.nextToken());
            customerID = Integer.parseInt(tok.nextToken());
            Name = tok.nextToken();
            licenseType = Integer.parseInt(tok.nextToken());
//            hostName = tok.nextToken();
//            ip = tok.nextToken();
            productID = Integer.parseInt(tok.nextToken());
            String dateString = tok.nextToken();
//            CPU = Integer.parseInt(tok.nextToken());
//            if (tok.hasMoreTokens()) {
//                String strCheckHostname = tok.nextToken();
//                if (strCheckHostname != null && strCheckHostname.equals("true")) {
//                    checkHostname = true;
//                }
//            }
            // 인스턴스 변수 초기화
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

            // 제품명 가져오기.
            if (tok.hasMoreTokens()) {
                softwareCount = Integer.parseInt(tok.nextToken());
                softwareNames = new String[softwareCount];
                for (int i=0; i<softwareCount; i++) {
                    softwareNames[i] = tok.nextToken();
                }
            } else {
                softwareCount = 0;
            }

            //appID 가져오기. hive 는 Android/iOS 두가지로 구분한다.
            // Android AppID 가져오기
            if (tok.hasMoreTokens()) {
                androidAppIDCount = Integer.parseInt(tok.nextToken());
                androidAppIDs = new String[androidAppIDCount];
                for (int i=0; i<androidAppIDCount; i++) {
                    androidAppIDs[i] = tok.nextToken();
                }
            } else {
                androidAppIDCount = 0;
            }

            // iOS AppID 가져오기
            if (tok.hasMoreTokens()) {
                iOSAppIDCount = Integer.parseInt(tok.nextToken());
                iOSAppIDs = new String[iOSAppIDCount];
                for (int i=0; i<iOSAppIDCount; i++) {
                    iOSAppIDs[i] = tok.nextToken();
                }
            } else {
                iOSAppIDCount = 0;
            }

            // Platform Type List 가져오기
            if (tok.hasMoreTokens()) {
                platformTypesCount = Integer.parseInt(tok.nextToken());
                platformTypes = new String[platformTypesCount];
                for (int i=0; i<platformTypesCount; i++) {
                    platformTypes[i] = tok.nextToken();
                }
            } else {
                platformTypesCount = 0;
            }


            try {
                expiredDate = sdf.parse(dateString);
            } catch (ParseException pe) {
                logger.warn(pe.getMessage(), pe);
                throw new InvalidLicenseException("Invalid date in license key");
            }

            isDataLoaded = true;
        } catch (NoSuchElementException e) {
            logger.warn(e.getMessage(), e);
            throw new InvalidLicenseException("Cannot extract license information.[StringTokenizer.nextToken() Error]");
        } finally {

            // 라이센스아이디 : 고객아이디 : 고객이름 : 라이센스타입 : 호스트네임 : 맥어드레스 : 제품아이디 : 만료일 : CPU갯수
            licenseInfoString = licenseID + ":" + customerID + ":" + Name
                    + ":" + licenseType + ":" + productID + ":" + expiredDate ; // + hostName + ":" + ":" + ":" + CPU

        }
        return licenseInfoString;
    }

    /**
     * 라이선스 만료일 얻기
     *
     * @return 라이선스 만료일
     */
    public Date getExpiredDate() {
        return expiredDate;
    }

    /**
     * 허용된 CPU 개수 읽기
     *
     * @return CPU 개수
     */
    public int getCpuNumber() {
        // cpuNumber와 CPU가 CPU숫자를 저장하기 위해 사용되었는데, cpuNumber에는 아무런 값이 들어가고 있지 않았음. (2010.07.06)
        return CPU;
    }

    /**
     * 라이선스가 적용되는 ip 얻기
     *
     * @return ip
     */

    /**
     * 라이선스 번호 얻기
     *
     * @return 벤더가 생성한 라이선스 번호
     */
    public int getLicenseID() {
        return licenseID;
    }

    /**
     * 라이선스 클라이언트 얻기
     *
     * @return 라이선스가 적용되는 개인(혹은 조직)ID
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * 라이선스가 데모인지 여부
     *
     * @return 0이면 데모, 1이면 실버전
     */
    public String getIsDemo() {
        return ("0".equals(licenseType + "") ? "true" : "false");
    }

    /**
     * 고객 이름 얻기
     *
     * @return 고객 이름
     */
    public String getCustomerName() {
        // customerName과 Name이 고객 이름을 저장하기 위해 사용되었는데, customerName에는 아무런 값이 들어가고 있지 않았음. (2010.07.06)
        return Name;
    }

    public String[] getSoftwareNames() {
        return softwareNames;
    }


    public String getHostName() {
        return hostName;
    }

    public String getIP() {
        return ip;
    }

    public int getProcessNumber() {
        return CPU;
    }

    // AppID string array return
    public String[] getAppIDs() {
        return appIDs;
    }

    // Android AppID, iOS App ID String array return 추가 해야함
    public String[] getAndroidAppIDs() { return androidAppIDs; }

    public String[] getiOSAppIDs() { return iOSAppIDs; }

    // 그리고 platform string array return 도 추가하기
    public String[] getPlatformTypes() { return platformTypes; }

    /**
     * 라이선스가 유효한지를 결정하는 핼퍼 메소드. 로컬 시스템 날짜 사용. 보안 강화를 위해 타임 서버 추가 가능.
     *
     * @return 만료일이 지났으면 참, 라이선스가 여전히 유효하면 거짓
     */
    public boolean isValid() {
        if (!isDataLoaded) { return false; }
        // CPU 수 체크 필요
        int systemCpuNumber = 0;
        String systemHostName = null;
        String[] systemIPs = null;
        try {
            systemCpuNumber = SystemLicenseInfo.getCpuNumber();
            systemHostName = SystemLicenseInfo.getHostname();
            systemIPs = SystemLicenseInfo.getIPArray();
        } catch (Exception ex) {
            System.err.println("Exception occured while retrieving system information.");
            logger.warn(ex.getMessage(), ex);
            return false;
        }

        // productCount가 1이상이면 productNames를 이용해 출력. 0이면 productID 사용하여 기존대로 출력.
        if (softwareCount > 0) {
            for (int i=0; i<softwareNames.length;i++) {
                System.err.println("############### LICENSE INFO["+(i+1)+"] ####################");
                System.err.println("License ID : " + licenseID);
                System.err.println("Customer ID: " + customerID);
                System.err.println("Name       : " + Name);
                if ("0".equals(licenseType + "")) {
                    System.err.println("License Type: (Demo License)");
                } else {
                    System.err.println("License Type: (Real License)");
                }
//                System.err.println("HOST Name  : " + hostName);
//                System.err.println("IP Address : " + ip);
                System.err.println("Product Name : " + softwareNames[i]);
//                System.err.println("CPU Number : " + CPU);
                System.err.println("Expiration : " + expiredDate);
                System.err.println("#################################################\n");
            }
        } else {
            System.err.println("############### LICENSE INFO ####################");
            System.err.println("License ID : " + licenseID);
            System.err.println("Customer ID: " + customerID);
            System.err.println("Name       : " + Name);
            if ("0".equals(licenseType + "")) {
                System.err.println("License Type: (Demo License)");
            } else {
                System.err.println("License Type: (Real License)");
            }
//            System.err.println("HOST Name  : " + hostName);
//            System.err.println("IP Address : " + ip);
            System.err.println("Product ID : " + productID);
//            System.err.println("CPU Number : " + CPU);
            System.err.println("Expiration : " + expiredDate);
            System.err.println("#################################################\n");
        }
        System.err.println("############### SYSTEM INFO #####################");
//        if (systemCpuNumber > 0) {
//            System.err.println("CPU Number : " + systemCpuNumber);
//        }
//        System.err.println("HOST Name  : " + systemHostName);
//        if( systemIPs != null ) {
//            for( int i=0; i<systemIPs.length; i++ ) {
//                System.err.println("IP["+i+"]      : " + systemIPs[i] );
//            }
//        } else {
//            System.err.println("IP         : " + null );
//        }
        System.err.println("#################################################");

//        if (CPU < systemCpuNumber) {
//            System.err.println("\nCPU of system exceed license file. Purchase additional License.");
//            return false;
//        }

//        if (checkHostname) {
//            if (!systemHostName.toLowerCase().equals("") && !systemHostName.toLowerCase().equals("localhost") && !hostName.toLowerCase().equals("") && !hostName.toLowerCase().equals("any") && !hostName.toLowerCase().equals("dev") && !hostName.toLowerCase().equals(systemHostName.toLowerCase())) {
//                System.err.println("\nHostname does not match to license file. Purchase additional License.");
//                return false;
//            }
//        }

//        if (systemIPs == null) {
//            System.err.println("\nIP address does not found.");
//            return false;
//        } else {
//            int i = 0;
//            for (; i < systemIPs.length; i++) {
//                String systemIP = systemIPs[i].replaceAll(":", "");
//
//                if (ip.indexOf('/') == -1) {
//                    if (ip.equals("0.0.0.0") || ip.equals(systemIP) || isValidRangeIP(systemIPs[i])) {
//                        break;
//                    }
//                } else {
//                    if (ip.equals("0.0.0.0") || ip.equals(systemIP) || isValidRangeIP(ip, systemIPs[i])) {
//                        break;
//                    }
//                }
//            }
//            if (i == systemIPs.length) {
//                System.err.println("\nIP address does not match to license file. If IP address change, please reissue liencse.");
//                return false;
//            }
//        }

        if (expiredDate.getTime() + 86400000 < new Date().getTime()) {
            System.err.println("License expired. Purchase additional License.");
            return false;
        }
        return true;
    }

    /**
     * @param systemIP
     * @return System IP : A.B.C.D, License IP : A.B.C.E return D equals E or E is zero
     */
    private boolean isValidRangeIP(String systemIP) {
        int index = ip.lastIndexOf(".");
        if (index < 0) {
            return false;
        }

        String ipRange = ip.substring(0, index);
        if ("0".equals(ip.substring(index + 1))) {
            index = systemIP.lastIndexOf(".");
            if (index < 0) {
                return false;
            } else {
                boolean retVal = systemIP.substring(0, index).equals(ipRange);
                if (retVal) {
                    System.out.println("[AbstractWebSquareLicense.isValidRangeIP] Not equal exactly, but is contained range");
                }
                return retVal;
            }
        }

        return false;
    }

    /**
     * @param systemIP
     * @return ip 10.10.10.20/24 (CIDR), System IP : A.B.C.D
     */
    private boolean isValidRangeIP(String licenseIP, String systemIP) {
        int licenseIdx = ip.lastIndexOf(".");
        int systemIdx = systemIP.lastIndexOf(":");
        if (licenseIdx < 0 || systemIdx > 0) {
            return false;
        }

        String IP = "";
        String subNetBit = "";
        String[] CIDR = new String[2];
        if (licenseIP.indexOf("/") > -1) {
            CIDR = licenseIP.split("/", 2);
            IP = CIDR[0];
            subNetBit = CIDR[1];
        }

        int ip32Bit = IPv4CIDR.calcIPtoInt(IP);
        int subNetBitLength = Integer.parseInt(subNetBit);
        int ip32BitMask = (1 << (32 - subNetBitLength)) - 1;

        int startIP = ip32Bit & (~ip32BitMask);
        int endIP = startIP | ip32BitMask;
        int serverIP = IPv4CIDR.calcIPtoInt(systemIP);

        String startIPv4 = IPv4CIDR.getIPv4ByInt(startIP);
        String endIPv4 = IPv4CIDR.getIPv4ByInt(endIP);

        String addressRange = startIPv4 + " ~ " + endIPv4;
        if( startIP <= serverIP && serverIP <= endIP ) {
            System.out.println("[AbstractWebSquareLicense.isValidRangeIP] is contained range. " + addressRange);
            return true;
        } else {
            System.out.println("[AbstractWebSquareLicense.isValidRangeIP] is not contained range. " + addressRange);
            return false;
        }
    }

    /**
     * 이 메소드는 모든 서브 클래스에 의해 구현되어야 하며, 암호화된 라이선스 스트링의 BASE64 인코디드 버전을 받아, 동일한 스트링의 평문 버전을 반환한다.
     *
     * @param origLicense
     *            암호화된 라이선스 스트링 (BASE64)
     * @return 평문 라이선스 스트링
     * @throws InvalidLicenseException
     *             라이선스 스트링이 유효하지 않을 경우
     */
    protected abstract String decipherLicense(String origLicense) throws InvalidLicenseException;


}
