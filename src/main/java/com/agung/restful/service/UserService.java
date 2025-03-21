package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.agung.restful.model.RegisterUserRequest;
import com.agung.restful.model.UpdateUserRequest;
import com.agung.restful.model.UserResponse;
import com.agung.restful.repository.UserRepository;
import com.agung.restful.security.BCrypt;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Transactional
    public void register(RegisterUserRequest request){

        validationService.validate(request);

        if (userRepository.existsById(request.getUsername())){//cek existing username
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username sudah terdaftar");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(),BCrypt.gensalt()));
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
            user.setPassword(BCrypt.hashpw(request.getPassword(),BCrypt.gensalt()));
        }

        userRepository.save(user);
        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }


}
