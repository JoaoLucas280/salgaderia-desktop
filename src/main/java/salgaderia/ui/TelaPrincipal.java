package salgaderia.ui;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("Salgaderia Manager");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();


        TelaAdmin telaAdmin = new TelaAdmin(this);
        tabbedPane.addTab("Admin", telaAdmin);


        JPanel placeholderPedidos = new JPanel();
        placeholderPedidos.add(new JLabel("📝 Tela de Pedidos - Em breve"));
        tabbedPane.addTab("Pedidos", placeholderPedidos);

        add(tabbedPane, BorderLayout.CENTER);


        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.add(new JLabel("v1.0 - Salgaderia Manager"));
        add(rodape, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}