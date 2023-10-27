package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.HotelRuralDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRuralMongoRepository extends MongoRepository<HotelRuralDocument,String> {

}
