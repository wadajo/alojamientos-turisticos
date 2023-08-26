package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hosterias")
public final class HosteriaDocument extends AlojamientoDocument {

    public HosteriaDocument(){
    }

}
