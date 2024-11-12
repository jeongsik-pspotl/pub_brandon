package com.inswave.whive.common.enums;

public enum UpdateStatus {
    UPDATE_STATUS_NONE("NONE"),
    UPDATE_STATUS_OLD("OLD"),
    UPDATE_STATUS_LATEST("LATEST"),
    UPDATE_STATUS_RESERVE("RESERVE"),
    UPDATE_STATUS_OVER("OVER");

    private final String updateStatus;

    UpdateStatus(String updateStatus) {
        this.updateStatus = updateStatus;
    }

    public String getUpdateStatus() {
        return updateStatus;
    }
}
