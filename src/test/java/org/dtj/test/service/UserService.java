package org.dtj.test.service;

import org.dtj.test.dto.UserDTO;

public interface UserService {

	 UserDTO get(UserDTO dto);

	 boolean add(UserDTO dto);

	 boolean delete(UserDTO dto);

	 boolean update(UserDTO dto);

}