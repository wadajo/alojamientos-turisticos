package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.client.AlojamientosClient;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
class AlojamientosService {

    private static final Logger LOGGER= LogManager.getLogger();

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

    private List<AlojamientoTuristico> convertFromRaw(List<AlojamientoTuristicoRaw> listaRaw){
        var alojamientosTuristicos=new ArrayList<AlojamientoTuristico>();
        listaRaw.forEach(alojamientoTuristicoRaw -> {
            switch (alojamientoTuristicoRaw.alojamiento_tipo()) {
                case "APARTAMENTO RURAL" -> alojamientosTuristicos.add(new AlojamientoTuristico.ApartamentoRural(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.APARTAMENTO_RURAL
                ));
                case "APART-TURISTICO" -> alojamientosTuristicos.add(new AlojamientoTuristico.ApartTuristico(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.APART_TURISTICO
                ));
                case "CAMPING" -> alojamientosTuristicos.add(new AlojamientoTuristico.Camping(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.CAMPING
                ));
                case "CASA HUESPEDES" -> alojamientosTuristicos.add(new AlojamientoTuristico.CasaHuespedes(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.CASA_HUESPEDES
                ));
                case "CASA RURAL" -> alojamientosTuristicos.add(new AlojamientoTuristico.CasaRural(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.CASA_RURAL
                ));
                case "HOSTAL" -> alojamientosTuristicos.add(new AlojamientoTuristico.Hostal(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.HOSTAL
                ));
                case "HOSTERIAS" -> alojamientosTuristicos.add(new AlojamientoTuristico.Hosteria(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.HOSTERIAS
                ));
                case "HOTEL" -> alojamientosTuristicos.add(new AlojamientoTuristico.Hotel(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.HOTEL
                ));
                case "HOTEL-APART." -> alojamientosTuristicos.add(new AlojamientoTuristico.HotelApart(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.HOTEL_APART
                ));
                case "HOTEL RURAL" -> alojamientosTuristicos.add(new AlojamientoTuristico.HotelRural(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.HOTEL_RURAL
                ));
                case "PENSION" -> alojamientosTuristicos.add(new AlojamientoTuristico.Pension(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.PENSION
                ));
                case "VIVIENDAS DE USO TU " -> alojamientosTuristicos.add(new AlojamientoTuristico.ViviendaTuristica(
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
                    alojamientoTuristicoRaw.localidad(),
                    TipoAlojamiento.VIVIENDAS_TURISTICAS
                ));
                default -> LOGGER.error("not recognized alojamiento tipo: {}", alojamientoTuristicoRaw.alojamiento_tipo());
            }
        });
        return Collections.unmodifiableList(alojamientosTuristicos);
    }

}
