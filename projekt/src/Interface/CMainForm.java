package Interface;

import API.ApiCommunication;
import org.jfree.chart.axis.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;

import com.google.gson.JsonObject;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class CMainForm extends JPanel {
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
    private Timer timer; // Timer do okresowego odświeżania lokalizacji
    private JPanel weatherDataPanel;
    private JPanel chartOptionsPanel;
    private JPanel chartPanel;
    private JPanel searchPanel;
    private JLabel daysLabel;
    private JLabel chartLabel;
    private JPanel chart;
    private Image background;

    private ApiCommunication apiCommunication;

    /**
     * Konstruktor głównego formularza.
     * Inicjalizuje komponenty interfejsu oraz ustawia layout.
     */
    public CMainForm() {
        apiCommunication = new ApiCommunication("Krakow Polska");
        ResourceBundle bundle = ResourceBundle.getBundle("messages");

        cityLabel.setText(bundle.getString("cityLabel"));
        temperatureLabel.setText(bundle.getString("temperatureLabel"));
        weatherLabel.setText(bundle.getString("weatherLabel"));

        // Główny panel i jego layout (FlowLayout lub BoxLayout dla lepszej organizacji)
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(360, 640));  // Szerokość i wysokość okna
        mainPanel.setBackground(new Color(196, 224, 249));

        // Top Panel (miasto, temperatura, pogoda)
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
        mainPanel.add(topPanel);

        // Panel danych pogodowych (wilgotność, ciśnienie, wiatr, szansa na opady)
        weatherDataPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        weatherDataPanel.setPreferredSize(new Dimension(360, 200));

        humidityLabel.setText("Wilgotność: ...%");
        humidityLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        pressureLabel.setText("Ciśnienie: ... hPa");
        pressureLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
        windSpeedLabel.setText("Wiatr: ... m/s");
        windSpeedLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        rainChanceLabel.setText("Szansa na opady: ...%");
        rainChanceLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        weatherDataPanel.add(humidityLabel);
        weatherDataPanel.add(pressureLabel);
        weatherDataPanel.add(windSpeedLabel);
        weatherDataPanel.add(rainChanceLabel);

        mainPanel.add(weatherDataPanel);

        // Panel opcji wykresu
        chartOptionsPanel.setLayout(new BoxLayout(chartOptionsPanel, BoxLayout.Y_AXIS));
        chartOptionsPanel.setPreferredSize(new Dimension(360, 200));

        daysLabel.setText("Wybierz kolejne dni do wykresu:");
        String[] forecastDays = new String[]{"3 dni", "5 dni", "8 dni"};
        daysComboBox.setModel(new DefaultComboBoxModel<>(forecastDays));

        // Ustawienie koloru tła na biały dla ComboBox
        daysComboBox.setBackground(Color.WHITE);


        daysComboBox.addActionListener(e -> {
            String selectedDays = (String) daysComboBox.getSelectedItem();
            int days = Integer.parseInt(selectedDays.split(" ")[0]);

            // Pobieranie prognozy temperatur na wybrane dni
            if (apiCommunication != null) {
                try {
                    double[] temperatures = apiCommunication.getTemperatureForecast(days);
                    System.out.println("Temperatura: " + Arrays.toString(temperatures));

                    String[] dayLabels = generateDayLabels(days);
                    System.out.println("Dzień: " + Arrays.toString(dayLabels));

                    // Rysowanie wykresu
                    createChart(temperatures, dayLabels);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, "Błąd połączenia z API. Spróbuj ponownie.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        chartOptionsPanel.add(daysLabel);
        chartOptionsPanel.add(daysComboBox);

        // Dodajemy przycisk do rysowania wykresu
        JButton drawChartButton = new JButton("Rysuj wykres");
        drawChartButton.addActionListener(e -> {
            String selectedDays = (String) daysComboBox.getSelectedItem();
            int days = Integer.parseInt(selectedDays.split(" ")[0]);

            // Pobieranie temperatur na wybrane dni i rysowanie wykresu
            if (apiCommunication != null) {
                try {
                    double[] temperatures = apiCommunication.getTemperatureForecast(days);
                    String[] dayLabels = generateDayLabels(days);
                    createChart(temperatures, dayLabels);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(mainPanel, "Błąd połączenia z API. Spróbuj ponownie.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        chartOptionsPanel.add(drawChartButton);

        mainPanel.add(chartOptionsPanel);

        // Panel wykresu (panel, który zawiera wykres i opcje)
        chartPanel.setPreferredSize(new Dimension(500, 400));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        mainPanel.add(chartPanel);


        // Panel do wyszukiwania miasta
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
        mainPanel.add(searchPanel);
        this.add(mainPanel);
    }


    /**
     * Metoda do aktualizacji pogody na podstawie WeatherData.
     * Pobiera dane o aktualnej pogodzie z API i aktualizuje odpowiednie etykiety.
     */
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

    /**
     * Metoda do generowania etykiet dni w oparciu o liczbę dni.
     * @param days Liczba dni, dla których mają zostać wygenerowane etykiety.
     * @return Tablica z etykietami dni.
     */
    private String[] generateDayLabels(int days) {
        String[] labels = new String[days];
        for (int i = 0; i < days; i++) {
            labels[i] = getDateForDay(i);
        }
        return labels;
    }
    private String getDateForDay(int dayIndex) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, dayIndex);
        return new SimpleDateFormat("dd-MM").format(calendar.getTime()); // Użycie formatu DD-MM
    }


    /**
     * Metoda do wyświetlania komunikatu o błędzie.
     * @param message Wiadomość błędu do wyświetlenia.
     */
    private void showError(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(mainPanel, message, "Błąd", JOptionPane.ERROR_MESSAGE));
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }


    // Zatrzymanie automatycznej aktualizacji przy zamknięciu okna
    public void stopAutoUpdate() {
        if (timer != null) {
            timer.cancel();
        }
    }

    /**
     * Metoda do tworzenia wykresu na podstawie danych o temperaturze.
     * @param temperatures Tablica temperatur.
     * @param days Tablica etykiet dni.
     */
    private void createChart(double[] temperatures, String[] days) {
        // Utworzenie zbioru danych
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Temperatures");

        // Dodanie temperatur do serii
        for (int i = 0; i < temperatures.length; i++) {
            // Użyj milisekund od 1970 jako wartości X
            series.add(getDateForIndex(i).getTime(), temperatures[i]);
        }
        dataset.addSeries(series);

        // Utworzenie wykresu
        JFreeChart chart = ChartFactory.createXYLineChart(
                null,   // Brak tytułu wykresu
                "Data (DD-MM)",   // Etykieta osi X
                "Temperatura (°C)",   // Etykieta osi Y
                dataset, // Dane
                PlotOrientation.VERTICAL, // Orientacja wykresu
                false,  // Brak legendy
                true,   // Włącz tooltips
                false   // Brak URL
        );

        // Ustawienie koloru linii wykresu
        chart.getXYPlot().getRenderer().setSeriesPaint(0, new Color(0, 191, 255)); // Błękitna linia
        chart.getXYPlot().setBackgroundPaint(Color.WHITE); // Zmiana koloru tła wykresu na biały

        // Ustawienie kształtu punktów na linii temperatury
        chart.getXYPlot().getRenderer().setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6)); // Okrąg jako punkt

        // Ustawienie osi X jako DateAxis
        DateAxis domainAxis = new DateAxis();
        domainAxis.setDateFormatOverride(new SimpleDateFormat("dd-MM")); // Ustawienie formatu etykiet
        domainAxis.setLowerBound(getDateForIndex(0).getTime());
        domainAxis.setUpperBound(getDateForIndex(temperatures.length - 1).getTime());

        // Przypisanie DateAxis do wykresu
        chart.getXYPlot().setDomainAxis(domainAxis);

        // Utworzenie panelu wykresu
        ChartPanel chartPanelInstance = new ChartPanel(chart);
        this.chartPanel.removeAll();  // Usuwanie poprzedniego wykresu
        this.chartPanel.setLayout(new BorderLayout());
        this.chartPanel.add(chartPanelInstance, BorderLayout.CENTER);
        this.chartPanel.revalidate();  // Odświeżenie układu
        this.chartPanel.repaint();     // Rysowanie wykresu
    }


    // Metoda do generowania daty dla indeksu
    private Date getDateForIndex(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, index); // Dodajemy dni do bieżącej daty
        return calendar.getTime();
    }

}