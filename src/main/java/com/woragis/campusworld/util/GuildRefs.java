package com.woragis.campusworld.util;

import java.util.UUID;
import java.util.regex.Pattern;

public final class GuildRefs {

    private static final Pattern UUID_PATTERN = Pattern.compile(
            "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"
    );

    private GuildRefs() {
    }

    public static boolean isUuid(String value) {
        return value != null && UUID_PATTERN.matcher(value).matches();
    }
}
