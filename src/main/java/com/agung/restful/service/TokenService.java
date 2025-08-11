package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
public class TokenService {

    private final Algorithm algorithm = Algorithm.HMAC256("rahasiajanganmudahditebak317919782");

    private final JWTVerifier verifier = JWT.require(algorithm).build();

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String Create(User user){
        String token =  JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("name", user.getName())
                .withExpiresAt(java.util.Date.from(java.time.Instant.now().plus(Duration.ofDays(30))))//expired 30 hari
                .sign(algorithm);

        //simpan token ke redis
        redisTemplate.opsForValue().set(token, user.getUsername(),Duration.ofDays(30));
        return token;
    }

    public User fromToken(String token){

        //cek token ada atau tidak di redis
        if(!redisTemplate.hasKey(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token expired");
        }

        DecodedJWT jwt = verifier.verify(token);

        User user = new User();
        user.setToken(token);
        user.setUsername(jwt.getClaim("username").asString());
        user.setName(jwt.getClaim("name").asString());
        user.setTokenExpiredAt(jwt.getExpiresAt().getTime());

        return user;
    }

    public void deleteToken(String token){
        redisTemplate.delete(token);
    }

}
