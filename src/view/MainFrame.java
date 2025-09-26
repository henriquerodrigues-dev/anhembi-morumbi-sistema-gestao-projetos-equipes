package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import java.awt.Cursor;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.UIManager;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import java.util.Objects;
import java.awt.FlowLayout;

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
    private JPanel contentPanel;
    private JPanel toastPanel;
    private JPanel cadastroPanel;
    private JPanel listaPanel;

    public MainFrame() {
        // Inicializa as variáveis de instância no construtor
        idField = new JTextField();
        nomeField = new JTextField();
        cpfField = new JTextField();
        emailField = new JTextField();
        cargoField = new JTextField();
        loginField = new JTextField();
        senhaField = new JTextField();

        // Basic frame settings
        setTitle("Sistema de Gestão de Projetos e Equipes");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Global style settings
        UIManager.put("TextField.border", new LineBorder(Color.decode("#BDC3C7"), 1, true));
        UIManager.put("Button.background", Color.decode("#FF6500"));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Sans-serif", Font.BOLD, 14));
        UIManager.put("Button.focusPainted", false);
        UIManager.put("Table.background", Color.decode("#ECF0F1"));
        UIManager.put("Table.gridColor", Color.decode("#BDC3C7"));

        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#0B192C"));

        // Sidebar navigation panel
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(Color.decode("#1E3E62"));
        sidebarPanel.setPreferredSize(new Dimension(200, 800));

        JLabel appTitleLabel = new JLabel("<html><font color='#ECF0F1' size='6'><b>SISTEMA DE GESTÃO</b></font></html>");
        appTitleLabel.setBorder(new EmptyBorder(25, 20, 25, 20));
        appTitleLabel.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        sidebarPanel.add(appTitleLabel);

        JButton cadastroButton = createSidebarButton("➕ Cadastro de Usuário");
        JButton gerenciarUsuariosButton = createSidebarButton("👥 Gerenciar Usuários");
        JButton gerenciarProjetosButton = createSidebarButton("📋 Gerenciar Projetos");

        sidebarPanel.add(cadastroButton);
        sidebarPanel.add(gerenciarUsuariosButton);
        sidebarPanel.add(gerenciarProjetosButton);

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Main content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.decode("#ECF0F1"));
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Criar os painéis de cadastro e listagem uma vez
        cadastroPanel = createCadastroPanel();
        listaPanel = createListaPanel();
        
        add(mainPanel);

        // Initial view
        showPanel(listaPanel);
        refreshUserList();

        // Button actions
        cadastroButton.addActionListener(e -> {
            clearFields();
            showPanel(cadastroPanel);
        });
        gerenciarUsuariosButton.addActionListener(e -> {
            showPanel(listaPanel);
            refreshUserList();
        });
        gerenciarProjetosButton.addActionListener(e -> showToast("Página de Gerenciar Projetos em construção!", Color.decode("#3498DB")));
    }
    
    // Método auxiliar para limpar os campos
    private void clearFields() {
        idField.setText("");
        nomeField.setText("");
        cpfField.setText("");
        emailField.setText("");
        cargoField.setText("");
        loginField.setText("");
        senhaField.setText("");
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(JButton.LEFT_ALIGNMENT);
        button.setBackground(Color.decode("#1E3E62"));
        button.setForeground(Color.decode("#ECF0F1"));
        button.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(15, 30, 15, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.decode("#1E3E62").brighter());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.decode("#1E3E62"));
            }
        });
        return button;
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createCadastroPanel() {
        JPanel cadastroWrapper = new JPanel(new BorderLayout());
        cadastroWrapper.setBackground(Color.decode("#ECF0F1"));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBackground(Color.decode("#ECF0F1"));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Formulário de Cadastro");
        titleLabel.setFont(new Font("Sans-serif", Font.BOLD, 22));
        titleLabel.setForeground(Color.decode("#0B192C"));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        cadastroWrapper.add(titleLabel, BorderLayout.NORTH);
        
        formPanel.add(createIconLabel("ID (para busca, edição ou exclusão):", "🆔"));
        formPanel.add(idField);
        formPanel.add(createIconLabel("Nome Completo:", "👤"));
        formPanel.add(nomeField);
        formPanel.add(createIconLabel("CPF:", "📝"));
        formPanel.add(cpfField);
        formPanel.add(createIconLabel("Email:", "✉️"));
        formPanel.add(emailField);
        formPanel.add(createIconLabel("Cargo:", "💼"));
        formPanel.add(cargoField);
        formPanel.add(createIconLabel("Login:", "🔑"));
        formPanel.add(loginField);
        formPanel.add(createIconLabel("Senha:", "🔒"));
        formPanel.add(senhaField);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBackground(Color.decode("#ECF0F1"));

        JButton saveButton = new JButton("💾 Salvar");
        JButton findButton = new JButton("🔎 Buscar por ID");
        JButton updateButton = new JButton("🔄 Atualizar");
        JButton deleteButton = new JButton("🗑️ Excluir");
        
        styleActionButton(saveButton, Color.decode("#2ECC71"));
        styleActionButton(findButton, Color.decode("#3498DB"));
        styleActionButton(updateButton, Color.decode("#F1C40F"));
        styleActionButton(deleteButton, Color.decode("#E74C3C"));
        
        buttonPanel.add(saveButton);
        buttonPanel.add(findButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        cadastroWrapper.add(formPanel, BorderLayout.CENTER);
        cadastroWrapper.add(buttonPanel, BorderLayout.SOUTH);

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
                showToast("Usuário salvo com sucesso!", Color.decode("#2ECC71"));
                clearFields();
            } catch (Exception ex) {
                showToast("Erro ao salvar usuário: " + ex.getMessage(), Color.decode("#E74C3C"));
            }
        });

        findButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                showToast("Por favor, insira um ID.", Color.decode("#F1C40F"));
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
                showToast("Usuário encontrado com sucesso!", Color.decode("#3498DB"));
            } else {
                showToast("Usuário não encontrado.", Color.decode("#E74C3C"));
            }
        });
        
        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                if (id.isEmpty()) {
                    showToast("Por favor, insira o ID do usuário para atualizar.", Color.decode("#F1C40F"));
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
                showToast("Usuário atualizado com sucesso!", Color.decode("#F1C40F"));
                refreshUserList();
            } catch (Exception ex) {
                showToast("Erro ao atualizar usuário: " + ex.getMessage(), Color.decode("#E74C3C"));
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                showToast("Por favor, insira o ID do usuário para excluir.", Color.decode("#F1C40F"));
                return;
            }
            
            usuarioDAO.delete(id);
            showToast("Usuário excluído com sucesso!", Color.decode("#E74C3C"));
            refreshUserList();
        });
        
        // Centraliza o painel de cadastro
        JPanel centeringPanel = new JPanel();
        centeringPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centeringPanel.setBackground(Color.decode("#ECF0F1"));
        centeringPanel.add(cadastroWrapper);
        
        return centeringPanel;
    }

    private JLabel createIconLabel(String text, String iconText) {
        JLabel label = new JLabel(iconText + " " + text);
        label.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        label.setForeground(Color.decode("#0B192C"));
        return label;
    }

    private JPanel createListaPanel() {
        JPanel listaPanel = new JPanel(new BorderLayout());
        listaPanel.setBackground(Color.decode("#ECF0F1"));
        String[] columnNames = {"ID", "Nome Completo", "Email", "CPF", "Login"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        
        userTable.setFillsViewportHeight(true);
        userTable.setFont(new Font("Sans-serif", Font.PLAIN, 14));
        userTable.getTableHeader().setBackground(Color.decode("#1E3E62"));
        userTable.getTableHeader().setForeground(Color.decode("#ECF0F1"));
        userTable.getTableHeader().setFont(new Font("Sans-serif", Font.BOLD, 14));
        
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
                            
                            showPanel(cadastroPanel);
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
                                showToast("Usuário excluído com sucesso!", Color.decode("#E74C3C"));
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
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(color, 1, true));
        button.setFont(new Font("Sans-serif", Font.BOLD, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
    }

    private void showToast(String message, Color color) {
        if (toastPanel != null) {
            this.remove(toastPanel);
        }

        toastPanel = new JPanel();
        toastPanel.setBackground(color);
        toastPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel toastLabel = new JLabel("<html><font color='#FFFFFF'>" + message + "</font></html>");
        toastLabel.setFont(new Font("Sans-serif", Font.BOLD, 14));
        toastPanel.add(toastLabel);
        
        this.add(toastPanel, BorderLayout.SOUTH);
        this.revalidate();
        this.repaint();
        
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainFrame.this.remove(toastPanel);
                MainFrame.this.revalidate();
                MainFrame.this.repaint();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
}
