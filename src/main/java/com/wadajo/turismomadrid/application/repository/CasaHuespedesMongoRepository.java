package com.wadajo.turismomadrid.application.repository;

import com.wadajo.turismomadrid.domain.document.CasaHuespedesDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CasaHuespedesMongoRepository extends MongoRepository<CasaHuespedesDocument,String> {

}
