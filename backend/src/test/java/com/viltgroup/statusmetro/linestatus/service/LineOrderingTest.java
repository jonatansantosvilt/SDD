package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.domain.Line;
import com.viltgroup.statusmetro.linestatus.domain.LineStatus;
import com.viltgroup.statusmetro.linestatus.domain.StatusCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LineOrderingTest {

    private static Line line(String name, String operator, String number, StatusCategory category) {
        return new Line(operator + "-" + number, number, name, "#000000", null,
                new LineStatus("code", "label", null, category), operator);
    }

    @Test
    void disruptedLinesSortBeforeUnknownThenNormal() {
        Line normal = line("Azul", "METRO", "1", StatusCategory.NORMAL);
        Line unknown = line("Esmeralda", "CPTM", "9", StatusCategory.UNKNOWN);
        Line disrupted = line("Vermelha", "METRO", "3", StatusCategory.DISRUPTED);

        List<Line> ordered = LineOrdering.order(List.of(normal, unknown, disrupted));

        assertThat(ordered).extracting(Line::name)
                .containsExactly("Vermelha", "Esmeralda", "Azul");
    }

    @Test
    void withinSameCategoryOrdersByOperatorThenLineNumber() {
        Line cptm8 = line("Diamante", "CPTM", "8", StatusCategory.NORMAL);
        Line metro2 = line("Verde", "METRO", "2", StatusCategory.NORMAL);
        Line metro1 = line("Azul", "METRO", "1", StatusCategory.NORMAL);

        List<Line> ordered = LineOrdering.order(List.of(cptm8, metro2, metro1));

        assertThat(ordered).extracting(Line::name)
                .containsExactly("Diamante", "Azul", "Verde");
    }
}
