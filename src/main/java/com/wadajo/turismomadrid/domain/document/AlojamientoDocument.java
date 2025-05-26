package com.wadajo.turismomadrid.domain.document;

import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "alojamientosturisticos")
public abstract sealed class AlojamientoDocument permits ApartamentoRuralDocument,ApartTuristicoDocument,CampingDocument,CasaHuespedesDocument,CasaRuralDocument,HostalDocument,HosteriaDocument,HotelApartDocument,HotelDocument,HotelRuralDocument,PensionDocument,ViviendaTuristicaDocument{

    /**
     * Campo generado por BBDD, no se debe modificar.
     */
    @Id
    @SuppressWarnings("NullAway")
    public String id;

    public @Nullable String via_tipo, via_nombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, codpostal, localidad;

    /**
     * Tipo de alojamiento turístico, siempre incluido en BBDD.
     * Ejemplos: HOTEL, HOSTAL, VIVIENDAS DE USO TURÍSTICO, etc.
     */
    @SuppressWarnings("NullAway")
    public String alojamiento_tipo;

    /**
     * Campo generado por BBDD, no se debe modificar.
     */
    @SuppressWarnings("NullAway")
    public LocalDateTime timestamp;

}
