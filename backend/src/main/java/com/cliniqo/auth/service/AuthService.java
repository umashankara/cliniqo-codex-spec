package com.cliniqo.auth.service;

import com.cliniqo.auth.dto.AuthResponse;
import com.cliniqo.auth.dto.AuthUserDto;
import com.cliniqo.auth.entity.User;
import com.cliniqo.auth.repository.UserRepository;
import com.cliniqo.common.enums.UserStatus;
import com.cliniqo.common.exception.UnauthorizedException;
import com.cliniqo.common.security.JwtTokenProvider;
import com.cliniqo.common.security.UserPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private static final long ACCESS_TOKEN_SECONDS = 900L;

    private final CredentialService credentialService;
    private final RefreshSessionService refreshSessionService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthAuditPublisher auditPublisher;

    public AuthService(
            CredentialService credentialService,
            RefreshSessionService refreshSessionService,
            UserRepository userRepository,
            JwtTokenProvider jwtTokenProvider,
            AuthAuditPublisher auditPublisher) {
        this.credentialService = credentialService;
        this.refreshSessionService = refreshSessionService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.auditPublisher = auditPublisher;
    }

    @Transactional
    public AuthResponse login(String email, String password) {
        User user = credentialService.authenticate(email, password);
        RefreshSessionService.IssuedRefreshSession refresh = refreshSessionService.issue(user);
        auditPublisher.loginSuccess(user);
        return response(user, refresh.rawToken());
    }

    @Transactional
    public AuthResponse refresh(String refreshToken) {
        RefreshSessionService.RotatedRefreshSession rotated = refreshSessionService.rotate(refreshToken);
        User user = userRepository.findById(rotated.previous().getUserId())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh session"));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("Invalid refresh session");
        }
        auditPublisher.refreshRotated(user);
        return response(user, rotated.next().rawToken());
    }

    @Transactional
    public void logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshSessionService.revoke(refreshToken, "logout");
        }
    }

    private AuthResponse response(User user, String refreshToken) {
        UserPrincipal principal = new UserPrincipal(
                user.getId(),
                user.getClinicId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getRole(),
                user.getStatus() == UserStatus.ACTIVE);
        String accessToken = jwtTokenProvider.createAccessToken(principal);
        return new AuthResponse(accessToken, refreshToken, ACCESS_TOKEN_SECONDS,
                new AuthUserDto(user.getId(), user.getEmail(), user.getRole(), user.getClinicId()));
    }
}
