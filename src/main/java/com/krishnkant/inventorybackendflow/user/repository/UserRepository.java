package com.krishnkant.inventorybackendflow.user.repository;

import com.krishnkant.inventorybackendflow.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByIdAndActiveTrue(Long id);
    boolean existsByEmail(String email);
    Page<User> findByActiveTrue(Pageable pageable);
}

