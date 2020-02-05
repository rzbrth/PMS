package com.rzb.pms.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;

import com.rzb.pms.dto.DrugType;
import com.rzb.pms.dto.ReferenceType;
import com.rzb.pms.exception.CustomException;
import com.rzb.pms.log.Log;
import com.rzb.pms.rsql.SearchCriteria;
import com.rzb.pms.rsql.SearchKey;
import com.rzb.pms.rsql.SearchOperators;

public class BaseUtil {

	@Log
	private static Logger logger;

	/**
	 * This method will take the sort criteria and process it accordingly Only
	 * Supported sort criteria are expiryDate, unitPrice, mrp and sort orders are
	 * ASC(Ascending) or DSC(Descending)
	 * 
	 * @param sort
	 * @return sortObj
	 */
	public static Sort getSortObject(String sort) {
		Sort sortObj = null;
		String[] sorts = sort.split(",");
		for (String s : sorts) {
			if (s.split(":").length != 2) {
				logger.error("Please give a proper sort argument", HttpStatus.NOT_FOUND);
				throw new CustomException("Please give a proper sort argument", HttpStatus.NOT_FOUND);
			}
			String sortBy = s.split(":")[0];
			String sortOrder = s.split(":")[1];

			if (sortBy.equalsIgnoreCase("expiryDate") || sortBy.equalsIgnoreCase("unitPrice")
					|| sortBy.equalsIgnoreCase("mrp")) {
				if ("ASC".equals(sortOrder) || "DESC".equals(sortOrder)) {
					if (sortObj == null) {
						sortObj = Sort.by(Direction.fromString(sortOrder), sortBy);
					} else {
						sortObj = sortObj.and(Sort.by(Direction.fromString(sortOrder), sortBy));
					}
				} else {
					logger.error("Please give a proper sort Order like ASC(Ascending) or DSC(Descending) ",
							HttpStatus.NOT_FOUND);
					throw new CustomException("Please give a proper sort Order like ASC(Ascending) or DSC(Descending) ",
							HttpStatus.NOT_FOUND);

				}
			} else if (sortBy.equalsIgnoreCase("createdDate") || sortBy.equalsIgnoreCase("updatedDate")) {

				if ("ASC".equals(sortOrder) || "DESC".equals(sortOrder)) {
					if (sortObj == null) {
						sortObj = Sort.by(Direction.fromString(sortOrder), sortBy);
					} else {
						sortObj = sortObj.and(Sort.by(Direction.fromString(sortOrder), sortBy));
					}
				} else {
					logger.error("Please give a proper sort Order like ASC(Ascending) or DSC(Descending) ",
							HttpStatus.NOT_FOUND);
					throw new CustomException("Please give a proper sort Order like ASC(Ascending) or DSC(Descending) ",
							HttpStatus.NOT_FOUND);

				}

			} else {
				logger.error("Please give a proper sort argument Like expiryDate, unitPrice, mrp",
						HttpStatus.NOT_FOUND);
				throw new CustomException("Please give a proper sort argument Like expiryDate, unitPrice, mrp",
						HttpStatus.NOT_FOUND);
			}
		}
		return sortObj;
	}

	/*
	 * This method will take custom queryParam of the form (Key Operator Value) eg:
	 * company==cipla . queryParamwill be processed into SearchCriteria members like
	 * key(company), Operation(==), Value(cipla) based on the Search Key and
	 * Operator. Currently supported Search keys are genericanme, brandName,
	 * company, composition, location and Supported Search operators are ==, >=,=lk=
	 * ,<
	 */
	// "id=bt=(2,4)";// id>=2 && id<=4 //between
	public static SearchCriteria getCriteria(String queryParam) {
		Object value = null;
		String key, operator = null;
		String[] o = queryParam.split("==|>=|=lk=|<|=bt=");
		if (o.length < 2) {
			logger.error("Please provide proper search Criteria, Supported operators are ==, >=,=lk= ,< ",
					HttpStatus.BAD_REQUEST);
			throw new CustomException("Please provide proper search Criteria, Supported operators are ==, >=,=lk= ,< ",
					HttpStatus.BAD_REQUEST);
		}
		operator = queryParam.substring(o[0].length(), queryParam.length() - o[1].length()).replaceAll("\\s+", "");
		if (operator.equalsIgnoreCase("=bt=")) {

			value = null;
		}
		value = o[1].trim().replaceAll("\\s+", " ");
		key = o[0].replaceAll("\\s+", "");

		switch (SearchKey.getKeyData(key)) {

		case DRUG_NAME: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, brandName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException("Please provide proper search params, brandName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}

		case GENERIC_NAME: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, genericName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException(
						"Please provide proper search params, genericName will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}

		case COMPANY: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, company will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException("Please provide proper search params, company will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}
		case COMPOSITION: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, composition will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException(
						"Please provide proper search params, composition will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}
		}

		case LOCATION: {
			if (!(SearchOperators.LIKE.getName().equals(operator)
					|| SearchOperators.EQUALITY.getName().equals(operator))) {
				logger.error("Please provide proper search params, location will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
				throw new CustomException("Please provide proper search params, location will only support =lk= OR ==",
						HttpStatus.BAD_REQUEST);
			} else {
				return new SearchCriteria(key, operator, value);
			}

		}
		default:
			logger.error("Please Provide right search parameter . Like genericanme, brandName, company, composition, "
					+ "location, cretedBy, updatedBy, createdDate, updatedDate", HttpStatus.BAD_REQUEST);
			throw new CustomException(
					"Please Provide right search parameter . Like genericanme, brandName, company, composition, "
							+ "location, cretedBy, updatedBy, createdDate, updatedDate",
					HttpStatus.BAD_REQUEST);
		}
	}
	public static float calculatePriceAfterDiscount(float mrp, float discount, float itemSellPriceBeforeDiscount) {

		
		return ((100 - discount) * itemSellPriceBeforeDiscount) / 100;
	}

	public static String stripTrailingZero(String s) {

		return s.replaceAll("()\\.0+$|(\\..+?)0+$", "$2");

	}

	public static String getRandomPoReference(String type) {

		if (ReferenceType.PO.toString().equalsIgnoreCase(type)) {
			return "PO-" + RandomStringUtils.randomAlphabetic(4);
		} else if (ReferenceType.DIRECT_STOCK.toString().equalsIgnoreCase(type)) {
			return "DIR-" + RandomStringUtils.randomAlphabetic(4);
		}else {
			return "";
		}

	}
	
	public static String findQntyInWord(Double avlQntyInWhole, String drugForm) {

		String param = String.valueOf(avlQntyInWhole);
		String LHS = param.split("\\.")[0];
		String RHS = param.split("\\.")[1];
		String lhsDrugForm = null;

		if (drugForm.equalsIgnoreCase(DrugType.CAPSULE.toString())
				|| drugForm.equalsIgnoreCase(DrugType.TABLET.toString())) {

			lhsDrugForm = DrugType.STRIP.toString();
			String result = LHS + " " + lhsDrugForm + " " + RHS + " " + getSuffix(drugForm);
			return result;

		} else {
			lhsDrugForm = drugForm;
			String result = LHS + " " + lhsDrugForm;
			return result;
		}

	}

	public static String getSuffix(String drugForm) {

		if (drugForm.equalsIgnoreCase(DrugType.TABLET.toString())) {

			return DrugType.TABLET.toString();

		} else if (drugForm.equalsIgnoreCase(DrugType.CAPSULE.toString())) {

			return DrugType.CAPSULE.toString();
		} else {
			return drugForm;
		}

	}
}
