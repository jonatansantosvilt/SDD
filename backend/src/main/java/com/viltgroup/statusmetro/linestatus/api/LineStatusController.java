package com.viltgroup.statusmetro.linestatus.api;

import com.viltgroup.statusmetro.linestatus.api.dto.Unavailable;
import com.viltgroup.statusmetro.linestatus.service.SnapshotCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Serves the current normalized line-status snapshot from the {@link SnapshotCache}. Never calls
 * upstream synchronously — the scheduled poller keeps the cache warm.
 *
 * <ul>
 *   <li>200 — a snapshot is available (fresh, or last-known-good with {@code stale: true}).</li>
 *   <li>503 — no snapshot has ever loaded (Spec FR-009a).</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1")
public class LineStatusController {

    private final SnapshotCache cache;

    public LineStatusController(SnapshotCache cache) {
        this.cache = cache;
    }

    @Operation(summary = "Get the current normalized line-status snapshot")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Snapshot available (possibly stale)"),
            @ApiResponse(responseCode = "503", description = "No snapshot has ever loaded")
    })
    @GetMapping(value = "/line-status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getLineStatus() {
        return cache.current()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(Unavailable.now()));
    }
}
