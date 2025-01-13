package com.pspotl.sidebranden.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum WebsocketState {

    CL_WEBSOCKET_STATE_INACTIVE(0),
    CL_WEBSOCKET_STATE_ACTIVE(1),
    CL_WEBSOCKET_STATE_MAX(2);

    private final int dbValue;

    private WebsocketState(int dbValue) {
        this.dbValue = dbValue;
    }

    @JsonValue
    public Integer toDbValue() {
        return dbValue;
    }

    public static final Map<Integer, WebsocketState> dbValues = new HashMap<>();
    static {
        for(WebsocketState state : values()) {
            dbValues.put(state.dbValue, state);
        }
    }
    public static WebsocketState fromDbValue(Integer dbValue) {
        return dbValues.get(dbValue);
    }

}
