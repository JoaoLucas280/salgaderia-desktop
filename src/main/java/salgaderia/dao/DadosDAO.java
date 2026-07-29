package salgaderia.dao;

import salgaderia.model.*;
import salgaderia.model.enums.tipoProduto;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DadosDAO {

    public List<Produto> carregarProdutos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = "SELECT * FROM produtos";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto p = new Produto();
                p.setId(rs.getLong("id"));
                p.setNomeProduto(rs.getString("nome"));
                p.setPrecoUnitario(BigDecimal.valueOf(rs.getDouble("preco_unitario")));
                p.setTipoProduto(tipoProduto.valueOf(rs.getString("tipo_produto")));
                p.setAtivo(rs.getInt("ativo") == 1);
                produtos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar produtos: " + e.getMessage());
        }
        return produtos;
    }

    public void salvarProduto(Produto p) {
        String sql = "INSERT INTO produtos (nome, preco_unitario, tipo_produto, ativo) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNomeProduto());
            pstmt.setDouble(2, p.getPrecoUnitario().doubleValue());
            pstmt.setString(3, p.getTipoProduto().name());
            pstmt.setInt(4, p.isAtivo() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    public void atualizarProduto(Produto p) {
        String sql = "UPDATE produtos SET nome = ?, preco_unitario = ?, tipo_produto = ?, ativo = ? WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, p.getNomeProduto());
            pstmt.setDouble(2, p.getPrecoUnitario().doubleValue());
            pstmt.setString(3, p.getTipoProduto().name());
            pstmt.setInt(4, p.isAtivo() ? 1 : 0);
            pstmt.setLong(5, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public void deletarProduto(long id) {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar produto: " + e.getMessage());
        }
    }
}