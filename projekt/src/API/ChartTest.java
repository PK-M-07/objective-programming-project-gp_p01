package API;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ChartTest {
    private static JFreeChart chart; // Wykres
    private static ChartPanel chartPanel; // Panel wykresu
    private static String city = "Warsaw"; // Nazwa miasta
    private static JTextField cityInput; // Pole tekstowe do wprowadzania miasta

    public static void main(String[] args) {
        // Inicjalizacja okna
        JFrame frame = new JFrame("Wykres temperatur");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Panel do wyświetlania wykresu
        chartPanel = new ChartPanel(null); // Na początku wykres jest pusty
        chartPanel.setPreferredSize(new Dimension(750, 500));

        // Panel do przycisków
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Tworzenie przycisków
        JButton button3Days = new JButton("3 dni");
        JButton button5Days = new JButton("5 dni");
        JButton button8Days = new JButton("8 dni");
        JButton updateCityButton = new JButton("Zaktualizuj miasto");

        // Pole tekstowe do wprowadzania nazwy miasta
        cityInput = new JTextField(city, 15); // Domyślnie ustawione na "Warsaw"

        // Dodawanie akcji do przycisków
        button3Days.addActionListener(e -> updateChart(3));
        button5Days.addActionListener(e -> updateChart(5));
        button8Days.addActionListener(e -> updateChart(8));
        updateCityButton.addActionListener(e -> {
            city = cityInput.getText(); // Ustawienie nowego miasta
            updateChart(5); // Aktualizacja wykresu na 5 dni
        });

        // Dodawanie przycisków i pola tekstowego do panelu
        buttonPanel.add(cityInput);
        buttonPanel.add(updateCityButton);
        buttonPanel.add(button3Days);
        buttonPanel.add(button5Days);
        buttonPanel.add(button8Days);

        // Dodawanie komponentów do ramki
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);
        frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // Wyświetlanie okna
        frame.setVisible(true);

        // Inicjalne załadowanie wykresu
        updateChart(5); // Domyślnie 5 dni
    }

    /**
     * Aktualizuje wykres na podstawie liczby dni prognozy.
     *
     * @param forecastDays liczba dni prognozy
     */
    private static void updateChart(int forecastDays) {
        try {
            // Inicjalizacja komunikacji z API
            ApiCommunication api = new ApiCommunication(city);

            // Pobranie prognozy temperatur
            double[] temperatures = api.getTemperatureForecast(forecastDays);
            String[] dates = generateDateLabels(forecastDays);

            // Tworzenie wykresu
            createTemperatureChart(temperatures, dates);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Błąd podczas pobierania danych z API: " + e.getMessage(),
                    "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Generuje etykiety z datami dla wykresu.
     *
     * @param days liczba dni prognozy
     * @return tablica etykiet z datami
     */
    private static String[] generateDateLabels(int days) {
        String[] dateLabels = new String[days];
        LocalDate today = LocalDate.now(); // Aktualna data
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // Format daty

        for (int i = 0; i < days; i++) {
            LocalDate date = today.plusDays(i); // Kolejne dni
            dateLabels[i] = date.format(formatter); // Formatowanie daty do czytelnego formatu
        }
        return dateLabels;
    }
    /**
     * Tworzy wykres prognozy temperatur.
     *
     * @param temperatures tablica temperatur
     * @param dates        tablica dat (etykiety)
     */
    private static void createTemperatureChart(double[] temperatures, String[] dates) {
        // Tworzenie danych do wykresu
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < temperatures.length; i++) {
            dataset.addValue(temperatures[i], "Temperatura (°C)", dates[i]);
        }

        // Tworzenie wykresu liniowego
        chart = ChartFactory.createLineChart(
                "Prognoza temperatur dla miasta: " + city,
                "Data",
                "Temperatura (°C)",
                dataset,
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Konfiguracja stylu wykresu
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        // Dodanie punktów na wykresie
        renderer.setSeriesShapesVisible(0, true); // Pokazuje punkty na serii danych (seria 0)
        renderer.setSeriesLinesVisible(0, true);  // Linia pozostaje widoczna

        // Konfiguracja wyglądu punktów (np. kółka)
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0)); // Koło o średnicy 6 pikseli
        renderer.setSeriesPaint(0, Color.BLUE); // Ustawienie koloru linii i punktów

        plot.setRenderer(renderer);

        // Ustawianie nowego wykresu w panelu
        chartPanel.setChart(chart);
    }
}
