package ru.ewm;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
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
        log.info("Отправка POST-запроса на: {} с телом: {}", serverUrl + "/hit", createDto);
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
    public List<ViewStats> getViews(List<String> uris, boolean unique) {
        log.info("Получены URI: {}", uris);
        Map<String, Object> parameters = Map.of("uris", String.join(",", uris), "unique", unique);

        log.info("Отправка GET-запроса на: {} с параметрами: {}", serverUrl + "/stats", parameters);
        ResponseEntity<List<ViewStats>> response = rest.exchange
                (
                        serverUrl + "/stats?uris={uris}&unique={unique}",
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        },
                        parameters
                );

        return response.getBody();
    }
}
