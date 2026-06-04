package org.example.debug;

public enum LogLevel {
    INFO("[INFO]"),
    DEBUG("[DEBUG]"),
    WARN("[WARN]"),
    ERROR("[ERROR]");

    private final String prefix;

    LogLevel(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
