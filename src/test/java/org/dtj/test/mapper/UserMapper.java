package org.dtj.test.mapper;

import org.dtj.test.domain.UserDO;

public interface UserMapper {

	 int insert(UserDO userDO);

	 int update(UserDO userDO);

	 int delete(UserDO userDO);

	 UserDO select(UserDO userDO);

}