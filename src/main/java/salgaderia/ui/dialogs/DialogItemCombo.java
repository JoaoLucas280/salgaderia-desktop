package salgaderia.ui.dialogs;

import salgaderia.dao.DadosDAO;
import salgaderia.model.ItemCombo;
import salgaderia.model.Produto;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DialogItemCombo extends JDialog {

    private DadosDAO dao;
    private ItemCombo itemSalvo;
    private boolean salvou = false;

    private JComboBox<Produto> comboProdutos;

    public DialogItemCombo(JDialog parent) {
        super(parent, "Adicionar Sabor ao Combo", true);
        this.dao = DadosDAO.getInstance();

        initComponents();
        configurarJanela();
        carregarProdutos();
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
        add(new JLabel("Sabor:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        comboProdutos = new JComboBox<>();
        comboProdutos.setPreferredSize(new Dimension(200, 30));
        add(comboProdutos, gbc);


        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("✅ Salvar");
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        add(painelBotoes, gbc);
    }

    private void carregarProdutos() {
        List<Produto> produtos = dao.carregarProdutos();


        List<Produto> unitarios = produtos.stream()
                .filter(p -> p.getTipoProduto() == salgaderia.model.enums.tipoProduto.UNIDADE)
                .toList();

        if (unitarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum produto unitário cadastrado!");
            dispose();
            return;
        }

        for (Produto p : unitarios) {
            comboProdutos.addItem(p);
        }
    }

    private void salvar() {
        Produto produto = (Produto) comboProdutos.getSelectedItem();
        
        System.out.println("💾 DialogItemCombo.salvar() chamado");
        System.out.println("   comboProdutos.getSelectedItem() retornou: " + (produto != null ? "✅ " + produto.getNomeProduto() : "❌ null"));
        
        if (produto == null) {
            System.out.println("   ❌ ERRO: Produto é null!");
            JOptionPane.showMessageDialog(this, "Selecione um sabor!");
            return;
        }

        itemSalvo = new ItemCombo(produto);
        System.out.println("   ✅ ItemCombo criado: " + itemSalvo.getProduto().getNomeProduto());
        
        salvou = true;
        dispose();
    }

    private void configurarJanela() {
        setSize(400, 130);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public ItemCombo getItemCombo() {
        return salvou ? itemSalvo : null;
    }
}