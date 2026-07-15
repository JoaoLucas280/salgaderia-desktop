package salgaderia.ui;


import salgaderia.model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TelaMontagemCombo extends JDialog {

    private Combo combo;
    private List<ItemPedido> itensSelecionados;
    private List<JSpinner> spinners;
    private JLabel labelTotalEscolhido;
    private JLabel labelLimite;
    private JButton botaoAdicionar;
    private boolean confirmado;

    public TelaMontagemCombo(JFrame parent, Combo combo) {
        super(parent, "Montar Combo: " + combo.getNome(), true);
        this.combo = combo;
        this.itensSelecionados = new ArrayList<>();
        this.spinners = new ArrayList<>();
        this.confirmado = false;

        initComponents();
        configurarJanela();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Painel superior: informações do combo
        JPanel painelInfo = new JPanel(new GridLayout(2, 1));
        painelInfo.setBorder(BorderFactory.createTitledBorder("Combo"));
        painelInfo.add(new JLabel("Combo: " + combo.getNome()));
        painelInfo.add(new JLabel("Preço total: R$ " + String.format("%.2f", combo.getPrecoTotal().doubleValue())));
        add(painelInfo, BorderLayout.NORTH);

        // Painel central: itens do combo
        JPanel painelItens = new JPanel(new GridLayout(0, 3, 10, 10));
        painelItens.setBorder(BorderFactory.createTitledBorder("Escolha os salgados"));

        for (ItemCombo item : combo.getItens()) {
            painelItens.add(new JLabel(item.getProduto().getNomeProduto()));
            JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, item.getQuantidadeMaxima(), 1));
            spinners.add(spinner);
            painelItens.add(spinner);
            painelItens.add(new JLabel("Máx: " + item.getQuantidadeMaxima()));
        }

        add(new JScrollPane(painelItens), BorderLayout.CENTER);

        // Painel inferior: totais e botões
        JPanel painelRodape = new JPanel(new BorderLayout(10, 10));

        JPanel painelTotais = new JPanel(new FlowLayout());
        labelTotalEscolhido = new JLabel("Total escolhido: 0");
        labelLimite = new JLabel("Limite: " + combo.getItens().stream().mapToInt(ItemCombo::getQuantidadeMaxima).sum());
        painelTotais.add(labelTotalEscolhido);
        painelTotais.add(labelLimite);
        painelRodape.add(painelTotais, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout());
        botaoAdicionar = new JButton("Adicionar ao Pedido");
        botaoAdicionar.addActionListener(e -> adicionarItens());
        painelBotoes.add(botaoAdicionar);

        JButton botaoCancelar = new JButton("Cancelar");
        botaoCancelar.addActionListener(e -> {
            confirmado = false;
            dispose();
        });
        painelBotoes.add(botaoCancelar);

        painelRodape.add(painelBotoes, BorderLayout.SOUTH);
        add(painelRodape, BorderLayout.SOUTH);

        // Atualiza total escolhido quando qualquer spinner mudar
        for (JSpinner spinner : spinners) {
            spinner.addChangeListener(e -> atualizarTotalEscolhido());
        }
    }

    private void atualizarTotalEscolhido() {
        int total = 0;
        for (JSpinner spinner : spinners) {
            total += (int) spinner.getValue();
        }
        labelTotalEscolhido.setText("Total escolhido: " + total);

        // Verifica se ultrapassou o limite
        int limite = combo.getItens().stream().mapToInt(ItemCombo::getQuantidadeMaxima).sum();
        if (total > limite) {
            labelTotalEscolhido.setForeground(Color.RED);
            botaoAdicionar.setEnabled(false);
        } else {
            labelTotalEscolhido.setForeground(Color.BLACK);
            botaoAdicionar.setEnabled(true);
        }
    }

    private void adicionarItens() {
        itensSelecionados.clear();
        int total = 0;

        for (int i = 0; i < spinners.size(); i++) {
            int quantidade = (int) spinners.get(i).getValue();
            if (quantidade > 0) {
                Produto produto = combo.getItens().get(i).getProduto();
                itensSelecionados.add(new Item(produto, quantidade));
                total += quantidade;
            }
        }

        if (itensSelecionados.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecione pelo menos um item!");
            return;
        }

        int limite = combo.getItens().stream().mapToInt(ItemCombo::getQuantidadeMaxima).sum();
        if (total > limite) {
            JOptionPane.showMessageDialog(this, "Total excede o limite do combo!");
            return;
        }

        confirmado = true;
        dispose();
    }

    private void configurarJanela() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    public List<Item> getItensSelecionados() {
        return confirmado ? itensSelecionados : null;
    }
}

