package salgaderia.service;

import salgaderia.model.ItemPedido;
import salgaderia.model.Pedido;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReciboService {

    private static final DateTimeFormatter FORMATO_DIA_MES = DateTimeFormatter.ofPattern("dd/MM");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    public void gerarRecibo(Pedido pedido, File arquivoDestino) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Nota");

            CellStyle estiloTitulo = criarEstiloTitulo(workbook);
            CellStyle estiloRotulo = criarEstiloRotulo(workbook);
            CellStyle estiloValor = criarEstiloValor(workbook);
            CellStyle estiloCabecalhoTabela = criarEstiloCabecalhoTabela(workbook);
            CellStyle estiloTotal = criarEstiloTotal(workbook);

            int linha = 0;
            linha = escreverTitulo(sheet, linha, pedido, estiloTitulo);
            linha = escreverDadosCliente(sheet, linha, pedido, estiloRotulo, estiloValor);
            linha++;
            linha = escreverItensComPrecoFechado(sheet, linha, pedido, estiloRotulo, estiloValor);
            linha = escreverDataHorario(sheet, linha, pedido, estiloRotulo, estiloValor);
            linha++;
            linha = escreverTabelaSabores(sheet, linha, pedido, estiloCabecalhoTabela, estiloValor, estiloRotulo);
            linha++;
            escreverTotais(sheet, linha, pedido, estiloRotulo, estiloValor, estiloTotal);

            sheet.setColumnWidth(0, 18 * 256);
            sheet.setColumnWidth(1, 24 * 256);

            configurarImpressao(sheet);

            try (FileOutputStream fos = new FileOutputStream(arquivoDestino)) {
                workbook.write(fos);
            }
        }
    }

    private int escreverTitulo(Sheet sheet, int linha, Pedido pedido, CellStyle estilo) {
        Row row = sheet.createRow(linha);
        Cell cell = row.createCell(0);
        cell.setCellValue("Salgaderia - Pedido #" + pedido.getId());
        cell.setCellStyle(estilo);
        sheet.addMergedRegion(new CellRangeAddress(linha, linha, 0, 1));
        return linha + 2;
    }

    private int escreverDadosCliente(Sheet sheet, int linha, Pedido pedido, CellStyle estiloRotulo, CellStyle estiloValor) {
        linha = escreverCampo(sheet, linha, "NOME", pedido.getNomeCliente(), estiloRotulo, estiloValor);
        linha = escreverCampo(sheet, linha, "TEL", pedido.getTelefone(), estiloRotulo, estiloValor);

        String endereco = pedido.getEndereco() != null && !pedido.getEndereco().isBlank() ? pedido.getEndereco() : "-";
        linha = escreverCampo(sheet, linha, "ENDEREÇO", endereco, estiloRotulo, estiloValor);

        return linha;
    }

    private int escreverItensComPrecoFechado(Sheet sheet, int linha, Pedido pedido, CellStyle estiloRotulo, CellStyle estiloValor) {
        if (pedido.getItens() == null) {
            return linha;
        }

        for (ItemPedido item : pedido.getItens()) {
            if (item.getPrecoUnitario() != null && item.getPrecoUnitario().compareTo(BigDecimal.ZERO) > 0) {
                String valor = item.getQuantidade() > 1
                        ? item.getQuantidade() + "x - " + formatarMoeda(item.getPrecoUnitario())
                        : formatarMoeda(item.getPrecoUnitario());
                linha = escreverCampo(sheet, linha, item.getNomeProduto().toUpperCase(), valor, estiloRotulo, estiloValor);
            }
        }

        return linha;
    }

    private int escreverDataHorario(Sheet sheet, int linha, Pedido pedido, CellStyle estiloRotulo, CellStyle estiloValor) {
        String dataFormatada = abreviarDiaSemana(pedido.getDataHora().getDayOfWeek()) + " - " + pedido.getDataHora().format(FORMATO_DIA_MES);
        linha = escreverCampo(sheet, linha, "DATA", dataFormatada, estiloRotulo, estiloValor);

        String horarioFormatado = pedido.getDataHora().format(FORMATO_HORA) + "H";
        linha = escreverCampo(sheet, linha, "HORÁRIO", horarioFormatado, estiloRotulo, estiloValor);

        return linha;
    }

    private int escreverTabelaSabores(Sheet sheet, int linha, Pedido pedido, CellStyle estiloCabecalho, CellStyle estiloValor, CellStyle estiloRotulo) {
        List<ItemPedido> sabores = new ArrayList<>();
        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                if (item.getPrecoUnitario() == null || item.getPrecoUnitario().compareTo(BigDecimal.ZERO) == 0) {
                    sabores.add(item);
                }
            }
        }

        if (sabores.isEmpty()) {
            return linha;
        }

        Row header = sheet.createRow(linha++);
        criarCelula(header, 0, "SABORES", estiloCabecalho);
        criarCelula(header, 1, "QTD", estiloCabecalho);

        int qtdTotal = 0;
        for (ItemPedido item : sabores) {
            Row row = sheet.createRow(linha++);
            criarCelula(row, 0, item.getNomeProduto().toUpperCase(), estiloValor);
            criarCelula(row, 1, String.valueOf(item.getQuantidade()), estiloValor);
            qtdTotal += item.getQuantidade();
        }

        linha = escreverCampo(sheet, linha, "QTD-TOTAL", String.valueOf(qtdTotal), estiloRotulo, estiloValor);

        return linha;
    }

    private void escreverTotais(Sheet sheet, int linha, Pedido pedido, CellStyle estiloRotulo, CellStyle estiloValor, CellStyle estiloTotal) {
        BigDecimal taxa = pedido.getTaxaEntrega() != null ? pedido.getTaxaEntrega() : BigDecimal.ZERO;
        linha = escreverCampo(sheet, linha, "TAXA R$", formatarMoeda(taxa), estiloRotulo, estiloValor);

        Row rowTotal = sheet.createRow(linha++);
        criarCelula(rowTotal, 0, "TOTAL R$", estiloTotal);
        criarCelula(rowTotal, 1, formatarMoeda(pedido.getTotal()), estiloTotal);

        String pagamento = pedido.getFormaPagamento() != null && !pedido.getFormaPagamento().isBlank()
                ? pedido.getFormaPagamento()
                : "-";
        escreverCampo(sheet, linha, "PAGAMENTO", pagamento, estiloRotulo, estiloValor);
    }

    private int escreverCampo(Sheet sheet, int linha, String rotulo, String valor, CellStyle estiloRotulo, CellStyle estiloValor) {
        Row row = sheet.createRow(linha);
        criarCelula(row, 0, rotulo, estiloRotulo);
        criarCelula(row, 1, valor, estiloValor);
        return linha + 1;
    }

    private void criarCelula(Row row, int coluna, String valor, CellStyle estilo) {
        Cell cell = row.createCell(coluna);
        cell.setCellValue(valor);
        cell.setCellStyle(estilo);
    }

    private String formatarMoeda(BigDecimal valor) {
        return "R$ " + String.format("%.2f", valor.doubleValue());
    }

    private String abreviarDiaSemana(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY -> "SEG";
            case TUESDAY -> "TER";
            case WEDNESDAY -> "QUA";
            case THURSDAY -> "QUI";
            case FRIDAY -> "SEX";
            case SATURDAY -> "SAB";
            case SUNDAY -> "DOM";
        };
    }

    private void configurarImpressao(Sheet sheet) {
        sheet.setFitToPage(true);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setFitWidth((short) 1);
        printSetup.setFitHeight((short) 0);
        printSetup.setLandscape(false);
    }

    // ===== ESTILOS =====

    private CellStyle criarEstiloTitulo(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle criarEstiloRotulo(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        aplicarBordas(style);
        return style;
    }

    private CellStyle criarEstiloValor(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        aplicarBordas(style);
        return style;
    }

    private CellStyle criarEstiloCabecalhoTabela(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        aplicarBordas(style);
        return style;
    }

    private CellStyle criarEstiloTotal(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        aplicarBordas(style);
        return style;
    }

    private void aplicarBordas(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
