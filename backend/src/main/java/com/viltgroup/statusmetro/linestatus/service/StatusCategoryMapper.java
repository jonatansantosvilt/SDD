package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.domain.StatusCategory;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

/**
 * Maps an upstream status {@code codigo} to a {@link StatusCategory}. Unrecognized or blank codes
 * map to {@link StatusCategory#UNKNOWN} so the line is still shown with its original label
 * (Spec FR-011).
 */
@Component
public class StatusCategoryMapper {

    private static final Set<String> NORMAL_CODES = Set.of(
            "operacaonormal"
    );

    private static final Set<String> DISRUPTED_CODES = Set.of(
            "velocidadereduzida",
            "operacaoparcial",
            "paralisada",
            "paralisadatotal",
            "operacaoencerrada",
            "encerrada",
            "servicoencerrado",
            "operacaodiferenciada"
    );

    public StatusCategory map(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return StatusCategory.UNKNOWN;
        }
        String normalized = codigo.trim().toLowerCase(Locale.ROOT);
        if (NORMAL_CODES.contains(normalized)) {
            return StatusCategory.NORMAL;
        }
        if (DISRUPTED_CODES.contains(normalized)) {
            return StatusCategory.DISRUPTED;
        }
        return StatusCategory.UNKNOWN;
    }
}
