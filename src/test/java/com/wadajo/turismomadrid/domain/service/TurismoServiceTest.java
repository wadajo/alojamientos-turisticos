package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.client.AlojamientosClient;
import com.wadajo.turismomadrid.application.exception.ResponseTypeDtoException;
import com.wadajo.turismomadrid.application.repository.*;
import com.wadajo.turismomadrid.domain.document.HotelDocument;
import com.wadajo.turismomadrid.domain.document.ViviendaTuristicaDocument;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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

    @Mock
    private AlojamientosService alojamientosService;

    @InjectMocks
    private TurismoService turismoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(conversionService.convert(Mockito.any(AlojamientoTuristicoRaw.class), eq(AlojamientoTuristico.class)))
            .thenAnswer(invocation -> {
                AlojamientoTuristicoRaw raw = invocation.getArgument(0);
                return new AlojamientoTuristico.Hotel(
                    raw.via_tipo(), raw.via_nombre(), raw.numero(), raw.portal(), raw.bloque(), raw.planta(), raw.puerta(), raw.signatura(), raw.categoria(), raw.escalera(), raw.denominacion(), raw.cdpostal(), raw.localidad(), TipoAlojamiento.valueOf(raw.alojamiento_tipo()));
            });
        when(conversionService.convert(Mockito.any(AlojamientoTuristico.Hotel.class), eq(HotelDocument.class)))
            .thenReturn(new HotelDocument());

        when(alojamientosService.getAlojamientosTotales())
            .thenReturn(getAlojamientosTuristicosEnRemoto());

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
        List<AlojamientoTuristico> result = turismoService.getAlojamientosTuristicosEnRemoto();

        assertThat(result).size().isEqualTo(2);
        assertThat(output).contains(TestConstants.RESULTADO_OUTPUT_MOCKS);
    }

    @Test
    void debeDevolverUnaListaVaciaCuandoEnElServidorNoHayAlojamientos(CapturedOutput output) {
        when(alojamientosService.getAlojamientosTotales())
            .thenReturn(Collections.emptyList());

        List<AlojamientoTuristico> result = turismoService.getAlojamientosTuristicosEnRemoto();

        assertThat(result).isEmpty();
        assertThat(output.getOut()).contains("Resultado: Total alojamientos turisticos: 0");
    }

    @Test
    void debeGuardarAlojamientoEnDbEInformarElResultado(CapturedOutput output) {
        when(hotelMongoRepository.saveAll(anyList()))
            .thenReturn(List.of(new HotelDocument(), new HotelDocument()));

        var result = turismoService.guardarTodosLosAlojamientosRemotosEnDb();

        assertThat(result).isEqualTo("Han sido guardados en DB: 2 alojamientos.");
        assertThat(output)
            .contains(TestConstants.RESULTADO_OUTPUT_MOCKS)
            .contains("Guardados en DB 2 hoteles.");
    }

    @Test
    void debeBorrarLosAlojamientosObsoletosEnCasoDeHaberlosEnBbDd(CapturedOutput output) {
        var hotelDocument1= new HotelDocument();
        setHotelDocumentValues(hotelDocument1, "2", "HOTEL DEL TEST");
        var hotelDocument2= new HotelDocument();
        setHotelDocumentValues(hotelDocument2, "5", "HOTEL DEL ENSAYO");
        var viviendaTuristicaDocument= new ViviendaTuristicaDocument();
        setViviendaTuristicaDocumentValues(viviendaTuristicaDocument);

        when(viviendaTuristicaMongoRepository.count())
            .thenReturn(1L);
        when(hotelMongoRepository.count())
            .thenReturn(2L);
        when(viviendaTuristicaMongoRepository.findAll())
            .thenReturn(Collections.singletonList(viviendaTuristicaDocument));
        when(hotelMongoRepository.findAll())
            .thenReturn(List.of(hotelDocument1, hotelDocument2));

        var result = turismoService.eliminarTodosLosAlojamientosObsoletosDeBbDd();

        verify(viviendaTuristicaMongoRepository).deleteAll(anyIterable());
        verify(hotelMongoRepository,never()).deleteAll(anyIterable());
        assertThat(result).isEqualTo("Han sido eliminados alojamientos obsoletos.");
        assertThat(output).contains(List.of(
            "Encontrados 1 alojamiento(s) obsoleto(s) del tipo: VIVIENDAS DE USO TU ",
            "Encontrado alojamiento obsoleto denominado: DALIAS, Nº 11"));
    }

    private static void setViviendaTuristicaDocumentValues(ViviendaTuristicaDocument viviendaTuristicaDocument) {
        viviendaTuristicaDocument.via_tipo = "CALLE";
        viviendaTuristicaDocument.via_nombre = "Dalias";
        viviendaTuristicaDocument.numero = "11";
        viviendaTuristicaDocument.portal = "";
        viviendaTuristicaDocument.bloque = "";
        viviendaTuristicaDocument.planta = "";
        viviendaTuristicaDocument.puerta = "";
        viviendaTuristicaDocument.signatura = "VT-1";
        viviendaTuristicaDocument.denominacion = "DALIAS, Nº 11";
        viviendaTuristicaDocument.codpostal = "28690";
        viviendaTuristicaDocument.localidad = "Brunete";
        viviendaTuristicaDocument.alojamiento_tipo = "VIVIENDAS DE USO TU ";
    }

    private static void setHotelDocumentValues(HotelDocument hotelDocument1, String numero, String denominacion) {
        hotelDocument1.via_tipo = "CALLE";
        hotelDocument1.via_nombre = "del Pez";
        hotelDocument1.numero = numero;
        hotelDocument1.portal = "";
        hotelDocument1.bloque = "";
        hotelDocument1.planta = "";
        hotelDocument1.puerta = "";
        hotelDocument1.signatura = "";
        hotelDocument1.categoria = "";
        hotelDocument1.escalera = "";
        hotelDocument1.denominacion = denominacion;
        hotelDocument1.codpostal = "28012";
        hotelDocument1.localidad = "Madrid";
        hotelDocument1.alojamiento_tipo = "HOTEL";
    }

    private static List<AlojamientoTuristico> getAlojamientosTuristicosEnRemoto() {
        AlojamientoTuristico.Hotel hotel1 = new AlojamientoTuristico.Hotel("CALLE","del Pez","2","","","","","","","","HOTEL DEL TEST","28012","Madrid",TipoAlojamiento.HOTEL);
        AlojamientoTuristico.Hotel hotel2 = new AlojamientoTuristico.Hotel("CALLE","del Pez","5","","","","","","","","HOTEL DEL ENSAYO","28012","Madrid",TipoAlojamiento.HOTEL);
        List<AlojamientoTuristico> alojamientosTuristicos = new ArrayList<>();
        alojamientosTuristicos.add(hotel1);
        alojamientosTuristicos.add(hotel2);
        return alojamientosTuristicos;
    }

}