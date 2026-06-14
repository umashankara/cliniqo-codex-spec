package com.cliniqo.clinic.service;

import com.cliniqo.clinic.entity.FaqSeed;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DefaultFaqSeedProvider {
    public List<FaqSeed> create(UUID clinicId) {
        FaqSeed hours = seed(clinicId, "general", "What are the clinic hours?", "Please contact the clinic for current operating hours.");
        FaqSeed location = seed(clinicId, "general", "Where is the clinic located?", "Please contact the clinic for location details.");
        return List.of(hours, location);
    }

    private FaqSeed seed(UUID clinicId, String category, String question, String answer) {
        FaqSeed seed = new FaqSeed();
        seed.setClinicId(clinicId);
        seed.setCategory(category);
        seed.setQuestion(question);
        seed.setAnswer(answer);
        seed.setEnabled(true);
        return seed;
    }
}
