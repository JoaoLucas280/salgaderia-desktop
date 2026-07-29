package salgaderia;

import salgaderia.dao.Database;
import java.sql.*;

public class TesteDatabase {
    public static void main(String[] args) {
        try {
            Connection conn = Database.getConnection();
            System.out.println(" Conexão com SQLite estabelecida!");


            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getTables(null, null, "produtos", null);
            if (rs.next()) {
                System.out.println("Tabela 'produtos' existe!");
            }

            Database.close();
        } catch (SQLException e) {
            System.err.println(" Erro: " + e.getMessage());
        }
    }
}