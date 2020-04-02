package com.rzb.pms.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.rzb.pms.model.Customer;
import com.rzb.pms.model.Dispense;
import com.rzb.pms.model.DispenseLineItems;
import com.rzb.pms.repository.CustomerRepository;
import com.rzb.pms.repository.DispenseRepository;

import io.rocketbase.commons.email.EmailTemplateBuilder;
import io.rocketbase.commons.email.TableConfig;
import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.email.template.Alignment;

@Service
public class DocumentService<K> {

	@Autowired
	private DispenseRepository dispenseRepo;

	@Autowired
	private CustomerRepository custRepo;

	public ResponseEntity<K> printInvoice(Integer id, String docType, HttpServletResponse response) {
 
		Integer dispenseId = id;

		Dispense info = dispenseRepo.findById(dispenseId).orElse(null);

		if (info == null) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Dispense info not found for id : " + dispenseId);
		}
		Customer cust = custRepo.findById(info.getCustomerId()).orElse(null);
		if (cust == null) {
			throw new ResponseStatusException(HttpStatus.NO_CONTENT,
					"Customer info not found for id : " + info.getCustomerId());
		}
		TableConfig disTable = EmailTemplateBuilder.builder().header("Sell Invoice").and().addTable();
		disTable.addHeader(
				"Seller : " + info.getSellBy() + "<br>Date : " + LocalDateTime.now() + "<br>Customer : "
						+ cust.getName() + "(" + cust.getMobileNumber() + ")" + "<br>Payment Mode : "
						+ info.getPaymentMode() + "Invoice Number : " + info.getSellInvoiceNumber(),
				true, Alignment.RIGHT);
		disTable.addItemRowWithPrefixMiddle("Item Name[Exp date]", "Quantity", "Mrp", "S.P(Tax %, Discount %)")
				.headerRow().nextRow();

		for (DispenseLineItems res : info.getDispenseLineItems()) {
			disTable.addItemRowWithPrefixMiddle(res.getBrandName(),
					res.getItemSellQuantity() + "(" + res.getDrugUnit() + ")", String.valueOf(res.getMrp()),
					res.getItemSellPrice() + "(" + res.getGstPercentage() + "," + res.getDiscount() + ")");
		}
		HtmlTextEmail htmlTextEmail = disTable.addTotalRow(BigDecimal.valueOf(info.getTotalAmountBeforeTaxAndDiscount()))
				.totalCaption("Total amount before tax and discount").borderBottom(false).nextRow()
				.addTotalRow(BigDecimal.valueOf(info.getTotalAmountBeforeTaxAndDiscount())).totalCaption("Amount Paid")
				.borderTop(false).totalCaption("<br>Total amount paid").borderBottom(true).nextRow().and()
				.addText("Thanks and Regards").and().addText("Team Pill-H").and().copyright("pill-H")
				.url("https://www.pillh.io").build();

		FileOutputStream fileOut = null;
		try {

			// Write the output to a file
			fileOut = new FileOutputStream("Sell invoice");
			// Flying Saucer part
			ITextRenderer renderer = new ITextRenderer();

			renderer.setDocument(htmlTextEmail.getText());
			renderer.layout();
			renderer.createPDF(fileOut);
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=\"Sell invoice.pdf\""); 
			renderer.createPDF(response.getOutputStream());
			fileOut.close();
		} catch (DocumentException | IOException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
					"Problem while Creating sell invoice report in pdf format :", e);
		}

		return (ResponseEntity<K>) ResponseEntity.ok().body(response);
	}

}
