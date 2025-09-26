package controller;

import dao.ProjetoDAO;
import dao.UsuarioDAO;
import model.Projeto;
import model.Usuario;
import util.ValidadorUtil;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ProjetoController {
    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO;

    public ProjetoController() {
        this.projetoDAO = new ProjetoDAO();
        this.usuarioDAO = new UsuarioDAO();
    }

    public String criarProjeto(String nome, String descricao, String dataInicio, String dataTermino, String status, String gerenteId) {
        // Validações
        if (!ValidadorUtil.validarCampoObrigatorio(nome)) {
            return "Nome do projeto é obrigatório!";
        }
        
        if (!ValidadorUtil.validarCampoObrigatorio(descricao)) {
            return "Descrição do projeto é obrigatória!";
        }
        
        if (!ValidadorUtil.validarCampoObrigatorio(dataInicio)) {
            return "Data de início é obrigatória!";
        }
        
        if (!ValidadorUtil.validarCampoObrigatorio(status)) {
            return "Status do projeto é obrigatório!";
        }

        try {
            LocalDate inicio = LocalDate.parse(dataInicio.trim());
            LocalDate termino = null;
            if (ValidadorUtil.validarCampoObrigatorio(dataTermino)) {
                termino = LocalDate.parse(dataTermino.trim());
            }
            
            Usuario gerente = null;
            if (ValidadorUtil.validarCampoObrigatorio(gerenteId)) {
                gerente = usuarioDAO.findById(gerenteId.trim());
                if (gerente == null) {
                    return "Gerente não encontrado!";
                }
            }
            
            Projeto projeto = new Projeto(nome.trim(), descricao.trim(), inicio, termino, gerente);
            projeto.setStatus(status.trim());
            
            projetoDAO.create(projeto);
            return null; // Sucesso
            
        } catch (DateTimeParseException e) {
            return "Data deve estar no formato YYYY-MM-DD!";
        } catch (Exception e) {
            return "Erro ao salvar projeto: " + e.getMessage();
        }
    }

    public Projeto buscarProjeto(String id) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return null;
        }
        return projetoDAO.findById(id.trim());
    }

    public String atualizarProjeto(String id, String nome, String descricao, String dataInicio, String dataTermino, String status, String gerenteId) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return "ID é obrigatório para atualização!";
        }

        Projeto projetoExistente = projetoDAO.findById(id.trim());
        if (projetoExistente == null) {
            return "Projeto não encontrado!";
        }

        try {
            LocalDate inicio = LocalDate.parse(dataInicio.trim());
            LocalDate termino = null;
            if (ValidadorUtil.validarCampoObrigatorio(dataTermino)) {
                termino = LocalDate.parse(dataTermino.trim());
            }
            
            Usuario gerente = null;
            if (ValidadorUtil.validarCampoObrigatorio(gerenteId)) {
                gerente = usuarioDAO.findById(gerenteId.trim());
                if (gerente == null) {
                    return "Gerente não encontrado!";
                }
            }
            
            projetoExistente.setNome(nome.trim());
            projetoExistente.setDescricao(descricao.trim());
            projetoExistente.setDataInicio(inicio);
            projetoExistente.setDataTerminoPrevista(termino);
            projetoExistente.setStatus(status.trim());
            projetoExistente.setGerenteResponsavel(gerente);
            
            projetoDAO.update(projetoExistente);
            return null; // Sucesso
            
        } catch (DateTimeParseException e) {
            return "Data deve estar no formato YYYY-MM-DD!";
        } catch (Exception e) {
            return "Erro ao atualizar projeto: " + e.getMessage();
        }
    }

    public String excluirProjeto(String id) {
        if (!ValidadorUtil.validarCampoObrigatorio(id)) {
            return "ID é obrigatório para exclusão!";
        }

        try {
            projetoDAO.delete(id.trim());
            return null; // Sucesso
        } catch (Exception e) {
            return "Erro ao excluir projeto: " + e.getMessage();
        }
    }

    public List<Projeto> listarTodosProjetos() {
        return projetoDAO.findAll();
    }

    public List<Projeto> listarProjetosPorGerente(String gerenteId) {
        if (!ValidadorUtil.validarCampoObrigatorio(gerenteId)) {
            return List.of();
        }
        return projetoDAO.findByGerente(gerenteId.trim());
    }
}
