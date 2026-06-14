package com.cliniqo.whatsapp.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "whatsapp_metadata")
public class WhatsAppMetadata extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, unique = true, columnDefinition = "uuid")
    private UUID clinicId;
    @Column(name = "waba_id", nullable = false, length = 120)
    private String wabaId;
    @Column(name = "phone_number_id", nullable = false, unique = true, length = 120)
    private String phoneNumberId;
    @Column(name = "display_phone_number", nullable = false, unique = true, length = 32)
    private String displayPhoneNumber;
    @Column(name = "template_namespace", nullable = false, length = 120)
    private String templateNamespace;
    @Column(name = "encrypted_access_token_placeholder", columnDefinition = "text")
    private String encryptedAccessTokenPlaceholder;
    @Column(name = "encrypted_app_secret_placeholder", columnDefinition = "text")
    private String encryptedAppSecretPlaceholder;
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getWabaId() { return wabaId; }
    public void setWabaId(String wabaId) { this.wabaId = wabaId; }
    public String getPhoneNumberId() { return phoneNumberId; }
    public void setPhoneNumberId(String phoneNumberId) { this.phoneNumberId = phoneNumberId; }
    public String getDisplayPhoneNumber() { return displayPhoneNumber; }
    public void setDisplayPhoneNumber(String displayPhoneNumber) { this.displayPhoneNumber = displayPhoneNumber; }
    public String getTemplateNamespace() { return templateNamespace; }
    public void setTemplateNamespace(String templateNamespace) { this.templateNamespace = templateNamespace; }
    public String getEncryptedAccessTokenPlaceholder() { return encryptedAccessTokenPlaceholder; }
    public void setEncryptedAccessTokenPlaceholder(String encryptedAccessTokenPlaceholder) { this.encryptedAccessTokenPlaceholder = encryptedAccessTokenPlaceholder; }
    public String getEncryptedAppSecretPlaceholder() { return encryptedAppSecretPlaceholder; }
    public void setEncryptedAppSecretPlaceholder(String encryptedAppSecretPlaceholder) { this.encryptedAppSecretPlaceholder = encryptedAppSecretPlaceholder; }
}
