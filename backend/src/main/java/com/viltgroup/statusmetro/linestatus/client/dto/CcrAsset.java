package com.viltgroup.statusmetro.linestatus.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/** A branding asset reference; the upstream field is {@code _path} (relative to the CCR host). */
public record CcrAsset(
        @JsonProperty("_path") String path
) {
}
