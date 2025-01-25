package API;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Klasa reprezentująca dane pogodowe, mapowana z odpowiedzi JSON z api
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {

    private Location location; // Obiekt lokalizacji
    private Current current;   // Obiekt z bieżącymi danymi pogodowymi

    public static class Location {
        private String name;     // Nazwa lokalizacji (miasto)
        private String country;   // Kraj

        // Gettery i settery
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }

    public static class Current {
        private double temp_c;        // Temperatura w stopniach Celsjusza
        private int humidity;         // Wilgotność
        private double pressure_mb;   // Ciśnienie w mb
        private Wind wind;            // Obiekt z danymi o wietrze
        private Condition condition;   // Warunki pogodowe
        private double precip_mm;      // Ilość opadów w mm

        // Gettery i settery
        public double getTempC() {
            return temp_c;
        }

        public void setTempC(double temp_c) {
            this.temp_c = temp_c;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getPressureMb() {
            return pressure_mb;
        }

        public void setPressureMb(double pressure_mb) {
            this.pressure_mb = pressure_mb;
        }

        public Wind getWind() {
            return wind;
        }

        public void setWind(Wind wind) {
            this.wind = wind;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        public double getPrecipMm() {
            return precip_mm;
        }

        public void setPrecipMm(double precip_mm) {
            this.precip_mm = precip_mm;
        }
    }

    public static class Wind {
        private double speed; // Prędkość wiatru w km/h

        // Getter i setter
        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }
    }

    public static class Condition {
        private String text; // Opis warunków pogodowych

        // Getter i setter
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    // Gettery i settery dla WeatherData
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Current getCurrent() {
        return current;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }

    // Metoda do pobierania danych pogodowych z API
    public static WeatherData fetchWeatherData(String city) throws Exception {
        String apiKey = "d213bc64cbb74aa290a110104251401"; // Podaj swój klucz API
        String urlString = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + city;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Przetwarzanie odpowiedzi JSON
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(response.toString(), WeatherData.class);
    }

    /*
    // Nazwa lokalizacji (miasto)
    private String name;

    // Obiekt głównych danych pogodowych (temperatura, ciśnienie, wilgotność)
    private Main main;

    // Lista obiektów "Weather", opisujących szczegóły pogody (np. opis, ikona)
    private List<Weather> weather;

    // Klasa z danymi głównymi (temperatura, wilgotność, ciśnienie)
    public static class Main {
        private double temp;         // Temperatura w stopniach Celsjusza
        private int pressure;        // Ciśnienie atmosferyczne w hPa
        private int humidity;        // Wilgotność w procentach

        // Gettery i settery
        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public int getPressure() {
            return pressure;
        }

        public void setPressure(int pressure) {
            this.pressure = pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }
    }

    // Klasa z opisem pogody (np. "pochmurno", "słonecznie")
    public static class Weather {
        private String description; // Opis pogody (np. "clear sky")
        private String icon;        // Kod ikony pogody

        // Gettery i settery
        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    // Gettery i settery dla głównych pól klasy "WeatherData"
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
     */
}
