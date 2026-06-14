package com.cliniqo.whatsapp;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cliniqo.whatsapp.repository.WhatsAppMetadataRepository;
import org.junit.jupiter.api.Test;

class WhatsAppDisplayPhoneConflictIT {
    @Test
    void repositoryHasDisplayPhoneLookup() throws Exception {
        assertTrue(WhatsAppMetadataRepository.class.getMethod("existsByDisplayPhoneNumber", String.class).getReturnType().equals(boolean.class));
    }
}
