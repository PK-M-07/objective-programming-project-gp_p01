package API;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Klasa reprezentująca dane pogodowe, mapowana z odpowiedzi JSON z API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherData {

    private Location location; // Obiekt lokalizacji
    private Current current;   // Obiekt z bieżącymi danymi pogodowymi

    /**
     * Klasa wewnętrzna reprezentująca lokalizację (miasto, kraj).
     */
    public static class Location {
        private String name;     // Nazwa lokalizacji (miasto)
        private String country;   // Kraj

        /**
         * Pobiera nazwę lokalizacji (miasta).
         * @return nazwa lokalizacji
         */
        public String getName() {
            return name;
        }

        /**
         * Ustawia nazwę lokalizacji (miasta).
         * @param name nazwa lokalizacji
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Pobiera nazwę kraju.
         * @return nazwa kraju
         */
        public String getCountry() {
            return country;
        }

        /**
         * Ustawia nazwę kraju.
         * @param country nazwa kraju
         */
        public void setCountry(String country) {
            this.country = country;
        }
    }

    /**
     * Klasa wewnętrzna reprezentująca bieżące dane pogodowe.
     */
    public static class Current {
        private double temp_c;        // Temperatura w stopniach Celsjusza
        private int humidity;         // Wilgotność
        private double pressure_mb;   // Ciśnienie w mb
        private Wind wind;            // Obiekt z danymi o wietrze
        private Condition condition;   // Warunki pogodowe
        private double precip_mm;      // Ilość opadów w mm

        /**
         * Pobiera temperaturę w stopniach Celsjusza.
         * @return temperatura w stopniach Celsjusza
         */
        public double getTempC() {
            return temp_c;
        }

        /**
         * Ustawia temperaturę w stopniach Celsjusza.
         * @param temp_c temperatura w stopniach Celsjusza
         */
        public void setTempC(double temp_c) {
            this.temp_c = temp_c;
        }

        /**
         * Pobiera wilgotność w procentach.
         * @return wilgotność w procentach
         */
        public int getHumidity() {
            return humidity;
        }

        /**
         * Ustawia wilgotność w procentach.
         * @param humidity wilgotność w procentach
         */
        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        /**
         * Pobiera ciśnienie atmosferyczne w mb.
         * @return ciśnienie w mb
         */
        public double getPressureMb() {
            return pressure_mb;
        }

        /**
         * Ustawia ciśnienie atmosferyczne w mb.
         * @param pressure_mb ciśnienie w mb
         */
        public void setPressureMb(double pressure_mb) {
            this.pressure_mb = pressure_mb;
        }

        /**
         * Pobiera dane o wietrze.
         * @return obiekt Wind z danymi o wietrze
         */
        public Wind getWind() {
            return wind;
        }

        /**
         * Ustawia dane o wietrze.
         * @param wind obiekt Wind z danymi o wietrze
         */
        public void setWind(Wind wind) {
            this.wind = wind;
        }

        /**
         * Pobiera warunki pogodowe.
         * @return obiekt Condition z opisem warunków pogodowych
         */
        public Condition getCondition() {
            return condition;
        }

        /**
         * Ustawia warunki pogodowe.
         * @param condition obiekt Condition z opisem warunków pogodowych
         */
        public void setCondition(Condition condition) {
            this.condition = condition;
        }

        /**
         * Pobiera ilość opadów w mm.
         * @return ilość opadów w mm
         */
        public double getPrecipMm() {
            return precip_mm;
        }

        /**
         * Ustawia ilość opadów w mm.
         * @param precip_mm ilość opadów w mm
         */
        public void setPrecipMm(double precip_mm) {
            this.precip_mm = precip_mm;
        }
    }

    /**
     * Klasa wewnętrzna reprezentująca dane o wietrze.
     */
    public static class Wind {
        private double speed; // Prędkość wiatru w km/h

        /**
         * Pobiera prędkość wiatru w km/h.
         * @return prędkość wiatru w km/h
         */
        public double getSpeed() {
            return speed;
        }

        /**
         * Ustawia prędkość wiatru w km/h.
         * @param speed prędkość wiatru w km/h
         */
        public void setSpeed(double speed) {
            this.speed = speed;
        }
    }


    /**
     * Klasa wewnętrzna reprezentująca warunki pogodowe.
     */
    public static class Condition {
        private String text; // Opis warunków pogodowych

        /**
         * Pobiera opis warunków pogodowych.
         * @return opis warunków pogodowych
         */
        public String getText() {
            return text;
        }

        /**
         * Ustawia opis warunków pogodowych.
         * @param text opis warunków pogodowych
         */
        public void setText(String text) {
            this.text = text;
        }
    }

    /**
     * Pobiera dane o lokalizacji.
     * @return obiekt Location z danymi o lokalizacji
     */
    public Location getLocation() {
        return location;
    }

    /**
            * Ustawia dane o lokalizacji.
            * @param location obiekt Location z danymi o lokalizacji
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Pobiera bieżące dane pogodowe.
     * @return obiekt Current z bieżącymi danymi pogodowymi
     */
    public Current getCurrent() {
        return current;
    }

    /**
     * Ustawia bieżące dane pogodowe.
     * @param current obiekt Current z bieżącymi danymi pogodowymi
     */
    public void setCurrent(Current current) {
        this.current = current;
    }


    /**
     * Metoda do pobierania danych pogodowych z API dla określonego miasta.
     * @param city nazwa miasta
     * @return obiekt WeatherData z danymi pogodowymi
     * @throws Exception w przypadku błędu połączenia lub nieprawidłowej odpowiedzi
     */
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
}