package salgaderia.ui;

import salgaderia.model.ItemPedido;
import salgaderia.model.Produto;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class TelaMontagemUnitario extends JDialog {

    private final List<Produto> produtos;
    private final List<JSpinner> spinners;
    private final List<ItemPedido> itensSelecionados;
    private JLabel labelTotalItens;
    private JLabel labelTotalValor;
    private boolean confirmado;

    public TelaMontagemUnitario(JFrame parent, List<Produto> produtosUnitarios) {
        super(parent, "🥒 Montar Unitários", true);
        this.produtos = produtosUnitarios;
        this.spinners = new ArrayList<>();
        this.itensSelecionados = new ArrayList<>();
        this.confirmado = false;

        initComponents();
        setSize(500, 500);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ===== TÍTULO =====
        JLabel labelTitulo = new JLabel("Escolha os salgados e as quantidades", SwingConstants.CENTER);
        labelTitulo.setFont(StyleConfig.FONTE_TITULO);
        labelTitulo.setForeground(StyleConfig.COR_PRIMARIA);
        add(labelTitulo, BorderLayout.NORTH);

        // ===== PAINEL DE PRODUTOS =====
        JPanel painelProdutos = new JPanel(new GridBagLayout());
        painelProdutos.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🥒 Salgados disponíveis",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (produtos == null || produtos.isEmpty()) {
            JLabel labelVazio = new JLabel("Nenhum produto unitário cadastrado.");
            labelVazio.setForeground(Color.RED);
            gbc.gridx = 0;
            gbc.gridy = 0;
            painelProdutos.add(labelVazio, gbc);
        } else {
            int linha = 0;
            for (Produto produto : produtos) {
                gbc.gridx = 0;
                gbc.gridy = linha;
                gbc.gridwidth = 1;
                gbc.weightx = 0.6;
                JLabel labelNome = new JLabel(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + ")");
                painelProdutos.add(labelNome, gbc);

                gbc.gridx = 1;
                gbc.gridy = linha;
                gbc.gridwidth = 1;
                gbc.weightx = 0.2;
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 999, 1));
                spinner.setPreferredSize(new Dimension(70, 30));
                spinner.addChangeListener(e -> atualizarTotais());
                spinners.add(spinner);
                painelProdutos.add(spinner, gbc);

                linha++;
            }
        }

        JScrollPane scrollProdutos = new JScrollPane(painelProdutos);
        scrollProdutos.setPreferredSize(new Dimension(450, 300));

        // ===== TOTAIS =====
        JPanel painelTotal = new JPanel(new GridLayout(2, 1));
        painelTotal.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        labelTotalItens = new JLabel("Itens selecionados: 0");
        labelTotalItens.setFont(new Font("Arial", Font.BOLD, 12));
        painelTotal.add(labelTotalItens);

        labelTotalValor = new JLabel("Total: R$ 0,00");
        labelTotalValor.setFont(StyleConfig.FONTE_SUBTITULO);
        labelTotalValor.setForeground(StyleConfig.COR_PRIMARIA);
        painelTotal.add(labelTotalValor);

        // ===== BOTÕES =====
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoAdicionar = new JButton("✅ Adicionar ao Pedido");
        StyleConfig.estilizarBotao(botaoAdicionar, StyleConfig.COR_SUCESSO);
        botaoAdicionar.addActionListener(e -> adicionarItens());
        painelBotoes.add(botaoAdicionar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        StyleConfig.estilizarBotao(botaoCancelar, StyleConfig.COR_ERRO);
        botaoCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        painelBotoes.add(botaoCancelar);

        // ===== MONTAGEM FINAL =====
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.add(scrollProdutos, BorderLayout.CENTER);
        painelCentral.add(painelTotal, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atualizarTotais() {
        int totalItens = 0;
        BigDecimal totalValor = BigDecimal.ZERO;

        for (int i = 0; i < spinners.size(); i++) {
            int quantidade = (int) spinners.get(i).getValue();
            if (quantidade > 0) {
                totalItens += quantidade;
                totalValor = totalValor.add(
                        produtos.get(i).getPrecoUnitario().multiply(BigDecimal.valueOf(quantidade))
                );
            }
        }

        labelTotalItens.setText("Itens selecionados: " + totalItens);
        labelTotalValor.setText("Total: R$ " + String.format("%.2f", totalValor.doubleValue()));
    }

    private void adicionarItens() {
        itensSelecionados.clear();

        for (int i = 0; i < spinners.size(); i++) {
            int quantidade = (int) spinners.get(i).getValue();
            if (quantidade > 0) {
                Produto produto = produtos.get(i);
                itensSelecionados.add(new ItemPedido(produto.getNomeProduto(), quantidade, produto.getPrecoUnitario()));
            }
        }

        if (itensSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Escolha pelo menos um salgado!");
            return;
        }

        confirmado = true;
        dispose();
    }

    public List<ItemPedido> getItensSelecionados() {
        return confirmado ? itensSelecionados : null;
    }
}
