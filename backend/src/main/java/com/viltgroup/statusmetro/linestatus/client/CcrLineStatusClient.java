package com.viltgroup.statusmetro.linestatus.client;

import com.viltgroup.statusmetro.linestatus.client.dto.CcrEnvelope;
import com.viltgroup.statusmetro.linestatus.config.LineStatusProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Thin client over the public CCR mobility line-status endpoint. Performs a single GET and
 * deserializes the envelope. HTTP/IO errors propagate to the caller (the poller treats them as a
 * failed fetch).
 */
@Component
public class CcrLineStatusClient {

    private final RestClient restClient;
    private final String upstreamUrl;

    public CcrLineStatusClient(RestClient.Builder restClientBuilder, LineStatusProperties properties) {
        this.restClient = restClientBuilder.build();
        this.upstreamUrl = properties.upstreamUrl();
    }

    public CcrEnvelope fetch() {
        return restClient.get()
                .uri(upstreamUrl)
                .retrieve()
                .body(CcrEnvelope.class);
    }
}
