package com.example.User.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.User.model.User;
import com.example.User.model.dto.UserRequest;
import com.example.User.model.dto.UserResponse;
import com.example.User.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public UserResponse register(@RequestBody UserRequest userRequest) {

        User user = userService.register(userRequest);
        UserResponse res = UserResponse.builder()
        .username(user.getUsername())
        .id(user.getId())
        .build();

        System.out.println("Resposne :  " + res);
        return res;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody UserRequest userRequest) {

        User user = userService.login(userRequest);
        UserResponse res = UserResponse.builder()
        .username(user.getUsername())
        .id(user.getId())
        .build();

        System.out.println("Resposne :  " + res);
        return res;
    }
    

}
