package com.marcuslull.auth.models.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum AuthType {
    CLIENT_SECRET_BASIC("client_secret_basic"),
    CLIENT_SECRET_POST("client_secret_post"),
    CLIENT_SECRET_JWT("client_secret_jwt"),
    PRIVATE_KEY_JWT("private_key_jwt"),
    NONE("none");

    private static final Map<String, AuthType> BY_LABEL = new ConcurrentHashMap<>();
    static {
        for (AuthType auth : values()) {
            BY_LABEL.put(auth.label, auth);
        }
    }

    public final String label;
    AuthType(String label) {
        this.label = label;
    }

    public static AuthType valueOfLabel(String label) {
        if (Arrays.stream(AuthType.values()).noneMatch(value -> value.label.equals(label))) {
            throw new RuntimeException("AuthType: " + label + " not found");
        }
        return BY_LABEL.get(label);
    }
}
