/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Random;

/**
 *
 * @author Caíque
 */
public class Pedido {

    private Random gerador;
    private String estado;

    public Pedido() {
        this.estado = estado;
        this.estado = "Pedido Recebido";
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    public boolean verificarPagamentoPedidoReprovado() {
        return (estado.equals("Pagamento Reprovado"));
    }

    public boolean verificarEnviado() {
        return (estado.equals("Pedido Enviado"));
    }

    public void gerarEstadoPagamento() {
        gerador = new Random();
        if (gerador.nextBoolean()) {
            this.estado = "Pagamento Aprovado";
        } else {
            this.estado = "Pagamento Reprovado";
        }
    }

    public int gerarTempoAleatorio() {
        gerador = new Random();
        return gerador.nextInt(6000 - 4000 + 1) + 4000;
    }

}
