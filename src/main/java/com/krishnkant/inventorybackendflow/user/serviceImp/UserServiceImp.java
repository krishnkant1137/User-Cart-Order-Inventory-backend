package com.krishnkant.inventorybackendflow.user.serviceImp;

import com.krishnkant.inventorybackendflow.exception.EmailAlreadyExistException;
import com.krishnkant.inventorybackendflow.exception.UserNotFoundException;
import com.krishnkant.inventorybackendflow.user.dto.UserRequestDTO;
import com.krishnkant.inventorybackendflow.user.dto.UserResponseDTO;
import com.krishnkant.inventorybackendflow.user.entity.User;
import com.krishnkant.inventorybackendflow.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    public UserServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserResponseDTO create(UserRequestDTO dto) {

        log.info("Creating user with email={}", dto.email());

        if (userRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyExistException("Email already exists");
        }

        User user = User.builder()
                .name(dto.name())
                .email(dto.email())
                .active(true)
                .build();

        User saved = userRepository.save(user);

        return mapToResponse(saved);
    }

    public User getActiveUser(Long userId) {

        log.info("Fetching user with id: {}", userId);

        return userRepository.findByIdAndActiveTrue(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));
    }

    public Page<UserResponseDTO> getAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

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

