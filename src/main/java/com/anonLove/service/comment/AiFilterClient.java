package com.anonLove.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiFilterClient {
    // TODO: 이건 잘 몰라서 일단 RestTemplate으로 해놨는데 연결하는 분이 WebClient나 수정해주시길..
    private final RestTemplate restTemplate;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    public boolean checkToxic(String content) {
        // URL이 설정되지 않았거나 기본값인 경우 통과 처리 (임시)
        if (!StringUtils.hasText(aiServerUrl) || aiServerUrl.contains("localhost")) {
            return false;
        }
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
            return false;
        }
    }

    @lombok.Getter
    @lombok.NoArgsConstructor
    private static class AiFilterResponse {
        private boolean isToxic;
    }
}