package com.inswave.whive.headquater.listener;

import com.inswave.whive.common.builderqueue.BuilderQueueManaged;
import com.inswave.whive.common.builderqueue.BuilderQueueManagedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WHiveListener implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    BuilderQueueManagedService builderQueueManagedService;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {


        /**
         * Builder Queue Managed 컬럼 기능이다.
         *
         */
        List<BuilderQueueManaged> builderQueueManagedList =  builderQueueManagedService.findByAll();

        for(int i = 0; i <builderQueueManagedList.size();i++){

            BuilderQueueManaged setBuilderQueueManaged = builderQueueManagedList.get(i);

            setBuilderQueueManaged.setBuild_queue_status_cnt(0L);
            setBuilderQueueManaged.setDeploy_queue_status_cnt(0L);
            setBuilderQueueManaged.setProject_queue_status_cnt(0L);
            setBuilderQueueManaged.setEtc_queue_status_cnt(0L);

            builderQueueManagedService.update(setBuilderQueueManaged);

        }

    }
}
