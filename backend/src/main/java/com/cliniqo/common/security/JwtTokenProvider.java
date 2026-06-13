package com.cliniqo.common.security;

import com.cliniqo.common.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    private final SecretKey key;
    private final Duration accessTokenTtl;

    public JwtTokenProvider(
            @Value("${cliniqo.security.jwt-secret}") String secret,
            @Value("${cliniqo.security.access-token-minutes}") long accessTokenMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtl = Duration.ofMinutes(accessTokenMinutes);
    }

    public String createAccessToken(UserPrincipal principal) {
        Instant now = Instant.now();
        var builder = Jwts.builder()
                .subject(principal.getUserId().toString())
                .claim("email", principal.getUsername())
                .claim("role", principal.getRole().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenTtl)))
                .signWith(key);
        if (principal.getClinicId() != null) {
            builder.claim("clinicId", principal.getClinicId().toString());
        }
        return builder.compact();
    }

    public Claims parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public UserPrincipal toPrincipal(String token) {
        Claims claims = parse(token);
        UUID userId = UUID.fromString(claims.getSubject());
        String clinicClaim = claims.get("clinicId", String.class);
        UUID clinicId = clinicClaim == null ? null : UUID.fromString(clinicClaim);
        UserRole role = UserRole.valueOf(claims.get("role", String.class));
        String email = claims.get("email", String.class);
        return new UserPrincipal(userId, clinicId, email, "", role, true);
    }
}
