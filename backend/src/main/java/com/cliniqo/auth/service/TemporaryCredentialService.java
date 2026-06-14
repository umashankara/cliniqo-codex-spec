package com.cliniqo.auth.service;

import java.security.SecureRandom;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class TemporaryCredentialService {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789!@#$%";
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordEncoder passwordEncoder;

    public TemporaryCredentialService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public IssuedTemporaryCredential issue() {
        StringBuilder raw = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            raw.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }
        String password = raw.toString();
        return new IssuedTemporaryCredential(password, passwordEncoder.encode(password));
    }

    public record IssuedTemporaryCredential(String temporaryPassword, String passwordHash) {}
}
