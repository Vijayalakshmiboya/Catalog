import java.io.*;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONObject;

public class SecretFinder {
    
    // Function to convert a number from given base to decimal
    public static BigInteger decodeValue(String value, int base) {
        return new BigInteger(value, base);
    }

    // Function to perform Lagrange Interpolation and find constant term c
    public static BigInteger lagrangeInterpolation(List<long[]> points) {
        BigInteger c = BigInteger.ZERO;
        int k = points.size();

        for (int i = 0; i < k; i++) {
            BigInteger term = BigInteger.valueOf(points.get(i)[1]); // yi
            long xi = points.get(i)[0];

            // Compute Lagrange basis polynomial Li(0)
            for (int j = 0; j < k; j++) {
                if (i != j) {
                    long xj = points.get(j)[0];
                    term = term.multiply(BigInteger.valueOf(-xj))
                               .divide(BigInteger.valueOf(xi - xj));
                }
            }

            c = c.add(term);
        }

        return c;
    }

    // Function to read JSON, extract values, and compute secret c
    public static void processJSON(String filename) {
        try {
            // Read JSON file
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filename)));
            JSONObject root = new JSONObject(content);

            int n = root.getJSONObject("keys").getInt("n");
            int k = root.getJSONObject("keys").getInt("k");

            List<long[]> points = new ArrayList<>();

            for (String key : root.keySet()) {
                if (key.equals("keys")) continue; // Skip "keys" object
                
                long x = Long.parseLong(key);
                int base = root.getJSONObject(key).getInt("base");
                String value = root.getJSONObject(key).getString("value");

                BigInteger y = decodeValue(value, base);
                points.add(new long[]{x, y.longValue()});

                if (points.size() == k) break; // We only need k points
            }

            BigInteger secret = lagrangeInterpolation(points);
            System.out.println("Secret (c) from " + filename + ": " + secret);
        } catch (Exception e) {
            System.err.println("Error processing " + filename + ": " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        processJSON("testcase1.json");
        processJSON("testcase2.json");
    }
}