package salgaderia.ui.dialogs;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Cento;
import salgaderia.model.ItemCombo;
import salgaderia.model.Produto;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DialogCento extends JDialog {

    private DadosDAO dao;
    private Cento cento;
    private boolean salvou = false;

    private JTextField campoNome;
    private JTextField campoPreco;
    private JTextField campoMaxSabores;

    private JTable tabelaItens;
    private DefaultTableModel modeloTabela;
    private List<ItemCombo> itensCento;

    public DialogCento(JFrame parent, Cento centoExistente) {
        super(parent, centoExistente == null ? "📦 Novo Cento" : "✏️ Editar Cento", true);
        this.dao = DadosDAO.getInstance();
        this.cento = centoExistente;
        this.itensCento = new ArrayList<>();

        if (centoExistente != null && centoExistente.getItens() != null) {
            this.itensCento.addAll(centoExistente.getItens());
        }

        initComponents();
        configurarJanela();

        if (centoExistente != null) {
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
        painelDados.add(new JLabel("Máx Sabores:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        campoMaxSabores = new JTextField(10);
        campoMaxSabores.setPreferredSize(new Dimension(120, 30));
        campoMaxSabores.setText("4");
        painelDados.add(campoMaxSabores, gbc);

        add(painelDados, BorderLayout.NORTH);

        JPanel painelTabela = new JPanel(new BorderLayout());
        painelTabela.setBorder(BorderFactory.createTitledBorder("🥒 Sabores do Cento (100 salgados)"));

        String[] colunas = {"Produto"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaItens = new JTable(modeloTabela);
        tabelaItens.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tabelaItens);
        scroll.setPreferredSize(new Dimension(400, 150));
        painelTabela.add(scroll, BorderLayout.CENTER);

        JPanel painelBotoesTabela = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JButton botaoAdicionar = new JButton("➕ Adicionar Sabor");
        botaoAdicionar.addActionListener(e -> adicionarItem());
        painelBotoesTabela.add(botaoAdicionar);

        JButton botaoRemover = new JButton("➖ Remover Sabor");
        botaoRemover.addActionListener(e -> removerItem());
        painelBotoesTabela.add(botaoRemover);

        painelTabela.add(painelBotoesTabela, BorderLayout.SOUTH);

        add(painelTabela, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("💾 Salvar");
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);

        carregarTabela();
    }

    private void adicionarItem() {
        DialogItemCombo dialog = new DialogItemCombo(this);
        dialog.setVisible(true);

        ItemCombo item = dialog.getItemCombo();
        if (item != null) {
            itensCento.add(item);
            carregarTabela();
        }
    }

    private void removerItem() {
        int linha = tabelaItens.getSelectedRow();
        if (linha >= 0) {
            itensCento.remove(linha);
            carregarTabela();
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um item para remover!");
        }
    }

    private void carregarTabela() {
        modeloTabela.setRowCount(0);
        for (ItemCombo item : itensCento) {
            modeloTabela.addRow(new Object[]{
                    item.getProduto().getNomeProduto()
            });
        }
    }

    private void preencherCampos() {
        campoNome.setText(cento.getNome());
        campoPreco.setText(String.format("%.2f", cento.getPrecoTotal()).replace(".", ","));
        campoMaxSabores.setText(String.valueOf(cento.getMaxSabores()));
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

            int maxSabores = Integer.parseInt(campoMaxSabores.getText().trim());
            if (maxSabores <= 0) {
                JOptionPane.showMessageDialog(this, "Máximo de sabores deve ser maior que zero!");
                return;
            }

            if (itensCento.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Adicione pelo menos um sabor!");
                return;
            }

            if (cento == null) {
                Cento novo = new Cento();
                novo.setNome(nome);
                novo.setPrecoTotal(preco);
                novo.setMaxSabores(maxSabores);
                novo.setItens(new ArrayList<>(itensCento));

                dao.salvarCento(novo);
                System.out.println("✅ Cento salvo: " + nome);
            } else {
                cento.setNome(nome);
                cento.setPrecoTotal(preco);
                cento.setMaxSabores(maxSabores);
                cento.setItens(new ArrayList<>(itensCento));

                dao.atualizarCento(cento);
                System.out.println("✅ Cento atualizado: " + nome);
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
        setSize(500, 450);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public boolean salvou() {
        return salvou;
    }
}