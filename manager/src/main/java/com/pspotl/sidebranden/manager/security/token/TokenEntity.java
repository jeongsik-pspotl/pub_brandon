package com.pspotl.sidebranden.manager.security.token;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "USER_TOKEN")
public class TokenEntity {
    @Column(name = "idx", nullable = false)
    Long id;

    @Id
    @Column(name = "user_id")
    String userId;

    @Column(name = "token")
    String token;

    @Column(length = 100)
    String issuedDate;

    @Column(length = 100)
    String expireDate;

    @Override
    public String toString() {
        return String.format("id=%s, token=%s, issuedDate=%s, expireDate=%s", id, token, issuedDate, expireDate);
    }

}
