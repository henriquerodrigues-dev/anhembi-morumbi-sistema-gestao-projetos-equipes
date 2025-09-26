package controller;

import dao.UsuarioDAO;
import model.Usuario;
import util.ValidadorUtil;
import java.util.List;

public class UsuarioController {
    private UsuarioDAO usuarioDAO;

    public UsuarioController() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public String criarUsuario(String nome, String cpf, String email, String cargo, String login, String senha) {
        // Validações
        if (!ValidadorUtil.validarCampoObrigatorio(nome)) {
            return "Nome completo é obrigatório!";
        }
        
        if (!ValidadorUtil.validarNomeCompleto(nome)) {
            return "Nome deve conter pelo menos nome e sobrenome!";
        }

        if (!ValidadorUtil.validarCampoObrigatorio(cpf)) {
            return "CPF é obrigatório!";
        }
        
        if (!ValidadorUtil.validarCPF(cpf)) {
            return "CPF inválido!";
        }

        if (!ValidadorUtil.validarCampoObrigatorio(email)) {
            return "Email é obrigatório!";
        }
        
        if (!ValidadorUtil.validarEmail(email)) {
            return "Email inválido!";
        }

        if (!ValidadorUtil.validarCampoObrigatorio(cargo)) {
            return "Cargo é obrigatório!";
        }

        if (!ValidadorUtil.validarCampoObrigatorio(login)) {
            return "Login é obrigatório!";
        }
        
        if (!ValidadorUtil.validarLogin(login)) {
            return "Login deve ter 3-20 caracteres (letras, números e underscore)!";
        }

        if (!ValidadorUtil.validarCampoObrigatorio(senha)) {
            return "Senha é obrigatória!";
        }
        
        if (!ValidadorUtil.validarSenha(senha)) {
            return "Senha deve ter pelo menos 6 caracteres, com letras e números!";
        }

        try {
            // Limpar e formatar dados
            String cpfLimpo = ValidadorUtil.limparCPF(cpf);
            
            Usuario novoUsuario = new Usuario(
                nome.trim(),
                cpfLimpo,
                email.toLowerCase().trim(),
                cargo.trim(),
                login.toLowerCase().trim(),
                senha
            );
            
            usuarioDAO.create(novoUsuario);
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao salvar usuário: " + e.getMessage();
        }
    }

    public Usuario buscarUsuario(String id) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return null;
        }
        return usuarioDAO.findById(id.trim());
    }

    public String atualizarUsuario(String id, String nome, String cpf, String email, String cargo, String login, String senha) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return "ID é obrigatório para atualização!";
        }

        Usuario usuarioExistente = usuarioDAO.findById(id.trim());
        if (usuarioExistente == null) {
            return "Usuário não encontrado!";
        }

        // Usar as mesmas validações do criar
        String validacao = criarUsuario(nome, cpf, email, cargo, login, senha);
        if (validacao != null) {
            return validacao;
        }

        try {
            // Limpar e formatar dados
            String cpfLimpo = ValidadorUtil.limparCPF(cpf);
            
            usuarioExistente.setNomeCompleto(nome.trim());
            usuarioExistente.setCpf(cpfLimpo);
            usuarioExistente.setEmail(email.toLowerCase().trim());
            usuarioExistente.setCargo(cargo.trim());
            usuarioExistente.setLogin(login.toLowerCase().trim());
            usuarioExistente.setSenha(senha);
            
            usuarioDAO.update(usuarioExistente);
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao atualizar usuário: " + e.getMessage();
        }
    }

    public String excluirUsuario(String id) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return "ID é obrigatório para exclusão!";
        }

        try {
            usuarioDAO.delete(id.trim());
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao excluir usuário: " + e.getMessage();
        }
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioDAO.findAll();
    }
}
