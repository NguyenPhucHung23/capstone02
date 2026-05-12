package cap2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiLayoutService {

    private final RestTemplate restTemplate;

    @Value("${ai.layout.url}")
    private String aiLayoutUrl;

    public Map<String, Object> generateLayout(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    aiLayoutUrl,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            return body != null ? body : Collections.emptyMap();
        } catch (Exception e) {
            log.error("Error calling AI layout service {}: {}", aiLayoutUrl, e.getMessage(), e);

            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("success", false);
            fallback.put("message", "Cannot call AI layout service: " + e.getMessage());
            fallback.put("items", Collections.emptyList());
            fallback.put("rejected", Collections.emptyList());
            return fallback;
        }
    }
}
