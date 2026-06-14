package com.cliniqo.auth.service;

import com.cliniqo.auth.entity.DefaultPermissionAssignment;
import com.cliniqo.auth.entity.User;
import com.cliniqo.auth.repository.DefaultPermissionAssignmentRepository;
import com.cliniqo.auth.repository.UserRepository;
import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.enums.UserStatus;
import com.cliniqo.common.exception.ConflictException;
import com.cliniqo.superadmin.dto.ClinicOnboardingRequest;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ClinicAdminProvisioningService {
    private final UserRepository userRepository;
    private final DefaultPermissionAssignmentRepository permissionRepository;

    public ClinicAdminProvisioningService(UserRepository userRepository, DefaultPermissionAssignmentRepository permissionRepository) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
    }

    public User createFirstAdmin(UUID clinicId, ClinicOnboardingRequest.FirstClinicAdminInput input, String passwordHash) {
        if (userRepository.existsByEmailIgnoreCase(input.email())) {
            throw new ConflictException("firstAdmin.email already exists");
        }
        User user = new User();
        user.setClinicId(clinicId);
        user.setEmail(input.email());
        user.setFullName(input.fullName());
        user.setPhone(input.phone());
        user.setPasswordHash(passwordHash);
        user.setRole(UserRole.CLINIC_ADMIN);
        user.setStatus(UserStatus.ACTIVE);
        user.setForcePasswordReset(true);
        user.setTemporaryCredentialIssuedAt(Instant.now());
        return userRepository.save(user);
    }

    public void assignDefaultPermissions(UUID clinicId, UUID userId) {
        for (String module : List.of("clinic-settings", "staff", "appointments", "whatsapp", "faq")) {
            DefaultPermissionAssignment assignment = new DefaultPermissionAssignment();
            assignment.setClinicId(clinicId);
            assignment.setUserId(userId);
            assignment.setModule(module);
            assignment.setLevel("admin");
            permissionRepository.save(assignment);
        }
    }
}
