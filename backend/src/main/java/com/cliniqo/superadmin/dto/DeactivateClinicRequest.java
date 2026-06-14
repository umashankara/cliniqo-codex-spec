package com.cliniqo.superadmin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeactivateClinicRequest(@NotBlank @Size(min = 3, max = 500) String reason) {
}
