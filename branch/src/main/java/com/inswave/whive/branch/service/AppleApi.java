package com.inswave.whive.branch.service;

import org.springframework.http.HttpHeaders;

public interface AppleApi {

    default HttpHeaders getToken(String token){

        if(token == null || token.equals("")){
            return null;
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        return headers;
    }


}
