package com.pspotl.sidebranden.manager.util.systeminfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUnixSystemInfo implements SystemInfoIF {

    private String osName;
    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";
    private static final String SILIB = "silib";

    private static final Logger logger = LoggerFactory.getLogger(AbstractUnixSystemInfo.class);

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
                // "[AbstractUnixSystemInfo] java.library.path:" +
                System.loadLibrary(SILIB);
            }
        } catch (UnsatisfiedLinkError e) {
            System.err.println("[AbstractUnixSystemInfo] native lib '" + SILIB + "' not found in 'java.library.path': " + System.getProperty("java.library.path"));
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Constructor for AbstractUnixSystemInfo.
     */
    public AbstractUnixSystemInfo(String osName) {
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
    public abstract int getCPUNumber();

    /**
     * @see websquare.util.SystemInfo.SystemInfoIF#getProcessCPUUsage()
     */
    public abstract double getProcessCPUUsage();

    /**
     * @see websquare.util.SystemInfo.SystemInfoIF#getProcessID()
     */
    public native int getProcessID();

}
