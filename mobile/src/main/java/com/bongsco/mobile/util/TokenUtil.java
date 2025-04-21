package com.bongsco.mobile.util;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenUtil {
    public static String extractEmailFromEncodedJsonToken(String encodedJson) {
        try {
            // 1. Base64 디코딩
            byte[] decodedBytes = Base64.getDecoder().decode(encodedJson);
            String decodedJson = new String(decodedBytes);

            // 2. JSON 파싱
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(decodedJson);

            // 3. email 추출
            return jsonNode.get("email").asText();

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract email from encoded JSON", e);
        }
    }
}
