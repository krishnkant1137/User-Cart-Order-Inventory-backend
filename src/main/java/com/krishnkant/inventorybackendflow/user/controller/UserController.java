package com.krishnkant.inventorybackendflow.user.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.user.dto.UserRequestDTO;
import com.krishnkant.inventorybackendflow.user.dto.UserResponseDTO;
import com.krishnkant.inventorybackendflow.user.serviceImp.UserServiceImp;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImp userServiceImp;

    public UserController(UserServiceImp userServiceImp) {
        this.userServiceImp = userServiceImp;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(
            @Valid @RequestBody UserRequestDTO dto) {

        UserResponseDTO response = userServiceImp.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response,
                        "User created successfully",
                        201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<UserResponseDTO> users =
                userServiceImp.getAll(page, size);

        return ResponseEntity.ok(
                ApiResponse.success(users,
                        "Users fetched successfully",
                        200)
        );
    }
}
