package com.viltgroup.statusmetro.linestatus.domain;

/**
 * Classification of a line's operational condition, used for visual treatment and ordering.
 * Lower {@link #rank()} sorts first (disrupted lines surface to the top — Spec FR-014).
 */
public enum StatusCategory {
    DISRUPTED(0),
    UNKNOWN(1),
    NORMAL(2);

    private final int rank;

    StatusCategory(int rank) {
        this.rank = rank;
    }

    public int rank() {
        return rank;
    }
}
