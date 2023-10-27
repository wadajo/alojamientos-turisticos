package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.HosteriaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HosteriaMongoRepository extends MongoRepository<HosteriaDocument,String> {

}
