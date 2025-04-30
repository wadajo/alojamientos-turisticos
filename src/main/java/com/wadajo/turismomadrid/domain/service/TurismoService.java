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
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public List<AlojamientoTuristico> getAlojamientosTuristicosEnRemoto() throws ResponseTypeDtoException {
        var listaRaw = alojamientosService.getAlojamientosTotales();
        generarMapaConLaCuenta(listaRaw);
        return listaRaw;
    }

    public String guardarTodosLosAlojamientosRemotosEnDb(){
        List<AlojamientoTuristico> todosLosAlojamientosEnRemoto = alojamientosService.getAlojamientosTotales();
        AtomicLong cuenta = new AtomicLong();

        List<AlojamientoDocument> apartamentosRuralesDocumentList = new ArrayList<>();
        List<AlojamientoDocument> apartTuristicosDocumentList = new ArrayList<>();
        List<AlojamientoDocument> campingsDocumentList = new ArrayList<>();
        List<AlojamientoDocument> casasHuespedesDocumentList = new ArrayList<>();
        List<AlojamientoDocument> casasRuralesDocumentList = new ArrayList<>();
        List<AlojamientoDocument> hostalesDocumentList = new ArrayList<>();
        List<AlojamientoDocument> hosteriasDocumentList = new ArrayList<>();
        List<AlojamientoDocument> hotelesDocumentList = new ArrayList<>();
        List<AlojamientoDocument> hotelesApartDocumentList = new ArrayList<>();
        List<AlojamientoDocument> hotelesRuralesDocumentList = new ArrayList<>();
        List<AlojamientoDocument> pensionesDocumentList = new ArrayList<>();
        List<AlojamientoDocument> viviendasTuristicasDocumentList = new ArrayList<>();

        try (var executor = Executors.newSingleThreadExecutor()) {
            todosLosAlojamientosEnRemoto
                .forEach(alojamientoTuristicoEnRemoto ->
                executor.submit(() -> {
                    switch (alojamientoTuristicoEnRemoto) {
                        case AlojamientoTuristico.ApartamentoRural apartamentoRuralRemoto -> {
                            var apartamentoRuralDocument=conversionService.convert(apartamentoRuralRemoto, ApartamentoRuralDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, apartamentoRuralRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(apartamentoRuralDocument, apartamentosRuralesDocumentList, cuenta, apartamentoRuralMongoRepository);
                        }
                        case AlojamientoTuristico.ApartTuristico apartTuristicoRemoto -> {
                            var apartTuristicoDocument=conversionService.convert(apartTuristicoRemoto, ApartTuristicoDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, apartTuristicoRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(apartTuristicoDocument, apartTuristicosDocumentList, cuenta, apartTuristicoMongoRepository);
                        }
                        case AlojamientoTuristico.Camping campingRemoto -> {
                            var campingDocument=conversionService.convert(campingRemoto, CampingDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, campingRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(campingDocument,campingsDocumentList,cuenta,campingMongoRepository);
                        }
                        case AlojamientoTuristico.CasaHuespedes casaHuespedesRemoto -> {
                            var casaHuespedesDocument=conversionService.convert(casaHuespedesRemoto, CasaHuespedesDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, casaHuespedesRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(casaHuespedesDocument,casasHuespedesDocumentList,cuenta,casaHuespedesMongoRepository);
                        }
                        case AlojamientoTuristico.CasaRural casaRuralRemoto -> {
                            var casaRuralDocument=conversionService.convert(casaRuralRemoto, CasaRuralDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, casaRuralRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(casaRuralDocument,casasRuralesDocumentList,cuenta,casaRuralMongoRepository);
                        }
                        case AlojamientoTuristico.Hostal hostalRemoto -> {
                            var hostalDocument=conversionService.convert(hostalRemoto, HostalDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, hostalRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(hostalDocument,hostalesDocumentList,cuenta,hostalMongoRepository);
                        }
                        case AlojamientoTuristico.Hosteria hosteriaRemoto -> {
                            var hosteriaDocument=conversionService.convert(hosteriaRemoto, HosteriaDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, hosteriaRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(hosteriaDocument,hosteriasDocumentList,cuenta,hosteriaMongoRepository);
                        }
                        case AlojamientoTuristico.Hotel hotelRemoto -> {
                            var hotelDocument=conversionService.convert(hotelRemoto, HotelDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, hotelRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(hotelDocument,hotelesDocumentList,cuenta,hotelMongoRepository);
                        }
                        case AlojamientoTuristico.HotelApart hotelApartRemoto -> {
                            var hotelApartDocument=conversionService.convert(hotelApartRemoto, HotelApartDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, hotelApartRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(hotelApartDocument,hotelesApartDocumentList,cuenta,hotelApartMongoRepository);
                        }
                        case AlojamientoTuristico.HotelRural hotelRuralRemoto -> {
                            var hotelRuralDocument=conversionService.convert(hotelRuralRemoto, HotelRuralDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, hotelRuralRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(hotelRuralDocument,hotelesRuralesDocumentList,cuenta,hotelRuralMongoRepository);
                        }
                        case AlojamientoTuristico.Pension pensionRemoto -> {
                            var pensionDocument=conversionService.convert(pensionRemoto, PensionDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, pensionRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(pensionDocument,pensionesDocumentList,cuenta,pensionMongoRepository);
                        }
                        case AlojamientoTuristico.ViviendaTuristica viviendaTuristicaRemoto -> {
                            var viviendaTuristicaDocument=conversionService.convert(viviendaTuristicaRemoto, ViviendaTuristicaDocument.class);
                            LOGGER.log(DEBUG, RECONOCIDO_UN, viviendaTuristicaRemoto.alojamiento_tipo());
                            verificarAlojamientoDocumentEIncrementarCuenta(viviendaTuristicaDocument,viviendasTuristicasDocumentList,cuenta,viviendaTuristicaMongoRepository);
                        }
                    }
                }));
        } catch (Exception e) {
            LOGGER.error("Error al procesar alojamientos turísticos", e);
        }

        var apartamentoRuralResult=apartamentoRuralMongoRepository.saveAll(toApartamentoRuralDocumentList(apartamentosRuralesDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} apartamentos rurales.", apartamentoRuralResult.size());
        var apartTuristicoResult=apartTuristicoMongoRepository.saveAll(toApartTuristicoDocumentList(apartTuristicosDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} apart turísticos.", apartTuristicoResult.size());
        var campingResult=campingMongoRepository.saveAll(toCampingDocumentList(campingsDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} campings.", campingResult.size());
        var casaHuespedesResult=casaHuespedesMongoRepository.saveAll(toCasaHuespedesDocumentList(casasHuespedesDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} casas huéspedes.", casaHuespedesResult.size());
        var casaRuralResult=casaRuralMongoRepository.saveAll(toCasaRuralDocumentList(casasRuralesDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} casas rurales.", casaRuralResult.size());
        var hostalResult=hostalMongoRepository.saveAll(toHostalDocumentList(hostalesDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} hostales.", hostalResult.size());
        var hosteriaResult=hosteriaMongoRepository.saveAll(toHosteriaDocumentList(hosteriasDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} hosterías.", hosteriaResult.size());
        var hotelApartResult=hotelApartMongoRepository.saveAll(toHotelApartDocumentList(hotelesApartDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} hoteles apart.", hotelApartResult.size());
        var hotelResult=hotelMongoRepository.saveAll(toHotelDocumentList(hotelesDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} hoteles.", hotelResult.size());
        var hotelRuralResult=hotelRuralMongoRepository.saveAll(toHotelRuralDocumentList(hotelesRuralesDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} hoteles rurales.", hotelRuralResult.size());
        var pensionResult=pensionMongoRepository.saveAll(toPensionDocumentList(pensionesDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} pensiones.", pensionResult.size());
        var viviendaTuristicaResult=viviendaTuristicaMongoRepository.saveAll(toViviendaTuristicaDocumentList(viviendasTuristicasDocumentList));
        LOGGER.log(Level.INFO, "Guardados en DB {} viviendas turísticas.", viviendaTuristicaResult.size());

        generarMapaConLaCuenta(todosLosAlojamientosEnRemoto);
        return "Han sido guardados en DB: "+ cuenta+" alojamientos.";
    }

    public String eliminarTodosLosAlojamientosObsoletosDeBbDd() {
        var todosLosAlojamientosEnRemoto=new ArrayList<>(alojamientosService.getAlojamientosTotales());
        var cuentaTotalAlojamientosEnDb = getCuentaTotalAlojamientosEnDb();
        if (cuentaTotalAlojamientosEnDb.get()>todosLosAlojamientosEnRemoto.size()) {
            LOGGER.info("Se han encontrado alojamientos obsoletos en DB.");
            eliminarAlojamientosTuristicosObsoletos(apartamentoRuralMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.ApartamentoRural).toList());
            eliminarAlojamientosTuristicosObsoletos(apartTuristicoMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.ApartTuristico).toList());
            eliminarAlojamientosTuristicosObsoletos(campingMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.Camping).toList());
            eliminarAlojamientosTuristicosObsoletos(casaHuespedesMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.CasaHuespedes).toList());
            eliminarAlojamientosTuristicosObsoletos(casaRuralMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.CasaRural).toList());
            eliminarAlojamientosTuristicosObsoletos(hostalMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.Hostal).toList());
            eliminarAlojamientosTuristicosObsoletos(hosteriaMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.Hosteria).toList());
            eliminarAlojamientosTuristicosObsoletos(hotelApartMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.HotelApart).toList());
            eliminarAlojamientosTuristicosObsoletos(hotelMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.Hotel).toList());
            eliminarAlojamientosTuristicosObsoletos(hotelRuralMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.HotelRural).toList());
            eliminarAlojamientosTuristicosObsoletos(pensionMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.Pension).toList());
            eliminarAlojamientosTuristicosObsoletos(viviendaTuristicaMongoRepository, todosLosAlojamientosEnRemoto.stream().filter(alojamientoTuristico -> alojamientoTuristico instanceof AlojamientoTuristico.ViviendaTuristica).toList());
            return "Han sido eliminados alojamientos.";
        } else {
            LOGGER.info("No se han encontrado alojamientos obsoletos en DB.");
            return "No han sido eliminados alojamientos obsoletos.";
        }
    }

    private AtomicLong getCuentaTotalAlojamientosEnDb() {
        var cuentaTotalAlojamientosEnDb=new AtomicLong();
        List<MongoRepository<? extends AlojamientoDocument, String>> repositories = List.of(
            apartamentoRuralMongoRepository,
            apartTuristicoMongoRepository,
            campingMongoRepository,
            casaHuespedesMongoRepository,
            casaRuralMongoRepository,
            hostalMongoRepository,
            hosteriaMongoRepository,
            hotelApartMongoRepository,
            hotelMongoRepository,
            hotelRuralMongoRepository,
            pensionMongoRepository,
            viviendaTuristicaMongoRepository
        );
        repositories.forEach(repo -> cuentaTotalAlojamientosEnDb.addAndGet(repo.count()));
        LOGGER.info("Total alojamientos en DB: {}", cuentaTotalAlojamientosEnDb.get());
        return cuentaTotalAlojamientosEnDb;
    }

    private <S extends AlojamientoDocument> void eliminarAlojamientosTuristicosObsoletos(
                                                      MongoRepository<S, String> repository,
                                                      List<AlojamientoTuristico> alojamientosTuristicosRemotoDeEsteTipo) {
        List<? extends AlojamientoDocument> alojamientosTuristicosEnBbDd=repository.findAll();
        List<AlojamientoDocument> alojamientosTuristicosObsoletos = new ArrayList<>();
        for (AlojamientoDocument alojamientoEnBbDd : alojamientosTuristicosEnBbDd) {
            if (noEstaEnLaListaRemota(alojamientoEnBbDd, alojamientosTuristicosRemotoDeEsteTipo)) {
                alojamientosTuristicosObsoletos.add(alojamientoEnBbDd);
            }
        }
        if (alojamientosTuristicosObsoletos.isEmpty()) {
            String input=alojamientosTuristicosEnBbDd.getFirst().getClass().toString();
            Pattern pattern = Pattern.compile("\\.(\\w+?)Document");
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) {
                String result = matcher.group(1);
                LOGGER.info("No se han encontrado alojamientos obsoletos del tipo: {}", result);
            } else {
                LOGGER.info("No se han encontrado alojamientos obsoletos.");
            }
        } else {
            LOGGER.info("Encontrado alojamiento obsoleto del tipo: {} ", alojamientosTuristicosObsoletos.getFirst().denominacion);
            LOGGER.info("Encontrados {} alojamientos obsoletos del tipo: {} ", alojamientosTuristicosObsoletos.size(),alojamientosTuristicosEnBbDd.getFirst().alojamiento_tipo);
            repository.deleteAll((Iterable<? extends S>) alojamientosTuristicosObsoletos);
        }
    }

    private boolean noEstaEnLaListaRemota(AlojamientoDocument alojamientoEnBbDd, List<AlojamientoTuristico> alojamientosTuristicosRemotoDeEsteTipo) {
        return alojamientosTuristicosRemotoDeEsteTipo.stream()
            .noneMatch(alojamientoTuristicoRemoto -> sonEquivalentes(alojamientoEnBbDd, alojamientoTuristicoRemoto));
    }

    private static <S extends AlojamientoTuristico> boolean sonEquivalentes(AlojamientoDocument alojamientoDocument, S alojamientoTuristicoRemoto) {
        switch (alojamientoDocument) {
            case ApartamentoRuralDocument apartamentoRuralDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.ApartamentoRural(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            ) -> {
                return esEquivalenteEsteDocumentAlRemoto(apartamentoRuralDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case ApartTuristicoDocument apartTuristicoDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.ApartTuristico(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(apartTuristicoDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case CampingDocument campingDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Camping(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(campingDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case CasaHuespedesDocument casaHuespedesDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.CasaHuespedes(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(casaHuespedesDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case CasaRuralDocument casaRuralDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.CasaRural(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(casaRuralDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case HostalDocument hostalDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Hostal(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(hostalDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case HosteriaDocument hosteriaDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Hosteria(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(hosteriaDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case HotelDocument hotelDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Hotel(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(hotelDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case HotelApartDocument hotelApartDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.HotelApart(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(hotelApartDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case HotelRuralDocument hotelRuralDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.HotelRural(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(hotelRuralDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case PensionDocument pensionDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Pension(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(pensionDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            case ViviendaTuristicaDocument viviendaTuristicaDocument when alojamientoTuristicoRemoto instanceof AlojamientoTuristico.ViviendaTuristica(
                String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta,
                String puerta, String signatura, String categoria, String escalera, String denominacion,
                String cdpostal, String localidad, TipoAlojamiento alojamientoTipo
            )  -> {
                return esEquivalenteEsteDocumentAlRemoto(viviendaTuristicaDocument, viaTipo, viaNombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, cdpostal, localidad, alojamientoTipo);
            }
            default -> {
                LOGGER.debug("Valores innecesarios para Document: {} y remoto: {}",alojamientoDocument,alojamientoTuristicoRemoto);
                return false;
            }
        }
    }

    private static boolean esEquivalenteEsteDocumentAlRemoto(AlojamientoDocument alojamientoEnBbDd, String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta, String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad, TipoAlojamiento alojamientoTipo) {
        return Objects.equals(alojamientoEnBbDd.via_tipo, viaTipo) &&
            Objects.equals(alojamientoEnBbDd.via_nombre, viaNombre) &&
            Objects.equals(alojamientoEnBbDd.numero, numero) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.portal,""), portal) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.bloque,""), bloque) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.planta,""), planta) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.puerta,""), puerta) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.signatura,""), signatura) &&
            Objects.equals(alojamientoEnBbDd.categoria, categoria) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.escalera,""), escalera) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.denominacion,""), denominacion) &&
            Objects.equals(Objects.requireNonNullElse(alojamientoEnBbDd.codpostal,""), cdpostal) &&
            Objects.equals(alojamientoEnBbDd.localidad, localidad) &&
            // TODO check para apart-hotel y vivienda turistica
            Objects.equals(alojamientoEnBbDd.alojamiento_tipo, alojamientoTipo.printValue);
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
                                                                List<? super AlojamientoDocument> alojamientoDocumentList,
                                                                AtomicLong cuenta,
                                                                MongoRepository<S, String> repository) {
        if(!repository.exists(Example.of(alojamientoDocument,alojamientoMatcher))) {
            alojamientoDocumentList.addLast(alojamientoDocument);
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

        try (var executor = Executors.newSingleThreadExecutor()) {
            listaFinal
                .forEach(unAlojamiento ->
                    executor.submit(() -> {
                        switch (unAlojamiento){
                            case AlojamientoTuristico.ApartamentoRural apartamentoRural -> {
                                apartamentosRurales.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, apartamentoRural.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                                apartTuristicos.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, apartTuristico.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.Camping camping -> {
                                campings.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, camping.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                                casasHuespedes.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, casaHuespedes.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.CasaRural casaRural -> {
                                casasRurales.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, casaRural.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.Hostal hostal -> {
                                hostales.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, hostal.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.Hosteria hosteria -> {
                                hosterias.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, hosteria.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.Hotel hotel -> {
                                hoteles.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, hotel.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.HotelApart hotelApart -> {
                                apartHoteles.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, hotelApart.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.HotelRural hotelRural -> {
                                hotelesRurales.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, hotelRural.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.Pension pension -> {
                                pensiones.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, pension.alojamiento_tipo());
                            }
                            case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
                                viviendasTuristicas.incrementAndGet();
                                LOGGER.log(DEBUG, Constants.CONTADO_UN, viviendaTuristica.alojamiento_tipo());
                            }
                        }
                    }
                ));
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
        } catch (Exception e) {
            LOGGER.error("Error al generar mapa con la cuenta. ", e);
        } finally {
            String message = String.format("Resultado: Total alojamientos turisticos: %d. %s", listaFinal.size(), mapa);
            LOGGER.log(Level.INFO, message);
        }
    }

}
