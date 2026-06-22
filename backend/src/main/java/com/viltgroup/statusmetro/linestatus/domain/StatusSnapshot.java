package com.viltgroup.statusmetro.linestatus.domain;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The full normalized result retrieved together at a point in time — the object held by the cache
 * and returned by the API. Serializes directly to the REST contract
 * ({@code specs/001-sp-line-status/contracts/line-status-api.yaml}).
 *
 * @param lastUpdated  upstream {@code dataAtualizacao}
 * @param fetchedAt    when the backend last fetched successfully
 * @param stale        true when serving a retained snapshot after a failed refresh (FR-009)
 * @param available    true when at least one operator/line is present
 * @param partial      true when the upstream set was incomplete (FR-010)
 * @param operators    normalized operators (each with its lines)
 * @param orderedLines all lines flattened, ordered disrupted-first (FR-014)
 */
public record StatusSnapshot(
        LocalDateTime lastUpdated,
        LocalDateTime fetchedAt,
        boolean stale,
        boolean available,
        boolean partial,
        List<Operator> operators,
        List<Line> orderedLines
) {
    /** Returns a copy of this snapshot flagged stale (used when serving last-known-good). */
    public StatusSnapshot asStale() {
        return new StatusSnapshot(lastUpdated, fetchedAt, true, available, partial, operators, orderedLines);
    }
}
