package com.inswave.whive.headquater.config;

import com.inswave.whive.headquater.handler.WHiveWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.HandshakeFailureException;
import org.springframework.web.socket.server.RequestUpgradeStrategy;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;


import java.security.Principal;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocket
@ComponentScan(value = {"com.inswave.whive.headquater"})
public class WebSocketConfig implements WebSocketConfigurer {

    @Value("${maxBinaryBufferSize}")
    private Integer MAX_BINARY_MESSAGE_BUFFER_SIZE;

    @Value("${defaultBinaryBufferSize}")
    private Integer MAX_TEXT_BUFFER_SIZE;

    private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getWHiveWebSocketHandler(),"/whivebranch").setAllowedOrigins("*").setHandshakeHandler(new DefaultHandshakeHandler(new RequestUpgradeStrategy() {
            private final RequestUpgradeStrategy strategy = new TomcatRequestUpgradeStrategy();

            @Override
            public String[] getSupportedVersions() {
                return strategy.getSupportedVersions();
            }

            @Override
            public List<WebSocketExtension> getSupportedExtensions(ServerHttpRequest request) {
                logger.info("getSupportedExtensions request : {}",request.getRemoteAddress().getAddress().getHostAddress());

                return strategy.getSupportedExtensions(request);
            }

            @Override
            public void upgrade(ServerHttpRequest request,
                                ServerHttpResponse response,
                                String selectedProtocol,
                                List<WebSocketExtension> selectedExtensions,
                                Principal user,
                                WebSocketHandler wsHandler,
                                Map<String, Object> attributes) throws HandshakeFailureException {
                strategy.upgrade(request, response, selectedProtocol, selectedExtensions, user, wsHandler, attributes);
            }
        }));

    }

    @Bean
    public WHiveWebSocketHandler getWHiveWebSocketHandler() { return new WHiveWebSocketHandler(); }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();

        container.setMaxTextMessageBufferSize(MAX_TEXT_BUFFER_SIZE);
        container.setMaxBinaryMessageBufferSize(MAX_BINARY_MESSAGE_BUFFER_SIZE);
        container.setMaxSessionIdleTimeout((long) 300000);
        container.setAsyncSendTimeout((long) 300000);
        return container;
    }


}