package Interface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ResourceBundle;

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

    //klasa z api
    //private WeatherData weatherData;

    public CMainForm() {
        //weatherData = new WeatherData();  // inicjalizacja klasy z API
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        // Ustawienie tesktów z pliku zasobów
        cityLabel.setText(bundle.getString("cityLabel"));
        temperatureLabel.setText(bundle.getString("temperatureLabel"));
        weatherLabel.setText(bundle.getString("weatherLabel"));

        // Inicjalizacja głównego panelu
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setPreferredSize(new Dimension(360, 640)); // px

        // Górny panel z miastem, temperaturą i pogodą
        topPanel = new JPanel();
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
        weatherDataPanel = new JPanel();
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
        chartOptionsPanel = new JPanel();
        chartOptionsPanel.setLayout(new BoxLayout(chartOptionsPanel, BoxLayout.Y_AXIS));
        chartOptionsPanel.setPreferredSize(new Dimension(360, 150));

        daysLabel.setText("Wybierz kolejne dni do wykresu:");
        daysComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "3 dni", "5 dni", "7 dni" }));
        tempHistoryLabel.setText("Wybierz zakres minionych dni:");
        tempHistoryComboBox.setModel(new DefaultComboBoxModel<>(new String[] { "3 dni", "5 dni", "7 dni" }));

        chartOptionsPanel.add(daysLabel);
        chartOptionsPanel.add(daysComboBox);
        chartOptionsPanel.add(tempHistoryLabel);
        chartOptionsPanel.add(tempHistoryComboBox);

        // Wykres zmiany temperatury
        chartPanel = new JPanel();
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
        searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        searchField.setColumns(15);
        searchButton.setText("Szukaj");

        searchButton.addActionListener((ActionEvent e) -> {
            String city = searchField.getText().trim();
            if (!city.isEmpty()) {
                //fetchWeatherData(city);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Proszę wprowadzić nazwę miasta!", "Błąd", JOptionPane.ERROR_MESSAGE);
            }
        });

        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        mainPanel.add(searchPanel, BorderLayout.PAGE_END);
    }

    // Metoda do aktualizacji pogody na podstawie miasta
    private void updateWeatherData(String city) {
        /*try {
            // Pobieramy dane z API na podstawie wprowadzonego miasta
            WeatherData weatherData = weatherAPI.getWeatherByCity(city);
        */
        // Jeśli dane zostały pobrane, aktualizujemy UI
        cityLabel.setText("Lokalizacja: " + city);
        temperatureLabel.setText("Temperatura: 20°C");
        weatherLabel.setText("Pogoda: Słonecznie");
        humidityLabel.setText("Wilgotność: 55%");
        pressureLabel.setText("Ciśnienie: 1015 hPa");
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
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(mainPanel, message, "Błąd", JOptionPane.ERROR_MESSAGE);
        });
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
                String newLocation = getCityFromCoordinates(); // Pobranie nowej lokalizacji
                String newTemperature = getTemperature(); // Pobranie nowej temperatury

                // Aktualizacja etykiet
                SwingUtilities.invokeLater(() -> {
                    cityLabel.setText(newLocation);
                    temperatureLabel.setText(newTemperature + "°C");
                });
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
