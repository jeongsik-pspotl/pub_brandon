package com.inswave.whive.branch.hazelcast;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.inswave.whive.branch.hazelcast.producer.ClusterBuilderMessageProducer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

@Slf4j
//@Configuration
public class HazelcastClientBuilder {

    @Getter
    private static String            localUuid;
    @Getter
    private static HazelcastInstance hazelcastInstance;

    @Value("classpath:hazelcast-client.yml")
    private String hazelcastClientYml;

    private ClusterBuilderMessageProducer clusterBuilderMessageProducer;


    public HazelcastClientBuilder(ClusterBuilderMessageProducer clusterBuilderMessageProducer) {
        this.clusterBuilderMessageProducer = clusterBuilderMessageProducer;
    }


    @PostConstruct
    public void postConstruct() {

        ClientConfig clientConfig = null;

        log.info(hazelcastClientYml);
        URL url = this.getClass().getClassLoader().getResource("hazelcast-client.yml");
        log.info("url check {}", url.getPath());

        String yamlFileName = url.getPath();

        // Host IP is set in Docker scripts only
//        String hostIp = System.getProperty("HOST_IP", "");

//        if (hostIp.length() > 0) {
//            clientNetworkConfig.setAddresses(Arrays.asList(new String[] {hostIp + ":5701", hostIp + ":5702",
//                    hostIp + ":5703", }));
//
//            log.info("Non-Kubernetes configuration: member-list: {}", clientNetworkConfig.getAddresses());
//        } else {
//
//        }

        hazelcastInstance = HazelcastClient.newHazelcastClient();
        log.info(hazelcastInstance.getLocalEndpoint().toString());
        localUuid = hazelcastInstance.getLocalEndpoint().getUuid().toString();

    }

}
