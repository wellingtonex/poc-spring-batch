package com.example.pocspringbatch.models;

import lombok.*;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class Movimento {

    private Integer id;
    private BigDecimal valor;
    private Date data;
    private Conta conta;
    private Operacao operacao;

    @Builder
    public Movimento(BigDecimal valor, Date data, Conta conta, Operacao operacao) {
        this.valor = valor;
        this.data = data;
        this.conta = conta;
        this.operacao = operacao;
    }
}
