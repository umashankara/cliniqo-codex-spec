package com.cliniqo.clinic.repository;

import com.cliniqo.clinic.entity.FaqSeed;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqSeedRepository extends JpaRepository<FaqSeed, UUID> {
}
