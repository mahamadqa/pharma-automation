package utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

/**
 * Utility class to read test data from Excel files (.xlsx).
 * Supports caching and multiple sheets (e.g. SOP Contents, SOP Data).
 */
public class ExcelReader {

	private static final Logger log = LogManager.getLogger(ExcelReader.class);
	private static final String DEFAULT_EXCEL_PATH = System.getProperty("user.dir") + "/src/test/resources/testdata/sop_contents.xlsx";
	private static final Map<String, String> cache = new HashMap<>();

	/**
	 * Reads all key-value rows from the specified sheet in an Excel file.
	 */
	public static synchronized Map<String, String> getAllData(String filePath, String sheetName) {
		Map<String, String> data = new HashMap<>();
		File file = new File(filePath);
		if (!file.exists()) {
			log.warn("Excel file not found at: {}", filePath);
			return data;
		}

		try (FileInputStream fis = new FileInputStream(file);
				Workbook workbook = WorkbookFactory.create(fis)) {
			Sheet sheet = (sheetName != null && !sheetName.isEmpty()) ? workbook.getSheet(sheetName) : workbook.getSheetAt(0);
			if (sheet == null) {
				log.warn("Sheet [{}] not found in {}", sheetName, filePath);
				return data;
			}

			for (Row row : sheet) {
				if (row == null) continue;
				Cell keyCell = row.getCell(0);
				Cell valCell = row.getCell(1);
				if (keyCell == null || valCell == null) continue;

				String key = getCellValueAsString(keyCell).trim();
				String val = getCellValueAsString(valCell).trim();
				if (!key.isEmpty()) {
					data.put(key, val);
				}
			}
			log.info("Loaded [{}] test data entries from Excel sheet [{}] in {}", data.size(), sheetName != null ? sheetName : "default", file.getName());
		} catch (Exception e) {
			log.error("Failed to read Excel data from {}: {}", filePath, e.getMessage(), e);
		}
		return data;
	}

	/**
	 * Loads all sheets from the default SOP contents Excel file into the cache.
	 */
	public static synchronized void loadAllData() {
		File file = new File(DEFAULT_EXCEL_PATH);
		if (!file.exists()) {
			log.warn("Default Excel file not found at: {}", DEFAULT_EXCEL_PATH);
			return;
		}

		try (FileInputStream fis = new FileInputStream(file);
				Workbook workbook = WorkbookFactory.create(fis)) {
			int numberOfSheets = workbook.getNumberOfSheets();
			for (int i = 0; i < numberOfSheets; i++) {
				Sheet sheet = workbook.getSheetAt(i);
				for (Row row : sheet) {
					if (row == null) continue;
					Cell keyCell = row.getCell(0);
					Cell valCell = row.getCell(1);
					if (keyCell == null || valCell == null) continue;

					String key = getCellValueAsString(keyCell).trim();
					String val = getCellValueAsString(valCell).trim();
					if (!key.isEmpty() && !key.equalsIgnoreCase("Section") && !key.equalsIgnoreCase("Key")) {
						cache.put(key, val);
						cache.put(key.toLowerCase(), val);
						if (key.startsWith("sop.")) {
							String raw = key.substring(4);
							cache.put(raw, val);
							cache.put(raw.toLowerCase(), val);
						} else {
							cache.put("sop." + key, val);
							cache.put("sop." + key.toLowerCase(), val);
						}
						// Convenient aliases
						if (key.equalsIgnoreCase("AnnexureContent")) {
							cache.put("sop.annexure.content", val);
							cache.put("annexurecontent", val);
						}
						if (key.equalsIgnoreCase("FormatContent")) {
							cache.put("sop.format.content", val);
							cache.put("formatcontent", val);
						}
					}
				}
			}
			log.info("Total [{}] entries cached from Excel file: {}", cache.size(), file.getName());
		} catch (Exception e) {
			log.error("Error loading Excel file {}: {}", DEFAULT_EXCEL_PATH, e.getMessage(), e);
		}
	}

	/**
	 * Retrieves value by key from the cached Excel data.
	 */
	public static synchronized String get(String key) {
		if (cache.isEmpty()) {
			loadAllData();
		}
		if (key == null) return null;
		String val = cache.get(key);
		if (val == null) val = cache.get(key.toLowerCase());
		if (val == null && key.startsWith("sop.")) val = cache.get(key.substring(4));
		if (val == null && key.startsWith("sop.")) val = cache.get(key.substring(4).toLowerCase());
		if (val == null && !key.startsWith("sop.")) val = cache.get("sop." + key);
		if (val == null && !key.startsWith("sop.")) val = cache.get("sop." + key.toLowerCase());
		return val;
	}

	/**
	 * Retrieves value by key with a default fallback.
	 */
	public static String getOrDefault(String key, String defaultValue) {
		String val = get(key);
		return (val != null && !val.trim().isEmpty()) ? val : defaultValue;
	}

	/**
	 * Converts an Excel Cell to its String representation.
	 */
	private static String getCellValueAsString(Cell cell) {
		if (cell == null) return "";
		switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				if (DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue().toString();
				}
				double num = cell.getNumericCellValue();
				if (num == (long) num) {
					return String.valueOf((long) num);
				}
				return String.valueOf(num);
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			case FORMULA:
				try {
					return cell.getStringCellValue();
				} catch (Exception e) {
					return String.valueOf(cell.getNumericCellValue());
				}
			default:
				return "";
		}
	}
}
