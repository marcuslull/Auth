package com.marcuslull.auth.models.enums;

import java.util.HashMap;
import java.util.Map;

public enum ScopeType {
    OPENID("openid"),
    PROFILE("profile"),
    EMAIL("email"),
    OFFLINE("offline"),
    READ("read"),
    WRITE("write"),
    FULL("full");

    private static final Map<String, ScopeType> BY_LABEL = new HashMap<>();
    static {
        for (ScopeType scope : values()) {
            BY_LABEL.put(scope.label, scope);
        }
    }

    public final String label;
    ScopeType(String label) {
        this.label = label;
    }

    public static ScopeType valueOfLabel(String label) {
        return BY_LABEL.get(label);
    }
}
