package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.PensionDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PensionMongoRepository extends MongoRepository<PensionDocument,String> {

}
