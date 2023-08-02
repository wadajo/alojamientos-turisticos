package com.wadajo.turismomadrid.domain.model;

import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;

public final class ApartamentoRural extends AlojamientoTuristico {

    public ApartamentoRural(String via_tipo, String via_nombre, String numero, String portal, String bloque, String planta, String puerta, String escalera, String denominacion, String cdpostal, String localidad) {
        super(via_tipo, via_nombre, numero, portal, bloque, planta, puerta, escalera, denominacion, cdpostal, localidad);
        this.alojamiento_tipo=TipoAlojamiento.APARTAMENTO_RURAL;
    }
}
