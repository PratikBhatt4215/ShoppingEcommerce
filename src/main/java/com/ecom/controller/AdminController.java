package com.ecom.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private UserService userService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/category")
	public ResponseEntity<?> category(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		Page<Category> page = categoryService.getAllCategorPagination(pageNo, pageSize);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/category/{id}")
	public ResponseEntity<?> getCategory(@PathVariable int id) {
		return ResponseEntity.ok(categoryService.getCategoryById(id));
	}

	@PostMapping("/saveCategory")
	public ResponseEntity<?> saveCategory(@ModelAttribute Category category,
			@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

		String imageName = (file != null && !file.isEmpty()) ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());

		if (existCategory) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Category Name already exists");
		} else {

			Category saveCategory = categoryService.saveCategory(category);

			if (ObjectUtils.isEmpty(saveCategory)) {
				return ResponseEntity.internalServerError().body("Not saved ! internal server error");
			} else {

				if (file != null && !file.isEmpty()) {
					File saveFile = new ClassPathResource("static/img").getFile();

					Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
							+ file.getOriginalFilename());

					Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				}
				return ResponseEntity.ok(saveCategory);
			}
		}
	}

	@GetMapping("/deleteCategory/{id}")
	public ResponseEntity<?> deleteCategory(@PathVariable int id) {
		Boolean deleteCategory = categoryService.deleteCategory(id);

		if (deleteCategory) {
			return ResponseEntity.ok("category delete success");
		} else {
			return ResponseEntity.internalServerError().body("something wrong on server");
		}
	}

	@PostMapping("/updateCategory")
	public ResponseEntity<?> updateCategory(@ModelAttribute Category category,
			@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

		Category oldCategory = categoryService.getCategoryById(category.getId());
		String imageName = (file == null || file.isEmpty()) ? oldCategory.getImageName() : file.getOriginalFilename();

		if (!ObjectUtils.isEmpty(category)) {

			oldCategory.setName(category.getName());
			oldCategory.setIsActive(category.getIsActive());
			oldCategory.setImageName(imageName);
		}

		Category updateCategory = categoryService.saveCategory(oldCategory);

		if (!ObjectUtils.isEmpty(updateCategory)) {

			if (file != null && !file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator
						+ file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			return ResponseEntity.ok(updateCategory);
		} else {
			return ResponseEntity.internalServerError().body("something wrong on server");
		}
	}

	@PostMapping("/saveProduct")
	public ResponseEntity<?> saveProduct(@ModelAttribute Product product,
			@RequestParam(value = "file", required = false) MultipartFile image) throws IOException {

		String imageName = (image == null || image.isEmpty()) ? "default.jpg" : image.getOriginalFilename();

		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {

			if (image != null && !image.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator
						+ image.getOriginalFilename());

				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			return ResponseEntity.ok(saveProduct);
		} else {
			return ResponseEntity.internalServerError().body("something wrong on server");
		}
	}

	@GetMapping("/products")
	public ResponseEntity<?> loadViewProduct(@RequestParam(defaultValue = "") String ch,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		Page<Product> page = null;
		if (ch != null && ch.length() > 0) {
			page = productService.searchProductPagination(pageNo, pageSize, ch);
		} else {
			page = productService.getAllProductsPagination(pageNo, pageSize);
		}
		return ResponseEntity.ok(page);
	}

	@GetMapping("/deleteProduct/{id}")
	public ResponseEntity<?> deleteProduct(@PathVariable int id) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			return ResponseEntity.ok("Product delete success");
		} else {
			return ResponseEntity.internalServerError().body("Something wrong on server");
		}
	}

	@PostMapping("/updateProduct")
	public ResponseEntity<?> updateProduct(@ModelAttribute Product product,
			@RequestParam(value = "file", required = false) MultipartFile image) {

		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			return ResponseEntity.badRequest().body("invalid Discount");
		} else {
			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				return ResponseEntity.ok(updateProduct);
			} else {
				return ResponseEntity.internalServerError().body("Something wrong on server");
			}
		}
	}

	@GetMapping("/users")
	public ResponseEntity<?> getAllUsers(@RequestParam Integer type) {
		List<UserDtls> users = null;
		if (type == 1) {
			users = userService.getUsers("ROLE_USER");
		} else {
			users = userService.getUsers("ROLE_ADMIN");
		}
		return ResponseEntity.ok(users);
	}

	@GetMapping("/updateSts")
	public ResponseEntity<?> updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id) {
		Boolean f = userService.updateAccountStatus(id, status);
		if (f) {
			return ResponseEntity.ok("Account Status Updated");
		} else {
			return ResponseEntity.internalServerError().body("Something wrong on server");
		}
	}

	@GetMapping("/orders")
	public ResponseEntity<?> getAllOrders(@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
		Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
		return ResponseEntity.ok(page);
	}

	@PostMapping("/update-order-status")
	public ResponseEntity<?> updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st) {

		OrderStatus[] values = OrderStatus.values();
		String status = null;

		for (OrderStatus orderSt : values) {
			if (orderSt.getId().equals(st)) {
				status = orderSt.getName();
			}
		}

		ProductOrder updateOrder = orderService.updateOrderStatus(id, status);

		try {
			commonUtil.sendMailForProductOrder(updateOrder, status);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (!ObjectUtils.isEmpty(updateOrder)) {
			return ResponseEntity.ok(updateOrder);
		} else {
			return ResponseEntity.internalServerError().body("status not updated");
		}
	}

	@GetMapping("/search-order")
	public ResponseEntity<?> searchProduct(@RequestParam String orderId,
			@RequestParam(name = "pageNo", defaultValue = "0") Integer pageNo,
			@RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

		if (orderId != null && orderId.length() > 0) {
			ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());
			if (ObjectUtils.isEmpty(order)) {
				return ResponseEntity.notFound().build();
			} else {
				return ResponseEntity.ok(order);
			}
		} else {
			Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
			return ResponseEntity.ok(page);
		}
	}

	@PostMapping("/save-admin")
	public ResponseEntity<?> saveAdmin(@ModelAttribute UserDtls user,
			@RequestParam(value = "img", required = false) MultipartFile file)
			throws IOException {

		String imageName = (file == null || file.isEmpty()) ? "default.jpg" : file.getOriginalFilename();
		user.setProfileImage(imageName);
		UserDtls saveUser = userService.saveAdmin(user);

		if (!ObjectUtils.isEmpty(saveUser)) {
			if (file != null && !file.isEmpty()) {
				File saveFile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
						+ file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}
			return ResponseEntity.ok(saveUser);
		} else {
			return ResponseEntity.internalServerError().body("something wrong on server");
		}
	}

	@GetMapping("/profile")
	public ResponseEntity<?> profile(Principal p) {
		UserDtls user = commonUtil.getLoggedInUserDetails(p);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/update-profile")
	public ResponseEntity<?> updateProfile(@ModelAttribute UserDtls user,
			@RequestParam(value = "img", required = false) MultipartFile img) {
		UserDtls updateUserProfile = userService.updateUserProfile(user, img);
		if (ObjectUtils.isEmpty(updateUserProfile)) {
			return ResponseEntity.internalServerError().body("Profile not updated");
		} else {
			return ResponseEntity.ok(updateUserProfile);
		}
	}

	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestParam String newPassword, @RequestParam String currentPassword,
			Principal p) {
		UserDtls loggedInUserDetails = commonUtil.getLoggedInUserDetails(p);

		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());

		if (matches) {
			String encodePassword = passwordEncoder.encode(newPassword);
			loggedInUserDetails.setPassword(encodePassword);
			UserDtls updateUser = userService.updateUser(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updateUser)) {
				return ResponseEntity.internalServerError().body("Password not updated !! Error in server");
			} else {
				return ResponseEntity.ok("Password Updated sucessfully");
			}
		} else {
			return ResponseEntity.badRequest().body("Current Password incorrect");
		}
	}

}
