package com.wadajo.turismomadrid;

import com.wadajo.turismomadrid.application.controller.TurismoGraphqlController;
import com.wadajo.turismomadrid.domain.service.TurismoService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wiremock.spring.EnableWireMock;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TurismoGraphqlController.class)
@EnableWireMock
@ActiveProfiles("test")
public class TurismoAcceptanceBase {

    @Value("${wiremock.server.baseUrl}")
    protected String wireMockUrl;

    @MockitoBean
    TurismoService turismoService;

}
