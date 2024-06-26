package com.marcuslull.auth.models.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ScopeType {
    OPENID("openid"),
    PROFILE("profile"),
    EMAIL("email"),
    OFFLINE("offline"),
    READ("read"),
    WRITE("write"),
    FULL("full");

    private static final Map<String, ScopeType> BY_LABEL = new ConcurrentHashMap<>();
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
        if (Arrays.stream(ScopeType.values()).noneMatch(value -> value.label.equals(label))) {
            throw new RuntimeException("ScopeType: " + label + " not found");
        }
        return BY_LABEL.get(label);
    }
}
