package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reusable utility class for file and path operations across tests and page objects.
 */
public class FileUtils {

	private static final Logger log = LogManager.getLogger(FileUtils.class);

	private static final String[] DEFAULT_DMS_CANDIDATE_LOCATIONS = {
			"src/test/resources/testData_SOP",
			"src/test/resources/testData_Documents",
			"src/test/resources",
			"target/test-classes/testData_SOP",
			"target/test-classes/testData_Documents",
			"target/test-classes",
			"."
	};

	/**
	 * Dynamically resolves the DMS file path by checking candidate resource directories
	 * or searching the workspace.
	 *
	 * @param fileName Name or relative path of the DMS file (e.g. "Training_Completion_Report.pdf")
	 * @return Absolute path of the resolved DMS file, or candidate relative fallback if not found
	 */
	public static String getDynamicDmsFilePath(String fileName) {
		return getDynamicFilePath(fileName, DEFAULT_DMS_CANDIDATE_LOCATIONS);
	}

	/**
	 * Dynamically resolves a file path by checking given candidate directories
	 * and falling back to a deep walk of the workspace tree.
	 *
	 * @param fileName           Name or relative path of the file
	 * @param candidateLocations Candidate directory paths to check first
	 * @return Absolute path of the file if found, otherwise candidate relative fallback
	 */
	public static String getDynamicFilePath(String fileName, String... candidateLocations) {
		if (fileName == null || fileName.trim().isEmpty()) {
			throw new IllegalArgumentException("File name cannot be null or empty");
		}

		String cleanName = fileName.trim();

		// If file already exists as absolute or valid relative path
		File directFile = new File(cleanName);
		if (directFile.exists() && directFile.isFile()) {
			log.info("File [{}] found directly at: [{}]", cleanName, directFile.getAbsolutePath());
			return directFile.getAbsolutePath();
		}

		String targetName = directFile.getName();

		// 1. Check candidate locations
		if (candidateLocations != null) {
			for (String dir : candidateLocations) {
				File candidate = new File(dir, targetName);
				if (candidate.exists() && candidate.isFile()) {
					log.info("File [{}] found dynamically at: [{}]", targetName, candidate.getAbsolutePath());
					return candidate.getAbsolutePath();
				}
			}
		}

		// 2. Search dynamically across project tree if not found in candidate locations
		try (Stream<Path> stream = Files.walk(Paths.get("."))) {
			Optional<Path> match = stream
					.filter(Files::isRegularFile)
					.filter(p -> p.getFileName().toString().equalsIgnoreCase(targetName))
					.findFirst();
			if (match.isPresent()) {
				String resolved = match.get().toAbsolutePath().normalize().toString();
				log.info("File [{}] located dynamically via filesystem walk: [{}]", targetName, resolved);
				return resolved;
			}
		} catch (Exception e) {
			log.warn("Error while dynamically locating file [{}]: {}", targetName, e.getMessage());
		}

		String fallbackDir = (candidateLocations != null && candidateLocations.length > 0) ? candidateLocations[0] : ".";
		String fallbackPath = new File(fallbackDir, targetName).getPath();
		log.warn("File [{}] not found on disk, falling back to: [{}]", targetName, fallbackPath);
		return fallbackPath;
	}
}
