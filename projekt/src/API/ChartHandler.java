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

public class ChartHandler {
    private static JFreeChart chart; // Wykres
    private static ChartPanel chartPanel; // Panel wykresu
    private static String city = "Warsaw"; // Nazwa miasta

    /**
     * Tworzy wykres i dodaje go do panelu w głównym formularzu.
     *
     * @param mainPanel Panel, do którego zostanie dodany wykres.
     * @param forecastDays Liczba dni prognozy.
     */
    public static void createChartAndAddToPanel(JPanel mainPanel, int forecastDays) {
        try {
            // Inicjalizacja komunikacji z API
            ApiCommunication api = new ApiCommunication(city);

            // Pobranie prognozy temperatur
            double[] temperatures = api.getTemperatureForecast(forecastDays);
            String[] dates = generateDateLabels(forecastDays);

            // Tworzenie wykresu
            createTemperatureChart(temperatures, dates);

            // Dodanie wykresu do panelu
            mainPanel.removeAll(); // Usuwamy wszystko z panelu (jeśli coś było wcześniej)
            mainPanel.add(chartPanel, BorderLayout.CENTER); // Dodajemy wykres
            mainPanel.revalidate(); // Odświeżamy panel
            mainPanel.repaint(); // Rysujemy ponownie panel
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

        // Tworzenie panelu dla wykresu
        chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(750, 500));
    }
}
