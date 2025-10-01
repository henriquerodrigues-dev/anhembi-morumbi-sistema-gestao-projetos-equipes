package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class responsible for cleaning and seeding the SQLite database with
 * mock data. Execute once via: {@code java -cp "bin;lib/*" util.DataSeeder}
 */
public class DataSeeder {

    public static void main(String[] args) {
        seedDatabase();
    }

    public static void seedDatabase() {
        Connection conn = Database.getConnection();
        if (conn == null) {
            System.err.println("Não foi possível estabelecer conexão com o banco para realizar o seed.");
            return;
        }

        try {
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");

                // Limpeza das tabelas respeitando a integridade referencial
                stmt.executeUpdate("DELETE FROM membros_equipe");
                stmt.executeUpdate("DELETE FROM projetos");
                stmt.executeUpdate("DELETE FROM equipes");
                stmt.executeUpdate("DELETE FROM usuarios");
            }

            insertUsuarios(conn);
            insertEquipes(conn);
            insertProjetos(conn);
            insertMembros(conn);

            conn.commit();
            System.out.println("Base de dados limpa e populada com sucesso!");
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Erro ao realizar rollback: " + rollbackEx.getMessage());
            }
            System.err.println("Erro ao realizar seed do banco: " + e.getMessage());
        } finally {
            Database.closeConnection(conn);
        }
    }

    private static void insertUsuarios(Connection conn) throws SQLException {
        String sql = "INSERT INTO usuarios (id, nomeCompleto, cpf, email, cargo, login, senha) VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> usuarios = Arrays.asList(
            new Object[]{"u-001", "Henrique Andrade", "123.456.789-00", "henrique@empresa.com", "Gerente de Projetos", "henrique", "senha123"},
            new Object[]{"u-002", "Maria Oliveira", "234.567.890-11", "maria@empresa.com", "Analista de Sistemas", "maria.o", "senha123"},
            new Object[]{"u-003", "João Santos", "345.678.901-22", "joao@empresa.com", "Designer UX", "joao.s", "senha123"},
            new Object[]{"u-004", "Carla Pereira", "456.789.012-33", "carla@empresa.com", "Scrum Master", "carla.p", "senha123"},
            new Object[]{"u-005", "Rafael Nascimento", "567.890.123-44", "rafael@empresa.com", "Desenvolvedor Back-end", "rafael.n", "senha123"}
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] usuario : usuarios) {
                ps.setString(1, (String) usuario[0]);
                ps.setString(2, (String) usuario[1]);
                ps.setString(3, (String) usuario[2]);
                ps.setString(4, (String) usuario[3]);
                ps.setString(5, (String) usuario[4]);
                ps.setString(6, (String) usuario[5]);
                ps.setString(7, (String) usuario[6]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void insertEquipes(Connection conn) throws SQLException {
        String sql = "INSERT INTO equipes (id, nome, descricao) VALUES (?, ?, ?)";

        List<Object[]> equipes = Arrays.asList(
            new Object[]{"e-001", "Equipe Phoenix", "Time multidisciplinar focado em soluções mobile."},
            new Object[]{"e-002", "Equipe Atlas", "Especialistas em integrações e APIs corporativas."}
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] equipe : equipes) {
                ps.setString(1, (String) equipe[0]);
                ps.setString(2, (String) equipe[1]);
                ps.setString(3, (String) equipe[2]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void insertProjetos(Connection conn) throws SQLException {
        String sql = "INSERT INTO projetos (id, nome, descricao, dataInicio, dataTerminoPrevista, status, gerenteResponsavelId) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        List<Object[]> projetos = Arrays.asList(
            new Object[]{"p-001", "Portal do Cliente", "Novo portal responsivo para clientes corporativos.", "2024-01-15", "2024-06-30", "Em Andamento", "u-001"},
            new Object[]{"p-002", "App Delivery", "Aplicativo móvel para entregas em tempo real.", "2024-02-10", "2024-08-15", "Pendente", "u-002"},
            new Object[]{"p-003", "Dashboard Financeiro", "Painel analítico para diretoria financeira.", "2023-11-01", "2024-03-31", "Concluído", "u-004"}
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] projeto : projetos) {
                ps.setString(1, (String) projeto[0]);
                ps.setString(2, (String) projeto[1]);
                ps.setString(3, (String) projeto[2]);
                ps.setString(4, (String) projeto[3]);
                ps.setString(5, (String) projeto[4]);
                ps.setString(6, (String) projeto[5]);
                ps.setString(7, (String) projeto[6]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    private static void insertMembros(Connection conn) throws SQLException {
        String sql = "INSERT INTO membros_equipe (equipeId, usuarioId) VALUES (?, ?)";

        List<Object[]> membros = Arrays.asList(
            new Object[]{"e-001", "u-001"},
            new Object[]{"e-001", "u-002"},
            new Object[]{"e-001", "u-003"},
            new Object[]{"e-002", "u-004"},
            new Object[]{"e-002", "u-005"}
        );

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (Object[] membro : membros) {
                ps.setString(1, (String) membro[0]);
                ps.setString(2, (String) membro[1]);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}


