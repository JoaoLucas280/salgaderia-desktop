package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Adicional;
import salgaderia.model.Cento;
import salgaderia.model.Combo;
import salgaderia.model.Produto;
import salgaderia.ui.dialogs.DialogAdicional;
import salgaderia.ui.dialogs.DialogCento;
import salgaderia.ui.dialogs.DialogCombo;
import salgaderia.ui.dialogs.DialogUnitario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TelaAdmin extends JPanel {

    private JFrame parentFrame;
    private DadosDAO dao;


    private JTable tabelaUnitarios;
    private DefaultTableModel modeloTabelaUnitarios;
    private List<Produto> produtos;


    private JTable tabelaAdicionais;
    private DefaultTableModel modeloTabelaAdicionais;

    private JTable tabelaCombos;
    private DefaultTableModel modeloTabelaCombos;
    private List<Combo> combos;

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
        abas.addTab("🧩 Combos", criarAbaCombos());
        abas.addTab("📦 Centos", criarAbaCentos());

        add(abas, BorderLayout.CENTER);
    }

    // ABA DE UNITÁRIOS

    private JPanel criarAbaUnitarios() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

    // ABA DE ADICIONAIS

    private JPanel criarAbaAdicionais() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

    // ABA DE COMBOS


    private JPanel criarAbaCombos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Preço", "Max Itens", "Max Sabores"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Combo> combos = dao.carregarCombos();
        for (Combo c : combos) {
            modeloTabela.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    String.format("R$ %.2f", c.getPrecoTotal()),
                    c.getQuantidadeMaximaDeItems(),
                    c.getQuantidadeMaximaDeFlavors()
            });
        }

        JTable tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo Combo");
        botaoNovo.addActionListener(e -> {
            DialogCombo dialog = new DialogCombo(parentFrame, null);
            dialog.setVisible(true);
            atualizarTabelaCombos(modeloTabela);
        });
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modeloTabela.getValueAt(linha, 0);
                Combo c = dao.carregarCombos().stream()
                        .filter(combo -> combo.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (c != null) {
                    DialogCombo dialog = new DialogCombo(parentFrame, c);
                    dialog.setVisible(true);
                    atualizarTabelaCombos(modeloTabela);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um combo!");
            }
        });
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Deletar este combo?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) modeloTabela.getValueAt(linha, 0);
                    dao.deletarCombo(id);
                    atualizarTabelaCombos(modeloTabela);
                    JOptionPane.showMessageDialog(this, "✅ Combo deletado!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um combo!");
            }
        });
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void atualizarTabelaCombos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Combo> combos = dao.carregarCombos();
        for (Combo c : combos) {
            modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    String.format("R$ %.2f", c.getPrecoTotal()),
                    c.getQuantidadeMaximaDeItems(),
                    c.getQuantidadeMaximaDeFlavors()
            });
        }
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


    private JPanel criarAbaCentos() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Preço", "Máx Sabores"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        List<Cento> centos = dao.carregarCentos();
        for (Cento c : centos) {
            modeloTabela.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    String.format("R$ %.2f", c.getPrecoTotal()),
                    c.getMaxSabores()
            });
        }

        JTable tabela = new JTable(modeloTabela);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabela);
        painel.add(scroll, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo Cento");
        botaoNovo.addActionListener(e -> {
            DialogCento dialog = new DialogCento(parentFrame, null);
            dialog.setVisible(true);
            atualizarTabelaCentos(modeloTabela);
        });
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modeloTabela.getValueAt(linha, 0);
                Cento c = dao.carregarCentos().stream()
                        .filter(cento -> cento.getId() == id)
                        .findFirst()
                        .orElse(null);
                if (c != null) {
                    DialogCento dialog = new DialogCento(parentFrame, c);
                    dialog.setVisible(true);
                    atualizarTabelaCentos(modeloTabela);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cento!");
            }
        });
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Deletar este cento?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int id = (int) modeloTabela.getValueAt(linha, 0);
                    dao.deletarCento(id);
                    atualizarTabelaCentos(modeloTabela);
                    JOptionPane.showMessageDialog(this, "✅ Cento deletado!");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cento!");
            }
        });
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void atualizarTabelaCentos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        List<Cento> centos = dao.carregarCentos();
        for (Cento c : centos) {
            modelo.addRow(new Object[]{
                    c.getId(),
                    c.getNome(),
                    String.format("R$ %.2f", c.getPrecoTotal()),
                    c.getMaxSabores()
            });
        }
    }
}