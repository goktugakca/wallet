package com.goktug.wallet.dto;


import com.goktug.wallet.domain.Role;
import com.goktug.wallet.domain.User;

import java.util.UUID;

public record UserResponse(UUID id, String username, Role role) {
    public static UserResponse from(User user){
        return new UserResponse(user.getId(),user.getUsername(),user.getRole());
    }
}
