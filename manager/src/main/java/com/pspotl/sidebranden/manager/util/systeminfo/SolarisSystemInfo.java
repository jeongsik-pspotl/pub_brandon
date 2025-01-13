package com.pspotl.sidebranden.manager.util.systeminfo;

import com.pspotl.sidebranden.manager.util.system.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.StringTokenizer;

public class SolarisSystemInfo implements SystemInfoIF {

    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";
    private String osName;

    private static final Logger logger = LoggerFactory.getLogger(SolarisSystemInfo.class);

    /**
     * Constructor for SolarisSystemInfo.
     */
    public SolarisSystemInfo(String osName) {
        this.osName = osName;
    }

    @Override
    public String getOsName() {
        return osName;
    }

    @Override
    public int getCPUNumber() {
        try {
            String[] result = new Execute().execArray("/usr/sbin/psrinfo");
            String out = result[1];

            int number = 0;

            int pointer = 0;
            while (true) {
                pointer = out.indexOf("Status of processor", pointer);
                if (pointer < 0)
                    break;
                number++;
                pointer += 19;
            }
            return number;
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            return -1;
        }
    }

    @Override
    public double getProcessCPUUsage() {
        BufferedReader br = null;
        try {
            int processID = getProcessID();
            String[] result = new Execute().execArray("/usr/ucb/ps -aux | grep pid | grep -v 'grep'");
            String out = result[1];

            br = new BufferedReader(new StringReader(out));
            String s;
            while ((s = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(s, " ", false);
                st.nextToken();
                String pid = st.nextToken();
                if (pid.equals("" + processID)) { return Double.parseDouble(st.nextToken()); }
            }

            return 0;
        } catch (Exception ex) {
            return -1;
        } finally {
            try {
                if(br != null) br.close();
            } catch (Exception ignored) {}
        }
    }

    @Override
    public int getProcessID() {
        return -1;
    }
}
