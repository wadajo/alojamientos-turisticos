package com.wadajo.turismomadrid;

import com.wadajo.turismomadrid.application.repository.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.wiremock.spring.EnableWireMock;

@ExtendWith(SpringExtension.class)
@ExtendWith(OutputCaptureExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TurismomadridApplication.class)
@EnableWireMock
@ActiveProfiles("test")
public class TurismoAcceptanceBaseIT {

    @LocalServerPort
    protected int port;

    @MockitoBean
    private ApartamentoRuralMongoRepository apartamentoRuralMongoRepository;

    @MockitoBean
    private ApartTuristicoMongoRepository apartTuristicoMongoRepository;

    @MockitoBean
    private CampingMongoRepository campingMongoRepository;

    @MockitoBean
    private CasaHuespedesMongoRepository casaHuespedesMongoRepository;

    @MockitoBean
    private CasaRuralMongoRepository casaRuralMongoRepository;

    @MockitoBean
    private HostalMongoRepository hostalMongoRepository;

    @MockitoBean
    private HosteriaMongoRepository hosteriaMongoRepository;

    @MockitoBean
    private HotelApartMongoRepository hotelApartMongoRepository;

    @MockitoBean
    private HotelMongoRepository hotelMongoRepository;

    @MockitoBean
    private HotelRuralMongoRepository hotelRuralMongoRepository;

    @MockitoBean
    private PensionMongoRepository pensionMongoRepository;

    @MockitoBean
    private ViviendaTuristicaMongoRepository viviendaTuristicaMongoRepository;

}
