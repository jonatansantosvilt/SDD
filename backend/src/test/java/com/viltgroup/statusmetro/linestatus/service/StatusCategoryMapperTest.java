package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.domain.StatusCategory;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusCategoryMapperTest {

    private final StatusCategoryMapper mapper = new StatusCategoryMapper();

    @Test
    void mapsOperacaoNormalToNormal() {
        assertThat(mapper.map("OperacaoNormal")).isEqualTo(StatusCategory.NORMAL);
    }

    @Test
    void mapsKnownDisruptionCodesToDisrupted() {
        assertThat(mapper.map("VelocidadeReduzida")).isEqualTo(StatusCategory.DISRUPTED);
        assertThat(mapper.map("OperacaoParcial")).isEqualTo(StatusCategory.DISRUPTED);
        assertThat(mapper.map("Paralisada")).isEqualTo(StatusCategory.DISRUPTED);
        assertThat(mapper.map("OperacaoEncerrada")).isEqualTo(StatusCategory.DISRUPTED);
    }

    @Test
    void mappingIsCaseInsensitiveAndTrimmed() {
        assertThat(mapper.map("  operacaonormal ")).isEqualTo(StatusCategory.NORMAL);
    }

    @Test
    void mapsUnrecognizedOrBlankCodeToUnknown() {
        assertThat(mapper.map("AlgoInesperado")).isEqualTo(StatusCategory.UNKNOWN);
        assertThat(mapper.map("")).isEqualTo(StatusCategory.UNKNOWN);
        assertThat(mapper.map(null)).isEqualTo(StatusCategory.UNKNOWN);
    }
}
