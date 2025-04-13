package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "alojamientosturisticos")
public abstract sealed class AlojamientoDocument permits ApartamentoRuralDocument,ApartTuristicoDocument,CampingDocument,CasaHuespedesDocument,CasaRuralDocument,HostalDocument,HosteriaDocument,HotelApartDocument,HotelDocument,HotelRuralDocument,PensionDocument,ViviendaTuristicaDocument{

    @Id
    public String id;

    public String via_tipo, via_nombre, numero, portal, bloque, planta, puerta, signatura, categoria, escalera, denominacion, codpostal, localidad;
    public String alojamiento_tipo;
    public LocalDateTime timestamp;

    protected AlojamientoDocument(){
    }

}
