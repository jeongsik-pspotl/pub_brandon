package com.inswave.whive.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Domain {

    private Long domain_id;
    private String domain_name;
    private LocalDateTime create_date;
    private LocalDateTime updated_date;


}
