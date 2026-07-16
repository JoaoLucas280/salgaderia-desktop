package salgaderia.ui;

import salgaderia.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TelaMontagemCento extends JDialog {

    private Combo cento;
    private List<Item> itensSelecionados;
    private List<JSpinner> spinnersSalgados;
    private List<Produto> produtosSalgados;
    private JLabel labelTotalSalgados;
    private JButton botaoAdicionar;
    private boolean confirmado;
    private static final int LIMITE_CENTO = 100;

    public TelaMontagemCento(JFrame parent, Combo cento) {
        super(parent, "📦 Montar Cento: " + cento.getNome(), true);
        this.cento = cento;
        this.itensSelecionados = new ArrayList<>();
        this.spinnersSalgados = new ArrayList<>();
        this.produtosSalgados = new ArrayList<>();
        this.confirmado = false;

        initComponents();
        configurarJanela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel informações do cento
        JPanel painelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        painelInfo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Cento - Informações",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));
        painelInfo.add(new JLabel("Cento: " + cento.getNome()));
        painelInfo.add(new JLabel("Preço total: R$ " + String.format("%.2f", cento.getPrecoTotal().doubleValue())));
        add(painelInfo, BorderLayout.NORTH);

        // Painel central com scroll
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel salgados
        JPanel painelSalgados = new JPanel(new GridBagLayout());
        painelSalgados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🥒 Distribua os 100 Salgados",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int linha = 0;
        for (ItemCombo item : cento.getItens()) {
            Produto produto = item.getProduto();
            produtosSalgados.add(produto);

            // Nome do produto
            gbc.gridx = 0;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.5;
            JLabel labelNome = new JLabel(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + ")");
            labelNome.setFont(new Font("Arial", Font.PLAIN, 11));
            painelSalgados.add(labelNome, gbc);

            // Spinner quantidade
            gbc.gridx = 1;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.3;
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, LIMITE_CENTO, 1));
            spinner.setPreferredSize(new Dimension(60, 30));
            spinnersSalgados.add(spinner);
            spinner.addChangeListener(e -> atualizarTotalSalgados());
            painelSalgados.add(spinner, gbc);

            linha++;
        }

        // Separador
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 10, 5);
        painelSalgados.add(new JSeparator(), gbc);
        linha++;

        // Totais de salgados
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        painelSalgados.add(new JLabel("Total de Salgados:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        labelTotalSalgados = new JLabel("0 / " + LIMITE_CENTO);
        labelTotalSalgados.setFont(new Font("Arial", Font.BOLD, 12));
        labelTotalSalgados.setForeground(Color.BLUE);
        painelSalgados.add(labelTotalSalgados, gbc);

        JScrollPane scrollSalgados = new JScrollPane(painelSalgados);
        scrollSalgados.setPreferredSize(new Dimension(500, 300));
        painelCentral.add(scrollSalgados, BorderLayout.CENTER);

        add(painelCentral, BorderLayout.CENTER);

        // Painel rodapé com botões
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        painelRodape.setBorder(BorderFactory.createEtchedBorder());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        botaoAdicionar = new JButton("✅ Adicionar ao Pedido");
        botaoAdicionar.setFont(new Font("Arial", Font.BOLD, 12));
        botaoAdicionar.setBackground(new Color(0, 150, 0));
        botaoAdicionar.setForeground(Color.WHITE);
        botaoAdicionar.addActionListener(e -> adicionarItens());
        painelBotoes.add(botaoAdicionar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        botaoCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        painelBotoes.add(botaoCancelar);

        painelRodape.add(painelBotoes, BorderLayout.CENTER);
        add(painelRodape, BorderLayout.SOUTH);
    }

    private void atualizarTotalSalgados() {
        int total = 0;
        for (JSpinner spinner : spinnersSalgados) {
            total += (int) spinner.getValue();
        }

        labelTotalSalgados.setText(total + " / " + LIMITE_CENTO);

        // Validação: se ultrapassar o limite, desabilita botão
        if (total > LIMITE_CENTO) {
            labelTotalSalgados.setForeground(Color.RED);
            botaoAdicionar.setEnabled(false);
        } else if (total == LIMITE_CENTO) {
            labelTotalSalgados.setForeground(new Color(0, 150, 0));
            botaoAdicionar.setEnabled(true);
        } else {
            labelTotalSalgados.setForeground(Color.BLUE);
            botaoAdicionar.setEnabled(true);
        }
    }

    private void adicionarItens() {
        itensSelecionados.clear();
        int totalSalgados = 0;

        // Adicionar salgados
        for (int i = 0; i < spinnersSalgados.size(); i++) {
            int quantidade = (int) spinnersSalgados.get(i).getValue();
            if (quantidade > 0) {
                Produto produto = produtosSalgados.get(i);
                Item item = new Item(produto.getNomeProduto(), quantidade, produto.getPrecoUnitario());
                itensSelecionados.add(item);
                totalSalgados += quantidade;
            }
        }

        // Validar total de salgados
        if (totalSalgados > LIMITE_CENTO) {
            JOptionPane.showMessageDialog(this, "Total de salgados não pode exceder " + LIMITE_CENTO + "!");
            return;
        }

        if (itensSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um tipo de salgado!");
            return;
        }

        confirmado = true;
        dispose();
    }

    private void configurarJanela() {
        setSize(600, 450);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public List<Item> getItensSelecionados() {
        return confirmado ? itensSelecionados : null;
    }
}
