package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenService {

    private final Algorithm algorithm = Algorithm.HMAC256("rahasiajanganmudahditebak317919782");

    private final JWTVerifier verifier = JWT.require(algorithm).build();

    public String Create(User user){
        return JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("name", user.getName())
                .withExpiresAt(java.util.Date.from(java.time.Instant.now().plus(Duration.ofDays(30))))//expired 30 hari
                .sign(algorithm);
    }

    public User fromToken(String token){
        DecodedJWT jwt = verifier.verify(token);

        User user = new User();
        user.setUsername(jwt.getClaim("username").asString());
        user.setName(jwt.getClaim("name").asString());
        user.setTokenExpiredAt(jwt.getExpiresAt().getTime());

        return user;
    }

}
