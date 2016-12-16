/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presenter;

import view.ViewPedido;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.JMSException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Cliente;
import model.Pedido;

/**
 *
 * @author Caíque
 */
public class ViewPedidoPresenter {

    private Cliente cliente;
    private ViewPedido view;
    private Pedido pedido;

    public ViewPedidoPresenter() {
        try {
            view = new ViewPedido();
            configurarTela();
            cliente = Cliente.getInstancia(this);
            cliente.aguardaEscrita();
        } catch (Exception ex) {
            Logger.getLogger(ViewPedidoPresenter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void configurarTela() {
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        view.setTitle("Pedidos");
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.setResizable(false);
        view.getBtnConsultarPedido().setEnabled(false);
        view.getTxtStatusPedido().setEditable(false);

        view.getBtnGerarNovoPedido().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                view.getBtnGerarNovoPedido().setEnabled(false);
                view.getBtnConsultarPedido().setEnabled(true);
                pedido = new Pedido();
                try {
                    cliente.writeMessage("1;" + pedido.getEstado());
                } catch (JMSException ex) {
                    Logger.getLogger(ViewPedidoPresenter.class.getName()).log(Level.SEVERE, null, ex);
                }
                view.getTxtStatusPedido().setText(pedido.getEstado());
                view.getBarraProgressoPedido().setValue(16);
            }
        });

        view.getBtnConsultarPedido().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    cliente.writeMessage("0;consulta");
                } catch (JMSException ex) {
                    Logger.getLogger(ViewPedidoPresenter.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    public void atualizarPedido(String estadoRetornado) {
        new Thread() {
            public void run() {
                if (!estadoRetornado.equals(view.getTxtStatusPedido().getText())) {
                    if (estadoRetornado.equals("Pagamento Reprovado")) {
                        view.getTxtStatusPedido().setText("Pagamento Reprovado");
                        JOptionPane.showMessageDialog(view, "Pagamento do pedido reprovado!");
                        view.getBtnGerarNovoPedido().setEnabled(true);
                        view.getBtnConsultarPedido().setEnabled(false);
                        view.getBarraProgressoPedido().setValue(0);
                    }
                    if (estadoRetornado.equals("Pedido Enviado")) {
                        view.getBarraProgressoPedido().setValue(view.getBarraProgressoPedido().getValue() + 20);
                        view.getTxtStatusPedido().setText("Pedido Enviado");
                        JOptionPane.showMessageDialog(view, "Pedido Enviado!");
                        view.getBarraProgressoPedido().setValue(0);
                        view.getBtnGerarNovoPedido().setEnabled(true);
                        view.getBtnConsultarPedido().setEnabled(false);
                    } else {
                        view.getTxtStatusPedido().setText(estadoRetornado);
                        view.getBarraProgressoPedido().setValue(view.getBarraProgressoPedido().getValue() + 16);
                    }
                }
            }
        }
                .start();
    }

    public String getPedido() {
        return pedido.getEstado();
    }

    public boolean pedidoEnviado() {
        return pedido.equals("Pedido Enviado!");
    }

}
