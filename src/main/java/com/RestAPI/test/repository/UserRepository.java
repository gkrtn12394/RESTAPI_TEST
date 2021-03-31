package com.RestAPI.test.repository;

import com.RestAPI.test.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class UserRepository {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    public int insert(UserDto user) {
        int re = this.jdbcTemplate.update("insert into users (user_name, password) values (?, ?)", user.getUsername(), user.getPassword());
        if (re == 1) {
            return findUser(user.getUsername()).getId();
        } else {
            throw new RuntimeException("insert error");
        }
    }


    public UserDto findUser(String username) {
        try {
            return this.jdbcTemplate.queryForObject("select id, user_name, password from users where user_name = ? ", new String[]{username}, new RowMapper<UserDto>() {

                @Override
                public UserDto mapRow(ResultSet rs, int i) throws SQLException {
                    UserDto user = new UserDto();

                    //가져온 사용자 정보 매핑
                    user.setId(Integer.parseInt(rs.getString("id")));
                    user.setUsername(rs.getString("user_name"));
                    user.setPassword(rs.getString("password"));

                    return user;
                }

            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }
}
