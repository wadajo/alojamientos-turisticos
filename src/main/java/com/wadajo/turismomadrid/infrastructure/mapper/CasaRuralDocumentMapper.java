package com.wadajo.turismomadrid.infrastructure.mapper;

import com.wadajo.turismomadrid.domain.document.AlojamientoDocument;
import com.wadajo.turismomadrid.domain.model.AlojamientoTuristico;
import org.mapstruct.*;
import org.springframework.core.convert.converter.Converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CasaRuralDocumentMapper extends Converter<AlojamientoTuristico.CasaRural, AlojamientoDocument> {

    @Mapping(target = "portal", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "bloque", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "planta", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "puerta", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "escalera", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "codpostal", source = "cdpostal", qualifiedBy = EmptyStringToNull.class)
    @Mapping(target = "alojamiento_tipo", constant = "Casa rural")
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    @Override
    AlojamientoDocument convert(AlojamientoTuristico.CasaRural casaRural);

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
