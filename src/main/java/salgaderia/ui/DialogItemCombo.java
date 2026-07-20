package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.ItemCombo;
import salgaderia.model.Produto;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogItemCombo extends JDialog {

    private ItemCombo itemSalvo;
    private boolean salvou = false;

    private JComboBox<Produto> comboProdutos;
    private JSpinner spinnerQuantidadeMaxima;

    public DialogItemCombo(JDialog parent) {
        super(parent, "Adicionar Item ao Combo", true);
        initComponents();
        configurarJanela();
        carregarProdutos();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Produto
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        comboProdutos = new JComboBox<>();
        comboProdutos.setPreferredSize(new Dimension(200, 30));
        add(comboProdutos, gbc);

        // Quantidade Máxima
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Quantidade Máxima:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        spinnerQuantidadeMaxima = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinnerQuantidadeMaxima.setPreferredSize(new Dimension(80, 30));
        add(spinnerQuantidadeMaxima, gbc);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton botaoSalvar = new JButton("✅ Salvar");
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        add(painelBotoes, gbc);
    }

    private void carregarProdutos() {
        DadosDAO dao = new DadosDAO();
        List<Produto> unitarios = dao.carregarUnitarios();

        if (unitarios.isEmpty()) {
            // Fallback: produtos padrão
            comboProdutos.addItem(new Produto(1L, "Coxinha", java.math.BigDecimal.valueOf(0.60),
                    salgaderia.model.enums.tipoProduto.UNIDADE, true));
            comboProdutos.addItem(new Produto(2L, "Risole", java.math.BigDecimal.valueOf(0.70),
                    salgaderia.model.enums.tipoProduto.UNIDADE, true));
            comboProdutos.addItem(new Produto(3L, "Kibe", java.math.BigDecimal.valueOf(0.80),
                    salgaderia.model.enums.tipoProduto.UNIDADE, true));
            comboProdutos.addItem(new Produto(4L, "Churro", java.math.BigDecimal.valueOf(0.90),
                    salgaderia.model.enums.tipoProduto.UNIDADE, true));
        } else {
            for (Produto p : unitarios) {
                comboProdutos.addItem(p);
            }
        }
    }

    private void salvar() {
        Produto produto = (Produto) comboProdutos.getSelectedItem();
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
            return;
        }

        int quantidadeMaxima = (int) spinnerQuantidadeMaxima.getValue();
        if (quantidadeMaxima <= 0) {
            JOptionPane.showMessageDialog(this, "Quantidade máxima deve ser maior que zero!");
            return;
        }

        itemSalvo = new ItemCombo(produto, quantidadeMaxima);
        salvou = true;
        dispose();
    }

    private void configurarJanela() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public ItemCombo getItemCombo() {
        return salvou ? itemSalvo : null;
    }
}
