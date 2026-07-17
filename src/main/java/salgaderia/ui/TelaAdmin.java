package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.*;
import salgaderia.model.enums.tipoProduto;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TelaAdmin extends JPanel {

    private final DadosDAO dao;
    private List<Combo> combos;
    private List<Cento> centos;
    private List<Produto> unitarios;
    private List<Adicional> adicionais;

    private JTable tabelaCombos;
    private JTable tabelaCentos;
    private JTable tabelaUnitarios;
    private JTable tabelaAdicionais;

    private final JFrame parentFrame;

    public TelaAdmin(JFrame parent) {
        this.parentFrame = parent;
        this.dao = new DadosDAO();
        this.combos = dao.carregarCombos();
        this.centos = dao.carregarCentos();
        this.unitarios = dao.carregarUnitarios();
        this.adicionais = new ArrayList<>();

        if (combos.isEmpty() && centos.isEmpty() && unitarios.isEmpty()) {
            carregarDadosPadroes();
            salvarTodosOsDados();
        }

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));  // ← setLayout agora é do JPanel

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("🧩 Combos", criarAbaCombo());
        abas.addTab("📦 Centos", criarAbaCento());
        abas.addTab("🥒 Unitários", criarAbaUnitario());
        abas.addTab("🎁 Adicionais", criarAbaAdicional());

        add(abas, BorderLayout.CENTER);

        JPanel painelRodape = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        // Remove o botão Fechar porque agora é um painel
        add(painelRodape, BorderLayout.SOUTH);
    }

    private void salvarTodosOsDados() {
        dao.salvarCombos(combos);
        dao.salvarCentos(centos);
    }

    private JPanel criarAbaCombo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Preço", "Max Items", "Max Flavors"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaCombos = new JTable(modeloTabela);
        tabelaCombos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carregarTabelaCombos(modeloTabela);

        JScrollPane scrollPane = new JScrollPane(tabelaCombos);
        painel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo Combo");
        botaoNovo.addActionListener(e -> abrirDialogCombo(null));
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> {
            int linhaSelecionada = tabelaCombos.getSelectedRow();
            if (linhaSelecionada >= 0) {
                Combo combo = combos.get(linhaSelecionada);
                abrirDialogCombo(combo);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um combo para editar!");
            }
        });
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> {
            int linhaSelecionada = tabelaCombos.getSelectedRow();
            if (linhaSelecionada >= 0) {
                combos.remove(linhaSelecionada);
                carregarTabelaCombos(modeloTabela);
                JOptionPane.showMessageDialog(this, "Combo deletado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um combo para deletar!");
            }
        });
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void carregarTabelaCombos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Combo combo : combos) {
            modelo.addRow(new Object[]{
                    combo.getId(),
                    combo.getNome(),
                    String.format("R$ %.2f", combo.getPrecoTotal().doubleValue()),
                    combo.getQuantidadeMaximaDeItems(),
                    combo.getQuantidadeMaximaDeFlavors()
            });
        }
    }

    private void abrirDialogCombo(Combo comboExistente) {
        DialogCombo dialog = new DialogCombo(parentFrame, comboExistente);
        dialog.setVisible(true);

        Combo comboRetorno = dialog.getComboSalvo();
        if (comboRetorno != null) {
            if (comboExistente != null) {
                int indice = combos.indexOf(comboExistente);
                combos.set(indice, comboRetorno);
            } else {
                combos.add(comboRetorno);
            }
            dao.salvarCombos(combos);
            carregarTabelaCombos((DefaultTableModel) tabelaCombos.getModel());
        }
    }

    private void deletarCombo() {
        int linhaSelecionada = tabelaCombos.getSelectedRow();
        if (linhaSelecionada >= 0) {
            combos.remove(linhaSelecionada);
            dao.salvarCombos(combos);
            carregarTabelaCombos((DefaultTableModel) tabelaCombos.getModel());
            JOptionPane.showMessageDialog(this, "Combo deletado com sucesso!");
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um combo para deletar!");
        }
    }

    private JPanel criarAbaCento() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Preço", "Max Flavors"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaCentos = new JTable(modeloTabela);
        tabelaCentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carregarTabelaCentos(modeloTabela);

        JScrollPane scrollPane = new JScrollPane(tabelaCentos);
        painel.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo Cento");
        botaoNovo.addActionListener(e -> abrirDialogCento(null));
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> {
            int linhaSelecionada = tabelaCentos.getSelectedRow();
            if (linhaSelecionada >= 0) {
                Cento cento = centos.get(linhaSelecionada);
                abrirDialogCento(cento);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cento para editar!");
            }
        });
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> {
            int linhaSelecionada = tabelaCentos.getSelectedRow();
            if (linhaSelecionada >= 0) {
                centos.remove(linhaSelecionada);
                carregarTabelaCentos(modeloTabela);
                JOptionPane.showMessageDialog(this, "Cento deletado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cento para deletar!");
            }
        });
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void carregarTabelaCentos(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Cento cento : centos) {
            modelo.addRow(new Object[]{
                    cento.getId(),
                    cento.getNome(),
                    String.format("R$ %.2f", cento.getPrecoTotal().doubleValue()),
                    cento.getQuantidadeMaximaDeSabores()
            });
        }
    }

    private void abrirDialogCento(Cento centoExistente) {
        DialogCento dialog = new DialogCento(parentFrame, centoExistente);
        dialog.setVisible(true);

        Cento centoRetorno = dialog.getCentoSalvo();
        if (centoRetorno != null) {
            if (centoExistente != null) {
                int indice = centos.indexOf(centoExistente);
                centos.set(indice, centoRetorno);
            } else {
                centos.add(centoRetorno);
            }
            DefaultTableModel modelo = (DefaultTableModel) tabelaCentos.getModel();
            carregarTabelaCentos(modelo);
        }
    }

    private JPanel criarAbaUnitario() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Preço Unitário", "Ativo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaUnitarios = new JTable(modeloTabela);
        tabelaUnitarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carregarTabelaUnitarios(modeloTabela);

        JScrollPane scrollPane = new JScrollPane(tabelaUnitarios);
        painel.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo Unitário");
        botaoNovo.addActionListener(e -> abrirDialogUnitario(null));
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> {
            int linhaSelecionada = tabelaUnitarios.getSelectedRow();
            if (linhaSelecionada >= 0) {
                Produto produto = unitarios.get(linhaSelecionada);
                abrirDialogUnitario(produto);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um unitário para editar!");
            }
        });
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> {
            int linhaSelecionada = tabelaUnitarios.getSelectedRow();
            if (linhaSelecionada >= 0) {
                unitarios.remove(linhaSelecionada);
                carregarTabelaUnitarios(modeloTabela);
                JOptionPane.showMessageDialog(this, "Unitário deletado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um unitário para deletar!");
            }
        });
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void carregarTabelaUnitarios(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Produto produto : unitarios) {
            modelo.addRow(new Object[]{
                    produto.getId(),
                    produto.getNomeProduto(),
                    String.format("R$ %.2f", produto.getPrecoUnitario().doubleValue()),
                    produto.isAtivo() ? "Sim" : "Não"
            });
        }
    }

    private void abrirDialogUnitario(Produto produtoExistente) {
        DialogUnitario dialog = new DialogUnitario(parentFrame, produtoExistente);
        dialog.setVisible(true);

        Produto produtoRetorno = dialog.getProdutoSalvo();
        if (produtoRetorno != null) {
            if (produtoExistente != null) {
                int indice = unitarios.indexOf(produtoExistente);
                unitarios.set(indice, produtoRetorno);
            } else {
                unitarios.add(produtoRetorno);
            }
            DefaultTableModel modelo = (DefaultTableModel) tabelaUnitarios.getModel();
            carregarTabelaUnitarios(modelo);
        }
    }

    private JPanel criarAbaAdicional() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        DefaultTableModel modeloTabela = new DefaultTableModel(
                new String[]{"ID", "Nome", "Preço"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaAdicionais = new JTable(modeloTabela);
        tabelaAdicionais.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carregarTabelaAdicionais(modeloTabela);

        JScrollPane scrollPane = new JScrollPane(tabelaAdicionais);
        painel.add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoNovo = new JButton("➕ Novo Adicional");
        botaoNovo.addActionListener(e -> abrirDialogAdicional(null));
        painelBotoes.add(botaoNovo);

        JButton botaoEditar = new JButton("✏️ Editar");
        botaoEditar.addActionListener(e -> {
            int linhaSelecionada = tabelaAdicionais.getSelectedRow();
            if (linhaSelecionada >= 0) {
                Adicional adicional = adicionais.get(linhaSelecionada);
                abrirDialogAdicional(adicional);
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um adicional para editar!");
            }
        });
        painelBotoes.add(botaoEditar);

        JButton botaoDeletar = new JButton("🗑️ Deletar");
        botaoDeletar.addActionListener(e -> {
            int linhaSelecionada = tabelaAdicionais.getSelectedRow();
            if (linhaSelecionada >= 0) {
                adicionais.remove(linhaSelecionada);
                carregarTabelaAdicionais(modeloTabela);
                JOptionPane.showMessageDialog(this, "Adicional deletado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um adicional para deletar!");
            }
        });
        painelBotoes.add(botaoDeletar);

        painel.add(painelBotoes, BorderLayout.NORTH);

        return painel;
    }

    private void carregarTabelaAdicionais(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Adicional adicional : adicionais) {
            modelo.addRow(new Object[]{
                    adicional.getId(),
                    adicional.getNome(),
                    String.format("R$ %.2f", adicional.getPreco().doubleValue())
            });
        }
    }

    private void abrirDialogAdicional(Adicional adicionalExistente) {
        DialogAdicional dialog = new DialogAdicional(parentFrame, adicionalExistente);
        dialog.setVisible(true);

        Adicional adicionalRetorno = dialog.getAdicionalSalvo();
        if (adicionalRetorno != null) {
            if (adicionalExistente != null) {
                int indice = adicionais.indexOf(adicionalExistente);
                adicionais.set(indice, adicionalRetorno);
            } else {
                adicionais.add(adicionalRetorno);
            }
            DefaultTableModel modelo = (DefaultTableModel) tabelaAdicionais.getModel();
            carregarTabelaAdicionais(modelo);
        }
    }

    private void carregarDadosPadroes() {

        unitarios.add(new Produto(1L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE, true));
        unitarios.add(new Produto(2L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE, true));
        unitarios.add(new Produto(3L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE, true));
        unitarios.add(new Produto(4L, "Churro", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE, true));


        adicionais.add(new Adicional(1, "Refrigerante", BigDecimal.valueOf(5.00)));
        adicionais.add(new Adicional(2, "Suco", BigDecimal.valueOf(4.00)));
    }
}
