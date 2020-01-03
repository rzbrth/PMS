package com.rzb.pms.dto;

import java.util.List;

import com.rzb.pms.model.AddToCart;

public class AddToCartWrapper {

	private List<AddToCart> cart;

	public List<AddToCart> getCart() {
		return cart;
	}

	public void setCart(List<AddToCart> cart) {
		this.cart = cart;
	}
	
	
}
