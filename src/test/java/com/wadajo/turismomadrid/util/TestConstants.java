package com.wadajo.turismomadrid.util;

public class TestConstants {
    public static final String GRAPHQL = "/graphql";
    public static final String ALOJAMIENTOS_QUERY_JSON_FILE = "src/test/resources/request/queryAlojamientos.json";
    public static final String ALOJAMIENTOS_QUERY_TIPO_JSON_FILE = "src/test/resources/request/queryAlojamientos-tipo.json";
    public static final String ALOJAMIENTOS_QUERY_TIPO_VACIO_JSON_FILE = "src/test/resources/request/queryAlojamientos-tipo-vacio.json";
    public static final String ALOJAMIENTOS_QUERY_TIPO_KO_JSON_FILE = "src/test/resources/request/queryAlojamientos-tipo-KO.json";
    public static final String ALOJAMIENTOS_BORRAR_FILE = "src/test/resources/request/mutationBorrarTodo.json";
    public static final String ALOJAMIENTOS_ACTUALIZAR_FILE = "src/test/resources/request/mutationActualizarDb.json";

    public static final String ALOJAMIENTOS_RAW_STUBBING_FILE = "src/test/resources/stubbing/alojamientos-raw.json";

    public static final String RESULTADO_BASE_OUT_QUERY = "Resultado: Total alojamientos turisticos: 12. {CASA_RURAL=1, HOTEL_APART=1, APARTAMENTO_RURAL=1, CASA_HUESPEDES=1, HOTEL_RURAL=1, PENSION=1, HOSTERIAS=1, CAMPING=1, HOSTAL=1, VIVIENDAS_TURISTICAS=1, APART_TURISTICO=1, HOTEL=1}";
    public static final String RESULTADO_FILTRADO_OUT_QUERY = "Resultado: Total alojamientos turisticos: 1. {CASA_RURAL=0, HOTEL_APART=0, APARTAMENTO_RURAL=0, CASA_HUESPEDES=0, HOTEL_RURAL=0, PENSION=0, HOSTERIAS=0, CAMPING=0, HOSTAL=0, VIVIENDAS_TURISTICAS=0, APART_TURISTICO=1, HOTEL=0}";
    public static final String RESULTADO_OUTPUT_MOCKS = "Resultado: Total alojamientos turisticos: 2. {CASA_RURAL=0, HOTEL_APART=0, APARTAMENTO_RURAL=0, CASA_HUESPEDES=0, HOTEL_RURAL=0, PENSION=0, HOSTERIAS=0, CAMPING=0, HOSTAL=0, VIVIENDAS_TURISTICAS=0, APART_TURISTICO=0, HOTEL=2}";

    public static final String RESULTADO_API_ACTUALIZARDB = "Han sido actualizados en DB: 12 alojamientos.";

    public static final String RESULTADO_API_BORRAR = "Borrados";
    public static final String RESULTADO_OUT_BORRAR = "Borradas todas las colecciones";
}
