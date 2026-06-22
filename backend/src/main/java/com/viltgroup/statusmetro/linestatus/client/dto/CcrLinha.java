package com.viltgroup.statusmetro.linestatus.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** A line in the upstream payload. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CcrLinha(
        String uid,
        String numero,
        String nome,
        CcrAsset icone,
        String corRgb,
        CcrStatusLinha statusLinha
) {
}
