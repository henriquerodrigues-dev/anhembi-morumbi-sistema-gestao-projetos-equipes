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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Usuario;
import dao.UsuarioDAO;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;

public class MainFrame extends JFrame {

    private JTextField idField;
    private JTextField nomeField;
    private JTextField cpfField;
    private JTextField emailField;
    private JTextField cargoField;
    private JTextField loginField;
    private JTextField senhaField;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;

    public MainFrame() {
        // Configurações básicas da janela
        setTitle("Sistema de Gestão de Projetos e Equipes");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Configurações de estilo global
        UIManager.put("TextField.border", new LineBorder(Color.decode("#BDC3C7"), 1, true));
        
        // --- Painel principal com layout de borda ---
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#0B192C"));
        
        // --- Painel de navegação lateral (Sidebar) ---
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#1E3E62"));
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        
        JLabel appTitleLabel = new JLabel("<html><font color='#ECF0F1' size='6'><b>SISTEMA DE GESTÃO</b></font></html>");
        appTitleLabel.setBorder(new EmptyBorder(25, 20, 25, 20));
        appTitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        sidebarPanel.add(appTitleLabel);
        
        JButton sidebarButton1 = new JButton("⭐ Dashboard");
        JButton sidebarButton2 = new JButton("👥 Gerenciar Usuários");
        JButton sidebarButton3 = new JButton("📋 Gerenciar Projetos");

        styleSidebarButton(sidebarButton1);
        styleSidebarButton(sidebarButton2);
        styleSidebarButton(sidebarButton3);

        sidebarPanel.add(sidebarButton1);
        sidebarPanel.add(sidebarButton2);
        sidebarPanel.add(sidebarButton3);
        
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // --- Painel de conteúdo com abas ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(Color.decode("#ECF0F1"));
        tabbedPane.setForeground(Color.decode("#0B192C"));
        tabbedPane.setFont(new Font("Sans-serif", Font.BOLD, 14));
        
        JPanel cadastroPanel = createCadastroPanel();
        JPanel listaPanel = createListaPanel();
        
        tabbedPane.addTab("Cadastro", cadastroPanel);
        tabbedPane.addTab("Lista de Usuários", listaPanel);

        sidebarButton2.addActionListener(e -> {
            tabbedPane.setSelectedIndex(1);
            refreshUserList();
        });
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        add(mainPanel);
        
        refreshUserList();

    }
    
    private void styleSidebarButton(JButton button) {
        button.setAlignmentX(JButton.LEFT_ALIGNMENT);
        button.setBackground(Color.decode("#1E3E62"));
        button.setForeground(Color.decode("#ECF0F1"));
        button.setFont(new Font("Sans-serif", Font.PLAIN, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(15, 30, 15, 10));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.decode("#1E3E62").brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.decode("#1E3E62"));
            }
        });
    }

    private JPanel createCadastroPanel() {
        JPanel cadastroPanel = new JPanel();
        cadastroPanel.setLayout(new BoxLayout(cadastroPanel, BoxLayout.Y_AXIS));
        cadastroPanel.setBackground(Color.decode("#ECF0F1"));
        cadastroPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Cadastro de Usuário");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 22));
        titleLabel.setForeground(Color.decode("#0B192C"));
        cadastroPanel.add(titleLabel);
        
        idField = new JTextField();
        nomeField = new JTextField();
        cpfField = new JTextField();
        emailField = new JTextField();
        cargoField = new JTextField();
        loginField = new JTextField();
        senhaField = new JTextField();

        idField.setPreferredSize(new Dimension(300, 35));
        nomeField.setPreferredSize(new Dimension(300, 35));
        cpfField.setPreferredSize(new Dimension(300, 35));
        emailField.setPreferredSize(new Dimension(300, 35));
        cargoField.setPreferredSize(new Dimension(300, 35));
        loginField.setPreferredSize(new Dimension(300, 35));
        senhaField.setPreferredSize(new Dimension(300, 35));
        
        Border roundedBorder = new LineBorder(Color.decode("#BDC3C7"), 1, true);
        idField.setBorder(roundedBorder);
        nomeField.setBorder(roundedBorder);
        cpfField.setBorder(roundedBorder);
        emailField.setBorder(roundedBorder);
        cargoField.setBorder(roundedBorder);
        loginField.setBorder(roundedBorder);
        senhaField.setBorder(roundedBorder);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.decode("#ECF0F1"));
        formPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
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
        
        cadastroPanel.add(formPanel);

        JButton saveButton = new JButton("➕ Salvar");
        JButton findButton = new JButton("🔍 Buscar por ID");
        JButton updateButton = new JButton("✍️ Atualizar");
        JButton deleteButton = new JButton("🗑️ Excluir");

        styleActionButton(saveButton, Color.decode("#2ECC71"));
        styleActionButton(findButton, Color.decode("#3498DB"));
        styleActionButton(updateButton, Color.decode("#F1C40F"));
        styleActionButton(deleteButton, Color.decode("#E74C3C"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode("#ECF0F1"));
        buttonPanel.add(saveButton);
        buttonPanel.add(findButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        cadastroPanel.add(buttonPanel);
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();

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
                refreshUserList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar usuário: " + ex.getMessage());
            }
        });

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
                refreshUserList();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao atualizar usuário: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, insira o ID do usuário para excluir.");
                return;
            }
            
            usuarioDAO.delete(id);
            JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso!");
            refreshUserList();
        });
        
        return cadastroPanel;
    }

    private JPanel createListaPanel() {
        JPanel listaPanel = new JPanel(new BorderLayout());
        listaPanel.setBackground(Color.decode("#ECF0F1"));
        String[] columnNames = {"ID", "Nome Completo", "Email", "CPF", "Login"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        
        userTable.setFillsViewportHeight(true);
        userTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        userTable.setBackground(Color.decode("#ECF0F1"));
        userTable.getTableHeader().setBackground(Color.decode("#1E3E62"));
        userTable.getTableHeader().setForeground(Color.decode("#ECF0F1"));
        
        userTable.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = userTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < userTable.getRowCount()) {
                        userTable.setRowSelectionInterval(row, row);
                    } else {
                        userTable.clearSelection();
                    }
                    
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem editItem = new JMenuItem("✍️ Editar");
                    JMenuItem deleteItem = new JMenuItem("🗑️ Excluir");

                    popupMenu.add(editItem);
                    popupMenu.add(deleteItem);

                    editItem.addActionListener(action -> {
                        int selectedRow = userTable.getSelectedRow();
                        if (selectedRow != -1) {
                            String id = (String) tableModel.getValueAt(selectedRow, 0);
                            String nome = (String) tableModel.getValueAt(selectedRow, 1);
                            String email = (String) tableModel.getValueAt(selectedRow, 2);
                            String cpf = (String) tableModel.getValueAt(selectedRow, 3);
                            String login = (String) tableModel.getValueAt(selectedRow, 4);

                            idField.setText(id);
                            nomeField.setText(nome);
                            emailField.setText(email);
                            cpfField.setText(cpf);
                            loginField.setText(login);
                            
                            tabbedPane.setSelectedIndex(0);
                        }
                    });

                    deleteItem.addActionListener(action -> {
                        int selectedRow = userTable.getSelectedRow();
                        if (selectedRow != -1) {
                            String id = (String) tableModel.getValueAt(selectedRow, 0);
                            int confirm = JOptionPane.showConfirmDialog(
                                MainFrame.this,
                                "Tem certeza que deseja excluir o usuário com ID: " + id + "?",
                                "Confirmar Exclusão",
                                JOptionPane.YES_NO_OPTION);
                            
                            if (confirm == JOptionPane.YES_OPTION) {
                                UsuarioDAO usuarioDAO = new UsuarioDAO();
                                usuarioDAO.delete(id);
                                refreshUserList();
                                JOptionPane.showMessageDialog(MainFrame.this, "Usuário excluído com sucesso!");
                            }
                        }
                    });
                    
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(new LineBorder(Color.decode("#0B192C"), 1, true));
        listaPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshListButton = new JButton("🔄 Atualizar Lista");
        styleActionButton(refreshListButton, Color.decode("#3498DB"));
        listaPanel.add(refreshListButton, BorderLayout.SOUTH);

        refreshListButton.addActionListener(e -> refreshUserList());

        return listaPanel;
    }

    private void refreshUserList() {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        List<Usuario> usuarios = usuarioDAO.findAll();
        
        tableModel.setRowCount(0);
        for (Usuario u : usuarios) {
            Vector<Object> row = new Vector<>();
            row.add(u.getId());
            row.add(u.getNomeCompleto());
            row.add(u.getEmail());
            row.add(u.getCpf());
            row.add(u.getLogin());
            tableModel.addRow(row);
        }
    }
    
    private void styleActionButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(color, 1, true));
        button.setFont(new Font("Sans-serif", Font.BOLD, 14));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }
}
