/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chain;

import java.util.ArrayList;
import model.Pedido;

/**
 *
 * @author Caíque
 */
public class Processador {

    private ArrayList<TratadorPedido> tratadores;
    private static Processador instancia = null;

    private Processador() {
        tratadores = new ArrayList();
        tratadores.add(new TratadorPedido("Pedido Recebido", "Aguardando Aprovação do Pagamento"));
        tratadores.add(new TratadorPedido("Aguardando Aprovação do Pagamento", ""));
        tratadores.add(new TratadorPedido("Pagamento Aprovado", "Pedido Faturado"));
        tratadores.add(new TratadorPedido("Pedido Faturado", "Pedido Preparado Para Envio"));
        tratadores.add(new TratadorPedido("Pedido Preparado Para Envio", "Pedido Enviado"));
    }

    public static Processador getInstancia() {
        if (instancia == null) {
            instancia = new Processador();
        }
        return instancia;
    }

    public void processarPedido(Pedido pedido) {
        for (TratadorPedido tratador : tratadores) {
            if (tratador.aceitarPedido(pedido)) {
                tratador.tratarPedido(pedido);
                break;
            }
        }
    }
}
