package salgaderia.ui;

import salgaderia.model.*;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TelaMontagemCento extends JDialog {

    // Um "cento" é sempre 100 salgados, por definição de negócio.
    // Não existe coluna equivalente a max_items na tabela centos porque
    // esse valor nunca muda — por isso é uma constante aqui, não um campo do model.
    private static final int TOTAL_CENTO = 100;

    private final Cento cento;
    private final List<ItemPedido> itensSelecionados;
    private final List<JSpinner> spinners;
    private final List<Produto> produtos;
    private JLabel labelTotal;
    private JButton botaoAdicionar;
    private boolean confirmado;

    public TelaMontagemCento(JFrame parent, Cento cento) {
        super(parent, "📦 Montar Cento: " + cento.getNome(), true);
        this.cento = cento;
        this.itensSelecionados = new ArrayList<>();
        this.spinners = new ArrayList<>();
        this.produtos = new ArrayList<>();
        this.confirmado = false;

        initComponents();
        setSize(550, 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ===== TÍTULO =====
        JLabel labelTitulo = new JLabel("Monte seu cento: " + cento.getNome(), SwingConstants.CENTER);
        labelTitulo.setFont(StyleConfig.FONTE_TITULO);
        labelTitulo.setForeground(StyleConfig.COR_PRIMARIA);
        add(labelTitulo, BorderLayout.NORTH);

        // ===== PAINEL DE INFORMAÇÕES =====
        JPanel painelInfo = new JPanel(new GridLayout(2, 2, 10, 5));
        painelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelInfo.add(new JLabel("Total de salgados:"));
        painelInfo.add(new JLabel(String.valueOf(TOTAL_CENTO)));
        painelInfo.add(new JLabel("Máximo de sabores:"));
        painelInfo.add(new JLabel(String.valueOf(cento.getMaxSabores())));

        // ===== PAINEL DE SABORES =====
        JPanel painelSabores = new JPanel(new GridBagLayout());
        painelSabores.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🥒 Escolha os sabores",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        if (cento.getItens() == null || cento.getItens().isEmpty()) {
            JLabel labelVazio = new JLabel("Nenhum sabor cadastrado para este cento.");
            labelVazio.setForeground(Color.RED);
            painelSabores.add(labelVazio);
        } else {
            int linha = 0;
            for (ItemCombo item : cento.getItens()) {
                Produto produto = item.getProduto();
                produtos.add(produto);

                // Nome do produto
                gbc.gridx = 0;
                gbc.gridy = linha;
                gbc.gridwidth = 1;
                gbc.weightx = 0.5;
                JLabel labelNome = new JLabel(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + ")");
                painelSabores.add(labelNome, gbc);

                // Spinner
                gbc.gridx = 1;
                gbc.gridy = linha;
                gbc.gridwidth = 1;
                gbc.weightx = 0.3;
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, TOTAL_CENTO, 1));
                spinner.setPreferredSize(new Dimension(60, 30));
                spinners.add(spinner);
                spinner.addChangeListener(e -> atualizarTotal());
                painelSabores.add(spinner, gbc);

                linha++;
            }
        }

        // ===== TOTAL =====
        JPanel painelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelTotal.add(new JLabel("Total:"));
        labelTotal = new JLabel("0 / " + TOTAL_CENTO);
        labelTotal.setFont(StyleConfig.FONTE_SUBTITULO);
        labelTotal.setForeground(StyleConfig.COR_PRIMARIA);
        painelTotal.add(labelTotal);

        // ===== BOTÕES =====
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        botaoAdicionar = new JButton("✅ Adicionar ao Pedido");
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

        // ===== ADICIONA AO PAINEL PRINCIPAL =====
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.add(painelInfo, BorderLayout.NORTH);
        painelCentral.add(new JScrollPane(painelSabores), BorderLayout.CENTER);
        painelCentral.add(painelTotal, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atualizarTotal() {
        int total = 0;
        for (JSpinner spinner : spinners) {
            total += (int) spinner.getValue();
        }

        labelTotal.setText(total + " / " + TOTAL_CENTO);

        if (total > TOTAL_CENTO) {
            labelTotal.setForeground(Color.RED);
            botaoAdicionar.setEnabled(false);
        } else {
            labelTotal.setForeground(StyleConfig.COR_PRIMARIA);
            botaoAdicionar.setEnabled(true);
        }
    }

    private void adicionarItens() {
        itensSelecionados.clear();
        int total = 0;
        int saboresEscolhidos = 0;

        for (int i = 0; i < spinners.size(); i++) {
            int quantidade = (int) spinners.get(i).getValue();
            if (quantidade > 0) {
                Produto produto = produtos.get(i);
                itensSelecionados.add(new ItemPedido(produto.getNomeProduto(), quantidade, produto.getPrecoUnitario()));
                total += quantidade;
                saboresEscolhidos++;
            }
        }

        if (total == 0) {
            JOptionPane.showMessageDialog(this, "Escolha pelo menos um salgado!");
            return;
        }

        if (total > TOTAL_CENTO) {
            JOptionPane.showMessageDialog(this, "Total excede o limite de " + TOTAL_CENTO + " salgados!");
            return;
        }

        if (saboresEscolhidos > cento.getMaxSabores()) {
            JOptionPane.showMessageDialog(this, "Você pode escolher no máximo " + cento.getMaxSabores() + " sabores!");
            return;
        }

        confirmado = true;
        dispose();
    }

    public List<ItemPedido> getItensSelecionados() {
        return confirmado ? itensSelecionados : null;
    }
}