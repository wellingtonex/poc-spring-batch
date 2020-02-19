package com.example.pocspringbatch.controllers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Date;

@RestController
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class JobController {

    private final DataSource dataSource;

    @GetMapping("/teste")
    public String teste() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        for (int i = 0; i < 10000; i++) {
            jdbcTemplate.update(
                    "insert into conta(saldo, ultima_atualizacao) values (?, ?);",
                    BigDecimal.valueOf(10).multiply(BigDecimal.valueOf(i)), new Date());
        }

        return "Contas inseridas com sucesso";
    }
}
