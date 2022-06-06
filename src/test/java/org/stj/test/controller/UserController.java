package org.stj.test.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.stj.test.service.UserService;
import org.stj.test.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/getDetail")
	public UserDTO get(UserDTO dto) {
		return userService.get(dto);
	}

	@PostMapping("/add")
	public boolean add(UserDTO dto) {
		return userService.add(dto);
	}

	@PostMapping("/delete")
	public boolean delete(UserDTO dto) {
		return userService.delete(dto);
	}

	@PostMapping("/update")
	public boolean update(UserDTO dto) {
		return userService.update(dto);
	}

}