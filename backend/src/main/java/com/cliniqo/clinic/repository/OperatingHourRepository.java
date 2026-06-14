package com.cliniqo.clinic.repository;

import com.cliniqo.clinic.entity.OperatingHour;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperatingHourRepository extends JpaRepository<OperatingHour, UUID> {
}
