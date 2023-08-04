package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import com.wadajo.turismomadrid.domain.model.*;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

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
            var listaRaw = responseRaw.data();
            listaRaw.sort(Comparator.comparing(AlojamientoTuristicoRaw::alojamiento_tipo).thenComparing(AlojamientoTuristicoRaw::cdpostal));
            var listaFinal=convertFromRaw(listaRaw);
            generarMapaConLaCuenta(listaFinal);
            return listaFinal;
        } else {
            return Collections.emptyList();
        }
    }

    private static void generarMapaConLaCuenta(List<AlojamientoTuristico> listaFinal) {
        HashMap<String, AtomicLong> mapa=new HashMap<>();
        AtomicLong apartamentosRurales = new AtomicLong();
        AtomicLong apartTuristicos = new AtomicLong();
        AtomicLong campings = new AtomicLong();
        AtomicLong casasHuespedes = new AtomicLong();
        AtomicLong casasRurales = new AtomicLong();
        AtomicLong hostales = new AtomicLong();
        AtomicLong hosterias = new AtomicLong();
        AtomicLong hoteles = new AtomicLong();
        AtomicLong apartHoteles = new AtomicLong();
        AtomicLong hotelesRurales = new AtomicLong();
        AtomicLong pensiones = new AtomicLong();
        AtomicLong viviendasTuristicas = new AtomicLong();

        for (AlojamientoTuristico unAlojamiento : listaFinal){
            switch (unAlojamiento){
                case ApartamentoRural apartamentoRural -> {
                    apartamentosRurales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+apartamentoRural.alojamiento_tipo());
                }
                case ApartTuristico apartTuristico -> {
                    apartTuristicos.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+apartTuristico.alojamiento_tipo());
                }
                case Camping camping -> {
                    campings.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+camping.alojamiento_tipo());
                }
                case CasaHuespedes casaHuespedes -> {
                    casasHuespedes.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+casaHuespedes.alojamiento_tipo());
                }
                case CasaRural casaRural -> {
                    casasRurales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+casaRural.alojamiento_tipo());
                }
                case Hostal hostal -> {
                    hostales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hostal.alojamiento_tipo());
                }
                case Hosteria hosteria -> {
                    hosterias.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hosteria.alojamiento_tipo());
                }
                case Hotel hotel -> {
                    hoteles.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hotel.alojamiento_tipo());
                }
                case HotelApart hotelApart -> {
                    apartHoteles.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hotelApart.alojamiento_tipo());
                }
                case HotelRural hotelRural -> {
                    hotelesRurales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hotelRural.alojamiento_tipo());
                }
                case Pension pension -> {
                    pensiones.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+pension.alojamiento_tipo());
                }
                case ViviendaTuristica viviendaTuristica -> {
                    viviendasTuristicas.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+viviendaTuristica.alojamiento_tipo());
                }
            }
        }
            mapa.put(TipoAlojamiento.APARTAMENTO_RURAL.toString(),apartamentosRurales);
            mapa.put(TipoAlojamiento.APART_TURISTICO.toString(),apartTuristicos);
            mapa.put(TipoAlojamiento.CAMPING.toString(),campings);
            mapa.put(TipoAlojamiento.CASA_HUESPEDES.toString(),casasHuespedes);
            mapa.put(TipoAlojamiento.CASA_RURAL.toString(),casasRurales);
            mapa.put(TipoAlojamiento.HOSTAL.toString(),hostales);
            mapa.put(TipoAlojamiento.HOSTERIAS.toString(),hosterias);
            mapa.put(TipoAlojamiento.HOTEL.toString(),hoteles);
            mapa.put(TipoAlojamiento.HOTEL_APART.toString(),apartHoteles);
            mapa.put(TipoAlojamiento.HOTEL_RURAL.toString(),hotelesRurales);
            mapa.put(TipoAlojamiento.PENSION.toString(),pensiones);
            mapa.put(TipoAlojamiento.VIVIENDAS_TURISTICAS.toString(),viviendasTuristicas);
            LOGGER.log(Level.INFO,"Resultado: Total alojamientos turísticos: "+listaFinal.size()+". "+ mapa);
    }

    private AlojamientosTuristicosResponseDto getResponseRaw() {
        var client= RestClient.create(ALOJAMIENTOS_URL);
        return client
                .get()
                .retrieve()
                .body(AlojamientosTuristicosResponseDto.class);
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
