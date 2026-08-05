package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.*;
import salgaderia.service.PedidoService;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TelaPedido extends JPanel {

    private final JFrame parentFrame;
    private final DadosDAO dao;
    private final PedidoService pedidoService;

    // ===== DADOS DO CLIENTE =====
    private JTextField campoNome;
    private JTextField campoTelefone;
    private JTextField campoEndereco;
    private JTextField campoTaxa;

    // ===== PRODUTOS (ABAS) =====
    private JComboBox<Combo> comboCombos;
    private JButton botaoMontarCombo;

    private JComboBox<Cento> comboCentos;
    private JButton botaoMontarCento;

    private JComboBox<Produto> comboUnitarios;
    private JSpinner spinnerUnitarioQtd;
    private JButton botaoAdicionarUnitario;
    private JButton botaoMontarVariosUnitarios;

    private JComboBox<Adicional> comboAdicionais;
    private JSpinner spinnerAdicionalQtd;
    private JButton botaoAdicionarAdicional;

    // ===== LISTA DE ITENS =====
    private DefaultListModel<String> modelListaItens;
    private JList<String> listaItens;
    private JButton botaoRemoverItem;
    private JButton botaoEditarItem;

    // ===== TOTAIS =====
    private JLabel labelSubtotal;
    private JLabel labelTotal;

    // ===== BOTÕES PRINCIPAIS =====
    private JButton botaoSalvar;
    private JButton botaoLimpar;

    // ===== DADOS TEMPORÁRIOS =====
    private List<ItemPedido> itensPedido;
    private int proximoIdPedido = 1;

    public TelaPedido(JFrame parent) {
        this.parentFrame = parent;
        this.dao = DadosDAO.getInstance();
        this.pedidoService = new PedidoService();
        this.itensPedido = new ArrayList<>();

        initComponents();
        carregarDados();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel Cliente (Norte)
        JPanel painelCliente = criarPainelCliente();
        add(painelCliente, BorderLayout.NORTH);

        // Painel Produtos (Centro)
        JPanel painelProdutos = criarPainelProdutos();
        add(painelProdutos, BorderLayout.CENTER);

        // Painel Rodapé (Sul)
        JPanel painelRodape = criarPainelRodape();
        add(painelRodape, BorderLayout.SOUTH);
    }

    // ==========================================
    // PAINEL CLIENTE
    // ==========================================

    private JPanel criarPainelCliente() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(StyleConfig.criarBorda("📋 Dados do Cliente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 3;
        campoNome = new JTextField(20);
        campoNome.setPreferredSize(new Dimension(250, 30));
        painel.add(campoNome, gbc);

        // Telefone
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Telefone:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1;
        campoTelefone = new JTextField(15);
        campoTelefone.setPreferredSize(new Dimension(150, 30));
        painel.add(campoTelefone, gbc);

        // Endereço
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Endereço:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 2;
        campoEndereco = new JTextField(20);
        campoEndereco.setPreferredSize(new Dimension(250, 30));
        painel.add(campoEndereco, gbc);

        // Taxa
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Taxa Entrega (R$):"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1;
        campoTaxa = new JTextField(10);
        campoTaxa.setPreferredSize(new Dimension(100, 30));
        campoTaxa.setEnabled(false);
        painel.add(campoTaxa, gbc);

        campoEndereco.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { habilitarTaxa(); }
            @Override
            public void removeUpdate(DocumentEvent e) { habilitarTaxa(); }
            @Override
            public void changedUpdate(DocumentEvent e) { habilitarTaxa(); }
        });

        return painel;
    }

    private void habilitarTaxa() {
        if (!campoEndereco.getText().trim().isEmpty()) {
            campoTaxa.setEnabled(true);
            campoTaxa.setText("10,00");
        } else {
            campoTaxa.setEnabled(false);
            campoTaxa.setText("");
        }
        atualizarTotais();
    }

    // ==========================================
    // PAINEL PRODUTOS
    // ==========================================

    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🍗 Adicionar Produtos",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14)
        ));

        // Abas
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("🧩 Combos", criarAbaCombos());
        abas.addTab("📦 Centos", criarAbaCentos());
        abas.addTab("🥒 Unitários", criarAbaUnitarios());
        abas.addTab("🥤 Adicionais", criarAbaAdicionais());

        painel.add(abas, BorderLayout.NORTH);

        // Lista de itens
        modelListaItens = new DefaultListModel<>();
        listaItens = new JList<>(modelListaItens);
        listaItens.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLista = new JScrollPane(listaItens);
        scrollLista.setPreferredSize(new Dimension(500, 200));

        listaItens.getInputMap().put(KeyStroke.getKeyStroke("DELETE"), "removerItem");
        listaItens.getActionMap().put("removerItem", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                removerItemSelecionado();
            }
        });

        listaItens.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarItemSelecionado();
                }
            }
        });

        JPanel painelLista = new JPanel(new BorderLayout(5, 5));
        painelLista.add(scrollLista, BorderLayout.CENTER);

        JPanel painelBotaoRemover = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        botaoEditarItem = new JButton("✏️ Editar Item Selecionado");
        botaoEditarItem.addActionListener(e -> editarItemSelecionado());
        painelBotaoRemover.add(botaoEditarItem);

        botaoRemoverItem = new JButton("🗑️ Remover Item Selecionado");
        botaoRemoverItem.addActionListener(e -> removerItemSelecionado());
        painelBotaoRemover.add(botaoRemoverItem);
        painelLista.add(painelBotaoRemover, BorderLayout.SOUTH);

        painel.add(painelLista, BorderLayout.CENTER);

        return painel;
    }

    // ===== ABA COMBOS =====
    private JPanel criarAbaCombos() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        comboCombos = new JComboBox<>();
        comboCombos.setPreferredSize(new Dimension(250, 30));
        painel.add(new JLabel("Selecione um Combo:"));
        painel.add(comboCombos);

        botaoMontarCombo = new JButton("🧩 Montar Combo");
        botaoMontarCombo.addActionListener(e -> abrirMontagemCombo());
        painel.add(botaoMontarCombo);

        return painel;
    }

    // ===== ABA CENTOS =====
    private JPanel criarAbaCentos() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        comboCentos = new JComboBox<>();
        comboCentos.setPreferredSize(new Dimension(250, 30));
        painel.add(new JLabel("Selecione um Cento:"));
        painel.add(comboCentos);

        botaoMontarCento = new JButton("📦 Montar Cento");
        botaoMontarCento.addActionListener(e -> abrirMontagemCento());
        painel.add(botaoMontarCento);

        return painel;
    }

    // ===== ABA UNITÁRIOS =====
    private JPanel criarAbaUnitarios() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        comboUnitarios = new JComboBox<>();
        comboUnitarios.setPreferredSize(new Dimension(200, 30));
        painel.add(new JLabel("Produto:"));
        painel.add(comboUnitarios);

        spinnerUnitarioQtd = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        spinnerUnitarioQtd.setPreferredSize(new Dimension(60, 30));
        painel.add(new JLabel("Qtd:"));
        painel.add(spinnerUnitarioQtd);

        botaoAdicionarUnitario = new JButton("➕ Adicionar");
        botaoAdicionarUnitario.addActionListener(e -> adicionarUnitario());
        painel.add(botaoAdicionarUnitario);


        botaoMontarVariosUnitarios = new JButton("📋 Montar Vários");
        botaoMontarVariosUnitarios.addActionListener(e -> abrirMontagemUnitarios());
        painel.add(botaoMontarVariosUnitarios);

        return painel;
    }

    // ===== ABA ADICIONAIS =====
    private JPanel criarAbaAdicionais() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        comboAdicionais = new JComboBox<>();
        comboAdicionais.setPreferredSize(new Dimension(200, 30));
        painel.add(new JLabel("Adicional:"));
        painel.add(comboAdicionais);

        spinnerAdicionalQtd = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));
        spinnerAdicionalQtd.setPreferredSize(new Dimension(60, 30));
        painel.add(new JLabel("Qtd:"));
        painel.add(spinnerAdicionalQtd);

        botaoAdicionarAdicional = new JButton("➕ Adicionar");
        botaoAdicionarAdicional.addActionListener(e -> adicionarAdicional());
        painel.add(botaoAdicionarAdicional);

        return painel;
    }

    // ==========================================
    // CARREGAR DADOS
    // ==========================================

    private void carregarDados() {
        // Carrega combos
        List<Combo> combos = dao.carregarCombos();
        for (Combo c : combos) {
            comboCombos.addItem(c);
        }

        // Carrega centos
        List<Cento> centos = dao.carregarCentos();
        for (Cento c : centos) {
            comboCentos.addItem(c);
        }

        // Carrega unitários
        List<Produto> unitarios = dao.carregarProdutos();
        for (Produto p : unitarios) {
            if (p.getTipoProduto() == salgaderia.model.enums.tipoProduto.UNIDADE) {
                comboUnitarios.addItem(p);
            }
        }

        // Carrega adicionais
        List<Adicional> adicionais = dao.carregarAdicionais();
        for (Adicional a : adicionais) {
            comboAdicionais.addItem(a);
        }
    }

    public void atualizarListas() {
        comboCombos.removeAllItems();
        comboCentos.removeAllItems();
        comboUnitarios.removeAllItems();
        comboAdicionais.removeAllItems();
        carregarDados();
    }

    private void adicionarUnitario() {
        Produto produto = (Produto) comboUnitarios.getSelectedItem();
        if (produto == null) {
            JOptionPane.showMessageDialog(this, "Selecione um produto!");
            return;
        }

        int quantidade = (int) spinnerUnitarioQtd.getValue();
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero!");
            return;
        }

        BigDecimal subtotal = produto.getPrecoUnitario().multiply(BigDecimal.valueOf(quantidade));

        ItemPedido item = new ItemPedido(produto.getNomeProduto(), quantidade, produto.getPrecoUnitario());
        itensPedido.add(item);
        modelListaItens.addElement(
                String.format("%dx %s - R$ %.2f",
                        quantidade,
                        produto.getNomeProduto(),
                        subtotal.doubleValue()
                )
        );

        spinnerUnitarioQtd.setValue(1);
        atualizarTotais();
    }

    private void abrirMontagemUnitarios() {
        List<Produto> unitarios = dao.carregarProdutos().stream()
                .filter(p -> p.getTipoProduto() == salgaderia.model.enums.tipoProduto.UNIDADE)
                .toList();

        if (unitarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum produto unitário cadastrado!");
            return;
        }

        TelaMontagemUnitario tela = new TelaMontagemUnitario(parentFrame, unitarios);
        tela.setVisible(true);

        List<ItemPedido> itens = tela.getItensSelecionados();
        if (itens != null && !itens.isEmpty()) {
            for (ItemPedido item : itens) {
                itensPedido.add(item);
                modelListaItens.addElement(
                        String.format("%dx %s - R$ %.2f",
                                item.getQuantidade(),
                                item.getNomeProduto(),
                                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())).doubleValue()
                        )
                );
            }
            atualizarTotais();
            JOptionPane.showMessageDialog(this, "✅ " + itens.size() + " item(ns) adicionado(s)!");
        }
    }

    private void adicionarAdicional() {
        Adicional adicional = (Adicional) comboAdicionais.getSelectedItem();
        if (adicional == null) {
            JOptionPane.showMessageDialog(this, "Selecione um adicional!");
            return;
        }

        int quantidade = (int) spinnerAdicionalQtd.getValue();
        if (quantidade <= 0) {
            JOptionPane.showMessageDialog(this, "Quantidade deve ser maior que zero!");
            return;
        }

        BigDecimal subtotal = adicional.getPreco().multiply(BigDecimal.valueOf(quantidade));

        ItemPedido item = new ItemPedido(adicional.getNome(), quantidade, adicional.getPreco());
        itensPedido.add(item);
        modelListaItens.addElement(
                String.format("%dx %s - R$ %.2f",
                        quantidade,
                        adicional.getNome(),
                        subtotal.doubleValue()
                )
        );

        spinnerAdicionalQtd.setValue(1);
        atualizarTotais();
    }

    private void abrirMontagemCombo() {
        Combo comboSelecionado = (Combo) comboCombos.getSelectedItem();
        if (comboSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um combo!");
            return;
        }

        Combo comboAtualizado = dao.carregarCombos().stream()
                .filter(c -> c.getId() == comboSelecionado.getId())
                .findFirst()
                .orElse(comboSelecionado);

        TelaMontagemCombo tela = new TelaMontagemCombo(parentFrame, comboAtualizado);
        tela.setVisible(true);

        List<ItemPedido> itens = tela.getItensSelecionados();
        if (itens != null && !itens.isEmpty()) {
            for (ItemPedido item : itens) {
                itensPedido.add(item);
                modelListaItens.addElement(
                        String.format("%dx %s - R$ %.2f",
                                item.getQuantidade(),
                                item.getNomeProduto(),
                                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())).doubleValue()
                        )
                );
            }
            atualizarTotais();
            JOptionPane.showMessageDialog(this, "✅ Combo adicionado com " + itens.size() + " itens!");
        }
    }

    private void abrirMontagemCento() {
        Cento centoSelecionado = (Cento) comboCentos.getSelectedItem();
        if (centoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cento!");
            return;
        }
        Cento centoAtualizado = dao.carregarCentos().stream()
                .filter(c -> c.getId() == centoSelecionado.getId())
                .findFirst()
                .orElse(centoSelecionado);

        TelaMontagemCento tela = new TelaMontagemCento(parentFrame, centoAtualizado);
        tela.setVisible(true);

        List<ItemPedido> itens = tela.getItensSelecionados();
        if (itens != null && !itens.isEmpty()) {
            for (ItemPedido item : itens) {
                itensPedido.add(item);
                modelListaItens.addElement(
                        String.format("%dx %s - R$ %.2f",
                                item.getQuantidade(),
                                item.getNomeProduto(),
                                item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())).doubleValue()
                        )
                );
            }
            atualizarTotais();
            JOptionPane.showMessageDialog(this, "✅ Cento adicionado com " + itens.size() + " itens!");
        }
    }

    private void editarItemSelecionado() {
        int indice = listaItens.getSelectedIndex();
        if (indice < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item da lista para editar!");
            return;
        }

        ItemPedido item = itensPedido.get(indice);

        String novaQtdStr = JOptionPane.showInputDialog(
                this,
                "Nova quantidade para \"" + item.getNomeProduto() + "\":",
                item.getQuantidade()
        );

        if (novaQtdStr == null) {
            return; // usuário cancelou
        }

        try {
            int novaQtd = Integer.parseInt(novaQtdStr.trim());

            if (novaQtd <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Quantidade deve ser maior que zero!\nPara excluir o item, use o botão \"Remover Item Selecionado\".");
                return;
            }

            ItemPedido itemAtualizado = new ItemPedido(item.getNomeProduto(), novaQtd, item.getPrecoUnitario());
            itensPedido.set(indice, itemAtualizado);

            BigDecimal subtotal = item.getPrecoUnitario().multiply(BigDecimal.valueOf(novaQtd));
            modelListaItens.set(indice, String.format("%dx %s - R$ %.2f",
                    novaQtd,
                    item.getNomeProduto(),
                    subtotal.doubleValue()
            ));

            atualizarTotais();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Digite um número válido!");
        }
    }

    private void removerItemSelecionado() {
        int indice = listaItens.getSelectedIndex();
        if (indice < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um item da lista para remover!");
            return;
        }

        itensPedido.remove(indice);
        modelListaItens.remove(indice);
        atualizarTotais();
    }

    private void atualizarTotais() {
        BigDecimal subtotal = itensPedido.stream()
                .map(item -> item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        labelSubtotal.setText("R$ " + String.format("%.2f", subtotal.doubleValue()));

        BigDecimal total = subtotal;
        String taxaStr = campoTaxa.getText().trim().replace(",", ".");
        if (!taxaStr.isEmpty()) {
            try {
                BigDecimal taxa = new BigDecimal(taxaStr);
                total = total.add(taxa);
            } catch (NumberFormatException ignored) {}
        }

        labelTotal.setText("R$ " + String.format("%.2f", total.doubleValue()));
    }

    private JPanel criarPainelRodape() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEtchedBorder());

        // Totais
        JPanel painelTotais = new JPanel(new GridLayout(2, 2, 10, 5));
        painelTotais.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        painelTotais.add(new JLabel("Subtotal:"));
        labelSubtotal = new JLabel("R$ 0,00");
        labelSubtotal.setFont(new Font("Arial", Font.BOLD, 14));
        painelTotais.add(labelSubtotal);

        painelTotais.add(new JLabel("Total (com taxa):"));
        labelTotal = new JLabel("R$ 0,00");
        labelTotal.setFont(StyleConfig.FONTE_TITULO);
        labelTotal.setForeground(StyleConfig.COR_PRIMARIA);
        painelTotais.add(labelTotal);

        painel.add(painelTotais, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        botaoSalvar = new JButton("💾 Salvar Pedido");
        StyleConfig.estilizarBotao(botaoSalvar, StyleConfig.COR_SUCESSO);
        botaoSalvar.addActionListener(e -> salvarPedido());
        painelBotoes.add(botaoSalvar);

        botaoLimpar = new JButton("🗑️ Limpar");
        botaoLimpar.addActionListener(e -> limparCampos());
        painelBotoes.add(botaoLimpar);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private void salvarPedido() {

        JOptionPane.showMessageDialog(this, "Salvar pedido em desenvolvimento!");
    }

    private void limparCampos() {
        campoNome.setText("");
        campoTelefone.setText("");
        campoEndereco.setText("");
        campoTaxa.setText("");
        campoTaxa.setEnabled(false);
        itensPedido.clear();
        modelListaItens.clear();
        atualizarTotais();
    }
}