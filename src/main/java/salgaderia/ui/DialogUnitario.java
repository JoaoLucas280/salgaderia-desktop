package salgaderia.ui;

import salgaderia.model.Produto;
import salgaderia.model.enums.tipoProduto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DialogUnitario extends JDialog {

    private Produto produtoSalvo;
    private boolean salvou = false;  // ← ADICIONA ESSA VARIÁVEL
    private JTextField campoNome;
    private JTextField campoPreco;
    private JCheckBox checkboxAtivo;

    public DialogUnitario(JFrame parent, Produto produtoExistente) {
        super(parent, "🥒 " + (produtoExistente != null ? "Editar" : "Novo") + " Unitário", true);
        this.produtoSalvo = null;

        initComponents(produtoExistente);
        configurarJanela();
    }

    private void initComponents(Produto produtoExistente) {
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        painelCampos.add(new JLabel("Nome do Produto:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        campoNome = new JTextField(20);
        painelCampos.add(campoNome, gbc);

        // Preço Unitário
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Preço Unitário (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        campoPreco = new JTextField(10);
        painelCampos.add(campoPreco, gbc);

        // Ativo
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        checkboxAtivo = new JCheckBox("Ativo");
        checkboxAtivo.setSelected(true);
        painelCampos.add(checkboxAtivo, gbc);

        add(painelCampos, BorderLayout.CENTER);

        if (produtoExistente != null) {
            campoNome.setText(produtoExistente.getNomeProduto());
            campoPreco.setText(String.format("%.2f", produtoExistente.getPrecoUnitario().doubleValue()));
            checkboxAtivo.setSelected(produtoExistente.isAtivo());
        }

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("✅ Salvar");
        botaoSalvar.addActionListener(e -> salvarProduto());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void salvarProduto() {
        String nome = campoNome.getText().trim();
        String precoStr = campoPreco.getText().trim();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Preço são obrigatórios!");
            return;
        }

        try {
            BigDecimal preco = new BigDecimal(precoStr.replace(",", "."));
            boolean ativo = checkboxAtivo.isSelected();

            long id = System.currentTimeMillis();

            produtoSalvo = new Produto(id, nome, preco, tipoProduto.UNIDADE, ativo);
            salvou = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido!");
        }
    }

    private void configurarJanela() {
        setSize(400, 250);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public Produto getProdutoSalvo() {
        return salvou ? produtoSalvo : null;
    }
}