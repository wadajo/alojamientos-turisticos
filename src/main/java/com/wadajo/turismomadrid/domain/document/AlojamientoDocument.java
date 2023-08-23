package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "alojamientosturisticos")
public class AlojamientoDocument {

    @Id
    public String id;

    public String via_tipo, via_nombre, numero, portal, bloque, planta, puerta, escalera, denominacion, codpostal, localidad;
    public String alojamiento_tipo;
    public LocalDateTime timestamp;

    public AlojamientoDocument(){
    }

}
