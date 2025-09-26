package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import model.Usuario;
import dao.UsuarioDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

public class MainFrame extends JFrame {

    private JTextArea displayArea;
    private JTextField idField;
    private JTextField nomeField;
    private JTextField cpfField;
    private JTextField emailField;
    private JTextField cargoField;
    private JTextField loginField;
    private JTextField senhaField;

    public MainFrame() {
        // Configurações básicas da janela
        setTitle("Sistema de Gestão de Projetos e Equipes");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Layout principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Título
        JLabel titleLabel = new JLabel("Gestão de Usuários");
        mainPanel.add(titleLabel);
        
        // Formulário
        idField = new JTextField();
        nomeField = new JTextField();
        cpfField = new JTextField();
        emailField = new JTextField();
        cargoField = new JTextField();
        loginField = new JTextField();
        senhaField = new JTextField();

        idField.setPreferredSize(new Dimension(200, 25));
        nomeField.setPreferredSize(new Dimension(200, 25));
        cpfField.setPreferredSize(new Dimension(200, 25));
        emailField.setPreferredSize(new Dimension(200, 25));
        cargoField.setPreferredSize(new Dimension(200, 25));
        loginField.setPreferredSize(new Dimension(200, 25));
        senhaField.setPreferredSize(new Dimension(200, 25));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.add(new JLabel("ID (para busca, edição ou exclusão):"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Nome Completo:"));
        formPanel.add(nomeField);
        formPanel.add(new JLabel("CPF:"));
        formPanel.add(cpfField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(new JLabel("Cargo:"));
        formPanel.add(cargoField);
        formPanel.add(new JLabel("Login:"));
        formPanel.add(loginField);
        formPanel.add(new JLabel("Senha:"));
        formPanel.add(senhaField);
        
        mainPanel.add(formPanel);

        // Botões de ação
        JButton saveButton = new JButton("Salvar");
        JButton findButton = new JButton("Buscar por ID");
        JButton listButton = new JButton("Listar Todos");
        JButton updateButton = new JButton("Atualizar");
        JButton deleteButton = new JButton("Excluir");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(findButton);
        buttonPanel.add(listButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel);
        
        // Área para exibir os usuários
        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        mainPanel.add(scrollPane);

        // Adiciona o painel principal à janela
        add(mainPanel, BorderLayout.CENTER);
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // Ação do botão salvar
        saveButton.addActionListener(e -> {
            try {
                Usuario novoUsuario = new Usuario(
                    nomeField.getText(),
                    cpfField.getText(),
                    emailField.getText(),
                    cargoField.getText(),
                    loginField.getText(),
                    senhaField.getText()
                );
                
                usuarioDAO.create(novoUsuario);
                
                JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!");
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar usuário: " + ex.getMessage());
            }
        });

        // Ação do botão buscar por ID
        findButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira um ID.");
                return;
            }
            Usuario usuario = usuarioDAO.findById(id);
            if (usuario != null) {
                nomeField.setText(usuario.getNomeCompleto());
                cpfField.setText(usuario.getCpf());
                emailField.setText(usuario.getEmail());
                cargoField.setText(usuario.getCargo());
                loginField.setText(usuario.getLogin());
                senhaField.setText(usuario.getSenha());
            } else {
                JOptionPane.showMessageDialog(this, "Usuário não encontrado.");
            }
        });
        
        // Ação do botão listar
        listButton.addActionListener(e -> {
            List<Usuario> usuarios = usuarioDAO.findAll();
            
            displayArea.setText(""); // Limpa a área de texto
            for (Usuario u : usuarios) {
                displayArea.append("ID: " + u.getId() + "\n");
                displayArea.append("Nome: " + u.getNomeCompleto() + "\n");
                displayArea.append("Email: " + u.getEmail() + "\n");
                displayArea.append("----------------------------\n");
            }
        });

        // Ação do botão atualizar
        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                if (id.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Por favor, insira o ID do usuário para atualizar.");
                    return;
                }
                
                Usuario usuarioParaAtualizar = new Usuario(
                    id,
                    nomeField.getText(),
                    cpfField.getText(),
                    emailField.getText(),
                    cargoField.getText(),
                    loginField.getText(),
                    senhaField.getText()
                );
                
                usuarioDAO.update(usuarioParaAtualizar);
                JOptionPane.showMessageDialog(this, "Usuário atualizado com sucesso!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar usuário: " + ex.getMessage());
            }
        });

        // Ação do botão excluir
        deleteButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira o ID do usuário para excluir.");
                return;
            }
            
            usuarioDAO.delete(id);
            JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
        });
    }
}