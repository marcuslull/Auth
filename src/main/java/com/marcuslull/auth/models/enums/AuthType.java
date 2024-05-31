package com.marcuslull.auth.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum AuthType {
    CLIENT_SECRET_BASIC("client_secret_basic"),
    CLIENT_SECRET_POST("client_secret_post"),
    CLIENT_SECRET_JWT("client_secret_jwt"),
    PRIVATE_KEY_JWT("private_key_jwt"),
    NONE("none");

    private static final Map<String, AuthType> BY_LABEL = new HashMap<>();
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
        return BY_LABEL.get(label);
    }
}
