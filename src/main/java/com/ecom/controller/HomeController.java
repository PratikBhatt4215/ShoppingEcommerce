package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

@org.springframework.web.bind.annotation.RestController
@org.springframework.web.bind.annotation.RequestMapping("/api")
@org.springframework.web.bind.annotation.CrossOrigin
public class HomeController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private CartService cartService;

	@GetMapping("/home")
	public org.springframework.http.ResponseEntity<?> index() {
		List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
				.sorted((c1, c2) -> c2.getId().compareTo(c1.getId())).limit(6).toList();
		List<Product> allActiveProducts = productService.getAllActiveProducts("").stream()
				.sorted((p1, p2) -> p2.getId().compareTo(p1.getId())).limit(8).toList();

		java.util.Map<String, Object> response = new java.util.HashMap<>();
		response.put("category", allActiveCategory);
		response.put("products", allActiveProducts);
		return org.springframework.http.ResponseEntity.ok(response);
	}

	@GetMapping("/products")
	public org.springframework.http.ResponseEntity<?> products(
			@RequestParam(value = "category", defaultValue = "") String category,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "12") Integer pageSize,
			@RequestParam(defaultValue = "") String ch) {

		Page<Product> page = null;
		if (io.micrometer.common.util.StringUtils.isEmpty(ch)) {
			page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
		} else {
			page = productService.searchActiveProductPagination(pageNo, pageSize, category, ch);
		}
		return org.springframework.http.ResponseEntity.ok(page);
	}

	@GetMapping("/product/{id}")
	public org.springframework.http.ResponseEntity<?> product(@PathVariable int id) {
		Product productById = productService.getProductById(id);
		return org.springframework.http.ResponseEntity.ok(productById);
	}

	@PostMapping("/saveUser")
	public org.springframework.http.ResponseEntity<?> saveUser(@ModelAttribute UserDtls user,
			@RequestParam(value = "img", required = false) MultipartFile file)
			throws IOException {

		Boolean existsEmail = userService.existsEmail(user.getEmail());

		if (existsEmail) {
			return org.springframework.http.ResponseEntity.badRequest().body("Email already exist");
		} else {
			String imageName = (file == null || file.isEmpty()) ? "default.jpg" : file.getOriginalFilename();
			user.setProfileImage(imageName);
			UserDtls saveUser = userService.saveUser(user);

			if (!ObjectUtils.isEmpty(saveUser)) {
				if (file != null && !file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
							+ file.getOriginalFilename());

					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				return org.springframework.http.ResponseEntity.ok("Register successfully");
			} else {
				return org.springframework.http.ResponseEntity.internalServerError().body("something wrong on server");
			}
		}
	}

	@PostMapping("/forgot-password")
	public org.springframework.http.ResponseEntity<?> processForgotPassword(@RequestParam String email,
			HttpServletRequest request)
			throws UnsupportedEncodingException, MessagingException {

		UserDtls userByEmail = userService.getUserByEmail(email);

		if (ObjectUtils.isEmpty(userByEmail)) {
			return org.springframework.http.ResponseEntity.badRequest().body("Invalid email");
		} else {

			String resetToken = UUID.randomUUID().toString();
			userService.updateUserResetToken(email, resetToken);

			String url = "http://localhost:5173/reset-password?token=" + resetToken;

			Boolean sendMail = commonUtil.sendMail(url, email);

			if (sendMail) {
				return org.springframework.http.ResponseEntity.ok("Please check your email..Password Reset link sent");
			} else {
				return org.springframework.http.ResponseEntity.internalServerError()
						.body("Somethong wrong on server ! Email not send");
			}
		}
	}

	@PostMapping("/reset-password")
	public org.springframework.http.ResponseEntity<?> resetPassword(@RequestParam String token,
			@RequestParam String password) {

		UserDtls userByToken = userService.getUserByToken(token);
		if (userByToken == null) {
			return org.springframework.http.ResponseEntity.badRequest().body("Your link is invalid or expired !!");
		} else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updateUser(userByToken);
			return org.springframework.http.ResponseEntity.ok("Password change successfully");
		}
	}
}
