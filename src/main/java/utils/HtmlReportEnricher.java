package utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Enriches the ExtentReport HTML output with an executive Test Classes summary:
 * - Metric cards: Total Test Classes, Classes Passed, Classes Failed, Class Pass Rate
 * - Breakdown table: detailed per-class execution results (Total Methods, Passed, Failed, Skipped, Duration, Status)
 * - System/Environment integration
 * - Modern typography, layout, and visual styling
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
	 * Enriches the ExtentReport HTML file at the specified path using data from TestClassTracker.
	 * If TestClassTracker is empty, extracts test information from the HTML file itself.
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

			// Fallback: If TestClassTracker is empty, parse classes from the existing HTML report
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

			// 2. Inject or update System/Environment entries
			updatedContent = injectSystemInfo(updatedContent, totalClasses, passedClasses, failedClasses, skippedClasses, passRate);

			// 3. Inject Header summary pill
			updatedContent = injectHeaderPill(updatedContent, totalClasses, passedClasses, failedClasses);

			// 4. Inject Enhanced CSS
			updatedContent = injectEnhancedCss(updatedContent, enhancedCss);

			Files.writeString(path, updatedContent, StandardCharsets.UTF_8);
			log.info("ExtentReport successfully enriched with Test Classes summary: {} total, {} passed, {} failed",
					totalClasses, passedClasses, failedClasses);
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

	private static String injectSystemInfo(String content, int total, int passed, int failed, int skipped, double passRate) {
		String sysEnvHeader = "<div class=\"card-header\"><p>System/Environment</p></div>";
		int headerIdx = content.indexOf(sysEnvHeader);
		if (headerIdx == -1) {
			return content;
		}

		int tbodyStart = content.indexOf("<tbody>", headerIdx);
		if (tbodyStart == -1) {
			return content;
		}
		int insertionPoint = tbodyStart + "<tbody>".length();

		// Remove any previously injected system info rows to avoid duplicates
		String cleanContent = content.replaceAll("<!-- TEST_CLASS_SYSENV_START -->[\\s\\S]*?<!-- TEST_CLASS_SYSENV_END -->", "");

		// Recalculate insertion point after clean
		headerIdx = cleanContent.indexOf(sysEnvHeader);
		if (headerIdx == -1) {
			return cleanContent;
		}
		tbodyStart = cleanContent.indexOf("<tbody>", headerIdx);
		if (tbodyStart == -1) {
			return cleanContent;
		}
		insertionPoint = tbodyStart + "<tbody>".length();

		StringBuilder sb = new StringBuilder();
		sb.append("\n<!-- TEST_CLASS_SYSENV_START -->\n");
		sb.append("<tr style=\"background-color: #f8fafc; font-weight: 600;\"><td>Total Test Classes</td><td><span class=\"badge badge-primary\" style=\"background:#3b82f6;\">").append(total).append("</span></td></tr>\n");
		sb.append("<tr style=\"background-color: #f8fafc; font-weight: 600;\"><td>Test Classes Passed</td><td><span class=\"badge badge-success\" style=\"background:#10b981;\">").append(passed).append("</span></td></tr>\n");
		sb.append("<tr style=\"background-color: #f8fafc; font-weight: 600;\"><td>Test Classes Failed</td><td><span class=\"badge badge-danger\" style=\"background:#ef4444;\">").append(failed).append("</span></td></tr>\n");
		if (skipped > 0) {
			sb.append("<tr style=\"background-color: #f8fafc; font-weight: 600;\"><td>Test Classes Skipped</td><td><span class=\"badge badge-warning\" style=\"background:#f59e0b;\">").append(skipped).append("</span></td></tr>\n");
		}
		sb.append("<tr style=\"background-color: #f8fafc; font-weight: 600;\"><td>Class Pass Rate</td><td><span class=\"badge badge-info\" style=\"background:#8b5cf6;\">").append(String.format("%.1f%%", passRate)).append("</span></td></tr>\n");
		sb.append("<!-- TEST_CLASS_SYSENV_END -->");

		return cleanContent.substring(0, insertionPoint) + sb.toString() + cleanContent.substring(insertionPoint);
	}

	private static String injectHeaderPill(String content, int total, int passed, int failed) {
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
				+ "    <span style=\"color: " + (failed > 0 ? "#f87171" : "#94a3b8") + ";\"><i class=\"fa fa-times-circle m-r-5\"></i>" + failed + " Failed</span>\n"
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
		// If style tag not found or closed, inject before </head>
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

		// ── Banner Card ──
		sb.append("  <div class=\"row m-b-15\">\n");
		sb.append("    <div class=\"col-md-12\">\n");
		sb.append("      <div class=\"card border-0 shadow-sm\" style=\"background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); color: #ffffff; border-radius: 12px;\">\n");
		sb.append("        <div class=\"card-body p-3 d-flex align-items-center justify-content-between flex-wrap\">\n");
		sb.append("          <div class=\"d-flex align-items-center\">\n");
		sb.append("            <div style=\"width: 46px; height: 46px; border-radius: 10px; background: rgba(59, 130, 246, 0.2); display: flex; align-items: center; justify-content: center; margin-right: 14px;\">\n");
		sb.append("              <i class=\"fa fa-cubes text-primary\" style=\"font-size: 22px;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("            <div>\n");
		sb.append("              <h5 class=\"m-b-0 text-white font-weight-bold\" style=\"letter-spacing: 0.3px;\">Test Classes Executive Summary</h5>\n");
		sb.append("              <small style=\"color: #94a3b8;\">Automated Suite Execution Metrics &amp; Class Health Status</small>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("          <div class=\"d-flex align-items-center flex-wrap gap-2 mt-2 mt-md-0\">\n");
		sb.append("            <span class=\"badge badge-pill font-size-13 m-r-10\" style=\"background: #3b82f6; padding: 7px 14px; font-weight: 600;\">Total Classes: ").append(total).append("</span>\n");
		sb.append("            <span class=\"badge badge-pill font-size-13 m-r-10\" style=\"background: #10b981; padding: 7px 14px; font-weight: 600;\">Passed: ").append(passed).append("</span>\n");
		sb.append("            <span class=\"badge badge-pill font-size-13 m-r-10\" style=\"background: ").append(failed > 0 ? "#ef4444" : "#64748b").append("; padding: 7px 14px; font-weight: 600;\">Failed: ").append(failed).append("</span>\n");
		if (skipped > 0) {
			sb.append("            <span class=\"badge badge-pill font-size-13 m-r-10\" style=\"background: #f59e0b; padding: 7px 14px; font-weight: 600;\">Skipped: ").append(skipped).append("</span>\n");
		}
		sb.append("            <span class=\"badge badge-pill font-size-13\" style=\"background: #8b5cf6; padding: 7px 14px; font-weight: 600;\">Pass Rate: ").append(String.format("%.1f%%", passRate)).append("</span>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");
		sb.append("  </div>\n");

		// ── 4 Metric Cards ──
		sb.append("  <div class=\"row m-b-15\">\n");

		// Card 1: Total Test Classes
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0 shadow-sm\" style=\"border-radius: 10px; border-left: 5px solid #3b82f6 !important;\">\n");
		sb.append("        <div class=\"card-body\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div>\n");
		sb.append("              <p class=\"m-b-0 text-muted font-weight-semibold text-uppercase font-size-12\">Total Test Classes</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold text-dark mt-1\">").append(total).append("</h2>\n");
		sb.append("              <small class=\"text-primary font-weight-semibold\"><i class=\"fa fa-tasks m-r-5\"></i>100% Executed</small>\n");
		sb.append("            </div>\n");
		sb.append("            <div class=\"metric-icon-circle\" style=\"width: 48px; height: 48px; border-radius: 50%; background: rgba(59, 130, 246, 0.12); display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa fa-folder-open text-primary\" style=\"font-size: 22px;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		// Card 2: Classes Passed
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0 shadow-sm\" style=\"border-radius: 10px; border-left: 5px solid #10b981 !important;\">\n");
		sb.append("        <div class=\"card-body\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div>\n");
		sb.append("              <p class=\"m-b-0 text-success font-weight-semibold text-uppercase font-size-12\">Classes Passed</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold text-success mt-1\">").append(passed).append("</h2>\n");
		sb.append("              <small class=\"text-success font-weight-semibold\"><i class=\"fa fa-check-circle m-r-5\"></i>").append(String.format("%.1f%%", passRate)).append(" Success Rate</small>\n");
		sb.append("            </div>\n");
		sb.append("            <div class=\"metric-icon-circle\" style=\"width: 48px; height: 48px; border-radius: 50%; background: rgba(16, 185, 129, 0.12); display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa fa-check-circle text-success\" style=\"font-size: 22px;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		// Card 3: Classes Failed
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0 shadow-sm\" style=\"border-radius: 10px; border-left: 5px solid #ef4444 !important;\">\n");
		sb.append("        <div class=\"card-body\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div>\n");
		sb.append("              <p class=\"m-b-0 text-danger font-weight-semibold text-uppercase font-size-12\">Classes Failed</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold text-danger mt-1\">").append(failed).append("</h2>\n");
		sb.append("              <small class=\"font-weight-semibold\" style=\"color: ").append(failed > 0 ? "#ef4444" : "#64748b").append(";\"><i class=\"fa fa-shield m-r-5\"></i>").append(failed).append(" Failures</small>\n");
		sb.append("            </div>\n");
		sb.append("            <div class=\"metric-icon-circle\" style=\"width: 48px; height: 48px; border-radius: 50%; background: rgba(239, 68, 68, 0.12); display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa fa-times-circle text-danger\" style=\"font-size: 22px;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		// Card 4: Class Pass Rate
		sb.append("    <div class=\"col-md-3\">\n");
		sb.append("      <div class=\"card class-metric-card border-0 shadow-sm\" style=\"border-radius: 10px; border-left: 5px solid #8b5cf6 !important;\">\n");
		sb.append("        <div class=\"card-body\">\n");
		sb.append("          <div class=\"d-flex justify-content-between align-items-center\">\n");
		sb.append("            <div style=\"width: 100%;\">\n");
		sb.append("              <p class=\"m-b-0 font-weight-semibold text-uppercase font-size-12\" style=\"color: #8b5cf6;\">Class Pass Rate</p>\n");
		sb.append("              <h2 class=\"m-b-0 font-weight-bold mt-1\" style=\"color: #8b5cf6;\">").append(String.format("%.1f%%", passRate)).append("</h2>\n");
		sb.append("              <div class=\"progress m-t-10\" style=\"height: 6px; border-radius: 3px; background-color: #ede9fe; width: 85%;\">\n");
		sb.append("                <div class=\"progress-bar\" role=\"progressbar\" style=\"width: ").append(Math.min(100.0, passRate)).append("%; background: linear-gradient(90deg, #8b5cf6, #10b981); border-radius: 3px;\" aria-valuenow=\"").append((int) passRate).append("\" aria-valuemin=\"0\" aria-valuemax=\"100\"></div>\n");
		sb.append("              </div>\n");
		sb.append("            </div>\n");
		sb.append("            <div class=\"metric-icon-circle\" style=\"width: 48px; height: 48px; border-radius: 50%; background: rgba(139, 92, 246, 0.12); display: flex; align-items: center; justify-content: center;\">\n");
		sb.append("              <i class=\"fa fa-percent\" style=\"font-size: 20px; color: #8b5cf6;\"></i>\n");
		sb.append("            </div>\n");
		sb.append("          </div>\n");
		sb.append("        </div>\n");
		sb.append("      </div>\n");
		sb.append("    </div>\n");

		sb.append("  </div>\n");

		// ── Breakdown Table Card ──
		sb.append("  <div class=\"row\">\n");
		sb.append("    <div class=\"col-md-12\">\n");
		sb.append("      <div class=\"card border-0 shadow-sm\" style=\"border-radius: 10px; overflow: hidden;\">\n");
		sb.append("        <div class=\"card-header d-flex justify-content-between align-items-center\" style=\"background: #f8fafc; border-bottom: 1px solid #e2e8f0; padding: 12px 20px;\">\n");
		sb.append("          <h6 class=\"card-title m-b-0 font-weight-bold text-dark\" style=\"font-size: 14px;\">\n");
		sb.append("            <i class=\"fa fa-list-alt text-primary m-r-8\"></i>Test Classes Execution Breakdown\n");
		sb.append("          </h6>\n");
		sb.append("          <span class=\"badge font-weight-semibold\" style=\"background: #e2e8f0; color: #334155; padding: 5px 10px; border-radius: 6px;\">").append(total).append(" Classes</span>\n");
		sb.append("        </div>\n");
		sb.append("        <div class=\"card-body p-0 table-responsive\">\n");
		sb.append("          <table class=\"table table-hover table-striped m-b-0 align-middle\" style=\"font-size: 13px;\">\n");
		sb.append("            <thead style=\"background: #f1f5f9; color: #475569;\">\n");
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
				statusBadge = "<span class=\"badge badge-success p-2 px-3 font-weight-bold\" style=\"background-color: #10b981; letter-spacing: 0.5px; border-radius: 6px;\">PASSED</span>";
			} else if (isFail) {
				statusBadge = "<span class=\"badge badge-danger p-2 px-3 font-weight-bold\" style=\"background-color: #ef4444; letter-spacing: 0.5px; border-radius: 6px;\">FAILED</span>";
			} else {
				statusBadge = "<span class=\"badge badge-warning p-2 px-3 font-weight-bold\" style=\"background-color: #f59e0b; letter-spacing: 0.5px; border-radius: 6px;\">SKIPPED</span>";
			}

			sb.append("              <tr>\n");
			sb.append("                <td style=\"text-align: center; font-weight: 600; color: #64748b;\">").append(idx++).append("</td>\n");
			sb.append("                <td><strong class=\"text-dark\">").append(cs.getSimpleClassName()).append("</strong></td>\n");
			sb.append("                <td><span class=\"badge badge-pill\" style=\"background: #e2e8f0; color: #334155; font-size: 11px;\">").append(cs.getPackageName()).append("</span></td>\n");
			sb.append("                <td style=\"text-align: center; font-weight: 600;\">").append(cs.getTotalMethods()).append("</td>\n");
			sb.append("                <td style=\"text-align: center;\"><span class=\"badge badge-pill badge-success\" style=\"background-color: #10b981;\">").append(cs.getPassedMethods()).append("</span></td>\n");
			sb.append("                <td style=\"text-align: center;\"><span class=\"badge badge-pill\" style=\"background-color: ").append(cs.getFailedMethods() > 0 ? "#ef4444" : "#e2e8f0").append("; color: ").append(cs.getFailedMethods() > 0 ? "#fff" : "#64748b").append(";\">").append(cs.getFailedMethods()).append("</span></td>\n");
			sb.append("                <td style=\"text-align: center;\"><span class=\"badge badge-pill\" style=\"background-color: ").append(cs.getSkippedMethods() > 0 ? "#f59e0b" : "#e2e8f0").append("; color: ").append(cs.getSkippedMethods() > 0 ? "#fff" : "#64748b").append(";\">").append(cs.getSkippedMethods()).append("</span></td>\n");
			sb.append("                <td style=\"text-align: center; color: #475569; font-weight: 500;\"><i class=\"fa fa-clock-o m-r-5 text-muted\"></i>").append(cs.getDurationFormatted()).append("</td>\n");
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
		return "/* Antigravity Modern Extent Report Theme Enhancements */\n"
				+ "body, .app, .card, table {\n"
				+ "  font-family: -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, \"Helvetica Neue\", Arial, sans-serif !important;\n"
				+ "}\n"
				+ ".card {\n"
				+ "  border-radius: 10px !important;\n"
				+ "  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.04) !important;\n"
				+ "  transition: transform 0.2s ease, box-shadow 0.2s ease;\n"
				+ "}\n"
				+ ".card:hover {\n"
				+ "  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08) !important;\n"
				+ "}\n"
				+ ".class-metric-card {\n"
				+ "  transition: transform 0.2s ease;\n"
				+ "}\n"
				+ ".class-metric-card:hover {\n"
				+ "  transform: translateY(-3px);\n"
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
				+ "  background: linear-gradient(135deg, #2563eb, #1d4ed8) !important;\n"
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
			String module = "default";
			if (!tags.isEmpty()) {
				for (String tag : tags.split("\\s+")) {
					if (!tag.equalsIgnoreCase(className) && !tag.equalsIgnoreCase("advance") && !tag.equalsIgnoreCase("basic")) {
						module = tag;
						break;
					}
				}
				if ("default".equals(module)) {
					for (String tag : tags.split("\\s+")) {
						if (!tag.equalsIgnoreCase(className)) {
							module = tag;
							break;
						}
					}
				}
			}

			String qualifiedName = "default".equals(module) ? className : (module + "." + className);
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
}
