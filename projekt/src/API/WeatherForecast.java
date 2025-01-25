package API;

import java.util.List;

public class WeatherForecast {
    private List<ForecastDay> list;

    public List<ForecastDay> getList() {
        return list;
    }

    public void setList(List<ForecastDay> list) {
        this.list = list;
    }

    public static class ForecastDay {
        private Main main;
        private List<Weather> weather;
        private String dt_txt; // Data i czas prognozy

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

        public String getDt_txt() {
            return dt_txt;
        }

        public void setDt_txt(String dt_txt) {
            this.dt_txt = dt_txt;
        }
    }

    public static class Main {
        private double temp;

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }
    }

    public static class Weather {
        private String description;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
