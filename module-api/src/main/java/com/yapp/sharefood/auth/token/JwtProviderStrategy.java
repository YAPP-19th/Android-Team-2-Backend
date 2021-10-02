package com.yapp.sharefood.auth.token;

import com.yapp.sharefood.oauth.exception.TokenExpireExcetion;
import com.yapp.sharefood.oauth.exception.TokenValidationException;
import com.yapp.sharefood.user.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.function.Function;

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

    @Override
    public Long extractIdByToken(String token) {
        String subjectStr = getClaimFromToken(token, Claims::getSubject);
        if (subjectStr == null) {
            throw new TokenValidationException();
        }

        return Long.parseLong(subjectStr);
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimGetFunction) {
        Claims allClaims = getAllClaims(token);
        return claimGetFunction.apply(allClaims);
    }

    private Claims getAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(tokenSecretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (IllegalArgumentException ignore) {
            throw new TokenValidationException();
        }
    }

    @Override
    public boolean isValidToken(String token) {
        Date expireDate = getClaimFromToken(token, Claims::getExpiration);
        if (expireDate.before(new Date())) {
            throw new TokenExpireExcetion();
        }

        return true;
    }
}
