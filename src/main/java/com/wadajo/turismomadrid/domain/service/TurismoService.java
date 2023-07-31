package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristico;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Value("${turismomadrid.endpoint.url}")
    private String ALOJAMIENTOS_URL;

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
        var client= RestClient.create(ALOJAMIENTOS_URL);
        var responseRaw = client
                .get()
                .retrieve()
                .body(AlojamientosTuristicosResponseDto.class);
        LOGGER.log(Level.INFO,"Respuesta raw: "+responseRaw);
        return responseRaw;
    }
}
