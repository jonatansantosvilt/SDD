package com.viltgroup.statusmetro.linestatus.api.dto;

/**
 * Body returned with HTTP 503 when no snapshot has ever been successfully loaded (Spec FR-009a).
 * {@code available} is always false.
 */
public record Unavailable(boolean available, String message) {

    public static Unavailable now() {
        return new Unavailable(false,
                "Status temporariamente indisponível. Tente novamente em instantes.");
    }
}
