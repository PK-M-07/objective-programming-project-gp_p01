package API;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ChartTest {
    public static void main(String[] args) {
        // Testowe dane
        double[] temperatures = {22, 23, 24, 25, 26};
        String[] days = {"Dzień 1", "Dzień 2", "Dzień 3", "Dzień 4", "Dzień 5"};

        // Utworzenie wykresu
        createChartTest(temperatures, days);
    }

    private static void createChartTest(double[] temperatures, String[] days) {
        // Sprawdzenie danych w konsoli
        System.out.println("Temperatures: " + Arrays.toString(temperatures));
        System.out.println("Days: " + Arrays.toString(days));

        // Tworzenie danych do wykresu
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < temperatures.length; i++) {
            dataset.addValue(temperatures[i], "Temperature", days[i]);
        }

        // Tworzymy wykres
        JFreeChart chart = ChartFactory.createLineChart(
                "Temperature Chart", // Tytuł wykresu
                "Days",              // Etykieta osi X
                "Temperature (°C)",  // Etykieta osi Y
                dataset,             // Dane
                org.jfree.chart.plot.PlotOrientation.VERTICAL,
                true,                // Legenda
                true,                // Tooltips
                false                // URLs
        );

        // Tworzymy okno JFrame
        JFrame frame = new JFrame("Wykres temperatur");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Tworzymy panel do wyświetlania wykresu
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(550, 300));

        // Dodajemy panel do okna
        frame.getContentPane().add(chartPanel, BorderLayout.CENTER);

        // Wyświetlamy okno
        frame.setVisible(true);
    }
}
