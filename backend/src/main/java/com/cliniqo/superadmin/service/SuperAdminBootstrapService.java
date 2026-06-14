package com.cliniqo.superadmin.service;

import com.cliniqo.auth.repository.UserRepository;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.enums.UserStatus;
import com.cliniqo.superadmin.dto.BootstrapStatusResponse;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminBootstrapService {
    private final UserRepository userRepository;

    public SuperAdminBootstrapService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public BootstrapStatusResponse status() {
        long count = userRepository.countByRoleAndStatus(UserRole.SUPER_ADMIN, UserStatus.ACTIVE);
        return new BootstrapStatusResponse(count > 0, count);
    }
}
