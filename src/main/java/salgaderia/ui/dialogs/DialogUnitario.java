package salgaderia.ui.dialogs;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Produto;
import salgaderia.model.enums.tipoProduto;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DialogUnitario extends JDialog {

    private DadosDAO dao;
    private Produto produto;
    private boolean salvou = false;

    private JTextField campoNome;
    private JTextField campoPreco;
    private JCheckBox checkAtivo;

    public DialogUnitario(JFrame parent, Produto produtoExistente) {
        super(parent, produtoExistente == null ? "Novo Unitário" : "Editar Unitário", true);
        this.dao = DadosDAO.getInstance();
        this.produto = produtoExistente;

        initComponents();
        configurarJanela();

        if (produtoExistente != null) {
            preencherCampos();
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        campoNome = new JTextField(20);
        add(campoNome, gbc);

        // Preço
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        campoPreco = new JTextField(10);
        add(campoPreco, gbc);

        // Ativo
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 3;
        checkAtivo = new JCheckBox("Ativo", true);
        add(checkAtivo, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton botaoSalvar = new JButton("💾 Salvar");
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        add(painelBotoes, gbc);
    }

    private void preencherCampos() {
        campoNome.setText(produto.getNomeProduto());
        campoPreco.setText(String.format("%.2f", produto.getPrecoUnitario()));
        checkAtivo.setSelected(produto.isAtivo());
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
            return;
        }

        try {
            BigDecimal preco = new BigDecimal(campoPreco.getText().replace(",", "."));

            if (produto == null) {
                Produto novo = new Produto(1L, nome, preco, tipoProduto.UNIDADE, checkAtivo.isSelected());
                dao.salvarProduto(novo);
            } else {
                produto.setNomeProduto(nome);
                produto.setPrecoUnitario(preco);
                produto.setAtivo(checkAtivo.isSelected());
                dao.atualizarProduto(produto);
            }

            salvou = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Preço inválido!");
        }
    }

    private void configurarJanela() {
        setSize(400, 220);
        setLocationRelativeTo(getParent());
    }
}
