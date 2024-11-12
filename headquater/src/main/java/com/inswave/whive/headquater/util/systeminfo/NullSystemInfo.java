package com.inswave.whive.headquater.util.systeminfo;

public class NullSystemInfo implements SystemInfoIF {
    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";
    private String osName;

    /**
     * Constructor for NullSystemInfo.
     */
    public NullSystemInfo(String osName) {
        this.osName = osName;
    }

    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getOsName()
     */
    @Override
    public String getOsName() { return osName; }

    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getCPUNumber()
     */
    @Override
    public int getCPUNumber() {
        return -1;
    }

    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getProcessCPUUsage()
     */
    @Override
    public double getProcessCPUUsage() {
        return -1;
    }

    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getProcessID()
     */
    @Override
    public int getProcessID() {
        return -1;
    }
}
