package API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;


// Klasa do komunikacji z OpenWeatherAPI

public class ApiCommunication{

    // aktualna pogoda, prognoza pogody oraz historia pogody

    private final String URL_CURRENT = "https://api.openweathermap.org/geo/1.0/direct?q=London&limit=1&appid=0de6d7fd015e1159acf4e42b2489df68";
    // do poprawy
    private final String URL_FORECAST = "";
    private final String URL_HISTORY = "https://api.openweathermap.org/geo/1.0/";

    // Dane do komunikacji z OpenWeatherAPI.

    private String locationName;
    private String locationLatitude;
    private String locationLongitude;
    private String date;
    private String time;

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


    // pobieranie danych o aktualnej pogodzie

    public String getCurrentWeather() throws IOException{
        URL url;
        if(locationName != null){
            url = new URL(URL_CURRENT.replace("{location}", locationNameTranslation()));
        }
        else{
            url = new URL(URL_CURRENT.replace("{location}", locationLatitude + "," + locationLongitude));
        }

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
}