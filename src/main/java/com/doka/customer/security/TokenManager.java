package com.doka.customer.security;

import com.doka.customer.util.DateUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Component
public class TokenManager {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getBearerToken(String authorization) {
        if(authorization != null && authorization.startsWith("Bearer ")) {
            authorization = authorization.substring(7);
        }

        return authorization;
    }

    public String generateToken(String customerId, Map<String, Object> claims) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDate = now.plusYears(100);

        return Jwts.builder()
                .setSubject(customerId)
                .setIssuedAt(DateUtils.asDate(now))
                .setExpiration(DateUtils.asDate(expireDate))
                .addClaims(claims)
                .signWith(key)
                .compact();
    }

    public boolean isExpired(String token) {
        return getClaims(token)
                .getExpiration()
                .before(new Date(System.currentTimeMillis()));
    }

    public Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public JwtUserInfo getJwtUserInfo(String authorization) {
        String token = getBearerToken(authorization);
        Claims claims = getClaims(token);

        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        if (claims.getSubject() != null) {
            Long userId = Long.valueOf(claims.getSubject());
            jwtUserInfo.setId(userId);
        }

        return jwtUserInfo;
    }

}
