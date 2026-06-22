package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.client.CcrLineStatusClient;
import com.viltgroup.statusmetro.linestatus.client.dto.CcrEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Periodically fetches the upstream status, normalizes it, and updates the {@link SnapshotCache}.
 * Runs every {@code linestatus.poll-interval} (60s — Spec FR-008) and once shortly after startup.
 * Any failure (HTTP, IO, {@code status:false}, empty data) records a cache failure so the API serves
 * last-known-good with a staleness flag.
 */
@Component
public class LineStatusPoller {

    private static final Logger log = LoggerFactory.getLogger(LineStatusPoller.class);

    private final CcrLineStatusClient client;
    private final NormalizationService normalizationService;
    private final SnapshotCache cache;

    public LineStatusPoller(CcrLineStatusClient client,
                            NormalizationService normalizationService,
                            SnapshotCache cache) {
        this.client = client;
        this.normalizationService = normalizationService;
        this.cache = cache;
    }

    @Scheduled(fixedRateString = "${linestatus.poll-interval}", initialDelay = 0)
    public void poll() {
        try {
            CcrEnvelope envelope = client.fetch();
            if (envelope != null && envelope.status() && envelope.data() != null) {
                cache.recordSuccess(normalizationService.normalize(envelope, LocalDateTime.now()));
                log.debug("Line status refreshed from upstream");
            } else {
                log.warn("Upstream returned an unsuccessful envelope; keeping last-known-good");
                cache.recordFailure();
            }
        } catch (Exception ex) {
            log.warn("Failed to fetch line status from upstream: {}", ex.getMessage());
            cache.recordFailure();
        }
    }
}
