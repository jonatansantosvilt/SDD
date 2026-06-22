package com.viltgroup.statusmetro.linestatus.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.viltgroup.statusmetro.linestatus.domain.Line;
import com.viltgroup.statusmetro.linestatus.domain.LineStatus;
import com.viltgroup.statusmetro.linestatus.domain.Operator;
import com.viltgroup.statusmetro.linestatus.domain.StatusCategory;
import com.viltgroup.statusmetro.linestatus.domain.StatusSnapshot;
import com.viltgroup.statusmetro.linestatus.service.SnapshotCache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LineStatusControllerTest {

    private final SnapshotCache cache = mock(SnapshotCache.class);
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // ObjectMapper configured like Spring Boot's default (ISO-8601 java.time, no timestamps).
        ObjectMapper objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(new LineStatusController(cache))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    private static StatusSnapshot sampleSnapshot() {
        Line azul = new Line("METRO-L1", "1", "Azul", "#0A3D91", null,
                new LineStatus("OperacaoNormal", "Operação Normal", null, StatusCategory.NORMAL), "METRO");
        Line vermelha = new Line("METRO-L3", "3", "Vermelha", "#EE3D23", null,
                new LineStatus("VelocidadeReduzida", "Velocidade Reduzida", null, StatusCategory.DISRUPTED), "METRO");
        Operator metro = new Operator("METRO", "Metro SP", "SP", null, List.of(azul, vermelha));
        return new StatusSnapshot(
                LocalDateTime.of(2026, 6, 22, 18, 47, 59),
                LocalDateTime.of(2026, 6, 22, 18, 48, 1),
                false, true, false,
                List.of(metro),
                List.of(vermelha, azul));
    }

    @Test
    void returns200WithSnapshotMatchingContract() throws Exception {
        when(cache.current()).thenReturn(Optional.of(sampleSnapshot()));

        mockMvc.perform(get("/api/v1/line-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.stale").value(false))
                .andExpect(jsonPath("$.lastUpdated").value("2026-06-22T18:47:59"))
                .andExpect(jsonPath("$.operators[0].uid").value("METRO"))
                .andExpect(jsonPath("$.operators[0].lines[0].name").value("Azul"))
                .andExpect(jsonPath("$.operators[0].lines[0].status.category").value("NORMAL"))
                // orderedLines: disrupted first (FR-014)
                .andExpect(jsonPath("$.orderedLines[0].name").value("Vermelha"))
                .andExpect(jsonPath("$.orderedLines[0].status.category").value("DISRUPTED"))
                .andExpect(jsonPath("$.orderedLines[1].name").value("Azul"));
    }

    @Test
    void returns503WithFriendlyMessageWhenNoSnapshotEverLoaded() throws Exception {
        when(cache.current()).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/line-status"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.available").value(false))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
