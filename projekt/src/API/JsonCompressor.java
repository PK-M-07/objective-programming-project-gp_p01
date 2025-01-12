package API;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonCompressor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // Opcjonalne formatowanie JSON
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Serializuje obiekt do JSON.
     *
     * @param object Obiekt do serializacji
     * @return JSON w postaci Stringa
     * @throws Exception Jeśli wystąpi błąd serializacji
     */
    public static String toJson(Object object) throws Exception {
        if (object == null) {
            throw new IllegalArgumentException("Obiekt do serializacji nie może być null");
        }
        return objectMapper.writeValueAsString(object);

    }

}
