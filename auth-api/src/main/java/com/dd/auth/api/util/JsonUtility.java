package com.dd.auth.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class JsonUtility {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtility.class);
    private final ObjectMapper objectMapper;

    public JsonUtility(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> String convertToString(T object) throws JsonProcessingException {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException ex) {
            LOGGER.error("Unable to convert into string.." + ex.getMessage());
        }
        return null;
    }

    public <T> T convertFromJson(String json, Class<T> type) throws IOException {
        try {
            return objectMapper.readValue(json, type);
        } catch (IOException ex) {
            LOGGER.error("Unable to covert from json.." + ex.getMessage());
        }
        return null;
    }

    public <T> T convertCollectionFromJson(String json, Class<T> type) throws IOException {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        try {
            return objectMapper.readValue(json, typeFactory.constructCollectionType(List.class, type));
        } catch (IOException ex) {
            LOGGER.error("Unable to convert into list..." + ex.getMessage());
        }
        return null;
    }
}
