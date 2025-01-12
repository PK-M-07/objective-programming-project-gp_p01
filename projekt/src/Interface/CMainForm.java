package Interface;

import API.ApiCommunication;
import API.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ResourceBundle;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class CMainForm {
    public JPanel mainPanel;
    private JLabel cityLabel;
    private JLabel temperatureLabel;
    private JLabel weatherLabel;
    public JPanel topPanel;
    private JLabel humidityLabel;
    private JLabel pressureLabel;
    private JLabel windSpeedLabel;
    private JLabel rainChanceLabel;
    private JTextField searchField;
    private JButton searchButton;
    private JComboBox<String> daysComboBox;
    private JComboBox<String> tempHistoryComboBox;
    private Timer timer; // Timer do okresowego odświeżania lokalizacji
    private JPanel weatherDataPanel;
    private JPanel chartOptionsPanel;
    private JPanel chartPanel;
    private JPanel searchPanel;
    private JLabel daysLabel;
    private JLabel tempHistoryLabel;
    private JLabel chartLabel;
    private JPanel chart;

    private ApiCommunication apiCommunication;
    //private WeatherData weatherData;

    public CMainForm() {
        //weatherData = new WeatherData();  // inicjalizacja klasy z API
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        // Ustawienie tesktów z pliku zasobów
        cityLabel.setText(bundle.getString("cityLabel"));
        temperatureLabel.setText(bundle.getString("temperatureLabel"));
        weatherLabel.setText(bundle.getString("weatherLabel"));

        // Inicjalizacja głównego panelu
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(360, 640)); // px

        // Górny panel z miastem, temperaturą i pogodą
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 5, 10));

        cityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        temperatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        weatherLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        topPanel.add(cityLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(temperatureLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(weatherLabel);

        // Dodanie do głównego panelu
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // panel pogodowy: wilgotność, ciśnienie, wiatr, szansa na opady
        weatherDataPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        weatherDataPanel.setPreferredSize(new Dimension(360, 200));

        humidityLabel.setText("Wilgotność: 55%");
        humidityLabel.setBorder(BorderFactory.createEmptyBorder(50, 10, 0, 10));
        pressureLabel.setText("Ciśnienie: 1013 hPa");
        pressureLabel.setBorder(BorderFactory.createEmptyBorder(50, 10, 0, 10));
        windSpeedLabel.setText("Wiatr: 10 km/h N");
        windSpeedLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rainChanceLabel.setText("Szansa na opady: 40%");
        rainChanceLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        weatherDataPanel.add(humidityLabel);
        weatherDataPanel.add(pressureLabel);
        weatherDataPanel.add(windSpeedLabel);
        weatherDataPanel.add(rainChanceLabel);

        mainPanel.add(weatherDataPanel, BorderLayout.CENTER);
        weatherDataPanel.revalidate();
        weatherDataPanel.repaint();

        // Wybór dni do wykresu
        chartOptionsPanel.setLayout(new BoxLayout(chartOptionsPanel, BoxLayout.Y_AXIS));
        chartOptionsPanel.setPreferredSize(new Dimension(360, 150));

        daysLabel.setText("Wybierz kolejne dni do wykresu:");
        String[] forecastDays = new String[]{"3 dni", "5 dni", "8 dni"};
        daysComboBox.setModel(new DefaultComboBoxModel<>(forecastDays));
        tempHistoryLabel.setText("Wybierz zakres minionych dni:");
        String[] historyDays = new String[]{"3 dni", "5 dni", "7 dni"};
        tempHistoryComboBox.setModel(new DefaultComboBoxModel<>(historyDays));

        chartOptionsPanel.add(daysLabel);
        chartOptionsPanel.add(daysComboBox);
        chartOptionsPanel.add(tempHistoryLabel);
        chartOptionsPanel.add(tempHistoryComboBox);

        /*
        daysComboBox.addActionListener(e -> {
            String selectedDays = (String) daysComboBox.getSelectedItem();
            int days = Integer.parseInt(selectedDays.split(" ")[0]);

            // Pobieranie danych dla okreslonej liczny dni
            double[] temperatures = apiCommunication.getTemperatureForecast(days);
            String[] daysLabels = generateDayLabels(days);

            // Rysuj wykres
            createChart(temperatures, daysLabels);
        });
        */

        // Wykres zmiany temperatury
        chartPanel.setPreferredSize(new Dimension(360, 200));
        chartPanel.setBackground(Color.LIGHT_GRAY);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Wykres temperatury"));

        // panel, który zawiera opcje i wykres
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS)); // Układ pionowy
        southPanel.add(chartOptionsPanel);
        southPanel.add(Box.createVerticalStrut(10));
        southPanel.add(chartPanel);

        mainPanel.add(southPanel, BorderLayout.SOUTH);

        // Panel do wyszukiwania miasta
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchField.setColumns(15);
        searchButton.setText("Szukaj");

        // Obsługa przycisku "Szukaj"
        searchButton.addActionListener(e -> {
            String city = searchField.getText();
            if (!city.isEmpty()) {
                // Tworzenie obiektu ApiCommunication dla podanej lokalizacji
                apiCommunication = new ApiCommunication(city);

                try {
                    // Pobieranie danych o aktualnej pogodzie
                    String jsonResponse = apiCommunication.getCurrentWeather();

                    // Parsowanie odpowiedzi JSON na obiekt WeatherData
                    ObjectMapper objectMapper = new ObjectMapper();
                    WeatherData weatherData = objectMapper.readValue(jsonResponse, WeatherData.class);

                    // Aktualizowanie komponentów UI na podstawie pobranych danych
                    updateWeatherData(weatherData);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Błąd podczas pobierania danych pogodowych.");
                }
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        mainPanel.add(searchPanel, BorderLayout.PAGE_END);
    }

    // metoda do rysowanaia wykresu
    public void createChart(double[] temperatures, String[] days) {
        // Tworzenie zbioru danych
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < temperatures.length; i++) {
            dataset.addValue(temperatures[i], "Temperatura", days[i]);
        }

        // Tworzenie wykresu
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Wykres temperatury", // Tytuł wykresu
                "Dni",               // Oś X
                "Temperatura (°C)",  // Oś Y
                dataset              // Dane
        );

        // Panel z wykresem
        ChartPanel chartPanelComponent = new ChartPanel(lineChart);
        chartPanelComponent.setPreferredSize(new Dimension(360, 200));

        // Czyszczenie panelu i dodanie wykresu
        chartPanel.removeAll();
        chartPanel.add(chartPanelComponent);
        chartPanel.revalidate();
        chartPanel.repaint();
    }

    // Metoda do aktualizacji pogody na podstawie WeatherData
    private void updateWeatherData(WeatherData weatherData) {
        cityLabel.setText("Lokalizacja: " + weatherData.getName());
        temperatureLabel.setText("Temperatura: " + weatherData.getMain().getTemp() + "°C");
        weatherLabel.setText("Pogoda: " + weatherData.getWeather().get(0).getDescription());
        humidityLabel.setText("Wilgotność: " + weatherData.getMain().getHumidity() + "%");
        pressureLabel.setText("Ciśnienie: " + weatherData.getMain().getPressure() + " hPa");
        windSpeedLabel.setText("Wiatr: 12 km/h NE");
        rainChanceLabel.setText("Szansa na opady: 30%");
        /*
        } catch (IOException e) {
            showError("Błąd połączenia z serwerem. Spróbuj ponownie.");
        } catch (Exception e) {
            showError("Nie znaleziono danych dla podanego miasta.");
        }*/
    }

    // Obsługa błędów
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainPanel, message, "Błąd", JOptionPane.ERROR_MESSAGE));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    // Automatyczne pobieranie danych i aktualizacja etykiet
    private void startAutoUpdate() {
        timer = new Timer();

        // Zadanie wykonywane co 5 sekund
        TimerTask updateTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    String city = getCityFromCoordinates(); // Pobranie nowej lokalizacji
                    String newTemperature = getTemperature(); // Pobranie nowej temperatury
                    apiCommunication = new ApiCommunication(city);

                    /*
                    // Pobierz dane z API
                    String jsonResponse = apiCommunication.getCurrentWeather();
                    ObjectMapper objectMapper = new ObjectMapper();
                    WeatherData weatherData = objectMapper.readValue(jsonResponse, WeatherData.class);

                    // Aktualizacja etykiet
                    SwingUtilities.invokeLater(() -> updateWeatherData(weatherData));

                    */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // Uruchomienie timera: 0 ms opóźnienia, aktualizacja co 5 sekund
        timer.schedule(updateTask, 0, 5000);
    }

    // pobieranie nazwy miasta
    private String getCityFromCoordinates() {
        // pobieranie z API
        return "Warszawa";
    }

    // pobieranie temperatury
    private String getTemperature() {
        // pobieranie z API
        return String.valueOf((int) (Math.random() * 10 + 10));
    }

    // Zatrzymanie automatycznej aktualizacji przy zamknięciu okna
    public void stopAutoUpdate() {
        if (timer != null) {
            timer.cancel();
        }
    }

}
