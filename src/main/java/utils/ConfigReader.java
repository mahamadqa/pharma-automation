package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigReader {

	static Properties prop;
	protected static ConfigReader reader;

	public static Properties initProp() {
		prop = new Properties();
		try {
			FileInputStream configFile = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/config.properties");
			FileInputStream loginCred = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/loginCredentials.properties");

			prop.load(configFile);
			prop.load(loginCred);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

	public static String get(String key) {
		// Priority 1: System property passed via CLI or Jenkins (e.g. -Dbrowser=firefox)
		String sysProp = System.getProperty(key);
		if (sysProp != null && !sysProp.trim().isEmpty()) {
			return sysProp.trim();
		}

		if (prop == null) {
			initProp();
		}

		String value = prop.getProperty(key);

		if (value == null) {
			throw new RuntimeException("Property not found : " + key);
		}

		return value;
	}

	public static String getOrDefault(String key, String defaultValue) {
		try {
			return get(key);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static synchronized void setProperty(String key, String value) {
		if (key == null || value == null) {
			return;
		}

		// Priority 1: Update JVM System property
		System.setProperty(key, value);

		// Priority 2: Update in-memory Properties cache
		if (prop == null) {
			initProp();
		}
		if (prop != null) {
			prop.setProperty(key, value);
		}

		// Update DataManager in-memory cache if active
		DataManager.updatePropertyInMemory(key, value);

		// Priority 3: Persist to properties files on disk (preserving comments and layout)
		String baseDir = System.getProperty("user.dir");
		
		updatePropertyInFile(baseDir + "/src/test/resources/testData_SOP/sop_master_localization.properties", key, value);
		updatePropertyInFile(baseDir + "/src/test/resources/testData_SOP/sop_master_non-localization.properties", key, value);

		// Update compiled target directory copies if present
	
		updatePropertyInFile(baseDir + "/target/test-classes/testData_SOP/sop_master_localization.properties", key, value);
		updatePropertyInFile(baseDir + "/target/test-classes/testData_SOP/sop_master_non-localization.properties", key, value);
	}

	public static synchronized void updatePropertyInFile(String filePath, String key, String value) {
		File file = new File(filePath);
		if (!file.exists()) {
			return;
		}

		try {
			List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
			boolean found = false;
			List<String> updatedLines = new ArrayList<>();

			for (String line : lines) {
				String trimmed = line.trim();
				if (!trimmed.startsWith("#") && !trimmed.startsWith("!")
						&& (trimmed.startsWith(key + "=") || trimmed.startsWith(key + " ") || trimmed.startsWith(key + "\t") || trimmed.equals(key))) {
					updatedLines.add(key + "=" + value);
					found = true;
				} else {
					updatedLines.add(line);
				}
			}

			if (!found) {
				updatedLines.add(key + "=" + value);
			}

			Files.write(file.toPath(), updatedLines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.err.println("Failed to update property [" + key + "] in file [" + filePath + "]: " + e.getMessage());
		}
	}

	public static boolean headlessMode() {
		reader = new ConfigReader();
		if (ConfigReader.get("headless").equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}

	}

}
