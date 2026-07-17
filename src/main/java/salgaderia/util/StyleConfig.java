package salgaderia.util;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class StyleConfig {

    public static final Color COR_PRIMARIA = new Color(230, 120, 50);
    public static final Color COR_SECUNDARIA = new Color(50, 50, 60);
    public static final Color COR_FUNDO = new Color(245, 245, 245);
    public static final Color COR_SUCESSO = new Color(0, 150, 0);
    public static final Color COR_ERRO = new Color(180, 0, 0);


    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_PEQUENA = new Font("Segoe UI", Font.PLAIN, 11);


    public static void estilizarBotao(JButton botao, Color cor) {
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFont(FONTE_NORMAL);
        botao.setFocusPainted(false);
        botao.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        botao.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public static TitledBorder criarBorda(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                titulo,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                FONTE_SUBTITULO,
                COR_PRIMARIA
        );
    }


    public static void estilizarCampo(JTextField campo) {
        campo.setFont(FONTE_NORMAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
    }
}
