package com.viltgroup.statusmetro.linestatus.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** The {@code data} payload: update timestamp plus the list of operators. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CcrData(
        String dataAtualizacao,
        List<CcrConcessao> concessoes
) {
}
