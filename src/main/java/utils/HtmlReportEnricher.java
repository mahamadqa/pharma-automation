package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Enriches the ExtentReport HTML output:
 * 1. Places "Test Classes Executive Summary" as the FIRST option in the side toolbar and default open view.
 * 2. Restricts System/Environment section strictly to the 10 requested fields without duplicates.
 * 3. Restricts Tags section strictly to TestNG group names (removes class names).
 * 4. Injects Executive Metric Cards & Class Breakdown Table.
 */
public class HtmlReportEnricher {

	private static final Logger log = LogManager.getLogger(HtmlReportEnricher.class);

	private static final String SUMMARY_SECTION_START = "<!-- TEST_CLASS_SUMMARY_START -->";
	private static final String SUMMARY_SECTION_END = "<!-- TEST_CLASS_SUMMARY_END -->";

	private HtmlReportEnricher() {
		// Utility class
	}

	public static void main(String[] args) {
		String reportPath = (args != null && args.length > 0) ? args[0] : "test-output/ExtentReport.html";
		boolean success = enrich(reportPath);
		System.out.println("Enrichment status for " + reportPath + ": " + (success ? "SUCCESS" : "FAILED"));
	}

	/**
	 * Enriches the ExtentReport HTML file at the specified path.
	 */
	public static synchronized boolean enrich(String htmlFilePath) {
		if (htmlFilePath == null || htmlFilePath.trim().isEmpty()) {
			return false;
		}

		Path path = Paths.get(htmlFilePath);
		if (!Files.exists(path)) {
			log.warn("Report file does not exist for enrichment: {}", htmlFilePath);
			return false;
		}

		try {
			String content = Files.readString(path, StandardCharsets.UTF_8);

			List<TestClassTracker.ClassSummary> summaries = new ArrayList<>(TestClassTracker.getClassSummaries());

			// Fallback 1: If TestClassTracker is empty, parse from test-output/testng-results.xml
			if (summaries.isEmpty()) {
				Path testngXml = path.getParent() != null ? path.getParent().resolve("testng-results.xml") : Paths.get("test-output/testng-results.xml");
				summaries = parseClassesFromTestngXml(testngXml);
			}

			// Fallback 2: If still empty, parse classes from the existing HTML report
			if (summaries.isEmpty()) {
				summaries = parseClassesFromHtml(content);
			}

			if (summaries.isEmpty()) {
				log.info("No test classes found to summarize in {}", htmlFilePath);
				return false;
			}

			int totalClasses = summaries.size();
			int passedClasses = 0;
			int failedClasses = 0;
			int skippedClasses = 0;

			for (TestClassTracker.ClassSummary cs : summaries) {
				String status = cs.getStatus();
				if ("PASS".equalsIgnoreCase(status)) {
					passedClasses++;
				} else if ("FAIL".equalsIgnoreCase(status)) {
					failedClasses++;
				} else if ("SKIP".equalsIgnoreCase(status)) {
					skippedClasses++;
				}
			}

			double passRate = totalClasses > 0 ? ((double) passedClasses / totalClasses) * 100.0 : 0.0;

			String summaryHtml = buildSummarySectionHtml(summaries, totalClasses, passedClasses, failedClasses, skippedClasses, passRate);
			String enhancedCss = getEnhancedCss();

			// 1. Inject or update the Summary Section in the Dashboard View
			String updatedContent = injectSummarySection(content, summaryHtml);

			// 2. Clean Tags Section: keep only group names
			updatedContent = cleanTagsSection(updatedContent, summaries);

			// 3. Keep strictly the 10 requested fields in System/Environment section
			updatedContent = injectSystemInfo(updatedContent, totalClasses, passedClasses, failedClasses, skippedClasses, passRate);

			// 4. Inject Header summary pill
			updatedContent = injectHeaderPill(updatedContent, totalClasses, passedClasses, failedClasses, skippedClasses);

			// 5. Reorder Views and Navigation: Make Executive Summary / Dashboard first option in side toolbar & default open view
			updatedContent = reorderViewsAndNavigation(updatedContent);

			// 6. Inject Enhanced CSS
			updatedContent = injectEnhancedCss(updatedContent, enhancedCss);

			Files.writeString(path, updatedContent, StandardCharsets.UTF_8);
			log.info("ExtentReport successfully enriched: {} total classes, {} passed, {} failed, {} skipped",
					totalClasses, passedClasses, failedClasses, skippedClasses);
			return true;
		} catch (IOException e) {
			log.error("Failed to enrich ExtentReport HTML: {}", e.getMessage(), e);
			return false;
		}
	}

	private static String injectSummarySection(String content, String summaryHtml) {
		// If already present, replace existing summary section
		if (content.contains(SUMMARY_SECTION_START) && content.contains(SUMMARY_SECTION_END)) {
			int start = content.indexOf(SUMMARY_SECTION_START);
			int end = content.indexOf(SUMMARY_SECTION_END) + SUMMARY_SECTION_END.length();
			return content.substring(0, start) + summaryHtml + content.substring(end);
		}

		// Otherwise insert right after <div class="container-fluid p-4 view dashboard-view">
		String target = "<div class=\"container-fluid p-4 view dashboard-view\">";
		int idx = content.indexOf(target);
		if (idx != -1) {
			int insertionPoint = idx + target.length();
			return content.substring(0, insertionPoint) + "\n" + summaryHtml + content.substring(insertionPoint);
		}

		return content;
	}

	/**
	 * Strictly formats the System/Environment section to keep only the requested 10 fields:
	 * Total Test Classes, Test Classes Passed, Test Classes Failed, Test Classes Skipped,
	 * Class Pass Rate, OS, Java Version, User, Browser, Region / Profile.
	 */
	private static String injectSystemInfo(String content, int total, int passed, int failed, int skipped, double passRate) {
		String sysEnvHeader = "<div class=\"card-header\"><p>System/Environment</p></div>";
		int headerIdx = content.indexOf(sysEnvHeader);
		if (headerIdx == -1) {
			return content;
		}

		int tbodyStart = content.indexOf("<tbody>", headerIdx);
		int tbodyEnd = content.indexOf("</tbody>", tbodyStart);
		if (tbodyStart == -1 || tbodyEnd == -1) {
			return content;
		}

		String existingTbody = content.substring(tbodyStart, tbodyEnd + "</tbody>".length());

		String os = extractEnvValue(existingTbody, "OS", System.getProperty("os.name"));
		String javaVersion = extractEnvValue(existingTbody, "Java Version", System.getProperty("java.version"));
		String user = extractEnvValue(existingTbody, "User", ExtentManager.getReportUser());
		user = capitalizeWord(user);
		String jenkinsBuild = extractEnvValue(existingTbody, "Jenkins Build No", ExtentManager.getJenkinsBuildNo());
		if ("N/A (Local Run)".equals(jenkinsBuild)) {
			String alt = extractEnvValue(existingTbody, "Jenkins Build", "");
			if (!alt.isEmpty()) {
				jenkinsBuild = alt;
			}
		}
		String browser = extractEnvValue(existingTbody, "Browser", "Chrome");
		browser = capitalizeWord(browser);
		String region = extractEnvValue(existingTbody, "Region / Profile", "Localization");
		region = capitalizeWord(region);

		StringBuilder sb = new StringBuilder();
		sb.append("<tbody>\n");
		sb.append("<tr><td>OS</td><td>").append(os).append("</td></tr>\n");
		sb.append("<tr><td>Java Version</td><td>").append(javaVersion).append("</td></tr>\n");
		sb.append("<tr><td>User</td><td>").append(user).append("</td></tr>\n");
		sb.append("<tr><td>Jenkins Build No</td><td>").append(jenkinsBuild).append("</td></tr>\n");
		sb.append("<tr><td>Browser</td><td>").append(browser).append("</td></tr>\n");
		sb.append("<tr><td>Region / Profile</td><td>").append(region).append("</td></tr>\n");
		sb.append("<tr><td>Total Test Classes</td><td>").append(total).append("</td></tr>\n");
		sb.append("<tr><td>Test Classes Passed</td><td>").append(passed).append("</td></tr>\n");
		sb.append("<tr><td>Test Classes Failed</td><td>").append(failed).append("</td></tr>\n");
		sb.append("<tr><td>Test Classes Skipped</td><td>").append(skipped).append("</td></tr>\n");
		sb.append("<tr><td>Class Pass Rate</td><td>").append(String.format("%.1f%%", passRate)).append("</td></tr>\n");
		sb.append("</tbody>");

		return content.substring(0, tbodyStart) + sb.toString() + content.substring(tbodyEnd + "</tbody>".length());
	}

	public static String capitalizeWord(String str) {
		if (str == null || str.trim().isEmpty()) {
			return str;
		}
		str = str.trim();
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}

	private static String extractEnvValue(String html, String key, String defaultVal) {
		Pattern p = Pattern.compile("<td>" + Pattern.quote(key) + "</td>\\s*<td>(?:<span[^>]*>)?([^<]+)(?:</span>)?</td>", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(html);
		if (m.find()) {
			String val = m.group(1).trim();
			if (!val.isEmpty()) {
				return val;
			}
		}
		return defaultVal;
	}

	/**
	 * Filters out test class names from the Tags and Category sections, keeping strictly Group names.
	 * Also ensures that all groups (basic, advance, sop) from the executed test suite are accurately represented.
	 */
	private static String cleanTagsSection(String content, List<TestClassTracker.ClassSummary> summaries) {
		Set<String> classNames = new HashSet<>();
		for (TestClassTracker.ClassSummary cs : summaries) {
			classNames.add(cs.getSimpleClassName());
			classNames.add(cs.getFullClassName());
		}
		classNames.add("VerificationOfSOPMasterFlow");
		classNames.add("VerifySOShipmentInvoiceFlow");
		classNames.add("CreateGRNFromPOFlow");
		classNames.add("VerificationOfSOPRequestAndIssueFlowControlled");
		classNames.add("VerificationOfSOPRequestAndIssueFlowForDisplay");
		classNames.add("VerificationOfSOPFormatRequestAndIssueFlow");
		classNames.add("VerifySOPMasterFlowIfEffectivenessCheckRequired");
		classNames.add("VerificationOfSOPAnnexureRequestAndIssueFlow");
		classNames.add("CustomSoftAssertTest");
		classNames.add("DataManagerTest");

		// Group statistics map
		java.util.Map<String, List<TestClassTracker.ClassSummary>> groupMap = new java.util.LinkedHashMap<>();
		groupMap.put("advance", new ArrayList<>());
		groupMap.put("basic", new ArrayList<>());
		groupMap.put("sop", new ArrayList<>());

		for (TestClassTracker.ClassSummary cs : summaries) {
			String name = cs.getSimpleClassName();
			if ("CreateGRNFromPOFlow".equals(name) || "VerifySOShipmentInvoiceFlow".equals(name)) {
				groupMap.get("basic").add(cs);
			} else {
				groupMap.get("advance").add(cs);
				groupMap.get("sop").add(cs);
			}
		}

		// 1. Clean up Dashboard Tags Table: strictly keep group names only
		int tagsHeader = content.indexOf("<div class=\"card-header\"><p>Tags</p></div>");
		if (tagsHeader != -1) {
			int tbodyStart = content.indexOf("<tbody>", tagsHeader);
			int tbodyEnd = content.indexOf("</tbody>", tbodyStart);
			if (tbodyStart != -1 && tbodyEnd != -1) {
				StringBuilder cleanedRows = new StringBuilder();
				for (java.util.Map.Entry<String, List<TestClassTracker.ClassSummary>> entry : groupMap.entrySet()) {
					String groupName = entry.getKey();
					List<TestClassTracker.ClassSummary> list = entry.getValue();
					if (list.isEmpty()) {
						continue;
					}
					int total = list.size();
					int p = 0, f = 0, s = 0;
					for (TestClassTracker.ClassSummary cs : list) {
						if ("PASS".equalsIgnoreCase(cs.getStatus())) p++;
						else if ("FAIL".equalsIgnoreCase(cs.getStatus())) f++;
						else s++;
					}
					String passPct = total > 0 ? String.format("%.0f%%", ((double) p / total) * 100.0) : "0%";
					cleanedRows.append("<tr>\n")
							.append("<td>").append(groupName).append("</td>\n")
							.append("<td>").append(p).append("</td>\n")
							.append("<td>").append(f).append("</td>\n")
							.append("<td>").append(s).append("</td>\n")
							.append("<td>0</td>\n")
							.append("<td>").append(passPct).append("</td>\n")
							.append("</tr>\n");
				}

				content = content.substring(0, tbodyStart + "<tbody>".length()) + "\n" + cleanedRows.toString() + content.substring(tbodyEnd);
			}
		}

		// 2. Clean up Category View: Keep strictly group names
		int catStart = content.indexOf("view category-view");
		if (catStart != -1) {
			int catEnd = content.indexOf("view author-view", catStart);
			if (catEnd == -1) {
				catEnd = content.indexOf("view dashboard-view", catStart);
			}
			if (catEnd != -1) {
				String catView = content.substring(catStart, catEnd);
				int listWrapperStart = catView.indexOf("<ul class=\"test-list-item\">");
				int listWrapperEnd = catView.indexOf("</ul>", listWrapperStart);
				if (listWrapperStart != -1 && listWrapperEnd != -1) {
					List<ExtentTestRecord> records = extractTestRecordsFromTestView(content);

					// Fallback if test-view did not yield records
					if (records.isEmpty() && summaries != null) {
						int idCounter = 1;
						for (TestClassTracker.ClassSummary cs : summaries) {
							ExtentTestRecord rec = new ExtentTestRecord();
							rec.id = String.valueOf(idCounter++);
							rec.name = cs.getSimpleClassName() + " - " + cs.getTestedMethodsDisplay();
							rec.status = "PASS".equalsIgnoreCase(cs.getStatus()) ? "pass" : ("FAIL".equalsIgnoreCase(cs.getStatus()) ? "fail" : "skip");
							rec.timestamp = new java.text.SimpleDateFormat("HH:mm:ss a").format(new java.util.Date());
							String cName = cs.getSimpleClassName();
							if ("CreateGRNFromPOFlow".equals(cName) || "VerifySOShipmentInvoiceFlow".equals(cName)) {
								rec.tags.add("basic");
							} else {
								rec.tags.add("advance");
								rec.tags.add("sop");
							}
							records.add(rec);
						}
					}

					StringBuilder cleanedItems = new StringBuilder();
					int validGroupCount = 0;

					for (String groupName : groupMap.keySet()) {
						List<ExtentTestRecord> groupTests = new ArrayList<>();
						for (ExtentTestRecord r : records) {
							if (r.tags.contains(groupName.toLowerCase())) {
								groupTests.add(r);
							} else if (r.tags.isEmpty()) {
								if ("basic".equalsIgnoreCase(groupName) && (r.name.contains("CreateGRN") || r.name.contains("ShipmentInvoice"))) {
									groupTests.add(r);
								} else if (!"basic".equalsIgnoreCase(groupName) && !r.name.contains("CreateGRN") && !r.name.contains("ShipmentInvoice")) {
									groupTests.add(r);
								}
							}
						}

						if (groupTests.isEmpty()) {
							continue;
						}
						validGroupCount++;

						int p = 0, f = 0, s = 0;
						for (ExtentTestRecord r : groupTests) {
							if ("pass".equalsIgnoreCase(r.status)) p++;
							else if ("fail".equalsIgnoreCase(r.status)) f++;
							else s++;
						}

						String metaBadge;
						if (f > 0) {
							metaBadge = "<span class='badge log badge-danger'>" + f + "</span>";
						} else if (p > 0) {
							metaBadge = "<span class='badge log pass-bg'>" + p + "</span>";
						} else {
							metaBadge = "<span class='badge log skip-bg'>" + s + "</span>";
						}

						StringBuilder infoStatus = new StringBuilder();
						if (f > 0) {
							infoStatus.append("<span status=\"fail\" class='badge log badge-danger'>").append(f).append(" failed</span> ");
						}
						if (p > 0) {
							infoStatus.append("<span status=\"pass\" class='badge log pass-bg'>").append(p).append(" passed</span> ");
						}
						if (s > 0) {
							infoStatus.append("<span status=\"skip\" class='badge log skip-bg'>").append(s).append(" skipped</span>");
						}

						StringBuilder tableRows = new StringBuilder();
						for (ExtentTestRecord r : groupTests) {
							String badgeClass = "pass".equalsIgnoreCase(r.status) ? "pass-bg" : ("fail".equalsIgnoreCase(r.status) ? "fail-bg" : "skip-bg");
							String label = "pass".equalsIgnoreCase(r.status) ? "Pass" : ("fail".equalsIgnoreCase(r.status) ? "Fail" : "Skip");
							String rowStatus = "pass".equalsIgnoreCase(r.status) ? "pass" : ("fail".equalsIgnoreCase(r.status) ? "fail" : "skip");

							tableRows.append("<tr class=\"tag-test-status\" status=\"").append(rowStatus).append("\">")
									.append("<td><span class=\"badge log ").append(badgeClass).append("\">").append(label).append("</span></td>")
									.append("<td>").append(r.timestamp.isEmpty() ? "-" : r.timestamp).append("</td>")
									.append("<td><a href=\"#\" class=\"linked\" test-id='").append(r.id).append("' id='").append(r.id).append("'>")
									.append(r.name).append("</a></td>")
									.append("</tr>\n");
						}

						cleanedItems.append("<li class=\"test-item\">\n")
								.append("<div class=\"test-detail\">\n")
								.append("<span class=\"meta\">\n").append(metaBadge).append("\n</span>\n")
								.append("<p class=\"name\">").append(groupName).append("</p>\n")
								.append("<p class=\"duration text-sm\">").append(groupTests.size()).append(" tests</p>\n")
								.append("</div>\n")
								.append("<div class=\"test-contents d-none\">\n")
								.append("<div class=\"info\">\n<h4>").append(groupName).append("</h4>\n").append(infoStatus.toString().trim()).append("</div>\n")
								.append("<table class='table table-sm mt-4'>\n<thead>\n<tr><th class=\"status-col\">Status</th><th class=\"timestamp-col\">Timestamp</th><th>TestName</th></tr>\n</thead>\n<tbody>\n")
								.append(tableRows.toString())
								.append("</tbody>\n</table>\n</div>\n</li>\n");
					}

					String updatedCatView = catView.substring(0, listWrapperStart + "<ul class=\"test-list-item\">".length())
							+ "\n" + cleanedItems.toString() + catView.substring(listWrapperEnd);
					// Update category count badge in tools
					updatedCatView = updatedCatView.replaceAll("(?s)<ul class=\"tools text-right\">\\s*<li><a href=\"#\"><span class=\"badge badge-primary\">\\d+</span>",
							"<ul class=\"tools text-right\"><li><a href=\"#\"><span class=\"badge badge-primary\">" + validGroupCount + "</span>");
					content = content.substring(0, catStart) + updatedCatView + content.substring(catEnd);
				}
			}
		}

		// 3. Clean up class name badges from test details
		for (String className : classNames) {
			content = content.replace("<span class=\"badge badge-pill badge-default\">" + className + "</span>", "");
		}

		// 4. Clean up tag toggle dropdown in test tools
		Pattern tagDropdownPattern = Pattern.compile("<a class=\"dropdown-item\" href=\"#\">([^<]+)</a>");
		int tagToggleStart = content.indexOf("<ul id=\"tag-toggle\"");
		if (tagToggleStart != -1) {
			int tagToggleEnd = content.indexOf("</ul>", tagToggleStart);
			if (tagToggleEnd != -1) {
				String dropdownContent = content.substring(tagToggleStart, tagToggleEnd);
				StringBuilder cleanedDropdown = new StringBuilder("<ul id=\"tag-toggle\" class=\"dropdown-menu dropdown-md p-v-0\">");
				Matcher m = tagDropdownPattern.matcher(dropdownContent);
				Set<String> seenDropdown = new HashSet<>();
				while (m.find()) {
					String tag = m.group(1).trim();
					if (!classNames.contains(tag) && !tag.endsWith("Flow") && !tag.endsWith("Test")) {
						cleanedDropdown.append(m.group(0));
						seenDropdown.add(tag);
					}
				}
				if (!seenDropdown.contains("basic") && !groupMap.get("basic").isEmpty()) {
					cleanedDropdown.append("<a class=\"dropdown-item\" href=\"#\">basic</a>");
				}
				cleanedDropdown.append("</ul>");
				content = content.substring(0, tagToggleStart) + cleanedDropdown.toString() + content.substring(tagToggleEnd + "</ul>".length());
			}
		}

		return content;
	}

	/**
	 * Reorders the views and side toolbar so that:
	 * 1. "Test Classes Executive Summary" is the FIRST icon in the side toolbar.
	 * 2. When the user opens the report, this executive summary section displays first.
	 */
	private static String reorderViewsAndNavigation(String content) {
		// 1. Move dashboard-view item to FIRST position in side-nav-menu
		String sideNavMenuTag = "<ul class=\"side-nav-menu\">";
		int menuStart = content.indexOf(sideNavMenuTag);
		if (menuStart != -1) {
			Pattern dashboardLiPattern = Pattern.compile("(?s)<li class=\"nav-item dropdown\" onclick=\"toggleView\\('dashboard-view'\\)\">.*?</li>");
			Matcher m = dashboardLiPattern.matcher(content);
			if (m.find()) {
				String dashboardLi = m.group(0);
				// Add title attribute for tooltip clarity
				if (!dashboardLi.contains("title=")) {
					dashboardLi = dashboardLi.replace("<a id=\"nav-dashboard\" class=\"dropdown-toggle\" href=\"#\">",
							"<a id=\"nav-dashboard\" class=\"dropdown-toggle\" href=\"#\" title=\"Test Classes Executive Summary\">");
				}
				// Remove from existing position
				content = content.substring(0, m.start()) + content.substring(m.end());
				// Insert at beginning of side-nav-menu
				menuStart = content.indexOf(sideNavMenuTag);
				int insertPoint = menuStart + sideNavMenuTag.length();
				content = content.substring(0, insertPoint) + "\n" + dashboardLi + content.substring(insertPoint);
			}
		}

		// 2. Move dashboard-view to be the FIRST view child in .main-content
		String mainContentTag = "<div class=\"main-content\">";
		int mainStart = content.indexOf(mainContentTag);
		if (mainStart != -1) {
			int dashStart = content.indexOf("<div class=\"container-fluid p-4 view dashboard-view\">");
			// Check if dashboard-view is already the first child
			int testViewStart = content.indexOf("class=\"test-wrapper row view test-view", mainStart);
			if (dashStart != -1 && testViewStart != -1 && dashStart > testViewStart) {
				// Find end of dashboard-view: right before <script>\nvar statusGroup
				int scriptIdx = content.indexOf("<script>\nvar statusGroup =", dashStart);
				if (scriptIdx == -1) {
					scriptIdx = content.indexOf("<script>var statusGroup =", dashStart);
				}
				if (scriptIdx == -1) {
					int sg = content.indexOf("var statusGroup =", dashStart);
					if (sg != -1) {
						scriptIdx = content.lastIndexOf("<script", sg);
					}
				}
				if (scriptIdx != -1) {
					String dashboardHtml = content.substring(dashStart, scriptIdx);
					// Remove dashboardHtml from current location
					content = content.substring(0, dashStart) + content.substring(scriptIdx);
					// Re-find mainContentTag
					mainStart = content.indexOf(mainContentTag);
					int insertPoint = mainStart + mainContentTag.length();

					// Ensure test-view has d-none so dashboard displays first
					content = content.replace("class=\"test-wrapper row view test-view\"", "class=\"test-wrapper row view test-view d-none\"");

					// Insert dashboardHtml at top of main-content
					content = content.substring(0, insertPoint) + "\n" + dashboardHtml + content.substring(insertPoint);
				}
			}
		}

		// 3. Inject JS to set dashboard-view as default active view on document load
		String startupScript = "<!-- TEST_CLASS_STARTUP_SCRIPT -->\n"
				+ "<script type=\"text/javascript\">\n"
				+ "var currentView = 'dashboard-view';\n"
				+ "document.addEventListener('DOMContentLoaded', function() {\n"
				+ "  if (typeof toggleView === 'function') { toggleView('dashboard-view'); }\n"
				+ "});\n"
				+ "$(document).ready(function() {\n"
				+ "  if (typeof toggleView === 'function') { toggleView('dashboard-view'); }\n"
				+ "});\n"
				+ "</script>\n"
				+ "<!-- TEST_CLASS_STARTUP_SCRIPT_END -->";

		if (!content.contains("<!-- TEST_CLASS_STARTUP_SCRIPT -->")) {
			int bodyEnd = content.indexOf("</body>");
			if (bodyEnd != -1) {
				content = content.substring(0, bodyEnd) + startupScript + "\n" + content.substring(bodyEnd);
			}
		}

		return content;
	}

	private static String injectHeaderPill(String content, int total, int passed, int failed, int skipped) {
		String target = "<ul class=\"nav-right\">";
		int idx = content.indexOf(target);
		if (idx == -1) {
			return content;
		}

		// Remove any existing header pill
		String cleanContent = content.replaceAll("<!-- TEST_CLASS_HEADER_PILL_START -->[\\s\\S]*?<!-- TEST_CLASS_HEADER_PILL_END -->", "");
		idx = cleanContent.indexOf(target);
		if (idx == -1) {
			return cleanContent;
		}
		int insertionPoint = idx + target.length();

		String pillHtml = "\n<!-- TEST_CLASS_HEADER_PILL_START -->\n"
				+ "<li class=\"m-r-10 d-none d-md-inline-block\">\n"
				+ "  <span class=\"badge text-white\" style=\"background: linear-gradient(135deg, #1e293b, #334155); padding: 7px 14px; border-radius: 20px; font-size: 12px; border: 1px solid rgba(255,255,255,0.15);\">\n"
				+ "    <i class=\"fa fa-cubes m-r-5 text-primary\"></i>Classes: <strong>" + total + "</strong> Total &nbsp;|&nbsp; "
				+ "    <span style=\"color: #4ade80;\"><i class=\"fa fa-check-circle m-r-5\"></i>" + passed + " Passed</span> &nbsp;|&nbsp; "
				+ "    <span style=\"color: " + (failed > 0 ? "#f87171" : "#94a3b8") + ";\"><i class=\"fa fa-times-circle m-r-5\"></i>" + failed + " Failed</span>"
				+ (skipped > 0 ? " &nbsp;|&nbsp; <span style=\"color: #fbbf24;\"><i class=\"fa fa-exclamation-triangle m-r-5\"></i>" + skipped + " Skipped</span>" : "")
				+ "  </span>\n"
				+ "</li>\n"
				+ "<!-- TEST_CLASS_HEADER_PILL_END -->";

		return cleanContent.substring(0, insertionPoint) + pillHtml + cleanContent.substring(insertionPoint);
	}

	private static String injectEnhancedCss(String content, String css) {
		String styleTag = "<style type=\"text/css\">";
		int idx = content.indexOf(styleTag);
		if (idx != -1) {
			int endTagIdx = content.indexOf("</style>", idx);
			if (endTagIdx != -1) {
				return content.substring(0, idx + styleTag.length()) + "\n" + css + "\n" + content.substring(endTagIdx);
			}
		}
		int headIdx = content.indexOf("</head>");
		if (headIdx != -1) {
			return content.substring(0, headIdx) + "<style type=\"text/css\">\n" + css + "\n</style>\n" + content.substring(headIdx);
		}
		return content;
	}

	private static String buildSummarySectionHtml(List<TestClassTracker.ClassSummary> summaries, int total, int passed, int failed, int skipped, double passRate) {
		StringBuilder sb = new StringBuilder();
		sb.append(SUMMARY_SECTION_START).append("\n");
		sb.append("<div class=\"test-class-summary-wrapper m-b-25\" id=\"test-class-summary-section\">\n");

		// ── Banner Card: Minimal & Aesthetic ──
		sb.append("  <div class=\"row m-b-15\">\n");
		sb.append("    <div class=\"col-md-12\">\n");
		sb.append("      <div class=\"card border-0\" style=\"background: #ffffff; border: 1px solid #e2e8f0 !important; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.03);\">\n");
		sb.append("        <div class=\"card-body p-3 d-flex align-items-center justify-content-between flex-wrap\">\n");
		sb.append("          <div class=\"d-flex align-items-center\">\n");
		sb.append("            <div style=\"width: 42px; height: 42px; border-radius: 10px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: center; margin-right: 14px;\">\n");
		sb.append("              <i class=\"fa fa-cubes\" style=\"font-size: 18px; color: #475569;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("            <div>\n");
		sb.append("              <h5 class=\"m-b-0 font-weight-bold\" style=\"color: #0f172a; font-size: 16px; letter-spacing: -0.2px;\">Test Classes Executive Summary</h5>\n");
		sb.append("              <small style=\"color: #64748b; font-size: 12px;\">Automated Suite Execution Metrics &amp; Class Health Status</small>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("          <div class=\"d-flex align-items-center flex-wrap gap-2 mt-2 mt-md-0\">\n");
		sb.append("            <span class=\"badge badge-pill font-size-12 m-r-8\" style=\"background: #f1f5f9; color: #334155; border: 1px solid #e2e8f0; padding: 6px 12px; font-weight: 600;\">Total Classes: ").append(total).append("</span>\n");
		sb.append("            <span class=\"badge badge-pill font-size-12 m-r-8\" style=\"background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; padding: 6px 12px; font-weight: 600;\">Passed: ").append(passed).append("</span>\n");
		if (failed > 0) {
			sb.append("            <span class=\"badge badge-pill font-size-12 m-r-8\" style=\"background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; padding: 6px 12px; font-weight: 600;\">Failed: ").append(failed).append("</span>\n");
		} else {
			sb.append("            <span class=\"badge badge-pill font-size-12 m-r-8\" style=\"background: #f8fafc; color: #64748b; border: 1px solid #e2e8f0; padding: 6px 12px; font-weight: 500;\">Failed: 0</span>\n");
		}
		if (skipped > 0) {
			sb.append("            <span class=\"badge badge-pill font-size-12 m-r-8\" style=\"background: #fffbeb; color: #b45309; border: 1px solid #fde68a; padding: 6px 12px; font-weight: 600;\">Skipped: ").append(skipped).append("</span>\n");
		}
		sb.append("            <span class=\"badge badge-pill font-size-12\" style=\"background: #f5f3ff; color: #6d28d9; border: 1px solid #ddd6fe; padding: 6px 12px; font-weight: 600;\">Pass Rate: ").append(String.format("%.1f%%", passRate)).append("</span>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");
		sb.append("  </div>\n");

		// ── 4 Metric Cards: Clean, minimal pastel aesthetic ──
		sb.append("  <div class=\"row m-b-15\">\n");

		// Card 1: Total Test Classes
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0\" style=\"background: #ffffff; border: 1px solid #e2e8f0 !important; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.03);\">\n");
		sb.append("        <div class=\"card-body p-3\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div>\n");
		sb.append("              <p class=\"m-b-0 text-muted font-weight-semibold text-uppercase font-size-11\" style=\"letter-spacing: 0.5px;\">Total Test Classes</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold mt-1\" style=\"color: #0f172a; font-size: 26px;\">").append(total).append("</h2>\n");
		sb.append("              <small style=\"color: #64748b; font-weight: 500;\"><i class=\"fa fa-tasks m-r-5 text-muted\"></i>100% Executed</small>\n");
		sb.append("            </div>\n");
		sb.append("            <div style=\"width: 42px; height: 42px; border-radius: 10px; background: #f8fafc; border: 1px solid #e2e8f0; display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa fa-folder-open\" style=\"font-size: 18px; color: #475569;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		// Card 2: Classes Passed
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0\" style=\"background: #ffffff; border: 1px solid #e2e8f0 !important; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.03);\">\n");
		sb.append("        <div class=\"card-body p-3\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div>\n");
		sb.append("              <p class=\"m-b-0 font-weight-semibold text-uppercase font-size-11\" style=\"letter-spacing: 0.5px; color: #059669;\">Classes Passed</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold mt-1\" style=\"color: #059669; font-size: 26px;\">").append(passed).append("</h2>\n");
		sb.append("              <small style=\"color: #059669; font-weight: 500;\"><i class=\"fa fa-check-circle m-r-5\"></i>").append(String.format("%.1f%%", passRate)).append(" Success Rate</small>\n");
		sb.append("            </div>\n");
		sb.append("            <div style=\"width: 42px; height: 42px; border-radius: 10px; background: #ecfdf5; border: 1px solid #a7f3d0; display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa fa-check\" style=\"font-size: 18px; color: #059669;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		// Card 3: Classes Failed
		String failTextColor = failed > 0 ? "#dc2626" : "#64748b";
		String failBgColor = failed > 0 ? "#fef2f2" : "#f8fafc";
		String failBorderColor = failed > 0 ? "#fecaca" : "#e2e8f0";
		String failIcon = failed > 0 ? "fa-times" : "fa-shield";
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0\" style=\"background: #ffffff; border: 1px solid #e2e8f0 !important; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.03);\">\n");
		sb.append("        <div class=\"card-body p-3\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div>\n");
		sb.append("              <p class=\"m-b-0 font-weight-semibold text-uppercase font-size-11\" style=\"letter-spacing: 0.5px; color: ").append(failTextColor).append(";\">Classes Failed</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold mt-1\" style=\"color: ").append(failTextColor).append("; font-size: 26px;\">").append(failed).append("</h2>\n");
		sb.append("              <small style=\"color: ").append(failTextColor).append("; font-weight: 500;\"><i class=\"fa ").append(failIcon).append(" m-r-5\"></i>").append(failed).append(" Failures</small>\n");
		sb.append("            </div>\n");
		sb.append("            <div style=\"width: 42px; height: 42px; border-radius: 10px; background: ").append(failBgColor).append("; border: 1px solid ").append(failBorderColor).append("; display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa ").append(failIcon).append("\" style=\"font-size: 18px; color: ").append(failTextColor).append(";\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		// Card 4: Class Pass Rate
		String rateBarColor = passRate >= 80.0 ? "#059669" : (passRate >= 50.0 ? "#4f46e5" : "#dc2626");
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0\" style=\"background: #ffffff; border: 1px solid #e2e8f0 !important; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.03);\">\n");
		sb.append("        <div class=\"card-body p-3\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div style=\"width: 100%;\">\n");
		sb.append("              <p class=\"m-b-0 font-weight-semibold text-uppercase font-size-11\" style=\"letter-spacing: 0.5px; color: #4f46e5;\">Class Pass Rate</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold mt-1\" style=\"color: #4f46e5; font-size: 26px;\">").append(String.format("%.1f%%", passRate)).append("</h2>\n");
		sb.append("              <div class=\"progress m-t-8\" style=\"height: 5px; border-radius: 3px; background-color: #f1f5f9; width: 85%;\">\n");
		sb.append("                <div class=\"progress-bar\" role=\"progressbar\" style=\"width: ").append(Math.min(100.0, passRate)).append("%; background: ").append(rateBarColor).append("; border-radius: 3px;\" aria-valuenow=\"").append((int) passRate).append("\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>\n");
		sb.append("              </div>\n");
		sb.append("            </div>\n");
		sb.append("            <div style=\"width: 42px; height: 42px; border-radius: 10px; background: #eef2ff; border: 1px solid #c7d2fe; display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa fa-percent\" style=\"font-size: 16px; color: #4f46e5;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		sb.append("  </div>\n");

		// ── Breakdown Table Card: Clean & Aesthetic ──
		sb.append("  <div class=\"row\">\n");
		sb.append("    <div class=\"col-md-12\">\n");
		sb.append("      <div class=\"card border-0\" style=\"background: #ffffff; border: 1px solid #e2e8f0 !important; border-radius: 12px; overflow: hidden; box-shadow: 0 1px 3px rgba(0,0,0,0.03);\">\n");
		sb.append("        <div class=\"card-header d-flex justify-content-between align-items-center\" style=\"background: #ffffff; border-bottom: 1px solid #f1f5f9; padding: 14px 20px;\">\n");
		sb.append("          <h6 class=\"card-title m-b-0 font-weight-bold\" style=\"color: #0f172a; font-size: 14px;\">\n");
		sb.append("            <i class=\"fa fa-list-alt m-r-8\" style=\"color: #64748b;\"></i>Test Classes Execution Breakdown\n");
		sb.append("          </h6>\n");
		sb.append("          <span class=\"badge\" style=\"background: #f1f5f9; color: #475569; border: 1px solid #e2e8f0; padding: 4px 10px; border-radius: 6px; font-weight: 600;\">").append(total).append(" Classes</span>\n");
		sb.append("        </div>\n");
		sb.append("        <div class=\"card-body p-0 table-responsive\">\n");
		sb.append("          <table class=\"table table-hover m-b-0 align-middle\" style=\"font-size: 13px;\">\n");
		sb.append("            <thead style=\"background: #f8fafc; color: #475569; border-bottom: 1px solid #e2e8f0;\">\n");
		sb.append("              <tr>\n");
		sb.append("                <th style=\"width: 50px; text-align: center;\">#</th>\n");
		sb.append("                <th>Test Class Name</th>\n");
		sb.append("                <th>Package / Module</th>\n");
		sb.append("                <th style=\"text-align: center;\">Total Methods</th>\n");
		sb.append("                <th style=\"text-align: center;\">Passed</th>\n");
		sb.append("                <th style=\"text-align: center;\">Failed</th>\n");
		sb.append("                <th style=\"text-align: center;\">Skipped</th>\n");
		sb.append("                <th style=\"text-align: center;\">Duration</th>\n");
		sb.append("                <th style=\"text-align: center; width: 110px;\">Class Status</th>\n");
		sb.append("              </tr>\n");
		sb.append("            </thead>\n");
		sb.append("            <tbody>\n");

		int idx = 1;
		for (TestClassTracker.ClassSummary cs : summaries) {
			String status = cs.getStatus();
			boolean isPass = "PASS".equalsIgnoreCase(status);
			boolean isFail = "FAIL".equalsIgnoreCase(status);

			String statusBadge;
			if (isPass) {
				statusBadge = "<span class=\"badge\" style=\"background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; padding: 4px 12px; font-weight: 600; font-size: 11px; letter-spacing: 0.5px; border-radius: 6px;\">PASSED</span>";
			} else if (isFail) {
				statusBadge = "<span class=\"badge\" style=\"background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; padding: 4px 12px; font-weight: 600; font-size: 11px; letter-spacing: 0.5px; border-radius: 6px;\">FAILED</span>";
			} else {
				statusBadge = "<span class=\"badge\" style=\"background: #fffbeb; color: #b45309; border: 1px solid #fde68a; padding: 4px 12px; font-weight: 600; font-size: 11px; letter-spacing: 0.5px; border-radius: 6px;\">SKIPPED</span>";
			}

			sb.append("              <tr>\n");
			sb.append("                <td style=\"text-align: center; font-weight: 600; color: #94a3b8;\">").append(idx++).append("</td>\n");
			sb.append("                <td><strong style=\"color: #0f172a;\">").append(cs.getSimpleClassName()).append("</strong></td>\n");
			sb.append("                <td><span style=\"background: #f8fafc; color: #475569; border: 1px solid #e2e8f0; font-size: 11px; padding: 2px 8px; border-radius: 4px;\">").append(cs.getPackageName()).append("</span></td>\n");
			sb.append("                <td style=\"text-align: center; font-weight: 600; color: #334155;\">").append(cs.getTotalMethods()).append("</td>\n");
			sb.append("                <td style=\"text-align: center;\"><span style=\"background: #ecfdf5; color: #047857; border: 1px solid #a7f3d0; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600;\">").append(cs.getPassedMethods()).append("</span></td>\n");
			sb.append("                <td style=\"text-align: center;\">").append(cs.getFailedMethods() > 0 ? "<span style=\"background: #fef2f2; color: #b91c1c; border: 1px solid #fecaca; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600;\">" + cs.getFailedMethods() + "</span>" : "<span style=\"color: #94a3b8;\">0</span>").append("</td>\n");
			sb.append("                <td style=\"text-align: center;\">").append(cs.getSkippedMethods() > 0 ? "<span style=\"background: #fffbeb; color: #b45309; border: 1px solid #fde68a; padding: 2px 8px; border-radius: 10px; font-size: 11px; font-weight: 600;\">" + cs.getSkippedMethods() + "</span>" : "<span style=\"color: #94a3b8;\">0</span>").append("</td>\n");
			sb.append("                <td style=\"text-align: center; color: #64748b; font-weight: 500; font-size: 12px;\"><i class=\"fa fa-clock-o m-r-5 text-muted\"></i>").append(cs.getDurationFormatted()).append("</td>\n");
			sb.append("                <td style=\"text-align: center;\">").append(statusBadge).append("</td>\n");
			sb.append("              </tr>\n");
		}

		sb.append("            </tbody>\n");
		sb.append("          </table>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");
		sb.append("  </div>\n");

		sb.append("</div>\n");
		sb.append(SUMMARY_SECTION_END);

		return sb.toString();
	}

	private static String getEnhancedCss() {
		return "/* Minimal & Aesthetic Modern Theme Enhancements */\n"
				+ "body, .app, .card, table {\n"
				+ "  font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif !important;\n"
				+ "}\n"
				+ ".card {\n"
				+ "  border-radius: 12px !important;\n"
				+ "  border: 1px solid #e2e8f0 !important;\n"
				+ "  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.03) !important;\n"
				+ "  transition: transform 0.2s ease, box-shadow 0.2s ease;\n"
				+ "}\n"
				+ ".card:hover {\n"
				+ "  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05) !important;\n"
				+ "}\n"
				+ ".class-metric-card {\n"
				+ "  transition: transform 0.2s ease, box-shadow 0.2s ease;\n"
				+ "}\n"
				+ ".class-metric-card:hover {\n"
				+ "  transform: translateY(-2px);\n"
				+ "  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.06) !important;\n"
				+ "}\n"
				+ ".table thead th {\n"
				+ "  font-weight: 600 !important;\n"
				+ "  text-transform: uppercase;\n"
				+ "  font-size: 11px;\n"
				+ "  letter-spacing: 0.5px;\n"
				+ "}\n"
				+ ".badge {\n"
				+ "  font-weight: 600;\n"
				+ "}\n"
				+ ".vheader .nav-right .badge-primary {\n"
				+ "  background: #f1f5f9 !important;\n"
				+ "  color: #334155 !important;\n"
				+ "  border: 1px solid #e2e8f0;\n"
				+ "  border-radius: 6px;\n"
				+ "  padding: 6px 12px;\n"
				+ "}\n";
	}

	/**
	 * Parses test classes and their methods from the raw ExtentReport HTML file.
	 * Used as a fallback when TestClassTracker was not populated in-memory.
	 */
	private static List<TestClassTracker.ClassSummary> parseClassesFromHtml(String content) {
		List<TestClassTracker.ClassSummary> list = new ArrayList<>();
		int start = content.indexOf("view test-view");
		if (start == -1) {
			return list;
		}

		int end = content.indexOf("view exception-view", start);
		if (end == -1) {
			end = content.indexOf("view category-view", start);
		}
		if (end == -1) {
			end = content.indexOf("view author-view", start);
		}
		if (end == -1) {
			end = content.indexOf("view dashboard-view", start);
		}
		if (end == -1) {
			end = content.length();
		}

		String testViewContent = content.substring(start, end);

		// Extract timeline durations: var timeline = { "Name": 85.986 };
		java.util.Map<String, Long> timelineDurations = new java.util.HashMap<>();
		Pattern timelinePattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*([0-9.]+)", Pattern.CASE_INSENSITIVE);
		Matcher timelineMatcher = timelinePattern.matcher(content);
		while (timelineMatcher.find()) {
			String name = timelineMatcher.group(1);
			try {
				double sec = Double.parseDouble(timelineMatcher.group(2));
				timelineDurations.put(name, (long) (sec * 1000));
			} catch (NumberFormatException ignored) {
			}
		}

		Pattern itemPattern = Pattern.compile("(<li class=\"test-item\"[^>]*>)([\\s\\S]*?<p class=\"name\">([^<]+)</p>)", Pattern.CASE_INSENSITIVE);
		Matcher matcher = itemPattern.matcher(testViewContent);

		java.util.Map<String, TestClassTracker.ClassSummary> map = new java.util.LinkedHashMap<>();

		while (matcher.find()) {
			String openTag = matcher.group(1);
			String fullName = matcher.group(3).trim();

			String status = "PASS";
			Matcher sm = Pattern.compile("status=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE).matcher(openTag);
			if (sm.find()) {
				status = sm.group(1).trim().toUpperCase();
			}

			String tags = "";
			Matcher tm = Pattern.compile("tag=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE).matcher(openTag);
			if (tm.find()) {
				tags = tm.group(1).trim();
			}

			String className;
			String methodName;
			if (fullName.contains(" - ")) {
				String[] parts = fullName.split(" - ", 2);
				className = parts[0].trim();
				methodName = parts[1].trim();
			} else {
				className = fullName;
				methodName = fullName;
			}

			// Infer module/package from tags if available
			String module = "sop";
			if (!tags.isEmpty()) {
				for (String tag : tags.split("\\s+")) {
					if (!tag.equalsIgnoreCase(className) && !tag.equalsIgnoreCase("advance") && !tag.equalsIgnoreCase("basic")) {
						module = tag;
						break;
					}
				}
				if ("sop".equals(module)) {
					for (String tag : tags.split("\\s+")) {
						if (!tag.equalsIgnoreCase(className)) {
							module = tag;
							break;
						}
					}
				}
			}

			String qualifiedName = module + "." + className;
			TestClassTracker.ClassSummary cs = map.computeIfAbsent(qualifiedName, k -> new TestClassTracker.ClassSummary(qualifiedName));

			long duration = timelineDurations.getOrDefault(fullName, 0L);
			cs.recordMethodStart(methodName, 1000);

			if (status.contains("PASS")) {
				cs.recordMethodSuccess(methodName, 1000 + duration);
			} else if (status.contains("FAIL")) {
				cs.recordMethodFailure(methodName, 1000 + duration);
			} else {
				cs.recordMethodSkipped(methodName, 1000 + duration);
			}
		}

		list.addAll(map.values());
		return list;
	}

	private static List<TestClassTracker.ClassSummary> parseClassesFromTestngXml(Path xmlPath) {
		List<TestClassTracker.ClassSummary> list = new ArrayList<>();
		if (xmlPath == null || !Files.exists(xmlPath)) {
			return list;
		}
		try {
			String xml = Files.readString(xmlPath, StandardCharsets.UTF_8);
			Pattern classPattern = Pattern.compile("(?s)<class name=\"([^\"]+)\">(.*?)</class>");
			Matcher classMatcher = classPattern.matcher(xml);
			while (classMatcher.find()) {
				String fullClassName = classMatcher.group(1);
				String classBody = classMatcher.group(2);
				TestClassTracker.ClassSummary cs = new TestClassTracker.ClassSummary(fullClassName);

				Pattern methodPattern = Pattern.compile("(?s)<test-method\\b([^>]*)>");
				Matcher methodMatcher = methodPattern.matcher(classBody);
				boolean hasTestMethods = false;
				while (methodMatcher.find()) {
					String attrs = methodMatcher.group(1);
					if (attrs.contains("is-config=\"true\"")) {
						continue;
					}
					String methodName = extractXmlAttr(attrs, "name");
					String status = extractXmlAttr(attrs, "status");
					String durationStr = extractXmlAttr(attrs, "duration-ms");
					long duration = 0;
					try {
						if (durationStr != null) {
							duration = Long.parseLong(durationStr);
						}
					} catch (Exception ignored) {
					}

					if (methodName != null && status != null) {
						hasTestMethods = true;
						cs.recordMethodStart(methodName, 1000);
						if ("PASS".equalsIgnoreCase(status)) {
							cs.recordMethodSuccess(methodName, 1000 + duration);
						} else if ("FAIL".equalsIgnoreCase(status) || "FAILURE".equalsIgnoreCase(status)) {
							cs.recordMethodFailure(methodName, 1000 + duration);
						} else {
							cs.recordMethodSkipped(methodName, 1000 + duration);
						}
					}
				}
				if (hasTestMethods) {
					list.add(cs);
				}
			}
		} catch (Exception e) {
			log.warn("Failed to parse testng-results.xml: {}", e.getMessage());
		}
		return list;
	}

	private static String extractXmlAttr(String attrs, String attrName) {
		Pattern p = Pattern.compile(attrName + "=\"([^\"]*)\"");
		Matcher m = p.matcher(attrs);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	private static class ExtentTestRecord {
		String id = "1";
		String status = "pass";
		String name = "Unknown Test";
		String timestamp = "";
		Set<String> tags = new HashSet<>();
	}

	private static List<ExtentTestRecord> extractTestRecordsFromTestView(String content) {
		List<ExtentTestRecord> list = new ArrayList<>();
		int start = content.indexOf("view test-view");
		if (start == -1) {
			return list;
		}
		int end = content.indexOf("view category-view", start);
		if (end == -1) {
			end = content.indexOf("view author-view", start);
		}
		if (end == -1) {
			end = content.indexOf("view dashboard-view", start);
		}
		if (end == -1) {
			end = content.length();
		}

		String testView = content.substring(start, end);
		Matcher itemMatcher = Pattern.compile("<li class=\"test-item\"([^>]*)>(.*?)(?=<li class=\"test-item\"|</ul>)", Pattern.DOTALL).matcher(testView);
		while (itemMatcher.find()) {
			String attrs = itemMatcher.group(1);
			String body = itemMatcher.group(2);

			ExtentTestRecord rec = new ExtentTestRecord();

			Matcher sm = Pattern.compile("status=\"([^\"]*)\"").matcher(attrs);
			if (sm.find()) {
				rec.status = sm.group(1).trim().toLowerCase();
			}

			Matcher idm = Pattern.compile("test-id=\"([^\"]*)\"").matcher(attrs);
			if (idm.find()) {
				rec.id = idm.group(1).trim();
			}

			Matcher tm = Pattern.compile("tag=\"([^\"]*)\"").matcher(attrs);
			if (tm.find()) {
				for (String t : tm.group(1).trim().split("\\s+")) {
					if (!t.isEmpty()) {
						rec.tags.add(t.toLowerCase());
					}
				}
			}

			Matcher nm = Pattern.compile("<p class=\"name\">([^<]*)</p>").matcher(body);
			if (nm.find()) {
				rec.name = nm.group(1).trim();
			}

			Matcher tim = Pattern.compile("<span>([^<]*[ap]m)</span>", Pattern.CASE_INSENSITIVE).matcher(body);
			if (tim.find()) {
				rec.timestamp = tim.group(1).trim();
			}

			list.add(rec);
		}
		return list;
	}
}
