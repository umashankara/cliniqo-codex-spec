package com.cliniqo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CredentialEncryptionProperties {
    private final String key;

    public CredentialEncryptionProperties(@Value("${cliniqo.encryption.key}") String key) {
        if (key == null || key.length() < 16) {
            throw new IllegalStateException("Credential encryption key must be configured");
        }
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
