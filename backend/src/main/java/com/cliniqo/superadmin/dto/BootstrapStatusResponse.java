package com.cliniqo.superadmin.dto;

public record BootstrapStatusResponse(boolean activeSuperAdminExists, long activeSuperAdminCount) {
}
