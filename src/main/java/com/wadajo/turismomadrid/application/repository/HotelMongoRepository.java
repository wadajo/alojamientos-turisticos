package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.HotelDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelMongoRepository extends MongoRepository<HotelDocument,String> {

}
