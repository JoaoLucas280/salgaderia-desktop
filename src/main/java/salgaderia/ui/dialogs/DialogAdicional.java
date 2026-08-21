package salgaderia.ui.dialogs;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Adicional;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DialogAdicional extends JDialog {

    private DadosDAO dao;
    private Adicional adicional;
    private boolean salvou = false;

    private JTextField campoNome;
    private JTextField campoPreco;

    public DialogAdicional(JFrame parent, Adicional adicionalExistente) {
        super(parent, adicionalExistente == null ? "➕ Novo Adicional" : "Editar Adicional", true);
        this.dao = DadosDAO.getInstance();
        this.adicional = adicionalExistente;

        initComponents();
        configurarJanela();

        if (adicionalExistente != null) {
            preencherCampos();
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        campoNome = new JTextField(20);
        campoNome.setPreferredSize(new Dimension(250, 30));
        add(campoNome, gbc);


        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        add(new JLabel("Preço (R$):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        campoPreco = new JTextField(10);
        campoPreco.setPreferredSize(new Dimension(120, 30));
        add(campoPreco, gbc);


        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("💾 Salvar");
        StyleConfig.estilizarBotao(botaoSalvar, StyleConfig.COR_SUCESSO);
        botaoSalvar.addActionListener(e -> salvar());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        StyleConfig.estilizarBotao(botaoCancelar, StyleConfig.COR_ERRO);
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weightx = 0;
        add(painelBotoes, gbc);
    }

    private void preencherCampos() {
        campoNome.setText(adicional.getNome());
        campoPreco.setText(String.format("%.2f", adicional.getPreco()).replace(".", ","));
    }

    private void salvar() {
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

        try {
            BigDecimal preco = new BigDecimal(precoStr);

            if (preco.compareTo(BigDecimal.ZERO) <= 0) {
                JOptionPane.showMessageDialog(this, "Preço deve ser maior que zero!");
                return;
            }

            if (adicional == null) {
                Adicional novo = new Adicional(0, nome, preco);
                dao.salvarAdicional(novo);
                System.out.println("✅ Adicional salvo: " + nome);
            } else {
                adicional.setNome(nome);
                adicional.setPreco(preco);
                dao.atualizarAdicional(adicional);
                System.out.println("✅ Adicional atualizado: " + nome);
            }

            salvou = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Use números (ex: 0,60)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarJanela() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public boolean salvou() {
        return salvou;
    }
}