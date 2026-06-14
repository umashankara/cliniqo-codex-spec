package com.cliniqo.clinic.repository;

import com.cliniqo.clinic.entity.ReminderDefault;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderDefaultRepository extends JpaRepository<ReminderDefault, UUID> {
}
