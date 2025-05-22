package com.agung.restful.security;

import com.agung.restful.entity.User;
import com.agung.restful.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {
    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value(("${security.jwt.secret-key}"))
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Autowired
    private UserRepository userRepository;

//    private final long expirationDate = 1000 * 60 * 60;

    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaim, User user) {
        return buildToken(new HashMap<>(), user, jwtExpiration);
    }

    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaim, User user, Long expiration) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claims(extraClaim)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    public boolean validateAccessToken(String authToken, User user) {
        Optional<User> userToken = userRepository.findFirstByToken(authToken);
        if (userToken.isPresent()){
            return validateToken(authToken,user);
        }
        return false;
    }

    public boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public long getExpirationTime() {
        return System.currentTimeMillis() + jwtExpiration;
    }

    public long getRefreshTokenExpiration() {
        return System.currentTimeMillis() + refreshTokenExpiration;
    }
}
