package dao;

import model.Equipe;
import model.Usuario;
import util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipeDAO {

    public void create(Equipe equipe) {
        String sql = "INSERT INTO equipes (id, nome, descricao) VALUES (?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipe.getId());
            stmt.setString(2, equipe.getNome());
            stmt.setString(3, equipe.getDescricao());
            
            stmt.executeUpdate();
            
            // Inserir membros da equipe
            for (Usuario membro : equipe.getMembros()) {
                adicionarMembro(equipe.getId(), membro.getId());
            }
            
            System.out.println("Equipe " + equipe.getNome() + " inserida no banco de dados.");
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir equipe: " + e.getMessage());
            throw new RuntimeException("Erro ao criar equipe", e);
        }
    }

    public Equipe findById(String id) {
        String sql = "SELECT * FROM equipes WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Equipe equipe = new Equipe(rs.getString("nome"), rs.getString("descricao"));
                    equipe.setId(rs.getString("id"));
                    
                    // Carregar membros da equipe
                    List<Usuario> membros = findMembros(id);
                    for (Usuario membro : membros) {
                        equipe.adicionarMembro(membro);
                    }
                    
                    return equipe;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar equipe por ID: " + e.getMessage());
        }
        return null;
    }

    public List<Equipe> findAll() {
        String sql = "SELECT * FROM equipes";
        List<Equipe> equipes = new ArrayList<>();
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Equipe equipe = new Equipe(rs.getString("nome"), rs.getString("descricao"));
                equipe.setId(rs.getString("id"));
                
                // Carregar membros da equipe
                List<Usuario> membros = findMembros(equipe.getId());
                for (Usuario membro : membros) {
                    equipe.adicionarMembro(membro);
                }
                
                equipes.add(equipe);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar todas as equipes: " + e.getMessage());
        }
        return equipes;
    }

    public void update(Equipe equipe) {
        String sql = "UPDATE equipes SET nome = ?, descricao = ? WHERE id = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipe.getNome());
            stmt.setString(2, equipe.getDescricao());
            stmt.setString(3, equipe.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Atualizar membros da equipe
                removerTodosMembros(equipe.getId());
                for (Usuario membro : equipe.getMembros()) {
                    adicionarMembro(equipe.getId(), membro.getId());
                }
                
                System.out.println("Equipe " + equipe.getNome() + " atualizada com sucesso.");
            } else {
                System.out.println("Nenhuma equipe foi atualizada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar equipe: " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar equipe", e);
        }
    }

    public void delete(String id) {
        try (Connection conn = Database.getConnection()) {
            // Primeiro remover todos os membros
            removerTodosMembros(id);
            
            // Depois remover a equipe
            String sql = "DELETE FROM equipes WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, id);
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    System.out.println("Equipe com ID " + id + " deletada com sucesso.");
                } else {
                    System.out.println("Nenhuma equipe foi deletada.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao deletar equipe: " + e.getMessage());
            throw new RuntimeException("Erro ao deletar equipe", e);
        }
    }

    public void adicionarMembro(String equipeId, String usuarioId) {
        String sql = "INSERT INTO membros_equipe (equipeId, usuarioId) VALUES (?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipeId);
            stmt.setString(2, usuarioId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar membro à equipe: " + e.getMessage());
        }
    }

    public void removerMembro(String equipeId, String usuarioId) {
        String sql = "DELETE FROM membros_equipe WHERE equipeId = ? AND usuarioId = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipeId);
            stmt.setString(2, usuarioId);
            
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover membro da equipe: " + e.getMessage());
        }
    }

    private void removerTodosMembros(String equipeId) {
        String sql = "DELETE FROM membros_equipe WHERE equipeId = ?";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipeId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Erro ao remover todos os membros da equipe: " + e.getMessage());
        }
    }

    private List<Usuario> findMembros(String equipeId) {
        String sql = "SELECT u.* FROM usuarios u " +
                    "INNER JOIN membros_equipe me ON u.id = me.usuarioId " +
                    "WHERE me.equipeId = ?";
        List<Usuario> membros = new ArrayList<>();
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, equipeId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Usuario usuario = new Usuario(
                        rs.getString("id"),
                        rs.getString("nomeCompleto"),
                        rs.getString("cpf"),
                        rs.getString("email"),
                        rs.getString("cargo"),
                        rs.getString("login"),
                        rs.getString("senha")
                    );
                    membros.add(usuario);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar membros da equipe: " + e.getMessage());
        }
        return membros;
    }
}
