package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "viviendasturisticas")
public final class ViviendaTuristicaDocument extends AlojamientoDocument {

    public ViviendaTuristicaDocument(){
        // Empty constructor for MongoDB
    }

}
