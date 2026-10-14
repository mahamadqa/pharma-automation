package utils;

/**
 * Model class representing a single failed assertion.
 * Encapsulates the assertion message, expected vs actual values,
 * caller code location (file and line), screenshot, and timestamp.
 */
public class FailedAssertion {

	private final int index;
	private final String message;
	private final Object expected;
	private final Object actual;
	private final String location;
	private final String screenshotBase64;
	private final String timestamp;

	public FailedAssertion(int index, String message, Object expected, Object actual,
			String location, String screenshotBase64, String timestamp) {
		this.index = index;
		this.message = message;
		this.expected = expected;
		this.actual = actual;
		this.location = location;
		this.screenshotBase64 = screenshotBase64;
		this.timestamp = timestamp;
	}

	public int getIndex() {
		return index;
	}

	public String getMessage() {
		return message;
	}

	public Object getExpected() {
		return expected;
	}

	public Object getActual() {
		return actual;
	}

	public String getLocation() {
		return location;
	}

	public String getScreenshotBase64() {
		return screenshotBase64;
	}

	public String getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "#" + index + " [" + message + "] Expected: [" + expected + "], Actual: [" + actual
				+ "] at " + location;
	}
}
