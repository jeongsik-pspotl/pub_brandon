package com.inswave.whive.headquater.util.system;

public class InvalidLicenseException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -5493973796621070554L;

    /**
     * Constructor InvalidLicenseException.
     * @param string
     */
    public InvalidLicenseException(String string) {

        super(string);

    }

    public InvalidLicenseException(Exception ex) {

        super(ex);

    }

}
