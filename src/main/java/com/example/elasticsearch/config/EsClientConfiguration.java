package com.example.elasticsearch.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EsClientConfiguration {


//    @Value("${elasticsearch.host}")
//    private String host;
//
//    @Value("${elasticsearch.port}")
//    private int port;

    @Value("${elasticsearch.apiKey}")
    private String apiKey;

    @Value("${elasticsearch.serverUrl}")
    private String serverUrl;


    @PostConstruct
    private void init() {
        log.info("Elastic server {} on port", serverUrl);
    }


    @Bean
    public RestClient restClient() {
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();
        return restClient;
    }

    @Bean
    ElasticsearchTransport elasticsearchTransport(RestClient restClient) {
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        return transport;
    }

    @Bean
    ElasticsearchClient elasticsearchClient(ElasticsearchTransport elasticsearchTransport) {
        // And create the API client
        ElasticsearchClient client = new ElasticsearchClient(elasticsearchTransport);
        return client;
    }

}