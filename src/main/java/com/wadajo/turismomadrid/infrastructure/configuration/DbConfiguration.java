package com.wadajo.turismomadrid.infrastructure.configuration;

import com.mongodb.ConnectionString;
import org.springframework.boot.autoconfigure.mongo.MongoConnectionDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class DbConfiguration {

    @Bean
    MongoConnectionDetails myMongoConnectionDetails(){
         return () -> new ConnectionString("mongodb://leon:pelonchi@localhost:27017/turismo?ssl=false");
    }
}
