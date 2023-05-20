package com.shoewear.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.shoewear.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Integer> {
	List<Cart> findAllByUser_Id(int id);
}


