package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.client.AlojamientosClient;
import com.wadajo.turismomadrid.application.exception.ResponseTypeDtoException;
import com.wadajo.turismomadrid.application.repository.*;
import com.wadajo.turismomadrid.domain.document.HotelDocument;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import com.wadajo.turismomadrid.util.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
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
        when(conversionService.convert(Mockito.any(AlojamientoTuristicoRaw.class), eq(AlojamientoTuristico.class)))
            .thenAnswer(invocation -> {
                AlojamientoTuristicoRaw raw = invocation.getArgument(0);
                return new AlojamientoTuristico.Hotel(
                    raw.via_tipo(), raw.via_nombre(), raw.numero(), raw.portal(), raw.bloque(), raw.planta(), raw.puerta(), raw.escalera(), raw.denominacion(), raw.cdpostal(), raw.localidad(), TipoAlojamiento.valueOf(raw.alojamiento_tipo()));
            });
        when(conversionService.convert(Mockito.any(AlojamientoTuristico.Hotel.class), eq(HotelDocument.class)))
            .thenReturn(new HotelDocument());
    }

    @Test
    void debeBorrarTodo(CapturedOutput output) {
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

        assertThat(output).contains("Borradas todas las colecciones");
    }

    @Test
    void debeObtenerLosAlojamientosTuristicos(CapturedOutput output) throws ResponseTypeDtoException {
        AlojamientoTuristicoRaw raw1 = new AlojamientoTuristicoRaw("","2","HOTEL","3-HOTEL","","","HM-127","del Pez","HOTEL DEL TEST","CALLE","28012","Madrid","","");
        AlojamientoTuristicoRaw raw2 = new AlojamientoTuristicoRaw("","5","HOTEL","3-HOTEL","","","HM-123","del Pez","HOTEL DEL ENSAYO","CALLE","28012","Madrid","","");
        List<AlojamientoTuristicoRaw> rawList = new ArrayList<>();
        rawList.add(raw1);
        rawList.add(raw2);
        AlojamientosTuristicosResponseDto responseDto = new AlojamientosTuristicosResponseDto(rawList);

        when(alojamientosClient.getResponseRaw())
                .thenReturn(responseDto);

        List<AlojamientoTuristico> result = turismoService.getAlojamientosTuristicos();

        assertThat(result).size().isEqualTo(2);
        assertThat(output).contains(TestConstants.RESULTADO_OUTPUT_MOCKS);
    }

    @Test
    void debeDevolverUnaListaVaciaCuandoEnElServidorNoHayAlojamientos(CapturedOutput output) {
        when(alojamientosClient.getResponseRaw())
            .thenReturn(new AlojamientosTuristicosResponseDto(null));

        List<AlojamientoTuristico> result = turismoService.getAlojamientosTuristicos();

        assertThat(result).isEmpty();
        assertThat(output.getOut()).contains("Resultado: Total alojamientos turisticos: 0");
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

    @Test
    void debeActualizarAlojamientoEnDbEInformarElResultado(CapturedOutput output) {
        AlojamientoTuristicoRaw raw1 = new AlojamientoTuristicoRaw("","2","HOTEL","3-HOTEL","","","HM-127","del Pez","HOTEL DEL TEST","CALLE","28012","Madrid","","");
        AlojamientoTuristicoRaw raw2 = new AlojamientoTuristicoRaw("","5","HOTEL","3-HOTEL","","","HM-123","del Pez","HOTEL DEL ENSAYO","CALLE","28012","Madrid","","");
        List<AlojamientoTuristicoRaw> rawList = new ArrayList<>();
        rawList.add(raw1);
        rawList.add(raw2);
        AlojamientosTuristicosResponseDto responseDto = new AlojamientosTuristicosResponseDto(rawList);

        when(alojamientosClient.getResponseRaw())
            .thenReturn(responseDto);

        var result = turismoService.actualizarAlojamientosEnDb();

        assertThat(result).isEqualTo("Han sido actualizados en DB: 2 alojamientos.");
        assertThat(output)
            .contains(TestConstants.RESULTADO_OUTPUT_MOCKS)
            .contains("Guardados en DB 2 hoteles.");
    }

}