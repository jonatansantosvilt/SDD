package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.domain.StatusSnapshot;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Holds the most recent successfully retrieved {@link StatusSnapshot} (last-known-good).
 *
 * <ul>
 *   <li>After a successful fetch the snapshot is current and not stale.</li>
 *   <li>After a failed fetch, a previously stored snapshot is retained and marked stale (FR-009).</li>
 *   <li>If no fetch has ever succeeded, {@link #current()} is empty (the API then reports
 *       unavailable — FR-009a).</li>
 * </ul>
 */
@Component
public class SnapshotCache {

    private volatile StatusSnapshot lastGood;
    private volatile boolean stale;

    public synchronized void recordSuccess(StatusSnapshot snapshot) {
        this.lastGood = snapshot;
        this.stale = false;
    }

    public synchronized void recordFailure() {
        if (lastGood != null) {
            this.stale = true;
        }
    }

    public Optional<StatusSnapshot> current() {
        StatusSnapshot snapshot = lastGood;
        if (snapshot == null) {
            return Optional.empty();
        }
        return Optional.of(stale ? snapshot.asStale() : snapshot);
    }
}
