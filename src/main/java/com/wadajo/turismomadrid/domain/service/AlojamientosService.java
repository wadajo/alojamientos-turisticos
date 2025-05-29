package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.client.AlojamientosClient;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.wadajo.turismomadrid.infrastructure.util.Utils.convertFromRaw;

@Service
class AlojamientosService {

    private final AlojamientosClient client;

    AlojamientosService(AlojamientosClient client) {
        this.client = client;
    }

    @Cacheable("alojamientos")
    List<AlojamientoTuristico> getAlojamientosTotales() {
        var responseRaw = client.getResponseRaw();
        if (Objects.nonNull(responseRaw.data())) {
            var listaRaw = responseRaw.data();
            listaRaw.sort(Comparator.comparing(AlojamientoTuristicoRaw::alojamiento_tipo).thenComparing(AlojamientoTuristicoRaw::cdpostal));
            return convertFromRaw(listaRaw);
        } else {
            return Collections.emptyList();
        }
    }


}
