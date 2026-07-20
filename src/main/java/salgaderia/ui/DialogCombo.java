package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DialogCombo extends JDialog {

    private Combo comboSalvo;
    private boolean salvou = false;

    private JTextField campoNome;
    private JTextField campoPreco;
    private JTextField campoMaxItems;
    private JTextField campoMaxFlavors;
    private JSpinner spinnerQtdAdicionais;  // ★ NOVO
    private JTable tabelaItens;
    private DefaultTableModel modeloTabela;
    private List<ItemCombo> itensCombo;

    // ★ ADICIONAIS ★
    private List<JCheckBox> checkboxesAdicionais;
    private List<Adicional> adicionaisDisponiveis;

    public DialogCombo(JFrame parent, Combo comboExistente) {
        super(parent, comboExistente == null ? "🧩 Novo Combo" : "🧩 Editar Combo", true);
        this.itensCombo = new ArrayList<>();
        this.checkboxesAdicionais = new ArrayList<>();
        this.adicionaisDisponiveis = new ArrayList<>();

        if (comboExistente != null) {
            this.comboSalvo = comboExistente;
            if (comboExistente.getItens() != null) {
                this.itensCombo.addAll(comboExistente.getItens());
            }
        }

        initComponents();
        configurarJanela();

        if (comboExistente != null) {
            preencherCampos(comboExistente);
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // ===== PAINEL DE DADOS =====
        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        painelDados.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        campoNome = new JTextField(20);
        painelDados.add(campoNome, gbc);

        // Preço Total
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        painelDados.add(new JLabel("Preço Total (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        campoPreco = new JTextField(10);
        painelDados.add(campoPreco, gbc);

        // Máximo de Itens
        gbc.gridx = 0; gbc.gridy = 2;
        painelDados.add(new JLabel("Máx Itens:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        campoMaxItems = new JTextField(10);
        painelDados.add(campoMaxItems, gbc);

        // Máximo de Sabores
        gbc.gridx = 0; gbc.gridy = 3;
        painelDados.add(new JLabel("Máx Sabores:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        campoMaxFlavors = new JTextField(10);
        painelDados.add(campoMaxFlavors, gbc);

        // ★ QUANTIDADE DE ADICIONAIS PERMITIDOS ★
        gbc.gridx = 0; gbc.gridy = 4;
        painelDados.add(new JLabel("Qtd Adicionais Permitidos:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        spinnerQtdAdicionais = new JSpinner(new SpinnerNumberModel(0, 0, 10, 1));
        painelDados.add(spinnerQtdAdicionais, gbc);

        add(painelDados, BorderLayout.NORTH);

        // ===== TABELA DE ITENS DO COMBO =====
        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("Itens do Combo"));

        modeloTabela = new DefaultTableModel(
                new String[]{"Produto", "Quantidade Máxima"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaItens = new JTable(modeloTabela);
        JScrollPane scroll = new JScrollPane(tabelaItens);
        scroll.setPreferredSize(new Dimension(400, 100));
        painelTabela.add(scroll, BorderLayout.CENTER);

        JPanel painelBotoesTabela = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton botaoAdicionarItem = new JButton("➕ Adicionar Item");
        botaoAdicionarItem.addActionListener(e -> adicionarItem());
        painelBotoesTabela.add(botaoAdicionarItem);

        JButton botaoRemoverItem = new JButton("➖ Remover Item");
        botaoRemoverItem.addActionListener(e -> removerItem());
        painelBotoesTabela.add(botaoRemoverItem);

        painelTabela.add(painelBotoesTabela, BorderLayout.SOUTH);

        add(painelTabela, BorderLayout.CENTER);

        // ===== ADICIONAIS ELEGÍVEIS =====
        JPanel painelAdicionais = new JPanel(new BorderLayout());
        painelAdicionais.setBorder(BorderFactory.createTitledBorder("Adicionais Elegíveis"));

        JPanel painelCheckboxes = new JPanel(new GridLayout(0, 2, 10, 5));
        painelCheckboxes.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // ★ CARREGA ADICIONAIS DO JSON ★
        DadosDAO dao = new DadosDAO();
        adicionaisDisponiveis = dao.carregarAdicionais();

        if (adicionaisDisponiveis.isEmpty()) {
            // Fallback
            adicionaisDisponiveis.add(new Adicional(1, "Refrigerante", BigDecimal.valueOf(5.00)));
            adicionaisDisponiveis.add(new Adicional(2, "Suco", BigDecimal.valueOf(4.00)));
            adicionaisDisponiveis.add(new Adicional(3, "Água de Coco", BigDecimal.valueOf(3.00)));
        }

        for (Adicional a : adicionaisDisponiveis) {
            JCheckBox checkbox = new JCheckBox(a.getNome() + " (R$ " + String.format("%.2f", a.getPreco()) + ")");
            checkboxesAdicionais.add(checkbox);
            painelCheckboxes.add(checkbox);
        }

        painelAdicionais.add(painelCheckboxes, BorderLayout.CENTER);
        add(painelAdicionais, BorderLayout.SOUTH);

        // ===== BOTÕES SALVAR/CANCELAR =====
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("💾 Salvar");
        botaoSalvar.addActionListener(e -> salvarCombo());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        carregarTabelaItens();
    }

    private void adicionarItem() {
        DialogItemCombo dialog = new DialogItemCombo(this);
        dialog.setVisible(true);

        ItemCombo item = dialog.getItemCombo();
        if (item != null) {
            itensCombo.add(item);
            carregarTabelaItens();
        }
    }

    private void removerItem() {
        int linha = tabelaItens.getSelectedRow();
        if (linha >= 0) {
            itensCombo.remove(linha);
            carregarTabelaItens();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover!");
        }
    }

    private void carregarTabelaItens() {
        modeloTabela.setRowCount(0);
        for (ItemCombo item : itensCombo) {
            modeloTabela.addRow(new Object[]{
                    item.getProduto().getNomeProduto(),
                    item.getQuantidadeMaxima()
            });
        }
    }

    private void preencherCampos(Combo combo) {
        campoNome.setText(combo.getNome());
        campoPreco.setText(String.format("%.2f", combo.getPrecoTotal().doubleValue()));
        campoMaxItems.setText(String.valueOf(combo.getQuantidadeMaximaDeItems()));
        campoMaxFlavors.setText(String.valueOf(combo.getQuantidadeMaximaDeFlavors()));
        spinnerQtdAdicionais.setValue(combo.getQuantidadeAdicionaisPermitidos());

        // Marca os adicionais elegíveis
        if (combo.getAdicionaisElegiveis() != null) {
            for (JCheckBox checkbox : checkboxesAdicionais) {
                for (Adicional a : combo.getAdicionaisElegiveis()) {
                    if (checkbox.getText().startsWith(a.getNome())) {
                        checkbox.setSelected(true);
                        break;
                    }
                }
            }
        }
    }

    private void salvarCombo() {
        try {
            String nome = campoNome.getText().trim();
            if (nome.isBlank()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
                return;
            }

            BigDecimal preco = new BigDecimal(campoPreco.getText().replace(",", "."));
            int maxItems = Integer.parseInt(campoMaxItems.getText());
            int maxFlavors = Integer.parseInt(campoMaxFlavors.getText());

            if (itensCombo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Adicione pelo menos um item ao combo!");
                return;
            }

            // ★ PEGA OS ADICIONAIS SELECIONADOS ★
            List<Adicional> adicionaisSelecionados = new ArrayList<>();
            for (int i = 0; i < checkboxesAdicionais.size(); i++) {
                if (checkboxesAdicionais.get(i).isSelected()) {
                    adicionaisSelecionados.add(adicionaisDisponiveis.get(i));
                }
            }

            int qtdAdicionaisPermitidos = (int) spinnerQtdAdicionais.getValue();

            // Validação: se tem adicionais selecionados, a quantidade permitida deve ser > 0
            if (!adicionaisSelecionados.isEmpty() && qtdAdicionaisPermitidos <= 0) {
                int resposta = JOptionPane.showConfirmDialog(this,
                        "Você selecionou adicionais mas a quantidade permitida é 0. Deseja continuar?",
                        "Aviso",
                        JOptionPane.YES_NO_OPTION);
                if (resposta != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            int id = comboSalvo != null ? comboSalvo.getId() : (int) System.currentTimeMillis();

            comboSalvo = new Combo(id, nome, itensCombo, preco, maxItems, maxFlavors,
                    adicionaisSelecionados, qtdAdicionaisPermitidos);
            salvou = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preencha os campos numéricos corretamente!");
        }
    }

    private void configurarJanela() {
        setSize(600, 650);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public Combo getComboSalvo() {
        return salvou ? comboSalvo : null;
    }
}