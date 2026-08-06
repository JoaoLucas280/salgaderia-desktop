package salgaderia.ui;

import salgaderia.dao.DadosDAO;
import salgaderia.model.Pedido;
import salgaderia.model.enums.PeriodoFiltro;
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

    private DefaultTableModel modeloTabela;
    private JTable tabelaPedidos;
    private List<Pedido> pedidosExibidos;

    private JButton botaoHoje;
    private JButton botaoSemana;
    private JButton botaoMes;
    private JButton botaoTodos;
    private JButton botaoGerarNota;

    private JLabel labelResultado;

    public TelaHistoricoPedidos(JFrame parent) {
        this.parentFrame = parent;
        this.dao = DadosDAO.getInstance();
        this.pedidosExibidos = new ArrayList<>();

        initComponents();
        carregarPedidos(PeriodoFiltro.DIARIO); // filtro inicial: hoje
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
        painel.setBorder(StyleConfig.criarBorda("📅 Filtrar por período"));

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

        labelResultado = new JLabel();
        labelResultado.setFont(new Font("Arial", Font.BOLD, 12));
        labelResultado.setForeground(StyleConfig.COR_PRIMARIA);
        painel.add(labelResultado);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());

        String[] colunas = {"ID", "Cliente", "Telefone", "Data", "Total"};
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

        botaoGerarNota = new JButton("📄 Gerar Nota do Pedido Selecionado");
        StyleConfig.estilizarBotao(botaoGerarNota, StyleConfig.COR_SUCESSO);
        botaoGerarNota.addActionListener(e -> gerarNotaDoSelecionado());
        painel.add(botaoGerarNota);

        return painel;
    }
    private void carregarPedidos(PeriodoFiltro periodo) {
        pedidosExibidos = dao.carregarPedidosPorPeriodo(periodo);
        preencherTabela();
    }

    private void carregarTodosPedidos() {
        pedidosExibidos = dao.carregarPedidos();
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
                    "R$ " + String.format("%.2f", p.getTotal().doubleValue())
            });
        }

        labelResultado.setText(pedidosExibidos.size() + " pedido(s) encontrado(s)");
    }

    private void gerarNotaDoSelecionado() {
        int linhaSelecionada = tabelaPedidos.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um pedido na lista!");
            return;
        }

        Pedido pedido = pedidosExibidos.get(linhaSelecionada);

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
        carregarTodosPedidos();
    }
}
