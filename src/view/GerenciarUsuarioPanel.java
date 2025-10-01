package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.Ikon;

import dao.UsuarioDAO;
import model.Usuario;

/**
 * Painel para gerenciamento de usuários
 */
public class GerenciarUsuarioPanel extends JPanel {
    
    private JTextField buscaField;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private Consumer<String> toastCallback;
    private Consumer<Usuario> editUserCallback;
    
    public GerenciarUsuarioPanel(Consumer<String> toastCallback, Consumer<Usuario> editCallback) {
        this.toastCallback = toastCallback;
        this.editUserCallback = editCallback;
        initializeComponents();
        setupLayout();
        setupActions();
        refreshUserList();
    }
    
    private void initializeComponents() {
        setBackground(Color.decode("#ECF0F1"));
        setLayout(new BorderLayout());
        
        buscaField = createStyledTextField();
        buscaField.setToolTipText("Buscar por nome, CPF, email ou login");
        
        // Configurar tabela
        String[] colunas = {"ID", "Nome", "CPF", "Email", "Cargo", "Login"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(Color.decode("#34495E"));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Ocultar coluna ID
        userTable.getColumnModel().getColumn(0).setMinWidth(0);
        userTable.getColumnModel().getColumn(0).setMaxWidth(0);
        userTable.getColumnModel().getColumn(0).setWidth(0);
    }
    
    private void setupLayout() {
        // Container principal
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 20, 20);
                
                // Fundo branco
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        mainPanel.setLayout(new BorderLayout());
        
        // Título
        JPanel titlePanel = createTitlePanel();
        
        // Painel de busca
        JPanel searchPanel = createSearchPanel();
        
        // Tabela
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1));
        
        // Botões de ação
        JPanel actionPanel = createActionPanel();
        
        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USERS_COG, 24, Color.decode("#2C3E50"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciar Usuários");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#2C3E50"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        return titlePanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(10, 0, 20, 0));
        
        JLabel searchLabel = new JLabel("Buscar Usuário:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(Color.decode("#2C3E50"));
        
        JPanel searchFieldPanel = new JPanel(new BorderLayout());
        searchFieldPanel.setOpaque(false);
        
        JLabel searchIcon = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.SEARCH, 16, Color.decode("#7F8C8D"));
        searchIcon.setIcon(icon);
        searchIcon.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        searchFieldPanel.add(searchIcon, BorderLayout.WEST);
        searchFieldPanel.add(buscaField, BorderLayout.CENTER);
        
        searchPanel.add(searchLabel, BorderLayout.NORTH);
        searchPanel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        searchPanel.add(searchFieldPanel, BorderLayout.SOUTH);
        
        return searchPanel;
    }
    
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionPanel.setOpaque(false);
        actionPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton editButton = createIconActionButton(FontAwesomeSolid.EDIT, "Editar", Color.decode("#F39C12"));
        JButton deleteButton = createIconActionButton(FontAwesomeSolid.TRASH, "Excluir", Color.decode("#E74C3C"));
        JButton refreshButton = createIconActionButton(FontAwesomeSolid.SYNC_ALT, "Atualizar", Color.decode("#3498DB"));
        
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(refreshButton);
        
        return actionPanel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setPreferredSize(new Dimension(0, 35));
        return field;
    }
    
    private JButton createIconActionButton(Ikon iconCode, String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(bgColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(bgColor.brighter());
                } else {
                    g2d.setColor(bgColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Texto centralizado
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - fm.getDescent();
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                g2d.drawString(getText(), x, y);
            }
        };
        
        FontIcon icon = FontIcon.of(iconCode, 16, Color.WHITE);
        button.setIcon(icon);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(120, 40));
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        
        return button;
    }
    
    private void setupActions() {
        // Busca em tempo real
        buscaField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterUsers(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterUsers(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterUsers(); }
        });
        
        // Ações dos botões
        JPanel actionPanel = (JPanel) ((JPanel) getComponent(0)).getComponent(2);
        JButton editButton = (JButton) actionPanel.getComponent(0);
        JButton deleteButton = (JButton) actionPanel.getComponent(1);
        JButton refreshButton = (JButton) actionPanel.getComponent(2);
        
        editButton.addActionListener(e -> editarUsuario());
        deleteButton.addActionListener(e -> excluirUsuarios());
        refreshButton.addActionListener(e -> refreshUserList());
    }
    
    private void filterUsers() {
        String searchTerm = buscaField.getText().toLowerCase().trim();
        
        if (searchTerm.isEmpty()) {
            refreshUserList();
            return;
        }
        
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.findAll();
            
            // Limpar tabela
            tableModel.setRowCount(0);
            
            // Filtrar e adicionar usuários
            for (Usuario usuario : usuarios) {
                boolean matches = usuario.getNomeCompleto().toLowerCase().contains(searchTerm) ||
                                usuario.getCpf().toLowerCase().contains(searchTerm) ||
                                usuario.getEmail().toLowerCase().contains(searchTerm) ||
                                usuario.getLogin().toLowerCase().contains(searchTerm);
                
                if (matches) {
                    Object[] row = {
                        usuario.getId(),
                        usuario.getNomeCompleto(),
                        usuario.getCpf(),
                        usuario.getEmail(),
                        usuario.getCargo(),
                        usuario.getLogin()
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (Exception ex) {
            showToast("Erro ao filtrar usuários: " + ex.getMessage(), "error");
        }
    }
    
    private void editarUsuario() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            toastCallback.accept("Selecione um usuário para editar");
            return;
        }
        
        if (userTable.getSelectedRowCount() > 1) {
            toastCallback.accept("Selecione apenas um usuário para editar");
            return;
        }
        
        try {
            String userId = (String) tableModel.getValueAt(selectedRow, 0);
            String nome = (String) tableModel.getValueAt(selectedRow, 1);
            String cpf = (String) tableModel.getValueAt(selectedRow, 2);
            String email = (String) tableModel.getValueAt(selectedRow, 3);
            String cargo = (String) tableModel.getValueAt(selectedRow, 4);
            String login = (String) tableModel.getValueAt(selectedRow, 5);
            
            // Buscar senha atual do usuário
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            Usuario usuarioCompleto = usuarioDAO.findById(userId);
            
            if (usuarioCompleto != null && editUserCallback != null) {
                // Usar callback para navegar para cadastro e preencher dados
                editUserCallback.accept(usuarioCompleto);
                toastCallback.accept("Usuário carregado para edição no formulário de cadastro!");
            } else {
                toastCallback.accept("Erro ao carregar dados do usuário");
            }
        } catch (Exception e) {
            toastCallback.accept("Erro ao editar usuário: " + e.getMessage());
        }
    }
    
    private void excluirUsuarios() {
        int[] selectedRows = userTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showToast("Selecione pelo menos um usuário para excluir", "warning");
            return;
        }
        
        String message = selectedRows.length == 1 ? 
            "Tem certeza que deseja excluir este usuário?" :
            "Tem certeza que deseja excluir " + selectedRows.length + " usuários?";
        
        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                int deletedCount = 0;
                
                for (int row : selectedRows) {
                    String userId = (String) tableModel.getValueAt(row, 0);
                    usuarioDAO.delete(userId);
                    deletedCount++;
                }
                
                showToast(deletedCount + " usuário(s) excluído(s) com sucesso!", "success");
                refreshUserList();
                
            } catch (Exception ex) {
                showToast("Erro ao excluir usuário(s): " + ex.getMessage(), "error");
            }
        }
    }
    
    private void refreshUserList() {
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.findAll();
            
            // Limpar tabela
            tableModel.setRowCount(0);
            
            // Adicionar usuários
            for (Usuario usuario : usuarios) {
                Object[] row = {
                    usuario.getId(),
                    usuario.getNomeCompleto(),
                    usuario.getCpf(),
                    usuario.getEmail(),
                    usuario.getCargo(),
                    usuario.getLogin()
                };
                tableModel.addRow(row);
            }
        } catch (Exception ex) {
            showToast("Erro ao carregar usuários: " + ex.getMessage(), "error");
        }
    }
    
    private void showToast(String message, String type) {
        if (toastCallback != null) {
            toastCallback.accept(type + ":" + message);
        }
    }
    
    public void refresh() {
        refreshUserList();
    }
}
