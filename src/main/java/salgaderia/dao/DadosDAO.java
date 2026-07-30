package salgaderia.dao;

import salgaderia.model.*;
import salgaderia.model.enums.tipoProduto;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DadosDAO {

    private static DadosDAO instance;

    private DadosDAO() {
    }

    public static DadosDAO getInstance() {
        if (instance == null) {
            instance = new DadosDAO();
        }
        return instance;
    }

    public List<Produto> carregarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";

        try {
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getLong("id"));
                p.setNomeProduto(rs.getString("nome"));
                int centavos = rs.getInt("preco_unitario");
                p.setPrecoUnitario(BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100)));
                p.setTipoProduto(tipoProduto.valueOf(rs.getString("tipo_produto")));
                p.setAtivo(rs.getInt("ativo") == 1);
                produtos.add(p);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao carregar produtos: " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }

    public void salvarProduto(Produto p) {
        String sql = "INSERT INTO produtos (nome, preco_unitario, tipo_produto, ativo) VALUES (?, ?, ?, ?)";

        try {

            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, p.getNomeProduto());
            int centavos = p.getPrecoUnitario().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);
            pstmt.setString(3, p.getTipoProduto().name());
            pstmt.setInt(4, p.isAtivo() ? 1 : 0);

            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Produto salvo: " + p.getNomeProduto());

        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizarProduto(Produto p) {
        System.out.println("🔍 DadosDAO.atualizarProduto() chamado para ID: " + p.getId());

        String sql = "UPDATE produtos SET nome = ?, preco_unitario = ?, tipo_produto = ?, ativo = ? WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, p.getNomeProduto());
            int centavos = p.getPrecoUnitario().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);
            pstmt.setString(3, p.getTipoProduto().name());
            pstmt.setInt(4, p.isAtivo() ? 1 : 0);
            pstmt.setLong(5, p.getId());

            int rows = pstmt.executeUpdate();
            pstmt.close();

            System.out.println(" Produto atualizado: " + p.getNomeProduto() + " (" + rows + " linha(s) afetada(s))");

        } catch (SQLException e) {
            System.err.println(" Erro ao atualizar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletarProduto(long id) {
        System.out.println("🔍 DadosDAO.deletarProduto() chamado para ID: " + id);

        String sql = "DELETE FROM produtos WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setLong(1, id);

            int rows = pstmt.executeUpdate();
            pstmt.close();

            System.out.println("✅ Produto deletado: " + rows + " linha(s) afetada(s)");

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar produto: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void salvarPedido(Pedido pedido) {
        String sql = "INSERT INTO pedidos (nome_cliente, telefone, endereco, taxa_entrega, total, data_hora) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pedido.getNomeCliente());
            pstmt.setString(2, pedido.getTelefone());
            pstmt.setString(3, pedido.getEndereco());
            pstmt.setDouble(4, pedido.getTaxaEntrega() != null ? pedido.getTaxaEntrega().doubleValue() : 0.0);
            pstmt.setDouble(5, pedido.getTotal().doubleValue());
            pstmt.setString(6, pedido.getDataHora().toString());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        pedido.setId(generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar pedido: " + e.getMessage());
        }
    }

    public List<Pedido> carregarPedidos() {
        return new ArrayList<>();
    }


    // ==========================================
// MÉTODOS PARA ADICIONAIS
// ==========================================

    public List<Adicional> carregarAdicionais() {
        List<Adicional> adicionais = new ArrayList<>();
        String sql = "SELECT * FROM adicionais";

        try {
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Adicional a = new Adicional();
                a.setId(rs.getInt("id"));
                a.setNome(rs.getString("nome"));
                int centavos = rs.getInt("preco");
                a.setPreco(BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100)));
                adicionais.add(a);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("Erro ao carregar adicionais: " + e.getMessage());
            e.printStackTrace();
        }
        return adicionais;
    }

    public void salvarAdicional(Adicional a) {
        String sql = "INSERT INTO adicionais (nome, preco) VALUES (?, ?)";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, a.getNome());
            int centavos = a.getPreco().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);

            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Adicional salvo: " + a.getNome());

        } catch (SQLException e) {
            System.err.println("Erro ao salvar adicional: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizarAdicional(Adicional a) {
        String sql = "UPDATE adicionais SET nome = ?, preco = ? WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, a.getNome());
            int centavos = a.getPreco().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);
            pstmt.setInt(3, a.getId());

            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Adicional atualizado: " + a.getNome());

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar adicional: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletarAdicional(int id) {
        String sql = "DELETE FROM adicionais WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("Adicional deletado: ID " + id);

        } catch (SQLException e) {
            System.err.println("Erro ao deletar adicional: " + e.getMessage());
            e.printStackTrace();
        }
    }
}