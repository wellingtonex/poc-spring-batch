package com.example.pocspringbatch.models;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Conta {

    private Integer id;
    private BigDecimal saldo;
    private Date ultimaAtualizacao;
}
