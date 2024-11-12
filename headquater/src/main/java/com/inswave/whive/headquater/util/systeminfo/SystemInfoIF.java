package com.inswave.whive.headquater.util.systeminfo;

public interface SystemInfoIF {

    public static String version = "1.00";
    public static String buildDate = "2006-02-08";

    public String getOsName();

    public int getCPUNumber();

    public double getProcessCPUUsage();

    public int getProcessID();
}
