package dao;

import model.Projeto;
import model.Usuario;
import util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO {

    public void create(Projeto projeto) {
        String sql = "INSERT INTO projetos (id, nome, descricao, dataInicio, dataTerminoPrevista, status, gerenteResponsavelId) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, projeto.getId());
            stmt.setString(2, projeto.getNome());
            stmt.setString(3, projeto.getDescricao());
            stmt.setString(4, projeto.getDataInicio().toString());
            stmt.setString(5, projeto.getDataTerminoPrevista() != null ? projeto.getDataTerminoPrevista().toString() : null);
            stmt.setString(6, projeto.getStatus());
            stmt.setString(7, projeto.getGerenteResponsavel() != null ? projeto.getGerenteResponsavel().getId() : null);
            
            stmt.executeUpdate();
            
            System.out.println("Projeto " + projeto.getNome() + " inserido no banco de dados.");
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir projeto: " + e.getMessage());
            throw new RuntimeException("Erro ao criar projeto", e);
        }
    }

    public Projeto findById(String id) {
        String sql = "SELECT p.*, u.id as gerente_id, u.nomeCompleto as gerente_nome, u.cpf as gerente_cpf, " +
                    "u.email as gerente_email, u.cargo as gerente_cargo, u.login as gerente_login, u.senha as gerente_senha " +
                    "FROM projetos p LEFT JOIN usuarios u ON p.gerenteResponsavelId = u.id WHERE p.id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createProjetoFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar projeto por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Projeto> findAll() {
        String sql = "SELECT p.*, u.id as gerente_id, u.nomeCompleto as gerente_nome, u.cpf as gerente_cpf, " +
                    "u.email as gerente_email, u.cargo as gerente_cargo, u.login as gerente_login, u.senha as gerente_senha " +
                    "FROM projetos p LEFT JOIN usuarios u ON p.gerenteResponsavelId = u.id";
        List<Projeto> projetos = new ArrayList<>();
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                projetos.add(createProjetoFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todos os projetos: " + e.getMessage());
        }
        return projetos;
    }

    public List<Projeto> findByGerente(String gerenteId) {
        String sql = "SELECT p.*, u.id as gerente_id, u.nomeCompleto as gerente_nome, u.cpf as gerente_cpf, " +
                    "u.email as gerente_email, u.cargo as gerente_cargo, u.login as gerente_login, u.senha as gerente_senha " +
                    "FROM projetos p LEFT JOIN usuarios u ON p.gerenteResponsavelId = u.id WHERE p.gerenteResponsavelId = ?";
        List<Projeto> projetos = new ArrayList<>();
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, gerenteId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    projetos.add(createProjetoFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar projetos por gerente: " + e.getMessage());
        }
        return projetos;
    }

    public void update(Projeto projeto) {
        String sql = "UPDATE projetos SET nome = ?, descricao = ?, dataInicio = ?, dataTerminoPrevista = ?, status = ?, gerenteResponsavelId = ? WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, projeto.getNome());
            stmt.setString(2, projeto.getDescricao());
            stmt.setString(3, projeto.getDataInicio().toString());
            stmt.setString(4, projeto.getDataTerminoPrevista() != null ? projeto.getDataTerminoPrevista().toString() : null);
            stmt.setString(5, projeto.getStatus());
            stmt.setString(6, projeto.getGerenteResponsavel() != null ? projeto.getGerenteResponsavel().getId() : null);
            stmt.setString(7, projeto.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Projeto " + projeto.getNome() + " atualizado com sucesso.");
            } else {
                System.out.println("Nenhum projeto foi atualizado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar projeto: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar projeto", e);
        }
    }

    public void delete(String id) {
        String sql = "DELETE FROM projetos WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Projeto com ID " + id + " deletado com sucesso.");
            } else {
                System.out.println("Nenhum projeto foi deletado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar projeto: " + e.getMessage());
            throw new RuntimeException("Erro ao deletar projeto", e);
        }
    }

    private Projeto createProjetoFromResultSet(ResultSet rs) throws SQLException {
        // Criar gerente se existir
        Usuario gerente = null;
        if (rs.getString("gerente_id") != null) {
            gerente = new Usuario(
                rs.getString("gerente_id"),
                rs.getString("gerente_nome"),
                rs.getString("gerente_cpf"),
                rs.getString("gerente_email"),
                rs.getString("gerente_cargo"),
                rs.getString("gerente_login"),
                rs.getString("gerente_senha")
            );
        }

        // Criar projeto
        String id = rs.getString("id");
        String nome = rs.getString("nome");
        String descricao = rs.getString("descricao");
        LocalDate dataInicio = LocalDate.parse(rs.getString("dataInicio"));
        LocalDate dataTerminoPrevista = rs.getString("dataTerminoPrevista") != null ? 
            LocalDate.parse(rs.getString("dataTerminoPrevista")) : null;
        String status = rs.getString("status");

        // Usar construtor que aceita todos os parâmetros (preciso criar este construtor)
        Projeto projeto = new Projeto(nome, descricao, dataInicio, dataTerminoPrevista, gerente);
        
        // Definir ID e status manualmente
        projeto.setId(id);
        projeto.setStatus(status);
        
        return projeto;
    }
}
