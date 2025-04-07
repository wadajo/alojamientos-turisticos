package com.wadajo.turismomadrid.domain.service;

import com.wadajo.turismomadrid.application.exception.ResponseTypeDtoException;
import com.wadajo.turismomadrid.application.repository.*;
import com.wadajo.turismomadrid.domain.document.*;
import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import com.wadajo.turismomadrid.infrastructure.configuration.Constants;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.wadajo.turismomadrid.infrastructure.configuration.Constants.RECONOCIDO_UN;
import static org.apache.logging.log4j.Level.DEBUG;

@Service
public class TurismoService {

    private static final Logger LOGGER= LogManager.getLogger();

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

    private final AlojamientosService alojamientosService;

    private final ExampleMatcher alojamientoMatcher=ExampleMatcher.matchingAll()
            .withIgnorePaths("id")
            .withIgnorePaths("timestamp");

    public TurismoService(ApartamentoRuralMongoRepository apartamentoRuralMongoRepository, ApartTuristicoMongoRepository apartTuristicoMongoRepository, CampingMongoRepository campingMongoRepository, CasaHuespedesMongoRepository casaHuespedesMongoRepository, CasaRuralMongoRepository casaRuralMongoRepository, HostalMongoRepository hostalMongoRepository, HosteriaMongoRepository hosteriaMongoRepository, HotelApartMongoRepository hotelApartMongoRepository, HotelMongoRepository hotelMongoRepository, HotelRuralMongoRepository hotelRuralMongoRepository, PensionMongoRepository pensionMongoRepository, ViviendaTuristicaMongoRepository viviendaTuristicaMongoRepository, ConversionService conversionService, AlojamientosService alojamientosService) {
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
        this.alojamientosService = alojamientosService;
    }

    public List<AlojamientoTuristico> getAlojamientosTuristicos() throws ResponseTypeDtoException {
        var listaRaw = alojamientosService.getAlojamientosTotales();
        generarMapaConLaCuenta(listaRaw);
        return listaRaw;
    }

    public String actualizarAlojamientosEnDb(){
        var todosLosAlojamientos = alojamientosService.getAlojamientosTotales();
        AtomicLong cuenta = new AtomicLong();

        List<AlojamientoDocument> apartamentosRurales = new ArrayList<>();
        List<AlojamientoDocument> apartTuristicos = new ArrayList<>();
        List<AlojamientoDocument> campings = new ArrayList<>();
        List<AlojamientoDocument> casasHuespedes = new ArrayList<>();
        List<AlojamientoDocument> casasRurales = new ArrayList<>();
        List<AlojamientoDocument> hostales = new ArrayList<>();
        List<AlojamientoDocument> hosterias = new ArrayList<>();
        List<AlojamientoDocument> hoteles = new ArrayList<>();
        List<AlojamientoDocument> hotelesApart = new ArrayList<>();
        List<AlojamientoDocument> hotelesRurales = new ArrayList<>();
        List<AlojamientoDocument> pensiones = new ArrayList<>();
        List<AlojamientoDocument> viviendasTuristicas = new ArrayList<>();

        for (AlojamientoTuristico unAlojamiento : todosLosAlojamientos) {
            switch (unAlojamiento) {
                case AlojamientoTuristico.ApartamentoRural apartamentoRural -> {
                    var apartamentoRuralDocument=conversionService.convert(apartamentoRural, ApartamentoRuralDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, apartamentoRural.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(apartamentoRuralDocument, apartamentosRurales, cuenta, apartamentoRuralMongoRepository);
                }
                case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                    var apartTuristicoDocument=conversionService.convert(apartTuristico, ApartTuristicoDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, apartTuristico.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(apartTuristicoDocument, apartTuristicos, cuenta, apartTuristicoMongoRepository);
                }
                case AlojamientoTuristico.Camping camping -> {
                    var campingDocument=conversionService.convert(camping, CampingDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, camping.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(campingDocument,campings,cuenta,campingMongoRepository);
                }
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                    var casaHuespedesDocument=conversionService.convert(casaHuespedes, CasaHuespedesDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, casaHuespedes.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(casaHuespedesDocument,casasHuespedes,cuenta,casaHuespedesMongoRepository);
                }
                case AlojamientoTuristico.CasaRural casaRural -> {
                    var casaRuralDocument=conversionService.convert(casaRural, CasaRuralDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, casaRural.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(casaRuralDocument,casasRurales,cuenta,casaRuralMongoRepository);
                }
                case AlojamientoTuristico.Hostal hostal -> {
                    var hostalDocument=conversionService.convert(hostal, HostalDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, hostal.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hostalDocument,hostales,cuenta,hostalMongoRepository);
                }
                case AlojamientoTuristico.Hosteria hosteria -> {
                    var hosteriaDocument=conversionService.convert(hosteria, HosteriaDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, hosteria.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hosteriaDocument,hosterias,cuenta,hosteriaMongoRepository);
                }
                case AlojamientoTuristico.Hotel hotel -> {
                    var hotelDocument=conversionService.convert(hotel, HotelDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, hotel.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hotelDocument,hoteles,cuenta,hotelMongoRepository);
                }
                case AlojamientoTuristico.HotelApart hotelApart -> {
                    var hotelApartDocument=conversionService.convert(hotelApart, HotelApartDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, hotelApart.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hotelApartDocument,hotelesApart,cuenta,hotelApartMongoRepository);
                }
                case AlojamientoTuristico.HotelRural hotelRural -> {
                    var hotelRuralDocument=conversionService.convert(hotelRural, HotelRuralDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, hotelRural.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(hotelRuralDocument,hotelesRurales,cuenta,hotelRuralMongoRepository);
                }
                case AlojamientoTuristico.Pension pension -> {
                    var pensionDocument=conversionService.convert(pension, PensionDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, pension.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(pensionDocument,pensiones,cuenta,pensionMongoRepository);
                }
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
                    var viviendaTuristicaDocument=conversionService.convert(viviendaTuristica, ViviendaTuristicaDocument.class);
                    LOGGER.log(DEBUG, RECONOCIDO_UN, viviendaTuristica.alojamiento_tipo());
                    verificarAlojamientoDocumentEIncrementarCuenta(viviendaTuristicaDocument,viviendasTuristicas,cuenta,viviendaTuristicaMongoRepository);
                }
            }
        }

        apartamentoRuralMongoRepository.saveAll(toApartamentoRuralDocumentList(apartamentosRurales));
        LOGGER.log(Level.INFO, "Guardados en DB {} apartamentos rurales.", apartamentosRurales.size());
        apartTuristicoMongoRepository.saveAll(toApartTuristicoDocumentList(apartTuristicos));
        LOGGER.log(Level.INFO, "Guardados en DB {} apart turísticos.", apartTuristicos.size());
        campingMongoRepository.saveAll(toCampingDocumentList(campings));
        LOGGER.log(Level.INFO, "Guardados en DB {} campings.", campings.size());
        casaHuespedesMongoRepository.saveAll(toCasaHuespedesDocumentList(casasHuespedes));
        LOGGER.log(Level.INFO, "Guardados en DB {} casas huéspedes.", casasHuespedes.size());
        casaRuralMongoRepository.saveAll(toCasaRuralDocumentList(casasRurales));
        LOGGER.log(Level.INFO, "Guardados en DB {} casas rurales.", casasRurales.size());
        hostalMongoRepository.saveAll(toHostalDocumentList(hostales));
        LOGGER.log(Level.INFO, "Guardados en DB {} hostales.", hostales.size());
        hosteriaMongoRepository.saveAll(toHosteriaDocumentList(hosterias));
        LOGGER.log(Level.INFO, "Guardados en DB {} hosterías.", hosterias.size());
        hotelApartMongoRepository.saveAll(toHotelApartDocumentList(hotelesApart));
        LOGGER.log(Level.INFO, "Guardados en DB {} hoteles apart.", hotelesApart.size());
        hotelMongoRepository.saveAll(toHotelDocumentList(hoteles));
        LOGGER.log(Level.INFO, "Guardados en DB {} hoteles.", hoteles.size());
        hotelRuralMongoRepository.saveAll(toHotelRuralDocumentList(hotelesRurales));
        LOGGER.log(Level.INFO, "Guardados en DB {} hoteles rurales.", hotelesRurales.size());
        pensionMongoRepository.saveAll(toPensionDocumentList(pensiones));
        LOGGER.log(Level.INFO, "Guardados en DB {} pensiones.", pensiones.size());
        viviendaTuristicaMongoRepository.saveAll(toViviendaTuristicaDocumentList(viviendasTuristicas));
        LOGGER.log(Level.INFO, "Guardados en DB {} viviendas turísticas.", viviendasTuristicas.size());

        generarMapaConLaCuenta(todosLosAlojamientos);
        return "Han sido actualizados en DB: "+ cuenta+" alojamientos.";
    }

    public void borrarTodo() {
        apartamentoRuralMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion apartamentosrurales");
        apartTuristicoMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion apartamentosturisticos");
        campingMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion campings");
        casaHuespedesMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion casasdehuespedes");
        casaRuralMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion casasrurales");
        hostalMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion hostales");
        hosteriaMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion hosterias");
        hotelApartMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion aparthoteles");
        hotelMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion hoteles");
        hotelRuralMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion hotelesrurales");
        pensionMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion pensiones");
        viviendaTuristicaMongoRepository.deleteAll();
        LOGGER.log(DEBUG,"Borrada coleccion viviendasturisticas");
        LOGGER.log(Level.INFO,"Borradas todas las colecciones");
    }

    public List<AlojamientoTuristico> getAlojamientosByType(TipoAlojamiento tipo) {
        var listaFiltrada = alojamientosService.getAlojamientosTotales().stream()
            .filter(alojamientoTuristico ->
                switch (alojamientoTuristico) {
                    case AlojamientoTuristico.ApartTuristico apartTuristico -> apartTuristico.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.ApartamentoRural apartamentoRural -> apartamentoRural.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.Camping camping -> camping.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.CasaHuespedes casaHuespedes -> casaHuespedes.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.CasaRural casaRural -> casaRural.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.Hostal hostal -> hostal.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.Hosteria hosteria -> hosteria.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.Hotel hotel -> hotel.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.HotelApart hotelApart -> hotelApart.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.HotelRural hotelRural -> hotelRural.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.Pension pension -> pension.alojamiento_tipo().toString().equals(tipo.toString());
                    case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> viviendaTuristica.alojamiento_tipo().toString().equals(tipo.toString());
                })
            .toList();
        generarMapaConLaCuenta(listaFiltrada);
        return listaFiltrada;
    }

    private List<ApartamentoRuralDocument> toApartamentoRuralDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<ApartamentoRuralDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((ApartamentoRuralDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<ApartTuristicoDocument> toApartTuristicoDocumentList(List<AlojamientoDocument> alojamientoDocumentList) {
        List<ApartTuristicoDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((ApartTuristicoDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<CampingDocument> toCampingDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<CampingDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((CampingDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<CasaHuespedesDocument> toCasaHuespedesDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<CasaHuespedesDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((CasaHuespedesDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<CasaRuralDocument> toCasaRuralDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<CasaRuralDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((CasaRuralDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<HostalDocument> toHostalDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HostalDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((HostalDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<HosteriaDocument> toHosteriaDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HosteriaDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((HosteriaDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<HotelApartDocument> toHotelApartDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HotelApartDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((HotelApartDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<HotelDocument> toHotelDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HotelDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((HotelDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<HotelRuralDocument> toHotelRuralDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<HotelRuralDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((HotelRuralDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<PensionDocument> toPensionDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<PensionDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((PensionDocument)alojamientoDocument);
        }
        return lista;
    }
    private List<ViviendaTuristicaDocument> toViviendaTuristicaDocumentList(List<? extends AlojamientoDocument> alojamientoDocumentList) {
        List<ViviendaTuristicaDocument> lista=new ArrayList<>();
        for (AlojamientoDocument alojamientoDocument : alojamientoDocumentList) {
            lista.add((ViviendaTuristicaDocument)alojamientoDocument);
        }
        return lista;
    }

    private <S extends AlojamientoDocument> void verificarAlojamientoDocumentEIncrementarCuenta(S alojamientoDocument,
                                                                List<? super AlojamientoDocument> alojamientoDocumentArrayList,
                                                                AtomicLong cuenta,
                                                                MongoRepository<S, String> repository) {
        if(!repository.exists(Example.of(alojamientoDocument,alojamientoMatcher))) {
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
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, apartamentoRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                    apartTuristicos.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, apartTuristico.alojamiento_tipo());
                }
                case AlojamientoTuristico.Camping camping -> {
                    campings.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, camping.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                    casasHuespedes.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, casaHuespedes.alojamiento_tipo());
                }
                case AlojamientoTuristico.CasaRural casaRural -> {
                    casasRurales.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, casaRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hostal hostal -> {
                    hostales.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, hostal.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hosteria hosteria -> {
                    hosterias.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, hosteria.alojamiento_tipo());
                }
                case AlojamientoTuristico.Hotel hotel -> {
                    hoteles.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, hotel.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelApart hotelApart -> {
                    apartHoteles.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, hotelApart.alojamiento_tipo());
                }
                case AlojamientoTuristico.HotelRural hotelRural -> {
                    hotelesRurales.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, hotelRural.alojamiento_tipo());
                }
                case AlojamientoTuristico.Pension pension -> {
                    pensiones.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, pension.alojamiento_tipo());
                }
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
                    viviendasTuristicas.incrementAndGet();
                    LOGGER.log(Level.DEBUG, Constants.CONTADO_UN, viviendaTuristica.alojamiento_tipo());
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

            String message = String.format("Resultado: Total alojamientos turisticos: %d. %s", listaFinal.size(), mapa);
            LOGGER.log(Level.INFO, message);
    }

}
