package com.inswave.whive.branch.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class AppleApiResult<T>  implements Serializable {
    private T data;

    private Map<String,Object> links;

    private Map<String,Object> meta;

}
