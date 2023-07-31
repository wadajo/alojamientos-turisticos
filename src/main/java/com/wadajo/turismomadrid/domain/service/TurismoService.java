package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Value("${turismomadrid.endpoint.url}")
    private String ALOJAMIENTOS_URL;

    @Cacheable("alojamientos")
    public AlojamientosTuristicosResponseDto getAlojamientosTuristicos() {
        LOGGER.log(Level.INFO,"Dentro del service");
        var client= RestClient.create(ALOJAMIENTOS_URL);
        var response = client
                .get()
                .retrieve()
                .body(AlojamientosTuristicosResponseDto.class);
        LOGGER.log(Level.INFO,"Respuesta: "+response);
        return response;
    }
}
