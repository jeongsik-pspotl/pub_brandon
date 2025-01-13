package com.pspotl.sidebranden.common.user;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Data
@EqualsAndHashCode(of={"userId"})
@Entity
@Table( name="WSQ_USERS" )
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    @Id
    @Column(name="USERID", length=30)
    private String userId;

    @Column(name="NAME", length=50, nullable = false)
    private String name;

    @Column(name="DEPARTCODE", length=30, nullable = false)
    private String department;

    @Column(name="DUTY", length=30)
    private String duty;

    @Column(name="POSITION", length=30)
    @JsonProperty("pos")
    private String position;

    @Column(name="REGDATE")
    private Date regDate;

    @Column(name="APPLYDATE")
    private Date applyDate;

    @Column(name="VALID", length = 1)
    private String valid;

    @CreationTimestamp
    @Column(name="CREATEDATE", updatable = false)
    private Date createDate;

    @UpdateTimestamp
    @Column(name="UPDATEDATE")
    private Date updateDate;

}
