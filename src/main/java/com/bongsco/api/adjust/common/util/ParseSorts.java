package com.bongsco.api.adjust.common.util;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
public class ParseSorts {
    public static List<Map<String, String>> extractSorts(String sorts) throws JsonProcessingException {
        if (sorts == null || sorts.isBlank()) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> parsed = mapper.readValue(sorts, new TypeReference<>() {
        });
        return parsed;
    }
}
