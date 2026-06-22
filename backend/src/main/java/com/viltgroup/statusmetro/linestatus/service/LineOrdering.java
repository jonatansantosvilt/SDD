package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.domain.Line;

import java.util.Comparator;
import java.util.List;

/**
 * Ordering for the flattened line list: disrupted lines first, then unknown, then normal
 * (by {@code StatusCategory.rank()}); within a category, grouped by operator and then by ascending
 * line number (Spec FR-014).
 */
public final class LineOrdering {

    public static final Comparator<Line> DISRUPTED_FIRST =
            Comparator.comparingInt((Line l) -> l.status().category().rank())
                    .thenComparing(l -> safe(l.operatorUid()))
                    .thenComparingInt(LineOrdering::numberValue)
                    .thenComparing(l -> safe(l.name()));

    private LineOrdering() {
    }

    public static List<Line> order(List<Line> lines) {
        return lines.stream().sorted(DISRUPTED_FIRST).toList();
    }

    private static int numberValue(Line line) {
        try {
            return Integer.parseInt(line.number().trim());
        } catch (RuntimeException ex) {
            return Integer.MAX_VALUE;
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
