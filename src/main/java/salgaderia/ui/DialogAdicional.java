package salgaderia.ui;

import salgaderia.model.Adicional;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class DialogAdicional extends JDialog {

    private Adicional adicionalSalvo;
    private boolean salvou = false;

    private JTextField campoNome;
    private JTextField campoPreco;

    public DialogAdicional(JFrame parent, Adicional adicionalExistente) {
        super(parent, "🥤 " + (adicionalExistente != null ? "Editar" : "Novo") + " Adicional", true);
        this.adicionalSalvo = null;

        initComponents(adicionalExistente);
        configurarJanela();

        if (adicionalExistente != null) {
            preencherCampos(adicionalExistente);
        }
    }

    private void initComponents(Adicional adicionalExistente) {
        setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Nome
        gbc.gridx = 0; gbc.gridy = 0;
        painelCampos.add(new JLabel("Nome do Adicional:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        campoNome = new JTextField(20);
        painelCampos.add(campoNome, gbc);

        // Preço
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        painelCampos.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        campoPreco = new JTextField(10);
        painelCampos.add(campoPreco, gbc);

        add(painelCampos, BorderLayout.CENTER);

        // Botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoSalvar = new JButton("✅ Salvar");
        botaoSalvar.addActionListener(e -> salvarAdicional());
        painelBotoes.add(botaoSalvar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.addActionListener(e -> dispose());
        painelBotoes.add(botaoCancelar);

        add(painelBotoes, BorderLayout.SOUTH);
    }

    private void preencherCampos(Adicional adicional) {
        campoNome.setText(adicional.getNome());
        campoPreco.setText(String.format("%.2f", adicional.getPreco().doubleValue()));
    }

    private void salvarAdicional() {
        String nome = campoNome.getText().trim();
        String precoStr = campoPreco.getText().trim();

        if (nome.isEmpty() || precoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nome e Preço são obrigatórios!");
            return;
        }

        try {
            BigDecimal preco = new BigDecimal(precoStr.replace(",", "."));


            adicionalSalvo = new Adicional(0, nome, preco);
            salvou = true;
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preço inválido! Use números (ex: 5,00)");
        }
    }

    private void configurarJanela() {
        setSize(400, 200);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public Adicional getAdicionalSalvo() {
        return salvou ? adicionalSalvo : null;
    }
}