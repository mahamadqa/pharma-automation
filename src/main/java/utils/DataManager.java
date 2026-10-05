package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataManager {

    private static final Logger log = LogManager.getLogger(DataManager.class);
    private static Properties testData;
    private static String currentRegion;

    public static synchronized Properties initData() {
        // Read region from System property (-Dregion=localization or -Dregion=non-localization), fallback to 'localization'
        String region = System.getProperty("region", "localization").trim().toLowerCase();
        
        // Handle common aliases for backwards compatibility
        if (region.equals("india") || region.equals("local")) {
            region = "localization";
        } else if (region.equals("abroad") || region.equals("non_localization") || region.equals("nonlocalization") || region.equals("international")) {
            region = "non-localization";
        }

        // If data is already loaded for the current region, return it
        if (testData != null && region.equalsIgnoreCase(currentRegion)) {
            return testData;
        }

        testData = new Properties();
        currentRegion = region;

        String dataFilePath = System.getProperty("user.dir") 
                + "/src/test/resources/testdata/" + region + ".properties";

        File file = new File(dataFilePath);
        if (!file.exists()) {
            log.warn("Test data file not found at: {}. Falling back to localization.properties", dataFilePath);
            dataFilePath = System.getProperty("user.dir") + "/src/test/resources/testdata/localization.properties";
            currentRegion = "localization";
        }

        try (FileInputStream fis = new FileInputStream(dataFilePath)) {
            testData.load(fis);
            log.info("Loaded test data for region: [{}] from {}", currentRegion, dataFilePath);
        } catch (IOException e) {
            log.error("Failed to load test data from path: " + dataFilePath, e);
            throw new RuntimeException("Failed to load test data for region: " + region, e);
        }

        return testData;
    }

    public static String getData(String key) {
        if (testData == null) {
            initData();
        }

        // Check if overridden via CLI system property first
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }

        String value = testData.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Key [" + key + "] not found in test data for region [" + currentRegion + "]");
        }
        return value.trim();
    }

    public static String getRegion() {
        if (currentRegion == null) {
            initData();
        }
        return currentRegion;
    }
}
