package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "casasdehuespedes")
public final class CasaHuespedesDocument extends AlojamientoDocument {

    public CasaHuespedesDocument(){
        // Empty constructor for MongoDB
    }

}
