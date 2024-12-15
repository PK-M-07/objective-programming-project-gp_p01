package API;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class WeatherDataParser {

    // Metoda do parsowania JSON do obiektu WeatherData
    public WeatherData parseWeatherData(String json) throws IOException {
        // Tworzenie obiektu ObjectMapper do przetwarzania JSON
        ObjectMapper objectMapper = new ObjectMapper();

        // Deserializacja JSON do obiektu WeatherData
        WeatherData weatherData = objectMapper.readValue(json, WeatherData.class);

        return weatherData;
    }
}
