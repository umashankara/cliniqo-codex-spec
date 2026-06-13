package com.cliniqo.publicwebsite.context;

import java.util.UUID;

public class WebsiteContext {
    private final UUID clinicId;
    private final String slug;
    private final String requestId;

    public WebsiteContext(UUID clinicId, String slug, String requestId) {
        this.clinicId = clinicId;
        this.slug = slug;
        this.requestId = requestId;
    }

    public UUID getClinicId() { return clinicId; }
    public String getSlug() { return slug; }
    public String getRequestId() { return requestId; }
}
