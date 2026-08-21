package salgaderia.util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;


public class StyleConfig {

    // ===== PALETA (Casa da Coxinha) =====
    public static final Color COR_PRIMARIA = new Color(0xFD, 0xB8, 0x13);
    public static final Color COR_PRIMARIA_ESCURA = new Color(0xC7, 0x8E, 0x0A);
    public static final Color COR_SECUNDARIA = new Color(0x41, 0x15, 0x0A);
    public static final Color COR_DESTAQUE = new Color(0xFF, 0xE0, 0x8A);
    public static final Color COR_FUNDO = new Color(0xFA, 0xF3, 0xE7);
    public static final Color COR_FUNDO_CARTAO = Color.WHITE;
    public static final Color COR_BORDA = new Color(0xE3, 0xCF, 0xA8);
    public static final Color COR_SUCESSO = new Color(0x5B, 0x8C, 0x3A);
    public static final Color COR_ERRO = new Color(0xB3, 0x3A, 0x1A);
    public static final Color COR_TEXTO = new Color(0x2A, 0x1A, 0x10);


    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 11);


    public static void estilizarBotao(JButton botao) {
        estilizarBotao(botao, COR_PRIMARIA);
    }

    public static void estilizarBotao(JButton botao, Color cor) {
        botao.setBackground(cor);
        botao.setForeground(corTextoParaFundo(cor));
        botao.setFont(FONTE_NORMAL);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static void estilizarBotaoSecundario(JButton botao) {
        botao.setBackground(COR_FUNDO_CARTAO);
        botao.setForeground(COR_SECUNDARIA);
        botao.setFont(FONTE_NORMAL);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private static Color corTextoParaFundo(Color fundo) {
        double luminancia = (0.299 * fundo.getRed() + 0.587 * fundo.getGreen() + 0.114 * fundo.getBlue()) / 255.0;
        return luminancia > 0.6 ? COR_SECUNDARIA : Color.WHITE;
    }

    public static TitledBorder criarBorda(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COR_BORDA, 1, true),
                titulo,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONTE_SUBTITULO,
                COR_SECUNDARIA
        );
    }


    public static void estilizarCampo(JTextField campo) {
        campo.setFont(FONTE_NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COR_BORDA),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }


    public static void aplicarTema() {
        try {
            UIManager.put("Component.accentColor", COR_PRIMARIA);
            UIManager.put("Component.focusColor", COR_PRIMARIA);
            UIManager.put("Component.borderColor", COR_BORDA);
            UIManager.put("Component.arc", 10);
            UIManager.put("Button.arc", 12);
            UIManager.put("Button.default.background", COR_PRIMARIA);
            UIManager.put("Button.default.foreground", COR_SECUNDARIA);
            UIManager.put("ProgressBar.foreground", COR_PRIMARIA);
            UIManager.put("TabbedPane.selectedBackground", COR_DESTAQUE);
            UIManager.put("TabbedPane.underlineColor", COR_PRIMARIA);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("Panel.background", COR_FUNDO);
            UIManager.put("OptionPane.background", COR_FUNDO_CARTAO);

            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            System.err.println(" Não foi possível aplicar o tema FlatLaf, seguindo com o Look & Feel padrão: " + e.getMessage());
        }
    }
}
