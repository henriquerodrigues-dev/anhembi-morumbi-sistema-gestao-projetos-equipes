package view;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {

    public MainFrame() {
        // Configura o título da janela
        setTitle("Sistema de Gestão de Projetos e Equipes");
        
        // Define o tamanho inicial da janela
        setSize(800, 600);
        
        // Define o que acontece quando o usuário clica no 'X'
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Define o layout da janela
        setLayout(new BorderLayout());
        
        // Cria um painel principal
        JPanel mainPanel = new JPanel();
        
        // Adiciona um texto de boas-vindas ao painel
        JLabel welcomeLabel = new JLabel("Bem-vindo ao Sistema de Gestão!");
        mainPanel.add(welcomeLabel);
        
        // Adiciona o painel principal à janela
        add(mainPanel, BorderLayout.CENTER);
        
        // Centraliza a janela na tela
        setLocationRelativeTo(null);
    }
}