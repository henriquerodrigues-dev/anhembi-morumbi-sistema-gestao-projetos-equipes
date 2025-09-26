package controller;

import dao.EquipeDAO;
import model.Equipe;
import util.ValidadorUtil;
import java.util.List;

public class EquipeController {
    private EquipeDAO equipeDAO;

    public EquipeController() {
        this.equipeDAO = new EquipeDAO();
    }

    public String criarEquipe(String nome, String descricao) {
        // Validações
        if (!ValidadorUtil.validarCampoObrigatorio(nome)) {
            return "Nome da equipe é obrigatório!";
        }
        
        if (!ValidadorUtil.validarCampoObrigatorio(descricao)) {
            return "Descrição da equipe é obrigatória!";
        }

        try {
            Equipe equipe = new Equipe(nome.trim(), descricao.trim());
            equipeDAO.create(equipe);
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao salvar equipe: " + e.getMessage();
        }
    }

    public Equipe buscarEquipe(String id) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return null;
        }
        return equipeDAO.findById(id.trim());
    }

    public String atualizarEquipe(String id, String nome, String descricao) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return "ID é obrigatório para atualização!";
        }

        Equipe equipeExistente = equipeDAO.findById(id.trim());
        if (equipeExistente == null) {
            return "Equipe não encontrada!";
        }

        // Validações
        if (!ValidadorUtil.validarCampoObrigatorio(nome)) {
            return "Nome da equipe é obrigatório!";
        }
        
        if (!ValidadorUtil.validarCampoObrigatorio(descricao)) {
            return "Descrição da equipe é obrigatória!";
        }

        try {
            equipeExistente.setNome(nome.trim());
            equipeExistente.setDescricao(descricao.trim());
            
            equipeDAO.update(equipeExistente);
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao atualizar equipe: " + e.getMessage();
        }
    }

    public String excluirEquipe(String id) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return "ID é obrigatório para exclusão!";
        }

        try {
            equipeDAO.delete(id.trim());
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao excluir equipe: " + e.getMessage();
        }
    }

    public String adicionarMembro(String equipeId, String usuarioId) {
        if (!ValidadorUtil.validarCampoObrigatorio(equipeId)) {
            return "ID da equipe é obrigatório!";
        }
        
        if (!ValidadorUtil.validarCampoObrigatorio(usuarioId)) {
            return "ID do usuário é obrigatório!";
        }

        try {
            equipeDAO.adicionarMembro(equipeId.trim(), usuarioId.trim());
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao adicionar membro: " + e.getMessage();
        }
    }

    public String removerMembro(String equipeId, String usuarioId) {
        if (!ValidadorUtil.validarCampoObrigatorio(equipeId)) {
            return "ID da equipe é obrigatório!";
        }
        
        if (!ValidadorUtil.validarCampoObrigatorio(usuarioId)) {
            return "ID do usuário é obrigatório!";
        }

        try {
            equipeDAO.removerMembro(equipeId.trim(), usuarioId.trim());
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao remover membro: " + e.getMessage();
        }
    }

    public List<Equipe> listarTodasEquipes() {
        return equipeDAO.findAll();
    }
}
