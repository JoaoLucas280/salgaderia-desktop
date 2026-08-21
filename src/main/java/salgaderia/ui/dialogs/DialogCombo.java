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

    private JLabel labelContadorSabores;
    private JLabel labelContadorAdicionais;

    private JTable tabelaItens;
    private DefaultTableModel modeloTabelaItens;
    private List<ItemCombo> itensCombo;


    private JTable tabelaAdicionais;
    private DefaultTableModel modeloTabelaAdicionais;
    private List<Adicional> adicionaisElegiveis;
    private List<Adicional> adicionaisDisponiveis;

    public DialogCombo(JFrame parent, Combo comboExistente) {
        super(parent, comboExistente == null ? "🧩 Novo Combo" : "✏️ Editar Combo", true);
        this.dao = DadosDAO.getInstance();
        this.combo = comboExistente;
        this.itensCombo = new ArrayList<>();
        this.adicionaisElegiveis = new ArrayList<>();
        this.adicionaisDisponiveis = new ArrayList<>();

        if (comboExistente != null) {
            if (comboExistente.getItens() != null) {
                this.itensCombo.addAll(comboExistente.getItens());
            }
            if (comboExistente.getAdicionaisElegiveis() != null) {
                this.adicionaisElegiveis.addAll(comboExistente.getAdicionaisElegiveis());
            }
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
        painelDados.add(new JLabel("Preço (R$):"), gbc);

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


        JPanel painelTabelas = new JPanel(new GridLayout(2, 1, 5, 5));


        JPanel painelItens = new JPanel(new BorderLayout());
        painelItens.setBorder(StyleConfig.criarBorda("🥒 Sabores do Combo"));

        String[] colunasItens = {"Produto"};
        modeloTabelaItens = new DefaultTableModel(colunasItens, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaItens = new JTable(modeloTabelaItens);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollItens = new JScrollPane(tabelaItens);
        scrollItens.setPreferredSize(new Dimension(400, 100));
        painelItens.add(scrollItens, BorderLayout.CENTER);

        JPanel painelBotoesItens = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        labelContadorSabores = new JLabel("🥒 Sabores: 0");
        labelContadorSabores.setFont(new Font("Arial", Font.BOLD, 12));
        labelContadorSabores.setForeground(StyleConfig.COR_PRIMARIA_ESCURA);
        painelBotoesItens.add(labelContadorSabores);

        JButton botaoAdicionarItem = new JButton("➕ Adicionar Sabor");
        StyleConfig.estilizarBotaoSecundario(botaoAdicionarItem);
        botaoAdicionarItem.addActionListener(e -> adicionarItem());
        painelBotoesItens.add(botaoAdicionarItem);

        JButton botaoRemoverItem = new JButton("➖ Remover Sabor");
        StyleConfig.estilizarBotao(botaoRemoverItem, StyleConfig.COR_ERRO);
        botaoRemoverItem.addActionListener(e -> removerItem());
        painelBotoesItens.add(botaoRemoverItem);

        painelItens.add(painelBotoesItens, BorderLayout.SOUTH);

        JPanel painelAdicionais = new JPanel(new BorderLayout());
        painelAdicionais.setBorder(StyleConfig.criarBorda("🥤 Adicionais Elegíveis"));

        String[] colunasAdicionais = {"Adicional"};
        modeloTabelaAdicionais = new DefaultTableModel(colunasAdicionais, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaAdicionais = new JTable(modeloTabelaAdicionais);
        tabelaAdicionais.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollAdicionais = new JScrollPane(tabelaAdicionais);
        scrollAdicionais.setPreferredSize(new Dimension(400, 100));
        painelAdicionais.add(scrollAdicionais, BorderLayout.CENTER);

        JPanel painelBotoesAdicionais = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        labelContadorAdicionais = new JLabel("🥤 Adicionais: 0");
        labelContadorAdicionais.setFont(new Font("Arial", Font.BOLD, 12));
        labelContadorAdicionais.setForeground(StyleConfig.COR_PRIMARIA_ESCURA);
        painelBotoesAdicionais.add(labelContadorAdicionais);

        JButton botaoAdicionarAdicional = new JButton("➕ Adicionar Adicional");
        StyleConfig.estilizarBotaoSecundario(botaoAdicionarAdicional);
        botaoAdicionarAdicional.addActionListener(e -> adicionarAdicional());
        painelBotoesAdicionais.add(botaoAdicionarAdicional);

        JButton botaoRemoverAdicional = new JButton("➖ Remover Adicional");
        StyleConfig.estilizarBotao(botaoRemoverAdicional, StyleConfig.COR_ERRO);
        botaoRemoverAdicional.addActionListener(e -> removerAdicional());
        painelBotoesAdicionais.add(botaoRemoverAdicional);

        painelAdicionais.add(painelBotoesAdicionais, BorderLayout.SOUTH);

        painelTabelas.add(painelItens);
        painelTabelas.add(painelAdicionais);

        add(painelTabelas, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("💾 Salvar");
        StyleConfig.estilizarBotao(botaoSalvar, StyleConfig.COR_SUCESSO);
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        StyleConfig.estilizarBotaoSecundario(botaoCancelar);
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        carregarTabelaItens();
        carregarTabelaAdicionais();
        carregarAdicionaisDisponiveis();
    }


    private void adicionarItem() {
        System.out.println("🔵 adicionarItem() chamado");
        System.out.println("   itensCombo.size() ANTES: " + itensCombo.size());

        DialogItemCombo dialog = new DialogItemCombo(this);
        dialog.setVisible(true);

        ItemCombo item = dialog.getItemCombo();
        System.out.println("   DialogItemCombo retornou: " + (item != null ? "✅ ItemCombo não-nulo" : "❌ null"));

        if (item != null) {
            System.out.println("   Adicionando item: " + item.getProduto().getNomeProduto() + " (id=" + item.getProduto().getId() + ")");
            itensCombo.add(item);
            System.out.println("   itensCombo.size() DEPOIS: " + itensCombo.size());
            carregarTabelaItens();
            atualizarContadores();
        } else {
            System.out.println("   ⚠️  AVISO: item é null, não adicionado à lista");
        }
    }

    private void removerItem() {
        int linha = tabelaItens.getSelectedRow();
        if (linha >= 0) {
            itensCombo.remove(linha);
            carregarTabelaItens();
            atualizarContadores();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um sabor para remover!");
        }
    }

    private void carregarTabelaItens() {
        System.out.println("📋 carregarTabelaItens() chamado");
        System.out.println("   itensCombo.size() = " + itensCombo.size());

        modeloTabelaItens.setRowCount(0);

        if (itensCombo.isEmpty()) {
            System.out.println("   ⚠️  AVISO: itensCombo está vazio!");
        }

        for (ItemCombo item : itensCombo) {
            if (item.getProduto() != null) {
                System.out.println("   ✓ Adicionando linha na tabela: " + item.getProduto().getNomeProduto());
                modeloTabelaItens.addRow(new Object[]{
                        item.getProduto().getNomeProduto()
                });
            } else {
                System.out.println("   ⚠️  AVISO: ItemCombo sem produto!");
            }
        }

        System.out.println("   Total de linhas na tabela: " + modeloTabelaItens.getRowCount());
    }

    private void atualizarContadores() {
        labelContadorSabores.setText("🥒 Sabores: " + itensCombo.size());
        labelContadorAdicionais.setText("🥤 Adicionais: " + adicionaisElegiveis.size());
    }


    private void carregarAdicionaisDisponiveis() {
        adicionaisDisponiveis = dao.carregarAdicionais();
    }

    private void adicionarAdicional() {
        if (adicionaisDisponiveis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum adicional cadastrado! Cadastre na aba Adicionais.");
            return;
        }

        String[] nomes = adicionaisDisponiveis.stream()
                .map(a -> a.getNome() + " (R$ " + String.format("%.2f", a.getPreco()) + ")")
                .toArray(String[]::new);

        String selecionado = (String) JOptionPane.showInputDialog(
                this,
                "Selecione um adicional elegível:",
                "Adicionar Adicional",
                JOptionPane.PLAIN_MESSAGE,
                null,
                nomes,
                nomes[0]
        );

        if (selecionado != null) {
            for (Adicional a : adicionaisDisponiveis) {
                String nomeCompleto = a.getNome() + " (R$ " + String.format("%.2f", a.getPreco()) + ")";
                if (nomeCompleto.equals(selecionado)) {
                    boolean jaExiste = adicionaisElegiveis.stream()
                            .anyMatch(ad -> ad.getId() == a.getId());
                    if (!jaExiste) {
                        adicionaisElegiveis.add(a);
                        carregarTabelaAdicionais();
                        atualizarContadores();
                    } else {
                        JOptionPane.showMessageDialog(this, "Este adicional já foi adicionado!");
                    }
                    break;
                }
            }
        }
    }

    private void removerAdicional() {
        int linha = tabelaAdicionais.getSelectedRow();
        if (linha >= 0) {
            adicionaisElegiveis.remove(linha);
            carregarTabelaAdicionais();
            atualizarContadores();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um adicional para remover!");
        }
    }

    private void carregarTabelaAdicionais() {
        modeloTabelaAdicionais.setRowCount(0);
        for (Adicional a : adicionaisElegiveis) {
            modeloTabelaAdicionais.addRow(new Object[]{
                    a.getNome() + " (R$ " + String.format("%.2f", a.getPreco()) + ")"
            });
        }
    }

    private void preencherCampos() {
        campoNome.setText(combo.getNome());
        campoPreco.setText(String.format("%.2f", combo.getPrecoTotal()).replace(".", ","));
        campoMaxItems.setText(String.valueOf(combo.getQuantidadeMaximaDeItems()));
        campoMaxFlavors.setText(String.valueOf(combo.getQuantidadeMaximaDeFlavors()));

        if (combo.getAdicionaisElegiveis() != null) {
            adicionaisElegiveis.addAll(combo.getAdicionaisElegiveis());
            carregarTabelaAdicionais();
        }
    }

    private void salvar() {
        try {
            String nome = campoNome.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome é obrigatório!");
                return;
            }

            BigDecimal preco = new BigDecimal(campoPreco.getText().trim().replace(",", "."));
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

            System.out.println("💾 salvar() chamado com " + itensCombo.size() + " itens e " + adicionaisElegiveis.size() + " adicionais elegíveis:");
            for (int i = 0; i < itensCombo.size(); i++) {
                ItemCombo item = itensCombo.get(i);
                System.out.println("  ITEM [" + i + "] produto_id=" + item.getProduto().getId() + ", nome=" + item.getProduto().getNomeProduto());
            }
            for (int i = 0; i < adicionaisElegiveis.size(); i++) {
                Adicional a = adicionaisElegiveis.get(i);
                System.out.println("  ADICIONAL [" + i + "] adicional_id=" + a.getId() + ", nome=" + a.getNome());
            }

            if (combo == null) {
                Combo novo = new Combo();
                novo.setNome(nome);
                novo.setPrecoTotal(preco);
                novo.setQuantidadeMaximaDeItems(maxItems);
                novo.setQuantidadeMaximaDeFlavors(maxFlavors);
                novo.setItens(new ArrayList<>(itensCombo));
                novo.setAdicionaisElegiveis(new ArrayList<>(adicionaisElegiveis));
                novo.setQuantidadeAdicionaisPermitidos(1); // Sempre 1 adicional

                dao.salvarCombo(novo);
                System.out.println("✅ Combo salvo: " + nome + " (ID: " + novo.getId() + ")");

            } else {
                combo.setNome(nome);
                combo.setPrecoTotal(preco);
                combo.setQuantidadeMaximaDeItems(maxItems);
                combo.setQuantidadeMaximaDeFlavors(maxFlavors);
                combo.setItens(new ArrayList<>(itensCombo));
                combo.setAdicionaisElegiveis(new ArrayList<>(adicionaisElegiveis));
                combo.setQuantidadeAdicionaisPermitidos(1);

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