package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Cacheable("alojamientos")
    public AlojamientosTuristicosResponseDto getAlojamientosTuristicos() {
        LOGGER.log(Level.INFO,"Dentro del service");
        var client= RestClient.create("https://datos.comunidad.madrid/catalogo/dataset/134210b4-3fbc-457d-8064-18d6d8cc785e/resource/d5c66f92-0649-4454-85a5-8c3273f2c81a/download/alojamientos_turisticos.json");
        var response = client
                .get()
                .retrieve()
                .body(AlojamientosTuristicosResponseDto.class);
        LOGGER.log(Level.INFO,"Respuesta: "+response);
        return response;
    }
}
