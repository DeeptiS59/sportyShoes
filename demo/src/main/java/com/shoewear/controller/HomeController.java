package com.shoewear.controller;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.shoewear.model.Category;
import com.shoewear.model.Product;
import com.shoewear.model.User;
import com.shoewear.model.Cart;
import com.shoewear.model.PaymentHistory;
import com.shoewear.repository.CartRepository;
import com.shoewear.repository.PaymentRepository;
import com.shoewear.repository.UserRepository;
import com.shoewear.service.CategoryService;
import com.shoewear.service.ProductService;

@Controller
public class HomeController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	CartRepository cartRepository;
	@Autowired
	PaymentRepository paymentRepository;

	@GetMapping({"/","/home"})
	public String home(Authentication authentication, Model model)
	{
		Boolean isAdmin=authentication.getAuthorities().stream().anyMatch(item -> item.getAuthority().equals("ROLE_ADMIN"));
		model.addAttribute("isAdmin", isAdmin);
		return "index";
	}

	@GetMapping("/shop")
	public String shop(Model model)
	{
		model.addAttribute("categories", categoryService.getAllCategory());
		model.addAttribute("products", productService.getAllProduct());
		return "shop";
	}

	@GetMapping("/login")
	public String login(Model model)
	{
		return "login";
	}
	@GetMapping("/logoutSuccessful")
	public String logout(Model model)
	{
		return "logoutSuccessful";
	}

	@GetMapping("/signup")
	public String signup(Model model)
	{
		model.addAttribute("user",new User());
		return "signUp";
	}
	@PostMapping("/signupUser")
	public String signupUser(@ModelAttribute("user") User user)
	{
		user.setActive(true);
		user.setRoles("ROLE_USER");
		userRepository.save(user);
		return "signupSuccess";
	}
	@GetMapping("shop/category/{id}")
	public String shopByCategory(Model model, @PathVariable int id)
	{
		model.addAttribute("categories", categoryService.getAllCategory());
		model.addAttribute("products", productService.getAllProductByCategoryId(id));
		return "shop";
	}

	@GetMapping("/shop/viewproduct/{id}")
	public String viewProduct(Model model,@PathVariable long id)
	{
		//model.addAttribute("products", productService.getProductById(id).get());
		Optional<Product> product = productService.getProductById(id);
		model.addAttribute("product", product.get());
		return "viewProduct";
	}
	@GetMapping("/addToCart/{id}")
	public String addtocart(Model model,@PathVariable long id, Authentication authentication)
	{
		Cart cart= new Cart();
		Optional<Product> product = productService.getProductById(id);
		Optional<User> user=userRepository.findByUserName(authentication.getName());
		if(user.isPresent()) {
			cart.setUser(user.get());
			cart.setProduct(product.get());
			cartRepository.save(cart);
		}
		model.addAttribute("product", product.get());
		model.addAttribute("cartAdded", true);
		return "viewProduct";
	}
	@GetMapping("/cart")
	public String cart(Model model, Authentication authentication)
	{
		cartRepository.findAllByUser_Id(0);
		Optional<User> user=userRepository.findByUserName(authentication.getName());
		if(user.isPresent()) {
			List <Cart> cartList= cartRepository.findAllByUser_Id(user.get().getId());
			double sum=0;
			for(Cart cart:cartList) {
				sum=sum+cart.getProduct().getPrice();
			}
			model.addAttribute("sum",sum);
			model.addAttribute("cartProducts",cartList);
		}
		return "Cart";
	}
	@GetMapping("/payment")
	public String makePayment(Model model, Authentication authentication)
	{
		return "payment";
	}
	@GetMapping("/paymentReceived")
	public String receivePayment(Model model, Authentication authentication)
	{
		cartRepository.findAllByUser_Id(0);
		Optional<User> user=userRepository.findByUserName(authentication.getName());
		if(user.isPresent()) {
			List <Cart> cartItems= cartRepository.findAllByUser_Id(user.get().getId());
			for(Cart cart:cartItems) {
				PaymentHistory paymentHistory= new PaymentHistory();
				paymentHistory.setProduct(cart.getProduct());
				paymentHistory.setUser(cart.getUser());
				paymentHistory.setCategory(cart.getProduct().getCategory());
				paymentHistory.setPurchaseDate(LocalDateTime.now());
				paymentRepository.save(paymentHistory);
				cartRepository.delete(cart);
			}
		}
		return "paymentReceived"; 
	}
	@GetMapping("/cart/delete/{id}")
	public String deleteCartItem(@PathVariable int id)
	{
		cartRepository.deleteById(id);
		return "redirect:/cart";
	}
	
}