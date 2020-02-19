package com.example.pocspringbatch.mappers;

import com.example.pocspringbatch.models.Conta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ContaRowMapper implements RowMapper<Conta> {

    @Override
    public Conta mapRow(ResultSet resultSet, int i) throws SQLException {
        return Conta.builder()
                .id(resultSet.getInt("id"))
                .saldo(resultSet.getBigDecimal("saldo"))
                .ultimaAtualizacao(resultSet.getDate("ultimaAtualizacao"))
                .build();
    }
}
