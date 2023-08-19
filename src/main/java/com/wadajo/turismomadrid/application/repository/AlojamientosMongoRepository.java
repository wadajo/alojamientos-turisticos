package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.AlojamientoDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlojamientosMongoRepository extends MongoRepository<AlojamientoDocument,String> {

}
