package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
public class StatsClient extends BaseClient {
    private static final String API_PREFIX_HIT = "/hit";
    private static final String API_PREFIX_STATS = "/stats";

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(EndpointHit endpointHit) {
        return post(API_PREFIX_HIT, endpointHit);
    }

    public ResponseEntity<Object> getAll(LocalDateTime start, LocalDateTime end, Collection<String> uris, Boolean unique) {

        String resultUrisString = String.join(",", uris);
        String resultQuery = String.format("?start={%s}&end={%s}&uris={%s}&unique={%s}", start, end, resultUrisString, unique);

        return get(API_PREFIX_STATS + resultQuery, null);
    }

}
