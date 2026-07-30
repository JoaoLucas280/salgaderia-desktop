package salgaderia.ui.dialogs;

import salgaderia.dao.DadosDAO;
import salgaderia.dao.Database;
import salgaderia.model.Produto;
import salgaderia.model.enums.tipoProduto;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        campoNome = new JTextField(20);
        campoNome.setPreferredSize(new Dimension(250, 30));  // ← ADICIONA
        add(campoNome, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        add(new JLabel("Preço (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        campoPreco = new JTextField(10);
        campoPreco.setPreferredSize(new Dimension(120, 30));  // ← ADICIONA
        add(campoPreco, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        checkAtivo = new JCheckBox("Ativo", true);
        add(checkAtivo, gbc);


        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("Salvar");
        StyleConfig.estilizarBotao(botaoSalvar, StyleConfig.COR_SUCESSO);
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("Cancelar");
        StyleConfig.estilizarBotao(botaoCancelar, StyleConfig.COR_ERRO);
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        add(painelBotoes, gbc);
    }

    private void preencherCampos() {
        campoNome.setText(produto.getNomeProduto());

        double preco = produto.getPrecoUnitario().doubleValue();
        campoPreco.setText(String.format("%.2f", preco).replace(".", ","));

        checkAtivo.setSelected(produto.isAtivo());
    }

    public void atualizarProduto(Produto p) {
        String sql = "UPDATE produtos SET nome = ?, preco_unitario = ?, tipo_produto = ?, ativo = ? WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, p.getNomeProduto());

            int centavos = p.getPrecoUnitario().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);

            pstmt.setString(3, p.getTipoProduto().name());
            pstmt.setInt(4, p.isAtivo() ? 1 : 0);
            pstmt.setLong(5, p.getId());

            int rows = pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Produto atualizado: " + p.getNomeProduto() + " - " + centavos + " centavos");

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void salvar() {
        String nome = campoNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
            return;
        }

        String precoStr = campoPreco.getText().trim().replace(",", ".");
        if (precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preço é obrigatório!");
            return;
        }

        try {
            BigDecimal preco = new BigDecimal(precoStr);

            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Preço deve ser maior que zero!");
                return;
            }


            if (produto == null) {
                Produto novo = new Produto(0L, nome, preco, tipoProduto.UNIDADE, checkAtivo.isSelected());
                dao.salvarProduto(novo);
                System.out.println("✅ NOVO produto salvo: " + nome);
            } else {

                System.out.println("📝 Atualizando produto ID " + produto.getId() + ": " + nome);

                produto.setNomeProduto(nome);
                produto.setPrecoUnitario(preco);
                produto.setAtivo(checkAtivo.isSelected());
                dao.atualizarProduto(produto);

                System.out.println("✅ Produto atualizado: " + nome);
            }

            salvou = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Use números (ex: 0,60)");
            System.err.println("Erro ao converter preço: " + precoStr);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarJanela() {
        setSize(400, 220);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        SwingUtilities.updateComponentTreeUI(this);
    }
}
