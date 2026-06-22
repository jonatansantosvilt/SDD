package com.viltgroup.statusmetro.linestatus.domain;

import java.util.List;

/**
 * A transit operator (concessão) responsible for one or more lines. {@code lines} are ordered by
 * ascending line number within the operator.
 */
public record Operator(
        String uid,
        String name,
        String state,
        String logoUrl,
        List<Line> lines
) {
}
