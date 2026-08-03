package salgaderia.ui;

import salgaderia.ui.TelaPedido;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("🥧 Salgaderia Manager");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Cria o painel com abas
        JTabbedPane tabbedPane = new JTabbedPane();

        // ★ ADICIONA A TELA DE PEDIDOS ★
        TelaPedido telaPedido = new TelaPedido(this);
        tabbedPane.addTab("📝 Pedidos", telaPedido);

        // ★ ADICIONA A TELA ADMIN ★
        TelaAdmin telaAdmin = new TelaAdmin(this);
        tabbedPane.addTab("🔧 Admin", telaAdmin);

        // Placeholders para Finanças e Dashboard
        JPanel placeholderFinancas = new JPanel();
        placeholderFinancas.add(new JLabel("💰 Finanças - Em breve"));
        tabbedPane.addTab("💰 Finanças", placeholderFinancas);

        JPanel placeholderDashboard = new JPanel();
        placeholderDashboard.add(new JLabel("📊 Dashboard - Em breve"));
        tabbedPane.addTab("📊 Dashboard", placeholderDashboard);

        add(tabbedPane, BorderLayout.CENTER);

        // Rodapé
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