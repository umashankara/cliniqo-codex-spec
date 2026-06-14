package com.cliniqo.clinic.repository;

import com.cliniqo.clinic.entity.ClinicSettings;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicSettingsRepository extends JpaRepository<ClinicSettings, UUID> {
}
