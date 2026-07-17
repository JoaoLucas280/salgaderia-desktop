package salgaderia.ui;

import javax.swing.*;
import java.awt.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;  // ← IMPORTAÇÃO FALTANDO
import java.awt.event.ActionListener;

public class TelaPrincipal extends JFrame{

    private JTabbedPane tabbedPane;

    public TelaPrincipal() {
        initComponents();
        configurarJanela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));


        TelaPedido telaPedido = new TelaPedido(this);
        tabbedPane.addTab("📝 Pedidos", telaPedido);

        TelaAdmin telaAdmin = new TelaAdmin(this);
        tabbedPane.addTab("🔧 Admin", telaAdmin);

        // Placeholders
        JPanel painelEmBreve = new JPanel();
        painelEmBreve.add(new JLabel("💰 Finanças - Em breve!"));
        tabbedPane.addTab("💰 Finanças", painelEmBreve);

        JPanel painelEmBreve2 = new JPanel();
        painelEmBreve2.add(new JLabel("📊 Dashboard - Em breve!"));
        tabbedPane.addTab("📊 Dashboard", painelEmBreve2);

        add(tabbedPane, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JLabel labelVersao = new JLabel("v1.0 - Salgaderia Manager");
        labelVersao.setFont(new Font("Arial", Font.PLAIN, 11));
        labelVersao.setForeground(Color.GRAY);
        painelRodape.add(labelVersao);

        JButton botaoSair = new JButton("🚪 Sair");
        botaoSair.addActionListener(e -> System.exit(0));
        painelRodape.add(botaoSair);

        add(painelRodape, BorderLayout.SOUTH);
    }

    private void configurarJanela() {
        setTitle("🥧 Salgaderia Manager - Sistema de Gestão");
        setSize(900, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 650));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}



