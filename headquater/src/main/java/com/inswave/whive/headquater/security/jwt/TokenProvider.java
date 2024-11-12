package com.inswave.whive.headquater.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


import com.inswave.whive.common.member.MemberLogin;
import com.inswave.whive.common.member.MemberService;
import com.inswave.whive.headquater.security.DomainUserDetails;
import com.inswave.whive.headquater.security.token.TokenEntity;
import com.inswave.whive.headquater.security.token.TokenService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class TokenProvider {
    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String                AUTHORITIES_KEY = "auth";
    private static final String                REAL_NAME_KEY   = "userId";

    private              String                secretBase64;

    final String ISSUER = "WHIVE";

    long accessTokenValidityInSeconds = 1800;

    long refreshTokenValidityInSeconds = 604800;

    @Value("${whive.jwt.secret}")
    private String secretKey;

    TokenService tokenService;

    @Autowired
    MemberService memberService;

    @PostConstruct
    public void init() {
        secretBase64 = getTokenSecret();

        accessTokenValidityInSeconds *= 100;    // 30 min.
        refreshTokenValidityInSeconds *= 1000;   // 7 days.

    }


    /**
     * Access Token.
     * @param userId 사용자 관련 필요 데이터.
     * @param authorities
     */
    public TokenData createToken(String userId, String authorities) {

        log.debug("createToken secretKey={}", secretKey);
        log.debug("createToken authorities={}", authorities);

        try {
        Date now = new Date();
        String uniqueId = UUID.randomUUID().toString();

        //      Expire Date
        Date accessTokenExpiration = new Date(now.getTime() + Duration.ofSeconds(accessTokenValidityInSeconds).toMillis());
        Date refreshTokenExpiration = new Date(now.getTime() + Duration.ofSeconds(refreshTokenValidityInSeconds).toMillis());

        String accessToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiration)
                .setId(uniqueId)
                .signWith(SignatureAlgorithm.HS256, getEncodedSecretKey())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("access", true)
                .claim("userId", userId)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(ISSUER)
                .setIssuedAt(now)
                .setExpiration(refreshTokenExpiration)
                .setId(uniqueId)
                .signWith(SignatureAlgorithm.HS256, getEncodedSecretKey())
                .claim(AUTHORITIES_KEY, authorities)
                .claim("userId", userId)
                .compact();

//        // Insert DB (RefreshToken)
        Claims refreshTokenClaims = parseJwtToken(refreshToken);

        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setUserId(userId);
        tokenEntity.setToken(refreshToken);
        tokenEntity.setIssuedDate(refreshTokenClaims.getIssuedAt().toString());
        tokenEntity.setExpireDate(refreshTokenClaims.getExpiration().toString());

            return new TokenData(accessToken, refreshToken);

        }catch (Exception e) {
            log.info(e.getMessage(), e);
        }


        return null;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretBase64)
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        String realName = (String) claims.get(REAL_NAME_KEY);
        // log.info(realName);
        // log.info(claims.getSubject());
        // log.info(authorities.toString());

        MemberLogin memberLogin = memberService.findByUserLoginID(realName);

        DomainUserDetails principal = new DomainUserDetails(realName, realName, memberLogin.getPassword(), authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean getExpired(String token){

        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey.getBytes()).parseClaimsJws(token);
        boolean isNotExpire = claims.getBody().getExpiration().after(new Date());


        return isNotExpire;

    }

    public String getSubject(String token) {
        return Jwts.parser().setSigningKey(secretBase64).parseClaimsJws(token).getBody().getSubject();
    }

    public String getRealName(String token){

        Claims claims = Jwts.parser()
                .setSigningKey(secretBase64)
                .parseClaimsJws(token)
                .getBody();

        String realName = (String) claims.get(REAL_NAME_KEY);

        return realName;
    }

    public TokenValidationData validationToken(String token) {
        Exception tokenException = null;
        boolean isValid = false;

        try {
            parseJwtToken(token);
            isValid = true;
        } catch (Exception e) {
            tokenException = tokenException(e);
        }

        return new TokenValidationData(isValid, tokenException);
    }

    public TokenValidationData validationRefreshToken(String refreshToken) {
        Exception tokenException = null;
        boolean isValid = false;

        try {
            Claims claims = parseJwtToken(refreshToken);
            String userId = (String) claims.get("userId");
            long count = tokenService.isExistRefreshTokenByUserId(userId, refreshToken);

            if (count < 1) {
                tokenException = new MalformedJwtException("not presented refreshToken.");
            }
            else {
                isValid = true;
            }

        } catch (Exception e) {
            tokenException = tokenException(e);
        }

        return new TokenValidationData(isValid, tokenException);
    }

    public boolean validateToken(String authToken) {
        try {
            if (authToken == null || authToken.equals(""))
                return false;
            Jwts.parser().setSigningKey(secretBase64).parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e1) {
            log.debug("ExpiredJwtException : {}" + " authToken-->>" + authToken, e1);
        } catch (JwtException e2) {
            log.debug("JwtException : {}" + " authToken-->>" + authToken, e2);
        } catch (IllegalArgumentException e3) {
            log.debug("IllegalArgumentException : {}" + " authToken-->>" + authToken, e3);
        }
        return false;
    }

    private Exception tokenException(Exception e) {
        Exception exception;

        try {
            throw e;
        } catch (SignatureException se) {
            exception = new SignatureException("Invalid JWT signature");
        } catch (MalformedJwtException me) {
            exception = new MalformedJwtException("Invalid JWT token");
        } catch (ExpiredJwtException eje) {
            exception = new Exception("Expired JWT token");
        } catch (UnsupportedJwtException uje) {
            exception = new UnsupportedJwtException("Unsupported JWT token");
        } catch (IllegalArgumentException iae) {
            exception = new IllegalArgumentException("JWT claims string is empty.");
        } catch (Exception ex) {
            exception = null;
        }

        return exception;
    }

    public Long getTokenValidityInMilliseconds() {
        return Long.valueOf(0);
    }

    public String resolveToken(HttpServletRequest req) {
        Enumeration<String> headers = req.getHeaders("Cookie");
        while (headers.hasMoreElements()) {
            String value = headers.nextElement();
            Cookie[] cookies = req.getCookies();
            String cookieValue = null;
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("accessToken")) {
                        cookieValue = cookie.getValue();
                        return cookieValue;
                    }
                }
            }
        }

        return Strings.EMPTY;
    }
    public String getTokenSecret() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String removeBearer(String token) {
        return token.contains("Bearer ") ? token.substring("Bearer ".length()) : token;
    }

    public Claims parseJwtToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .parseClaimsJws(removeBearer(token))
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private String getEncodedSecretKey() {
        return Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

}
