package com.cliniqo.auth.repository;

import com.cliniqo.auth.entity.User;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.enums.UserStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
    long countByRoleAndStatus(UserRole role, UserStatus status);
    List<User> findByClinicId(UUID clinicId);
}
