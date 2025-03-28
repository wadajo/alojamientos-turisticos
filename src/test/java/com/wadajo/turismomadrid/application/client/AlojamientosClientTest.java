package com.wadajo.turismomadrid.application.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wadajo.turismomadrid.application.exception.ResponseTypeDtoException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.File;
import java.io.IOException;

import static com.wadajo.turismomadrid.util.TestConstants.ALOJAMIENTOS_RAW_FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(AlojamientosClient.class)
@OverrideAutoConfiguration(enabled = true)
class AlojamientosClientTest {

    @Autowired
    private AlojamientosClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void debeProbarQueElClienteFunciona() throws IOException {
        server
            .expect(requestTo(""))
            .andRespond(withSuccess(
                    new ObjectMapper().readTree(new File(ALOJAMIENTOS_RAW_FILE)).toString(),
                    MediaType.APPLICATION_JSON));

        var responseRaw = client.getResponseRaw();

        assertThat(responseRaw).isNotNull();
        assertThat(responseRaw.data().getFirst().denominacion()).isEqualTo("GRAN LEGAZPI");
    }

    @Test
    void debeArrojarExcepcionCuandoElServidorRetornaNull() {
        server
            .expect(requestTo(""))
            .andRespond(withSuccess());

        assertThatExceptionOfType(ResponseTypeDtoException.class)
            .isThrownBy(()-> client.getResponseRaw());
    }

}