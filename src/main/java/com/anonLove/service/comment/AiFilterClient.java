package com.anonLove.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiFilterClient {

    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public boolean checkToxic(String content) {
        try {
            String url = aiServerUrl + "/predict";

            Map<String, String> request = Map.of("text", content);

            ResponseEntity<AiFilterResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    AiFilterResponse.class
            );

            if (response.getBody() != null) {
                return response.getBody().isToxic();
            }

            return false;
        } catch (Exception e) {
            log.error("AI Filter Error: ", e);
            return false; // AI 서버 오류 시 필터링 안 함
        }
    }

    @lombok.Getter
    @lombok.NoArgsConstructor
    private static class AiFilterResponse {
        private boolean isToxic;
    }
}