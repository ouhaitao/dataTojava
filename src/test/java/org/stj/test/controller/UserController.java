package org.stj.test.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.stj.test.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.stj.test.service.UserService;

@RestController
@RequestMapping("/User")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/getDetail")
	public UserDTO get(UserDTO dto) {
		return userService.get(dto);
	}

	@PostMapping("/add")
	public boolean add(@RequestBody UserDTO dto) {
		return userService.add(dto);
	}

	@PostMapping("/delete")
	public boolean delete(@RequestBody UserDTO dto) {
		return userService.delete(dto);
	}

	@PostMapping("/update")
	public boolean update(@RequestBody UserDTO dto) {
		return userService.update(dto);
	}

}