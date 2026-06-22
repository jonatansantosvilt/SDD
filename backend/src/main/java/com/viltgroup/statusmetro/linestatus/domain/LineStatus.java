package com.viltgroup.statusmetro.linestatus.domain;

/**
 * The operational condition of a line. {@code label} is the human-readable PT-BR text from the
 * source and is always preserved (Spec FR-012); {@code code} is the raw upstream code.
 */
public record LineStatus(
        String code,
        String label,
        String description,
        StatusCategory category
) {
}
