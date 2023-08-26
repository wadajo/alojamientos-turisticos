package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "apartamentosrurales")
public final class ApartamentoRuralDocument extends AlojamientoDocument {

    public ApartamentoRuralDocument(){
    }

}
