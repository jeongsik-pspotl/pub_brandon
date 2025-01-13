package com.pspotl.sidebranden.manager.util;

import com.inswave.whive.headquater.util.systeminfo.*;
import com.pspotl.sidebranden.manager.util.systeminfo.*;

public class SystemInfo {
    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";
    private static SystemInfoIF systemInfoIF = SystemInfoFactory.getSystemInfo();

    public static String getOsName() {

        return systemInfoIF.getOsName();

    }

    /**
     * if exception occurs, return -1;
     */
    public static int getCPUNumber() {
        return systemInfoIF.getCPUNumber();
    }

    /**
     * Computes CPU usage (fraction of 1.0) between <code>start.m_CPUTime</code> and <code>end.m_CPUTime</code> time points [1.0 corresponds to 100% utilization of all processors]. if exception occurs, return -1;
     *
     */
    public static double getProcessCPUUsage() {
        return systemInfoIF.getProcessCPUUsage();
    }

    /**
     * Returns the PID of the current process. The result is useful when you need to integrate a Java app with external tools.
     */
    public static int getProcessID() {
        int pid = -1;
        try {
            pid = systemInfoIF.getProcessID();
            if (pid != -1) {
                System.out.println("[SystemInfo.getProcessID] process id : " + pid);
            }
        } catch (Throwable e) {}
        return pid;
    }

    private SystemInfo() {
    } // prevent subclassing

    private static class SystemInfoFactory {

        static SystemInfoIF getSystemInfo() {

            // aix
            // linux
            // solaris
            // unixware

            // compaq_alpha
            // hp
            // sol_x86

            String jni = System.getProperty("websquare.jni");
            if (jni == null) {
                jni = "false";
            }
            String osName = System.getProperty("os.name");
            System.out.println("[SystemInfoFactory.getSystemInfo] OS : " + osName + "\nJNI Enable : " + jni);
            if (jni.toLowerCase().equals("true")) {
                if (osName.startsWith("Linux")) {
                    return new NativeLinuxSystemInfo(osName);
                } else if (osName.startsWith("Windows")) {
                    return new NativeWindowsSystemInfo(osName);
                } else if (osName.startsWith("AIX")) {
                    return new NativeIBMSystemInfo(osName);
                } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
                    return new NativeSolarisSystemInfo(osName);
                } else {
                    return new NullSystemInfo(osName);
                }
            } else {
                if (osName.startsWith("Linux")) {
                    return new LinuxSystemInfo(osName);
                } else if (osName.startsWith("Windows")) {
                    return new WindowsSystemInfo(osName);
                } else if (osName.startsWith("AIX")) {
                    return new IBMSystemInfo(osName);
                } else if (osName.startsWith("Solaris") || osName.startsWith("SunOS")) {
                    return new SolarisSystemInfo(osName);
                } else {
                    return new NullSystemInfo(osName);
                }
            }

        }

    }
}
