package com.krishnkant.inventorybackendflow.user.controller;

import com.krishnkant.inventorybackendflow.common.ApiResponse;
import com.krishnkant.inventorybackendflow.user.dto.UserRequestDTO;
import com.krishnkant.inventorybackendflow.user.dto.UserResponseDTO;
import com.krishnkant.inventorybackendflow.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(
            @Valid @RequestBody UserRequestDTO dto) {

        UserResponseDTO response = userService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        response,
                        "User created successfully",
                        HttpStatus.CREATED
                ));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponseDTO>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<UserResponseDTO> users =
                userService.getAll(page, size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        users,
                        "Users fetched successfully",
                        HttpStatus.OK
                )
        );
    }
}
