package com.cliniqo.clinic.context;

import com.cliniqo.common.enums.UserRole;
import com.cliniqo.common.security.UserPrincipal;
import com.cliniqo.config.RequestIdFilter;
import jakarta.persistence.EntityManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.hibernate.Session;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TenantContextFilter extends OncePerRequestFilter {
    private final EntityManager entityManager;

    public TenantContextFilter(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Session session = entityManager.unwrap(Session.class);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            if (principal.getRole() != UserRole.SUPER_ADMIN && principal.getClinicId() != null) {
                TenantContext context = new TenantContext(
                        principal.getClinicId(),
                        principal.getUserId(),
                        principal.getRole(),
                        RequestIdFilter.currentRequestId());
                TenantContext.set(context);
                MDC.put("clinicId", principal.getClinicId().toString());
                MDC.put("userId", principal.getUserId().toString());
                session.enableFilter("tenantFilter").setParameter("clinicId", principal.getClinicId());
            } else {
                MDC.put("userId", principal.getUserId().toString());
            }
        }
        try {
            chain.doFilter(request, response);
        } finally {
            if (session.getEnabledFilter("tenantFilter") != null) {
                session.disableFilter("tenantFilter");
            }
            TenantContext.clear();
        }
    }
}
