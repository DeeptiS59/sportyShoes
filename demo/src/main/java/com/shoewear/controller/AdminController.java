package com.shoewear.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.shoewear.dto.ProductDTO;
import com.shoewear.dto.ResetPasswordDTO;
import com.shoewear.model.Category;
import com.shoewear.model.Product;
import com.shoewear.model.User;
import com.shoewear.repository.PaymentRepository;
import com.shoewear.repository.UserRepository;
import com.shoewear.service.CategoryService;
import com.shoewear.service.MyUserDetailsService;
import com.shoewear.service.ProductService;



@Controller
public class AdminController {

	public static String uploadDir = System.getProperty("user.dir")+"/src/main/resources/static/productImages";

	@Autowired
	CategoryService categoryService;


	@Autowired
	ProductService productService;

	@Autowired
	UserRepository userRepository;
	@Autowired
	PaymentRepository paymentRepository;
	//category


	@GetMapping("/admin")
	public String adminHome()
	{
		return "adminHome";
	}


	@GetMapping("/admin/categories")
	public String getCat(Model model)
	{
		model.addAttribute("categories", categoryService.getAllCategory());
		return "categories";
	}

	@GetMapping("/admin/categories/add")
	public String getCatAdd(Model model)
	{
		model.addAttribute("category",new Category());
		return "categoriesAdd";
	}

	@PostMapping("/admin/categories/add")
	public String postCatAdd(@ModelAttribute("category") Category category)
	{
		categoryService.addCategory(category);
		return "redirect:/admin/categories";
	}

	@GetMapping("/admin/categories/delete/{id}")
	public String deleteCat(@PathVariable int id)
	{
		categoryService.removeCategoryById(id);
		return "redirect:/admin/categories";
	}

	@GetMapping("/admin/categories/update/{id}")
	public String updateCat(@PathVariable int id, Model model)
	{
		Optional<Category> category = categoryService.getCategoryById(id);
		model.addAttribute("category", category.get());
		return "categoriesAdd";
	}


	// product

	@GetMapping("/admin/products")
	public String products(Model model)
	{
		model.addAttribute("products",productService.getAllProduct());
		return "products";
	}

	@GetMapping("/admin/products/add")
	public String productAddGet(Model model)
	{
		model.addAttribute("productDTO", new ProductDTO());
		model.addAttribute("categories",categoryService.getAllCategory());
		return "productsAdd";
	}

	@PostMapping("/admin/products/add")
	public String productAddPost(@ModelAttribute("productDTO") ProductDTO productDTO,
			@RequestParam("productImage") MultipartFile file,
			@RequestParam("imgName") String imgName)throws IOException
	{

		Product product = new Product();
		product.setId(productDTO.getId());
		product.setName(productDTO.getName());
		product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
		product.setPrice(productDTO.getPrice());
		product.setWeight(productDTO.getWeight());
		product.setDescription(productDTO.getDescription());
		String imageUUID;
		if(!file.isEmpty())
		{
			imageUUID= file.getOriginalFilename();
			Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
			Files.write(fileNameAndPath, file.getBytes());
		}
		else
		{
			imageUUID= "default.jpg";
		}

		product.setImageName(imageUUID);
		productService.addProduct(product);


		return "redirect:/admin/products";
	}

	@GetMapping("/admin/product/delete/{id}")
	public String deleteProduct(@PathVariable long id)
	{
		productService.removeProductById(id);
		return "redirect:/admin/products";
	}

	@GetMapping("/admin/product/update/{id}")
	public String updateProductGet(@PathVariable long id, Model model)
	{
		Product product = productService.getProductById(id).get();
		ProductDTO productDTO = new ProductDTO();
		productDTO.setId(product.getId());
		productDTO.setName(product.getName());
		productDTO.setCategoryId(product.getCategory().getId());
		productDTO.setPrice(product.getPrice());
		productDTO.setWeight(product.getWeight());
		productDTO.setDescription(product.getDescription());
		productDTO.setImageName(product.getImageName());
		model.addAttribute("categories", categoryService.getAllCategory());
		model.addAttribute("productDTO",productDTO);
		return "productsAdd";
	}
	@GetMapping("/admin/users")
	public String getUser(Model model)
	{
		model.addAttribute("users", userRepository.findAll());
		return "listUser";
	}
	@GetMapping("/admin/users/{id}/purchaseHistory")
	public String getPurchaseData(Model model, @PathVariable int id,@RequestParam Optional <Integer> categoryId,@RequestParam Optional <String> from,@RequestParam Optional <String> to)
	{
		model.addAttribute("categoryList", categoryService.getAllCategory());
		model.addAttribute("userId",id);
		model.addAttribute("purchaseList", paymentRepository.findAllByUser_Id(id));
		boolean dateSent=from.isPresent()&& !from.get().isEmpty() && to.isPresent() && !to.get().isEmpty();
		
		if(categoryId.isPresent()) {
			model.addAttribute("purchaseList", paymentRepository.findAllByUser_IdAndCategory_Id(id,categoryId.get()));
		}
		if(dateSent) {
			LocalDateTime fromDate=LocalDateTime.parse(from.get().concat("T00:00:00"));
			LocalDateTime toDate=LocalDateTime.parse(to.get().concat("T23:59:59"));
			model.addAttribute("purchaseList", paymentRepository.findAllByUser_IdAndPurchaseDateBetween(id, fromDate, toDate));
		}
		if(categoryId.isPresent()&&dateSent) {
			LocalDateTime fromDate=LocalDateTime.parse(from.get().concat("T00:00:00"));
			LocalDateTime toDate=LocalDateTime.parse(to.get().concat("T23:59:59"));
			model.addAttribute("purchaseList", paymentRepository.findAllByUser_IdAndCategory_IdAndPurchaseDateBetween(id, categoryId.get(), fromDate, toDate));
		}
		return "purchaseHistory";
	}
	@GetMapping("/admin/resetPwd")
	public String getNewPwd(Model model)
	{
		Optional <User> user= userRepository.findByUserName("admin");
		if(user.isEmpty()) {
			User newUser= new User();
			newUser.setUserName("admin");
			newUser.setPassword("123");
			newUser.setRoles("ROLE_ADMIN");
			newUser.setActive(true);
			userRepository.save(newUser);
		}
		model.addAttribute("resetPwd",new ResetPasswordDTO());
		return "resetPwd";
	}
    @PostMapping("/admin/resetPwd")
    public String setNewPwd(Model model, @ModelAttribute("resetPwd") ResetPasswordDTO resetPasswordDTO) {
    	Optional <User> user= userRepository.findByUserName("admin");
    	if(user.isPresent()&& resetPasswordDTO.getCurrentPassword().equals(user.get().getPassword())) {
    		user.get().setPassword(resetPasswordDTO.getNewPassword());
    		userRepository.save(user.get());
    		return "resetPwdSuccess";
    	}
    	return "redirect:/admin/resetPwd?error";
    }
}	
