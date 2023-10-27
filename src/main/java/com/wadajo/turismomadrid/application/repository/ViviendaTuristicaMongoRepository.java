package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.ViviendaTuristicaDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViviendaTuristicaMongoRepository extends MongoRepository<ViviendaTuristicaDocument,String> {

}
