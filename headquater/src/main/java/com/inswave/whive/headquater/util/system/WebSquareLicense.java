package com.inswave.whive.headquater.util.system;

import java.util.Date;

public interface WebSquareLicense {

    /**
     * 만료일
     */
    Date getExpiredDate();

    /**
     * CPU 개수
     */
    int getCpuNumber();

    /**
     * HOSTNAME
     */
    String getHostName();

    /**
     * IP
     */
    String getIP();

    /**
     *
     */
    int getProcessNumber();

    /**
     *
     */
    String[] getSoftwareNames();

    /**
     * 라이센스 ID
     */
    int getLicenseID();

    /**
     */
    String getIsDemo();

    /**
     */
    int getCustomerID();

    /**
     */
    String getCustomerName();

    /**
     */
    boolean isValid();


    String[] getAppIDs();


    String[] getAndroidAppIDs();


    String[] getiOSAppIDs();


    String[] getPlatformTypes();

}
