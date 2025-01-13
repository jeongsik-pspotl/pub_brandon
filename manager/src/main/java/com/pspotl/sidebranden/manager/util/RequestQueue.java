package com.pspotl.sidebranden.manager.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;

@Slf4j
@Component
public class RequestQueue {

    private LinkedList<HttpServletRequest> queue;

    public RequestQueue() {
        queue = new LinkedList<>();
    }

    public synchronized void addRequest(HttpServletRequest request) {
        queue.add(request);
        log.info("{}, {}",queue.size(), queue.element().getRequestURI());
        notify();  // 대기 중인 스레드에게 알림
    }

    public synchronized HttpServletRequest takeRequest() throws InterruptedException {
//        while (queue.isEmpty()) {
//            wait();  // 큐가 비어있으면 대기 상태로 유지
//        }
        return queue.remove();
    }

}
