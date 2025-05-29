package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "campings")
public final class CampingDocument extends AlojamientoDocument {

    public CampingDocument(){
        // Empty constructor for MongoDB
    }

}
