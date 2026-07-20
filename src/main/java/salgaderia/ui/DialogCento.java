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
    private boolean salvou = false;
    private JTextField campoNome;
    private JTextField campoPreco;
    private JSpinner spinnerMaxFlavors;
    private List<JCheckBox> checkboxesProdutos;
    private List<Produto> produtosDisponiveis;

    public DialogCento(JFrame parent, Cento centoExistente) {
        super(parent, "📦 " + (centoExistente != null ? "Editar" : "Novo") + " Cento", true);
        this.centoSalvo = null;
        this.checkboxesProdutos = new ArrayList<>();
        this.produtosDisponiveis = new ArrayList<>();

        initComponents(centoExistente);
        configurarJanela();

        if (centoExistente != null) {
            preencherCampos(centoExistente);
        }
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

        // Quantidade (fixa em 100)
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

        // Máximo de Sabores
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

        // Carrega produtos disponíveis
        produtosDisponiveis.add(new Produto(1L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE, true));

        for (Produto produto : produtosDisponiveis) {
            JCheckBox checkbox = new JCheckBox(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + "/un)");
            checkboxesProdutos.add(checkbox);
            painelProdutos.add(checkbox);
        }

        JScrollPane scrollProdutos = new JScrollPane(painelProdutos);
        scrollProdutos.setPreferredSize(new Dimension(400, 150));
        painelCampos.add(scrollProdutos, gbc);

        add(painelCampos, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("✅ Salvar");
        botaoSalvar.addActionListener(e -> salvarCento());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void preencherCampos(Cento cento) {
        campoNome.setText(cento.getNome());
        campoPreco.setText(String.format("%.2f", cento.getPrecoTotal().doubleValue()));
        spinnerMaxFlavors.setValue(cento.getQuantidadeMaximaDeSabores());

        // Marca os checkboxes dos produtos que estão no cento
        for (JCheckBox checkbox : checkboxesProdutos) {
            for (ItemCombo item : cento.getItens()) {
                String nomeProduto = item.getProduto().getNomeProduto();
                if (checkbox.getText().startsWith(nomeProduto)) {
                    checkbox.setSelected(true);
                    break;
                }
            }
        }
    }

    private void salvarCento() {
        try {
            String nome = campoNome.getText().trim();
            if (nome.isBlank()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
                return;
            }

            BigDecimal preco = new BigDecimal(campoPreco.getText().replace(",", "."));
            int maxSabores = (int) spinnerMaxFlavors.getValue();  // ← PEGA DO SPINNER


            List<ItemCombo> itensSelecionados = new ArrayList<>();
            for (int i = 0; i < checkboxesProdutos.size(); i++) {
                JCheckBox checkbox = checkboxesProdutos.get(i);
                if (checkbox.isSelected()) {
                    Produto produto = produtosDisponiveis.get(i);
                    itensSelecionados.add(new ItemCombo(produto, 100)); // Máximo 100
                }
            }

            if (itensSelecionados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecione pelo menos um produto!");
                return;
            }

            int id = centoSalvo != null ? centoSalvo.getId() : (int) System.currentTimeMillis();


            centoSalvo = new Cento(id, nome, itensSelecionados, preco, maxSabores);
            salvou = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preencha os campos numéricos corretamente!");
        }
    }

    private void configurarJanela() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public Cento getCentoSalvo() {
        return salvou ? centoSalvo : null;
    }
}