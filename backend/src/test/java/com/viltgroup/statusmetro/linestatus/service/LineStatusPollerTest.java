package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.client.CcrLineStatusClient;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrData;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrEnvelope;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LineStatusPollerTest {

    private final CcrLineStatusClient client = mock(CcrLineStatusClient.class);
    private final NormalizationService normalizationService = mock(NormalizationService.class);
    private final SnapshotCache cache = mock(SnapshotCache.class);
    private final LineStatusPoller poller = new LineStatusPoller(client, normalizationService, cache);

    @Test
    void successfulFetchRecordsSuccess() {
        CcrEnvelope envelope = new CcrEnvelope(true, "", "", new CcrData("2026-06-22T18:47:59", List.of()));
        when(client.fetch()).thenReturn(envelope);

        poller.poll();

        verify(normalizationService).normalize(any(), any());
        verify(cache).recordSuccess(any());
        verify(cache, never()).recordFailure();
    }

    @Test
    void clientExceptionRecordsFailure() {
        when(client.fetch()).thenThrow(new RuntimeException("boom"));

        poller.poll();

        verify(cache).recordFailure();
        verify(cache, never()).recordSuccess(any());
    }

    @Test
    void unsuccessfulEnvelopeRecordsFailure() {
        when(client.fetch()).thenReturn(new CcrEnvelope(false, "erro", "X", null));

        poller.poll();

        verify(cache).recordFailure();
        verify(cache, never()).recordSuccess(any());
    }
}
