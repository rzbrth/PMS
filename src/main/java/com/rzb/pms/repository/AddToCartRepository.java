package com.rzb.pms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.rzb.pms.model.AddToCart;

public interface AddToCartRepository extends JpaRepository<AddToCart, Integer>, CrudRepository<AddToCart, Integer> {

}
