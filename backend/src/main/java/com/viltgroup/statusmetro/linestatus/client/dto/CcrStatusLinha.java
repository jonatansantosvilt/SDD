package com.viltgroup.statusmetro.linestatus.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** The {@code statusLinha} object: machine code, PT-BR display text, and optional detail. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CcrStatusLinha(
        String codigo,
        String status,
        String descricao
) {
}
