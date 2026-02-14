package com.krishnkant.inventorybackendflow.user.service;

import com.krishnkant.inventorybackendflow.exception.CartNotFoundException;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getActiveUser(Long userId) {

        log.info("Fetching user with id: {}", userId);

        return userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
}

