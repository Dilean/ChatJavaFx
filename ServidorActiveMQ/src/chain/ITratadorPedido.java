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
public interface ITratadorPedido {
    public boolean aceitarPedido(Pedido pedido);
    public void tratarPedido(Pedido pedido);
}
