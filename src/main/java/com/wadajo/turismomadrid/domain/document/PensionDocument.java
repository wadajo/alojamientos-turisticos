package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pensiones")
public final class PensionDocument extends AlojamientoDocument {

    public PensionDocument(){
        // Empty constructor for MongoDB
    }

}
