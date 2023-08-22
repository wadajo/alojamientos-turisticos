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
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Value("${turismomadrid.endpoint.url}")
    private String ALOJAMIENTOS_URL;

    private final AlojamientosMongoRepository repository;

    private final ConversionService conversionService;

    public TurismoService(AlojamientosMongoRepository repository, ConversionService conversionService) {
        this.repository = repository;
        this.conversionService = conversionService;
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
        AtomicLong cuentaDeGuardados=new AtomicLong();
        var alojamientosAGuardar=new ArrayList<AlojamientoDocument>();

        for (AlojamientoTuristico unAlojamiento : todosLosAlojamientos) {
            AlojamientoDocument nuevo=conversionService.convert(unAlojamiento, AlojamientoDocument.class);
            switch (unAlojamiento) {
                case AlojamientoTuristico.ApartamentoRural apartamentoRural -> LOGGER.log(Level.INFO, "Reconocido un  " + apartamentoRural.alojamiento_tipo());
                case AlojamientoTuristico.ApartTuristico apartTuristico -> LOGGER.log(Level.INFO, "Reconocido un  " + apartTuristico.alojamiento_tipo());
                case AlojamientoTuristico.Camping camping -> LOGGER.log(Level.INFO, "Reconocido un  " + camping.alojamiento_tipo());
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> LOGGER.log(Level.INFO, "Reconocido un  " + casaHuespedes.alojamiento_tipo());
                case AlojamientoTuristico.CasaRural casaRural -> LOGGER.log(Level.INFO, "Reconocido un  " + casaRural.alojamiento_tipo());
                case AlojamientoTuristico.Hostal hostal -> LOGGER.log(Level.INFO, "Reconocido un  " + hostal.alojamiento_tipo());
                case AlojamientoTuristico.Hosteria hosteria -> LOGGER.log(Level.INFO, "Reconocido un  " + hosteria.alojamiento_tipo());
                case AlojamientoTuristico.Hotel hotel -> LOGGER.log(Level.INFO, "Reconocido un  " + hotel.alojamiento_tipo());
                case AlojamientoTuristico.HotelApart hotelApart -> LOGGER.log(Level.INFO, "Reconocido un  " + hotelApart.alojamiento_tipo());
                case AlojamientoTuristico.HotelRural hotelRural -> LOGGER.log(Level.INFO, "Reconocido un  " + hotelRural.alojamiento_tipo());
                case AlojamientoTuristico.Pension pension -> LOGGER.log(Level.INFO, "Reconocido un  " + pension.alojamiento_tipo());
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> LOGGER.log(Level.INFO, "Reconocido un  " + viviendaTuristica.alojamiento_tipo());
            }
            verificarSiExisteYDeLoContrarioSumarParaGuardarEnDb(nuevo, cuentaDeGuardados, alojamientosAGuardar);
        }
        repository.saveAll(alojamientosAGuardar);
        LOGGER.log(Level.INFO, "Guardados: "+cuentaDeGuardados);
        return "Guardados en DB: "+ cuentaDeGuardados;
    }

    private void verificarSiExisteYDeLoContrarioSumarParaGuardarEnDb(AlojamientoDocument nuevo, AtomicLong cuentaDeGuardados, ArrayList<AlojamientoDocument> alojamientosAGuardar) {
        ExampleMatcher alojamientoMatcher=ExampleMatcher.matchingAll()
                .withIgnorePaths("id")
                .withIgnorePaths("timestamp");
        boolean existe=repository.exists(Example.of(nuevo,alojamientoMatcher));
        if(!existe) {
            alojamientosAGuardar.add(nuevo);
            cuentaDeGuardados.incrementAndGet();
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
