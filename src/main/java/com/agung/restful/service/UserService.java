package com.agung.restful.service;

import com.agung.restful.entity.User;
import com.agung.restful.model.RegisterUserRequest;
import com.agung.restful.repository.UserRepository;
import com.agung.restful.security.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
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

}
