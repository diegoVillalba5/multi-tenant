package com.multitenant.repository.mapper;

import com.multitenant.dto.user.User;
import com.multitenant.dto.user.UserCredentials;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Results(id = "getUserByUsername")
    @Select("SELECT id, username, name, email, active FROM user WHERE username = #{username}")
    User getUserByUsername(String username);

    @Results(id = "getUsers")
    @Select("SELECT id, username, name, email, active FROM user")
    List<User> getUsers();

    @Results(id = "getUserCredentialsByUsername")
    @Select("SELECT username, password, active FROM user WHERE username = #{username}")
    UserCredentials getUserCredentialsByUsername(String username);

    @Results(id = "isUserActive")
    @Select("SELECT active FROM user WHERE username = #{username}")
    Boolean isUserActive(String username);
}
