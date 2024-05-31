package com.marcuslull.auth.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum GrantType {
    AUTHORIZATION_CODE("authorization_code"),
    REFRESH_TOKEN("refresh_token"),
    CLIENT_CREDENTIALS("client_credentials"),
    PASSWORD("password"),
    JWT_BEARER("urn:ietf:params:oauth:grant-type:jwt-bearer"),
    DEVICE_CODE("urn:ietf:params:oauth:grant-type:device_code");

    private static final Map<String, GrantType> BY_LABEL = new HashMap<>();
    static {
        for (GrantType grant : values()) {
            BY_LABEL.put(grant.label, grant);
        }
    }

    public final String label;
    GrantType(String label) {
        this.label = label;
    }

    public static GrantType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
