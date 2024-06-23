package ru.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.ewm.dto.EndpointHitCreateDto;
import ru.ewm.dto.ViewStats;

import java.util.List;
import java.util.Map;


@Service
public class StatsClient {
    private final RestTemplate rest;
    private final String serverUrl;

    public StatsClient(@Value("${stats-server.url}") String serverUrl) {
        this.rest = new RestTemplate();
        this.serverUrl = serverUrl;
    }

    /**
     *
     */
    public void addView(EndpointHitCreateDto createDto) {
        rest.exchange
                (
                        serverUrl + "/hit",
                        HttpMethod.POST,
                        new HttpEntity<>(createDto),
                        Object.class
                );
    }

    /**
     *
     */
    public List<ViewStats> getViews(List<String> uris) {
        Map<String, Object> parameters = Map.of("uris", String.join(",", uris));

        ResponseEntity<List<ViewStats>> response = rest.exchange
                (
                        serverUrl + "/stats?uris={uris}",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        },
                        parameters
                );

        return response.getBody();
    }
}
