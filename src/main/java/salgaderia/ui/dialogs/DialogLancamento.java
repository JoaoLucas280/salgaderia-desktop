package salgaderia.ui.dialogs;

import salgaderia.model.LancamentoFinanceiro;
import salgaderia.model.enums.tipoLancamento;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DialogLancamento extends JDialog {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String[] CATEGORIAS_ENTRADA = {"Venda de Pedido", "Venda Avulsa", "Outros"};
    private static final String[] CATEGORIAS_SAIDA = {
            "Ingredientes", "Gás", "Embalagens", "Aluguel", "Energia", "Água", "Transporte", "Manutenção", "Salários", "Outros"
    };
    public static final String[] FORMAS_PAGAMENTO = {"-", "Dinheiro", "Pix", "Cartão de Crédito", "Cartão de Débito", "Outro"};

    private final LancamentoFinanceiro lancamentoExistente;
    private final tipoLancamento tipoFixo;
    private LancamentoFinanceiro lancamentoSalvo;
    private boolean salvou = false;

    private JComboBox<String> comboTipo;
    private JComboBox<String> comboCategoria;
    private JTextField campoDescricao;
    private JTextField campoValor;
    private JTextField campoData;
    private JComboBox<String> comboFormaPagamento;
    private JTextField campoObservacao;

    public DialogLancamento(JFrame parent, LancamentoFinanceiro lancamentoExistente) {
        this(parent, lancamentoExistente, null);
    }

    public DialogLancamento(JFrame parent, LancamentoFinanceiro lancamentoExistente, tipoLancamento tipoFixo) {
        super(parent, tituloJanela(lancamentoExistente, tipoFixo), true);
        this.lancamentoExistente = lancamentoExistente;
        this.tipoFixo = tipoFixo;

        initComponents();
        configurarJanela();

        if (lancamentoExistente != null) {
            preencherCampos();
        } else {
            campoData.setText(LocalDate.now().format(FORMATO_DATA));

            if (tipoFixo != null) {
                comboTipo.setSelectedIndex(tipoFixo == tipoLancamento.ENTRADA ? 0 : 1);
                comboTipo.setEnabled(false);
                atualizarCategoriasDisponiveis();
            }
        }
    }

    private static String tituloJanela(LancamentoFinanceiro existente, tipoLancamento tipoFixo) {
        if (existente != null) {
            return "✏️ Editar Lançamento";
        }
        if (tipoFixo == tipoLancamento.ENTRADA) {
            return "🟢 Nova Entrada";
        }
        if (tipoFixo == tipoLancamento.SAIDA) {
            return "🔴 Nova Saída";
        }
        return "➕ Novo Lançamento";
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int linha = 0;

        // Tipo
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1; gbc.gridy = linha; gbc.gridwidth = 2; gbc.weightx = 1.0;
        comboTipo = new JComboBox<>(new String[]{"🟢 Entrada", "🔴 Saída"});
        comboTipo.addActionListener(e -> atualizarCategoriasDisponiveis());
        add(comboTipo, gbc);
        linha++;

        // Categoria
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("Categoria:"), gbc);

        gbc.gridx = 1; gbc.gridy = linha; gbc.gridwidth = 2; gbc.weightx = 1.0;
        comboCategoria = new JComboBox<>(CATEGORIAS_ENTRADA);
        comboCategoria.setEditable(true); // permite digitar uma categoria fora da lista sugerida
        add(comboCategoria, gbc);
        linha++;

        // Descrição
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1; gbc.gridy = linha; gbc.gridwidth = 2; gbc.weightx = 1.0;
        campoDescricao = new JTextField(20);
        campoDescricao.setPreferredSize(new Dimension(250, 30));
        add(campoDescricao, gbc);
        linha++;

        // Valor
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("Valor (R$):"), gbc);

        gbc.gridx = 1; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0.5;
        campoValor = new JTextField(10);
        campoValor.setPreferredSize(new Dimension(120, 30));
        add(campoValor, gbc);
        linha++;

        // Data
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("Data (dd/mm/aaaa):"), gbc);

        gbc.gridx = 1; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0.5;
        campoData = new JTextField(10);
        campoData.setPreferredSize(new Dimension(120, 30));
        add(campoData, gbc);
        linha++;

        // Forma de pagamento
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("Forma de pagamento:"), gbc);

        gbc.gridx = 1; gbc.gridy = linha; gbc.gridwidth = 2; gbc.weightx = 1.0;
        comboFormaPagamento = new JComboBox<>(FORMAS_PAGAMENTO);
        add(comboFormaPagamento, gbc);
        linha++;

        // Observação
        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 1; gbc.weightx = 0;
        add(new JLabel("Observação:"), gbc);

        gbc.gridx = 1; gbc.gridy = linha; gbc.gridwidth = 2; gbc.weightx = 1.0;
        campoObservacao = new JTextField(20);
        campoObservacao.setPreferredSize(new Dimension(250, 30));
        add(campoObservacao, gbc);
        linha++;

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("💾 Salvar");
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        gbc.gridx = 0; gbc.gridy = linha; gbc.gridwidth = 3;
        add(painelBotoes, gbc);
    }


    private void atualizarCategoriasDisponiveis() {
        Object textoAtual = comboCategoria.getEditor().getItem();

        String[] novasCategorias = comboTipo.getSelectedIndex() == 0 ? CATEGORIAS_ENTRADA : CATEGORIAS_SAIDA;
        comboCategoria.setModel(new DefaultComboBoxModel<>(novasCategorias));

        if (textoAtual != null && !textoAtual.toString().isBlank()) {
            comboCategoria.setSelectedItem(textoAtual);
        }
    }

    private void preencherCampos() {
        comboTipo.setSelectedIndex(lancamentoExistente.getTipo() == tipoLancamento.ENTRADA ? 0 : 1);
        atualizarCategoriasDisponiveis();

        comboCategoria.setSelectedItem(lancamentoExistente.getCategoria());
        campoDescricao.setText(lancamentoExistente.getDescricao());
        campoValor.setText(String.format("%.2f", lancamentoExistente.getValor()).replace(".", ","));
        campoData.setText(lancamentoExistente.getData().format(FORMATO_DATA));

        if (lancamentoExistente.getFormaPagamento() != null) {
            comboFormaPagamento.setSelectedItem(lancamentoExistente.getFormaPagamento());
        }
        if (lancamentoExistente.getObservacao() != null) {
            campoObservacao.setText(lancamentoExistente.getObservacao());
        }
    }

    private void salvar() {
        try {
            String descricao = campoDescricao.getText().trim();
            if (descricao.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Descrição é obrigatória!");
                return;
            }

            String categoria = comboCategoria.getEditor().getItem().toString().trim();
            if (categoria.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Categoria é obrigatória!");
                return;
            }

            BigDecimal valor;
            try {
                valor = new BigDecimal(campoValor.getText().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Valor inválido!");
                return;
            }
            if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Valor deve ser maior que zero!");
                return;
            }

            LocalDate data;
            try {
                data = LocalDate.parse(campoData.getText().trim(), FORMATO_DATA);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Data inválida! Use o formato dd/mm/aaaa.");
                return;
            }

            tipoLancamento tipo = comboTipo.getSelectedIndex() == 0 ? tipoLancamento.ENTRADA : tipoLancamento.SAIDA;

            String formaPagamento = (String) comboFormaPagamento.getSelectedItem();
            if ("-".equals(formaPagamento)) {
                formaPagamento = null;
            }

            String observacao = campoObservacao.getText().trim();
            if (observacao.isEmpty()) {
                observacao = null;
            }

            if (lancamentoExistente == null) {
                lancamentoSalvo = new LancamentoFinanceiro();
            } else {
                lancamentoSalvo = lancamentoExistente;
            }

            lancamentoSalvo.setTipo(tipo);
            lancamentoSalvo.setCategoria(categoria);
            lancamentoSalvo.setDescricao(descricao);
            lancamentoSalvo.setValor(valor);
            lancamentoSalvo.setData(data);
            lancamentoSalvo.setFormaPagamento(formaPagamento);
            lancamentoSalvo.setObservacao(observacao);

            salvou = true;
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarJanela() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public boolean salvou() {
        return salvou;
    }

    public LancamentoFinanceiro getLancamento() {
        return salvou ? lancamentoSalvo : null;
    }
}