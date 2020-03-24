package com.rzb.pms.service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.rzb.pms.model.Distributer;
import com.rzb.pms.model.PoLineItems;
import com.rzb.pms.model.PurchaseOrder;
import com.rzb.pms.model.enums.RequestStatus;
import com.rzb.pms.repository.DistributerRepository;
import com.rzb.pms.repository.PoLineItemsRepository;
import com.rzb.pms.repository.PurchaseOrderRepository;

import io.rocketbase.commons.email.EmailTemplateBuilder;
import io.rocketbase.commons.email.TableConfig;
import io.rocketbase.commons.email.model.HtmlTextEmail;
import io.rocketbase.commons.email.template.Alignment;
import io.rocketbase.commons.email.template.ColorStyle;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailService {

	@Autowired
	private JavaMailSender emailSender;

	@Autowired
	private PoLineItemsRepository itemsRepository;

	@Autowired
	private PurchaseOrderRepository orderRepository;

	@Autowired
	private DistributerRepository distributerRepository;

	@Value("admin.email")
	String fromEmail;

	public String sendEmail(HashMap<String, String> emailData, File attachment, HtmlTextEmail htmlTextEmail,
			Boolean isAttached) {

		try {
			MimeMessage message = emailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
					StandardCharsets.UTF_8.name());
			helper.setTo(String.join(",", emailData.get("To")));
			helper.setSubject(emailData.get("Subject"));
			helper.setText(htmlTextEmail.getText(), htmlTextEmail.getHtml());
			helper.setFrom(emailData.get("From"));
			log.info("Sending mail from : {}, to : {}", emailData.get("From"), String.join(",", emailData.get("To")));
			emailSender.send(message);
			log.info("Mail sent to : {} , from : {}", String.join(",", emailData.get("To")), emailData.get("From"));
			return "Mail send Successfully";
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Problem while sending email", e);

		}

	}

	public String sentPoMail(String emailType, Integer data) {

		HtmlTextEmail htmlTextEmail = null;
		String emailTo = null;
		HashMap<String, String> mailData = new HashMap<String, String>();

		switch (emailType) {

		case "EMAIL_PO":

			Integer poId = data;
			int count = 1;
			if (poId == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Po Id can't be null");

			}

			PurchaseOrder pd = orderRepository.findById(poId).orElse(null);

			if (pd == null) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No order found for given po id : " + poId);
			}
			if (pd.getPoStatus().equalsIgnoreCase(RequestStatus.PROCESSED.toString())) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order already processed");

			}
			Distributer distData = distributerRepository.findById(pd.getDistributerId()).orElse(null);
			if (distData == null) {
				throw new ResponseStatusException(HttpStatus.NO_CONTENT,
						"Distributer info not found for id : " + pd.getDistributerId());
			}
			emailTo = distData.getEmail();
			List<PoLineItems> items = itemsRepository.findByPoId(poId);

			TableConfig table = EmailTemplateBuilder.builder().header("Purchase Order").and()
					.addText("Please process below order.").and().addTable();
			table.addHeader(
					"Po Id : " + poId + "<br>Date : " + LocalDate.now() + "<br>PO-Reference : " + pd.getPoReference(),
					true, Alignment.RIGHT);
			table.addItemRowWithPrefixMiddle("Sl No", "Item Name", "Composition", "Quantity").headerRow().nextRow();
			for (PoLineItems res : items) {
				table.addItemRowWithPrefixMiddle("(" + count++ + ")", res.getDrugName(), res.getDrugDescription(),
						res.getDrugQuantity().toString());
			}
			htmlTextEmail = table.and().addText("Thanks and Regards").and().addText("Team Pill-H").and()
					.copyright("pill-H").url("https://www.pillh.io").build();

			mailData.put("Subject", "Purchase Order");

		case "EMAIL_SELL_INVOICE":

		}

		mailData.put("To", emailTo);
		mailData.put("From", fromEmail);

		return sendEmail(mailData, null, htmlTextEmail, false);

	}

	public void sendVerificationMail(String toEmail, String userName, String link) {

		HtmlTextEmail htmlTextEmail = EmailTemplateBuilder.builder()
				.logo("https://cdn.rocketbase.io/assets/signature/rocketbase-signature-20179.png", "pillh-logo", 250,
						50)
				.title("visit pillh.io").linkUrl("https://www.pillh.io").and().header("Verification Email")
				.color(new ColorStyle("ffffff", "ff9f00")).and().addText("Dear " + userName).and()
				.addText("Please verify your email by clicking below verify button.").and()
				.addButton("Verify Email", link).color(new ColorStyle("ffffff", "348eda")).center().and()
				.addText("Thanks for choosing PILL-H.").and()
				.addImage("https://assets-cdn.github.com/images/modules/logos_page/GitHub-Logo.png",
						"rocketbase-io/email-template-builder", 250, 65)
				.center().title("link to pill-h").linkUrl("https://pillh.io").and()
				.addText("<strong><i>Cheers your Pill-H-team</i></strong>").center().and().copyright("pillh.io")
				.url("https://www.pillh.io").build();

		HashMap<String, String> mailData = new HashMap<String, String>();
		mailData.put("To", toEmail);
		mailData.put("From", fromEmail);
		mailData.put("Subject", "Verification Email");

		sendEmail(mailData, null, htmlTextEmail, false);

	}

}
