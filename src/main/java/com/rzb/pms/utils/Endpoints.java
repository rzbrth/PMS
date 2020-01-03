package com.rzb.pms.utils;

public class Endpoints {

	public static final String VERSION_1 = "/api/v1";
	public static final String STOCK_LIST = "/stocks";
	public static final String DISTRIBUTER_LIST = "/distributers";
	
	//Generic
	public static final String GENERIC = "/generic";
	public static final String ALL_GENERIC = "/findAll";
	public static final String SEARCH_GENERIC_BY_ID = "/{genericId}";
	public static final String SEARCH_GENERIC_BY_NAME = "/{name}";
    
	//Drug
	public static final String DRUG = "/drug"; 
	//public static final String GET_DRUG_BY_GENERIC = GENERIC + "/{id}" + DRUG ;
	public static final String ALL_MEDECINE = "/findAll";
	public static final String SEARCH_MEDECINE_BY_ID = "/{drugId}";
	public static final String ADD_DRUG = "/addDrug";
	public static final String UPDATE_DRUG_BY_ID ="/{drugId}";
	
	//Manufactueres
	public static final String MANUFACTURES = "/manufacturers"; 
	public static final String GET_ALL_MANUFACTURES = "/getAllManufacturers";
	public static final String GET_MANUFACTURE_BY_ID = "/{id}";
	//public static final String GET_MEDECINE_BY_MANUFACTURE = "/{id}" + DRUG;
	
	//Autocomplete
	
	public static final String AUTOCOMPLETE = "/autocomplete";
	public static final String MEDECINE_AUTOCOMPLETE = DRUG + MANUFACTURES;
	//public static final String GENERIC_AUTOCOMPLETE = GENERIC + MANUFACTURES;
	
	//Cart
	public static final String CART ="/cart";
	
	public static final String ADD_TO_CART = "/addToCart";
	
	public static final String REMOVE_FROM_CART = "/removeFromCart";
	
	public static final String RETRIVE_FROM_CART = "/getCartItem";
	
	
	
}
