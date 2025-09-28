package com.wadajo.turismomadrid.application.client;

import com.wadajo.turismomadrid.infrastructure.configuration.RestClientConfig;
import com.wadajo.turismomadrid.util.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.OverrideAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;

import static com.wadajo.turismomadrid.util.TestConstants.ALOJAMIENTOS_RAW_STUBBING_FILE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(AlojamientosClient.class)
@OverrideAutoConfiguration(enabled = true)
@Import({RestClientConfig.class, TestConfig.class})
@ActiveProfiles("test")
class AlojamientosClientTest {

    @Autowired
    private AlojamientosClient client;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void debeProbarQueElClienteFunciona() {
        server
            .expect(requestTo(""))
            .andRespond(withSuccess(
                    new JsonMapper().readTree(new File(ALOJAMIENTOS_RAW_STUBBING_FILE)).toString(),
                    MediaType.APPLICATION_JSON));

        var responseRaw = client.getResponseRaw();

        assertThat(responseRaw).isNotNull();
        assertThat(responseRaw.data().getFirst().denominacion()).isEqualTo("GRAN LEGAZPI");
    }

}