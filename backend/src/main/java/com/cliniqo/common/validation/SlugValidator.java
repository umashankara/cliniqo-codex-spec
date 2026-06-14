package com.cliniqo.common.validation;

import com.cliniqo.common.exception.ValidationException;
import java.util.regex.Pattern;

public final class SlugValidator {
    private static final Pattern SLUG = Pattern.compile("^[a-z0-9]+(?:-[a-z0-9]+)*$");

    private SlugValidator() {
    }

    public static void requireCanonical(String field, String value) {
        if (value == null || value.length() < 3 || value.length() > 80 || !SLUG.matcher(value).matches()) {
            throw new ValidationException(field + " must be lower-case URL-safe slug");
        }
    }
}
