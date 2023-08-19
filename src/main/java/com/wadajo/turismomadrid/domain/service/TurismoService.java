package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.repository.AlojamientosMongoRepository;
import com.wadajo.turismomadrid.domain.document.AlojamientoDocument;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientoTuristicoRaw;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Value("${turismomadrid.endpoint.url}")
    private String ALOJAMIENTOS_URL;

    private final AlojamientosMongoRepository repository;

    public TurismoService(AlojamientosMongoRepository repository) {
        this.repository = repository;
    }

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

    public String guardarAlojamientosEnDb(){
        var todosLosAlojamientos=getAlojamientosTuristicos();
        AtomicLong guardados=new AtomicLong();

        for (AlojamientoTuristico unAlojamiento : todosLosAlojamientos) {
            AlojamientoDocument nuevo=new AlojamientoDocument();
            switch (unAlojamiento) {
                case AlojamientoTuristico.ApartamentoRural apartamentoRural -> {
                    nuevo = new AlojamientoDocument(apartamentoRural.via_tipo(), apartamentoRural.via_nombre(), apartamentoRural.numero(), apartamentoRural.portal(), apartamentoRural.bloque(), apartamentoRural.planta(), apartamentoRural.puerta(), apartamentoRural.escalera(), apartamentoRural.denominacion(), apartamentoRural.cdpostal(), apartamentoRural.localidad(), TipoAlojamiento.APARTAMENTO_RURAL, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + apartamentoRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                    nuevo = new AlojamientoDocument(apartTuristico.via_tipo(), apartTuristico.via_nombre(), apartTuristico.numero(), apartTuristico.portal(), apartTuristico.bloque(), apartTuristico.planta(), apartTuristico.puerta(), apartTuristico.escalera(), apartTuristico.denominacion(), apartTuristico.cdpostal(), apartTuristico.localidad(), TipoAlojamiento.APART_TURISTICO, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + apartTuristico.alojamiento_tipo());
                }
                case AlojamientoTuristico.Camping camping -> {
                    nuevo = new AlojamientoDocument(camping.via_tipo(), camping.via_nombre(), camping.numero(), camping.portal(), camping.bloque(), camping.planta(), camping.puerta(), camping.escalera(), camping.denominacion(), camping.cdpostal(), camping.localidad(), TipoAlojamiento.CAMPING, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + camping.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                    nuevo = new AlojamientoDocument(casaHuespedes.via_tipo(), casaHuespedes.via_nombre(), casaHuespedes.numero(), casaHuespedes.portal(), casaHuespedes.bloque(), casaHuespedes.planta(), casaHuespedes.puerta(), casaHuespedes.escalera(), casaHuespedes.denominacion(), casaHuespedes.cdpostal(), casaHuespedes.localidad(), TipoAlojamiento.CASA_HUESPEDES, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + casaHuespedes.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaRural casaRural -> {
                    nuevo = new AlojamientoDocument(casaRural.via_tipo(), casaRural.via_nombre(), casaRural.numero(), casaRural.portal(), casaRural.bloque(), casaRural.planta(), casaRural.puerta(), casaRural.escalera(), casaRural.denominacion(), casaRural.cdpostal(), casaRural.localidad(), TipoAlojamiento.CASA_RURAL, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + casaRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hostal hostal -> {
                    nuevo = new AlojamientoDocument(hostal.via_tipo(), hostal.via_nombre(), hostal.numero(), hostal.portal(), hostal.bloque(), hostal.planta(), hostal.puerta(), hostal.escalera(), hostal.denominacion(), hostal.cdpostal(), hostal.localidad(), TipoAlojamiento.HOSTAL, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + hostal.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hosteria hosteria -> {
                    nuevo = new AlojamientoDocument(hosteria.via_tipo(), hosteria.via_nombre(), hosteria.numero(), hosteria.portal(), hosteria.bloque(), hosteria.planta(), hosteria.puerta(), hosteria.escalera(), hosteria.denominacion(), hosteria.cdpostal(), hosteria.localidad(), TipoAlojamiento.HOSTERIAS, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + hosteria.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hotel hotel -> {
                    nuevo = new AlojamientoDocument(hotel.via_tipo(), hotel.via_nombre(), hotel.numero(), hotel.portal(), hotel.bloque(), hotel.planta(), hotel.puerta(), hotel.escalera(), hotel.denominacion(), hotel.cdpostal(), hotel.localidad(), TipoAlojamiento.HOTEL, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + hotel.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelApart hotelApart -> {
                    nuevo = new AlojamientoDocument(hotelApart.via_tipo(), hotelApart.via_nombre(), hotelApart.numero(), hotelApart.portal(), hotelApart.bloque(), hotelApart.planta(), hotelApart.puerta(), hotelApart.escalera(), hotelApart.denominacion(), hotelApart.cdpostal(), hotelApart.localidad(), TipoAlojamiento.HOTEL_APART, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + hotelApart.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelRural hotelRural -> {
                    nuevo = new AlojamientoDocument(hotelRural.via_tipo(), hotelRural.via_nombre(), hotelRural.numero(), hotelRural.portal(), hotelRural.bloque(), hotelRural.planta(), hotelRural.puerta(), hotelRural.escalera(), hotelRural.denominacion(), hotelRural.cdpostal(), hotelRural.localidad(), TipoAlojamiento.HOTEL_RURAL, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + hotelRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Pension pension -> {
                    nuevo = new AlojamientoDocument(pension.via_tipo(), pension.via_nombre(), pension.numero(), pension.portal(), pension.bloque(), pension.planta(), pension.puerta(), pension.escalera(), pension.denominacion(), pension.cdpostal(), pension.localidad(), TipoAlojamiento.PENSION, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + pension.alojamiento_tipo());
                }
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
                    nuevo = new AlojamientoDocument(viviendaTuristica.via_tipo(), viviendaTuristica.via_nombre(), viviendaTuristica.numero(), viviendaTuristica.portal(), viviendaTuristica.bloque(), viviendaTuristica.planta(), viviendaTuristica.puerta(), viviendaTuristica.escalera(), viviendaTuristica.denominacion(), viviendaTuristica.cdpostal(), viviendaTuristica.localidad(), TipoAlojamiento.VIVIENDAS_TURISTICAS, LocalDateTime.now());
                    LOGGER.log(Level.INFO, "Reconocido un  " + viviendaTuristica.alojamiento_tipo());
                }
            }
            verificarSiExisteYDeLoContrarioGuardarEnDb(nuevo, guardados);
        }
        LOGGER.log(Level.INFO, "Guardados: "+guardados);
        return "Guardados en DB: "+ guardados;
    }

    private void verificarSiExisteYDeLoContrarioGuardarEnDb(AlojamientoDocument nuevo, AtomicLong guardados) {
        ExampleMatcher alojamientoMatcher=ExampleMatcher.matchingAll()
                .withIgnorePaths("id")
                .withIgnorePaths("timestamp");
        boolean existe=repository.exists(Example.of(nuevo,alojamientoMatcher));
        if(!existe) {
            repository.save(nuevo);
            guardados.incrementAndGet();
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
                case AlojamientoTuristico.ApartamentoRural apartamentoRural -> {
                    apartamentosRurales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+apartamentoRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                    apartTuristicos.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+apartTuristico.alojamiento_tipo());
                }
                case AlojamientoTuristico.Camping camping -> {
                    campings.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+camping.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                    casasHuespedes.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+casaHuespedes.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaRural casaRural -> {
                    casasRurales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+casaRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hostal hostal -> {
                    hostales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hostal.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hosteria hosteria -> {
                    hosterias.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hosteria.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hotel hotel -> {
                    hoteles.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hotel.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelApart hotelApart -> {
                    apartHoteles.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hotelApart.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelRural hotelRural -> {
                    hotelesRurales.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+hotelRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Pension pension -> {
                    pensiones.incrementAndGet();
                    LOGGER.log(Level.INFO,"Contado un "+pension.alojamiento_tipo());
                }
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
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
            }
        });
        return alojamientosTuristicos;
    }

    public void borrarTodo() {
        repository.deleteAll();
    }
}
