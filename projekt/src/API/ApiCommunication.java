package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.google.gson.*;

import javax.net.ssl.HttpsURLConnection;


/**
 * Klasa do komunikacji z WeatherAPI.
 * Pozwala na pobieranie danych o aktualnej pogodzie, prognozach oraz historii pogodowej.
 */
public class ApiCommunication{

    private static final String API_KEY = "d213bc64cbb74aa290a110104251401"; // klucz API
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";

    // Dane do komunikacji z WeatherAPI.
    private String locationName;
    private String locationLatitude;
    private String locationLongitude;
    private String date;
    private String time;
    private final Gson gson = new Gson(); // Inicjalizacja Gson
    private String city;

    /**
     * Konstruktor klasy.
     * @param locationName Nazwa lokalizacji.
     */
    public ApiCommunication(String locationName) {
        this.locationName = locationName;
        this.city = locationName; // Inicjalizacja zmiennej city
    }

    /**
     * Konstruktor klasy.
     * @param locationLatitude Szerokość geograficzna lokalizacji.
     * @param locationLongitude Długość geograficzna lokalizacji.
     */
    public ApiCommunication(String locationLatitude, String locationLongitude){
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
    }

    /**
     * Konstruktor klasy.
     * @param locationName Nazwa lokalizacji.
     * @param date Data w formacie YYYY-MM-DD.
     * @param time Godzina w formacie HH:MM.
     */
    public ApiCommunication(String locationName, String date, String time){
        this.locationName = locationName;
        this.date = date;
        this.time = time;
    }

    /**
     * Konstruktor klasy.
     * @param locationLatitude Szerokość geograficzna lokalizacji.
     * @param locationLongitude Długość geograficzna lokalizacji.
     * @param date Data w formacie YYYY-MM-DD.
     * @param time Godzina w formacie HH:MM.
     */
    public ApiCommunication(String locationLatitude, String locationLongitude, String date, String time){
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.date = date;
        this.time = time;
    }


    /**
     * Metoda generująca URL na podstawie typu żądania.
     * @param endpoint Typ żądania (np. "current.json").
     * @return URL do API w postaci String.
     * @throws UnsupportedEncodingException W przypadku błędu kodowania.
     */
    private String getWeatherUrl(String endpoint) throws UnsupportedEncodingException {
        String location = (locationName != null) ? URLEncoder.encode(locationName, "UTF-8") : locationLatitude + "," + locationLongitude;
        return BASE_URL + endpoint + "?key=" + API_KEY + "&q=" + location + "&lang=pl&aqi=no";
    }


    /**
     * Metoda pobierająca dane o aktualnej pogodzie.
     * @return Obiekt JsonObject z danymi o aktualnej pogodzie.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
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

    /**
     * Metoda pobierająca zbuforowane dane o aktualnej pogodzie.
     * @return Obiekt JsonObject z danymi o aktualnej pogodzie.
     * @throws IOException W przypadku problemów z połączeniem.
     */
    public JsonObject getCachedCurrentWeather() throws IOException {
        if (cachedCurrentWeather == null) {
            cachedCurrentWeather = getCurrentWeather();
        }
        return cachedCurrentWeather;
    }

    /**
     * Pobieranie temperatury.
     * @return Temperatura w stopniach Celsjusza.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public double getTemperature() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("temp_c").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o temperaturze");
    }

    /**
     * Pobieranie warunków pogodowych.
     * @return Opis warunków pogodowych.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public String getWeatherCondition() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").getAsJsonObject("condition").get("text").getAsString();
        }
        throw new JsonSyntaxException("Brak danych o warunkach pogodowych");
    }

    /**
     * Pobieranie opadów.
     * @return Opady w milimetrach.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public double getPrecipitation() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("precip_mm").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o opadach");
    }

    /**
     * Pobieranie prędkości wiatru.
     * @return Prędkość wiatru w kilometrach na godzinę.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public double getWindSpeed() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("wind_kph").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o wietrze");
    }

    /**
     * Pobieranie ciśnienia atmosferycznego.
     * @return Ciśnienie w hektopaskalach (hPa).
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public double getPressure() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("pressure_mb").getAsDouble();
        }
        throw new JsonSyntaxException("Brak danych o ciśnieniu");
    }

    /**
     * Pobieranie wilgotności powietrza.
     * @return Wilgotność w procentach.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public int getHumidity() throws IOException, JsonSyntaxException {
        JsonObject currentWeather = getCurrentWeather();
        if (currentWeather.has("current")) {
            return currentWeather.getAsJsonObject("current").get("humidity").getAsInt();
        }
        throw new JsonSyntaxException("Brak danych o wilgotności");
    }


    /**
     * Pobieranie prognozy pogody na określoną liczbę dni.
     * @param days Liczba dni prognozy.
     * @return Obiekt JsonObject z prognozą pogody.
     */
    public JsonObject getForecastWeather(int days) {
        JsonObject jsonResponse = null;
        try {
            // Budowa URL-a do prognozy pogody
            String location = (city != null) ? city : locationLatitude + "," + locationLongitude;
            String url = BASE_URL + "forecast.json?key=" + API_KEY + "&q=" + location + "&days=" + days + "&aqi=no&alerts=no";

            // Użycie fetchDataFromApi zamiast makeApiRequest
            String response = fetchDataFromApi(new URL(url));

            // Parsowanie odpowiedzi na JSON
            jsonResponse = JsonParser.parseString(response).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResponse;
    }

    /**
     * Pobieranie prognozy temperatury na kolejne dni.
     * @param days Liczba dni prognozy.
     * @return Tablica temperatur w stopniach Celsjusza.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public double[] getTemperatureForecast(int days) throws IOException, JsonSyntaxException {
        JsonObject forecastWeather = getForecastWeather(days);

        double[] temperatures = new double[days];

        JsonObject forecast = forecastWeather.getAsJsonObject("forecast");
        for (int i = 0; i < days; i++) {
            JsonObject dayForecast = forecast.getAsJsonArray("forecastday").get(i).getAsJsonObject();
            JsonObject day = dayForecast.getAsJsonObject("day");

            temperatures[i] = day.get("avgtemp_c").getAsDouble();
        }

        return temperatures;
    }




    /**
     * Pobieranie danych o historii pogody.
     * @return Obiekt JsonObject z danymi o pogodzie w przeszłości.
     * @throws IOException W przypadku problemów z połączeniem.
     * @throws JsonSyntaxException W przypadku błędu parsowania JSON.
     */
    public JsonObject getHistoryWeather() throws IOException, JsonSyntaxException {
        URL url = new URL(getWeatherUrl("history.json") + "&dt=" + date);
        String jsonResponse = fetchDataFromApi(url);

        // Parsowanie JSON do JsonObject
        return JsonParser.parseString(jsonResponse).getAsJsonObject();
    }

    /**
     * Pobieranie nazwy miasta.
     * @return Nazwa miasta.
     */
    public String getCity() {
        return this.city;
    }

    /**
     * Metoda pomocnicza do pobierania danych z API.
     * @param url Obiekt URL do API.
     * @return Odpowiedź API w postaci String.
     * @throws IOException W przypadku problemów z połączeniem.
     */
    private String fetchDataFromApi(URL url) throws IOException {
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
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

    /**
     * Generowanie etykiet dni.
     * @param days Liczba dni.
     * @return Tablica etykiet w formacie "Dzień X".
     */
    public static String[] generateDayLabels(int days) {
        String[] dayLabels = new String[days];
        for (int i = 0; i < days; i++) {
            dayLabels[i] = "Dzień " + (i + 1); // Ustal etykiety dni
        }
        return dayLabels;
    }
}