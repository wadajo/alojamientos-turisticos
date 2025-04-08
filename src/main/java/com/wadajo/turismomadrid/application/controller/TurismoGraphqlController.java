package com.wadajo.turismomadrid.application.controller;

import com.wadajo.turismomadrid.domain.dto.cmadrid.enums.TipoAlojamiento;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import com.wadajo.turismomadrid.domain.service.TurismoService;
import graphql.GraphQLError;
import org.springframework.graphql.data.ArgumentValue;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindException;

import java.util.List;

@Controller
public class TurismoGraphqlController {

    private final TurismoService service;

    public TurismoGraphqlController(TurismoService service) {
        this.service = service;
    }

    @QueryMapping
    List<AlojamientoTuristico> alojamientosTuristicos(ArgumentValue<TipoAlojamiento> tipo) {
        if (tipo.isPresent()) {
            return service.getAlojamientosByType(tipo.value());
        } else {
            return service.getAlojamientosTuristicos();
        }
    }
    @MutationMapping
    String actualizarDB(){
        return service.actualizarAlojamientosEnDb();
    }

    @MutationMapping
    String borrarTodo(){
        service.borrarTodo();
        return "Borrados";
    }

    @GraphQlExceptionHandler
    public GraphQLError handle(BindException ex) {
        return GraphQLError.newError().errorType(ErrorType.BAD_REQUEST).message(ex.getMessage()).build();
    }
}
