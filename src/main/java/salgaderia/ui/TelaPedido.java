package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.*;
import salgaderia.model.enums.tipoProduto;
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

    private final PedidoService service;
    private final JFrame parentFrame;

    // Dados do cliente
    private JTextField campoNome;
    private JTextField campoTelefone;
    private JTextField campoEndereco;
    private JTextField campoTaxa;

    // Unitários
    private JComboBox<Produto> comboUnitarios;
    private JSpinner spinnerUnitarioQtd;
    private JButton botaoAdicionarUnitario;

    // Combos
    private JComboBox<Combo> comboCombos;
    private JButton botaoMontarCombo;

    // Centos
    private JComboBox<Cento> comboCentos;
    private JButton botaoMontarCento;

    // ★ ADICIONAIS ★
    private JComboBox<Adicional> comboAdicionais;
    private JSpinner spinnerAdicionalQtd;
    private JButton botaoAdicionarAdicional;

    // Lista de itens
    private DefaultListModel<String> modelListaItens;
    private JList<String> listaItens;

    // Totais
    private JLabel labelSubtotal;
    private JLabel labelTotal;

    // Botões principais
    private JButton botaoSalvar;
    private JButton botaoLimpar;

    // Dados temporários
    private List<Item> itensPedido;
    private int proximoIdPedido = 1;

    public TelaPedido(JFrame parent) {
        this.parentFrame = parent;
        this.service = new PedidoService();
        this.itensPedido = new ArrayList<>();
        initComponents();
        carregarProdutos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel painelCliente = criarPainelCliente();
        add(painelCliente, BorderLayout.NORTH);

        JPanel painelProdutos = criarPainelProdutos();
        add(painelProdutos, BorderLayout.CENTER);

        JPanel painelRodape = criarPainelRodape();
        add(painelRodape, BorderLayout.SOUTH);
    }

    private JPanel criarPainelCliente() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBorder(StyleConfig.criarBorda("📋 Dados do Cliente"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        campoNome = new JTextField(20);
        painel.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        painel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        campoTelefone = new JTextField(15);
        painel.add(campoTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painel.add(new JLabel("Endereço:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2;
        campoEndereco = new JTextField(20);
        painel.add(campoEndereco, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        painel.add(new JLabel("Taxa Entrega (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        campoTaxa = new JTextField(10);
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

    private JPanel criarPainelProdutos() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🍗 Adicionar Produtos",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14)
        ));

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("🥒 Unitários", criarAbaUnitarios());
        abas.addTab("🧩 Combos", criarAbaCombos());
        abas.addTab("📦 Centos", criarAbaCentos());
        abas.addTab("🥤 Adicionais", criarAbaAdicionais());  // ★ NOVA ABA ★

        painel.add(abas, BorderLayout.NORTH);

        modelListaItens = new DefaultListModel<>();
        listaItens = new JList<>(modelListaItens);
        listaItens.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollLista = new JScrollPane(listaItens);
        scrollLista.setPreferredSize(new Dimension(500, 150));
        painel.add(scrollLista, BorderLayout.CENTER);

        return painel;
    }

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
        botaoAdicionarUnitario.addActionListener(e -> adicionarProdutoUnitario());
        painel.add(botaoAdicionarUnitario);

        return painel;
    }

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

    // ★ ABA DE ADICIONAIS ★
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

    private JPanel criarPainelRodape() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.setBorder(BorderFactory.createEtchedBorder());

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

    // ==========================================
    // ★ MÉTODO PARA ADICIONAR ADICIONAL ★
    // ==========================================
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

        BigDecimal precoUnitario = adicional.getPreco();
        BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));

        Item item = new Item(adicional.getNome(), quantidade, precoUnitario);
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

    // ==========================================
    // MÉTODOS DE CARREGAMENTO DE PRODUTOS
    // ==========================================

    private void carregarProdutosPadrao() {
        comboUnitarios.addItem(new Produto(1L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE, true));
        comboUnitarios.addItem(new Produto(2L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE, true));
        comboUnitarios.addItem(new Produto(3L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE, true));
        comboUnitarios.addItem(new Produto(4L, "Churro", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE, true));
    }

    private void criarCombosPadrao() {
        List<ItemCombo> itensCombo1 = new ArrayList<>();
        itensCombo1.add(new ItemCombo(new Produto(101L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE), 30));
        itensCombo1.add(new ItemCombo(new Produto(102L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE), 30));
        itensCombo1.add(new ItemCombo(new Produto(103L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE), 20));
        itensCombo1.add(new ItemCombo(new Produto(104L, "Churro", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE), 20));
        comboCombos.addItem(new Combo(1, "Combo Família (100 salgados)", itensCombo1, BigDecimal.valueOf(50.00)));

        List<ItemCombo> itensCombo2 = new ArrayList<>();
        itensCombo2.add(new ItemCombo(new Produto(105L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE), 20));
        itensCombo2.add(new ItemCombo(new Produto(106L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE), 15));
        itensCombo2.add(new ItemCombo(new Produto(107L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE), 15));
        comboCombos.addItem(new Combo(2, "Combo Amigo (50 salgados)", itensCombo2, BigDecimal.valueOf(35.00)));
    }

    private void criarCentosPadrao() {
        List<ItemCombo> itensCento = new ArrayList<>();
        itensCento.add(new ItemCombo(new Produto(201L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE), 100));
        itensCento.add(new ItemCombo(new Produto(202L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE), 100));
        itensCento.add(new ItemCombo(new Produto(203L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE), 100));
        itensCento.add(new ItemCombo(new Produto(204L, "Churro", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE), 100));
        comboCentos.addItem(new Cento(101, "Cento de Salgados (100 total)", itensCento, BigDecimal.valueOf(60.00)));
    }

    private void carregarProdutos() {
        DadosDAO dao = new DadosDAO();

        // Unitários
        List<Produto> unitarios = dao.carregarUnitarios();
        if (unitarios.isEmpty()) {
            carregarProdutosPadrao();
        } else {
            for (Produto p : unitarios) {
                comboUnitarios.addItem(p);
            }
        }

        // Combos
        List<Combo> combos = dao.carregarCombos();
        if (!combos.isEmpty()) {
            for (Combo c : combos) {
                comboCombos.addItem(c);
            }
        } else {
            criarCombosPadrao();
        }

        // Centos
        List<Cento> centos = dao.carregarCentos();
        if (!centos.isEmpty()) {
            for (Cento c : centos) {
                comboCentos.addItem(c);
            }
        } else {
            criarCentosPadrao();
        }

        List<Adicional> adicionais = dao.carregarAdicionais();
        if (!adicionais.isEmpty()) {
            for (Adicional a : adicionais) {
                comboAdicionais.addItem(a);
            }
        } else {
            // Fallback: adicionais padrão
            comboAdicionais.addItem(new Adicional(1, "Refrigerante", BigDecimal.valueOf(5.00)));
            comboAdicionais.addItem(new Adicional(2, "Suco", BigDecimal.valueOf(4.00)));
            comboAdicionais.addItem(new Adicional(3, "Água de Coco", BigDecimal.valueOf(3.00)));
        }
    }

    // ==========================================
    // DEMAIS MÉTODOS (ADICIONAR UNITÁRIO, SALVAR, ETC)
    // ==========================================

    private void adicionarProdutoUnitario() {
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

        BigDecimal precoUnitario = produto.getPrecoUnitario();
        BigDecimal subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));

        Item item = new Item(produto.getNomeProduto(), quantidade, precoUnitario);
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

    private void salvarPedido() {
        try {
            String nome = campoNome.getText().trim();
            String telefone = campoTelefone.getText().trim();

            if (nome.isBlank() || telefone.isBlank()) {
                JOptionPane.showMessageDialog(this,
                        "Nome e Telefone são obrigatórios!",
                        "Campos obrigatórios",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (itensPedido.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Adicione pelo menos um produto ao pedido!",
                        "Pedido vazio",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Pedido pedido = new Pedido();
            pedido.setId(proximoIdPedido++);
            pedido.setNomeCliente(nome);
            pedido.setTelefone(telefone);

            String endereco = campoEndereco.getText().trim();
            if (!endereco.isBlank()) {
                pedido.setEndereco(endereco);
            }

            String taxaStr = campoTaxa.getText().trim();
            if (!taxaStr.isBlank()) {
                try {
                    BigDecimal taxa = new BigDecimal(taxaStr.replace(",", "."));
                    pedido.setTaxaEntrega(taxa);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Taxa inválida!");
                    return;
                }
            }

            List<ItemPedido> itensConvertidos = new ArrayList<>();
            for (Item item : itensPedido) {
                itensConvertidos.add(new ItemPedido(item.getNomeProduto(), item.getQuantidade(), item.getPrecoUnitario()));
            }
            pedido.setItens(itensConvertidos);

            service.salvarPedido(pedido);

            JOptionPane.showMessageDialog(this,
                    "✅ Pedido #" + pedido.getId() + " salvo com sucesso!\n" +
                            "Total: R$ " + String.format("%.2f", pedido.getTotal().doubleValue()),
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            limparCampos();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "❌ Erro ao salvar pedido: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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

    private void atualizarTotais() {
        BigDecimal subtotal = itensPedido.stream()
                .map(item -> item.getPrecoUnitario()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        labelSubtotal.setText("R$ " + String.format("%.2f", subtotal.doubleValue()));

        BigDecimal total = subtotal;
        String taxaStr = campoTaxa.getText().trim();
        if (!taxaStr.isBlank()) {
            try {
                BigDecimal taxa = new BigDecimal(taxaStr.replace(",", "."));
                total = total.add(taxa);
            } catch (NumberFormatException ignored) {}
        }

        labelTotal.setText("R$ " + String.format("%.2f", total.doubleValue()));
    }

    private void habilitarTaxa() {
        if (!campoEndereco.getText().trim().isBlank()) {
            campoTaxa.setEnabled(true);
            campoTaxa.setText("10,00");
        } else {
            campoTaxa.setEnabled(false);
            campoTaxa.setText("");
            atualizarTotais();
        }
    }

    private void abrirMontagemCombo() {
        Combo comboSelecionado = (Combo) comboCombos.getSelectedItem();
        if (comboSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um combo!");
            return;
        }

        TelaMontagemCombo tela = new TelaMontagemCombo(parentFrame, comboSelecionado);
        tela.setVisible(true);

        List<Item> itensEscolhidos = tela.getItensSelecionados();
        if (itensEscolhidos != null && !itensEscolhidos.isEmpty()) {
            for (Item item : itensEscolhidos) {
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
            JOptionPane.showMessageDialog(this, "✅ Combo adicionado com " + itensEscolhidos.size() + " itens!");
        }
    }

    private void abrirMontagemCento() {
        Cento centoSelecionado = (Cento) comboCentos.getSelectedItem();
        if (centoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cento!");
            return;
        }

        TelaMontagemCento tela = new TelaMontagemCento(parentFrame, centoSelecionado);
        tela.setVisible(true);

        List<Item> itensEscolhidos = tela.getItensSelecionados();
        if (itensEscolhidos != null && !itensEscolhidos.isEmpty()) {
            for (Item item : itensEscolhidos) {
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
            JOptionPane.showMessageDialog(this, "✅ Cento adicionado com " + itensEscolhidos.size() + " itens!");
        }
    }
}