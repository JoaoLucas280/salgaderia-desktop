package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Adicional;
import salgaderia.model.Produto;
import salgaderia.ui.dialogs.DialogAdicional;
import salgaderia.ui.dialogs.DialogUnitario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaAdmin extends JPanel {

    private JFrame parentFrame;
    private DadosDAO dao;

    // Tabela de Unitários
    private JTable tabelaUnitarios;
    private DefaultTableModel modeloTabelaUnitarios;
    private List<Produto> produtos;

    // Tabela de Adicionais
    private JTable tabelaAdicionais;
    private DefaultTableModel modeloTabelaAdicionais;

    public TelaAdmin(JFrame parent) {
        this.parentFrame = parent;
        this.dao = DadosDAO.getInstance();
        setLayout(new BorderLayout(10, 10));

        initComponents();
    }

    private void initComponents() {
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("🥒 Unitários", criarAbaUnitarios());
        abas.addTab("🥤 Adicionais", criarAbaAdicionais());

        add(abas, BorderLayout.CENTER);
    }

    // ==========================================
    // ABA DE UNITÁRIOS
    // ==========================================

    private JPanel criarAbaUnitarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabela
        String[] colunas = {"ID", "Nome", "Preço", "Tipo", "Ativo"};
        modeloTabelaUnitarios = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        produtos = dao.carregarProdutos();
        for (Produto p : produtos) {
            modeloTabelaUnitarios.addRow(new Object[]{
                    p.getId(),
                    p.getNomeProduto(),
                    String.format("R$ %.2f", p.getPrecoUnitario()),
                    p.getTipoProduto().name(),
                    p.isAtivo() ? "Sim" : "Não"
            });
        }

        tabelaUnitarios = new JTable(modeloTabelaUnitarios);
        tabelaUnitarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabelaUnitarios);
        painel.add(scroll, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo");
        botaoNovo.addActionListener(e -> abrirDialogUnitario(null));
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> editarProduto());
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> deletarProduto());
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void abrirDialogUnitario(Produto produtoExistente) {
        DialogUnitario dialog = new DialogUnitario(parentFrame, produtoExistente);
        dialog.setVisible(true);
        atualizarTabelaUnitarios();
    }

    private void editarProduto() {
        int linha = tabelaUnitarios.getSelectedRow();
        if (linha >= 0) {
            Produto produto = produtos.get(linha);
            abrirDialogUnitario(produto);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
        }
    }

    private void deletarProduto() {
        int linha = tabelaUnitarios.getSelectedRow();
        if (linha >= 0) {
            Produto produto = produtos.get(linha);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Deletar \"" + produto.getNomeProduto() + "\"?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dao.deletarProduto(produto.getId());
                atualizarTabelaUnitarios();
                JOptionPane.showMessageDialog(this, "✅ Produto deletado!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
        }
    }

    private void atualizarTabelaUnitarios() {
        modeloTabelaUnitarios.setRowCount(0);
        produtos = dao.carregarProdutos();
        for (Produto p : produtos) {
            modeloTabelaUnitarios.addRow(new Object[]{
                    p.getId(),
                    p.getNomeProduto(),
                    String.format("R$ %.2f", p.getPrecoUnitario()),
                    p.getTipoProduto().name(),
                    p.isAtivo() ? "Sim" : "Não"
            });
        }
    }

    // ==========================================
    // ABA DE ADICIONAIS
    // ==========================================

    private JPanel criarAbaAdicionais() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tabela
        String[] colunas = {"ID", "Nome", "Preço"};
        modeloTabelaAdicionais = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Adicional> adicionais = dao.carregarAdicionais();
        for (Adicional a : adicionais) {
            modeloTabelaAdicionais.addRow(new Object[]{
                    a.getId(),
                    a.getNome(),
                    String.format("R$ %.2f", a.getPreco())
            });
        }

        tabelaAdicionais = new JTable(modeloTabelaAdicionais);
        tabelaAdicionais.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabelaAdicionais);
        painel.add(scroll, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo");
        botaoNovo.addActionListener(e -> {
            DialogAdicional dialog = new DialogAdicional(parentFrame, null);
            dialog.setVisible(true);
            atualizarTabelaAdicionais();
        });
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> {
            int linha = tabelaAdicionais.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modeloTabelaAdicionais.getValueAt(linha, 0);
                Adicional a = dao.carregarAdicionais().stream()
                        .filter(ad -> ad.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (a != null) {
                    DialogAdicional dialog = new DialogAdicional(parentFrame, a);
                    dialog.setVisible(true);
                    atualizarTabelaAdicionais();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um adicional!");
            }
        });
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> {
            int linha = tabelaAdicionais.getSelectedRow();
            if (linha >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Deletar este adicional?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) modeloTabelaAdicionais.getValueAt(linha, 0);
                    dao.deletarAdicional(id);
                    atualizarTabelaAdicionais();
                    JOptionPane.showMessageDialog(this, "✅ Adicional deletado!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um adicional!");
            }
        });
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void atualizarTabelaAdicionais() {
        modeloTabelaAdicionais.setRowCount(0);
        List<Adicional> adicionais = dao.carregarAdicionais();
        for (Adicional a : adicionais) {
            modeloTabelaAdicionais.addRow(new Object[]{
                    a.getId(),
                    a.getNome(),
                    String.format("R$ %.2f", a.getPreco())
            });
        }
    }
}