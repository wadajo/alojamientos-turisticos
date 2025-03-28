package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.client.AlojamientosClient;
import com.wadajo.turismomadrid.application.exception.ResponseTypeDtoException;
import com.wadajo.turismomadrid.application.repository.*;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class TurismoServiceTest {

    @Mock
    private ApartamentoRuralMongoRepository apartamentoRuralMongoRepository;

    @Mock
    private ApartTuristicoMongoRepository apartTuristicoMongoRepository;

    @Mock
    private CampingMongoRepository campingMongoRepository;

    @Mock
    private CasaHuespedesMongoRepository casaHuespedesMongoRepository;

    @Mock
    private CasaRuralMongoRepository casaRuralMongoRepository;

    @Mock
    private HostalMongoRepository hostalMongoRepository;

    @Mock
    private HosteriaMongoRepository hosteriaMongoRepository;

    @Mock
    private HotelApartMongoRepository hotelApartMongoRepository;

    @Mock
    private HotelMongoRepository hotelMongoRepository;

    @Mock
    private HotelRuralMongoRepository hotelRuralMongoRepository;

    @Mock
    private PensionMongoRepository pensionMongoRepository;

    @Mock
    private ViviendaTuristicaMongoRepository viviendaTuristicaMongoRepository;

    @Mock
    private ConversionService conversionService;

    @Mock
    private AlojamientosClient alojamientosClient;

    @InjectMocks
    private TurismoService turismoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void debeBorrarTodo() {
        turismoService.borrarTodo();

        verify(apartamentoRuralMongoRepository).deleteAll();
        verify(apartTuristicoMongoRepository).deleteAll();
        verify(campingMongoRepository).deleteAll();
        verify(casaHuespedesMongoRepository).deleteAll();
        verify(casaRuralMongoRepository).deleteAll();
        verify(hostalMongoRepository).deleteAll();
        verify(hosteriaMongoRepository).deleteAll();
        verify(hotelApartMongoRepository).deleteAll();
        verify(hotelMongoRepository).deleteAll();
        verify(hotelRuralMongoRepository).deleteAll();
        verify(pensionMongoRepository).deleteAll();
        verify(viviendaTuristicaMongoRepository).deleteAll();
    }

    @Test
    void debeObtenerLosAlojamientosTuristicos() throws ResponseTypeDtoException {
        AlojamientoTuristicoRaw raw1 = new AlojamientoTuristicoRaw("","2","HOTEL","3-HOTEL","","","HM-127","del Pez","HOTEL DEL TEST","CALLE","28012","Madrid","","");
        AlojamientoTuristicoRaw raw2 = new AlojamientoTuristicoRaw("","5","HOTEL","3-HOTEL","","","HM-123","del Pez","HOTEL DEL ENSAYO","CALLE","28012","Madrid","","");
        List<AlojamientoTuristicoRaw> rawList = new ArrayList<>();
        rawList.add(raw1);
        rawList.add(raw2);
        AlojamientosTuristicosResponseDto responseDto = new AlojamientosTuristicosResponseDto(rawList);

        when(alojamientosClient.getResponseRaw())
                .thenReturn(responseDto);

        when(conversionService.convert(raw1, AlojamientoTuristico.class))
                .thenReturn(new AlojamientoTuristico.Hotel("Calle", "del Pez", "2", "", "", "", "", "", "HOTEL DEL TEST", "28012", "Madrid", TipoAlojamiento.HOTEL));
        when(conversionService.convert(raw2, AlojamientoTuristico.class))
                .thenReturn(new AlojamientoTuristico.Hotel("Calle", "del Pez", "5", "", "", "", "", "", "HOTEL DEL ENSAYO", "28012", "Madrid", TipoAlojamiento.HOTEL));

        List<AlojamientoTuristico> result = turismoService.getAlojamientosTuristicos();

        assertThat(result).size().isEqualTo(2);
    }

    @Test
    void debeDevolverUnaListaVaciaCuandoEnElServidorNoHayAlojamientos() {
        when(alojamientosClient.getResponseRaw())
            .thenReturn(new AlojamientosTuristicosResponseDto(null));

        List<AlojamientoTuristico> result = turismoService.getAlojamientosTuristicos();

        assertThat(result).isEmpty();
    }

    @Test
    void debeLogarUnErrorCuandoNoReconoceElTipoAlojamientoDelRaw(CapturedOutput output) {
        AlojamientoTuristicoRaw raw = new AlojamientoTuristicoRaw("","2","BIZARRO","3-HOTEL","","","HM-127","del Pez","HOTEL DEL TEST","CALLE","28012","Madrid","","");
        List<AlojamientoTuristicoRaw> rawList = Collections.singletonList(raw);
        AlojamientosTuristicosResponseDto responseDto = new AlojamientosTuristicosResponseDto(rawList);

        when(alojamientosClient.getResponseRaw())
                .thenReturn(responseDto);

        List<AlojamientoTuristico> result = turismoService.getAlojamientosTuristicos();

        assertThat(result).isEmpty();
        assertThat(output.getOut()).contains("not recognized alojamiento tipo: BIZARRO");
    }

}