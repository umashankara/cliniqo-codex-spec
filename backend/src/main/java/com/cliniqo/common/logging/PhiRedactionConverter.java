package com.cliniqo.common.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.pattern.CompositeConverter;
import java.util.regex.Pattern;

public class PhiRedactionConverter extends CompositeConverter<ILoggingEvent> {
    private static final String REDACTED = "[REDACTED]";
    private static final Pattern JWT = Pattern.compile("eyJ[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+");
    private static final Pattern E164 = Pattern.compile("\\+?[1-9]\\d{7,14}");
    private static final Pattern EMAIL = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}");
    private static final Pattern BCRYPT = Pattern.compile("\\$2[aby]\\$\\d{2}\\$[./A-Za-z0-9]{53}");
    private static final Pattern SENSITIVE_JSON = Pattern.compile(
            "(?i)(\"(?:password|accessToken|refreshToken|apiKey|appSecret|verifyToken|patientMessage|patientText|messageText|messageBody)\"\\s*:\\s*\")([^\"]+)(\")");

    @Override
    protected String transform(ILoggingEvent event, String message) {
        if (message == null) {
            return null;
        }
        message = SENSITIVE_JSON.matcher(message).replaceAll("$1" + REDACTED + "$3");
        message = JWT.matcher(message).replaceAll(REDACTED);
        message = BCRYPT.matcher(message).replaceAll(REDACTED);
        message = EMAIL.matcher(message).replaceAll(REDACTED);
        message = E164.matcher(message).replaceAll(REDACTED);
        return message;
    }
}
