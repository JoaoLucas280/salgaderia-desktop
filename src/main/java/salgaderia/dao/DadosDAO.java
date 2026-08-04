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

    private long ultimoIdInserido(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return -1;
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

        try {
            Connection conn = Database.getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, pedido.getNomeCliente());
                pstmt.setString(2, pedido.getTelefone());
                pstmt.setString(3, pedido.getEndereco());
                pstmt.setDouble(4, pedido.getTaxaEntrega() != null ? pedido.getTaxaEntrega().doubleValue() : 0.0);
                pstmt.setDouble(5, pedido.getTotal().doubleValue());
                pstmt.setString(6, pedido.getDataHora().toString());

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    long id = ultimoIdInserido(conn);
                    pedido.setId((int) id);
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao salvar pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Pedido> carregarPedidos() {
        return new ArrayList<>();
    }

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

    private List<ItemCombo> carregarItensCombo(int comboId) {
        List<ItemCombo> itens = new ArrayList<>();
        String sql = "SELECT ci.*, p.nome as produto_nome, p.preco_unitario FROM combo_itens ci " +
                "JOIN produtos p ON ci.produto_id = p.id WHERE ci.combo_id = ?";

        System.out.println("📖 carregarItensCombo() chamado para comboId=" + comboId);

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, comboId);
            ResultSet rs = pstmt.executeQuery();

            int contador = 0;
            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getLong("produto_id"));
                produto.setNomeProduto(rs.getString("produto_nome"));
                int centavos = rs.getInt("preco_unitario");
                produto.setPrecoUnitario(BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100)));

                ItemCombo item = new ItemCombo();
                item.setProduto(produto);

                itens.add(item);
                System.out.println("  ✓ Carregado item: produto_id=" + produto.getId() + ", nome=" + produto.getNomeProduto());
                contador++;
            }

            System.out.println("📊 Total de itens carregados para combo " + comboId + ": " + contador);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar itens do combo: " + e.getMessage());
            e.printStackTrace();
        }
        return itens;
    }

    private List<Adicional> carregarAdicionaisCombo(int comboId) {
        List<Adicional> adicionais = new ArrayList<>();
        String sql = "SELECT ca.*, a.nome, a.preco FROM combo_adicionais ca " +
                "JOIN adicionais a ON ca.adicional_id = a.id WHERE ca.combo_id = ?";

        System.out.println("🍴 carregarAdicionaisCombo() chamado para comboId=" + comboId);

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, comboId);
            ResultSet rs = pstmt.executeQuery();

            int contador = 0;
            while (rs.next()) {
                Adicional a = new Adicional();
                a.setId(rs.getInt("adicional_id"));
                a.setNome(rs.getString("nome"));
                int centavos = rs.getInt("preco");
                a.setPreco(BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100)));

                adicionais.add(a);
                System.out.println("  ✓ Carregado adicional: adicional_id=" + a.getId() + ", nome=" + a.getNome() + ", preco=" + a.getPreco());
                contador++;
            }

            System.out.println("📊 Total de adicionais carregados para combo " + comboId + ": " + contador);

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar adicionais do combo: " + e.getMessage());
            e.printStackTrace();
        }
        return adicionais;
    }

    public void salvarCombo(Combo c) {
        String sql = "INSERT INTO combos (nome, preco_total, max_items, max_flavors, qtd_adicionais_permitidos) " +
                "VALUES (?, ?, ?, ?, ?)";

        System.out.println("🔴 salvarCombo() chamado: nome=" + c.getNome() + ", itens=" + (c.getItens() != null ? c.getItens().size() : "null"));

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, c.getNome());
            int centavos = c.getPrecoTotal().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);
            pstmt.setInt(3, c.getQuantidadeMaximaDeItems());
            pstmt.setInt(4, c.getQuantidadeMaximaDeFlavors());
            pstmt.setInt(5, c.getQuantidadeAdicionaisPermitidos());

            int affectedRows = pstmt.executeUpdate();
            pstmt.close();

            System.out.println("   INSERT combo: " + affectedRows + " linha(s) afetada(s)");

            if (affectedRows > 0) {
                int comboId = (int) ultimoIdInserido(conn);
                c.setId(comboId);
                System.out.println("   ✅ Combo inserido com ID: " + comboId);

                System.out.println("   🔵 Chamando salvarItensCombo() com " + (c.getItens() != null ? c.getItens().size() : 0) + " itens");
                salvarItensCombo(comboId, c.getItens());

                System.out.println("   🔵 Chamando salvarAdicionaisCombo() com " + (c.getAdicionaisElegiveis() != null ? c.getAdicionaisElegiveis().size() : 0) + " adicionais");
                salvarAdicionaisCombo(comboId, c.getAdicionaisElegiveis());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Combo> carregarCombos() {
        List<Combo> combos = new ArrayList<>();
        String sql = "SELECT * FROM combos";

        System.out.println("🔍 carregarCombos() iniciado...");

        try {
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            int totalCombos = 0;
            while (rs.next()) {
                Combo c = new Combo();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                int centavos = rs.getInt("preco_total");
                c.setPrecoTotal(BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100)));
                c.setQuantidadeMaximaDeItems(rs.getInt("max_items"));
                c.setQuantidadeMaximaDeFlavors(rs.getInt("max_flavors"));
                c.setQuantidadeAdicionaisPermitidos(rs.getInt("qtd_adicionais_permitidos"));

                System.out.println("  📦 Carregando combo: id=" + c.getId() + ", nome=" + c.getNome());

                c.setItens(carregarItensCombo(c.getId()));
                System.out.println("  📋 Combo " + c.getId() + " tem " + c.getItens().size() + " itens");

                c.setAdicionaisElegiveis(carregarAdicionaisCombo(c.getId()));
                System.out.println("  🍴 Combo " + c.getId() + " tem " + c.getAdicionaisElegiveis().size() + " adicionais elegíveis");

                combos.add(c);
                totalCombos++;
            }

            System.out.println("✅ Total de combos carregados: " + totalCombos);

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar combos: " + e.getMessage());
            e.printStackTrace();
        }
        return combos;
    }

    private void salvarItensCombo(int comboId, List<ItemCombo> itens) {
        String sql = "INSERT INTO combo_itens (combo_id, produto_id) VALUES (?, ?)";

        System.out.println("📝 salvarItensCombo() chamado: comboId=" + comboId + ", itens=" + (itens != null ? itens.size() : "null"));

        try {
            Connection conn = Database.getConnection();
            if (itens != null && !itens.isEmpty()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);

                for (ItemCombo item : itens) {
                    System.out.println("  → Salvando: combo_id=" + comboId + ", produto_id=" + item.getProduto().getId());
                    pstmt.setInt(1, comboId);
                    pstmt.setLong(2, item.getProduto().getId());
                    pstmt.addBatch();
                }

                int[] result = pstmt.executeBatch();
                System.out.println("✅ " + result.length + " itens inseridos com sucesso");
                pstmt.close();
            } else {
                System.out.println("⚠️  Lista de itens vazia ou null - nenhum item será salvo");
            }

        } catch (Exception e) {
            System.err.println("❌ ERRO CRÍTICO ao salvar itens: " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void salvarAdicionaisCombo(int comboId, List<Adicional> adicionais) {
        String sql = "INSERT INTO combo_adicionais (combo_id, adicional_id) VALUES (?, ?)";

        System.out.println("🍴 salvarAdicionaisCombo() chamado: comboId=" + comboId + ", quantidade de adicionais=" + (adicionais != null ? adicionais.size() : 0));

        if (adicionais == null || adicionais.isEmpty()) {
            System.out.println("⚠️  AVISO: Lista de adicionais está vazia ou nula! Nenhum adicional será salvo.");
            return;
        }

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            int contador = 0;
            for (Adicional a : adicionais) {
                if (a == null) {
                    System.out.println("⚠️  AVISO: Adicional nulo em índice " + contador);
                    continue;
                }

                pstmt.setInt(1, comboId);
                pstmt.setInt(2, a.getId());
                pstmt.addBatch();
                System.out.println("  ✓ Adicionado ao batch: combo_id=" + comboId + ", adicional_id=" + a.getId() + ", nome=" + a.getNome());
                contador++;
            }

            System.out.println("🔄 Executando batch com " + contador + " adicionais...");
            int[] results = pstmt.executeBatch();
            System.out.println("✅ Batch executado! " + results.length + " linha(s) inserida(s)");

            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar adicionais do combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizarCombo(Combo c) {

        deletarItensCombo(c.getId());
        deletarAdicionaisCombo(c.getId());


        String sql = "UPDATE combos SET nome = ?, preco_total = ?, max_items = ?, max_flavors = ?, qtd_adicionais_permitidos = ? WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, c.getNome());
            int centavos = c.getPrecoTotal().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);
            pstmt.setInt(3, c.getQuantidadeMaximaDeItems());
            pstmt.setInt(4, c.getQuantidadeMaximaDeFlavors());
            pstmt.setInt(5, c.getQuantidadeAdicionaisPermitidos());
            pstmt.setInt(6, c.getId());

            pstmt.executeUpdate();
            pstmt.close();


            salvarItensCombo(c.getId(), c.getItens());
            salvarAdicionaisCombo(c.getId(), c.getAdicionaisElegiveis());

        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deletarItensCombo(int comboId) {
        String sql = "DELETE FROM combo_itens WHERE combo_id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, comboId);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar itens do combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deletarAdicionaisCombo(int comboId) {
        String sql = "DELETE FROM combo_adicionais WHERE combo_id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, comboId);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar adicionais do combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletarCombo(int id) {

        String sql = "DELETE FROM combos WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("✅ Combo deletado: ID " + id);

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar combo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Cento> carregarCentos() {
        List<Cento> centos = new ArrayList<>();
        String sql = "SELECT * FROM centos";

        try {
            Connection conn = Database.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Cento c = new Cento();
                c.setId(rs.getInt("id"));
                c.setNome(rs.getString("nome"));
                int centavos = rs.getInt("preco_total");
                c.setPrecoTotal(BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100)));
                c.setMaxSabores(rs.getInt("max_sabores"));

                c.setItens(carregarItensCento(c.getId()));

                centos.add(c);
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar centos: " + e.getMessage());
            e.printStackTrace();
        }
        return centos;
    }

    private List<ItemCombo> carregarItensCento(int centoId) {
        List<ItemCombo> itens = new ArrayList<>();
        String sql = "SELECT ci.*, p.nome as produto_nome, p.preco_unitario FROM cento_itens ci " +
                "JOIN produtos p ON ci.produto_id = p.id WHERE ci.cento_id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, centoId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getLong("produto_id"));
                produto.setNomeProduto(rs.getString("produto_nome"));
                int centavos = rs.getInt("preco_unitario");
                produto.setPrecoUnitario(BigDecimal.valueOf(centavos).divide(BigDecimal.valueOf(100)));

                ItemCombo item = new ItemCombo();
                item.setProduto(produto);

                itens.add(item);
            }

            rs.close();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao carregar itens do cento: " + e.getMessage());
            e.printStackTrace();
        }
        return itens;
    }

    public void salvarCento(Cento c) {
        String sql = "INSERT INTO centos (nome, preco_total, max_sabores) VALUES (?, ?, ?)";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, c.getNome());
            int centavos = c.getPrecoTotal().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);
            pstmt.setInt(3, c.getMaxSabores());

            int affectedRows = pstmt.executeUpdate();
            pstmt.close();

            if (affectedRows > 0) {
                int centoId = (int) ultimoIdInserido(conn);
                c.setId(centoId);
                salvarItensCento(centoId, c.getItens());
            }

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar cento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void salvarItensCento(int centoId, List<ItemCombo> itens) {
        if (itens == null || itens.isEmpty()) return;

        String sql = "INSERT INTO cento_itens (cento_id, produto_id) VALUES (?, ?)";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (ItemCombo item : itens) {
                pstmt.setInt(1, centoId);
                pstmt.setLong(2, item.getProduto().getId());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao salvar itens do cento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void atualizarCento(Cento c) {
        deletarItensCento(c.getId());

        String sql = "UPDATE centos SET nome = ?, preco_total = ?, max_sabores = ? WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, c.getNome());
            int centavos = c.getPrecoTotal().multiply(BigDecimal.valueOf(100)).intValue();
            pstmt.setInt(2, centavos);
            pstmt.setInt(3, c.getMaxSabores());
            pstmt.setInt(4, c.getId());

            pstmt.executeUpdate();
            pstmt.close();

            salvarItensCento(c.getId(), c.getItens());

            System.out.println("✅ Cento atualizado: " + c.getNome());

        } catch (SQLException e) {
            System.err.println("❌ Erro ao atualizar cento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deletarItensCento(int centoId) {
        String sql = "DELETE FROM cento_itens WHERE cento_id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, centoId);
            pstmt.executeUpdate();
            pstmt.close();

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar itens do cento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deletarCento(int id) {
        String sql = "DELETE FROM centos WHERE id = ?";

        try {
            Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            pstmt.close();

            System.out.println("✅ Cento deletado: ID " + id);

        } catch (SQLException e) {
            System.err.println("❌ Erro ao deletar cento: " + e.getMessage());
            e.printStackTrace();
        }
    }
}