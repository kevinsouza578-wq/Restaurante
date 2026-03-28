package com.restaurante;

public enum StatusPedido {
    RECEBIDO("Recebido"),
    EM_PREPARO("Em preparo"),
    PRONTO("Pronto para servir"),
    ENTREGUE("Entregue");

    String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

}
