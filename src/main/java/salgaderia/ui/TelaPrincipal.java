package salgaderia.ui;

import salgaderia.service.BackupService;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal() {
        setTitle("🥧 Casa da Coxinha - Sistema de Gerenciamento");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(StyleConfig.COR_FUNDO);
        aplicarIconeJanela();


        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(StyleConfig.COR_FUNDO);
        tabbedPane.setForeground(StyleConfig.COR_SECUNDARIA);
        tabbedPane.setFont(StyleConfig.FONTE_NORMAL);


        TelaPedido telaPedido = new TelaPedido(this);
        tabbedPane.addTab("Pedidos", telaPedido);


        TelaHistoricoPedidos telaHistorico = new TelaHistoricoPedidos(this);
        tabbedPane.addTab("Histórico", telaHistorico);


        TelaAdmin telaAdmin = new TelaAdmin(this);
        tabbedPane.addTab("Admin", telaAdmin);


        TelaFinanceiro telaFinanceiro = new TelaFinanceiro(this);
        tabbedPane.addTab("Finanças", telaFinanceiro);


        TelaDashboard telaDashboard = new TelaDashboard(this);
        tabbedPane.addTab("Dashboard", telaDashboard);

        tabbedPane.addChangeListener(e -> {
            Component selecionado = tabbedPane.getSelectedComponent();
            if (selecionado == telaPedido) {
                telaPedido.atualizarListas();
            } else if (selecionado == telaHistorico) {
                telaHistorico.atualizarLista();
            } else if (selecionado == telaFinanceiro) {
                telaFinanceiro.atualizarLista();
            } else if (selecionado == telaDashboard) {
                telaDashboard.atualizarDashboard();
            }
        });

        add(tabbedPane, BorderLayout.CENTER);


        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rodape.setBackground(StyleConfig.COR_FUNDO);

        JButton botaoBackup = new JButton("Backup do banco");
        StyleConfig.estilizarBotaoSecundario(botaoBackup);
        botaoBackup.addActionListener(e -> realizarBackup());
        rodape.add(botaoBackup);

        JLabel labelVersao = new JLabel("v1.0 - Casa da Coxinha");
        labelVersao.setFont(StyleConfig.FONTE_PEQUENA);
        labelVersao.setForeground(StyleConfig.COR_SECUNDARIA);
        rodape.add(labelVersao);
        add(rodape, BorderLayout.SOUTH);
    }

    private void aplicarIconeJanela() {
        java.net.URL urlIcone = getClass().getResource("/icons/icone_salgaderia.png");
        if (urlIcone != null) {
            setIconImage(new ImageIcon(urlIcone).getImage());
        } else {
            System.err.println("⚠️  Ícone não encontrado em /icons/icone_salgaderia.png (classpath).");
        }
    }

    private void realizarBackup() {
        try {
            Path destino = new BackupService().realizarBackup();
            JOptionPane.showMessageDialog(
                    this,
                    "Backup criado com sucesso em:\n" + destino.toAbsolutePath(),
                    "Backup concluído",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Não foi possível criar o backup:\n" + ex.getMessage(),
                    "Erro no backup",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public static void main(String[] args) {
        StyleConfig.aplicarTema();

        SwingUtilities.invokeLater(() -> {
            TelaPrincipal tela = new TelaPrincipal();
            tela.setVisible(true);
        });
    }
}