package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.ApartamentoRuralDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartamentoRuralMongoRepository extends MongoRepository<ApartamentoRuralDocument,String> {

}
