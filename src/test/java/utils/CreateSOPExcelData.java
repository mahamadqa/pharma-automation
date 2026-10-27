package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.Test;

public class CreateSOPExcelData {

	@Test
	public void createExcel() throws IOException {
		Workbook workbook = new XSSFWorkbook();

		// Sheet 1: SOP_Contents
		Sheet contentSheet = workbook.createSheet("SOP_Contents");
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);

		Row headerRow = contentSheet.createRow(0);
		Cell c0 = headerRow.createCell(0);
		c0.setCellValue("Section");
		c0.setCellStyle(headerStyle);
		Cell c1 = headerRow.createCell(1);
		c1.setCellValue("Content");
		c1.setCellStyle(headerStyle);

		String[][] contentData = {
				{ "Objective", "The purpose of this SOP is to provide stepwise instructions for the Annual Building Maintenance." },
				{ "Scope", "This Procedure is applicable to Annual Building Maintenance of factory building in the manufacturing plant located at all the Locations." },
				{ "Responsibility", "Engineering Officer / Designee: The engineering officer / designee is responsible to follow the procedure as per SOP of Annual Building Maintenance. Head of Department: To review the SOP and suggest necessary changes if required to the designee for further corrections. HOD shall ensure that engineering officer / designee are trained and familiarized with the SOPs and the existing relevant technical guidelines mentioned in the SOPs. QA Head / Designee: QA Head / Designee is responsible for Review and Approval of this SOP." },
				{ "Procedure", "Carry Out The Following Activities As Per Yearly or As & When Required Schedule: The area shall be inspected by the respective user department personnel with engineering personal on yearly basis to assess the building maintenance status. The required work shall be informed to engineering department through building maintenance requisition in Format No.:EMD/030-F01 titled \"Requisition of Building Maintenance\". After completion of work, it shall be checked by respective user department. In addition, as part of preventive maintenance the area shall be inspected by Maintenance personnel to assess the building maintenance status. The observation will be reported in Format No.: EMD/030-F02 titled \"Checklist for Building Maintenance \" and on completion of the work it shall be intimated to Head Engineering and Head Q.A. Cracks or damages are noticed then arrange for repairs, ensuring no product (at any stage) is lying in the area requiring repairs, preferably during holiday or complete shutdown of the plant after covering of all the equipment by Concerned Departments. Ensure the proper removal of the rubble or building materials after completion of the repair. For any work mentioned in scope by concerned department, work permit shall be raised and approval to be taken from Engineering department, concerned department and Quality Assurance. Civil Work: Work order shall be initiated by Sr. Technician / Officer / Executive Engineer and handed over to concerned agency. The area shall be spared and equipment shall be covered by concerned department If any product or material is lying in same area, it shall be removed On completion of work, area shall be cleaned and sanitized (if required). Concerned department / Quality Assurance (wherever applicable) shall inspect area. Painting Work: Work order shall be initiated by Sr. Technician / Officer / Executive Engineer and handed over to concerned agency. Painting shall be carried out after any modification in civil work of any part of building. The area shall be spared and equipment shall be covered by concerned department." },
				{ "AnnexureContent", "YEAR :- ___________ SR NO CHECKLIST SCHEDULE JAN FEB MAR APR MAY JUN JULY AUG SEP OCT NOV DEC 1 Facility Room Checklist PLANNED Q Q Q Q ACTUAL 2 Paint Record PLANNED H H ACTUAL 3 Roof Leak Check Record PLANNED H H ACTUAL 4 Crack Inspection Record PLANNED H H ACTUAL 5 Epoxy / Kota Flooring Inspection Record PLANNED H H ACTUAL" },
				{ "FormatContent", "SR NO. Name of the Area Type of paint Paint Done on Checked by(Sign/Date) Remark" }
		};

		int rowIdx = 1;
		for (String[] rowData : contentData) {
			Row row = contentSheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(rowData[0]);
			row.createCell(1).setCellValue(rowData[1]);
		}
		contentSheet.autoSizeColumn(0);

		// Sheet 2: SOP_Data
		Sheet dataSheet = workbook.createSheet("SOP_Data");
		Row dataHeader = dataSheet.createRow(0);
		Cell d0 = dataHeader.createCell(0);
		d0.setCellValue("Key");
		d0.setCellStyle(headerStyle);
		Cell d1 = dataHeader.createCell(1);
		d1.setCellValue("Value");
		d1.setCellStyle(headerStyle);

		String[][] kvData = {
				{ "sop.docType", "SOP" },
				{ "sop.description", "ANNUAL BUILDING MAINTENANCE" },
				{ "sop.department", "Maintenance" },
				{ "sop.respDept", "Maintenance,Quality Assurance" },
				{ "sop.masterCopyOwner", "Quality Assurance" },
				{ "sop.supersedesNo", "NIL" },
				{ "sop.abbreviation.qa.short", "QA" },
				{ "sop.abbreviation.qa.full", "Quality Assurance" },
				{ "sop.abbreviation.sop.short", "SOP" },
				{ "sop.abbreviation.sop.full", "Standard Operating Procedure" },
				{ "sop.annexure.name", "Annexure-I" },
				{ "sop.annexure.title", "BUILDING MAINTENANCE SCHEDULE" },
				{ "sop.annexure.printPaper", "A4 Landscape" },
				{ "sop.annexure.supersedesNo", "NIL" },
				{ "sop.format.docType", "SOP Format" },
				{ "sop.format.name", "PAINT RECORD" },
				{ "sop.format.printPaper", "A4 Portrait" },
				{ "sop.format.supersedesNo", "NIL" },
				{ "sop.distribution.dept1", "Maintenance" },
				{ "sop.distribution.copies1", "1" },
				{ "sop.distribution.dept2", "Quality Assurance" },
				{ "sop.distribution.copies2", "1" },
				{ "sop.dms.contentType", "Training Completion Report" },
				{ "sop.revision.reason", "Annual Review & building maintenance checklist update" },
				{ "sop.revision.changeControlNo", "CC-2026-001" },
				{ "sop.digitalSignature.dept", "Maintenance" }
		};

		rowIdx = 1;
		for (String[] rowData : kvData) {
			Row row = dataSheet.createRow(rowIdx++);
			row.createCell(0).setCellValue(rowData[0]);
			row.createCell(1).setCellValue(rowData[1]);
		}
		dataSheet.autoSizeColumn(0);

		File dest = new File("src/test/resources/testData_SOP/sop_contents.xlsx");
		dest.getParentFile().mkdirs();
		try (FileOutputStream fos = new FileOutputStream(dest)) {
			workbook.write(fos);
		}
		workbook.close();
		System.out.println("Created: " + dest.getAbsolutePath());
	}
}
