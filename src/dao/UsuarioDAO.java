package dao;

import model.Usuario;
import util.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UsuarioDAO {

    public void create(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id, nomeCompleto, cpf, email, cargo, login, senha) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, usuario.getId());
            stmt.setString(2, usuario.getNomeCompleto());
            stmt.setString(3, usuario.getCpf());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getCargo());
            stmt.setString(6, usuario.getLogin());
            stmt.setString(7, usuario.getSenha());
            
            stmt.executeUpdate();
            
            System.out.println("Usuário " + usuario.getNomeCompleto() + " inserido no banco de dados.");
            
        } catch (SQLException e) {
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
        }
    }
}