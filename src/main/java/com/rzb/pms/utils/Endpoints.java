package com.rzb.pms.utils;

public class Endpoints {

	public static final String VERSION_1 = "/api/v1";

	// Generic
	public static final String GENERIC = "/generic";
//	public static final String ALL_GENERIC = "/findAll";
//	public static final String SEARCH_GENERIC_BY_ID = "/id/{genericId}";
//	public static final String SEARCH_GENERIC_BY_NAME = "/name/{name}";

	// Drug
	public static final String DRUG = "/drug", GET_DRUG_BY_GENERIC_ID = GENERIC + "/{genericId}" + DRUG,
			GET_DRUG_BY_GENERIC_NAME = GENERIC + "/{genericName}", ALL_MEDECINE = "/findAll",
			SEARCH_MEDECINE_BY_ID = "/{drugId}", UPDATE_DRUG_BY_ID = "/{drugId}", ADD_DRUG = "/addDrug",
			GET_DRUG_BY_COMPOSITION = "/substitute/{composition}", GET_DRUG_BY_NAME = "/name/{brandName}",
			RETURN_EXPIRED_ITEM = "/return", FIND_ALL_RETURN_REQUEST = "/getReturnReq";

	// Manufactueres
//	public static final String MANUFACTURES = "/manufacturers";
//	public static final String GET_ALL_MANUFACTURES = "/getAllManufacturers";
//	public static final String GET_MANUFACTURE_BY_ID = "/{id}";
//	public static final String GET_MEDECINE_BY_MANUFACTURE = "/{id}" + DRUG;

	// Autocomplete

	public static final String AUTOCOMPLETE = "/autocomplete", MEDECINE_AUTOCOMPLETE = DRUG;
	// public static final String GENERIC_AUTOCOMPLETE = GENERIC + MANUFACTURES;

	// Cart
	public static final String SELL = "/sell", 
			ADD_TO_CART = "/cart", 
			DISPENSE = "/dispense",
			RETURN = "/return";

	// Stock
	public static final String STOCK = "/stock", UPDATE_STOCK = "/{stockId}", GET_STOCK_BY_ID = "/{stockId}",
			DELETE_STOCK = "/{stockId}", GET_TOP4_ABOUT_TO_EXPIRE_STOCK = "/findAboutToExpireItem",
			STOCK_EXPIRE = "/expireCheck";

	// Purchase Order
	public static final String PO = "/po", ADD_PO = PO, GET_ALL_PO_WITH_EXPORT_OPTION = PO, GET_ONE = "/{poId}",
			UPDATE_PO = "/{poId}", DELETE_PO = "/{poId}";

	// Mail
	public static final String MAIL = "/sentMail";
	
	// Print
	public static final String PRINT = "/print/{id}/{printType}";
	
	

	// Auth
	public static final String AUTHENTICATION = "/auth", REFRESH = "/refresh", BLACK_LIST = "/blacklist";

	// Users
	public static final String USER = "/user";

	public static final String SIGN_UP = "/signup";

	public static final String FORGOT_PASSWORD = "/reset-password";

	public static final String EMAIL_VERIFICATION = "/verify";

	public static final String MANAGE_ACCOUNT = "/manage";

}
