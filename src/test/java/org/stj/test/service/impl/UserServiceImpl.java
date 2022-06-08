package org.stj.test.service.impl;

import org.stj.test.dto.UserDTO;
import org.stj.test.domain.UserDO;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.stj.test.mapper.UserMapper;
import org.stj.test.service.UserService;

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