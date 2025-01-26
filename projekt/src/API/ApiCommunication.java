package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import javax.net.ssl.HttpsURLConnection;


// Klasa do komunikacji z OpenWeatherAPI
public class ApiCommunication{

    private static final String API_KEY = "d213bc64cbb74aa290a110104251401"; // klucz API
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";

    // Dane do komunikacji z OpenWeatherAPI.
    private String locationName;
    private String locationLatitude;
    private String locationLongitude;
    private String date;
    private String time;
    private final Gson gson = new Gson(); // Inicjalizacja Gson
    private String city;

    // konstruktor klasy
    public ApiCommunication(String locationName){
        this.locationName = locationName;
    }

    public ApiCommunication(String locationLatitude, String locationLongitude){
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    public ApiCommunication(String locationName, String date, String time){
        this.locationName = locationName;
        this.date = date;
        this.time = time;
    }

    public ApiCommunication(String locationLatitude, String locationLongitude, String date, String time){
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.date = date;
        this.time = time;
    }

    /*
    // obliczanie liczby dni między dniem aktualnym a dniem podanym w parametrze

    private String numberOfDays(){
        LocalDate localDate = LocalDate.now();
        LocalDate targetDate = LocalDate.parse(this.date);
        int numberOfDays = targetDate.getDayOfYear() - localDate.getDayOfYear();
        return String.valueOf(numberOfDays);
    }


    //tłumaczenie nazwy lokalizacji na angielski

    private String locationNameTranslation(){
        String originalLocationName = this.locationName;
        String translatedLocationName;
        HashMap<String, String> nameTranslation = new HashMap<>();
        nameTranslation.put("Warszawa", "Warsaw");
        nameTranslation.put("Kraków", "Cracow");

        if(nameTranslation.containsKey(originalLocationName)){
            translatedLocationName = nameTranslation.get(originalLocationName);
        }
        else{
            translatedLocationName = originalLocationName
                    .replace("ą", "a")
                    .replace("ć", "c")
                    .replace("ę", "e")
                    .replace("ł", "l")
                    .replace("ń", "n")
                    .replace("ó", "o")
                    .replace("ś", "s")
                    .replace("ź", "z")
                    .replace("ż", "z");

        }
        return translatedLocationName;
    }
    */

    // Metoda do generowania URL na podstawie typu żądania
    private String getWeatherUrl(String endpoint) throws UnsupportedEncodingException {
        String location = (locationName != null) ? URLEncoder.encode(locationName, "UTF-8") : locationLatitude + "," + locationLongitude;
        return BASE_URL + endpoint + "?key=" + API_KEY + "&q=" + location + "&lang=pl&aqi=no";
    }

    // pobieranie danych o aktualnej pogodzie
    public JsonObject getCurrentWeather() throws IOException, JsonSyntaxException {
        URL url = new URL(getWeatherUrl("current.json"));
        System.out.println("Requesting URL: " + url.toString());

        String jsonResponse = fetchDataFromApi(url);
        System.out.println("API Response: " + jsonResponse);

        if (jsonResponse.contains("error") || jsonResponse.contains("404")) {
            throw new IOException("Błąd w odpowiedzi API: " + jsonResponse);
        }

        return JsonParser.parseString(jsonResponse).getAsJsonObject();
    }

    // Dane do zbuforowanej odpowiedzi
    private JsonObject cachedCurrentWeather;

    public JsonObject getCachedCurrentWeather() throws IOException {
        if (cachedCurrentWeather == null) {
            cachedCurrentWeather = getCurrentWeather();
        }
        return cachedCurrentWeather;
    }

    // Metody do pobierania specyficznych danych pogodowych
    public double getTemperature() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("temp_c").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o temperaturze");
    }

    public String getWeatherCondition() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").getAsJsonObject("condition").get("text").getAsString();
        }
        throw new JsonSyntaxException("Brak danych o warunkach pogodowych");
    }

    public double getPrecipitation() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("precip_mm").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o opadach");
    }

    public double getWindSpeed() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("wind_kph").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o wietrze");
    }

    public double getPressure() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("pressure_mb").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o ciśnieniu");
    }

    public int getHumidity() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("humidity").getAsInt();
        }
        throw new JsonSyntaxException("Brak danych o wilgotności");
    }

    // Pobieranie prognozy pogody
    public JsonObject getForecastWeather(int days) throws IOException, JsonSyntaxException {
        URL url = new URL(getWeatherUrl("forecast.json") + "&days=" + days);
        String jsonResponse = fetchDataFromApi(url);

        // Parsowanie JSON do JsonObject
        return JsonParser.parseString(jsonResponse).getAsJsonObject();
    }

    // Metoda do pobierania prognozy temperatury na kolejne 'n' dni
    public double[] getTemperatureForecast(int days) throws IOException, JsonSyntaxException {
        // Pobierz prognozę pogody na 'days' dni
        JsonObject forecastWeather = getForecastWeather(days);

        // Inicjalizujemy tablicę do przechowywania temperatur
        double[] temperatures = new double[days];

        // Parsujemy dane o temperaturze z odpowiedzi API
        JsonObject forecast = forecastWeather.getAsJsonObject("forecast");
        for (int i = 0; i < days; i++) {
            // Pobieramy dane dla konkretnego dnia (w formacie API jest to lista na podstawie daty)
            JsonObject dayForecast = forecast.getAsJsonArray("forecastday").get(i).getAsJsonObject();
            JsonObject day = dayForecast.getAsJsonObject("day");

            // Zapisujemy temperaturę w tablicy
            temperatures[i] = day.get("avgtemp_c").getAsDouble();
        }

        return temperatures;
    }

    // Pobieranie danych o historii
    public JsonObject getHistoryWeather() throws IOException, JsonSyntaxException {
        URL url = new URL(getWeatherUrl("history.json") + "&dt=" + date);
        String jsonResponse = fetchDataFromApi(url);

        // Parsowanie JSON do JsonObject
        return JsonParser.parseString(jsonResponse).getAsJsonObject();
    }

    public String getCity() {
        return this.city;
    }

    private String fetchDataFromApi(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            // Wydrukuj odpowiedź w przypadku błędu
            String errorResponse;
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                StringBuilder errorBuilder = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorBuilder.append(line);
                }
                errorResponse = errorBuilder.toString();
            }
            throw new IOException("Błąd serwera: " + responseCode + " Odpowiedź: " + errorResponse);
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }

    /*
    // pobieranie danych o prognozie pogody
    public String getForecastWeather() throws IOException{
        URL url;
        String replacedUrl;
        if(locationName != null){
            replacedUrl = URL_FORECAST
                    .replace("{location}", locationNameTranslation())
                    .replace("{numberOfDay}", numberOfDays())
                    .replace("{dateOfDay}", date)
                    .replace("{hourOfDay}", time);
        }
        else{
            replacedUrl = URL_FORECAST
                    .replace("{location}", locationLatitude + "," + locationLongitude)
                    .replace("{numberOfDay}", numberOfDays())
                    .replace("{dateOfDay}", date)
                    .replace("{hourOfDay}", time);
        }
        url = new URL(replacedUrl);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }

    //pobieranie danych o historii

    public String getHistoryWeather() throws IOException{
        URL url;
        String replacedUrl;
        if(locationName != null){
            replacedUrl = URL_HISTORY
                    .replace("{location}", locationNameTranslation())
                    .replace("{date}", date)
                    .replace("{hourOfDay}", time);
        }
        else{
            replacedUrl = URL_HISTORY
                    .replace("{location}", locationLatitude + "," + locationLongitude)
                    .replace("{date}", date)
                    .replace("{hourOfDay}", time);
        }
        url = new URL(replacedUrl);

        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }
    }
     */
}