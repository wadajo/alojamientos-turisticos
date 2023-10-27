package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.CampingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampingMongoRepository extends MongoRepository<CampingDocument,String> {

}
