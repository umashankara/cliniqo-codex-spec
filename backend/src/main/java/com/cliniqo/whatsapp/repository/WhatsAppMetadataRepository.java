package com.cliniqo.whatsapp.repository;

import com.cliniqo.whatsapp.entity.WhatsAppMetadata;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WhatsAppMetadataRepository extends JpaRepository<WhatsAppMetadata, UUID> {
    boolean existsByDisplayPhoneNumber(String displayPhoneNumber);
    boolean existsByPhoneNumberId(String phoneNumberId);
}
