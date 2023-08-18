package com.wadajo.turismomadrid.domain.model;

import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;

public sealed interface AlojamientoTuristico {
    
    record ApartamentoRural(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record ApartTuristico(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record Camping(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record CasaHuespedes(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record CasaRural(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record Hostal(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record Hosteria(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record Hotel(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record HotelApart(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record HotelRural(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record Pension(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
    record ViviendaTuristica(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad,TipoAlojamiento alojamiento_tipo) implements AlojamientoTuristico { }
}


