package arranic;

import org.junit.jupiter.api.Test;
import arranic.PowerLaw;
import arranic.PowerLaw.PowerLawParameters;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerLawTest {

    @Test
    public void testFitPowerLaw() {
        Map<String, Integer> wordFrequencies = new HashMap<>();
        wordFrequencies.put("apple", 10);
        wordFrequencies.put("banana", 15);

        PowerLawParameters params = PowerLaw.fitPowerLaw(wordFrequencies);

        // Test the correctness of the fitting
        assertEquals(2.2239010857415096, params.getExponent(), 0.01); // Adjust the delta based on your expected
                                                                      // precision
        assertEquals(0.27, params.getScalingFactor(), 0.01); // Adjust the delta based on your expected precision
    }

    @Test
    public void testLinearRegression() {
        // Test the linear regression method with known input and output
        double[] xValues = { 1, 2, 3, 4, 5 };
        double[] yValues = { 2, 4, 5, 4, 5 };

        double[] coefficients = PowerLaw.linearRegression(toList(xValues), toList(yValues));

        assertEquals(0.6, coefficients[1], 0.01); // Adjust the delta based on your expected precision
        assertEquals(2.2, coefficients[0], 0.01); // Adjust the delta based on your expected precision
    }

    // Helper method to convert arrays to lists
    private List<Double> toList(double[] array) {
        List<Double> list = new ArrayList<>();
        for (double value : array) {
            list.add(value);
        }
        return list;
    }
}
