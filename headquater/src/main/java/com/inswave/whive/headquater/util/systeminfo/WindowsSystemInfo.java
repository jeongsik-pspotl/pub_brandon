package com.inswave.whive.headquater.util.systeminfo;

import com.inswave.whive.headquater.util.system.Execute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WindowsSystemInfo implements SystemInfoIF {

    public static final String version = "1.00";
    public static final String buildDate = "2006-02-08";
    private String osName;

    private static final Logger logger = LoggerFactory.getLogger(WindowsSystemInfo.class);

    /**
     * Constructor for WindowsSystemInfo.
     */
    public WindowsSystemInfo(String osName) {
        this.osName = osName;
    }


    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getOsName()
     */
    @Override
    public String getOsName() {
        return osName;
    }

    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getCPUNumber()
     */
    @Override
    public int getCPUNumber() {
        try {
            String[] result = new Execute().execArray("cmd /c echo %NUMBER_OF_PROCESSORS%");
            String out = result[1].trim();
            int number = 0;
            try {
                number = Integer.parseInt(out);
            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
            return number;
        } catch (Exception ex) {
            logger.warn(ex.getMessage(), ex);
            return -1;
        }
    }

    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getProcessCPUUsage()
     */
    @Override
    public double getProcessCPUUsage() {
        return 0;
    }

    /**
     * @see com.inswave.whive.headquater.util.SystemInfo.SystemInfoIF#getProcessID()
     */
    @Override
    public int getProcessID() {
        return -1;
    }
}
