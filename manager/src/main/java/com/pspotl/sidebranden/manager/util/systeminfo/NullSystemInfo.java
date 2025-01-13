package com.pspotl.sidebranden.manager.util.systeminfo;

import com.pspotl.sidebranden.manager.util.SystemInfo;

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
     * @see SystemInfo.SystemInfoIF#getOsName()
     */
    @Override
    public String getOsName() { return osName; }

    /**
     * @see SystemInfo.SystemInfoIF#getCPUNumber()
     */
    @Override
    public int getCPUNumber() {
        return -1;
    }

    /**
     * @see SystemInfo.SystemInfoIF#getProcessCPUUsage()
     */
    @Override
    public double getProcessCPUUsage() {
        return -1;
    }

    /**
     * @see SystemInfo.SystemInfoIF#getProcessID()
     */
    @Override
    public int getProcessID() {
        return -1;
    }
}
