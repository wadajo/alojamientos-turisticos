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

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            todosLosAlojamientosEnRemoto.parallelStream()
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

        eliminarAlojamientosTuristicosObsoletos(apartamentoRuralMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(apartTuristicoMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(campingMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(casaHuespedesMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(casaRuralMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(hostalMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(hosteriaMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(hotelApartMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(hotelMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(hotelRuralMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(pensionMongoRepository,todosLosAlojamientosEnRemoto);
        eliminarAlojamientosTuristicosObsoletos(viviendaTuristicaMongoRepository,todosLosAlojamientosEnRemoto);

        generarMapaConLaCuenta(todosLosAlojamientosEnRemoto);
        return "Han sido actualizados en DB: "+ cuenta+" alojamientos.";
    }

    private <S extends AlojamientoDocument> void eliminarAlojamientosTuristicosObsoletos(
                                                      MongoRepository<S, String> repository,
                                                      List<AlojamientoTuristico> alojamientosTuristicosRemoto) {
        List<? extends AlojamientoDocument> alojamientosTuristicosEnBbDd=repository.findAll();
        var alojamientosTuristicosObsoletos=
            alojamientosTuristicosEnBbDd.stream()
                .filter(alojamientoEnBbDd -> alojamientosTuristicosRemoto.stream()
                    .noneMatch(alojamientoRemoto -> sonEquivalentes(alojamientoEnBbDd, alojamientoRemoto)))
                .toList();
        if (alojamientosTuristicosObsoletos.isEmpty()) {
            LOGGER.info("No se han encontrado alojamientos obsoletos del tipo: {}", alojamientosTuristicosEnBbDd.getFirst().alojamiento_tipo);
        } else {
            LOGGER.info("Encontrado alojamiento obsoleto del tipo: {} ",alojamientosTuristicosObsoletos.getFirst().denominacion);
            LOGGER.info("Encontrados {} alojamientos obsoletos del tipo: {} ",alojamientosTuristicosObsoletos.size(),alojamientosTuristicosEnBbDd.getFirst().alojamiento_tipo);
            repository.deleteAll((Iterable<? extends S>) alojamientosTuristicosObsoletos);
        }
    }

    private static <S extends AlojamientoTuristico> boolean sonEquivalentes(AlojamientoDocument alojamientoDocument, S alojamientoTuristicoRemoto) {
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.ApartamentoRural(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                Objects.requireNonNull(alojamientoDocument.numero).equals(numero) &&
                Objects.requireNonNull(alojamientoDocument.portal).equals(portal) &&
                Objects.requireNonNull(alojamientoDocument.bloque).equals(bloque) &&
                Objects.requireNonNull(alojamientoDocument.planta).equals(planta) &&
                Objects.requireNonNull(alojamientoDocument.puerta).equals(puerta) &&
                Objects.requireNonNull(alojamientoDocument.signatura).equals(signatura) &&
                Objects.requireNonNull(alojamientoDocument.categoria).equals(categoria) &&
                Objects.requireNonNull(alojamientoDocument.escalera).equals(escalera) &&
                Objects.requireNonNull(alojamientoDocument.denominacion).equals(denominacion) &&
                Objects.requireNonNull(alojamientoDocument.codpostal).equals(cdpostal) &&
                Objects.requireNonNull(alojamientoDocument.localidad).equals(localidad) &&
                Objects.requireNonNull(alojamientoDocument.alojamiento_tipo).equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.ApartTuristico(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Camping(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.CasaHuespedes(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.CasaRural(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Hostal(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Hosteria(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.HotelApart(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Hotel(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.HotelRural(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.Pension(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        if (alojamientoTuristicoRemoto instanceof AlojamientoTuristico.ViviendaTuristica(
            String viaTipo, String viaNombre, String numero, String portal, String bloque, String planta, String puerta,
            String signatura, String categoria, String escalera, String denominacion, String cdpostal, String localidad,
            TipoAlojamiento alojamientoTipo
        )) {
            return Objects.requireNonNull(alojamientoDocument.via_tipo).equals(viaTipo) &&
                Objects.requireNonNull(alojamientoDocument.via_nombre).equals(viaNombre) &&
                alojamientoDocument.numero.equals(numero) &&
                alojamientoDocument.portal.equals(portal) &&
                alojamientoDocument.bloque.equals(bloque) &&
                alojamientoDocument.planta.equals(planta) &&
                alojamientoDocument.puerta.equals(puerta) &&
                alojamientoDocument.signatura.equals(signatura) &&
                alojamientoDocument.categoria.equals(categoria) &&
                alojamientoDocument.escalera.equals(escalera) &&
                alojamientoDocument.denominacion.equals(denominacion) &&
                alojamientoDocument.codpostal.equals(cdpostal) &&
                alojamientoDocument.localidad.equals(localidad) &&
                alojamientoDocument.alojamiento_tipo.equals(alojamientoTipo.toString());
        }
        return false;
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

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            listaFinal.parallelStream()
                .forEach(unAlojamiento ->
                    executor.submit(() -> {
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
