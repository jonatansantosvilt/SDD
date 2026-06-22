package com.viltgroup.statusmetro.linestatus.client;

import com.viltgroup.statusmetro.linestatus.client.dto.CcrEnvelope;
import com.viltgroup.statusmetro.linestatus.config.LineStatusProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CcrLineStatusClientTest {

    private static final String URL = "http://upstream/api";

    private static final String SUCCESS_JSON = """
            {
              "status": true, "message": "", "errorCode": "",
              "data": {
                "dataAtualizacao": "2026-06-22T18:47:59",
                "concessoes": [
                  {
                    "uid": "METRO", "nome": "Metro SP", "estados": "SP",
                    "logo": { "_path": "/logos/metro.svg" },
                    "linhas": [
                      {
                        "uid": "METRO-L1", "numero": "1", "nome": "Azul",
                        "icone": { "_path": "/icons/1.svg" }, "corRgb": "#0A3D91",
                        "statusLinha": { "codigo": "OperacaoNormal", "status": "Operação Normal", "descricao": "" }
                      }
                    ]
                  }
                ]
              }
            }
            """;

    private static LineStatusProperties props() {
        return new LineStatusProperties(URL, "https://host", Duration.ofSeconds(60),
                Duration.ofSeconds(5), Duration.ofSeconds(10));
    }

    @Test
    void parsesSuccessfulEnvelope() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(URL)).andRespond(withSuccess(SUCCESS_JSON, MediaType.APPLICATION_JSON));
        CcrLineStatusClient client = new CcrLineStatusClient(builder, props());

        CcrEnvelope envelope = client.fetch();

        assertThat(envelope.status()).isTrue();
        assertThat(envelope.data().dataAtualizacao()).isEqualTo("2026-06-22T18:47:59");
        assertThat(envelope.data().concessoes()).hasSize(1);
        assertThat(envelope.data().concessoes().get(0).linhas().get(0).statusLinha().codigo())
                .isEqualTo("OperacaoNormal");
        server.verify();
    }

    @Test
    void serverErrorPropagatesAsException() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(URL)).andRespond(withServerError());
        CcrLineStatusClient client = new CcrLineStatusClient(builder, props());

        assertThatThrownBy(client::fetch).isInstanceOf(RestClientException.class);
    }

    @Test
    void unsuccessfulEnvelopeIsReturnedForCallerToHandle() {
        RestClient.Builder builder = RestClient.builder();
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo(URL)).andRespond(withSuccess(
                "{\"status\":false,\"message\":\"erro\",\"errorCode\":\"X\",\"data\":null}",
                MediaType.APPLICATION_JSON));
        CcrLineStatusClient client = new CcrLineStatusClient(builder, props());

        CcrEnvelope envelope = client.fetch();

        assertThat(envelope.status()).isFalse();
        assertThat(envelope.data()).isNull();
    }
}
