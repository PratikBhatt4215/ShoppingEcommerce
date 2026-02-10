package com.ecom.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;

import com.ecom.model.Cart;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CommonUtil commonUtil;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/addCart")
	public ResponseEntity<?> addToCart(@RequestParam Integer pid, @RequestParam Integer uid) {
		Cart saveCart = cartService.saveCart(pid, uid);

		if (ObjectUtils.isEmpty(saveCart)) {
			return ResponseEntity.internalServerError().body("Product add to cart failed");
		} else {
			return ResponseEntity.ok(saveCart);
		}
	}

	@GetMapping("/cart")
	public ResponseEntity<?> loadCartPage(Principal p) {
		UserDtls user = commonUtil.getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		return ResponseEntity.ok(carts);
	}

	@GetMapping("/cartQuantityUpdate")
	public ResponseEntity<?> updateCartQuantity(@RequestParam String sy, @RequestParam Integer cid) {
		cartService.updateQuantity(sy, cid);
		return ResponseEntity.ok("Quantity updated");
	}

	@PostMapping("/save-order")
	public ResponseEntity<?> saveOrder(@ModelAttribute OrderRequest request, Principal p) throws Exception {
		UserDtls user = commonUtil.getLoggedInUserDetails(p);
		orderService.saveOrder(user.getId(), request);
		return ResponseEntity.ok("Order placed successfully");
	}

	@GetMapping("/user-orders")
	public ResponseEntity<?> myOrder(Principal p) {
		UserDtls loginUser = commonUtil.getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrdersByUser(loginUser.getId());
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/update-status")
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

	@GetMapping("/profile")
	public ResponseEntity<?> profile(Principal p) {
		UserDtls user = commonUtil.getLoggedInUserDetails(p);
		return ResponseEntity.ok(user);
	}

	@PostMapping("/update-profile")
	public ResponseEntity<?> updateProfile(@ModelAttribute UserDtls user,
			@RequestParam(value = "img", required = false) MultipartFile img, Principal p) {

		System.out.println("Principal: " + p);
		System.out.println("Principal name: " + (p != null ? p.getName() : "null"));

		// Get the currently logged-in user to ensure we have the correct ID
		UserDtls loggedInUser = commonUtil.getLoggedInUserDetails(p);

		System.out.println("Logged in user: " + loggedInUser);
		System.out.println("Logged in user ID: " + (loggedInUser != null ? loggedInUser.getId() : "null"));

		if (loggedInUser == null) {
			return ResponseEntity.status(401).body("User not authenticated");
		}

		// Set the ID from the logged-in user to prevent null ID error
		user.setId(loggedInUser.getId());

		System.out.println("User object ID after setting: " + user.getId());

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
