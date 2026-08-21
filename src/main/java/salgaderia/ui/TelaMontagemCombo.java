package salgaderia.ui;

import salgaderia.model.*;
import salgaderia.model.enums.TipoItemPedido;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaMontagemCombo extends JDialog {

    private final Combo combo;
    private final List<ItemPedido> itensSelecionados;
    private final List<JSpinner> spinners;
    private final List<Produto> produtos;
    private final List<JCheckBox> checkBoxesAdicionais;
    private final List<Adicional> adicionaisElegiveis;
    private JLabel labelTotal;
    private JButton botaoAdicionar;
    private boolean confirmado;

    public TelaMontagemCombo(JFrame parent, Combo combo) {
        super(parent, "🧩 Montar Combo: " + combo.getNome(), true);
        this.combo = combo;
        this.itensSelecionados = new ArrayList<>();
        this.spinners = new ArrayList<>();
        this.produtos = new ArrayList<>();
        this.checkBoxesAdicionais = new ArrayList<>();
        this.adicionaisElegiveis = combo.getAdicionaisElegiveis();
        this.confirmado = false;

        initComponents();
        setSize(550, combo.temAdicionaisElegiveis() ? 560 : 450);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));


        JLabel labelTitulo = new JLabel("Monte seu combo: " + combo.getNome(), SwingConstants.CENTER);
        labelTitulo.setFont(StyleConfig.FONTE_TITULO);
        labelTitulo.setForeground(StyleConfig.COR_PRIMARIA);
        add(labelTitulo, BorderLayout.NORTH);

        JPanel painelInfo = new JPanel(new GridLayout(2, 2, 10, 5));
        painelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelInfo.add(new JLabel("Total de salgados:"));
        painelInfo.add(new JLabel(String.valueOf(combo.getQuantidadeMaximaDeItems())));
        painelInfo.add(new JLabel("Máximo de sabores:"));
        painelInfo.add(new JLabel(String.valueOf(combo.getQuantidadeMaximaDeFlavors())));

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

        if (combo.getItens() == null || combo.getItens().isEmpty()) {
            JLabel labelVazio = new JLabel("Nenhum sabor cadastrado para este combo.");
            labelVazio.setForeground(Color.RED);
            painelSabores.add(labelVazio);
        } else {
            int linha = 0;
            for (ItemCombo item : combo.getItens()) {
                Produto produto = item.getProduto();
                produtos.add(produto);

                gbc.gridx = 0;
                gbc.gridy = linha;
                gbc.gridwidth = 1;
                gbc.weightx = 0.5;
                JLabel labelNome = new JLabel(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + ")");
                painelSabores.add(labelNome, gbc);

                gbc.gridx = 1;
                gbc.gridy = linha;
                gbc.gridwidth = 1;
                gbc.weightx = 0.3;
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, combo.getQuantidadeMaximaDeItems(), 1));
                spinner.setPreferredSize(new Dimension(60, 30));
                spinners.add(spinner);
                spinner.addChangeListener(e -> atualizarTotal());
                painelSabores.add(spinner, gbc);

                linha++;
            }
        }

        JPanel painelAdicionais = new JPanel(new GridBagLayout());
        if (combo.temAdicionaisElegiveis()) {
            painelAdicionais.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createEtchedBorder(),
                    "🥤 Adicionais (escolha até " + combo.getQuantidadeAdicionaisPermitidos() + ")",
                    TitledBorder.DEFAULT_JUSTIFICATION,
                    TitledBorder.DEFAULT_POSITION,
                    new Font("Arial", Font.BOLD, 12)
            ));

            GridBagConstraints gbcAd = new GridBagConstraints();
            gbcAd.insets = new Insets(5, 10, 5, 10);
            gbcAd.fill = GridBagConstraints.HORIZONTAL;
            gbcAd.gridx = 0;
            gbcAd.weightx = 1;

            int linhaAd = 0;
            for (Adicional adicional : adicionaisElegiveis) {
                gbcAd.gridy = linhaAd;
                JCheckBox checkBox = new JCheckBox(adicional.getNome() + " (grátis no combo)");
                checkBox.addActionListener(e -> atualizarLimiteAdicionais());
                checkBoxesAdicionais.add(checkBox);
                painelAdicionais.add(checkBox, gbcAd);
                linhaAd++;
            }
        }

        JPanel painelSaboresEAdicionais = new JPanel();
        painelSaboresEAdicionais.setLayout(new BoxLayout(painelSaboresEAdicionais, BoxLayout.Y_AXIS));
        painelSaboresEAdicionais.add(painelSabores);
        if (combo.temAdicionaisElegiveis()) {
            painelSaboresEAdicionais.add(painelAdicionais);
        }

        JPanel painelTotal = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelTotal.add(new JLabel("Total:"));
        labelTotal = new JLabel("0 / " + combo.getQuantidadeMaximaDeItems());
        labelTotal.setFont(StyleConfig.FONTE_SUBTITULO);
        labelTotal.setForeground(StyleConfig.COR_PRIMARIA);
        painelTotal.add(labelTotal);

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

        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.add(painelInfo, BorderLayout.NORTH);
        painelCentral.add(new JScrollPane(painelSaboresEAdicionais), BorderLayout.CENTER);
        painelCentral.add(painelTotal, BorderLayout.SOUTH);

        add(painelCentral, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void atualizarTotal() {
        int total = 0;
        for (JSpinner spinner : spinners) {
            total += (int) spinner.getValue();
        }

        int maxItems = combo.getQuantidadeMaximaDeItems();
        labelTotal.setText(total + " / " + maxItems);

        if (total > maxItems) {
            labelTotal.setForeground(Color.RED);
            botaoAdicionar.setEnabled(false);
        } else {
            labelTotal.setForeground(StyleConfig.COR_PRIMARIA);
            botaoAdicionar.setEnabled(true);
        }
    }

    private void atualizarLimiteAdicionais() {
        int selecionados = (int) checkBoxesAdicionais.stream().filter(JCheckBox::isSelected).count();
        int limite = combo.getQuantidadeAdicionaisPermitidos();

        for (JCheckBox checkBox : checkBoxesAdicionais) {
            if (!checkBox.isSelected()) {
                checkBox.setEnabled(selecionados < limite);
            }
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
                itensSelecionados.add(new ItemPedido(produto.getNomeProduto(), quantidade));
                total += quantidade;
                saboresEscolhidos++;
            }
        }

        if (total == 0) {
            JOptionPane.showMessageDialog(this, "Escolha pelo menos um salgado!");
            return;
        }

        if (total > combo.getQuantidadeMaximaDeItems()) {
            JOptionPane.showMessageDialog(this, "Total excede o limite de " + combo.getQuantidadeMaximaDeItems() + " salgados!");
            return;
        }

        if (saboresEscolhidos > combo.getQuantidadeMaximaDeFlavors()) {
            JOptionPane.showMessageDialog(this, "Você pode escolher no máximo " + combo.getQuantidadeMaximaDeFlavors() + " sabores!");
            return;
        }

        itensSelecionados.add(0, new ItemPedido(combo.getNome(), 1, combo.getPrecoTotal(), TipoItemPedido.PACOTE));

        for (int i = 0; i < checkBoxesAdicionais.size(); i++) {
            if (checkBoxesAdicionais.get(i).isSelected()) {
                Adicional adicional = adicionaisElegiveis.get(i);
                itensSelecionados.add(new ItemPedido(adicional.getNome(), 1, TipoItemPedido.ADICIONAL));
            }
        }

        confirmado = true;
        dispose();
    }

    public List<ItemPedido> getItensSelecionados() {
        return confirmado ? itensSelecionados : null;
    }
}