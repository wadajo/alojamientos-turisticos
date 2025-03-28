package com.wadajo.turismomadrid.application.client;

import com.wadajo.turismomadrid.application.exception.ResponseTypeDtoException;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class AlojamientosClient {

    private final RestClient restClient;

    public AlojamientosClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public AlojamientosTuristicosResponseDto getResponseRaw() throws ResponseTypeDtoException {
        var responseOptional = Optional.ofNullable(restClient
            .get()
            .retrieve()
            .body(AlojamientosTuristicosResponseDto.class));
        return responseOptional
            .orElseThrow(() -> new ResponseTypeDtoException("Response from the server was null"));
    }
}
