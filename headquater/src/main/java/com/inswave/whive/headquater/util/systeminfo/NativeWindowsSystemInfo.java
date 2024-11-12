package com.inswave.whive.headquater.util.systeminfo;

import com.inswave.whive.headquater.util.system.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NativeWindowsSystemInfo implements SystemInfoIF{

    private String osName;
    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";
    private static final String SILIB = "silib";

    private static final Logger logger = LoggerFactory.getLogger(NativeWindowsSystemInfo.class);

    static {
        // loading a native lib in a static initializer ensures that it is
        // available done before any method in this class is called:
        try {
            String jni = System.getProperty("websquare.jni");
            if (jni == null) {
                jni = "false";
            }
            if (jni.toLowerCase().equals("true")) {
                // LogUtil.printDebug(
                // "[NativeWindowsSystemInfo] java.library.path:" +
                System.loadLibrary(SILIB);
            }
        } catch (UnsatisfiedLinkError e) {
            logger.warn(e.getMessage(), e);
            System.err.println("[NativeWindowsSystemInfo] native lib '" + SILIB + "' not found in 'java.library.path': " + System.getProperty("java.library.path"));
        }
    }

    /**
     * Constructor for NativeWindowsSystemInfo.
     */
    public NativeWindowsSystemInfo(String osName) {
        this.osName = osName;
    }

    /**
     * @see websquare.util.SystemInfo.SystemInfoIF#getOsName()
     */
    public String getOsName() {
        return osName;
    }

    /**
     * @see websquare.util.SystemInfo.SystemInfoIF#getCPUNumber()
     */
    public int getCPUNumber() {

        try {
            String[] result = new Execute().execArray("cmd /c echo %NUMBER_OF_PROCESSORS%");
            String out = result[1].trim();
            int number = 0;
            try {
                number = Integer.parseInt(out);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            return number;
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            return -1;
        }
    }

    /**
     * @see websquare.util.SystemInfo.SystemInfoIF#getProcessCPUUsage()
     */
    public double getProcessCPUUsage() {
        return 0;
    }

    /**
     * @see websquare.util.SystemInfo.SystemInfoIF#getProcessID()
     */
    public native int getProcessID();
}
