package com.wadajo.turismomadrid.infrastructure.configuration;

import com.mongodb.ConnectionString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DbConfiguration {

    @Value("${turismomadrid.mongodb.local}")
    private String connectionString;

    @Bean
    MongoConnectionDetails myMongoConnectionDetails(){
         return () -> new ConnectionString(connectionString);
    }
}
