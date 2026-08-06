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
import java.time.format.DateTimeFormatter;


public class ReciboService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public void gerarRecibo(Pedido pedido, File arquivoDestino) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Nota");

            CellStyle estiloTitulo = criarEstiloTitulo(workbook);
            CellStyle estiloSubtitulo = criarEstiloSubtitulo(workbook);
            CellStyle estiloRotulo = criarEstiloRotulo(workbook);
            CellStyle estiloCabecalhoTabela = criarEstiloCabecalhoTabela(workbook);
            CellStyle estiloCelula = criarEstiloCelula(workbook);
            CellStyle estiloTotal = criarEstiloTotal(workbook);

            int linha = 0;
            linha = escreverTitulo(sheet, linha, estiloTitulo);
            linha = escreverDadosPedido(sheet, linha, pedido, estiloSubtitulo, estiloRotulo, estiloCelula);
            linha++;
            escreverTabelaItens(sheet, linha, pedido, estiloCabecalhoTabela, estiloCelula, estiloTotal);

            for (int col = 0; col <= 3; col++) {
                sheet.setColumnWidth(col, 22 * 256);
            }

            configurarImpressao(sheet);

            try (FileOutputStream fos = new FileOutputStream(arquivoDestino)) {
                workbook.write(fos);
            }
        }
    }

    private int escreverTitulo(Sheet sheet, int linha, CellStyle estilo) {
        Row row = sheet.createRow(linha);
        Cell cell = row.createCell(0);
        cell.setCellValue("Salgaderia - Nota de Pedido");
        cell.setCellStyle(estilo);
        sheet.addMergedRegion(new CellRangeAddress(linha, linha, 0, 3));
        return linha + 2;
    }

    private int escreverDadosPedido(Sheet sheet, int linha, Pedido pedido,
                                    CellStyle estiloSubtitulo, CellStyle estiloRotulo, CellStyle estiloCelula) {
        Row rowPedido = sheet.createRow(linha++);
        criarCelula(rowPedido, 0, "Pedido #" + pedido.getId(), estiloSubtitulo);

        Row rowData = sheet.createRow(linha++);
        criarCelula(rowData, 0, "Data:", estiloRotulo);
        criarCelula(rowData, 1, pedido.getDataHora().format(FORMATO_DATA), estiloCelula);

        Row rowCliente = sheet.createRow(linha++);
        criarCelula(rowCliente, 0, "Cliente:", estiloRotulo);
        criarCelula(rowCliente, 1, pedido.getNomeCliente(), estiloCelula);

        Row rowTelefone = sheet.createRow(linha++);
        criarCelula(rowTelefone, 0, "Telefone:", estiloRotulo);
        criarCelula(rowTelefone, 1, pedido.getTelefone(), estiloCelula);

        if (pedido.getEndereco() != null && !pedido.getEndereco().isBlank()) {
            Row rowEndereco = sheet.createRow(linha++);
            criarCelula(rowEndereco, 0, "Endereço:", estiloRotulo);
            criarCelula(rowEndereco, 1, pedido.getEndereco(), estiloCelula);
        }

        return linha;
    }

    private void escreverTabelaItens(Sheet sheet, int linha, Pedido pedido,
                                     CellStyle estiloCabecalho, CellStyle estiloCelula, CellStyle estiloTotal) {
        Row header = sheet.createRow(linha++);
        criarCelula(header, 0, "Produto", estiloCabecalho);
        criarCelula(header, 1, "Qtd", estiloCabecalho);
        criarCelula(header, 2, "Preço Unit.", estiloCabecalho);
        criarCelula(header, 3, "Subtotal", estiloCabecalho);

        BigDecimal subtotalGeral = BigDecimal.ZERO;

        if (pedido.getItens() != null) {
            for (ItemPedido item : pedido.getItens()) {
                Row row = sheet.createRow(linha++);
                BigDecimal subtotalItem = item.getPrecoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade()));
                subtotalGeral = subtotalGeral.add(subtotalItem);

                criarCelula(row, 0, item.getNomeProduto(), estiloCelula);
                criarCelula(row, 1, String.valueOf(item.getQuantidade()), estiloCelula);
                criarCelula(row, 2, formatarMoeda(item.getPrecoUnitario()), estiloCelula);
                criarCelula(row, 3, formatarMoeda(subtotalItem), estiloCelula);
            }
        }

        linha++; // linha em branco antes dos totais

        Row rowSubtotal = sheet.createRow(linha++);
        criarCelula(rowSubtotal, 2, "Subtotal:", estiloCelula);
        criarCelula(rowSubtotal, 3, formatarMoeda(subtotalGeral), estiloCelula);

        BigDecimal taxa = pedido.getTaxaEntrega() != null ? pedido.getTaxaEntrega() : BigDecimal.ZERO;
        Row rowTaxa = sheet.createRow(linha++);
        criarCelula(rowTaxa, 2, "Taxa de entrega:", estiloCelula);
        criarCelula(rowTaxa, 3, formatarMoeda(taxa), estiloCelula);

        Row rowTotal = sheet.createRow(linha++);
        criarCelula(rowTotal, 2, "TOTAL:", estiloTotal);
        criarCelula(rowTotal, 3, formatarMoeda(pedido.getTotal()), estiloTotal);
    }

    private void criarCelula(Row row, int coluna, String valor, CellStyle estilo) {
        Cell cell = row.createCell(coluna);
        cell.setCellValue(valor);
        cell.setCellStyle(estilo);
    }

    private String formatarMoeda(BigDecimal valor) {
        return "R$ " + String.format("%.2f", valor.doubleValue());
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
        font.setFontHeightInPoints((short) 16);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle criarEstiloSubtitulo(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle criarEstiloRotulo(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
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

    private CellStyle criarEstiloCelula(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
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