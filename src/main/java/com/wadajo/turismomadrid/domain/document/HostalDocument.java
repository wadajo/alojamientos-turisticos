package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hostales")
public final class HostalDocument extends AlojamientoDocument {

    public HostalDocument(){
        // Empty constructor for MongoDB
    }

}
