package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.LancamentoFinanceiro;
import salgaderia.model.Pedido;
import salgaderia.model.enums.PeriodoFiltro;
import salgaderia.model.enums.StatusPedido;
import salgaderia.service.ReciboService;
import salgaderia.util.StyleConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TelaHistoricoPedidos extends JPanel {

    private final JFrame parentFrame;
    private final DadosDAO dao;

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String[] OPCOES_STATUS_FILTRO = {"Todos", "🕒 Pendente", "✅ Entregue", "❌ Cancelado"};


    private List<Pedido> pedidosCarregados;
    private List<Pedido> pedidosExibidos;

    private PeriodoFiltro periodoAtual;

    private DefaultTableModel modeloTabela;
    private JTable tabelaPedidos;

    private JButton botaoHoje;
    private JButton botaoSemana;
    private JButton botaoMes;
    private JButton botaoTodos;
    private JComboBox<String> comboStatusFiltro;

    private JButton botaoMarcarEntregue;
    private JButton botaoCancelarPedido;
    private JButton botaoReabrirPendente;
    private JButton botaoGerarNota;

    private JLabel labelResultado;

    public TelaHistoricoPedidos(JFrame parent) {
        this.parentFrame = parent;
        this.dao = DadosDAO.getInstance();
        this.pedidosCarregados = new ArrayList<>();
        this.pedidosExibidos = new ArrayList<>();

        initComponents();
        carregarPedidos(PeriodoFiltro.DIARIO);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelFiltros(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        add(criarPainelAcoes(), BorderLayout.SOUTH);
    }

    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        painel.setBorder(StyleConfig.criarBorda("📅 Filtrar"));

        botaoHoje = new JButton("Hoje");
        botaoHoje.addActionListener(e -> carregarPedidos(PeriodoFiltro.DIARIO));
        painel.add(botaoHoje);

        botaoSemana = new JButton("Últimos 7 dias");
        botaoSemana.addActionListener(e -> carregarPedidos(PeriodoFiltro.SEMANAL));
        painel.add(botaoSemana);

        botaoMes = new JButton("Este mês");
        botaoMes.addActionListener(e -> carregarPedidos(PeriodoFiltro.MENSAL));
        painel.add(botaoMes);

        botaoTodos = new JButton("Todos");
        botaoTodos.addActionListener(e -> carregarTodosPedidos());
        painel.add(botaoTodos);

        painel.add(new JLabel("   |   Status:"));
        comboStatusFiltro = new JComboBox<>(OPCOES_STATUS_FILTRO);
        comboStatusFiltro.addActionListener(e -> aplicarFiltroStatus());
        painel.add(comboStatusFiltro);

        labelResultado = new JLabel();
        labelResultado.setFont(new Font("Arial", Font.BOLD, 12));
        labelResultado.setForeground(StyleConfig.COR_PRIMARIA);
        painel.add(labelResultado);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());

        String[] colunas = {"ID", "Cliente", "Telefone", "Data", "Total", "Status"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaPedidos = new JTable(modeloTabela);
        tabelaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaPedidos.setRowHeight(24);

        JScrollPane scroll = new JScrollPane(tabelaPedidos);
        painel.add(scroll, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        botaoMarcarEntregue = new JButton("✅ Marcar como Entregue");
        StyleConfig.estilizarBotao(botaoMarcarEntregue, StyleConfig.COR_SUCESSO);
        botaoMarcarEntregue.addActionListener(e -> alterarStatusSelecionado(StatusPedido.ENTREGUE));
        painel.add(botaoMarcarEntregue);

        botaoCancelarPedido = new JButton("❌ Cancelar Pedido");
        StyleConfig.estilizarBotao(botaoCancelarPedido, StyleConfig.COR_ERRO);
        botaoCancelarPedido.addActionListener(e -> cancelarPedidoSelecionado());
        painel.add(botaoCancelarPedido);

        botaoReabrirPendente = new JButton("🕒 Reabrir como Pendente");
        botaoReabrirPendente.addActionListener(e -> alterarStatusSelecionado(StatusPedido.PENDENTE));
        painel.add(botaoReabrirPendente);

        botaoGerarNota = new JButton("📄 Gerar Nota");
        botaoGerarNota.addActionListener(e -> gerarNotaDoSelecionado());
        painel.add(botaoGerarNota);

        return painel;
    }


    private void carregarPedidos(PeriodoFiltro periodo) {
        this.periodoAtual = periodo;
        pedidosCarregados = dao.carregarPedidosPorPeriodo(periodo);
        aplicarFiltroStatus();
    }

    private void carregarTodosPedidos() {
        this.periodoAtual = null;
        pedidosCarregados = dao.carregarPedidos();
        aplicarFiltroStatus();
    }

    private void aplicarFiltroStatus() {
        int indiceSelecionado = comboStatusFiltro.getSelectedIndex();

        if (indiceSelecionado <= 0) {
            pedidosExibidos = new ArrayList<>(pedidosCarregados);
        } else {
            StatusPedido statusAlvo = switch (indiceSelecionado) {
                case 1 -> StatusPedido.PENDENTE;
                case 2 -> StatusPedido.ENTREGUE;
                case 3 -> StatusPedido.CANCELADO;
                default -> null;
            };

            pedidosExibidos = pedidosCarregados.stream()
                    .filter(p -> p.getStatus() == statusAlvo)
                    .toList();
        }

        preencherTabela();
    }

    private void preencherTabela() {
        modeloTabela.setRowCount(0);

        for (Pedido p : pedidosExibidos) {
            modeloTabela.addRow(new Object[]{
                    p.getId(),
                    p.getNomeCliente(),
                    p.getTelefone(),
                    p.getDataHora().format(FORMATO_DATA),
                    "R$ " + String.format("%.2f", p.getTotal().doubleValue()),
                    formatarStatus(p.getStatus())
            });
        }

        labelResultado.setText(pedidosExibidos.size() + " pedido(s) encontrado(s)");
    }

    private String formatarStatus(StatusPedido status) {
        if (status == null) {
            return "🕒 Pendente";
        }
        return switch (status) {
            case PENDENTE -> "🕒 Pendente";
            case ENTREGUE -> "✅ Entregue";
            case CANCELADO -> "❌ Cancelado";
        };
    }


    private void alterarStatusSelecionado(StatusPedido novoStatus) {
        Pedido pedido = pegarPedidoSelecionado();
        if (pedido == null) {
            return;
        }

        dao.atualizarStatusPedido(pedido.getId(), novoStatus);

        if (periodoAtual != null) {
            carregarPedidos(periodoAtual);
        } else {
            carregarTodosPedidos();
        }
    }

    private void cancelarPedidoSelecionado() {
        Pedido pedido = pegarPedidoSelecionado();
        if (pedido == null) {
            return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(
                this,
                "Cancelar o pedido #" + pedido.getId() + " (" + pedido.getNomeCliente() + ")?",
                "Confirmar cancelamento",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmacao != JOptionPane.YES_OPTION) {
            return;
        }

        dao.atualizarStatusPedido(pedido.getId(), StatusPedido.CANCELADO);

        List<LancamentoFinanceiro> lancamentosVinculados = dao.carregarLancamentosPorPedidoId(pedido.getId());
        if (!lancamentosVinculados.isEmpty()) {
            int opcaoRemover = JOptionPane.showConfirmDialog(
                    this,
                    "Este pedido tem " + lancamentosVinculados.size() + " lançamento(s) financeiro(s) vinculado(s).\n" +
                            "Deseja remover essa(s) entrada(s) também, já que o pedido foi cancelado?",
                    "Remover lançamento financeiro?",
                    JOptionPane.YES_NO_OPTION
            );

            if (opcaoRemover == JOptionPane.YES_OPTION) {
                for (LancamentoFinanceiro l : lancamentosVinculados) {
                    dao.deletarLancamento(l.getId());
                }
            }
        }

        if (periodoAtual != null) {
            carregarPedidos(periodoAtual);
        } else {
            carregarTodosPedidos();
        }
    }

    private Pedido pegarPedidoSelecionado() {
        int linha = tabelaPedidos.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na lista!");
            return null;
        }
        return pedidosExibidos.get(linha);
    }

    private void gerarNotaDoSelecionado() {
        Pedido pedido = pegarPedidoSelecionado();
        if (pedido == null) {
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Salvar nota do pedido");
        fileChooser.setSelectedFile(new File("nota_pedido_" + pedido.getId() + ".xlsx"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Planilha Excel (*.xlsx)", "xlsx"));

        int resultado = fileChooser.showSaveDialog(this);
        if (resultado != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File arquivo = fileChooser.getSelectedFile();
        if (!arquivo.getName().toLowerCase().endsWith(".xlsx")) {
            arquivo = new File(arquivo.getParentFile(), arquivo.getName() + ".xlsx");
        }

        try {
            new ReciboService().gerarRecibo(pedido, arquivo);
            JOptionPane.showMessageDialog(this, "✅ Nota salva em:\n" + arquivo.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "❌ Erro ao gerar nota: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizarLista() {
        if (periodoAtual != null) {
            carregarPedidos(periodoAtual);
        } else {
            carregarTodosPedidos();
        }
    }
}