package salgaderia.dao;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:salgaderia.db";
    private static Connection connection = null;

    public static Connection getConnection() {
        try {

            if (connection == null || connection.isClosed()) {
                System.out.println("🔍 Abrindo conexão com o banco...");
                connection = DriverManager.getConnection(URL);
                criarTabelas();
                System.out.println(" Conexão estabelecida com sucesso!");
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    private static void criarTabelas() {
        String sqlProdutos = """
            CREATE TABLE IF NOT EXISTS produtos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                preco_unitario INTEGER NOT NULL,
                tipo_produto TEXT NOT NULL,
                ativo INTEGER DEFAULT 1
            )
        """;

        String sqlAdicionais = """
            CREATE TABLE IF NOT EXISTS adicionais (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                preco INTEGER NOT NULL
            )
        """;

        String sqlCombos = """
            CREATE TABLE IF NOT EXISTS combos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                preco_total INTEGER NOT NULL,
                max_items INTEGER NOT NULL,
                max_flavors INTEGER NOT NULL,
                qtd_adicionais_permitidos INTEGER DEFAULT 0
            )
        """;

        String sqlComboItens = """
            CREATE TABLE IF NOT EXISTS combo_itens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                combo_id INTEGER NOT NULL,
                produto_id INTEGER NOT NULL,
                quantidade_maxima INTEGER NOT NULL,
                FOREIGN KEY (combo_id) REFERENCES combos(id) ON DELETE CASCADE,
                FOREIGN KEY (produto_id) REFERENCES produtos(id)
            )
        """;

        String sqlComboAdicionais = """
            CREATE TABLE IF NOT EXISTS combo_adicionais (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                combo_id INTEGER NOT NULL,
                adicional_id INTEGER NOT NULL,
                FOREIGN KEY (combo_id) REFERENCES combos(id) ON DELETE CASCADE,
                FOREIGN KEY (adicional_id) REFERENCES adicionais(id)
            )
        """;

        String sqlCentos = """
            CREATE TABLE IF NOT EXISTS centos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome TEXT NOT NULL,
                preco_total INTEGER NOT NULL,
                max_sabores INTEGER NOT NULL
            )
        """;

        String sqlCentoItens = """
            CREATE TABLE IF NOT EXISTS cento_itens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cento_id INTEGER NOT NULL,
                produto_id INTEGER NOT NULL,
                FOREIGN KEY (cento_id) REFERENCES centos(id) ON DELETE CASCADE,
                FOREIGN KEY (produto_id) REFERENCES produtos(id)
            )
        """;

        String sqlPedidos = """
            CREATE TABLE IF NOT EXISTS pedidos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nome_cliente TEXT NOT NULL,
                telefone TEXT NOT NULL,
                endereco TEXT,
                taxa_entrega INTEGER DEFAULT 0,
                total INTEGER NOT NULL,
                data_hora TEXT NOT NULL
            )
        """;

        String sqlPedidoItens = """
            CREATE TABLE IF NOT EXISTS pedido_itens (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pedido_id INTEGER NOT NULL,
                produto_nome TEXT NOT NULL,
                quantidade INTEGER NOT NULL,
                preco_unitario INTEGER NOT NULL,
                FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE
            )
        """;

        String sqlLancamentos = """
            CREATE TABLE IF NOT EXISTS lancamentos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tipo TEXT NOT NULL,
                categoria TEXT NOT NULL,
                descricao TEXT NOT NULL,
                valor INTEGER NOT NULL,
                data TEXT NOT NULL,
                forma_pagamento TEXT,
                observacao TEXT,
                pedido_id INTEGER,
                FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sqlProdutos);
            stmt.execute(sqlAdicionais);
            stmt.execute(sqlCombos);
            stmt.execute(sqlComboItens);
            stmt.execute(sqlComboAdicionais);
            stmt.execute(sqlCentos);
            stmt.execute(sqlCentoItens);
            stmt.execute(sqlPedidos);
            stmt.execute(sqlPedidoItens);
            stmt.execute(sqlLancamentos);
            System.out.println("✅ Tabelas verificadas/criadas com sucesso!");
        } catch (SQLException e) {
            System.err.println(" Erro ao criar tabelas: " + e.getMessage());
        }
    }


    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println(" Conexão fechada.");
            }
        } catch (SQLException e) {
            System.err.println(" Erro ao fechar conexão: " + e.getMessage());
        }
    }
}