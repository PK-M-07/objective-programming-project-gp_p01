package Interface;

import API.ApiCommunication;
import API.WeatherData;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ResourceBundle;

import com.google.gson.JsonObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CMainForm extends JPanel{
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
    private Image background;

    private ApiCommunication apiCommunication;
    //private WeatherData weatherData;

    public CMainForm() {
        ResourceBundle bundle = ResourceBundle.getBundle("messages");
        // Ustawienie tesktów z pliku zasobów
        cityLabel.setText(bundle.getString("cityLabel"));
        temperatureLabel.setText(bundle.getString("temperatureLabel"));
        weatherLabel.setText(bundle.getString("weatherLabel"));

        // Inicjalizacja głównego panelu
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(360, 640)); // px
        mainPanel.setBackground(new Color(196, 224, 249));
        topPanel.setBackground(new Color(196, 224, 249));
        weatherDataPanel.setBackground(new Color(196, 224, 249));
        chartOptionsPanel.setBackground(new Color(196, 224, 249));
        chartPanel.setBackground(new Color(196, 224, 249));
        searchPanel.setBackground(new Color(196, 224, 249));

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
        pressureLabel.setText("Ciśnienie: 1010 hPa");
        pressureLabel.setBorder(BorderFactory.createEmptyBorder(50, 10, 0, 10));
        windSpeedLabel.setText("Wiatr: 4 m/s");
        windSpeedLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rainChanceLabel.setText("Szansa na opady: 0%");
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

        daysComboBox.addActionListener(e -> {
            String selectedDays = (String) daysComboBox.getSelectedItem();
            int days = Integer.parseInt(selectedDays.split(" ")[0]);

            // Pobieranie prognozy temperatur z API
            if (apiCommunication != null) {
                try {
                    double[] temperatures = apiCommunication.getTemperatureForecast(days);
                    System.out.println("Temperatures: " + Arrays.toString(temperatures));  // Dodaj logowanie danych

                    String[] dayLabels = generateDayLabels(days); // Generowanie etykiet dni (np. Dzień 1, Dzień 2...)

                    // Rysowanie wykresu
                    createChart(temperatures, dayLabels);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, "Błąd połączenia z API. Spróbuj ponownie.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        /*
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

        daysComboBox.addActionListener(e -> {
            String selectedDays = (String) daysComboBox.getSelectedItem();
            int days = Integer.parseInt(selectedDays.split(" ")[0]);

            // Pobieranie prognozy temperatur na wybrane dni
            if (apiCommunication != null) {
                try {
                    double[] temperatures = apiCommunication.getTemperatureForecast(days);
                    System.out.println("Temperatures: " + Arrays.toString(temperatures));  // Dodaj logowanie danych

                    String[] dayLabels = generateDayLabels(days); // Generowanie etykiet dni (np. Dzień 1, Dzień 2...)
                    System.out.println("Day Labels: " + Arrays.toString(dayLabels));  // Logowanie etykiet dni

                    // Rysowanie wykresu
                    createChart(temperatures, dayLabels);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, "Błąd połączenia z API. Spróbuj ponownie.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        */

        // Panel z wyszukiwaniem miasta
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchField.setColumns(15);
        searchButton.setText("Szukaj");

        searchButton.addActionListener(e -> {
            String city = searchField.getText();
            if (!city.isEmpty()) {
                apiCommunication = new ApiCommunication(city);
                updateWeatherData();
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        mainPanel.add(searchPanel, BorderLayout.PAGE_END);

        // Wykres zmiany temperatury
        chartPanel.setPreferredSize(new Dimension(350, 250));
        chartPanel.setBackground(Color.LIGHT_GRAY);
        chartPanel.setBorder(BorderFactory.createTitledBorder("Wykres temperatury"));

        // panel, który zawiera opcje i wykres
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS)); // Układ pionowy
        southPanel.add(chartOptionsPanel);
        southPanel.add(Box.createVerticalStrut(10));
        southPanel.add(chartPanel);

        mainPanel.add(southPanel, BorderLayout.SOUTH);
        mainPanel.add(chartPanel, BorderLayout.SOUTH);

    }

    // Metoda do aktualizacji pogody na podstawie WeatherData
    private void updateWeatherData() {
        try {
            // Pobieranie danych o aktualnej pogodzie
            JsonObject jsonResponseObject = apiCommunication.getCurrentWeather();

            // Sprawdzenie, czy "location" istnieje w odpowiedzi
            if (jsonResponseObject.has("location") && !jsonResponseObject.get("location").isJsonNull()) {
                JsonObject locationObject = jsonResponseObject.getAsJsonObject("location");
                if (locationObject.has("name") && !locationObject.get("name").isJsonNull()) {
                    String cityName = locationObject.get("name").getAsString();
                    cityLabel.setText("Lokalizacja: " + cityName);
                } else {
                    showError("Brak danych o nazwie miasta.");
                }
            } else {
                showError("Brak danych o lokalizacji.");
                return;
            }

            if (jsonResponseObject.has("current") && !jsonResponseObject.get("current").isJsonNull()) {
                JsonObject currentObject = jsonResponseObject.getAsJsonObject("current");
                if (currentObject.has("temp_c") && !currentObject.get("temp_c").isJsonNull()) {
                    int temperature = currentObject.get("temp_c").getAsInt();
                    temperatureLabel.setText("Temperatura: " + temperature + "°C");
                } else {
                    showError("Brak danych o temperaturze.");
                }

                if (currentObject.has("condition") && !currentObject.get("condition").isJsonNull()) {
                    String weatherDescription = currentObject.getAsJsonObject("condition").get("text").getAsString();
                    weatherLabel.setText("Pogoda: " + weatherDescription);
                } else {
                    showError("Brak danych o warunkach pogodowych.");
                }

                // Wilgotność
                if (currentObject.has("humidity") && !currentObject.get("humidity").isJsonNull()) {
                    int humidity = currentObject.get("humidity").getAsInt();
                    humidityLabel.setText("Wilgotność: " + humidity + "%");
                } else {
                    showError("Brak danych o wilgotności.");
                }

                // Prędkość wiatru
                if (currentObject.has("wind_kph") && !currentObject.get("wind_kph").isJsonNull()) {
                    double windSpeed = currentObject.get("wind_kph").getAsDouble();
                    windSpeedLabel.setText("Wiatr: " + windSpeed + " km/h");
                } else {
                    showError("Brak danych o prędkości wiatru.");
                }

                // Ciśnienie atmosferyczne
                if (currentObject.has("pressure_mb") && !currentObject.get("pressure_mb").isJsonNull()) {
                    double pressure = currentObject.get("pressure_mb").getAsDouble();
                    pressureLabel.setText("Ciśnienie: " + pressure + " hPa");
                } else {
                    showError("Brak danych o ciśnieniu.");
                }

                // Szansa na opady
                if (currentObject.has("precip_mm") && !currentObject.get("precip_mm").isJsonNull()) {
                    double precipitation = currentObject.get("precip_mm").getAsDouble();
                    rainChanceLabel.setText("Opady: " + precipitation + " mm");
                } else if (currentObject.has("precip_probability") && !currentObject.get("precip_probability").isJsonNull()) {
                    int rainChance = currentObject.get("precip_probability").getAsInt();
                    rainChanceLabel.setText("Szansa na opady: " + rainChance + "%");
                } else {
                    showError("Brak danych o opadach.");
                }

            } else {
                showError("Brak danych o bieżącej pogodzie.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błędna nazwa miasta!");
        }
    }

    private String[] generateDayLabels(int days) {
        String[] labels = new String[days];
        for (int i = 0; i < days; i++) {
            labels[i] = "Dzień " + (i + 1);  // Możesz tutaj dodać bardziej zaawansowane etykiety z datami
        }
        return labels;
    }

    // Metoda dodająca wykres do GUI
    private void createChart(double[] temperatures, String[] days) {
        // Sprawdzamy dane przed rysowaniem wykresu
        System.out.println("Temperatures: " + Arrays.toString(temperatures));  // Sprawdzenie danych
        System.out.println("Days: " + Arrays.toString(days));  // Sprawdzenie dni

        // Tworzymy dane do wykresu
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Temperatures");

        // Dodawanie danych do wykresu
        for (int i = 0; i < temperatures.length; i++) {
            series.add(i, temperatures[i]);
        }
        dataset.addSeries(series);

        // Tworzymy wykres
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Temperature Chart",   // Tytuł wykresu
                "Days",                // Etykieta osi X
                "Temperature (°C)",    // Etykieta osi Y
                dataset,               // Dane
                PlotOrientation.VERTICAL, // Orientacja wykresu
                true,                  // Legenda
                true,                  // Tooltips
                false                  // URLs
        );

        // Tworzymy ChartPanel i dodajemy go do chartPanel w CMainForm
        ChartPanel chartPanelInstance = new ChartPanel(chart);

        // Użycie GridBagConstraints do dodania wykresu do panelu
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;  // Ustawienie wypełnienia
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        this.chartPanel.removeAll();  // Usuwamy poprzedni wykres
        this.chartPanel.setLayout(new GridBagLayout());  // Ustawienie layoutu GridBagLayout
        this.chartPanel.add(chartPanelInstance, gbc);  // Dodanie wykresu do panelu z użyciem GridBagConstraints
        this.chartPanel.revalidate();  // Odświeżamy układ panelu
        this.chartPanel.repaint();     // Rysowanie panelu
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
