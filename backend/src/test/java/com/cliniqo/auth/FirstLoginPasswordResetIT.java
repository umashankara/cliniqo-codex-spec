package com.cliniqo.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.cliniqo.auth.entity.User;
import org.junit.jupiter.api.Test;

class FirstLoginPasswordResetIT {
    @Test
    void firstAdminCanBeMarkedForReset() {
        User user = new User();
        user.setForcePasswordReset(true);
        assertTrue(user.isForcePasswordReset());
    }
}
