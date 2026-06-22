package com.viltgroup.statusmetro.linestatus.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** An operator (concessão) in the upstream payload. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CcrConcessao(
        String uid,
        String nome,
        String estados,
        CcrAsset logo,
        List<CcrLinha> linhas
) {
}
