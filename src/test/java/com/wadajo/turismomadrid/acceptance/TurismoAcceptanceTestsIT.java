package com.wadajo.turismomadrid.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wadajo.turismomadrid.TurismoAcceptanceBaseIT;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.system.CapturedOutput;

import java.io.File;
import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.wadajo.turismomadrid.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

class TurismoAcceptanceTestsIT extends TurismoAcceptanceBaseIT {

    @Test
    void debeDevolverTodosLosAlojamientosTuristicosAlPedirLaQuery(CapturedOutput output) throws IOException {
        JsonNode alojamientosQueryJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_QUERY_JSON_FILE));
        JsonNode alojamientosRaw = new ObjectMapper().readTree(new File(ALOJAMIENTOS_RAW_FILE));
        JsonNode alojamientosResponse = new ObjectMapper().readTree(new File(ALOJAMIENTOS_RESPONSE_FILE));

        stubFor(get("/")
                .willReturn(aResponse()
                .withStatus(200)
                .withJsonBody(alojamientosRaw)
                ));

        given()
                .body(alojamientosQueryJson)
                .contentType(ContentType.JSON)
        .when()
                .post(String.format("http://localhost:%s/graphql",port))
                .prettyPeek()
        .then()
                .assertThat()
                .body(Matchers.equalToCompressingWhiteSpace(alojamientosResponse.toString()))
                .statusCode(200);

        assertThat(output.getOut(),
                containsString("Resultado: Total alojamientos turisticos: 2. {CASA_RURAL=0, HOTEL_APART=0, APARTAMENTO_RURAL=0, CASA_HUESPEDES=0, HOTEL_RURAL=0, PENSION=1, HOSTERIAS=0, CAMPING=0, HOSTAL=0, VIVIENDAS_TURISTICAS=0, APART_TURISTICO=0, HOTEL=1}"));

    }

    @Test
    void debeBorrarTodosLosAlojamientosTuristicosAlEjecutarElBorrarTodo(CapturedOutput output) throws IOException {
        JsonNode mutationBorrarTodoJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_BORRAR_FILE));
        JsonNode mutationBorrarTodoResponse = new ObjectMapper().readTree(new File(BORRAR_RESPONSE_FILE));

        stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalToJson(mutationBorrarTodoJson.toString()))
                .willReturn(aResponse()
                        .withStatus(200)
                ));

        given()
                .body(mutationBorrarTodoJson)
                .contentType(ContentType.JSON)
        .when()
                .post(String.format("http://localhost:%s/graphql",port))
        .then()
                .assertThat()
                .body(Matchers.equalToCompressingWhiteSpace(mutationBorrarTodoResponse.toString()))
                .statusCode(200);

        assertThat(output.getOut(),
                containsString("Borradas todas las colecciones"));

    }

}
