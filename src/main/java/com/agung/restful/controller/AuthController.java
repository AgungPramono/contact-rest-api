package com.agung.restful.controller;

import com.agung.restful.entity.User;
import com.agung.restful.model.request.LoginUserRequest;
import com.agung.restful.model.response.TokenResponse;
import com.agung.restful.model.response.WebResponse;
import com.agung.restful.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(
            path = "/api/auth/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request){
        TokenResponse tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).status(true).build();
    }

    @DeleteMapping(
            path = "/api/auth/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> logout( @AuthenticationPrincipal User user){
        authService.logout(user);
        SecurityContextHolder.clearContext();
        return WebResponse.<String>builder().data("Ok").status(true).build();
    }

    @PostMapping("/api/auth/refresh-token")
    public WebResponse<TokenResponse> refreshToken(HttpServletRequest request){
        TokenResponse tokenResponse = authService.refreshAccessToken(request);
        return WebResponse.<TokenResponse>builder()
                .data(tokenResponse)
                .build();
    }
}
