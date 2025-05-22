package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.agung.restful.model.request.RegisterUserRequest;
import com.agung.restful.model.request.UpdateUserRequest;
import com.agung.restful.model.response.UserResponse;
import com.agung.restful.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
@Slf4j
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void register(RegisterUserRequest request){

        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())){//cek existing username
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username sudah terdaftar");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user){
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request){

       log.info("REQUEST :{}",request);

        validationService.validate(request);

        if (Objects.nonNull(request.getName())){
            user.setName(request.getName());
        }

        if (Objects.nonNull(request.getPassword())){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        userRepository.save(user);
        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }


}
