package com.pspotl.sidebranden.manager.security.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TokenRepository extends JpaRepository<TokenEntity, String> {

    @Query("select count(t) from TokenEntity t where t.userId = :userId and t.token = :refreshToken")
    Long getCountRefreshToken(String userId, String refreshToken);
}
