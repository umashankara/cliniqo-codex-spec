package com.cliniqo.clinic.repository;

import com.cliniqo.clinic.entity.Clinic;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClinicRepository extends JpaRepository<Clinic, UUID> {
    Optional<Clinic> findBySlug(String slug);
    boolean existsBySlug(String slug);
}
