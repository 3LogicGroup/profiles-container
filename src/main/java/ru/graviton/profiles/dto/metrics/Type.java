package ru.graviton.profiles.dto.metrics;

public enum Type {
        UNKNOWN, // This is untyped in Prometheus text format.
        COUNTER,
        GAUGE,
        STATE_SET,
        INFO,
        HISTOGRAM,
        GAUGE_HISTOGRAM,
        SUMMARY,
    }