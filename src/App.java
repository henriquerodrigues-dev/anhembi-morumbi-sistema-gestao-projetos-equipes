import dao.UsuarioDAO;
import model.Equipe;
import model.Projeto;
import model.Usuario;
import util.Database;
import view.MainFrame;
import javax.swing.SwingUtilities;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class App {
    public static void main(String[] args) throws Exception {

        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            System.out.println("Erro na configuração de codificação: " + e.getMessage());
        }

        // --- Teste da conexão e criação de tabelas ---
        System.out.println("Tentando conectar com o banco de dados e criar tabelas...");
        Database.createTables();
        System.out.println("Operação de banco de dados concluída.");
        
        System.out.println("\n----------------------------------\n");

        // --- Teste da classe Usuario ---
        Usuario gerente = new Usuario(
            "João da Silva",
            "123.456.789-00",
            "joao.silva@email.com",
            "Gerente",
            "joao.s",
            "senha123"
        );

        Usuario membro = new Usuario(
            "Maria Oliveira",
            "987.654.321-00",
            "maria.o@email.com",
            "Colaborador",
            "maria.o",
            "senha456"
        );

        // Exibindo as informações dos usuários
        System.out.println("Usuário criado:");
        System.out.println("Nome: " + gerente.getNomeCompleto());
        System.out.println("Cargo: " + gerente.getCargo());
        
        System.out.println("\nUsuário criado:");
        System.out.println("Nome: " + membro.getNomeCompleto());
        System.out.println("Cargo: " + membro.getCargo());
        
        // --- Teste da classe Projeto ---
        Projeto projeto = new Projeto(
            "Sistema de Gestão de Equipes",
            "Desenvolvimento de uma ferramenta para gerenciar projetos e equipes.",
            LocalDate.of(2025, 9, 22),
            LocalDate.of(2025, 12, 22),
            gerente
        );

        // Exibindo as informações do projeto
        System.out.println("\nProjeto criado:");
        System.out.println("Nome do Projeto: " + projeto.getNome());
        System.out.println("Gerente Responsável: " + projeto.getGerenteResponsavel().getNomeCompleto());
        
        // --- Teste da classe Equipe ---
        Equipe equipeDev = new Equipe("Equipe de Desenvolvimento", "Equipe responsável pela codificação da solução.");
        
        // Adicionando membros à equipe
        equipeDev.adicionarMembro(gerente);
        equipeDev.adicionarMembro(membro);
        
        // Exibindo as informações da equipe e seus membros
        System.out.println("\nEquipe criada:");
        System.out.println("Nome da Equipe: " + equipeDev.getNome());
        System.out.println("Membros da Equipe:");
        for (Usuario u : equipeDev.getMembros()) {
            System.out.println("- " + u.getNomeCompleto() + " (" + u.getCargo() + ")");
        }

        // --- Teste de persistência de dados ---
        System.out.println("\n----------------------------------\n");
        System.out.println("Tentando salvar usuários no banco de dados...");
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioDAO.create(gerente);
        usuarioDAO.create(membro);

        // --- Teste da GUI ---
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}