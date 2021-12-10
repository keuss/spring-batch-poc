package com.example.batch.demobatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class CoffeeRowMapper implements RowMapper<Coffee> {
    @Override
    public Coffee mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("mapRow rowNum:[{}]", rowNum);
        Coffee c = new Coffee();
        c.setBrand(rs.getString("brand"));
        c.setOrigin(rs.getString("origin"));
        c.setCharacteristics(rs.getString("characteristics"));
        return c;
    }
}
