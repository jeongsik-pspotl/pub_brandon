package com.pspotl.sidebranden.manager.util.systeminfo;

import com.pspotl.sidebranden.manager.util.system.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.StringTokenizer;

public class NativeIBMSystemInfo extends AbstractUnixSystemInfo {

    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";

    private static final Logger logger = LoggerFactory.getLogger(NativeIBMSystemInfo.class);

    /**
     * Constructor for NativeIBMSystemInfo.
     */
    public NativeIBMSystemInfo(String osName) {
        super(osName);
    }

    @Override
    public int getCPUNumber() {
        BufferedReader br = null;
        try {
            String[] result = new Execute().execArray("lscfg -l proc*");
            String out = result[1];

            int number = 0;
            br = new BufferedReader(new StringReader(out));
            String s;
            while ((s = br.readLine()) != null) {
                StringTokenizer st = new StringTokenizer(s, " ", false);
                // 2��° �׸��� DEVICE
                String device = st.nextToken();
                if (device.startsWith("proc")) {
                    number++;
                }
            }

            return number;
        } catch (Exception ex) {
            logger.warn(ex.getMessage(),ex);
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
            String[] result = new Execute().execArray("ps auxww | grep " + processID + " | grep -v 'grep'");
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
