package com.RestAPI.test.service;

import com.RestAPI.test.dto.UserDto;
import com.RestAPI.test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public int insert(UserDto user) {
        if(findUser(user.getUsername()) != null)
            return -1;

        return userRepository.insert(user);
    }

    public UserDto findUser(String username) {
        return userRepository.findUser(username);
    }

}
