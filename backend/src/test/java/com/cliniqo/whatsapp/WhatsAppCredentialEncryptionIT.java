package com.cliniqo.whatsapp;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.cliniqo.common.crypto.CredentialEncryptor;
import org.junit.jupiter.api.Test;

class WhatsAppCredentialEncryptionIT {
    @Test
    void encryptsCredentialPlaceholder() {
        CredentialEncryptor encryptor = new CredentialEncryptor("local-development-32-byte-key!!");
        assertNotEquals("secret", encryptor.encrypt("secret"));
    }
}
