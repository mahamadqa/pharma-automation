package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {

	static Properties prop;
	protected static ConfigReader reader;

	public static Properties initProp() {
		prop = new Properties();
		try {
			// FileInputStream configFile = new
			// FileInputStream("src/test/resources/config.properties");
			// FileInputStream loginCred = new
			// FileInputStream("src/test/resources/loginCredentials.properties");

			FileInputStream configFile = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/config.properties");
			FileInputStream loginCred = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/loginCredentials.properties");
			FileInputStream Hersteller = new FileInputStream(
					System.getProperty("user.dir") + "/src/test/resources/Hersteller.properties");

			prop.load(configFile);
			prop.load(loginCred);
			prop.load(Hersteller);
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

	public static boolean headlessMode() {
		reader = new ConfigReader();
		if (ConfigReader.get("headless").equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}

	}

}
