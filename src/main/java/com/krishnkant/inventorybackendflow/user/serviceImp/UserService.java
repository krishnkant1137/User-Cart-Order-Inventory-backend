package com.krishnkant.inventorybackendflow.user.serviceImp;

import com.krishnkant.inventorybackendflow.user.dto.UserRequestDTO;
import com.krishnkant.inventorybackendflow.user.dto.UserResponseDTO;
import com.krishnkant.inventorybackendflow.user.entity.User;
import org.springframework.data.domain.Page;

public interface UserService {
    public UserResponseDTO create(UserRequestDTO dto);
    public User getActiveUser(Long userId);
    public Page<UserResponseDTO> getAll(int page, int size);
}
