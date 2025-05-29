package com.wadajo.turismomadrid.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wadajo.turismomadrid.application.repository.*;
import com.wadajo.turismomadrid.domain.document.*;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.wadajo.turismomadrid.infrastructure.util.Utils.convertFromRaw;
import static com.wadajo.turismomadrid.util.TestConstants.ALOJAMIENTOS_RAW_UPDATED_STUBBING_FILE;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testmongodb")
@DataMongoTest
@ExtendWith(SpringExtension.class)
class TurismoDbTest {

    @Autowired
    private ApartamentoRuralMongoRepository apartamentoRuralMongoRepository;
    @Autowired
    private ApartTuristicoMongoRepository apartTuristicoMongoRepository;
    @Autowired
    private CampingMongoRepository campingMongoRepository;
    @Autowired
    private CasaHuespedesMongoRepository casaHuespedesMongoRepository;
    @Autowired
    private CasaRuralMongoRepository casaRuralMongoRepository;
    @Autowired
    private HostalMongoRepository hostalMongoRepository;
    @Autowired
    private HosteriaMongoRepository hosteriaMongoRepository;
    @Autowired
    private HotelMongoRepository hotelMongoRepository;
    @Autowired
    private HotelApartMongoRepository hotelApartMongoRepository;
    @Autowired
    private HotelRuralMongoRepository hotelRuralMongoRepository;
    @Autowired
    private PensionMongoRepository pensionMongoRepository;
    @Autowired
    private ViviendaTuristicaMongoRepository viviendaTuristicaMongoRepository;

    @BeforeEach
    void setUp() {
        apartamentoRuralMongoRepository.deleteAll();
        apartTuristicoMongoRepository.deleteAll();
        campingMongoRepository.deleteAll();
        casaHuespedesMongoRepository.deleteAll();
        casaRuralMongoRepository.deleteAll();
        hostalMongoRepository.deleteAll();
        hosteriaMongoRepository.deleteAll();
        hotelMongoRepository.deleteAll();
        hotelApartMongoRepository.deleteAll();
        hotelRuralMongoRepository.deleteAll();
        pensionMongoRepository.deleteAll();
        viviendaTuristicaMongoRepository.deleteAll();
    }
    @Test
    void funcionaElMongoTemplate(@Autowired final MongoTemplate mongoTemplate) {
        Assertions.assertNotNull(mongoTemplate.getDb());
        ArrayList<String> collectionNames = mongoTemplate.getDb()
            .listCollectionNames()
            .into(new ArrayList<>());
        assertThat(collectionNames).isEmpty();
    }

    @Test
    void funcionaLaBbDdEmbebida() {
        // Aquí puedes agregar pruebas para verificar la funcionalidad de tu base de datos MongoDB
        // Por ejemplo, puedes guardar un hotel y luego verificar que se haya guardado correctamente.
        var hotel1=new HotelDocument();
        hotel1.alojamiento_tipo="HOTEL";
        hotel1.via_tipo="CALLE";
        hotel1.via_nombre="del Buen Suceso";
        hotel1.numero="2";
        hotel1.localidad="Madrid";
        hotel1.puerta="";
        hotel1.escalera="";
        hotel1.bloque="";
        hotel1.portal="";
        hotel1.codpostal="28012";

        hotelMongoRepository.save(hotel1);
        assertThat(hotelMongoRepository.findAll()).hasSize(1);
    }

    @Test
    void debeGuardarAlojamientosEnBbDd() throws IOException {
        var alojamientosRawUpdated = new ObjectMapper().readValue(new File(ALOJAMIENTOS_RAW_UPDATED_STUBBING_FILE), AlojamientosTuristicosResponseDto.class);
        var listaRaw = alojamientosRawUpdated.data();
        var lista=convertFromRaw(listaRaw);

        lista.forEach(alojamientoTuristico -> {
            switch (alojamientoTuristico) {
                case AlojamientoTuristico.ApartamentoRural apartamentoRural -> {
                    var apartamentoRuralDocument = new ApartamentoRuralDocument();
                    apartamentoRuralDocument.denominacion= apartamentoRural.denominacion();
                    apartamentoRuralMongoRepository.save(apartamentoRuralDocument);
                }
                case AlojamientoTuristico.ApartTuristico apartTuristico -> {
                    var apartTuristicoDocument = new ApartTuristicoDocument();
                    apartTuristicoDocument.denominacion= apartTuristico.denominacion();
                    apartTuristicoMongoRepository.save(apartTuristicoDocument);
                }
                case AlojamientoTuristico.Camping camping -> {
                    var campingDocument = new CampingDocument();
                    campingDocument.denominacion= camping.denominacion();
                    campingMongoRepository.save(campingDocument);
                }
                case AlojamientoTuristico.CasaHuespedes casaHuespedes -> {
                    var casaHuespedesDocument = new CasaHuespedesDocument();
                    casaHuespedesDocument.denominacion= casaHuespedes.denominacion();
                    casaHuespedesMongoRepository.save(casaHuespedesDocument);
                }
                case AlojamientoTuristico.CasaRural casaRural -> {
                    var casaRuralDocument = new CasaRuralDocument();
                    casaRuralDocument.denominacion= casaRural.denominacion();
                    casaRuralMongoRepository.save(casaRuralDocument);
                }
                case AlojamientoTuristico.Hostal hostal -> {
                    var hostalDocument = new HostalDocument();
                    hostalDocument.denominacion= hostal.denominacion();
                    hostalMongoRepository.save(hostalDocument);
                }
                case AlojamientoTuristico.Hosteria hosteria -> {
                    var hosteriaDocument = new HosteriaDocument();
                    hosteriaDocument.denominacion= hosteria.denominacion();
                    hosteriaMongoRepository.save(hosteriaDocument);
                }
                case AlojamientoTuristico.Hotel hotel -> {
                    var hotelDocument = new HotelDocument();
                    hotelDocument.denominacion= hotel.denominacion();
                    hotelMongoRepository.save(hotelDocument);
                }
                case AlojamientoTuristico.HotelApart hotelApart -> {
                    var hotelApartDocument = new HotelApartDocument();
                    hotelApartDocument.denominacion= hotelApart.denominacion();
                    hotelApartMongoRepository.save(hotelApartDocument);
                }
                case AlojamientoTuristico.HotelRural hotelRural -> {
                    var hotelRuralDocument = new HotelRuralDocument();
                    hotelRuralDocument.denominacion= hotelRural.denominacion();
                    hotelRuralMongoRepository.save(hotelRuralDocument);
                }
                case AlojamientoTuristico.Pension pension -> {
                    var pensionDocument = new PensionDocument();
                    pensionDocument.denominacion= pension.denominacion();
                    pensionMongoRepository.save(pensionDocument);
                }
                case AlojamientoTuristico.ViviendaTuristica viviendaTuristica -> {
                    var viviendaTuristicaDocument = new ViviendaTuristicaDocument();
                    viviendaTuristicaDocument.denominacion= viviendaTuristica.denominacion();
                    viviendaTuristicaMongoRepository.save(viviendaTuristicaDocument);
                }
            }
        });

        var resultApartamentoRural = apartamentoRuralMongoRepository.findAll();
        var resultApartTuristico = apartTuristicoMongoRepository.findAll();
        var resultCamping = campingMongoRepository.findAll();
        var resultCasaHuespedes = casaHuespedesMongoRepository.findAll();
        var resultCasaRural = casaRuralMongoRepository.findAll();
        var resultHostal = hostalMongoRepository.findAll();
        var resultHosteria = hosteriaMongoRepository.findAll();
        var resultHotel = hotelMongoRepository.findAll();
        var resultHotelApart = hotelApartMongoRepository.findAll();
        var resultHotelRural = hotelRuralMongoRepository.findAll();
        var resultPension = pensionMongoRepository.findAll();
        var resultViviendaTuristica = viviendaTuristicaMongoRepository.findAll();

        assertThat(resultApartamentoRural).hasSize(1);
        assertThat(resultApartamentoRural.getFirst().denominacion).isEqualTo("ALMENARA");
        assertThat(resultApartTuristico).hasSize(1);
        assertThat(resultApartTuristico.getFirst().denominacion).isEqualTo("MURALTO");
        assertThat(resultCamping).hasSize(1);
        assertThat(resultCamping.getFirst().denominacion).isEqualTo("CAMPING OSUNA");
        assertThat(resultCasaHuespedes).hasSize(1);
        assertThat(resultCasaHuespedes.getFirst().denominacion).isEqualTo("WOOHOO ROOMS BOUTIQUE SOL");
        assertThat(resultCasaRural).hasSize(1);
        assertThat(resultCasaRural.getFirst().denominacion).isEqualTo("LA CASITA DE PIEDRA");
        assertThat(resultHostal).hasSize(1);
        assertThat(resultHostal.getFirst().denominacion).isEqualTo("VELAZQUEZ 45 BY PILLOW");
        assertThat(resultHosteria).hasSize(1);
        assertThat(resultHosteria.getFirst().denominacion).isEqualTo("PUERTA DE ÁNGEL");
        assertThat(resultHotel).isEmpty();
        assertThat(resultHotelApart).hasSize(1);
        assertThat(resultHotelApart.getFirst().denominacion).isEqualTo("ATOCHA HOTEL MADRID TAPESTRY COLLECTION BY HILTON");
        assertThat(resultHotelRural).hasSize(1);
        assertThat(resultHotelRural.getFirst().denominacion).isEqualTo("POSADA-RESTAURANTE LA POSADA DE HORCAHUELO");
        assertThat(resultPension).hasSize(1);
        assertThat(resultPension.getFirst().denominacion).isEqualTo("ISABEL");
        assertThat(resultViviendaTuristica).hasSize(1);
        assertThat(resultViviendaTuristica.getFirst().denominacion).isEqualTo("DALIAS, Nº 11");
    }

}
