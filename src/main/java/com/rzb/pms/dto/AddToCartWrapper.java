package com.rzb.pms.dto;

import java.util.List;

import com.rzb.pms.model.DrugDispense;

public class AddToCartWrapper {

	private List<DrugDispense> cart;

	public List<DrugDispense> getCart() {
		return cart;
	}

	public void setCart(List<DrugDispense> cart) {
		this.cart = cart;
	}
	
	
}
