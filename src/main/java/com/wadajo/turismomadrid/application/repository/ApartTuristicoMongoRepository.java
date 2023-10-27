package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.ApartTuristicoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartTuristicoMongoRepository extends MongoRepository<ApartTuristicoDocument,String> {

}
