package com.inswave.whive.branch.domain;

public class BuildResponse {

    private ResponseResult responseResult;
    private String buildTaskId;
    private String sessionId;

    public BuildResponse() {
    }

    public BuildResponse(ResponseResult responseResult, String buildTaskId, String sessionId) {
        this.responseResult = responseResult;
        this.buildTaskId = buildTaskId;
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getbuildTaskId() {
        return buildTaskId;
    }

    public void setbuildTaskId(String buildTaskId) {
        this.buildTaskId = buildTaskId;
    }

    public ResponseResult getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(ResponseResult responseResult) {
        this.responseResult = responseResult;
    }

    @Override
    public String toString() {
        return "BuildResponse{" + "responseResult=" + responseResult + ", buildTaskId='" + buildTaskId + '\'' + ", sessionId='" + sessionId + '\'' + '}';
    }

    public enum ResponseResult {
        SUCCESS, CANCEL, TIMEOUT;
    }
}
