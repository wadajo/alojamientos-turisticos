package com.wadajo.turismomadrid.application;

import com.wadajo.turismomadrid.application.exception.ResponseTypeDtoException;
import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.UnknownContentTypeException;

import java.util.Optional;

@Component
public class AlojamientosClient {

    private final RestClient restClient;

    public AlojamientosClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public AlojamientosTuristicosResponseDto getResponseRaw() throws ResponseTypeDtoException {
        try {
            var responseOptional = Optional.ofNullable(restClient
                    .get()
                    .retrieve()
                    .body(AlojamientosTuristicosResponseDto.class));
            return responseOptional
                    .orElseThrow(() -> new ResponseTypeDtoException("Response from the server was null"));
        } catch (UnknownContentTypeException e) {
            throw new ResponseTypeDtoException("Could not serialise the response from the server", e);
        }
    }
}
