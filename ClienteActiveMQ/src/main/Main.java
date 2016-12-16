/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javax.swing.UIManager;
import presenter.ViewPedidoPresenter;

/**
 *
 * @author Dilean
 */
public class Main {
    public static void main(String[] args) throws Exception{
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new ViewPedidoPresenter();
    }
}
