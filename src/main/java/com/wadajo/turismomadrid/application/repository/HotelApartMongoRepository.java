package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.HotelApartDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelApartMongoRepository extends MongoRepository<HotelApartDocument,String> {

}
