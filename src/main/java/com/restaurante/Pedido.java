package com.restaurante;

import java.math.BigDecimal;

public class Pedido {
    int id;
    ItemCardapio item;
    String observacao;
    StatusPedido status;

    public Pedido(int id, ItemCardapio item, String observacao) {
        this.id = id;
        this.item = item;
        this.observacao = observacao;
        this.status = StatusPedido.RECEBIDO;
    }

    BigDecimal getValor() {
        return item.preco;
    }

    void avancarStatus() {
        switch (status) {
            case RECEBIDO:
                status = StatusPedido.EM_PREPARO;
                break;
            case EM_PREPARO:
                status = StatusPedido.PRONTO;
                break;
            case PRONTO:
                status = StatusPedido.ENTREGUE;
                break;
            case ENTREGUE:
                break;
            default:
                throw new IllegalStateException("Status do pedido desconhecido.");
        }
    }
}
