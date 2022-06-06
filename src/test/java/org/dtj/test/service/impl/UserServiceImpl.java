package org.dtj.test.service.impl;

import org.springframework.stereotype.Service;
import org.dtj.test.service.UserService;
import org.dtj.test.domain.UserDO;
import org.dtj.test.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.dtj.test.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public UserDTO get(UserDTO dto) {
		return transfer(userMapper.select(transfer(dto)));
	}

	@Override
	public boolean add(UserDTO dto) {
		return userMapper.insert(transfer(dto)) > 0;
	}

	@Override
	public boolean delete(UserDTO dto) {
		return userMapper.delete(transfer(dto)) > 0;
	}

	@Override
	public boolean update(UserDTO dto) {
		return userMapper.update(transfer(dto)) > 0;
	}

	private UserDO transfer(UserDTO dto) {
		if (dto == null) {
			return null;
		}
		UserDO domain = new UserDO();
		domain.setId(dto.getId());
		domain.setUsername(dto.getUsername());
		domain.setPassword(dto.getPassword());
		return domain;
	}

	private UserDTO transfer(UserDO domain) {
		if (domain == null) {
			return null;
		}
		UserDTO dto = new UserDTO();
		dto.setId(domain.getId());
		dto.setUsername(domain.getUsername());
		dto.setPassword(domain.getPassword());
		return dto;
	}

}