import util.Database;
import view.MainFrameModular;
import javax.swing.SwingUtilities;

/**
 * Classe principal do Sistema de Gestão de Projetos e Equipes
 * 
 * Este sistema permite o gerenciamento completo de:
 * - Usuários (cadastro, edição, exclusão)
 * - Projetos (com gerentes responsáveis)
 * - Equipes (com gestão de membros)
 * 
 * @author Sistema de Gestão
 * @version 1.0
 */
public class App {
    
    public static void main(String[] args) {
        // Inicializar banco de dados
        inicializarBancoDados();
        
        // Iniciar interface gráfica
        iniciarInterface();
    }
    
    /**
     * Inicializa o banco de dados e cria as tabelas necessárias
     */
    private static void inicializarBancoDados() {
        try {
            Database.createTables();
        } catch (Exception e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            System.exit(1);
        }
    }
    
    /**
     * Inicia a interface gráfica do sistema
     */
    private static void iniciarInterface() {
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrameModular mainFrame = new MainFrameModular();
                mainFrame.setVisible(true);
            } catch (Exception e) {
                System.err.println("Erro ao iniciar interface: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
}