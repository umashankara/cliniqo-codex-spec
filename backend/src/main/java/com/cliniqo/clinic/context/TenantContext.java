package com.cliniqo.clinic.context;

import com.cliniqo.common.enums.UserRole;
import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<TenantContext> CURRENT = new ThreadLocal<>();

    private final UUID clinicId;
    private final UUID userId;
    private final UserRole role;
    private final String requestId;

    public TenantContext(UUID clinicId, UUID userId, UserRole role, String requestId) {
        this.clinicId = clinicId;
        this.userId = userId;
        this.role = role;
        this.requestId = requestId;
    }

    public static void set(TenantContext context) { CURRENT.set(context); }
    public static TenantContext get() { return CURRENT.get(); }
    public static void clear() { CURRENT.remove(); }
    public static UUID requiredClinicId() {
        TenantContext context = get();
        if (context == null || context.getClinicId() == null) {
            throw new com.cliniqo.common.exception.TenancyException("Trusted clinic context is required");
        }
        return context.getClinicId();
    }

    public UUID getClinicId() { return clinicId; }
    public UUID getUserId() { return userId; }
    public UserRole getRole() { return role; }
    public String getRequestId() { return requestId; }
}
