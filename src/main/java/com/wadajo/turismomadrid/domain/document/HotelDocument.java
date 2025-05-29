package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hoteles")
public final class HotelDocument extends AlojamientoDocument {

    public HotelDocument(){
        // Empty constructor for MongoDB
    }

}
