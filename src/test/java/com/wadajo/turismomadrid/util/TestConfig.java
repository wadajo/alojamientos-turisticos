package com.wadajo.turismomadrid.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

    @Bean
    MockRestServiceServer mockRestServiceServer(){
        return MockRestServiceServer.createServer(new RestTemplate());
    }
}
