package salgaderia.ui;

import salgaderia.dao.DadosDAO;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

public class TelaDashboard extends JPanel {

    private final JFrame parentFrame;
    private final DadosDAO dao;

    private PeriodoFiltro periodoAtual;

    private List<LancamentoFinanceiro> lancamentosPeriodo;
    private List<Pedido> pedidosPeriodo;


    private JLabel labelFaturamento;
    private JLabel labelDespesas;
    private JLabel labelLucro;
    private JLabel labelPedidosEntregues;
    private JLabel labelPedidosCancelados;


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
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.setBorder(StyleConfig.criarBorda("📅 Período"));

        JButton botaoHoje = new JButton("Hoje");
        botaoHoje.addActionListener(e -> carregarPeriodo(PeriodoFiltro.DIARIO));
        painel.add(botaoHoje);

        JButton botaoSemana = new JButton("Últimos 7 dias");
        botaoSemana.addActionListener(e -> carregarPeriodo(PeriodoFiltro.SEMANAL));
        painel.add(botaoSemana);

        JButton botaoMes = new JButton("Este mês");
        botaoMes.addActionListener(e -> carregarPeriodo(PeriodoFiltro.MENSAL));
        painel.add(botaoMes);

        JButton botaoTodos = new JButton("Todos");
        botaoTodos.addActionListener(e -> carregarTodos());
        painel.add(botaoTodos);

        return painel;
    }

    private JPanel criarPainelCards() {
        JPanel painel = new JPanel(new GridLayout(1, 5, 10, 10));
        painel.setBorder(StyleConfig.criarBorda("📊 Resumo do período"));

        painel.add(criarCartao("Faturamento", labelFaturamento = new JLabel("R$ 0,00"), StyleConfig.COR_SUCESSO));
        painel.add(criarCartao("Despesas", labelDespesas = new JLabel("R$ 0,00"), StyleConfig.COR_ERRO));
        painel.add(criarCartao("Lucro Líquido", labelLucro = new JLabel("R$ 0,00"), StyleConfig.COR_PRIMARIA));
        painel.add(criarCartao("Pedidos Entregues", labelPedidosEntregues = new JLabel("0"), StyleConfig.COR_SUCESSO));
        painel.add(criarCartao("Pedidos Cancelados", labelPedidosCancelados = new JLabel("0"), StyleConfig.COR_ERRO));

        return painel;
    }

    private JPanel criarCartao(String titulo, JLabel labelValor, Color cor) {
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



    private JPanel criarPainelGraficos() {
        JPanel painel = new JPanel(new GridLayout(1, 2, 10, 10));

        painelGraficoPizza = new JPanel(new BorderLayout());
        painelGraficoPizza.setBorder(StyleConfig.criarBorda("🥧 Despesas por categoria"));
        painel.add(painelGraficoPizza);

        painelGraficoBarras = new JPanel(new BorderLayout());
        painelGraficoBarras.setBorder(StyleConfig.criarBorda("📈 Entradas x Saídas por dia"));
        painel.add(painelGraficoBarras);

        return painel;
    }



    private void carregarPeriodo(PeriodoFiltro periodo) {
        this.periodoAtual = periodo;
        lancamentosPeriodo = dao.carregarLancamentosPorPeriodo(periodo);
        pedidosPeriodo = dao.carregarPedidosPorPeriodo(periodo);
        atualizarTudo();
    }

    private void carregarTodos() {
        this.periodoAtual = null;
        lancamentosPeriodo = dao.carregarLancamentos();
        pedidosPeriodo = dao.carregarPedidos();
        atualizarTudo();
    }


    public void atualizarDashboard() {
        if (periodoAtual != null) {
            carregarPeriodo(periodoAtual);
        } else {
            carregarTodos();
        }
    }

    private void atualizarTudo() {
        atualizarCards();
        atualizarGraficoPizza();
        atualizarGraficoBarras();
    }



    private void atualizarCards() {
        BigDecimal totalEntradas = BigDecimal.ZERO;
        BigDecimal totalSaidas = BigDecimal.ZERO;

        for (LancamentoFinanceiro l : lancamentosPeriodo) {
            if (l.getTipo() == tipoLancamento.ENTRADA) {
                totalEntradas = totalEntradas.add(l.getValor());
            } else {
                totalSaidas = totalSaidas.add(l.getValor());
            }
        }

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
}
