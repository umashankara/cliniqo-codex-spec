package com.cliniqo.auth.dto;

import com.cliniqo.common.enums.UserRole;
import java.util.UUID;

public class AuthUserDto {
    private UUID id;
    private String email;
    private UserRole role;
    private UUID clinicId;

    public AuthUserDto(UUID id, String email, UserRole role, UUID clinicId) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.clinicId = clinicId;
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }
    public UUID getClinicId() { return clinicId; }
}
