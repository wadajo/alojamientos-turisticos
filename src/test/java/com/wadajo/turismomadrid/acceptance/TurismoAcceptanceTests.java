package com.wadajo.turismomadrid.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.internal.Files;
import com.wadajo.turismomadrid.TurismoAcceptanceBase;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.wadajo.turismomadrid.util.Constants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

class TurismoAcceptanceTests extends TurismoAcceptanceBase {

    @Test
    void debeDevolverTodosLosAlojamientosTuristicosAlPedirLaQuery() throws IOException {
        String alojamientosQueryString = Files.read(new File(ALOJAMIENTOS_QUERY_FILE), Charset.defaultCharset());
        JsonNode alojamientosJsonResponse = new ObjectMapper().readTree(new File(ALOJAMIENTOS_RESPONSE_FILE));

        stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalTo(alojamientosQueryString))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withJsonBody(alojamientosJsonResponse)
                ));

        given()
                .body(alojamientosQueryString)
        .when()
                .post(wireMockUrl+"/graphql")
        .then()
                .statusCode(200)
                .body(containsString(alojamientosJsonResponse.asText()));

    }

    @Test
    void debeBorrarTodosLosAlojamientosTuristicosAlEjecutarElBorrarTodo() throws IOException {
        String mutationBorrarTodo = Files.read(new File(ALOJAMIENTOS_BORRAR_FILE), Charset.defaultCharset());
        JsonNode borrarJsonResponse = new ObjectMapper().readTree(new File(BORRAR_RESPONSE_FILE));

        stubFor(post(urlEqualTo("/graphql"))
                .withRequestBody(equalTo(mutationBorrarTodo))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withJsonBody(borrarJsonResponse)
                ));

        given()
                .body(mutationBorrarTodo)
                .when()
                .post(wireMockUrl+"/graphql")
                .then()
                .statusCode(200)
                .body(containsString(borrarJsonResponse.asText()));

    }

}
