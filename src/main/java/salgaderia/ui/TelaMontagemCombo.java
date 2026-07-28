package salgaderia.ui;

import salgaderia.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TelaMontagemCombo extends JDialog {

    private Combo combo;
    private List<Item> itensSelecionados;
    private List<JSpinner> spinnersSalgados;
    private List<Produto> produtosSalgados;
    private List<JSpinner> spinnersAdicionais;  // ★ NOVO ★
    private List<Adicional> adicionaisElegiveis;  // ★ NOVO ★
    private JLabel labelTotalSalgados;
    private JButton botaoAdicionar;
    private boolean confirmado;
    private int limiteSalgados;

    public TelaMontagemCombo(JFrame parent, Combo combo) {
        super(parent, "🧩 Montar Combo: " + combo.getNome(), true);
        this.combo = combo;
        this.itensSelecionados = new ArrayList<>();
        this.spinnersSalgados = new ArrayList<>();
        this.produtosSalgados = new ArrayList<>();
        this.spinnersAdicionais = new ArrayList<>();
        this.adicionaisElegiveis = new ArrayList<>();
        this.confirmado = false;
        this.limiteSalgados = combo.getItens().stream().mapToInt(ItemCombo::getQuantidadeMaxima).sum();

        initComponents();
        configurarJanela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));


        JPanel painelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        painelInfo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Combo - Informações",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));
        painelInfo.add(new JLabel("Combo: " + combo.getNome()));
        painelInfo.add(new JLabel("Preço total: R$ " + String.format("%.2f", combo.getPrecoTotal().doubleValue())));
        add(painelInfo, BorderLayout.NORTH);


        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


        JPanel painelSalgados = new JPanel(new GridBagLayout());
        painelSalgados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🥒 Escolha os Salgados (máx " + limiteSalgados + ")",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int linha = 0;
        for (ItemCombo item : combo.getItens()) {
            Produto produto = item.getProduto();
            produtosSalgados.add(produto);

            gbc.gridx = 0;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.5;
            JLabel labelNome = new JLabel(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + ")");
            labelNome.setFont(new Font("Arial", Font.PLAIN, 11));
            painelSalgados.add(labelNome, gbc);

            gbc.gridx = 1;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.3;
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, item.getQuantidadeMaxima(), 1));
            spinner.setPreferredSize(new Dimension(60, 30));
            spinnersSalgados.add(spinner);
            spinner.addChangeListener(e -> atualizarTotalSalgados());
            painelSalgados.add(spinner, gbc);

            gbc.gridx = 2;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.2;
            painelSalgados.add(new JLabel("Máx: " + item.getQuantidadeMaxima()), gbc);

            linha++;
        }

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 5, 10, 5);
        painelSalgados.add(new JSeparator(), gbc);
        linha++;

        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        painelSalgados.add(new JLabel("Total de Salgados:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        labelTotalSalgados = new JLabel("0 / " + limiteSalgados);
        labelTotalSalgados.setFont(new Font("Arial", Font.BOLD, 12));
        painelSalgados.add(labelTotalSalgados, gbc);

        JScrollPane scrollSalgados = new JScrollPane(painelSalgados);
        scrollSalgados.setPreferredSize(new Dimension(500, 250));
        painelCentral.add(scrollSalgados, BorderLayout.CENTER);

        JPanel painelAdicionais = new JPanel(new GridBagLayout());
        painelAdicionais.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🥤 Adicionais Elegíveis",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));

        adicionaisElegiveis = combo.getAdicionaisElegiveis();

        if (adicionaisElegiveis != null && !adicionaisElegiveis.isEmpty()) {
            GridBagConstraints gbcBebidas = new GridBagConstraints();
            gbcBebidas.insets = new Insets(5, 5, 5, 5);
            gbcBebidas.fill = GridBagConstraints.HORIZONTAL;

            int linhaBebida = 0;
            for (Adicional adicional : adicionaisElegiveis) {
                gbcBebidas.gridx = 0;
                gbcBebidas.gridy = linhaBebida;
                gbcBebidas.weightx = 0.5;
                JLabel label = new JLabel(adicional.getNome() + " (R$ " + String.format("%.2f", adicional.getPreco()) + "):");
                painelAdicionais.add(label, gbcBebidas);

                gbcBebidas.gridx = 1;
                gbcBebidas.gridy = linhaBebida;
                gbcBebidas.weightx = 0.3;
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
                spinner.setPreferredSize(new Dimension(60, 30));
                spinnersAdicionais.add(spinner);
                painelAdicionais.add(spinner, gbcBebidas);

                linhaBebida++;
            }
        } else {
            painelAdicionais.add(new JLabel("Nenhum adicional elegível para este combo."));
        }

        painelCentral.add(painelAdicionais, BorderLayout.SOUTH);
        add(painelCentral, BorderLayout.CENTER);


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

        labelTotalSalgados.setText(total + " / " + limiteSalgados);

        if (total > limiteSalgados) {
            labelTotalSalgados.setForeground(Color.RED);
            botaoAdicionar.setEnabled(false);
        } else {
            labelTotalSalgados.setForeground(Color.BLACK);
            botaoAdicionar.setEnabled(true);
        }
    }

    private void adicionarItens() {
        itensSelecionados.clear();
        int totalSalgados = 0;

        // ===== SALGADOS =====
        for (int i = 0; i < spinnersSalgados.size(); i++) {
            int quantidade = (int) spinnersSalgados.get(i).getValue();
            if (quantidade > 0) {
                Produto produto = produtosSalgados.get(i);
                Item item = new Item(produto.getNomeProduto(), quantidade, produto.getPrecoUnitario());
                itensSelecionados.add(item);
                totalSalgados += quantidade;
            }
        }

        if (totalSalgados > limiteSalgados) {
            JOptionPane.showMessageDialog(this, "Total de salgados excede o limite (" + limiteSalgados + ")!");
            return;
        }

        if (totalSalgados == 0) {
            JOptionPane.showMessageDialog(this, "Adicione pelo menos um salgado!");
            return;
        }

        for (int i = 0; i < spinnersAdicionais.size(); i++) {
            int quantidade = (int) spinnersAdicionais.get(i).getValue();
            if (quantidade > 0) {
                Adicional adicional = adicionaisElegiveis.get(i);
                Item item = new Item(adicional.getNome(), quantidade, adicional.getPreco());
                itensSelecionados.add(item);
            }
        }

        confirmado = true;
        dispose();
    }

    private void configurarJanela() {
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public List<Item> getItensSelecionados() {
        return confirmado ? itensSelecionados : null;
    }
}