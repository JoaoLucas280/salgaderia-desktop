package salgaderia.ui;

import salgaderia.model.*;
import salgaderia.model.enums.tipoProduto;
import salgaderia.service.PedidoService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TelaPedido extends JFrame {

    private final PedidoService service;

    // ============ COMPONENTES ============
    // Dados do cliente
    private JTextField campoNome;
    private JTextField campoTelefone;
    private JTextField campoEndereco;
    private JTextField campoTaxa;

    // Aba UNITÁRIOS
    private JComboBox<Produto> comboUnitarios;
    private JSpinner spinnerUnitarioQtd;
    private JButton botaoAdicionarUnitario;

    // Aba COMBOS
    private JComboBox<Combo> comboCombos;
    private JButton botaoMontarCombo;

    // Aba CENTOS
    private JComboBox<Combo> comboCentos;
    private JButton botaoMontarCento;

    // Aba OFERTAS
    private JComboBox<Combo> comboOfertas;
    private JButton botaoMontarOferta;

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

    public TelaPedido() {
        this.service = new PedidoService();
        this.itensPedido = new ArrayList<>();
        initComponents();
        configurarJanela();
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
        painel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "📋 Dados do Cliente",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 14)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        painel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        campoNome = new JTextField(20);
        painel.add(campoNome, gbc);

        // Telefone
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Telefone:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        campoTelefone = new JTextField(15);
        painel.add(campoTelefone, gbc);

        // Endereço
        gbc.gridx = 0;
        gbc.gridy = 2;
        painel.add(new JLabel("Endereço:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        campoEndereco = new JTextField(20);
        painel.add(campoEndereco, gbc);

        // Taxa
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        painel.add(new JLabel("Taxa Entrega (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
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

        // Abas para cada tipo de produto
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("🥒 Unitários", criarAbaUnitarios());
        abas.addTab("🧩 Combos", criarAbaCombos());
        abas.addTab("📦 Centos", criarAbaCentos());
        abas.addTab("🎁 Ofertas", criarAbaOfertas());

        painel.add(abas, BorderLayout.NORTH);

        // Lista de itens do pedido
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

    private JPanel criarAbaOfertas() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        comboOfertas = new JComboBox<>();
        comboOfertas.setPreferredSize(new Dimension(250, 30));
        painel.add(new JLabel("Selecione uma Oferta:"));
        painel.add(comboOfertas);

        botaoMontarOferta = new JButton("🎁 Montar Oferta");
        botaoMontarOferta.addActionListener(e -> abrirMontagemOferta());
        painel.add(botaoMontarOferta);

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
        labelTotal.setFont(new Font("Arial", Font.BOLD, 16));
        labelTotal.setForeground(Color.BLUE);
        painelTotais.add(labelTotal);

        painel.add(painelTotais, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        botaoSalvar = new JButton("💾 Salvar Pedido");
        botaoSalvar.setBackground(new Color(0, 150, 0));
        botaoSalvar.setForeground(Color.WHITE);
        botaoSalvar.setFont(new Font("Arial", Font.BOLD, 14));
        botaoSalvar.addActionListener(e -> salvarPedido());
        painelBotoes.add(botaoSalvar);

        botaoLimpar = new JButton("🗑️ Limpar");
        botaoLimpar.addActionListener(e -> limparCampos());
        painelBotoes.add(botaoLimpar);

        painel.add(painelBotoes, BorderLayout.SOUTH);

        return painel;
    }

    private void carregarProdutos() {
        // Produtos UNITÁRIOS (preço fixo ou valor unitário)
        comboUnitarios.addItem(new Produto(1L, "Coxinha Unitária", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE));
        comboUnitarios.addItem(new Produto(2L, "Risole Unitária", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE));
        comboUnitarios.addItem(new Produto(3L, "Kibe Unitário", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE));
        comboUnitarios.addItem(new Produto(4L, "Churro Unitário", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE));

        // COMBOS (com limite de salgadinhos + bebidas)
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

        // CENTOS (100 salgados divisíveis)
        List<ItemCombo> itemsCento = new ArrayList<>();
        itemsCento.add(new ItemCombo(new Produto(201L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE), 100));
        itemsCento.add(new ItemCombo(new Produto(202L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE), 100));
        itemsCento.add(new ItemCombo(new Produto(203L, "Kibe", BigDecimal.valueOf(0.80), tipoProduto.UNIDADE), 100));
        itemsCento.add(new ItemCombo(new Produto(204L, "Churro", BigDecimal.valueOf(0.90), tipoProduto.UNIDADE), 100));
        comboCentos.addItem(new Combo(101, "Cento de Salgados (100 total)", itemsCento, BigDecimal.valueOf(60.00)));

        // OFERTAS
        List<ItemCombo> itensOferta = new ArrayList<>();
        itensOferta.add(new ItemCombo(new Produto(301L, "Coxinha", BigDecimal.valueOf(0.60), tipoProduto.UNIDADE), 30));
        itensOferta.add(new ItemCombo(new Produto(302L, "Risole", BigDecimal.valueOf(0.70), tipoProduto.UNIDADE), 20));
        comboOfertas.addItem(new Combo(201, "Oferta do Dia (50 salgados)", itensOferta, BigDecimal.valueOf(25.00)));
    }

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

    private void configurarJanela() {
        setTitle("📝 Novo Pedido - Salgaderia");
        setSize(750, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
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

        TelaMontagemCombo tela = new TelaMontagemCombo(this, comboSelecionado);
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
        Combo centoSelecionado = (Combo) comboCentos.getSelectedItem();
        if (centoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um cento!");
            return;
        }

        TelaMontagemCento tela = new TelaMontagemCento(this, centoSelecionado);
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

    private void abrirMontagemOferta() {
        Combo ofertaSelecionada = (Combo) comboOfertas.getSelectedItem();
        if (ofertaSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma oferta!");
            return;
        }

        TelaMontagemOferta tela = new TelaMontagemOferta(this, ofertaSelecionada);
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
            JOptionPane.showMessageDialog(this, "✅ Oferta adicionada com " + itensEscolhidos.size() + " itens!");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPedido tela = new TelaPedido();
            tela.setVisible(true);
        });
    }
}