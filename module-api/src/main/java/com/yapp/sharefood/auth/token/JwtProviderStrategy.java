package com.yapp.sharefood.auth.token;

import com.yapp.sharefood.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JwtProviderStrategy implements TokenProvider {
    @Value("${jwt.token.secret.key}")
    private String tokenSecretKey;

    @Value("${jwt.token.expiration.seconds}")
    private long expireTime;

    @Override
    public String createToken(User user) {
        Claims claims = Jwts.claims()
                .setSubject(String.valueOf(user.getId()));
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + expireTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS256, tokenSecretKey)
                .compact();
    }
}
