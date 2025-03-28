package com.wadajo.turismomadrid.util;

public class TestConstants {
    public static final String ALOJAMIENTOS_QUERY_JSON_FILE = "src/test/resources/request/queryAlojamientos.json";
    public static final String ALOJAMIENTOS_BORRAR_FILE = "src/test/resources/request/mutationBorrarTodo.json";
    public static final String ALOJAMIENTOS_ACTUALIZAR_FILE = "src/test/resources/request/mutationActualizarDb.json";

    public static final String ALOJAMIENTOS_RAW_FILE = "src/test/resources/response/alojamientos-raw.json";

    public static final String RESULTADO_OUT_QUERY = "Resultado: Total alojamientos turisticos: 12. {CASA_RURAL=1, HOTEL_APART=1, APARTAMENTO_RURAL=1, CASA_HUESPEDES=1, HOTEL_RURAL=1, PENSION=1, HOSTERIAS=1, CAMPING=1, HOSTAL=1, VIVIENDAS_TURISTICAS=1, APART_TURISTICO=1, HOTEL=1}";
    public static final String RESULTADO_API_ACTUALIZARDB = "Han sido actualizados en DB: 12 alojamientos.";
    public static final String RESULTADO_API_BORRAR = "Borrados";
    public static final String RESULTADO_OUT_BORRAR = "Borradas todas las colecciones";
    public static final String GRAPHQL = "/graphql";
}
