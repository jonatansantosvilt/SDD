package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.client.dto.CcrAsset;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrConcessao;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrData;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrEnvelope;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrLinha;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrStatusLinha;
import com.viltgroup.statusmetro.linestatus.config.LineStatusProperties;
import com.viltgroup.statusmetro.linestatus.domain.Line;
import com.viltgroup.statusmetro.linestatus.domain.StatusCategory;
import com.viltgroup.statusmetro.linestatus.domain.StatusSnapshot;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NormalizationServiceTest {

    private final LineStatusProperties props = new LineStatusProperties(
            "http://upstream/api",
            "https://assets.example.com",
            Duration.ofSeconds(60),
            Duration.ofSeconds(5),
            Duration.ofSeconds(10));

    private final NormalizationService service =
            new NormalizationService(new StatusCategoryMapper(), props);

    private static final LocalDateTime FETCHED_AT = LocalDateTime.of(2026, 6, 22, 18, 48, 1);

    private static CcrLinha linha(String uid, String numero, String nome, String cor, String codigo, String status) {
        return new CcrLinha(uid, numero, nome,
                new CcrAsset("/icons/" + numero + ".svg"), cor,
                new CcrStatusLinha(codigo, status, ""));
    }

    @Test
    void normalizesOperatorsLinesAndTimestamp() {
        CcrEnvelope envelope = new CcrEnvelope(true, "", "", new CcrData("2026-06-22T18:47:59",
                List.of(new CcrConcessao("METRO", "Metro SP", "SP", new CcrAsset("/logos/metro.svg"),
                        List.of(linha("METRO-L1", "1", "Azul", "#0A3D91", "OperacaoNormal", "Operação Normal"))))));

        StatusSnapshot snapshot = service.normalize(envelope, FETCHED_AT);

        assertThat(snapshot.available()).isTrue();
        assertThat(snapshot.partial()).isFalse();
        assertThat(snapshot.stale()).isFalse();
        assertThat(snapshot.lastUpdated()).isEqualTo(LocalDateTime.of(2026, 6, 22, 18, 47, 59));
        assertThat(snapshot.fetchedAt()).isEqualTo(FETCHED_AT);
        assertThat(snapshot.operators()).hasSize(1);
        assertThat(snapshot.orderedLines()).hasSize(1);

        Line azul = snapshot.orderedLines().get(0);
        assertThat(azul.name()).isEqualTo("Azul");
        assertThat(azul.colorRgb()).isEqualTo("#0A3D91");
        assertThat(azul.operatorUid()).isEqualTo("METRO");
        assertThat(azul.status().category()).isEqualTo(StatusCategory.NORMAL);
        assertThat(azul.status().label()).isEqualTo("Operação Normal");
    }

    @Test
    void resolvesAssetPathsAgainstConfiguredBase() {
        CcrEnvelope envelope = new CcrEnvelope(true, "", "", new CcrData("2026-06-22T18:47:59",
                List.of(new CcrConcessao("METRO", "Metro SP", "SP", new CcrAsset("/logos/metro.svg"),
                        List.of(linha("METRO-L1", "1", "Azul", "#0A3D91", "OperacaoNormal", "Operação Normal"))))));

        StatusSnapshot snapshot = service.normalize(envelope, FETCHED_AT);

        assertThat(snapshot.operators().get(0).logoUrl()).isEqualTo("https://assets.example.com/logos/metro.svg");
        assertThat(snapshot.orderedLines().get(0).iconUrl()).isEqualTo("https://assets.example.com/icons/1.svg");
    }

    @Test
    void invalidColorBecomesNull() {
        CcrEnvelope envelope = new CcrEnvelope(true, "", "", new CcrData("2026-06-22T18:47:59",
                List.of(new CcrConcessao("METRO", "Metro SP", "SP", null,
                        List.of(linha("METRO-L1", "1", "Azul", "blue", "OperacaoNormal", "Operação Normal"))))));

        StatusSnapshot snapshot = service.normalize(envelope, FETCHED_AT);

        assertThat(snapshot.orderedLines().get(0).colorRgb()).isNull();
    }

    @Test
    void emptyConcessoesYieldsUnavailableSnapshot() {
        CcrEnvelope envelope = new CcrEnvelope(true, "", "", new CcrData("2026-06-22T18:47:59", List.of()));

        StatusSnapshot snapshot = service.normalize(envelope, FETCHED_AT);

        assertThat(snapshot.available()).isFalse();
        assertThat(snapshot.orderedLines()).isEmpty();
    }

    @Test
    void malformedLineIsDroppedAndFlagsPartial() {
        CcrEnvelope envelope = new CcrEnvelope(true, "", "", new CcrData("2026-06-22T18:47:59",
                List.of(new CcrConcessao("METRO", "Metro SP", "SP", null, java.util.Arrays.asList(
                        linha("METRO-L1", "1", "Azul", "#0A3D91", "OperacaoNormal", "Operação Normal"),
                        new CcrLinha(null, null, null, null, null, null))))));

        StatusSnapshot snapshot = service.normalize(envelope, FETCHED_AT);

        assertThat(snapshot.partial()).isTrue();
        assertThat(snapshot.orderedLines()).hasSize(1);
    }

    @Test
    void unknownStatusKeepsLabelAndIsCategorizedUnknown() {
        CcrEnvelope envelope = new CcrEnvelope(true, "", "", new CcrData("2026-06-22T18:47:59",
                List.of(new CcrConcessao("CPTM", "CPTM", "SP", null,
                        List.of(linha("CPTM-L9", "9", "Esmeralda", "#9B59B6", "SituacaoNova", "Situação Nova"))))));

        StatusSnapshot snapshot = service.normalize(envelope, FETCHED_AT);

        Line line = snapshot.orderedLines().get(0);
        assertThat(line.status().category()).isEqualTo(StatusCategory.UNKNOWN);
        assertThat(line.status().label()).isEqualTo("Situação Nova");
    }
}
