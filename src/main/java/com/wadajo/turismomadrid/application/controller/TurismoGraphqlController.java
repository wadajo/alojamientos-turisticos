package com.wadajo.turismomadrid.application.controller;

import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import com.wadajo.turismomadrid.domain.service.TurismoService;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class TurismoGraphqlController {

    private final TurismoService service;

    public TurismoGraphqlController(TurismoService service) {
        this.service = service;
    }

    @QueryMapping
    List<AlojamientoTuristico> alojamientosTuristicos(){
        return service.getAlojamientosTuristicos();
    }

}
