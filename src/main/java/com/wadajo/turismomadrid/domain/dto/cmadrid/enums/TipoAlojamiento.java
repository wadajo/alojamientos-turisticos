package com.wadajo.turismomadrid.domain.dto.cmadrid.enums;

public enum TipoAlojamiento {
    APART_TURISTICO("APART-TURISTICO","Apartamento turístico"),
    APARTAMENTO_RURAL("APARTAMENTO RURAL","Apartamento rural"),
    CAMPING("CAMPING","Camping"),
    CASA_HUESPEDES("CASA HUESPEDES","Casa de huéspedes"),
    CASA_RURAL("CASA RURAL","Casa rural"),
    HOSTAL("HOSTAL","Hostal"),
    HOSTERIAS("HOSTERIAS","Hostería"),
    HOTEL("HOTEL","Hotel"),
    HOTEL_APART("HOTEL-APART.","Apart-hotel"),
    HOTEL_RURAL("HOTEL RURAL","Hotel rural"),
    PENSION("PENSION","Pensión"),
    VIVIENDAS_TURISTICAS("VIVIENDAS DE USO TU ","Vivienda de uso turístico (Airbnb o sim)");

    private final String literalValueRaw;
    private final String printValue;

    TipoAlojamiento(String literalValueRaw, String printValue) {
        this.literalValueRaw = literalValueRaw;
        this.printValue = printValue;
    }
}
