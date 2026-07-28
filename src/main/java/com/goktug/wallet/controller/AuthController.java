package com.goktug.wallet.controller;

import com.goktug.wallet.dto.RegisterRequest;
import com.goktug.wallet.dto.UserResponse;
import com.goktug.wallet.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public UserResponse register(@Valid @RequestBody RegisterRequest request){

        return UserResponse.from(userService.register(request.username(),request.password()));
    }
}
