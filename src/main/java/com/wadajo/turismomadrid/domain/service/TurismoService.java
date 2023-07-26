package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristico;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Cacheable("alojamientos")
    public List<AlojamientoTuristico> getAlojamientosTuristicos() {
        LOGGER.log(Level.INFO,"Dentro del service");
        var responseRaw = getResponseRaw();
        if (null!=responseRaw.data()) {
            var response = responseRaw.data();
            response.sort(Comparator.comparing(AlojamientoTuristico::cdpostal));
            return response;
        } else {
            return Collections.emptyList();
        }
    }

    private static AlojamientosTuristicosResponseDto getResponseRaw() {
        var client= RestClient.create("https://datos.comunidad.madrid/catalogo/dataset/134210b4-3fbc-457d-8064-18d6d8cc785e/resource/d5c66f92-0649-4454-85a5-8c3273f2c81a/download/alojamientos_turisticos.json");
        var responseRaw = client
                .get()
                .retrieve()
                .body(AlojamientosTuristicosResponseDto.class);
        LOGGER.log(Level.INFO,"Respuesta raw: "+responseRaw);
        return responseRaw;
    }
}
