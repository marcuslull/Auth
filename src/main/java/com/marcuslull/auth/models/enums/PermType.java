package com.marcuslull.auth.models.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum PermType {
    GUEST("GUEST"),
    USER("USER"),
    ADMIN("ADMIN");

    private static final Map<String, PermType> BY_LABEL = new ConcurrentHashMap<>();
    static {
        for (PermType perm : values()) {
            BY_LABEL.put(perm.label,perm);
        }
    }

    public final String label;
    PermType(String label) {
        this.label = label;
    }

    public static PermType valueOfLabel(String label) {
        if (Arrays.stream(PermType.values()).noneMatch(value -> value.label.equals(label))) {
            throw new RuntimeException("PermType: " + label + " not found");
        }
        return BY_LABEL.get(label);
    }
}
