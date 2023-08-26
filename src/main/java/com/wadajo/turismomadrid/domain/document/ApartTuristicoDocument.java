package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "apartamentosturisticos")
public final class ApartTuristicoDocument extends AlojamientoDocument {

    public ApartTuristicoDocument(){
    }

}
