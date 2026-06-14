package com.cliniqo.superadmin.service;

import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.exception.ForbiddenException;
import com.cliniqo.common.security.CurrentUserService;
import com.cliniqo.common.security.UserPrincipal;
import org.springframework.stereotype.Service;

@Service
public class SuperAdminAuthorizationService {
    private final CurrentUserService currentUserService;

    public SuperAdminAuthorizationService(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    public UserPrincipal requireSuperAdmin() {
        UserPrincipal principal = currentUserService.requirePrincipal();
        if (principal.getRole() != UserRole.SUPER_ADMIN || principal.getClinicId() != null) {
            throw new ForbiddenException("SUPER_ADMIN authority is required");
        }
        return principal;
    }
}
