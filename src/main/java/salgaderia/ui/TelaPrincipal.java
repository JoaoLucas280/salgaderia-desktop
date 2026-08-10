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

        JTabbedPane tabbedPane = new JTabbedPane();

        TelaPedido telaPedido = new TelaPedido(this);
        tabbedPane.addTab("📝 Pedidos", telaPedido);

        TelaHistoricoPedidos telaHistorico = new TelaHistoricoPedidos(this);
        tabbedPane.addTab("📜 Histórico", telaHistorico);

        TelaAdmin telaAdmin = new TelaAdmin(this);
        tabbedPane.addTab("🔧 Admin", telaAdmin);

        TelaFinanceiro telaFinanceiro = new TelaFinanceiro(this);
        tabbedPane.addTab("💰 Finanças", telaFinanceiro);

        JPanel placeholderDashboard = new JPanel();
        placeholderDashboard.add(new JLabel("📊 Dashboard - Em breve"));
        tabbedPane.addTab("📊 Dashboard", placeholderDashboard);

        tabbedPane.addChangeListener(e -> {
            Component selecionado = tabbedPane.getSelectedComponent();
            if (selecionado == telaPedido) {
                telaPedido.atualizarListas();
            } else if (selecionado == telaHistorico) {
                telaHistorico.atualizarLista();
            } else if (selecionado == telaFinanceiro) {
                telaFinanceiro.atualizarLista();
            }
        });

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