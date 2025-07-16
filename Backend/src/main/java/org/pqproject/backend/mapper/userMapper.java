package org.pqproject.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.pqproject.backend.pojo.User;

@Mapper
public interface userMapper {

    @Select("SELECT * FROM usertable WHERE email = #{email}")
    public User getUserByEmail(String email);

    @Insert("INSERT INTO usertable VALUES (#{userId},#{email}, #{password}, #{username},#{userPhone}, #{role})")
    public void insertUser(User user);

    @Select("SELECT * FROM usertable WHERE userId = #{userId}")
    public User getUserById(String userId);
}
