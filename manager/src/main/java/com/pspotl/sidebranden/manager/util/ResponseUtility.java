package com.pspotl.sidebranden.manager.util;

import org.json.simple.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ResponseUtility {

    private static final String RESULT = "result";
    private static final String SUCCESS = "success";

    public ResponseEntity<Object> makeSuccessResponse(){
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put(RESULT, SUCCESS);
        entities.add(entity);
        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }

    public ResponseEntity<Object> makeSuccessResponse(JSONObject entity){
        List<JSONObject> entities = new ArrayList<JSONObject>();
        entity.put(RESULT, SUCCESS);
        entities.add(entity);
        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }

    public ResponseEntity<Object> makeSuccessHeaderResponse(HttpHeaders headers, JSONObject entity){
        List<JSONObject> entities = new ArrayList<JSONObject>();
        entity.put(RESULT, SUCCESS);
        entities.add(entity);
        return new ResponseEntity<Object>(entities, headers, HttpStatus.OK);
    }

    public ResponseEntity<Object> checkSuccessResponse(){
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put(RESULT, SUCCESS);
        entities.add(entity);

        return new ResponseEntity<Object>(entities, HttpStatus.OK);
    }

    public ResponseEntity<Object> checkFailedResponse(){
        List<JSONObject> entities = new ArrayList<JSONObject>();
        JSONObject entity = new JSONObject();
        entity.put(RESULT, "server_error");
        entities.add(entity);

        return new ResponseEntity<Object>(entities, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<Object> checkFailedResponse(JSONObject entity){
        List<JSONObject> entities = new ArrayList<JSONObject>();
        entity.put(RESULT, "server_error");
        entities.add(entity);

        return new ResponseEntity<Object>(entities, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
