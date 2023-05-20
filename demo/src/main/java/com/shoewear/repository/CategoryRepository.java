package com.shoewear.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shoewear.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>{

}
