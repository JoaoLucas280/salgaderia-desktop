package salgaderia.ui.dialogs;

import salgaderia.dao.DadosDAO;
import salgaderia.model.*;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DialogCombo extends JDialog {

    private DadosDAO dao;
    private Combo combo;
    private boolean salvou = false;


    private JTextField campoNome;
    private JTextField campoPreco;
    private JTextField campoMaxItems;
    private JTextField campoMaxFlavors;


    private JTable tabelaItens;
    private DefaultTableModel modeloTabelaItens;
    private List<ItemCombo> itensCombo;


    private List<JCheckBox> checkboxesAdicionais;
    private List<Adicional> adicionaisDisponiveis;

    public DialogCombo(JFrame parent, Combo comboExistente) {
        super(parent, comboExistente == null ? "🧩 Novo Combo" : "✏️ Editar Combo", true);
        this.dao = DadosDAO.getInstance();
        this.combo = comboExistente;
        this.itensCombo = new ArrayList<>();
        this.checkboxesAdicionais = new ArrayList<>();
        this.adicionaisDisponiveis = new ArrayList<>();

        if (comboExistente != null && comboExistente.getItens() != null) {
            this.itensCombo.addAll(comboExistente.getItens());
        }

        initComponents();
        configurarJanela();

        if (comboExistente != null) {
            preencherCampos();
        }
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));


        JPanel painelDados = new JPanel(new GridBagLayout());
        painelDados.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        campoNome = new JTextField(20);
        campoNome.setPreferredSize(new Dimension(250, 30));
        painelDados.add(campoNome, gbc);


        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Preço Total (R$):"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        campoPreco = new JTextField(10);
        campoPreco.setPreferredSize(new Dimension(120, 30));
        painelDados.add(campoPreco, gbc);


        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Máx Itens (total):"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        campoMaxItems = new JTextField(10);
        campoMaxItems.setPreferredSize(new Dimension(120, 30));
        campoMaxItems.setText("100");
        painelDados.add(campoMaxItems, gbc);


        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        painelDados.add(new JLabel("Máx Sabores:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        campoMaxFlavors = new JTextField(10);
        campoMaxFlavors.setPreferredSize(new Dimension(120, 30));
        campoMaxFlavors.setText("4");
        painelDados.add(campoMaxFlavors, gbc);

        add(painelDados, BorderLayout.NORTH);


        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("🥒 Itens do Combo (Sabores)"));

        String[] colunas = {"Produto"};
        modeloTabelaItens = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaItens = new JTable(modeloTabelaItens);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabelaItens);
        scroll.setPreferredSize(new Dimension(400, 120));
        painelTabela.add(scroll, BorderLayout.CENTER);


        JPanel painelBotoesTabela = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoAdicionarItem = new JButton("➕ Adicionar Sabor");
        botaoAdicionarItem.addActionListener(e -> adicionarItem());
        painelBotoesTabela.add(botaoAdicionarItem);

        JButton botaoRemoverItem = new JButton("➖ Remover Sabor");
        botaoRemoverItem.addActionListener(e -> removerItem());
        painelBotoesTabela.add(botaoRemoverItem);

        painelTabela.add(painelBotoesTabela, BorderLayout.SOUTH);

        add(painelTabela, BorderLayout.CENTER);


        JPanel painelAdicionais = new JPanel(new BorderLayout());
        painelAdicionais.setBorder(BorderFactory.createTitledBorder("🥤 Adicionais Elegíveis (cliente escolhe 1)"));

        JPanel painelCheckboxes = new JPanel(new GridLayout(0, 3, 10, 5));
        painelCheckboxes.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));


        adicionaisDisponiveis = dao.carregarAdicionais();

        if (adicionaisDisponiveis.isEmpty()) {
            JLabel label = new JLabel("Nenhum adicional cadastrado. Cadastre na aba Adicionais.");
            painelCheckboxes.add(label);
        } else {
            for (Adicional a : adicionaisDisponiveis) {
                JCheckBox checkbox = new JCheckBox(a.getNome() + " (R$ " + String.format("%.2f", a.getPreco()) + ")");
                checkboxesAdicionais.add(checkbox);
                painelCheckboxes.add(checkbox);
            }
        }

        painelAdicionais.add(painelCheckboxes, BorderLayout.CENTER);
        add(painelAdicionais, BorderLayout.SOUTH);


        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("💾 Salvar");
        StyleConfig.estilizarBotao(botaoSalvar, StyleConfig.COR_SUCESSO);
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        StyleConfig.estilizarBotao(botaoCancelar, StyleConfig.COR_ERRO);
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
        modeloTabelaItens.setRowCount(0);
        for (ItemCombo item : itensCombo) {
            modeloTabelaItens.addRow(new Object[]{
                    item.getProduto().getNomeProduto()
            });
        }
    }

    private void preencherCampos() {
        campoNome.setText(combo.getNome());
        campoPreco.setText(String.format("%.2f", combo.getPrecoTotal()).replace(".", ","));
        campoMaxItems.setText(String.valueOf(combo.getQuantidadeMaximaDeItems()));
        campoMaxFlavors.setText(String.valueOf(combo.getQuantidadeMaximaDeFlavors()));


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

    private void salvar() {
        try {
            String nome = campoNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
                return;
            }

            String precoStr = campoPreco.getText().trim().replace(",", ".");
            if (precoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Preço é obrigatório!");
                return;
            }

            BigDecimal preco = new BigDecimal(precoStr);
            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Preço deve ser maior que zero!");
                return;
            }

            int maxItems = Integer.parseInt(campoMaxItems.getText().trim());
            if (maxItems <= 0) {
                JOptionPane.showMessageDialog(this, "Máximo de itens deve ser maior que zero!");
                return;
            }

            int maxFlavors = Integer.parseInt(campoMaxFlavors.getText().trim());
            if (maxFlavors <= 0) {
                JOptionPane.showMessageDialog(this, "Máximo de sabores deve ser maior que zero!");
                return;
            }

            if (itensCombo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Adicione pelo menos um sabor ao combo!");
                return;
            }


            if (itensCombo.size() > maxFlavors) {
                JOptionPane.showMessageDialog(this,
                        "Você adicionou " + itensCombo.size() + " sabores, mas o máximo é " + maxFlavors + "!",
                        "Limite de sabores excedido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }


            List<Adicional> adicionaisSelecionados = new ArrayList<>();
            for (int i = 0; i < checkboxesAdicionais.size(); i++) {
                if (checkboxesAdicionais.get(i).isSelected()) {
                    adicionaisSelecionados.add(adicionaisDisponiveis.get(i));
                }
            }


            if (adicionaisSelecionados.isEmpty()) {
                int resposta = JOptionPane.showConfirmDialog(this,
                        "O combo não tem nenhum adicional elegível. Deseja continuar mesmo assim?",
                        "Sem adicionais",
                        JOptionPane.YES_NO_OPTION);
                if (resposta != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            int qtdAdicionaisPermitidos = 1;

            if (combo == null) {
                Combo novo = new Combo();
                novo.setNome(nome);
                novo.setPrecoTotal(preco);
                novo.setQuantidadeMaximaDeItems(maxItems);
                novo.setQuantidadeMaximaDeFlavors(maxFlavors);
                novo.setItens(new ArrayList<>(itensCombo));
                novo.setAdicionaisElegiveis(adicionaisSelecionados);
                novo.setQuantidadeAdicionaisPermitidos(qtdAdicionaisPermitidos);

                dao.salvarCombo(novo);
                System.out.println("✅ Combo salvo: " + nome + " (ID: " + novo.getId() + ")");

            } else {
                combo.setNome(nome);
                combo.setPrecoTotal(preco);
                combo.setQuantidadeMaximaDeItems(maxItems);
                combo.setQuantidadeMaximaDeFlavors(maxFlavors);
                combo.setItens(new ArrayList<>(itensCombo));
                combo.setAdicionaisElegiveis(adicionaisSelecionados);
                combo.setQuantidadeAdicionaisPermitidos(qtdAdicionaisPermitidos);

                dao.atualizarCombo(combo);
                System.out.println("✅ Combo atualizado: " + nome + " (ID: " + combo.getId() + ")");
            }

            salvou = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preencha os campos numéricos corretamente!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarJanela() {
        setSize(600, 550);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public boolean salvou() {
        return salvou;
    }
}
