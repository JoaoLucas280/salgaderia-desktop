package salgaderia.ui;

import salgaderia.model.Cento;
import salgaderia.model.ItemCombo;
import salgaderia.model.Produto;
import salgaderia.model.enums.tipoProduto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DialogCento extends JDialog {

    private Cento centoSalvo;
    private JTextField campoNome;
    private JTextField campoPreco;
    private JSpinner spinnerMaxFlavors;
    private List<JCheckBox> checkboxesProdutos;

    public DialogCento(JFrame parent, Cento centoExistente) {
        super(parent, "📦 " + (centoExistente != null ? "Editar" : "Novo") + " Cento", true);
        this.centoSalvo = null;
        this.checkboxesProdutos = new ArrayList<>();

        initComponents(centoExistente);
        configurarJanela();
    }

    private void initComponents(Cento centoExistente) {
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCampos.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        campoNome = new JTextField(30);
        painelCampos.add(campoNome, gbc);

        // Preço
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Preço Fixo (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        campoPreco = new JTextField(10);
        painelCampos.add(campoPreco, gbc);

        // Quantidade
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Quantidade:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        JSpinner spinnerQtd = new JSpinner(new SpinnerNumberModel(100, 100, 100, 1));
        spinnerQtd.setEnabled(false);
        painelCampos.add(spinnerQtd, gbc);

        // Máximo de Flavors
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Máximo de Sabores:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        spinnerMaxFlavors = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        painelCampos.add(spinnerMaxFlavors, gbc);

        // Produtos disponíveis
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        JPanel painelProdutos = new JPanel(new GridLayout(0, 1, 5, 5));
        painelProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis (Unitários)"));

        List<Produto> produtosPadrao = new ArrayList<>();
        produtosPadrao.add(new Produto(1L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE, true));
        produtosPadrao.add(new Produto(2L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE, true));
        produtosPadrao.add(new Produto(3L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE, true));
        produtosPadrao.add(new Produto(4L, "Churro", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE, true));

        for (Produto produto : produtosPadrao) {
            JCheckBox checkbox = new JCheckBox(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + "/un)");
            checkboxesProdutos.add(checkbox);
            painelProdutos.add(checkbox);
        }

        JScrollPane scrollProdutos = new JScrollPane(painelProdutos);
        painelCampos.add(scrollProdutos, gbc);

        add(painelCampos, BorderLayout.CENTER);

        if (centoExistente != null) {
            campoNome.setText(centoExistente.getNome());
            campoPreco.setText(String.format("%.2f", centoExistente.getPrecoTotal().doubleValue()));
            spinnerMaxFlavors.setValue(centoExistente.getQuantidadeMaximaDeSabores());
        }

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("✅ Salvar");
        botaoSalvar.addActionListener(e -> salvarCento());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void salvarCento() {
        String nome = campoNome.getText().trim();
        String precoStr = campoPreco.getText().trim();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Preço são obrigatórios!");
            return;
        }

        try {
            BigDecimal preco = new BigDecimal(precoStr.replace(",", "."));
            int maxFlavors = (int) spinnerMaxFlavors.getValue();

            List<ItemCombo> itens = new ArrayList<>();
            int contadorSelecionados = 0;

            for (JCheckBox checkbox : checkboxesProdutos) {
                if (checkbox.isSelected()) {
                    contadorSelecionados++;
                }
            }

            if (contadorSelecionados > maxFlavors) {
                JOptionPane.showMessageDialog(this, "Número de sabores excede o máximo!");
                return;
            }

            centoSalvo = new Cento(1, nome, itens, preco, maxFlavors);

            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido!");
        }
    }

    private void configurarJanela() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public Cento getCentoSalvo() {
        return centoSalvo;
    }
}
