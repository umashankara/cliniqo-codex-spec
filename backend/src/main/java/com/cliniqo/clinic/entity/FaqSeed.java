package com.cliniqo.clinic.entity;

import com.cliniqo.common.entity.AuditableRecord;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "faq_seeds")
public class FaqSeed extends AuditableRecord {
    @Column(name = "clinic_id", nullable = false, columnDefinition = "uuid")
    private UUID clinicId;
    @Column(nullable = false, length = 80)
    private String category;
    @Column(nullable = false, length = 500)
    private String question;
    @Column(nullable = false, length = 1000)
    private String answer;
    @Column(nullable = false)
    private boolean enabled = true;
    public UUID getClinicId() { return clinicId; }
    public void setClinicId(UUID clinicId) { this.clinicId = clinicId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
