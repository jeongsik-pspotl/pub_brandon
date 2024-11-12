package com.inswave.whive.headquater.util;

import java.util.ArrayList;

public class PostControllerException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private final String nameSpace;

    private ArrayList<String> errorMsgArr;

    public PostControllerException(String nameSpace, String msg) {
        super(msg);
        this.nameSpace = nameSpace;
    }

    public PostControllerException(String nameSpace, ArrayList<String> msgArr) {
        super(msgArr.get(0));
        this.nameSpace = nameSpace;
        this.errorMsgArr = msgArr;
    }

    public PostControllerException(String nameSpace, String errorcode, String msg) {
        super(msg);
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public ArrayList<String> getErrorMsgArr() {
        return errorMsgArr;
    }

    public String getMessage() {
        String message = super.getMessage();
        return errorMessage(message);
    }

    public String getMsg() {
        String message = super.getMessage();
        return message;
    }

    protected String errorMessage(String message) {
        return "<Exception><message><![CDATA[]]></message></Exception>";
    }

}
