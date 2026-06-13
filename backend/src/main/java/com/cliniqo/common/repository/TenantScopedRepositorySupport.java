package com.cliniqo.common.repository;

import com.cliniqo.clinic.context.TenantContext;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class TenantScopedRepositorySupport {
    public UUID requiredClinicId() {
        return TenantContext.requiredClinicId();
    }
}
