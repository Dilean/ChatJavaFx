/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chain;

import model.Pedido;

/**
 *
 * @author Caíque
 */
public class TratadorPedido implements ITratadorPedido {

    private String proximoEstado;
    private String estadoAtual;

    public TratadorPedido(String estadoAtual, String proximoEstado) {
        this.estadoAtual = estadoAtual;
        this.proximoEstado = proximoEstado;
    }

    @Override
    public boolean aceitarPedido(Pedido pedido) {
        return pedido.getEstado().equals(estadoAtual);
    }

    @Override
    public void tratarPedido(Pedido pedido) {
        if (pedido.getEstado().equals("Aguardando Aprovação do Pagamento")) {
            pedido.gerarEstadoPagamento();
        } else {
            pedido.setEstado(proximoEstado);
        }
    }

}
