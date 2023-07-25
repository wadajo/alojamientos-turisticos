package com.wadajo.turismomadrid.application.controller;

import com.wadajo.turismomadrid.domain.dto.cmadrid.AlojamientosTuristicosResponseDto;
import com.wadajo.turismomadrid.domain.service.TurismoService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TurismoGraphqlController {

    private final TurismoService service;

    public TurismoGraphqlController(TurismoService service) {
        this.service = service;
    }

    @QueryMapping
    AlojamientosTuristicosResponseDto alojamientosTuristicos(){
        return service.getAlojamientosTuristicos();
    }

}
