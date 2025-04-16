package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "aparthoteles")
public final class HotelApartDocument extends AlojamientoDocument {

    public HotelApartDocument(){
        // Empty constructor for MongoDB
    }

}
