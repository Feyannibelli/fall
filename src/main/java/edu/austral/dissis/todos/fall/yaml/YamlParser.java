package edu.austral.dissis.todos.fall.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class YAMLParser {

    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public static  <T> T fromYAML(String yaml, Class<T> clazz) {
        try {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(yaml, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
