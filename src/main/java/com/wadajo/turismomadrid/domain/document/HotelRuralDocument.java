package com.wadajo.turismomadrid.domain.document;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "hotelesrurales")
public final class HotelRuralDocument extends AlojamientoDocument {

    public HotelRuralDocument(){
    }

}
