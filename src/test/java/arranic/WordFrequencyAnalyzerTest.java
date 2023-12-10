package arranic;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import arranic.PowerLaw.PowerLawParameters;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WordFrequencyAnalyzerTest {

    @Test
    void testProcessWikimediaDump() throws IOException {
        // Mock the BufferedReader
        BufferedReader reader = Mockito.mock(BufferedReader.class);
        when(reader.readLine()).thenReturn("word1 word2", "word3 word4", null);

        // Call the method under test
        Map<String, Integer> result = WordFrequencyAnalyzer.processWikimediaDump(reader);

        // Verify that the expected word frequencies are present
        assertEquals(1, result.get("word1"));
        assertEquals(1, result.get("word2"));
        assertEquals(1, result.get("word3"));
        assertEquals(1, result.get("word4"));
        assertEquals(4, result.size());

        // Verify that the BufferedReader was closed
        verify(reader, times(1)).close();
    }

    @Test
    void testUpdateWordFrequencies() {
        Map<String, Integer> wordFrequencies = new HashMap<>();

        // Call the method under test
        WordFrequencyAnalyzer.updateWordFrequencies("word1 word2 word1", wordFrequencies);

        // Verify that the expected word frequencies are present
        assertEquals(2, wordFrequencies.get("word1"));
        assertEquals(1, wordFrequencies.get("word2"));
        assertEquals(2, wordFrequencies.size());
    }

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
}