package com.wadajo.turismomadrid;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wiremock.spring.EnableWireMock;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableWireMock
@ActiveProfiles("test")
public class TurismoAcceptanceBase {



}
