package salgaderia.ui;

import salgaderia.model.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TelaMontagemOferta extends JDialog {

    private Combo oferta;
    private List<Item> itensSelecionados;
    private List<JSpinner> spinnersSalgados;
    private List<Produto> produtosSalgados;
    private JLabel labelTotalSalgados;
    private JButton botaoAdicionar;
    private boolean confirmado;
    private int limiteOferta;

    public TelaMontagemOferta(JFrame parent, Combo oferta) {
        super(parent, "🎁 Montar Oferta: " + oferta.getNome(), true);
        this.oferta = oferta;
        this.itensSelecionados = new ArrayList<>();
        this.spinnersSalgados = new ArrayList<>();
        this.produtosSalgados = new ArrayList<>();
        this.confirmado = false;
        this.limiteOferta = oferta.getItens().stream().mapToInt(ItemCombo::getQuantidadeMaxima).sum();

        initComponents();
        configurarJanela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel informações da oferta
        JPanel painelInfo = new JPanel(new GridLayout(2, 1, 5, 5));
        painelInfo.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "Oferta - Informações",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));
        painelInfo.add(new JLabel("Oferta: " + oferta.getNome()));
        painelInfo.add(new JLabel("Preço especial: R$ " + String.format("%.2f", oferta.getPrecoTotal().doubleValue())));
        add(painelInfo, BorderLayout.NORTH);

        // Painel central com scroll
        JPanel painelCentral = new JPanel(new BorderLayout(10, 10));
        painelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel salgados
        JPanel painelSalgados = new JPanel(new GridBagLayout());
        painelSalgados.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "🥒 Escolha os Salgados (máx " + limiteOferta + ")",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Arial", Font.BOLD, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        int linha = 0;
        for (ItemCombo item : oferta.getItens()) {
            Produto produto = item.getProduto();
            produtosSalgados.add(produto);

            // Nome do produto
            gbc.gridx = 0;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.5;
            JLabel labelNome = new JLabel(produto.getNomeProduto() + " (R$ " + String.format("%.2f", produto.getPrecoUnitario()) + ")");
            labelNome.setFont(new Font("Arial", Font.PLAIN, 11));
            painelSalgados.add(labelNome, gbc);

            // Spinner quantidade
            gbc.gridx = 1;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.3;
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, item.getQuantidadeMaxima(), 1));
            spinner.setPreferredSize(new Dimension(60, 30));
            spinnersSalgados.add(spinner);
            spinner.addChangeListener(e -> atualizarTotalSalgados());
            painelSalgados.add(spinner, gbc);

            // Máximo
            gbc.gridx = 2;
            gbc.gridy = linha;
            gbc.gridwidth = 1;
            gbc.weightx = 0.2;
            painelSalgados.add(new JLabel("Máx: " + item.getQuantidadeMaxima()), gbc);

            linha++;
        }

        // Separador
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(10, 5, 10, 5);
        painelSalgados.add(new JSeparator(), gbc);
        linha++;

        // Totais de salgados
        gbc.gridx = 0;
        gbc.gridy = linha;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        painelSalgados.add(new JLabel("Total de Salgados:"), gbc);

        gbc.gridx = 2;
        gbc.gridy = linha;
        gbc.gridwidth = 1;
        labelTotalSalgados = new JLabel("0 / " + limiteOferta);
        labelTotalSalgados.setFont(new Font("Arial", Font.BOLD, 12));
        painelSalgados.add(labelTotalSalgados, gbc);

        JScrollPane scrollSalgados = new JScrollPane(painelSalgados);
        scrollSalgados.setPreferredSize(new Dimension(500, 250));
        painelCentral.add(scrollSalgados, BorderLayout.CENTER);

        add(painelCentral, BorderLayout.CENTER);

        // Painel rodapé com botões
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));
        painelRodape.setBorder(BorderFactory.createEtchedBorder());

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        botaoAdicionar = new JButton("✅ Adicionar ao Pedido");
        botaoAdicionar.setFont(new Font("Arial", Font.BOLD, 12));
        botaoAdicionar.setBackground(new Color(0, 150, 0));
        botaoAdicionar.setForeground(Color.WHITE);
        botaoAdicionar.addActionListener(e -> adicionarItens());
        painelBotoes.add(botaoAdicionar);

        JButton botaoCancelar = new JButton("❌ Cancelar");
        botaoCancelar.setFont(new Font("Arial", Font.BOLD, 12));
        botaoCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        painelBotoes.add(botaoCancelar);

        painelRodape.add(painelBotoes, BorderLayout.CENTER);
        add(painelRodape, BorderLayout.SOUTH);
    }

    private void atualizarTotalSalgados() {
        int total = 0;
        for (JSpinner spinner : spinnersSalgados) {
            total += (int) spinner.getValue();
        }

        labelTotalSalgados.setText(total + " / " + limiteOferta);

        // Validação: se ultrapassar o limite, desabilita botão
        if (total > limiteOferta) {
            labelTotalSalgados.setForeground(Color.RED);
            botaoAdicionar.setEnabled(false);
        } else {
            labelTotalSalgados.setForeground(Color.BLACK);
            botaoAdicionar.setEnabled(true);
        }
    }

    private void adicionarItens() {
        itensSelecionados.clear();
        int totalSalgados = 0;

        // Adicionar salgados
        for (int i = 0; i < spinnersSalgados.size(); i++) {
            int quantidade = (int) spinnersSalgados.get(i).getValue();
            if (quantidade > 0) {
                Produto produto = produtosSalgados.get(i);
                Item item = new Item(produto.getNomeProduto(), quantidade, produto.getPrecoUnitario());
                itensSelecionados.add(item);
                totalSalgados += quantidade;
            }
        }

        // Validar total de salgados
        if (totalSalgados > limiteOferta) {
            JOptionPane.showMessageDialog(this, "Total de salgados excede o limite (" + limiteOferta + ")!");
            return;
        }

        if (itensSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um item!");
            return;
        }

        confirmado = true;
        dispose();
    }

    private void configurarJanela() {
        setSize(600, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public List<Item> getItensSelecionados() {
        return confirmado ? itensSelecionados : null;
    }
}
