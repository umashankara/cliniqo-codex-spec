package com.cliniqo.whatsapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cliniqo.whatsapp.repository.WhatsAppMetadataRepository;
import org.junit.jupiter.api.Test;

class WhatsAppPhoneNumberIdConflictIT {
    @Test
    void repositoryHasPhoneNumberIdLookup() throws Exception {
        assertTrue(WhatsAppMetadataRepository.class.getMethod("existsByPhoneNumberId", String.class).getReturnType().equals(boolean.class));
    }
}
