package com.example.pocspringbatch.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class Operacao {

    @Setter
    private Integer id;
    private String tx;
    private List<Movimento> movimentos = new ArrayList<>();

    public Operacao(String tx) {
        this.tx = tx;
    }

    public void addMovimentos(Movimento movimento) {
        this.movimentos.add(movimento);
    }
}
