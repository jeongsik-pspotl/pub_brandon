package com.inswave.whive.common.builderqueue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BuilderQueueManaged {

    private Long builder_id;
    private Long queue_managed_id;
    private Long project_queue_cnt;
    private Long build_queue_cnt;
    private Long deploy_queue_cnt;
    private Long etc_queue_cnt;
    private Long project_queue_status_cnt;
    private Long build_queue_status_cnt;
    private Long deploy_queue_status_cnt;
    private Long etc_queue_status_cnt;

    private String queue_etc_1;
    private String queue_etc_2;
    private String queue_etc_3;

    private LocalDateTime create_date;

    private LocalDateTime update_date;

}
