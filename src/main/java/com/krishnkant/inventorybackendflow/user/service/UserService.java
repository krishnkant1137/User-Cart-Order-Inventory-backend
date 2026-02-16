package com.krishnkant.inventorybackendflow.user.service;

import com.krishnkant.inventorybackendflow.user.dto.UserRequestDTO;
import com.krishnkant.inventorybackendflow.user.dto.UserResponseDTO;
import com.krishnkant.inventorybackendflow.user.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {

    UserResponseDTO create(UserRequestDTO dto);

    User getActiveUser(Long userId);

    Page<UserResponseDTO> getAll(int page, int size);
}
