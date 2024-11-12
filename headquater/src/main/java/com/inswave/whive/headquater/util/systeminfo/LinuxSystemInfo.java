package com.inswave.whive.headquater.util.systeminfo;

import com.inswave.whive.headquater.util.system.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.StringTokenizer;

public class LinuxSystemInfo implements SystemInfoIF {

    private String osName;
    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";

    private static final Logger logger = LoggerFactory.getLogger(LinuxSystemInfo.class);

    /**
     * Constructor LinuxSystemInfo.
     *
     * @param osName
     */
    public LinuxSystemInfo(String osName) {
        this.osName = osName;
    }

    @Override
    public String getOsName() {
        return osName;
    }

    @Override
    public int getCPUNumber() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
            StringBuffer sb = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String out = sb.toString();

            int number = 0;

            int pointer = 0;
            while (true) {
                pointer = out.indexOf("processor", pointer);
                if (pointer < 0)
                    break;

                int index = out.indexOf("vendor_id", ++pointer);
                if (index < 0) {
                    continue;
                } else {
                    number++;
                    pointer += 8;
                }
            }
            return number;
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            return -1;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {};
            }
        }
    }

    @Override
    public double getProcessCPUUsage() {

        BufferedReader br = null;
        try {
            int processID = getProcessID();
            String[] result = new Execute().execArray("ps -auxx | grep " + processID + " | grep -v 'grep'");
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
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {};
            }
        }
    }

    @Override
    public int getProcessID() {
        return -1;
    }
}
