package API;

import API.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    private static final String API_KEY = "d213bc64cbb74aa290a110104251401"; // Wstaw swój klucz API
    private static final String API_URL = "http://api.weatherapi.com/v1/current.json?key=" + API_KEY + "&q=";

    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.objectMapper = new ObjectMapper();
    }

    public WeatherData getWeatherData(String location) throws IOException {
        // Przygotowanie żądania do API
        String urlString = API_URL + location;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Wykonanie żądania i uzyskanie odpowiedzi
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Unexpected response code: " + responseCode);
        }

        // Odczytanie odpowiedzi
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder responseString = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseString.append(line);
        }
        reader.close();

        // Parsowanie odpowiedzi JSON do obiektów WeatherData
        return objectMapper.readValue(responseString.toString(), WeatherData.class);
    }

    public static void main(String[] args) {
        WeatherService weatherService = new WeatherService();
        try {
            WeatherData weatherData = weatherService.getWeatherData("Modjadje");
            // Przykład wykorzystania danych
            System.out.println("Lokalizacja: " + weatherData.getLocation().getName());
            System.out.println("Temperatura: " + weatherData.getCurrent().getTempC() + "°C");
            System.out.println("Warunki: " + weatherData.getCurrent().getCondition().getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
