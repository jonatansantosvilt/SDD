package com.viltgroup.statusmetro.linestatus.service;

import com.viltgroup.statusmetro.linestatus.domain.StatusSnapshot;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SnapshotCacheTest {

    private final SnapshotCache cache = new SnapshotCache();

    private static StatusSnapshot snapshot(LocalDateTime lastUpdated) {
        return new StatusSnapshot(lastUpdated, lastUpdated.plusSeconds(2), false, true, false,
                List.of(), List.of());
    }

    @Test
    void emptyWhenNoFetchEverSucceeded() {
        assertThat(cache.current()).isEmpty();
    }

    @Test
    void recordFailureWithNoPriorStaysUnavailable() {
        cache.recordFailure();
        assertThat(cache.current()).isEmpty();
    }

    @Test
    void afterSuccessSnapshotIsCurrentAndNotStale() {
        StatusSnapshot s = snapshot(LocalDateTime.of(2026, 6, 22, 18, 47, 59));
        cache.recordSuccess(s);

        assertThat(cache.current()).isPresent();
        assertThat(cache.current().get().stale()).isFalse();
        assertThat(cache.current().get().lastUpdated()).isEqualTo(s.lastUpdated());
    }

    @Test
    void afterFailureFollowingSuccessServesLastKnownGoodMarkedStale() {
        StatusSnapshot good = snapshot(LocalDateTime.of(2026, 6, 22, 18, 30, 0));
        cache.recordSuccess(good);

        cache.recordFailure();

        assertThat(cache.current()).isPresent();
        assertThat(cache.current().get().stale()).isTrue();
        assertThat(cache.current().get().lastUpdated()).isEqualTo(good.lastUpdated());
    }

    @Test
    void recoveryAfterFailureClearsStaleFlag() {
        cache.recordSuccess(snapshot(LocalDateTime.of(2026, 6, 22, 18, 30, 0)));
        cache.recordFailure();
        cache.recordSuccess(snapshot(LocalDateTime.of(2026, 6, 22, 18, 49, 0)));

        assertThat(cache.current().get().stale()).isFalse();
    }
}
