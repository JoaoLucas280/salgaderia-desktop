package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.ItemPedido;
import salgaderia.model.LancamentoFinanceiro;
import salgaderia.model.Pedido;
import salgaderia.model.enums.PeriodoFiltro;
import salgaderia.model.enums.StatusPedido;
import salgaderia.model.enums.tipoLancamento;
import salgaderia.util.StyleConfig;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class TelaDashboard extends JPanel {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int TOP_N_RANKING = 10;

    private final JFrame parentFrame;
    private final DadosDAO dao;

    private PeriodoFiltro periodoAtual;
    private boolean rangeCustomizado;

    private LocalDateTime inicioAtual;
    private LocalDateTime fimAtual;

    private List<LancamentoFinanceiro> lancamentosPeriodo;
    private List<Pedido> pedidosPeriodo;


    private JLabel labelFaturamento;
    private JLabel labelDespesas;
    private JLabel labelLucro;
    private JLabel labelPedidosEntregues;
    private JLabel labelPedidosCancelados;

    private JLabel labelComparacaoFaturamento;
    private JLabel labelComparacaoLucro;

    private JTextField campoDataInicio;
    private JTextField campoDataFim;

    private DefaultListModel<String> modelRanking;
    private JList<String> listaRanking;


    private JPanel painelGraficoPizza;
    private JPanel painelGraficoBarras;

    public TelaDashboard(JFrame parent) {
        this.parentFrame = parent;
        this.dao = DadosDAO.getInstance();

        initComponents();
        carregarPeriodo(PeriodoFiltro.MENSAL);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelTopo(), BorderLayout.NORTH);
        add(criarPainelGraficos(), BorderLayout.CENTER);
    }



    private JPanel criarPainelTopo() {
        JPanel painel = new JPanel(new BorderLayout(10, 10));
        painel.add(criarPainelFiltros(), BorderLayout.NORTH);
        painel.add(criarPainelCards(), BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(StyleConfig.criarBorda("📅 Período"));

        JPanel linhaAtalhos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        JButton botaoHoje = new JButton("Hoje");
        StyleConfig.estilizarBotaoSecundario(botaoHoje);
        botaoHoje.addActionListener(e -> carregarPeriodo(PeriodoFiltro.DIARIO));
        linhaAtalhos.add(botaoHoje);

        JButton botaoSemana = new JButton("Últimos 7 dias");
        StyleConfig.estilizarBotaoSecundario(botaoSemana);
        botaoSemana.addActionListener(e -> carregarPeriodo(PeriodoFiltro.SEMANAL));
        linhaAtalhos.add(botaoSemana);

        JButton botaoMes = new JButton("Este mês");
        StyleConfig.estilizarBotao(botaoMes);
        botaoMes.addActionListener(e -> carregarPeriodo(PeriodoFiltro.MENSAL));
        linhaAtalhos.add(botaoMes);

        JButton botaoTodos = new JButton("Todos");
        StyleConfig.estilizarBotaoSecundario(botaoTodos);
        botaoTodos.addActionListener(e -> carregarTodos());
        linhaAtalhos.add(botaoTodos);

        painel.add(linhaAtalhos);

        JPanel linhaRange = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        linhaRange.add(new JLabel("Intervalo customizado — De:"));

        campoDataInicio = new JTextField(10);
        campoDataInicio.setPreferredSize(new Dimension(100, 28));
        StyleConfig.estilizarCampo(campoDataInicio);
        linhaRange.add(campoDataInicio);

        linhaRange.add(new JLabel("Até:"));

        campoDataFim = new JTextField(10);
        campoDataFim.setPreferredSize(new Dimension(100, 28));
        StyleConfig.estilizarCampo(campoDataFim);
        linhaRange.add(campoDataFim);

        JButton botaoAplicarRange = new JButton("Aplicar");
        StyleConfig.estilizarBotao(botaoAplicarRange);
        botaoAplicarRange.addActionListener(e -> aplicarRangeCustomizado());
        linhaRange.add(botaoAplicarRange);

        painel.add(linhaRange);

        return painel;
    }

    private JPanel criarPainelCards() {
        JPanel painel = new JPanel(new GridLayout(1, 5, 10, 10));
        painel.setBorder(StyleConfig.criarBorda("📊 Resumo do período"));

        labelComparacaoFaturamento = new JLabel(" ");
        labelComparacaoLucro = new JLabel(" ");

        painel.add(criarCartao("Faturamento", labelFaturamento = new JLabel("R$ 0,00"), StyleConfig.COR_SUCESSO, labelComparacaoFaturamento));
        painel.add(criarCartao("Despesas", labelDespesas = new JLabel("R$ 0,00"), StyleConfig.COR_ERRO, null));
        painel.add(criarCartao("Lucro Líquido", labelLucro = new JLabel("R$ 0,00"), StyleConfig.COR_PRIMARIA, labelComparacaoLucro));
        painel.add(criarCartao("Pedidos Entregues", labelPedidosEntregues = new JLabel("0"), StyleConfig.COR_SUCESSO, null));
        painel.add(criarCartao("Pedidos Cancelados", labelPedidosCancelados = new JLabel("0"), StyleConfig.COR_ERRO, null));

        return painel;
    }

    private JPanel criarCartao(String titulo, JLabel labelValor, Color cor, JLabel labelComparacao) {
        JPanel cartao = new JPanel(new GridLayout(3, 1));
        cartao.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel labelTitulo = new JLabel(titulo, SwingConstants.CENTER);
        labelTitulo.setFont(new Font("Arial", Font.PLAIN, 12));
        cartao.add(labelTitulo);

        labelValor.setHorizontalAlignment(SwingConstants.CENTER);
        labelValor.setFont(StyleConfig.FONTE_SUBTITULO);
        labelValor.setForeground(cor);
        cartao.add(labelValor);

        JLabel comparacao = labelComparacao != null ? labelComparacao : new JLabel(" ");
        comparacao.setHorizontalAlignment(SwingConstants.CENTER);
        comparacao.setFont(StyleConfig.FONTE_PEQUENA);
        cartao.add(comparacao);

        return cartao;
    }



    private JPanel criarPainelGraficos() {
        JPanel painel = new JPanel(new GridLayout(1, 3, 10, 10));

        painelGraficoPizza = new JPanel(new BorderLayout());
        painelGraficoPizza.setBorder(StyleConfig.criarBorda("🥧 Despesas por categoria"));
        painel.add(painelGraficoPizza);

        painelGraficoBarras = new JPanel(new BorderLayout());
        painelGraficoBarras.setBorder(StyleConfig.criarBorda("📈 Entradas x Saídas por dia"));
        painel.add(painelGraficoBarras);

        JPanel painelRanking = new JPanel(new BorderLayout());
        painelRanking.setBorder(StyleConfig.criarBorda("🏆 Produtos mais vendidos"));
        modelRanking = new DefaultListModel<>();
        listaRanking = new JList<>(modelRanking);
        listaRanking.setFont(StyleConfig.FONTE_NORMAL);
        painelRanking.add(new JScrollPane(listaRanking), BorderLayout.CENTER);
        painel.add(painelRanking);

        return painel;
    }



    private void carregarPeriodo(PeriodoFiltro periodo) {
        this.periodoAtual = periodo;
        this.rangeCustomizado = false;

        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = switch (periodo) {
            case DIARIO -> fim.toLocalDate().atStartOfDay();
            case SEMANAL -> fim.toLocalDate().minusDays(6).atStartOfDay();
            case MENSAL -> fim.toLocalDate().withDayOfMonth(1).atStartOfDay();
        };

        this.inicioAtual = inicio;
        this.fimAtual = fim;

        recarregarDados();
    }

    private void carregarTodos() {
        this.periodoAtual = null;
        this.rangeCustomizado = false;
        this.inicioAtual = null;
        this.fimAtual = null;

        lancamentosPeriodo = dao.carregarLancamentos();
        pedidosPeriodo = dao.carregarPedidos();
        atualizarTudo();
    }

    private void aplicarRangeCustomizado() {
        LocalDate inicio;
        LocalDate fim;

        try {
            inicio = LocalDate.parse(campoDataInicio.getText().trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de início inválida! Use o formato dd/mm/aaaa.");
            return;
        }

        try {
            fim = LocalDate.parse(campoDataFim.getText().trim(), FORMATO_DATA);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data de fim inválida! Use o formato dd/mm/aaaa.");
            return;
        }

        if (inicio.isAfter(fim)) {
            JOptionPane.showMessageDialog(this, "A data de início não pode ser depois da data de fim!");
            return;
        }

        this.periodoAtual = null;
        this.rangeCustomizado = true;
        this.inicioAtual = inicio.atStartOfDay();
        this.fimAtual = fim.atTime(23, 59, 59);

        recarregarDados();
    }

    private void recarregarDados() {
        pedidosPeriodo = dao.carregarPedidosPorPeriodo(inicioAtual, fimAtual);
        lancamentosPeriodo = dao.carregarLancamentosPorPeriodo(inicioAtual.toLocalDate(), fimAtual.toLocalDate());
        atualizarTudo();
    }


    public void atualizarDashboard() {
        if (rangeCustomizado && inicioAtual != null && fimAtual != null) {
            recarregarDados();
        } else if (periodoAtual != null) {
            carregarPeriodo(periodoAtual);
        } else {
            carregarTodos();
        }
    }

    private void atualizarTudo() {
        atualizarCards();
        atualizarComparacaoComPeriodoAnterior();
        atualizarGraficoPizza();
        atualizarGraficoBarras();
        atualizarRanking();
    }



    private void atualizarCards() {
        BigDecimal totalEntradas = somarPorTipo(lancamentosPeriodo, tipoLancamento.ENTRADA);
        BigDecimal totalSaidas = somarPorTipo(lancamentosPeriodo, tipoLancamento.SAIDA);
        BigDecimal lucro = totalEntradas.subtract(totalSaidas);

        long entregues = pedidosPeriodo.stream().filter(p -> p.getStatus() == StatusPedido.ENTREGUE).count();
        long cancelados = pedidosPeriodo.stream().filter(p -> p.getStatus() == StatusPedido.CANCELADO).count();

        labelFaturamento.setText("R$ " + String.format("%.2f", totalEntradas.doubleValue()));
        labelDespesas.setText("R$ " + String.format("%.2f", totalSaidas.doubleValue()));
        labelLucro.setText("R$ " + String.format("%.2f", lucro.doubleValue()));
        labelLucro.setForeground(lucro.compareTo(BigDecimal.ZERO) < 0 ? StyleConfig.COR_ERRO : StyleConfig.COR_PRIMARIA);
        labelPedidosEntregues.setText(String.valueOf(entregues));
        labelPedidosCancelados.setText(String.valueOf(cancelados));
    }

    private BigDecimal somarPorTipo(List<LancamentoFinanceiro> lancamentos, tipoLancamento tipo) {
        BigDecimal total = BigDecimal.ZERO;
        for (LancamentoFinanceiro l : lancamentos) {
            if (l.getTipo() == tipo) {
                total = total.add(l.getValor());
            }
        }
        return total;
    }


    private void atualizarComparacaoComPeriodoAnterior() {
        if (inicioAtual == null || fimAtual == null) {
            labelComparacaoFaturamento.setText(" ");
            labelComparacaoLucro.setText(" ");
            return;
        }

        Duration duracao = Duration.between(inicioAtual, fimAtual);
        LocalDateTime fimAnterior = inicioAtual.minusSeconds(1);
        LocalDateTime inicioAnterior = fimAnterior.minus(duracao);

        List<LancamentoFinanceiro> lancamentosAnteriores =
                dao.carregarLancamentosPorPeriodo(inicioAnterior.toLocalDate(), fimAnterior.toLocalDate());

        BigDecimal faturamentoAtual = somarPorTipo(lancamentosPeriodo, tipoLancamento.ENTRADA);
        BigDecimal lucroAtual = faturamentoAtual.subtract(somarPorTipo(lancamentosPeriodo, tipoLancamento.SAIDA));

        BigDecimal faturamentoAnterior = somarPorTipo(lancamentosAnteriores, tipoLancamento.ENTRADA);
        BigDecimal lucroAnterior = faturamentoAnterior.subtract(somarPorTipo(lancamentosAnteriores, tipoLancamento.SAIDA));

        aplicarComparacao(labelComparacaoFaturamento, faturamentoAtual, faturamentoAnterior);
        aplicarComparacao(labelComparacaoLucro, lucroAtual, lucroAnterior);
    }

    private void aplicarComparacao(JLabel label, BigDecimal atual, BigDecimal anterior) {
        if (anterior.compareTo(BigDecimal.ZERO) == 0) {
            if (atual.compareTo(BigDecimal.ZERO) == 0) {
                label.setText(" ");
            } else {
                label.setText("🆕 sem dados no período anterior");
                label.setForeground(StyleConfig.COR_SECUNDARIA);
            }
            return;
        }

        BigDecimal variacao = atual.subtract(anterior)
                .divide(anterior, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        boolean subiu = variacao.compareTo(BigDecimal.ZERO) >= 0;
        String seta = subiu ? "↑" : "↓";
        Color cor = subiu ? StyleConfig.COR_SUCESSO : StyleConfig.COR_ERRO;

        label.setText(seta + " " + String.format("%.1f", variacao.abs().doubleValue()) + "% vs período anterior");
        label.setForeground(cor);
    }



    private void atualizarGraficoPizza() {
        painelGraficoPizza.removeAll();
        painelGraficoPizza.add(new ChartPanel(criarGraficoPizzaDespesas()), BorderLayout.CENTER);
        painelGraficoPizza.revalidate();
        painelGraficoPizza.repaint();
    }

    private JFreeChart criarGraficoPizzaDespesas() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        Map<String, BigDecimal> totalPorCategoria = new LinkedHashMap<>();
        for (LancamentoFinanceiro l : lancamentosPeriodo) {
            if (l.getTipo() == tipoLancamento.SAIDA) {
                totalPorCategoria.merge(l.getCategoria(), l.getValor(), BigDecimal::add);
            }
        }

        for (Map.Entry<String, BigDecimal> entry : totalPorCategoria.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }

        return ChartFactory.createPieChart(
                null,
                dataset,
                true,
                true,
                false
        );
    }



    private void atualizarGraficoBarras() {
        painelGraficoBarras.removeAll();
        painelGraficoBarras.add(new ChartPanel(criarGraficoEvolucaoDiaria()), BorderLayout.CENTER);
        painelGraficoBarras.revalidate();
        painelGraficoBarras.repaint();
    }

    private JFreeChart criarGraficoEvolucaoDiaria() {
        Map<LocalDate, BigDecimal> entradasPorDia = new TreeMap<>();
        Map<LocalDate, BigDecimal> saidasPorDia = new TreeMap<>();

        for (LancamentoFinanceiro l : lancamentosPeriodo) {
            if (l.getTipo() == tipoLancamento.ENTRADA) {
                entradasPorDia.merge(l.getData(), l.getValor(), BigDecimal::add);
            } else {
                saidasPorDia.merge(l.getData(), l.getValor(), BigDecimal::add);
            }
        }

        TreeSet<LocalDate> todasDatas = new TreeSet<>();
        todasDatas.addAll(entradasPorDia.keySet());
        todasDatas.addAll(saidasPorDia.keySet());

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        DateTimeFormatter formatoCurto = DateTimeFormatter.ofPattern("dd/MM");

        for (LocalDate data : todasDatas) {
            String rotulo = data.format(formatoCurto);
            dataset.addValue(entradasPorDia.getOrDefault(data, BigDecimal.ZERO), "Entradas", rotulo);
            dataset.addValue(saidasPorDia.getOrDefault(data, BigDecimal.ZERO), "Saídas", rotulo);
        }

        JFreeChart chart = ChartFactory.createBarChart(
                null,
                "Data",
                "R$",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, StyleConfig.COR_SUCESSO);
        renderer.setSeriesPaint(1, StyleConfig.COR_ERRO);

        return chart;
    }


    private void atualizarRanking() {
        Map<String, Integer> quantidadePorProduto = new LinkedHashMap<>();

        for (Pedido p : pedidosPeriodo) {
            if (p.getStatus() == StatusPedido.CANCELADO || p.getItens() == null) {
                continue;
            }
            for (ItemPedido item : p.getItens()) {
                quantidadePorProduto.merge(item.getNomeProduto(), item.getQuantidade(), Integer::sum);
            }
        }

        List<Map.Entry<String, Integer>> ranking = quantidadePorProduto.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(TOP_N_RANKING)
                .toList();

        modelRanking.clear();
        if (ranking.isEmpty()) {
            modelRanking.addElement("Nenhuma venda no período.");
            return;
        }

        int posicao = 1;
        for (Map.Entry<String, Integer> entry : ranking) {
            modelRanking.addElement(posicao + "º — " + entry.getKey() + " (" + entry.getValue() + " un.)");
            posicao++;
        }
    }
}
