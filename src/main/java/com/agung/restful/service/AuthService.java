package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.agung.restful.model.request.LoginUserRequest;
import com.agung.restful.model.response.TokenResponse;
import com.agung.restful.repository.UserRepository;
import com.agung.restful.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Transactional
    public TokenResponse login(LoginUserRequest request){
        validationService.validate(request);

        User user = userRepository.findById(request.getUserName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong"));

        if (BCrypt.checkpw(request.getPassword(), user.getPassword())){
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next30Days());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .formatStringExpireAt(formatDate(user.getTokenExpiredAt()))
                    .build();
        }else{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong");
        }
    }

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }


    private Long next30Days(){
        return System.currentTimeMillis()+(1000L * 60 * 60 * 24 * 30);
    }

    private String formatDate(Long date){
        // Konversi ke LocalDateTime dengan zona waktu UTC
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.of("UTC"));

        // Format tanggal
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
