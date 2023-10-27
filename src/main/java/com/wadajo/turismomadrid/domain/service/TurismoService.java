package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.repository.*;
import com.wadajo.turismomadrid.domain.document.*;
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
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.logging.log4j.Level.DEBUG;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

    @Value("${turismomadrid.endpoint.url}")
    private String ALOJAMIENTOS_URL;

    private final AlojamientosMongoRepository alojamientosMongoRepository;
    private final ApartamentoRuralMongoRepository apartamentoRuralMongoRepository;
    private final ApartTuristicoMongoRepository apartTuristicoMongoRepository;
    private final CampingMongoRepository campingMongoRepository;
    private final CasaHuespedesMongoRepository casaHuespedesMongoRepository;
    private final CasaRuralMongoRepository casaRuralMongoRepository;
    private final HostalMongoRepository hostalMongoRepository;
    private final HosteriaMongoRepository hosteriaMongoRepository;
    private final HotelApartMongoRepository hotelApartMongoRepository;
    private final HotelMongoRepository hotelMongoRepository;
    private final HotelRuralMongoRepository hotelRuralMongoRepository;
    private final PensionMongoRepository pensionMongoRepository;
    private final ViviendaTuristicaMongoRepository viviendaTuristicaMongoRepository;

    private final ConversionService conversionService;

    private final ExampleMatcher alojamientoMatcher=ExampleMatcher.matchingAll()
            .withIgnorePaths("id")
            .withIgnorePaths("timestamp");

    public TurismoService(AlojamientosMongoRepository alojamientosMongoRepository, ApartamentoRuralMongoRepository apartamentoRuralMongoRepository, ApartTuristicoMongoRepository apartTuristicoMongoRepository, CampingMongoRepository campingMongoRepository, CasaHuespedesMongoRepository casaHuespedesMongoRepository, CasaRuralMongoRepository casaRuralMongoRepository, HostalMongoRepository hostalMongoRepository, HosteriaMongoRepository hosteriaMongoRepository, HotelApartMongoRepository hotelApartMongoRepository, HotelMongoRepository hotelMongoRepository, HotelRuralMongoRepository hotelRuralMongoRepository, PensionMongoRepository pensionMongoRepository, ViviendaTuristicaMongoRepository viviendaTuristicaMongoRepository, ConversionService conversionService) {
        this.alojamientosMongoRepository = alojamientosMongoRepository;
        this.apartamentoRuralMongoRepository = apartamentoRuralMongoRepository;
        this.apartTuristicoMongoRepository = apartTuristicoMongoRepository;
        this.campingMongoRepository = campingMongoRepository;
        this.casaHuespedesMongoRepository = casaHuespedesMongoRepository;
        this.casaRuralMongoRepository = casaRuralMongoRepository;
        this.hostalMongoRepository = hostalMongoRepository;
        this.hosteriaMongoRepository = hosteriaMongoRepository;
        this.hotelApartMongoRepository = hotelApartMongoRepository;
        this.hotelMongoRepository = hotelMongoRepository;
        this.hotelRuralMongoRepository = hotelRuralMongoRepository;
        this.pensionMongoRepository = pensionMongoRepository;
        this.viviendaTuristicaMongoRepository = viviendaTuristicaMongoRepository;
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

    public String actualizarAlojamientosEnDb(){
        var todosLosAlojamientos=getAlojamientosTuristicos();
        AtomicLong cuenta=new AtomicLong();

        List<AlojamientoDocument> apartamentosRurales=new ArrayList<>();
        List<AlojamientoDocument> apartTuristicos= new ArrayList<>();
        List<AlojamientoDocument> campings= new ArrayList<>();
        List<AlojamientoDocument> casasHuespedes= new ArrayList<>();
        List<AlojamientoDocument> casasRurales=new ArrayList<>();
        List<AlojamientoDocument> hostales=new ArrayList<>();
        List<AlojamientoDocument> hosterias=new ArrayList<>();
        List<AlojamientoDocument> hoteles=new ArrayList<>();
        List<AlojamientoDocument> hotelesApart=new ArrayList<>();
        List<AlojamientoDocument> hotelesRurales=new ArrayList<>();
        List<AlojamientoDocument> pensiones=new ArrayList<>();
        List<AlojamientoDocument> viviendasTuristicas=new ArrayList<>();

        for (AlojamientoTuristico unAlojamiento : todosLosAlojamientos) {
            switch (unAlojamiento) {
                case AlojamientoTuristico.ApartamentoRural apartamentoRural -> {
                    var apartamentoRuralDocument=conversionService.convert(apartamentoRural, ApartamentoRuralDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + apartamentoRural.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(apartamentoRuralDocument, apartamentosRurales, cuenta, apartamentoRuralMongoRepository);
                }
                case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                    var apartTuristicoDocument=conversionService.convert(apartTuristico, ApartTuristicoDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + apartTuristico.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(apartTuristicoDocument, apartTuristicos, cuenta, apartTuristicoMongoRepository);
                }
                case AlojamientoTuristico.Camping camping -> {
                    var campingDocument=conversionService.convert(camping, CampingDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + camping.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(campingDocument,campings,cuenta,campingMongoRepository);
                }
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                    var casaHuespedesDocument=conversionService.convert(casaHuespedes, CasaHuespedesDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + casaHuespedes.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(casaHuespedesDocument,casasHuespedes,cuenta,casaHuespedesMongoRepository);
                }
                case AlojamientoTuristico.CasaRural casaRural -> {
                    var casaRuralDocument=conversionService.convert(casaRural, CasaRuralDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + casaRural.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(casaRuralDocument,casasRurales,cuenta,casaRuralMongoRepository);
                }
                case AlojamientoTuristico.Hostal hostal -> {
                    var hostalDocument=conversionService.convert(hostal, HostalDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + hostal.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hostalDocument,hostales,cuenta,hostalMongoRepository);
                }
                case AlojamientoTuristico.Hosteria hosteria -> {
                    var hosteriaDocument=conversionService.convert(hosteria, HosteriaDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + hosteria.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hosteriaDocument,hosterias,cuenta,hosteriaMongoRepository);
                }
                case AlojamientoTuristico.Hotel hotel -> {
                    var hotelDocument=conversionService.convert(hotel, HotelDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + hotel.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hotelDocument,hoteles,cuenta,hotelMongoRepository);
                }
                case AlojamientoTuristico.HotelApart hotelApart -> {
                    var hotelApartDocument=conversionService.convert(hotelApart, HotelApartDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + hotelApart.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hotelApartDocument,hotelesApart,cuenta,hotelApartMongoRepository);
                }
                case AlojamientoTuristico.HotelRural hotelRural -> {
                    var hotelRuralDocument=conversionService.convert(hotelRural, HotelRuralDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + hotelRural.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hotelRuralDocument,hotelesRurales,cuenta,hotelRuralMongoRepository);
                }
                case AlojamientoTuristico.Pension pension -> {
                    var pensionDocument=conversionService.convert(pension, PensionDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + pension.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(pensionDocument,pensiones,cuenta,pensionMongoRepository);
                }
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
                    var viviendaTuristicaDocument=conversionService.convert(viviendaTuristica, ViviendaTuristicaDocument.class);
                    LOGGER.log(DEBUG, "Reconocido un  " + viviendaTuristica.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(viviendaTuristicaDocument,viviendasTuristicas,cuenta,viviendaTuristicaMongoRepository);
                }
            }
        }

        apartamentoRuralMongoRepository.saveAll(toApartamentoRuralDocumentList(apartamentosRurales));
        LOGGER.log(Level.INFO, "Guardados en DB  " + apartamentosRurales.size()+" apartamentos rurales.");
        apartTuristicoMongoRepository.saveAll(toApartTuristicoDocumentList(apartTuristicos));
        LOGGER.log(Level.INFO, "Guardados en DB  " + apartTuristicos.size()+" apart turísticos.");
        campingMongoRepository.saveAll(toCampingDocumentList(campings));
        LOGGER.log(Level.INFO, "Guardados en DB  " + campings.size()+" campings.");
        casaHuespedesMongoRepository.saveAll(toCasaHuespedesDocumentList(casasHuespedes));
        LOGGER.log(Level.INFO, "Guardados en DB  " + casasHuespedes.size()+" casas huéspedes.");
        casaRuralMongoRepository.saveAll(toCasaRuralDocumentList(casasRurales));
        LOGGER.log(Level.INFO, "Guardados en DB  " + casasRurales.size()+" casas rurales.");
        hostalMongoRepository.saveAll(toHostalDocumentList(hostales));
        LOGGER.log(Level.INFO, "Guardados en DB  " + hostales.size()+" hostales.");
        hosteriaMongoRepository.saveAll(toHosteriaDocumentList(hosterias));
        LOGGER.log(Level.INFO, "Guardados en DB  " + hosterias.size()+" hosterías.");
        hotelApartMongoRepository.saveAll(toHotelApartDocumentList(hotelesApart));
        LOGGER.log(Level.INFO, "Guardados en DB  " + hotelesApart.size()+" hoteles apart.");
        hotelMongoRepository.saveAll(toHotelDocumentList(hoteles));
        LOGGER.log(Level.INFO, "Guardados en DB  " + hoteles.size()+" hoteles.");
        hotelRuralMongoRepository.saveAll(toHotelRuralDocumentList(hotelesRurales));
        LOGGER.log(Level.INFO, "Guardados en DB  " + hotelesRurales.size()+" hoteles rurales.");
        pensionMongoRepository.saveAll(toPensionDocumentList(pensiones));
        LOGGER.log(Level.INFO, "Guardados en DB  " + pensiones.size()+" pensiones.");
        viviendaTuristicaMongoRepository.saveAll(toViviendaTuristicaDocumentList(viviendasTuristicas));
        LOGGER.log(Level.INFO, "Guardados en DB  " + viviendasTuristicas.size()+" viviendas turísticas.");

        return "Han sido actualizados en DB: "+ cuenta+" alojamientos.";
    }

    private List<ApartamentoRuralDocument> toApartamentoRuralDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<ApartamentoRuralDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof ApartamentoRuralDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<ApartTuristicoDocument> toApartTuristicoDocumentList(List<AlojamientoDocument> alojamientoDocumentList) {
        List<ApartTuristicoDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof ApartTuristicoDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<CampingDocument> toCampingDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<CampingDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof CampingDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<CasaHuespedesDocument> toCasaHuespedesDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<CasaHuespedesDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof CasaHuespedesDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<CasaRuralDocument> toCasaRuralDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<CasaRuralDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof CasaRuralDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<HostalDocument> toHostalDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HostalDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof HostalDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<HosteriaDocument> toHosteriaDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HosteriaDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof HosteriaDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<HotelApartDocument> toHotelApartDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HotelApartDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof HotelApartDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<HotelDocument> toHotelDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HotelDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof HotelDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<HotelRuralDocument> toHotelRuralDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HotelRuralDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof HotelRuralDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<PensionDocument> toPensionDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<PensionDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof PensionDocument document)
                lista.add(document);
        }
        return lista;
    }
    private List<ViviendaTuristicaDocument> toViviendaTuristicaDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<ViviendaTuristicaDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            if (alojamientoDocument instanceof ViviendaTuristicaDocument document)
                lista.add(document);
        }
        return lista;
    }

    private void verificarAlojamientoDocumentEIncrementarCuenta(AlojamientoDocument alojamientoDocument, List<? super AlojamientoDocument> alojamientoDocumentArrayList, AtomicLong cuenta, MongoRepository repository) {
        boolean existe= repository.exists(Example.of(alojamientoDocument,alojamientoMatcher));
        if(!existe) {
            alojamientoDocumentArrayList.addLast(alojamientoDocument);
            cuenta.incrementAndGet();
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
                    LOGGER.log(Level.DEBUG,"Contado un "+apartamentoRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                    apartTuristicos.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+apartTuristico.alojamiento_tipo());
                }
                case AlojamientoTuristico.Camping camping -> {
                    campings.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+camping.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                    casasHuespedes.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+casaHuespedes.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaRural casaRural -> {
                    casasRurales.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+casaRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hostal hostal -> {
                    hostales.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+hostal.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hosteria hosteria -> {
                    hosterias.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+hosteria.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hotel hotel -> {
                    hoteles.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+hotel.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelApart hotelApart -> {
                    apartHoteles.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+hotelApart.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelRural hotelRural -> {
                    hotelesRurales.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+hotelRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Pension pension -> {
                    pensiones.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+pension.alojamiento_tipo());
                }
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
                    viviendasTuristicas.incrementAndGet();
                    LOGGER.log(Level.DEBUG,"Contado un "+viviendaTuristica.alojamiento_tipo());
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
        alojamientosMongoRepository.deleteAll();
        apartamentoRuralMongoRepository.deleteAll();
        apartTuristicoMongoRepository.deleteAll();
        campingMongoRepository.deleteAll();
        casaHuespedesMongoRepository.deleteAll();
        casaRuralMongoRepository.deleteAll();
        casaRuralMongoRepository.deleteAll();
        hostalMongoRepository.deleteAll();
        hosteriaMongoRepository.deleteAll();
        hotelApartMongoRepository.deleteAll();
        hotelMongoRepository.deleteAll();
        hotelRuralMongoRepository.deleteAll();
        pensionMongoRepository.deleteAll();
        viviendaTuristicaMongoRepository.deleteAll();
        LOGGER.log(Level.INFO,"Borradas todas las colecciones");
    }
}
