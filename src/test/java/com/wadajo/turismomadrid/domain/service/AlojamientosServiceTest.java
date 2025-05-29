package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.client.AlojamientosClient;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(OutputCaptureExtension.class)
class AlojamientosServiceTest {

    @Mock
    private AlojamientosClient alojamientosClient;

    @InjectMocks
    private AlojamientosService alojamientosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void debeLogarUnErrorCuandoNoReconoceElTipoAlojamientoDelRaw(CapturedOutput output) {
        AlojamientoTuristicoRaw raw = new AlojamientoTuristicoRaw("","2","BIZARRO","3-HOTEL","","","HM-127","del Pez","HOTEL DEL TEST","CALLE","28012","Madrid","","");
        List<AlojamientoTuristicoRaw> rawList = Collections.singletonList(raw);
        AlojamientosTuristicosResponseDto gotchaResponseDto = new AlojamientosTuristicosResponseDto(rawList);

        when(alojamientosClient.getResponseRaw()).thenReturn(gotchaResponseDto);

        var result = alojamientosService.getAlojamientosTotales();

        assertThat(result).isEmpty();
        assertThat(output.getOut()).contains("not recognized alojamiento tipo: BIZARRO");
    }

    @Test
    void debeDevolverVacioCuandoElClienteRetornaNulo() {
        when(alojamientosClient.getResponseRaw()).thenReturn(new AlojamientosTuristicosResponseDto(null));

        var result = alojamientosService.getAlojamientosTotales();

        assertThat(result)
            .isInstanceOf(List.class)
            .isEmpty();
    }
}
