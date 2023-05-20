package com.shoewear.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shoewear.model.Category;
import com.shoewear.repository.CategoryRepository;

@Service
public class CategoryService {

	
	@Autowired
	CategoryRepository categoryRepository;
	
	public void addCategory(Category category)
	{
		categoryRepository.save(category);
	}
	
	public Optional<Category> getCategoryById(int id)
	{
		return categoryRepository.findById(id);
	}
	
	public List<Category> getAllCategory()
	{
		return categoryRepository.findAll();
	}
	
	public  void removeCategoryById(int id)
	{
		categoryRepository.deleteById(id);
	}

	
	
	
	
	
}