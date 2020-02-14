package com.rzb.pms.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.rzb.pms.dto.DrugDTO;
import com.rzb.pms.dto.StockResponseDto;
import com.rzb.pms.exception.CustomException;

public class ReportUtills<K> {

	private static final Logger logger = LoggerFactory.getLogger(ReportUtills.class);

	@SuppressWarnings("unchecked")
	public ResponseEntity<K> generateReport(HttpServletResponse response, String exportTpe, String reportCategory,
			List<K> req) {

		boolean fileExists = false;
		File csvFile = null;
		String contentType = null;
		byte[] content = null;

		switch (reportCategory) {

		case "ALL_DRUG": {

			switch (exportTpe) {

			case "EXCEL": {
				try {
					Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

					/*
					 * CreationHelper helps us create instances of various things like DataFormat,
					 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
					 */
					CreationHelper createHelper = workbook.getCreationHelper();

					// Create a Sheet
					Sheet sheet = workbook.createSheet(reportCategory + "_Reports.xls");
					// Create a Font for styling header cells
					Font headerFont = workbook.createFont();
					headerFont.setBold(true);
					headerFont.setFontHeightInPoints((short) 14);
					headerFont.setColor(IndexedColors.BLUE.getIndex());

					// Create a CellStyle with the font
					CellStyle headerCellStyle = workbook.createCellStyle();
					headerCellStyle.setFont(headerFont);

					// Create a Row
					Row headerRow = sheet.createRow(0);

					// adding header to csv
					String[] header = { "Drug Id", "Generic Name", "Brand Name", "Composition", "Company", "Packing",
							"Drug Form", "GenericId", "Mrp" };
					// Create cells
					for (int i = 0; i < header.length; i++) {
						Cell cell = headerRow.createCell(i);
						cell.setCellValue(header[i]);
						cell.setCellStyle(headerCellStyle);
					}
					// add data to csv
					int rowNum = 1;
					for (int i = 0; i < req.size(); i++) {
						DrugDTO z = (DrugDTO) req.get(i);

						Row row = sheet.createRow(rowNum++);
						row.createCell(0).setCellValue(z.getDrugId());
						row.createCell(1).setCellValue(z.getGenericName());
						row.createCell(2).setCellValue(z.getBrandName());
						row.createCell(3).setCellValue(z.getComposition());
						row.createCell(4).setCellValue(z.getCompany());
						row.createCell(5).setCellValue(z.getPacking());
						row.createCell(6).setCellValue(z.getDrugForm());
						row.createCell(7).setCellValue(z.getGenericId());
						row.createCell(8).setCellValue(z.getMrp());

					}

					// Resize all columns to fit the content size
					for (int i = 0; i < header.length; i++) {
						sheet.autoSizeColumn(i);
					}

					// Write the output to a file
					FileOutputStream fileOut = new FileOutputStream(reportCategory + "_Reports.xls");
					workbook.write(fileOut);

					response.setContentType("application/vnd.ms-excel");
					response.setHeader("Content-Disposition",
							"attachment; filename=" + reportCategory + "_Reports.xls");
					workbook.write(response.getOutputStream());
					fileOut.close();
					workbook.close();

					return (ResponseEntity<K>) ResponseEntity.ok().body(response);

				} catch (Exception e) {
					throw new CustomException("Problem while Creating report in excel format :", e.getCause());
				}
			}

			default:
				throw new CustomException(
						"Please Provide proper export request ===>>" + "Export request:(" + exportTpe + ") not exist",
						HttpStatus.BAD_REQUEST);
			}

		}
		case "ALL_STOCK": {
			switch (exportTpe) {
			case "EXCEL": {
				try {
					Workbook workbook = new XSSFWorkbook(); // new HSSFWorkbook() for generating `.xls` file

					/*
					 * CreationHelper helps us create instances of various things like DataFormat,
					 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
					 */
					CreationHelper createHelper = workbook.getCreationHelper();

					// Create a Sheet
					Sheet sheet = workbook.createSheet(reportCategory + "_Reports.xls");
					// Create a Font for styling header cells
					Font headerFont = workbook.createFont();
					headerFont.setBold(true);
					headerFont.setFontHeightInPoints((short) 14);
					headerFont.setColor(IndexedColors.BLUE.getIndex());

					// Create a CellStyle with the font
					CellStyle headerCellStyle = workbook.createCellStyle();
					headerCellStyle.setFont(headerFont);

					// Create a Row
					Row headerRow = sheet.createRow(0);

					// adding header to csv
					String[] header = { "Stock Id", "Avl Qty Whole", "Avl Qty Trimmed", "Packing",
							"Drug Id", "Expiry Date", "Mrp", "Location", "Created By", "Created Date", "Updated By",
							"Updated Date", "Invoice Number", "Distributer Id", "Po Line Item Id", "Po Id" };
					// Create cells
					for (int i = 0; i < header.length; i++) {
						Cell cell = headerRow.createCell(i);
						cell.setCellValue(header[i]);
						cell.setCellStyle(headerCellStyle);
					}
					// add data to csv
					int rowNum = 1;
					for (int i = 0; i < req.size(); i++) {
						StockResponseDto z = (StockResponseDto) req.get(i);

						Row row = sheet.createRow(rowNum++);
						row.createCell(0).setCellValue(z.getStockId());
						row.createCell(1).setCellValue(z.getAvlQntyWhole());
						row.createCell(2).setCellValue(z.getAvlQntyTrimmed());
						row.createCell(3).setCellValue(z.getDrugId());
						row.createCell(4).setCellValue(z.getExpiryDate());
						row.createCell(5).setCellValue(z.getMrp());
						row.createCell(6).setCellValue(z.getLocation());
						row.createCell(7).setCellValue(z.getCreateddBy());
						row.createCell(8).setCellValue(z.getStockCreatedAt());
						row.createCell(9).setCellValue(z.getUpdatedBy());
						row.createCell(10).setCellValue(z.getStockUpdatedAt());
						row.createCell(11).setCellValue(z.getInvoiceReference());
						row.createCell(12).setCellValue(z.getDistributerId());
						//row.createCell(13).setCellValue(z.getPoReferenseNumber());
						//row.createCell(14).setCellValue(z.get);
						

					}

					// Resize all columns to fit the content size
					for (int i = 0; i < header.length; i++) {
						sheet.autoSizeColumn(i);
					}

					// Write the output to a file
					FileOutputStream fileOut = new FileOutputStream(reportCategory + "_Reports.xls");
					workbook.write(fileOut);

					response.setContentType("application/vnd.ms-excel");
					response.setHeader("Content-Disposition",
							"attachment; filename=" + reportCategory + "_Reports.xls");
					workbook.write(response.getOutputStream());
					fileOut.close();
					workbook.close();

					return (ResponseEntity<K>) ResponseEntity.ok().body(response);

				} catch (Exception e) {
					logger.error("Problem while exporting data in excel format:" + e);
					throw new CustomException("Problem while Creating report in excel format :", e.getCause());
				}
			}

			default:
				throw new CustomException(
						"Please Provide proper export request ===>>" + "Export request:(" + exportTpe + ") not exist",
						HttpStatus.BAD_REQUEST);
			}
		}
		default:

			throw new CustomException("Please Provide proper report category ===>>" + "Report category:("
					+ reportCategory + ") not exist", HttpStatus.BAD_REQUEST);
		}

	}
}
