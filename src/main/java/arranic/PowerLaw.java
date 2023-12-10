package arranic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerLaw {

    public static void main(String[] args) {
        // Example usage:
        Map<String, Integer> wordFrequencies = new HashMap<>();
        wordFrequencies.put("apple", 10);
        wordFrequencies.put("banana", 15);
        // Add more words and frequencies as needed

        // Fit the data to a power-law distribution
        PowerLawParameters params = fitPowerLaw(wordFrequencies);
        System.out.println("Power-law exponent: " + params.getExponent());
        System.out.println("Scaling factor: " + params.getScalingFactor());
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

    static double[] linearRegression(List<Double> xValues, List<Double> yValues) {
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
        double slope = (n * sumXY - sumX * sumY) / (n * sumXX - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;

        return new double[] { intercept, slope };
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
