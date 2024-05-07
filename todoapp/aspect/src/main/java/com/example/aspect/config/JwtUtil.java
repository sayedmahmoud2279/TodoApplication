package com.example.aspect.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private String expiration;

    private Key key;

    public JwtUtil() {
        this.secret = "MiAVzqUXy5Tfr1kVIGpPMiAVzqUXy5Tfr1kVIGpP";
        this.expiration = "86400";
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

//    @PostConstruct
//    public void initKey() {
//        key = Keys.hmacShaKeyFor(secret.getBytes());
//        System.out.println(key);
//    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    public String generate(UserVO user, String tokenType) {
        Map<String, Object> claims = Map.of("user", user);
        String userId = user.getId().toString();
        long expMillis = "ACCESS".equalsIgnoreCase(tokenType)
                ? Long.parseLong(expiration) * 1000
                : Long.parseLong(expiration) * 1000 * 5;

        final Date now = new Date();
        final Date exp = new Date(now.getTime() + expMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key)
                .compact();
    }

    public boolean isExpired(String token) {
        return getExpirationDate(token).before(new Date());
    }
}
