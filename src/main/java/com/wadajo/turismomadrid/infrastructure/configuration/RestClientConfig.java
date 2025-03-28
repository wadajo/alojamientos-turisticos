package com.wadajo.turismomadrid.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestClientConfig {


    @Value("${turismomadrid.endpoint.url}")
    private String alojamientosUrl;

    @Bean
    RestClient restClient(RestClient.Builder builder) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        converter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
        messageConverters.add(converter);
        return builder
                .baseUrl(alojamientosUrl)
                .messageConverters(messageConverters).build();
    }
}
