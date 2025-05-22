package com.agung.restful.controller;

import com.agung.restful.entity.User;
import com.agung.restful.model.request.RegisterUserRequest;
import com.agung.restful.model.request.UpdateUserRequest;
import com.agung.restful.model.response.UserResponse;
import com.agung.restful.model.response.WebResponse;
import com.agung.restful.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(path = "/api/users",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> register(@RequestBody RegisterUserRequest request){
        userService.register(request);
        return WebResponse.<String>builder().data("OK").build();
    }

    @GetMapping(
            path = "/api/user/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> get(@AuthenticationPrincipal User user){
        UserResponse userResponse = userService.get(user);
        return WebResponse.<UserResponse>builder().data(userResponse).status(true).build();
    }

    @PatchMapping(
            path = "/api/user/current",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> update(@AuthenticationPrincipal User user, @RequestBody UpdateUserRequest request){
        UserResponse userResponse = userService.update(user, request);
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }
}

