package com.restaurante;

import java.math.BigDecimal;

public enum ItemCardapio {
    MARIA_IZABEL(1, "Maria Izabel", new BigDecimal("29.90")),
    MOJICA_DE_PINTADO(2, "Mojica de Pintado", new BigDecimal("34.90")),
    FAROFA_DE_BANANA(3, "Farofa de Banana com Carne Seca", new BigDecimal("24.50")),
    PACU_ASSADO(4, "Pacu Assado", new BigDecimal("42.00")),
    VACA_ATOLADA(5, "Vaca Atolada", new BigDecimal("31.90")),
    FURRUNDU(6, "Furrundu", new BigDecimal("12.00"));

    int codigo;
    String nome;
    BigDecimal preco;

    ItemCardapio(int codigo, String nome, BigDecimal preco) {
        this.codigo = codigo;
        this.nome = nome;
        this.preco = preco;
    }

    static ItemCardapio porCodigo(int codigo) {
        for (ItemCardapio item : values()) {
            if (item.codigo == codigo) {
                return item;
            }
        }
        return null;
    }
}
