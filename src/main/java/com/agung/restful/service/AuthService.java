package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.agung.restful.exception.InvalidTokenException;
import com.agung.restful.model.request.LoginUserRequest;
import com.agung.restful.model.response.TokenResponse;
import com.agung.restful.repository.UserRepository;
import com.agung.restful.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional
    public TokenResponse login(LoginUserRequest request) {
        validationService.validate(request);

        User user = userRepository.findById(request.getUserName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong"));

        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            String refreshToken = jwtUtil.generateRefreshToken(user);
            user.setToken(jwtUtil.generateToken(user));
            user.setRefreshToken(refreshToken);
            user.setTokenExpiredAt(jwtUtil.getExpirationTime());
            user.setRefreshTokenExpiredAt(jwtUtil.getRefreshTokenExpiration());
            userRepository.save(user);

            return TokenResponse.builder()
                    .token(user.getToken())
                    .refreshToken(user.getRefreshToken())
                    .expiredAt(user.getTokenExpiredAt())
                    .refreshTokenExpired(user.getRefreshTokenExpiredAt())
                    .formatStringExpireAt(formatDate(user.getTokenExpiredAt()))
                    .formatStringRefreshTokenExpireAt(formatDate(user.getRefreshTokenExpiredAt()))
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "username or password wrong");
        }
    }

    private void revokeAllToken(User user) {
        User userWithToken = userRepository.findFirstByToken(user.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        userWithToken.setToken(null);
        userWithToken.setTokenExpiredAt(null);
        userRepository.save(userWithToken);
    }

    @Transactional
    public TokenResponse refreshAccessToken(HttpServletRequest request) {
        final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorization == null) {
            throw new InvalidTokenException("Token is missing");
        } else if (!authorization.startsWith("Bearer ")) {
            throw new InvalidTokenException("Token is invalid");
        }

        final String refreshToken = authorization.substring(7);
        final String username = jwtUtil.extractUsername(refreshToken);

        if (username != null) {
            User user = userRepository.findFirstByRefreshToken(refreshToken)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

            if (jwtUtil.validateToken(refreshToken, user)) {
                String accessToken = jwtUtil.generateToken(user);
                revokeAllToken(user);
                user.setToken(accessToken);

                userRepository.save(user);

                return TokenResponse.builder()
                        .token(accessToken)
                        .refreshToken(refreshToken)
                        .expiredAt(jwtUtil.getExpirationTime())
                        .formatStringExpireAt(formatDate(jwtUtil.getExpirationTime()))
                        .formatStringRefreshTokenExpireAt(formatDate(user.getRefreshTokenExpiredAt()))
                        .build();
            }
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token not Valid");
        }
        return null;
    }

    @Transactional
    public void logout(@AuthenticationPrincipal User user) {
        User userWithToken = userRepository.findFirstByToken(user.getToken())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));
        userWithToken.setToken(null);
        userWithToken.setRefreshToken(null);
        userWithToken.setTokenExpiredAt(null);
        userWithToken.setRefreshTokenExpiredAt(null);
        userRepository.save(userWithToken);
    }


    private Long next30Days() {
        return System.currentTimeMillis() + (1000 * 16 * 24 * 30);
    }

    private String formatDate(Long date) {
        LocalDateTime dateTime = Instant.ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(formatter);
    }
}
