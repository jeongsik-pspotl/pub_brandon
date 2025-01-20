package com.pspotl.sidebranden.manager.util;

import com.pspotl.sidebranden.manager.util.system.AbstractResource;
import com.pspotl.sidebranden.manager.util.system.InvalidLicenseException;
import com.pspotl.sidebranden.manager.util.system.SimpleSymmetricWebSquareLicense;
import com.pspotl.sidebranden.manager.util.system.WebSquareLicense;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Utility {

    private static AbstractResource resImpl;

    private static boolean demoLicense = false;

    public static String toPlatform(String path) {
        if (path != null && !path.equals("")) {
            if (File.separatorChar == '/') {
                path = toUnixFileSystem(path);
            } else {
                path = toDos(path);
            }

            return path;
        } else {
            return "";
        }
    }

    public static String toUnixFileSystem(String path) {
        if (path != null && !path.equals("")) {
            path = path.replace('\\', '/');
            path = path.replaceAll("[/]+", "/");
            if (path.startsWith("file:/")) {
                path = path.substring(6);
            }

            if (path.length() > 1 && path.endsWith(File.separator)) {
                path = path.substring(0, path.length() - 1);
            }

            return path;
        } else {
            return "";
        }
    }

    private static String toDos(String path) {
        path = toUnixFileSystem(path);
        path = path.replace('/', '\\');
        if (path.length() > 0 && path.charAt(0) == '\\' && path.indexOf(58) > -1) {
            path = path.substring(1);
        }

        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }

        return path;
    }

//    private static Vector readLicenseFileList() throws IOException {
//        String licenseFileName = System.getProperty("WHIVE_LICENSE_NAME");
//        if( licenseFileName == null || licenseFileName.length() == 0 ) {
//            licenseFileName = "license";
//        }
//        // 라이센스 파일에서 해당 라이센스 키 가져오는 부분
//        String whiveHome = ""; //WHybridServlet.whybridHome; // 고민해봐야할 소스 코드 구간..
//        if(whiveHome == null || whiveHome.equals( "" ) ) {
//            whiveHome = toPlatform(System.getProperty("WHIVE_HOME"));
//        }
//        if( whiveHome == null || whiveHome.equals( "" ) ) {
//            whiveHome = toPlatform(System.getProperty("WHIVE_HOME"));
//        }
//
//        if( whiveHome == null || whiveHome.equals("")){
//            whiveHome = toPlatform(System.getenv("WHIVE_HOME"));
//        }
//
//        if( whiveHome == null && System.getProperty("osgi.install.area") != null ) {
//            whiveHome = toPlatform(System.getProperty("osgi.install.area") );
//        }
//        String installArea = toPlatform( whiveHome );
//
//        if (installArea == null || installArea.equals("")) { throw new IOException("WHIVE_HOME is null"); }
//
//        String path = "";
//        if (installArea.indexOf("file:/") > -1 || installArea.indexOf("file:" + File.separator) > -1) { // 윈도우에서만 테스트됨.
//            path = installArea.substring(6, installArea.length() - 1) + File.separator + "license";
//        } else {
//            path = installArea + File.separator + "license";
//        }
//
//        Vector licenseStrList = new Vector();
//        String licensePath = "";
//        String readerImpl = System.getProperty("WHIVE_READER");
//
//        if(readerImpl != null && !readerImpl.equals("")) {
//            try {
//                Class c = ClassUtil.get(readerImpl);
//                resImpl = (AbstractResource)c.newInstance();
//                String licenseStr[] = resImpl.getResource(path, "license");
//                for(int i=0; i<licenseStr.length; i++) {
//                    licenseStrList.add(licenseStr[i]);
//                }
//            } catch(Exception e) {
//                System.err.println("AbstractResource 초기화 중 에러가 발생하였습니다. [" + readerImpl + "]");
//            }
//        } else if(path.indexOf("jar:") > -1) {
//            path = path.replaceAll("jar:", "");
//            path = path + File.separator;
//
//            InputStream is = null;
//            BufferedReader rd = null;
//            try {
//                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//                if (classloader == null) {
//                    classloader = ClassLoader.getSystemClassLoader();
//                }
//                URL dirURL = classloader.getResource(path);
//                String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
//
//                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
//                Enumeration entries = jar.entries();
//                while(entries.hasMoreElements()) {
//                    String name = ((ZipEntry)entries.nextElement()).getName();
//                    if(name.startsWith(path)) {
//                        String entry = name.substring(path.length());
//                        int checkSubdir = entry.indexOf("/");
//                        if (checkSubdir >= 0) {
//                            entry = entry.substring(0, checkSubdir);
//                        }
//                        licensePath = path + entry;
//
//                        is = classloader.getResourceAsStream(licensePath);
//                        rd = new BufferedReader(new InputStreamReader(is));
//                        String line;
//                        StringBuffer sb = new StringBuffer();
//                        while((line = rd.readLine()) != null) {
//                            sb.append(line);
//                        }
//
//                        licenseStrList.add(sb.toString());
//                    }
//                }
//            } finally {
//                if( is != null ) {
//                    is.close();
//                }
//
//                if( rd != null ) {
//                    rd.close();
//                }
//            }
//        } else {
//            File licenseFolder = new File(path);
//            File[] licenseFileList = licenseFolder.listFiles();
//
//            for(int i=0; i<licenseFileList.length; i++) {
//                BufferedReader br = null;
//                try {
//                    if(licenseFileList[i].isDirectory() || licenseFileList[i].getName().equals(".DS_Store")) {
//                        continue;
//                    }
//
//                    licensePath = path + File.separator + licenseFileList[i].getName();
//                    System.err.println("Loading License. PATH: " + licensePath);
//
//                    br = new BufferedReader(new FileReader(licensePath));
//
//                    int readCount = -1;
//                    char[] buffer = new char[1024];
//                    while (true) {
//                        readCount = br.read(buffer);
//                        break;
//                    }
//
//                    if(readCount != -1) {
//                        licenseStrList.add(new String(buffer, 0, readCount));
//                    }
//                } finally {
//                    try {
//                        if (br != null) {
//                            try {
//                                br.close();
//                            } catch (Exception ignored) {};
//                        }
//                    } catch (Exception ignored) {}
//                }
//            }
//        }
//        return licenseStrList;
//    }private static Vector readLicenseFileList() throws IOException {
//        String licenseFileName = System.getProperty("WHIVE_LICENSE_NAME");
//        if( licenseFileName == null || licenseFileName.length() == 0 ) {
//            licenseFileName = "license";
//        }
//        // 라이센스 파일에서 해당 라이센스 키 가져오는 부분
//        String whiveHome = ""; //WHybridServlet.whybridHome; // 고민해봐야할 소스 코드 구간..
//        if(whiveHome == null || whiveHome.equals( "" ) ) {
//            whiveHome = toPlatform(System.getProperty("WHIVE_HOME"));
//        }
//        if( whiveHome == null || whiveHome.equals( "" ) ) {
//            whiveHome = toPlatform(System.getProperty("WHIVE_HOME"));
//        }
//
//        if( whiveHome == null || whiveHome.equals("")){
//            whiveHome = toPlatform(System.getenv("WHIVE_HOME"));
//        }
//
//        if( whiveHome == null && System.getProperty("osgi.install.area") != null ) {
//            whiveHome = toPlatform(System.getProperty("osgi.install.area") );
//        }
//        String installArea = toPlatform( whiveHome );
//
//        if (installArea == null || installArea.equals("")) { throw new IOException("WHIVE_HOME is null"); }
//
//        String path = "";
//        if (installArea.indexOf("file:/") > -1 || installArea.indexOf("file:" + File.separator) > -1) { // 윈도우에서만 테스트됨.
//            path = installArea.substring(6, installArea.length() - 1) + File.separator + "license";
//        } else {
//            path = installArea + File.separator + "license";
//        }
//
//        Vector licenseStrList = new Vector();
//        String licensePath = "";
//        String readerImpl = System.getProperty("WHIVE_READER");
//
//        if(readerImpl != null && !readerImpl.equals("")) {
//            try {
//                Class c = ClassUtil.get(readerImpl);
//                resImpl = (AbstractResource)c.newInstance();
//                String licenseStr[] = resImpl.getResource(path, "license");
//                for(int i=0; i<licenseStr.length; i++) {
//                    licenseStrList.add(licenseStr[i]);
//                }
//            } catch(Exception e) {
//                System.err.println("AbstractResource 초기화 중 에러가 발생하였습니다. [" + readerImpl + "]");
//            }
//        } else if(path.indexOf("jar:") > -1) {
//            path = path.replaceAll("jar:", "");
//            path = path + File.separator;
//
//            InputStream is = null;
//            BufferedReader rd = null;
//            try {
//                ClassLoader classloader = Thread.currentThread().getContextClassLoader();
//                if (classloader == null) {
//                    classloader = ClassLoader.getSystemClassLoader();
//                }
//                URL dirURL = classloader.getResource(path);
//                String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
//
//                JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
//                Enumeration entries = jar.entries();
//                while(entries.hasMoreElements()) {
//                    String name = ((ZipEntry)entries.nextElement()).getName();
//                    if(name.startsWith(path)) {
//                        String entry = name.substring(path.length());
//                        int checkSubdir = entry.indexOf("/");
//                        if (checkSubdir >= 0) {
//                            entry = entry.substring(0, checkSubdir);
//                        }
//                        licensePath = path + entry;
//
//                        is = classloader.getResourceAsStream(licensePath);
//                        rd = new BufferedReader(new InputStreamReader(is));
//                        String line;
//                        StringBuffer sb = new StringBuffer();
//                        while((line = rd.readLine()) != null) {
//                            sb.append(line);
//                        }
//
//                        licenseStrList.add(sb.toString());
//                    }
//                }
//            } finally {
//                if( is != null ) {
//                    is.close();
//                }
//
//                if( rd != null ) {
//                    rd.close();
//                }
//            }
//        } else {
//            File licenseFolder = new File(path);
//            File[] licenseFileList = licenseFolder.listFiles();
//
//            for(int i=0; i<licenseFileList.length; i++) {
//                BufferedReader br = null;
//                try {
//                    if(licenseFileList[i].isDirectory() || licenseFileList[i].getName().equals(".DS_Store")) {
//                        continue;
//                    }
//
//                    licensePath = path + File.separator + licenseFileList[i].getName();
//                    System.err.println("Loading License. PATH: " + licensePath);
//
//                    br = new BufferedReader(new FileReader(licensePath));
//
//                    int readCount = -1;
//                    char[] buffer = new char[1024];
//                    while (true) {
//                        readCount = br.read(buffer);
//                        break;
//                    }
//
//                    if(readCount != -1) {
//                        licenseStrList.add(new String(buffer, 0, readCount));
//                    }
//                } finally {
//                    try {
//                        if (br != null) {
//                            try {
//                                br.close();
//                            } catch (Exception ignored) {};
//                        }
//                    } catch (Exception ignored) {}
//                }
//            }
//        }
//        return licenseStrList;
//    }

//    public static void checkHybridLicense(ArrayList<String> androidAppIDList, ArrayList<String> iOSAppIDList, ArrayList<String> platformTypeList) throws PostControllerException {
//        if (SimpleSymmetricWebSquareLicense.status()) {
//            return; // 이미 체크되었으므로 반환
//        }
//
//        Vector strLicense;
//        boolean validLicense = false;
//
//        try {
//            strLicense = readLicenseFileList();
//            if(strLicense.size() == 0){
//                throw new PostControllerException("Utility","Not Found WHive license file.");
//            }
//        } catch (IOException e) {
//            System.err.println("[Util.checkHybridLicense] Fail to read WHive license file [" + e.getMessage() + "]");
//            throw new PostControllerException("Utility", "Fail to read WHive license file.[" + e.getMessage() + "]");
//        }
//
//        try {
//            for(int l=0; l<strLicense.size(); l++) {
//                System.err.println("############### WHIVE LICENSE File["+(l+1)+"] ####################");
//
//                WebSquareLicense license = new SimpleSymmetricWebSquareLicense((String)strLicense.get(l));
//                if (license.isValid()) {
//
//                    // androidAppIDList set
//                    for(int b = 0 ; b < license.getAndroidAppIDs().length ; b++){
//                        androidAppIDList.add(license.getAndroidAppIDs()[b]);
//                    }
//
//                    // iOSAppIDList set
//                    for(int c = 0 ; c < license.getiOSAppIDs().length ; c++){
//                        iOSAppIDList.add(license.getiOSAppIDs()[c]);
//                    }
//
//                    // platformTypeList set
//                    for(int d = 0 ; d < license.getPlatformTypes().length ; d++){
//                        platformTypeList.add(license.getPlatformTypes()[d]);
//                    }
//
//                    demoLicense = license.getIsDemo().equalsIgnoreCase("true");
//                    validLicense = true;
//                    break;
//                }
//            }
//
//            if(!validLicense) {
//                throw new InvalidLicenseException("Invalid hive license");
//            }
//        } catch (InvalidLicenseException e) {
//            System.err.println("[Utility.checkHybridLicense] Invalid license file [" + e.getMessage() + "]");
//            throw new PostControllerException("Utility", "Invalid license file..");
//        }
//    }


    public static boolean getIsDemoLicense(){
        return demoLicense;
    }

}
