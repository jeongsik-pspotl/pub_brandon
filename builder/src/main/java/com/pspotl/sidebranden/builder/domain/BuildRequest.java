package com.pspotl.sidebranden.builder.domain;


import java.util.Objects;

public class BuildRequest {

    private String sessionId;

    private BuildParam param;
//    private BranchDebugBuild param;

    public BuildRequest() {
    }

    public BuildRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    public BuildRequest(String sessionId,BuildParam param) {
        this.sessionId = sessionId;
        this.param = param;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

//    public BuildParam getParam() { return param;}
    public BuildParam getParam() { return param;}

//    public void setParam(BuildParam param) { this.param = param; }
    public void setParam(BuildParam param) { this.param = param; }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BuildRequest)) {
            return false;
        }

        BuildRequest that = (BuildRequest) o;
        return Objects.equals(this.sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return sessionId.hashCode();
    }

    @Override
    public String toString() {
        return "BuildRequest{" + "sessionId='" + sessionId + '\'' + '}';
    }
}