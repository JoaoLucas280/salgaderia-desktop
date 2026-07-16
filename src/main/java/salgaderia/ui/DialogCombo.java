package salgaderia.ui;

import salgaderia.model.Combo;
import salgaderia.model.ItemCombo;
import salgaderia.model.Produto;
import salgaderia.model.enums.tipoProduto;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DialogCombo extends JDialog {

    private Combo comboSalvo;
    private JTextField campoNome;
    private JTextField campoPreco;
    private JSpinner spinnerMaxItems;
    private JSpinner spinnerMaxFlavors;
    private List<JCheckBox> checkboxesProdutos;
    private List<JSpinner> spinnersProdutos;

    public DialogCombo(JFrame parent, Combo comboExistente) {
        super(parent, "🧩 " + (comboExistente != null ? "Editar" : "Novo") + " Combo", true);
        this.comboSalvo = null;
        this.checkboxesProdutos = new ArrayList<>();
        this.spinnersProdutos = new ArrayList<>();

        initComponents(comboExistente);
        configurarJanela();
    }

    private void initComponents(Combo comboExistente) {
        setLayout(new BorderLayout(10, 10));
        add(BorderFactory.createEmptyBorder(), BorderLayout.NORTH);

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

        // Máximo de Items
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Máximo de Items:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        spinnerMaxItems = new JSpinner(new SpinnerNumberModel(100, 1, 999, 1));
        painelCampos.add(spinnerMaxItems, gbc);

        // Máximo de Flavors
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Máximo de Sabores:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        spinnerMaxFlavors = new JSpinner(new SpinnerNumberModel(4, 1, 20, 1));
        painelCampos.add(spinnerMaxFlavors, gbc);

        // Produtos disponíveis
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        JPanel painelProdutos = new JPanel(new GridLayout(0, 3, 5, 5));
        painelProdutos.setBorder(BorderFactory.createTitledBorder("Produtos Disponíveis"));

        // Produtos padrão do sistema
        List<Produto> produtosPadrao = new ArrayList<>();
        produtosPadrao.add(new Produto(1L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE, true));
        produtosPadrao.add(new Produto(2L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE, true));
        produtosPadrao.add(new Produto(3L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE, true));
        produtosPadrao.add(new Produto(4L, "Churro", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE, true));

        for (Produto produto : produtosPadrao) {
            JCheckBox checkbox = new JCheckBox(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + ")");
            checkboxesProdutos.add(checkbox);

            JSpinner spinner = new JSpinner(new SpinnerNumberModel(30, 0, 100, 1));
            spinnersProdutos.add(spinner);

            painelProdutos.add(checkbox);
            painelProdutos.add(spinner);
            painelProdutos.add(new JLabel("máx"));
        }

        JScrollPane scrollProdutos = new JScrollPane(painelProdutos);
        painelCampos.add(scrollProdutos, gbc);

        add(painelCampos, BorderLayout.CENTER);

        // Carregar dados se for edição
        if (comboExistente != null) {
            campoNome.setText(comboExistente.getNome());
            campoPreco.setText(String.format("%.2f", comboExistente.getPrecoTotal().doubleValue()));
            spinnerMaxItems.setValue(comboExistente.getQuantidadeMaximaDeItems());
            spinnerMaxFlavors.setValue(comboExistente.getQuantidadeMaximaDeFlavors());
        }

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("✅ Salvar");
        botaoSalvar.addActionListener(e -> salvarCombo());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void add(Border emptyBorder, String north) {
    }

    private void salvarCombo() {
        String nome = campoNome.getText().trim();
        String precoStr = campoPreco.getText().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
            return;
        }

        if (precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preço é obrigatório!");
            return;
        }

        try {
            BigDecimal preco = new BigDecimal(precoStr.replace(",", "."));
            int maxItems = (int) spinnerMaxItems.getValue();
            int maxFlavors = (int) spinnerMaxFlavors.getValue();

            List<ItemCombo> itens = new ArrayList<>();
            int contadorSelecionados = 0;

            for (int i = 0; i < checkboxesProdutos.size(); i++) {
                if (checkboxesProdutos.get(i).isSelected()) {
                    contadorSelecionados++;
                    int qtdMax = (int) spinnersProdutos.get(i).getValue();
                    Produto produto = new Produto((long) (i + 1), checkboxesProdutos.get(i).getText().split(" \\(")[0],
                            BigDecimal.valueOf(0.60 + i * 0.1), tipoProduto.UNIDADE, true);
                    itens.add(new ItemCombo(produto, qtdMax));
                }
            }

            if (contadorSelecionados > maxFlavors) {
                JOptionPane.showMessageDialog(this, "Número de sabores excede o máximo permitido!");
                return;
            }

            comboSalvo = new Combo(1, nome, itens, preco, maxItems, maxFlavors);
            comboSalvo.setPrecoTotal(preco);

            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido!");
        }
    }

    private void configurarJanela() {
        setSize(600, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public Combo getComboSalvo() {
        return comboSalvo;
    }
}
