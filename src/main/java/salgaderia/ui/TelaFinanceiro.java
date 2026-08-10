package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.LancamentoFinanceiro;
import salgaderia.model.enums.PeriodoFiltro;
import salgaderia.model.enums.tipoLancamento;
import salgaderia.ui.dialogs.DialogLancamento;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TelaFinanceiro extends JPanel {

    private final JFrame parentFrame;
    private final DadosDAO dao;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<LancamentoFinanceiro> lancamentosExibidos;

    private JButton botaoHoje;
    private JButton botaoSemana;
    private JButton botaoMes;
    private JButton botaoTodos;
    private PeriodoFiltro periodoAtual;

    private JLabel labelEntradas;
    private JLabel labelSaidas;
    private JLabel labelSaldo;

    private DefaultTableModel modeloResumo;
    private JTable tabelaResumo;

    private DefaultTableModel modeloLancamentos;
    private JTable tabelaLancamentos;

    public TelaFinanceiro(JFrame parent) {
        this.parentFrame = parent;
        this.dao = DadosDAO.getInstance();
        this.lancamentosExibidos = new ArrayList<>();

        initComponents();
        carregarPorPeriodo(PeriodoFiltro.MENSAL);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelTopo(), BorderLayout.NORTH);
        add(criarPainelCentral(), BorderLayout.CENTER);
        add(criarPainelAcoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));

        painel.add(criarPainelFiltros(), BorderLayout.NORTH);
        painel.add(criarPainelSaldo(), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.setBorder(StyleConfig.criarBorda("📅 Filtrar por período"));

        botaoHoje = new JButton("Hoje");
        botaoHoje.addActionListener(e -> carregarPorPeriodo(PeriodoFiltro.DIARIO));
        painel.add(botaoHoje);

        botaoSemana = new JButton("Últimos 7 dias");
        botaoSemana.addActionListener(e -> carregarPorPeriodo(PeriodoFiltro.SEMANAL));
        painel.add(botaoSemana);

        botaoMes = new JButton("Este mês");
        botaoMes.addActionListener(e -> carregarPorPeriodo(PeriodoFiltro.MENSAL));
        painel.add(botaoMes);

        botaoTodos = new JButton("Todos");
        botaoTodos.addActionListener(e -> carregarTodos());
        painel.add(botaoTodos);

        return painel;
    }

    private JPanel criarPainelSaldo() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 10, 10));
        painel.setBorder(StyleConfig.criarBorda("💰 Resumo do período"));

        painel.add(criarCartaoSaldo("Entradas", labelEntradas = new JLabel("R$ 0,00"), StyleConfig.COR_SUCESSO));
        painel.add(criarCartaoSaldo("Saídas", labelSaidas = new JLabel("R$ 0,00"), StyleConfig.COR_ERRO));
        painel.add(criarCartaoSaldo("Saldo", labelSaldo = new JLabel("R$ 0,00"), StyleConfig.COR_PRIMARIA));

        return painel;
    }

    private JPanel criarCartaoSaldo(String titulo, JLabel labelValor, Color cor) {
        JPanel cartao = new JPanel(new GridLayout(2, 1));
        cartao.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        cartao.add(labelTitulo);

        labelValor.setHorizontalAlignment(SwingConstants.CENTER);
        labelValor.setFont(StyleConfig.FONTE_SUBTITULO);
        labelValor.setForeground(cor);
        cartao.add(labelValor);

        return cartao;
    }

    private JPanel criarPainelCentral() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 10, 10));

        painel.add(criarPainelResumoCategoria());
        painel.add(criarPainelListaLancamentos());

        return painel;
    }

    private JPanel criarPainelResumoCategoria() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(StyleConfig.criarBorda("📊 Por categoria"));

        String[] colunas = {"Categoria", "Tipo", "Total"};
        modeloResumo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaResumo = new JTable(modeloResumo);
        painel.add(new JScrollPane(tabelaResumo), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelListaLancamentos() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(StyleConfig.criarBorda("📋 Lançamentos"));

        String[] colunas = {"Data", "Tipo", "Categoria", "Descrição", "Valor", "Pagamento"};
        modeloLancamentos = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaLancamentos = new JTable(modeloLancamentos);
        tabelaLancamentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        painel.add(new JScrollPane(tabelaLancamentos), BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton botaoNovaEntrada = new JButton("➕ Nova Entrada");
        StyleConfig.estilizarBotao(botaoNovaEntrada, StyleConfig.COR_SUCESSO);
        botaoNovaEntrada.addActionListener(e -> abrirDialogLancamento(null, tipoLancamento.ENTRADA));
        painel.add(botaoNovaEntrada);

        JButton botaoNovaSaida = new JButton("➖ Nova Saída");
        StyleConfig.estilizarBotao(botaoNovaSaida, StyleConfig.COR_ERRO);
        botaoNovaSaida.addActionListener(e -> abrirDialogLancamento(null, tipoLancamento.SAIDA));
        painel.add(botaoNovaSaida);

        JButton botaoEditar = new JButton("✏️ Editar Selecionado");
        botaoEditar.addActionListener(e -> editarSelecionado());
        painel.add(botaoEditar);

        JButton botaoRemover = new JButton("🗑️ Remover Selecionado");
        botaoRemover.addActionListener(e -> removerSelecionado());
        painel.add(botaoRemover);

        return painel;
    }

    private void carregarPorPeriodo(PeriodoFiltro periodo) {
        this.periodoAtual = periodo;
        lancamentosExibidos = dao.carregarLancamentosPorPeriodo(periodo);
        atualizarExibicao();
    }

    private void carregarTodos() {
        this.periodoAtual = null;
        lancamentosExibidos = dao.carregarLancamentos();
        atualizarExibicao();
    }

    public void atualizarLista() {
        if (periodoAtual != null) {
            carregarPorPeriodo(periodoAtual);
        } else {
            carregarTodos();
        }
    }

    private void atualizarExibicao() {
        preencherTabelaLancamentos();
        atualizarSaldo();
        atualizarResumoPorCategoria();
    }

    private void preencherTabelaLancamentos() {
        modeloLancamentos.setRowCount(0);

        for (LancamentoFinanceiro l : lancamentosExibidos) {
            String tipoTexto = l.getTipo() == tipoLancamento.ENTRADA ? "🟢 Entrada" : "🔴 Saída";
            modeloLancamentos.addRow(new Object[]{
                    l.getData().format(FORMATO_DATA),
                    tipoTexto,
                    l.getCategoria(),
                    l.getDescricao(),
                    "R$ " + String.format("%.2f", l.getValor().doubleValue()),
                    l.getFormaPagamento() != null ? l.getFormaPagamento() : "-"
            });
        }
    }

    private void atualizarSaldo() {
        BigDecimal totalEntradas = BigDecimal.ZERO;
        BigDecimal totalSaidas = BigDecimal.ZERO;

        for (LancamentoFinanceiro l : lancamentosExibidos) {
            if (l.getTipo() == tipoLancamento.ENTRADA) {
                totalEntradas = totalEntradas.add(l.getValor());
            } else {
                totalSaidas = totalSaidas.add(l.getValor());
            }
        }

        BigDecimal saldo = totalEntradas.subtract(totalSaidas);

        labelEntradas.setText("R$ " + String.format("%.2f", totalEntradas.doubleValue()));
        labelSaidas.setText("R$ " + String.format("%.2f", totalSaidas.doubleValue()));
        labelSaldo.setText("R$ " + String.format("%.2f", saldo.doubleValue()));
        labelSaldo.setForeground(saldo.compareTo(BigDecimal.ZERO) < 0 ? StyleConfig.COR_ERRO : StyleConfig.COR_PRIMARIA);
    }

    private void atualizarResumoPorCategoria() {
        modeloResumo.setRowCount(0);

        Map<String, BigDecimal> totais = new LinkedHashMap<>();

        for (LancamentoFinanceiro l : lancamentosExibidos) {
            String chave = l.getTipo() + "|" + l.getCategoria();
            totais.merge(chave, l.getValor(), BigDecimal::add);
        }

        for (Map.Entry<String, BigDecimal> entry : totais.entrySet()) {
            String[] partes = entry.getKey().split("\\|", 2);
            String tipoTexto = tipoLancamento.valueOf(partes[0]) == tipoLancamento.ENTRADA ? "🟢 Entrada" : "🔴 Saída";
            String categoria = partes[1];

            modeloResumo.addRow(new Object[]{
                    categoria,
                    tipoTexto,
                    "R$ " + String.format("%.2f", entry.getValue().doubleValue())
            });
        }
    }

    private void abrirDialogLancamento(LancamentoFinanceiro existente, tipoLancamento tipoPreSelecionado) {
        DialogLancamento dialog = new DialogLancamento(parentFrame, existente);
        dialog.setVisible(true);

        LancamentoFinanceiro lancamento = dialog.getLancamento();
        if (lancamento == null) {
            return;
        }

        if (existente == null) {
            dao.salvarLancamento(lancamento);
        } else {
            dao.atualizarLancamento(lancamento);
        }

        atualizarLista();
    }

    private void editarSelecionado() {
        int linha = tabelaLancamentos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um lançamento para editar!");
            return;
        }

        LancamentoFinanceiro existente = lancamentosExibidos.get(linha);
        abrirDialogLancamento(existente, existente.getTipo());
    }

    private void removerSelecionado() {
        int linha = tabelaLancamentos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um lançamento para remover!");
            return;
        }

        LancamentoFinanceiro l = lancamentosExibidos.get(linha);

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Remover o lançamento \"" + l.getDescricao() + "\" (R$ " + String.format("%.2f", l.getValor()) + ")?",
                "Confirmar remoção",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao == JOptionPane.YES_OPTION) {
            dao.deletarLancamento(l.getId());
            atualizarLista();
        }
    }
}
