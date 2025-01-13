package com.pspotl.sidebranden.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class PortOneAPIUtil {

        HttpClient httpClient;

        JSONParser parser = new JSONParser();

        public JSONObject commonPortOneAPI(String uri, JSONObject reqJsonDetail, String auth ){

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Proworks-Body","Y");
                headers.set("Proworks-Lang","ko");
                headers.set("AuthInfo","WHive");
                headers.set("Authorization",auth);

                HttpEntity<String> httpEntity = new HttpEntity<>( reqJsonDetail.toJSONString(), headers);

                String url = "https://pay.inswave.com" +uri;

                String response = restTemplate.postForObject(url,httpEntity, String.class );
                log.info(response.toString());
                try {
                        return (JSONObject) parser.parse(response.toString());
                } catch (ParseException e) {
                        throw new RuntimeException(e);
                }
        }

        /**
         * wspay token 값 가져 오는 외부 api 호출
         * @param uri
         * @return
         */
        public JSONObject getPricingToken(String uri){
                JSONObject jsonObject = new JSONObject();
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.set("Proworks-Body","Y");
                headers.set("Proworks-Lang","ko");
                headers.set("AuthInfo","WHive");

                HttpEntity<String> httpEntity = new HttpEntity<>(headers);

                String url = "https://pay.inswave.com" +uri;

                HttpEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class );
                log.info(response.getBody().toString());
                log.info(response.getHeaders().toString());
                log.info(response.getHeaders().get("Authorization").toString());
                String Authorization = response.getHeaders().get("Authorization").get(0).toString();
                jsonObject.put("Authorization", Authorization); /** Authorization 값을 jsonobject 에 세팅한다. */
                log.info(Authorization);
                /** jsonObject 값 바로 리턴 한다. */
                return jsonObject;

        }


}
