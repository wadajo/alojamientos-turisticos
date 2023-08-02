package com.wadajo.turismomadrid.domain.model;

import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;

public sealed class AlojamientoTuristico permits ApartTuristico, ApartamentoRural, Camping, CasaHuespedes, CasaRural, Hostal, Hosteria, Hotel, HotelApart, HotelRural, Pension, ViviendaTuristica {
    public AlojamientoTuristico(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad) {
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
    }

    TipoAlojamiento alojamiento_tipo;
    String via_tipo;
    String via_nombre;
    String numero;
    String portal;
    String bloque;
    String planta;
    String puerta;
    String escalera;
    String denominacion;
    String cdpostal;
    String localidad;

    public String puerta() {
        return puerta;
    }

    public String numero() {
        return numero;
    }

    public TipoAlojamiento alojamiento_tipo() {
        return alojamiento_tipo;
    }

    public String escalera() {
        return escalera;
    }

    public String planta() {
        return planta;
    }

    public String via_nombre() {
        return via_nombre;
    }

    public String denominacion() {
        return denominacion;
    }

    public String via_tipo() {
        return via_tipo;
    }

    public String cdpostal() {
        return cdpostal;
    }

    public String localidad() {
        return localidad;
    }

    public String portal() {
        return portal;
    }

    public String bloque() {
        return bloque;
    }
}


