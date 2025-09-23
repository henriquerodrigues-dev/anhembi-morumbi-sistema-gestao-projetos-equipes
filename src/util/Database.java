package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    // URL de conexão com o banco de dados SQLite
    private static final String URL = "jdbc:sqlite:./gestao_projetos.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Registra o driver JDBC do SQLite
            Class.forName("org.sqlite.JDBC");
            // Cria a conexão com o banco de dados
            conn = DriverManager.getConnection(URL);
            System.out.println("Conexão com o banco de dados estabelecida com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do SQLite não encontrado: " + e.getMessage());
        }
        return conn;
    }

    public static void createTables() {
        String sqlUsuario = "CREATE TABLE IF NOT EXISTS usuarios ("
                + "id TEXT PRIMARY KEY,"
                + "nomeCompleto TEXT NOT NULL,"
                + "cpf TEXT UNIQUE NOT NULL,"
                + "email TEXT UNIQUE NOT NULL,"
                + "cargo TEXT NOT NULL,"
                + "login TEXT UNIQUE NOT NULL,"
                + "senha TEXT NOT NULL"
                + ");";

        String sqlProjeto = "CREATE TABLE IF NOT EXISTS projetos ("
                + "id TEXT PRIMARY KEY,"
                + "nome TEXT NOT NULL,"
                + "descricao TEXT,"
                + "dataInicio TEXT NOT NULL,"
                + "dataTerminoPrevista TEXT,"
                + "status TEXT NOT NULL,"
                + "gerenteResponsavelId TEXT,"
                + "FOREIGN KEY (gerenteResponsavelId) REFERENCES usuarios(id)"
                + ");";

        String sqlEquipe = "CREATE TABLE IF NOT EXISTS equipes ("
                + "id TEXT PRIMARY KEY,"
                + "nome TEXT NOT NULL,"
                + "descricao TEXT"
                + ");";

        String sqlMembrosEquipe = "CREATE TABLE IF NOT EXISTS membros_equipe ("
                + "equipeId TEXT,"
                + "usuarioId TEXT,"
                + "PRIMARY KEY (equipeId, usuarioId),"
                + "FOREIGN KEY (equipeId) REFERENCES equipes(id),"
                + "FOREIGN KEY (usuarioId) REFERENCES usuarios(id)"
                + ");";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sqlUsuario);
            stmt.execute(sqlProjeto);
            stmt.execute(sqlEquipe);
            stmt.execute(sqlMembrosEquipe);
            System.out.println("Tabelas criadas com sucesso.");

        } catch (SQLException e) {
            System.err.println("Erro ao criar tabelas: " + e.getMessage());
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexão com o banco de dados fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
            }
        }
    }
}