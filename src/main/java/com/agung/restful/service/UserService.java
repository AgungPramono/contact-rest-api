package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.agung.restful.model.RegisterUserRequest;
import com.agung.restful.repository.UserRepository;
import com.agung.restful.security.BCrypt;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Validator validator;

    @Transactional
    public void register(RegisterUserRequest request){
        Set<ConstraintViolation<RegisterUserRequest>>constraintViolations = validator.validate(request);
        if (constraintViolations.size()!=0){
            throw new ConstraintViolationException(constraintViolations);
        }

        if (userRepository.existsById(request.getUsername())){//cek existing username
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"username sudah terdaftar");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(),BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

}
