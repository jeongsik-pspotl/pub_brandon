package com.inswave.whive.headquater.util.systeminfo;

import com.inswave.whive.headquater.util.system.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.StringTokenizer;

public class NativeSolarisSystemInfo extends AbstractUnixSystemInfo {

    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";

    private static final Logger logger = LoggerFactory.getLogger(NativeSolarisSystemInfo.class);

    /**
     * Constructor for NativeSolarisSystemInfo.
     */
    public NativeSolarisSystemInfo(String osName) {
        super(osName);
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
            if (br != null) {
                try {
                    br.close();
                } catch (Exception ignored) {};
            }
        }
    }
}
