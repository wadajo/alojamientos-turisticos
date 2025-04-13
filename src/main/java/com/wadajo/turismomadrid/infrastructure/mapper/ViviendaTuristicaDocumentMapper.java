package com.wadajo.turismomadrid.infrastructure.mapper;

import com.wadajo.turismomadrid.domain.document.ViviendaTuristicaDocument;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import org.mapstruct.*;
import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ViviendaTuristicaDocumentMapper extends Converter<AlojamientoTuristico.ViviendaTuristica, ViviendaTuristicaDocument> {

    @Mapping(target = "portal", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "bloque", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "planta", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "puerta", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "signatura", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "categoria", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "escalera", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "codpostal", source = "cdpostal", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "denominacion", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "alojamiento_tipo", constant = "Vivienda turística (Airbnb o sim)")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Override
    ViviendaTuristicaDocument convert(AlojamientoTuristico.ViviendaTuristica viviendaTuristica);

    @EmptyStringToNull
    default String emptyStringToNull(String s) {
        return s.isEmpty() ? null : s;
    }

    @Qualifier
    @java.lang.annotation.Target(ElementType.METHOD)
    @Retention(RetentionPolicy.CLASS)
    @interface EmptyStringToNull {
    }

}
