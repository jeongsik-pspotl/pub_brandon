package com.pspotl.sidebranden.manager.util;

import com.pspotl.sidebranden.manager.util.system.Execute;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.StringTokenizer;

public class SystemLicenseInfo {

    private SystemLicenseInfo() {
        super();
    }

    public static int getCpuNumber() throws Exception {
        return SystemInfo.getCPUNumber();
    }

    public static String getHostname() throws Exception {
        // proworks에 구현된 방식으로 변경 (2010.07.06)
        try {
            String[] result = new Execute().execArray("hostname");
            String out = result[1];
            return out.trim();
        } catch (Exception e) {
             
            try {
                return InetAddress.getLocalHost().getHostName();
            } catch (Exception ex) {
                   
                return "localhost";
            }
        }
    }

    /**
     * @deprecated This method can check only one network interface. System
     *             needs to check all of interface.
     * @see {@link SystemLicenseInfo#getIPArray()}
     */
    public static String getIP() throws Exception {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println("[SystemLicenseInfo.getIP] Exception"+ e.getMessage());

            try {
                String[] result = new Execute().execArray("ipconfig");
                String configInfo = result[1];
                StringTokenizer configSt = new StringTokenizer(configInfo, "\n");
                String[] ipStr = null;
                while (configSt.hasMoreElements()) {
                    String line = (String) configSt.nextElement();

                    if (line.trim().startsWith("IP Address")) {
                        ipStr = line.trim().split(":");
                        break;
                    }
                }

                String exceptionIp = "";

                if (ipStr != null) {
                    if (ipStr.length > 0) {
                        exceptionIp = ipStr[1].trim(); // return
                    }
                }

                return exceptionIp;

            } catch (Exception ex) {
                   
                return "127.0.0.1";
            }
        }
    }


    /**
     * System can have interfaces, one more. Then system needs to check all of
     * network interfaces.
     *
     * @return Array of Network Address
     */
    public static String[] getIPArray() throws Exception {
        String[] s = null;
        ArrayList al = new ArrayList();
        try {
            NetworkInterface ni = null;
            InetAddress i = null;
            Enumeration ne = NetworkInterface.getNetworkInterfaces();
            while (ne.hasMoreElements()) {
                ni = (NetworkInterface) ne.nextElement();
                Enumeration e = ni.getInetAddresses();
                while (e.hasMoreElements()) {
                    i = (InetAddress) e.nextElement();
                    al.add(i.getHostAddress());
                }
            }
            s = new String[al.size()];
            return (String[]) al.toArray(s);
        } catch (Throwable e) {
            try {
                String[] result = new Execute().execArray("ipconfig");
                String configInfo = result[1];
                StringTokenizer configSt = new StringTokenizer(configInfo, "\n");
                String[] ipStr = null;
                while (configSt.hasMoreElements()) {
                    String line = (String) configSt.nextElement();
                    if (line.trim().startsWith("IP Address")) {
                        ipStr = line.trim().split(":");
                        al.add(ipStr[1].trim());
                    }
                }
                s = new String[al.size()];
                return (String[]) al.toArray(s);
            } catch (Exception ex) {}
            return new String[] { "127.0.0.1" };
        }
    }
}
