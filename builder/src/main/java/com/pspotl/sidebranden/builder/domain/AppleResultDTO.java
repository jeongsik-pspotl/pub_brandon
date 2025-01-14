package com.pspotl.sidebranden.builder.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AppleResultDTO implements Serializable {

    private String type;

    private String id;

    private Map<String,Object> attributes;

    private Map<String,Object> relationships;

    private Map<String,Object> errors;

}
