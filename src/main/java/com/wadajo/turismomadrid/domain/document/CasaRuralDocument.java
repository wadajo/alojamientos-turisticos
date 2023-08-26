package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "casasrurales")
public final class CasaRuralDocument extends AlojamientoDocument {

    public CasaRuralDocument(){
    }

}
