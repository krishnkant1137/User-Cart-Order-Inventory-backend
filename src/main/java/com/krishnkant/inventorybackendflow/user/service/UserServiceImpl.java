package com.krishnkant.inventorybackendflow.user.service;

import com.krishnkant.inventorybackendflow.exception.EmailAlreadyExistException;
import com.krishnkant.inventorybackendflow.exception.UserNotFoundException;
import com.krishnkant.inventorybackendflow.user.dto.UserRequestDTO;
import com.krishnkant.inventorybackendflow.user.dto.UserResponseDTO;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDTO create(UserRequestDTO dto) {

        String email = dto.email().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException("Email already exists");
        }

        User user = User.builder()
                .name(dto.name())
                .email(email)
                .active(true)
                .build();

        User saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public User getActiveUser(Long userId) {

        return userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getAll(int page, int size) {

        Pageable pageable =
                PageRequest.of(page, size,
                        Sort.by("createdAt").descending());

        return userRepository.findByActiveTrue(pageable)
                .map(this::mapToResponse);
    }

    private UserResponseDTO mapToResponse(User user) {

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getActive()
        );
    }
}
