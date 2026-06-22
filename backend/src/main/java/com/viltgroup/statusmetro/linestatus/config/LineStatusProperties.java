package com.viltgroup.statusmetro.linestatus.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Configuration for the line-status feature, bound from the {@code linestatus.*} keys in
 * {@code application.yml}.
 */
@ConfigurationProperties(prefix = "linestatus")
public record LineStatusProperties(
        String upstreamUrl,
        String assetBaseUrl,
        Duration pollInterval,
        Duration connectTimeout,
        Duration readTimeout
) {
}
