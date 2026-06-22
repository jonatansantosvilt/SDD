package com.viltgroup.statusmetro.linestatus.domain;

/**
 * A single subway or train line. {@code colorRgb} is a validated {@code #RRGGBB} string or null;
 * {@code iconUrl} is an absolute URL or null.
 */
public record Line(
        String uid,
        String number,
        String name,
        String colorRgb,
        String iconUrl,
        LineStatus status,
        String operatorUid
) {
}
