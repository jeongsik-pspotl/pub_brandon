package com.inswave.whive.headquater.security.token;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TokenService {

    private final TokenRepository tokenRepository;

    // private final UserRepository userRepository;

    @Autowired
    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
     //   this.userRepository = userRepository;
    }

    public Optional<TokenEntity> findByUserId(String userId) {
        return tokenRepository.findById(userId);
    }

    public long isExistRefreshTokenByUserId(String userId, String refreshToken) {
        return tokenRepository.getCountRefreshToken(userId, refreshToken);
    }

    public void save(TokenEntity tokenEntity) {
//        try {
//            Optional<UserEntity> optionalUserEntity = userRepository.findById(tokenEntity.getUserId());
//
//            if (optionalUserEntity.isPresent()) {
//                UserEntity userEntity = optionalUserEntity.get();
//
//                tokenEntity.setId(userEntity.getId());
//                tokenRepository.save(tokenEntity);
//            }
//        }catch (Exception e) {
//            log.info(e.getMessage(), e);
//        }

    }

}
