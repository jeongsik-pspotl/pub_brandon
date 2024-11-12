package com.inswave.whive.headquater.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class APIHandler {

    private RequestQueue requestQueue;

    public APIHandler() {
        requestQueue = new RequestQueue();
    }

    public void handleRequest(HttpServletRequest request) {
        requestQueue.addRequest(request);
    }

    // 별도의 스레드에서 호출하여 큐를 처리하는 메서드
    public void processQueue() {
//        while (true) {
         try {
                HttpServletRequest request = requestQueue.takeRequest();
                // 요청 처리 로직 작성
                // ...
                // 처리된 요청에 대한 응답 생성 및 반환
                // ...
         } catch (InterruptedException e) {
                e.printStackTrace();
        }
//        }
    }

}
