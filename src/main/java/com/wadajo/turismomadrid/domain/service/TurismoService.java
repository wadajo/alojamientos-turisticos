package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import com.wadajo.turismomadrid.domain.model.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Value("${turismomadrid.endpoint.url}")
    private String ALOJAMIENTOS_URL;

    @Cacheable("alojamientos")
    public List<AlojamientoTuristicoRaw> getAlojamientosTuristicos() {
        LOGGER.log(Level.INFO,"Dentro del service");
        var responseRaw = getResponseRaw();
        if (null!=responseRaw.data()) {
            var listaRaw = responseRaw.data();
            listaRaw.sort(Comparator.comparing(AlojamientoTuristicoRaw::alojamiento_tipo).thenComparing(AlojamientoTuristicoRaw::cdpostal));
            var listaFinal=convertFromRaw(listaRaw);
            //TODO transformar la devolución a lista con clases particulares no raw
            return listaRaw;
        } else {
            return Collections.emptyList();
        }
    }

    private AlojamientosTuristicosResponseDto getResponseRaw() {
        var client= RestClient.create(ALOJAMIENTOS_URL);
        var responseRaw = client
                .get()
                .retrieve()
                .body(AlojamientosTuristicosResponseDto.class);
        LOGGER.log(Level.INFO,"Respuesta raw: "+responseRaw);
        return responseRaw;
    }

    private List<AlojamientoTuristico> convertFromRaw(List<AlojamientoTuristicoRaw> listaRaw){
        var alojamientosTuristicos=new ArrayList<AlojamientoTuristico>();
        listaRaw.forEach(alojamientoTuristicoRaw -> {
            switch (alojamientoTuristicoRaw.alojamiento_tipo()) {
                case "APARTAMENTO RURAL" -> alojamientosTuristicos.add(new ApartamentoRural(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "APART-TURISTICO" -> alojamientosTuristicos.add(new ApartTuristico(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "CAMPING" -> alojamientosTuristicos.add(new Camping(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "CASA HUESPEDES" -> alojamientosTuristicos.add(new CasaHuespedes(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "CASA RURAL" -> alojamientosTuristicos.add(new CasaRural(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "HOSTAL" -> alojamientosTuristicos.add(new Hostal(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "HOSTERIAS" -> alojamientosTuristicos.add(new Hosteria(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "HOTEL" -> alojamientosTuristicos.add(new Hotel(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "HOTEL-APART." -> alojamientosTuristicos.add(new HotelApart(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "HOTEL RURAL" -> alojamientosTuristicos.add(new HotelRural(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "PENSION" -> alojamientosTuristicos.add(new Pension(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
                case "VIVIENDAS DE USO TU " -> alojamientosTuristicos.add(new ViviendaTuristica(
                        alojamientoTuristicoRaw.via_tipo(),
                        alojamientoTuristicoRaw.via_nombre(),
                        alojamientoTuristicoRaw.numero(),
                        alojamientoTuristicoRaw.portal(),
                        alojamientoTuristicoRaw.bloque(),
                        alojamientoTuristicoRaw.planta(),
                        alojamientoTuristicoRaw.puerta(),
                        alojamientoTuristicoRaw.escalera(),
                        alojamientoTuristicoRaw.denominacion(),
                        alojamientoTuristicoRaw.cdpostal(),
                        alojamientoTuristicoRaw.localidad()
                ));
            }
        });
        return alojamientosTuristicos;
    }
}
