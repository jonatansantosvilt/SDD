package com.viltgroup.statusmetro.linestatus.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Enables {@link LineStatusProperties} and provides the {@link RestClient.Builder} used by the
 * upstream client, configured with connect/read timeouts. Defined explicitly (rather than relying on
 * auto-configuration) so the application is self-contained; tests bind their own
 * {@code MockRestServiceServer} to a separate builder.
 */
@Configuration
@EnableConfigurationProperties(LineStatusProperties.class)
public class HttpClientConfig {

    @Bean
    public RestClient.Builder ccrRestClientBuilder(LineStatusProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        if (properties.connectTimeout() != null) {
            factory.setConnectTimeout(properties.connectTimeout());
        }
        if (properties.readTimeout() != null) {
            factory.setReadTimeout(properties.readTimeout());
        }
        return RestClient.builder().requestFactory(factory);
    }
}
