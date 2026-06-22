package com.viltgroup.statusmetro.linestatus.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Top-level envelope of the CCR mobility response:
 * {@code { status, message, errorCode, data } }.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CcrEnvelope(
        boolean status,
        String message,
        String errorCode,
        CcrData data
) {
}
