package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Produto;
import salgaderia.ui.dialogs.DialogUnitario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaAdmin extends JPanel {

    private JFrame parentFrame;
    private DadosDAO dao;
    private JTable tabela;
    private DefaultTableModel modeloTabela;
    private List<Produto> produtos;

    public TelaAdmin(JFrame parent) {
        this.parentFrame = parent;
        this.dao = DadosDAO.getInstance();
        setLayout(new BorderLayout(10, 10));


        produtos = dao.carregarProdutos();


        criarTabela();


        criarBotoes();


    }

    private void criarTabela() {
        String[] colunas = {"ID", "Nome", "Preço", "Tipo", "Ativo"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (Produto p : produtos) {
            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getNomeProduto(),
                    String.format("R$ %.2f", p.getPrecoUnitario()),
                    p.getTipoProduto().name(),
                    p.isAtivo() ? "Sim" : "Não"
            });
        }

        tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);
        add(scroll, BorderLayout.CENTER);
    }

    private void criarBotoes() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("Novo");
        botaoNovo.addActionListener(e -> abrirDialogUnitario(null));
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("Editar");
        botaoEditar.addActionListener(e -> editarProduto());
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("Deletar");
        botaoDeletar.addActionListener(e -> deletarProduto());
        painelBotoes.add(botaoDeletar);

        add(painelBotoes, BorderLayout.NORTH);
    }

    private void abrirDialogUnitario(Produto produto) {
        DialogUnitario dialog = new DialogUnitario(parentFrame, produto);
        dialog.setVisible(true);
        atualizarTabela();
    }

    private void editarProduto() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            Produto produto = produtos.get(linha);
            abrirDialogUnitario(produto);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
        }
    }

    private void deletarProduto() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deletar este produto?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Produto produto = produtos.get(linha);
                dao.deletarProduto(produto.getId());
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
        }
    }

    private void atualizarTabela() {
        produtos = dao.carregarProdutos();
        modeloTabela.setRowCount(0);
        for (Produto p : produtos) {
            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getNomeProduto(),
                    String.format("R$ %.2f", p.getPrecoUnitario()),
                    p.getTipoProduto().name(),
                    p.isAtivo() ? "Sim" : "Não"
            });
        }
    }
}