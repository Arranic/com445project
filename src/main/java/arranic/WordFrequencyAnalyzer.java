package arranic;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;

public class WordFrequencyAnalyzer extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        String filePath = "C:\\Users\\Scotty Griffin\\OneDrive\\Documents\\Saint Leo School Work\\2023\\Fall 2\\COM-445 Software Quality Assurance\\final-project\\com445project\\reed.xml";

        File file = new File(filePath);
        if (!file.exists()) {
            System.err.println("ERROR: Path to data file does not exist.");
            System.exit(1);
        }

        try {
            // Step 1: Process the Wikimedia data dump line by line
            Map<String, Integer> wordFrequencies = processWikimediaDump(filePath);

            // Step 2: Draw a histogram of word frequencies using JavaFX
            PowerLawParameters params = fitPowerLaw(wordFrequencies);
            drawHistogram(wordFrequencies, params, stage);

            System.out.println("Power-law exponent: " + params.getExponent());
            System.out.println("Scaling factor: " + params.getScalingFactor());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static Map<String, Integer> processWikimediaDump(String filePath) throws IOException {
        Map<String, Integer> wordFrequencies = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Process each line and update word frequencies
                updateWordFrequencies(line, wordFrequencies);
            }
        }

        return wordFrequencies;
    }

    private static void updateWordFrequencies(String line, Map<String, Integer> wordFrequencies) {
        String[] words = line.split("\\s+"); // Split by whitespace

        for (String word : words) {
            // You may need more advanced preprocessing here
            word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();

            if (!word.isEmpty()) {
                wordFrequencies.put(word, wordFrequencies.get(word) + 1);
            }
        }
    }

    private static void drawHistogram(Map<String, Integer> wordFrequencies, PowerLawParameters params, Stage stage) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Word Frequency Histogram");

        xAxis.setLabel("Word");
        yAxis.setLabel("Frequency");

        XYChart.Series<String, Number> wordSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> powerLawSeries = new XYChart.Series<>();

        ObservableList<XYChart.Data<String, Number>> wordData = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<String, Number>> powerLawData = FXCollections.observableArrayList();

        // Add data to the series
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            wordData.add(new XYChart.Data<String, Number>(entry.getKey(), entry.getValue()));
        }

        // Add power-law distribution data to the series
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            double x = Math.log(entry.getKey().length());
            double y = params.getScalingFactor() * Math.pow(x, params.getExponent());
            powerLawData.add(new XYChart.Data<String, Number>(entry.getKey(), y));
        }

        wordSeries.setData(wordData);
        powerLawSeries.setData(powerLawData);

        barChart.getData().addAll(wordSeries, powerLawSeries);

        Scene scene = new Scene(barChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private static double[] linearRegression(List<Double> xValues, List<Double> yValues) {
        int n = xValues.size();

        // Calculate sums
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumXX = 0.0;

        for (int i = 0; i < n; i++) {
            sumX += xValues.get(i);
            sumY += yValues.get(i);
            sumXY += xValues.get(i) * yValues.get(i);
            sumXX += xValues.get(i) * xValues.get(i);
        }

        // Calculate linear regression coefficients (slope and intercept)
        double slopeDenom = n * sumXX - sumX * sumX;

        if (slopeDenom != 0) {
            double slope = (n * sumXY - sumX * sumY) / slopeDenom;
            double intercept = (sumY - slope * sumX) / n;
            return new double[] { intercept, slope };
        } else {
            System.out.println("ERROR: Cannot divide by 0.");
            return new double[0];
        }
    }

    public static PowerLawParameters fitPowerLaw(Map<String, Integer> wordFrequencies) {
        List<Double> logWordLengths = new ArrayList<>();
        List<Double> logFrequencies = new ArrayList<>();

        // Convert word frequencies to log-transformed lists
        for (Map.Entry<String, Integer> entry : wordFrequencies.entrySet()) {
            logWordLengths.add(Math.log(entry.getKey().length()));
            logFrequencies.add(Math.log(entry.getValue()));
        }

        // Calculate linear regression coefficients
        double[] regressionCoefficients = linearRegression(logWordLengths, logFrequencies);

        // Extract scaling factor and exponent from the coefficients
        double scalingFactor = Math.exp(regressionCoefficients[0]);
        double exponent = regressionCoefficients[1];

        return new PowerLawParameters(exponent, scalingFactor);
    }

    static class PowerLawParameters {
        private final double exponent;
        private final double scalingFactor;

        public PowerLawParameters(double exponent, double scalingFactor) {
            this.exponent = exponent;
            this.scalingFactor = scalingFactor;
        }

        public double getExponent() {
            return exponent;
        }

        public double getScalingFactor() {
            return scalingFactor;
        }
    }
}