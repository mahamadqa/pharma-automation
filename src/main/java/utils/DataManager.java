package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataManager {

    private static final Logger log = LogManager.getLogger(DataManager.class);
    private static volatile Properties testData;
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

        File testDataDir = new File(System.getProperty("user.dir") + "/src/test/resources");
        if (!testDataDir.exists() || !testDataDir.isDirectory()) {
            throw new RuntimeException("Test data directory not found at: " + testDataDir.getAbsolutePath());
        }

        String activeRegion = region;
        List<File> matchingFiles = new ArrayList<>();
        collectMatchingFiles(testDataDir, activeRegion, matchingFiles);

        // Fallback to localization if non-localization files not found
        if (matchingFiles.isEmpty() && !activeRegion.equals("localization")) {
            log.warn("No test data files found for region: [{}]. Falling back to [localization].", activeRegion);
            activeRegion = "localization";
            region = "localization";
            collectMatchingFiles(testDataDir, activeRegion, matchingFiles);
        }

        if (matchingFiles.isEmpty()) {
            throw new RuntimeException("No test data properties files found for region: " + activeRegion);
        }

        // Sort so purchase_order / base files load first, then sales_order, then other module files
        matchingFiles.sort(Comparator.comparing(File::getName));

        Properties newTestData = new Properties();
        for (File mf : matchingFiles) {
            try (FileInputStream fis = new FileInputStream(mf)) {
                newTestData.load(fis);
                log.info("Loaded test data from {} (in {})", mf.getName(), mf.getParentFile().getName());
            } catch (IOException e) {
                log.warn("Failed to load test data from: {}", mf.getAbsolutePath(), e);
            }
        }

        currentRegion = region;
        testData = newTestData;

        return testData;
    }

    private static void collectMatchingFiles(File dir, String activeRegion, List<File> result) {
        if (dir == null || !dir.exists()) {
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        for (File f : files) {
            if (f.isDirectory()) {
                collectMatchingFiles(f, activeRegion, result);
            } else if (isMatchingPropertyFile(f.getName(), activeRegion)) {
                result.add(f);
            }
        }
    }

    private static boolean isMatchingPropertyFile(String fileName, String activeRegion) {
        String lower = fileName.toLowerCase();
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

    public static synchronized void updatePropertyInMemory(String key, String value) {
        if (testData != null && key != null && value != null) {
            testData.setProperty(key, value);
        }
    }

    public static synchronized void setProperty(String key, String value) {
        ConfigReader.setProperty(key, value);
    }
}
