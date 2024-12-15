package API;

import java.util.List;

// Klasa reprezentująca dane pogodowe, mapowana z odpowiedzi JSON od OpenWeatherMap
public class WeatherData {

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
}
