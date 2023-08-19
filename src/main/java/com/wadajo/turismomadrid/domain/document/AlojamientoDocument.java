package com.wadajo.turismomadrid.domain.document;

import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "alojamientosturisticos")
public class AlojamientoDocument {

    @Id
    public String id;

    public String via_tipo, via_nombre, numero, portal, bloque, planta, puerta, escalera, denominacion, cdpostal, localidad;
    public TipoAlojamiento alojamiento_tipo;
    public LocalDateTime timestamp;

    public AlojamientoDocument(){
    }


    public AlojamientoDocument(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad, TipoAlojamiento alojamiento_tipo, LocalDateTime timestamp) {
        this.via_tipo = via_tipo;
        this.via_nombre = via_nombre;
        this.numero = numero;
        this.portal = portal;
        this.bloque = bloque;
        this.planta = planta;
        this.puerta = puerta;
        this.escalera = escalera;
        this.denominacion = denominacion;
        this.cdpostal = cdpostal;
        this.localidad = localidad;
        this.alojamiento_tipo = alojamiento_tipo;
        this.timestamp = timestamp;
    }
}
