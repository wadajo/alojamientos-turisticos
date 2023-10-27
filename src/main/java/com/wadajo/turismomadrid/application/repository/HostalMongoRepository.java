package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.HostalDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HostalMongoRepository extends MongoRepository<HostalDocument,String> {

}
