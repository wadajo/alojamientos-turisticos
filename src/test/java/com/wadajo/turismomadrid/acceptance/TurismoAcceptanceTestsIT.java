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
import static com.wadajo.turismomadrid.util.TestConstants.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TurismoAcceptanceTestsIT extends TurismoAcceptanceBaseIT {

    @Test
    void debeDevolverTodosLosAlojamientosTuristicosAlPedirLaQuery(CapturedOutput output) throws IOException {
        JsonNode alojamientosQueryJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_QUERY_JSON_FILE));

        given()
            .body(alojamientosQueryJson)
            .contentType(ContentType.JSON)
        .when()
            .post(String.format("http://localhost:%s/graphql",port))
            .prettyPeek()
        .then()
            .assertThat()
            .body("data.alojamientosTuristicos[0].via_nombre",Matchers.equalTo("del Buen Suceso"))
            .and()
            .body("data.alojamientosTuristicos[1].via_nombre",Matchers.equalTo("de las Seguidillas"))
            .statusCode(200);

        assertThat(output.getOut(), containsString(RESULTADO_BASE_OUT_QUERY));
    }

    @Test
    void debeBorrarTodosLosAlojamientosTuristicosAlEjecutarElBorrarTodo(CapturedOutput output) throws IOException {
        JsonNode mutationBorrarTodoJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_BORRAR_FILE));

        stubFor(post(urlEqualTo(GRAPHQL))
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
            .body("data.borrarTodo",Matchers.equalTo(RESULTADO_API_BORRAR))
            .statusCode(200);

        assertThat(output.getOut(), containsString(RESULTADO_OUT_BORRAR));
    }

    @Test
    void debeActualizarDb(CapturedOutput output) throws IOException {
        JsonNode mutationActualizarDbJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_ACTUALIZAR_FILE));

        stubFor(post(urlEqualTo(GRAPHQL))
            .withRequestBody(equalToJson(mutationActualizarDbJson.toString()))
            .willReturn(aResponse()
                    .withStatus(200)
            ));

        given()
            .body(mutationActualizarDbJson)
            .contentType(ContentType.JSON)
        .when()
            .post(String.format("http://localhost:%s/graphql",port))
        .then()
            .assertThat()
            .body("data.actualizarDB", Matchers.equalTo(RESULTADO_API_ACTUALIZARDB))
            .statusCode(200);

        assertThat(output.getOut(), containsString(RESULTADO_BASE_OUT_QUERY));
    }

    @Test
    void debeDevolverLosTiposEspecificosPedidos(CapturedOutput output) throws IOException {
        JsonNode alojamientosQueryJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_QUERY_TIPO_JSON_FILE));

        given()
            .body(alojamientosQueryJson)
            .contentType(ContentType.JSON)
        .when()
            .post(String.format("http://localhost:%s/graphql",port))
        .then()
            .assertThat()
            .body("data.alojamientosTuristicos[0].via_nombre",Matchers.equalTo("del Buen Suceso"))
            .and()
            .body("data.alojamientosTuristicos[0].numero",Matchers.equalTo("3"))
            .statusCode(200);

        assertThat(output.getOut(), Matchers.not(containsString(RESULTADO_BASE_OUT_QUERY)));
        assertThat(output.getOut(), containsString(RESULTADO_FILTRADO_OUT_QUERY));
    }

    @Test
    void debeDevolverTodosLosAlojamientosSiNoSeEnviaTipo(CapturedOutput output) throws IOException {
        JsonNode alojamientosQueryJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_QUERY_TIPO_VACIO_JSON_FILE));

        given()
            .body(alojamientosQueryJson)
            .contentType(ContentType.JSON)
            .when()
            .post(String.format("http://localhost:%s/graphql",port))
            .prettyPeek()
            .then()
            .assertThat()
            .body("data.alojamientosTuristicos[0].via_nombre",Matchers.equalTo("del Buen Suceso"))
            .and()
            .body("data.alojamientosTuristicos[1].via_nombre",Matchers.equalTo("de las Seguidillas"))
            .statusCode(200);

        assertThat(output.getOut(), containsString(RESULTADO_BASE_OUT_QUERY));
    }

    @Test
    void debeArrojarExcepcionAlPedirUnTipoNoReconocido() throws IOException {
        JsonNode alojamientosQueryJson = new ObjectMapper().readTree(new File(ALOJAMIENTOS_QUERY_TIPO_KO_JSON_FILE));

        given()
            .body(alojamientosQueryJson)
            .contentType(ContentType.JSON)
            .when()
            .post(String.format("http://localhost:%s/graphql",port))
            .prettyPeek()
            .then()
            .assertThat()
            .body("data.alojamientosTuristicos",nullValue())
            .and()
            .body("errors[0].message", stringContainsInOrder(
                "rejected value [Horreo]",
                "Failed to convert argument value"))
            .and()
            .body("errors[0].extensions.classification", Matchers.equalTo("BAD_REQUEST"))
            .statusCode(200);
    }
}
