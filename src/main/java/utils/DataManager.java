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

        File testDataDir = new File(System.getProperty("user.dir") + "/src/test/resources/testdata");
        if (!testDataDir.exists() || !testDataDir.isDirectory()) {
            throw new RuntimeException("Test data directory not found at: " + testDataDir.getAbsolutePath());
        }

        final String activeRegion = currentRegion;
        File[] matchingFiles = testDataDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            if (!lower.endsWith(".properties")) {
                return false;
            }
            String baseName = lower.substring(0, lower.length() - ".properties".length());
            if (activeRegion.equals("localization")) {
                return (baseName.endsWith("_localization") || baseName.equals("localization"))
                        && !baseName.endsWith("non-localization")
                        && !baseName.endsWith("non_localization");
            } else if (activeRegion.equals("non-localization")) {
                return baseName.endsWith("_non-localization")
                        || baseName.endsWith("-non-localization")
                        || baseName.endsWith("_non_localization")
                        || baseName.equals("non-localization")
                        || baseName.equals("non_localization");
            } else {
                return baseName.endsWith("_" + activeRegion) || baseName.equals(activeRegion);
            }
        });

        // Fallback to localization if non-localization files not found
        if ((matchingFiles == null || matchingFiles.length == 0) && !activeRegion.equals("localization")) {
            log.warn("No test data files found for region: [{}]. Falling back to [localization].", activeRegion);
            currentRegion = "localization";
            matchingFiles = testDataDir.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                if (!lower.endsWith(".properties")) {
                    return false;
                }
                String baseName = lower.substring(0, lower.length() - ".properties".length());
                return (baseName.endsWith("_localization") || baseName.equals("localization"))
                        && !baseName.endsWith("non-localization")
                        && !baseName.endsWith("non_localization");
            });
        }

        if (matchingFiles == null || matchingFiles.length == 0) {
            throw new RuntimeException("No test data properties files found for region: " + currentRegion);
        }

        // Sort so purchase_order / base files load first, then other module files
        java.util.Arrays.sort(matchingFiles, java.util.Comparator.comparing(File::getName));

        for (File mf : matchingFiles) {
            try (FileInputStream fis = new FileInputStream(mf)) {
                testData.load(fis);
                log.info("Loaded test data from {}", mf.getName());
            } catch (IOException e) {
                log.warn("Failed to load test data from: {}", mf.getAbsolutePath(), e);
            }
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

    public static String getData(String key, String fallbackKey) {
        if (testData == null) {
            initData();
        }

        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }

        String value = testData.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value.trim();
        }

        return getData(fallbackKey);
    }

    public static String getOrDefault(String key, String defaultValue) {
        try {
            return getData(key);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String getRegion() {
        if (currentRegion == null) {
            initData();
        }
        return currentRegion;
    }
}
