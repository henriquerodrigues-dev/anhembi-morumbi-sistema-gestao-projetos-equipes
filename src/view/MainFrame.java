package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
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
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.UIManager;
import java.awt.GridLayout;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.SwingUtilities;
import javax.swing.JLayeredPane;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.GradientPaint;
import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Container;
import java.awt.FontMetrics;
import javax.swing.Box;

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
    private JPanel toastOverlay;
    private java.util.List<String> activeToasts = new java.util.ArrayList<>();
    private JPanel cadastroPanel;
    private JPanel listaPanel;

    public MainFrame() {
        // Inicializa as variáveis de instância no construtor
        initializeFields();

        // Basic frame settings
        setTitle("Sistema de Gestão de Projetos e Equipes");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Global style settings - Design System Colors
        setupGlobalStyles();

        // Main panel with modern gradient background
        JPanel mainPanel = createMainPanel();

        // Modern sidebar navigation panel
        JPanel sidebarPanel = createModernSidebar();

        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Main content panel with transparency
        contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Criar os painéis de cadastro e listagem uma vez
        cadastroPanel = createCadastroPanel();
        listaPanel = createListaPanel();
        
        add(mainPanel);

        // Initial view
        showPanel(listaPanel);
        refreshUserList();

        // Setup button actions
        setupSidebarActions(sidebarPanel);
    }

    private void initializeFields() {
        idField = createStyledTextField();
        nomeField = createStyledTextField();
        cpfField = createStyledTextField();
        emailField = createStyledTextField();
        cargoField = createStyledTextField();
        loginField = createStyledTextField();
        senhaField = createStyledTextField();
    }

    private void setupGlobalStyles() {
        UIManager.put("TextField.border", new CompoundBorder(
            new LineBorder(Color.decode("#BDC3C7"), 1, true),
            new EmptyBorder(8, 12, 8, 12)
        ));
        UIManager.put("Button.background", Color.decode("#FF6500"));
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("Button.focusPainted", false);
        UIManager.put("Table.background", Color.decode("#FFFFFF"));
        UIManager.put("Table.gridColor", Color.decode("#E8EAED"));
        UIManager.put("Table.selectionBackground", Color.decode("#FF6500"));
        UIManager.put("Table.selectionForeground", Color.WHITE);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.decode("#0B192C"));
        field.setBorder(new CompoundBorder(
            new LineBorder(Color.decode("#BDC3C7"), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        return field;
    }

    private JPanel createMainPanel() {
        return new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#0B192C"),
                    getWidth(), getHeight(), Color.decode("#1E3E62")
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Semi-transparent background
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
                g2d.setColor(Color.decode("#ECF0F1"));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JPanel createModernSidebar() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradient background for sidebar
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#1E3E62"),
                    0, getHeight(), Color.decode("#0B192C")
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle shadow effect
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(getWidth() - 2, 0, 2, getHeight());
            }
        };
        
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(280, 800));
        sidebarPanel.setOpaque(false);

        // Modern app title with better typography
        JLabel appTitleLabel = createModernTitle();
        sidebarPanel.add(appTitleLabel);
        
        // Add some spacing
        sidebarPanel.add(createVerticalSpace(20));

        // Modern navigation buttons
        JButton cadastroButton = createModernSidebarButton("👤", "Cadastro de Usuário");
        JButton gerenciarUsuariosButton = createModernSidebarButton("👥", "Gerenciar Usuários");
        JButton gerenciarProjetosButton = createModernSidebarButton("📋", "Gerenciar Projetos");

        sidebarPanel.add(cadastroButton);
        sidebarPanel.add(createVerticalSpace(8));
        sidebarPanel.add(gerenciarUsuariosButton);
        sidebarPanel.add(createVerticalSpace(8));
        sidebarPanel.add(gerenciarProjetosButton);
        
        // Add flexible space at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Store buttons for action setup
        cadastroButton.setName("cadastro");
        gerenciarUsuariosButton.setName("usuarios");
        gerenciarProjetosButton.setName("projetos");

        return sidebarPanel;
    }

    private JLabel createModernTitle() {
        JLabel titleLabel = new JLabel("<html><div style='text-align: center;'>" +
            "<span style='font-size: 18px; font-weight: bold; color: #FFFFFF;'>SISTEMA DE</span><br>" +
            "<span style='font-size: 20px; font-weight: bold; color: #FF6500;'>GESTÃO</span>" +
            "</div></html>");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(30, 20, 20, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE); // Garantir cor branca
        return titleLabel;
    }

    private JPanel createVerticalSpace(int height) {
        JPanel space = new JPanel();
        space.setOpaque(false);
        space.setPreferredSize(new Dimension(0, height));
        space.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        return space;
    }

    private void setupSidebarActions(JPanel sidebarPanel) {
        Component[] components = sidebarPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                String name = button.getName();
                if (name != null) {
                    switch (name) {
                        case "cadastro":
                            button.addActionListener(e -> {
            clearFields();
            showPanel(cadastroPanel);
        });
                            break;
                        case "usuarios":
                            button.addActionListener(e -> {
            showPanel(listaPanel);
            refreshUserList();
        });
                            break;
                        case "projetos":
                            button.addActionListener(e -> showToast("Página de Gerenciar Projetos em construção!", "info"));
                            break;
                    }
                }
            }
        }
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

    private JButton createModernSidebarButton(String icon, String text) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background with rounded corners
                if (getModel().isPressed()) {
                    g2d.setColor(Color.decode("#FF6500"));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 101, 0, 100));
                } else {
                    g2d.setColor(new Color(255, 255, 255, 20));
                }
                
                g2d.fillRoundRect(10, 5, getWidth() - 20, getHeight() - 10, 12, 12);
                
                // Text and icon
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 16));
                
                // Draw icon
                int iconX = 25;
                int iconY = getHeight() / 2 + 5;
                g2d.drawString(icon, iconX, iconY);
                
                // Draw text
                int textX = iconX + 35;
                int textY = getHeight() / 2 + 5;
                g2d.drawString(text, textX, textY);
            }
        };
        
        button.setPreferredSize(new Dimension(260, 50));
        button.setMaximumSize(new Dimension(260, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createCadastroPanel() {
        JPanel cadastroWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Modern card background with shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        cadastroWrapper.setOpaque(false);
        cadastroWrapper.setPreferredSize(new Dimension(700, 600));
        cadastroWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        formPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("📝 Formulário de Cadastro");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.decode("#0B192C"));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
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

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton saveButton = createModernActionButton("💾", "Salvar", Color.decode("#2ECC71"));
        JButton findButton = createModernActionButton("🔍", "Buscar", Color.decode("#3498DB"));
        JButton updateButton = createModernActionButton("🔄", "Atualizar", Color.decode("#F1C40F"));
        JButton deleteButton = createModernActionButton("🗑️", "Excluir", Color.decode("#E74C3C"));
        
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
                showToast("Usuário salvo com sucesso!", "success");
                clearFields();
            } catch (Exception ex) {
                showToast("Erro ao salvar usuário: " + ex.getMessage(), "error");
            }
        });

        findButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                showToast("Por favor, insira um ID para buscar", "warning");
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
                showToast("Usuário encontrado com sucesso!", "info");
            } else {
                showToast("Usuário não encontrado", "error");
            }
        });
        
        updateButton.addActionListener(e -> {
            try {
                String id = idField.getText();
                if (id.isEmpty()) {
                    showToast("Por favor, insira o ID do usuário para atualizar", "warning");
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
                showToast("Usuário atualizado com sucesso!", "success");
                refreshUserList();
            } catch (Exception ex) {
                showToast("Erro ao atualizar usuário: " + ex.getMessage(), "error");
            }
        });

        deleteButton.addActionListener(e -> {
            String id = idField.getText();
            if (id.isEmpty()) {
                showToast("Por favor, insira o ID do usuário para excluir", "warning");
                return;
            }
            
            usuarioDAO.delete(id);
            showToast("Usuário excluído com sucesso!", "success");
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
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.decode("#0B192C"));
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JButton createModernActionButton(String icon, String text, Color baseColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color bgColor = baseColor;
                if (getModel().isPressed()) {
                    bgColor = baseColor.darker();
                } else if (getModel().isRollover()) {
                    bgColor = baseColor.brighter();
                }
                
                // Button background with rounded corners
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Button text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                
                String buttonText = icon + " " + text;
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(buttonText);
                int textHeight = fm.getAscent();
                
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 2;
                
                g2d.drawString(buttonText, x, y);
            }
        };
        
        button.setPreferredSize(new Dimension(120, 45));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return button;
    }

    private JPanel createListaPanel() {
        JPanel listaPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Modern card background with shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(15, 15, getWidth() - 30, getHeight() - 30, 20, 20);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(10, 10, getWidth() - 30, getHeight() - 30, 20, 20);
            }
        };
        listaPanel.setOpaque(false);
        listaPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Modern title for the list
        JLabel titleLabel = new JLabel("👥 Lista de Usuários");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.decode("#0B192C"));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        listaPanel.add(titleLabel, BorderLayout.NORTH);
        
        String[] columnNames = {"ID", "Nome Completo", "Email", "CPF", "Login"};
        tableModel = new DefaultTableModel(columnNames, 0);
        userTable = new JTable(tableModel);
        
        // Modern table styling
        userTable.setFillsViewportHeight(true);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        userTable.setRowHeight(35);
        userTable.setShowGrid(true);
        userTable.setGridColor(Color.decode("#E8EAED"));
        userTable.setSelectionBackground(Color.decode("#FF6500"));
        userTable.setSelectionForeground(Color.WHITE);
        
        // Modern header styling
        userTable.getTableHeader().setBackground(Color.decode("#1E3E62"));
        userTable.getTableHeader().setForeground(Color.decode("#ECF0F1"));
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        userTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
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
                    popupMenu.setBackground(Color.WHITE);
                    popupMenu.setBorder(new LineBorder(Color.decode("#BDC3C7"), 1, true));
                    
                    JMenuItem editItem = new JMenuItem("✏️ Editar Usuário");
                    JMenuItem deleteItem = new JMenuItem("🗑️ Excluir Usuário");
                    
                    // Estilizar itens do menu
                    editItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    editItem.setForeground(Color.decode("#0B192C"));
                    editItem.setBackground(Color.WHITE);
                    editItem.setBorder(new EmptyBorder(8, 15, 8, 15));
                    
                    deleteItem.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    deleteItem.setForeground(Color.decode("#E74C3C"));
                    deleteItem.setBackground(Color.WHITE);
                    deleteItem.setBorder(new EmptyBorder(8, 15, 8, 15));

                    popupMenu.add(editItem);
                    popupMenu.addSeparator();
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
                            String nome = (String) tableModel.getValueAt(selectedRow, 1);
                            
                            // Diálogo de confirmação mais elaborado
                            String[] options = {"🗑️ Sim, Excluir", "❌ Cancelar"};
                            int confirm = JOptionPane.showOptionDialog(
                                MainFrame.this,
                                "⚠️ ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
                                "Deseja realmente excluir o usuário?\n\n" +
                                "👤 Nome: " + nome + "\n" +
                                "🆔 ID: " + id,
                                "🚨 Confirmar Exclusão de Usuário",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[1]
                            );
                            
                            if (confirm == JOptionPane.YES_OPTION) {
                                UsuarioDAO usuarioDAO = new UsuarioDAO();
                                usuarioDAO.delete(id);
                                refreshUserList();
                                showToast("Usuário '" + nome + "' excluído com sucesso!", "success");
                            } else {
                                showToast("Exclusão cancelada pelo usuário", "info");
                            }
                        }
                    });
                    
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.add(scrollPane, BorderLayout.CENTER);
        listaPanel.add(tableContainer, BorderLayout.CENTER);
        
        // Modern refresh button
        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonContainer.setOpaque(false);
        buttonContainer.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JButton refreshListButton = createModernActionButton("🔄", "Atualizar Lista", Color.decode("#3498DB"));
        buttonContainer.add(refreshListButton);
        listaPanel.add(buttonContainer, BorderLayout.SOUTH);

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
    

    private void showToast(String message, String type) {
        // Evitar toasts duplicados
        String toastKey = type + ":" + message;
        if (activeToasts.contains(toastKey)) {
            return;
        }
        activeToasts.add(toastKey);
        
        // Usar SwingUtilities para thread safety
        SwingUtilities.invokeLater(() -> {
            createToastOverlay();
            
            // Criar toast individual
            JPanel singleToast = createSingleToast(message, type, toastKey);
            toastOverlay.add(singleToast);
            
            // Revalidar apenas o overlay
            toastOverlay.revalidate();
            toastOverlay.repaint();
            
            // Timer para remover o toast (não repetitivo)
            Timer removeTimer = new Timer(3000, null);
            removeTimer.addActionListener(e -> {
                removeTimer.stop();
                removeToast(singleToast, toastKey);
            });
            removeTimer.start();
        });
    }

    private void createToastOverlay() {
        if (toastOverlay == null) {
            toastOverlay = new JPanel();
            toastOverlay.setLayout(new BoxLayout(toastOverlay, BoxLayout.Y_AXIS));
            toastOverlay.setOpaque(false);
            toastOverlay.setBounds(0, 0, getWidth(), getHeight());
            
            // Usar JLayeredPane para overlay
            JLayeredPane layeredPane = getLayeredPane();
            layeredPane.add(toastOverlay, JLayeredPane.POPUP_LAYER);
        }
        
        // Atualizar posição do overlay
        if (toastOverlay != null) {
            toastOverlay.setBounds(getWidth() - 370, 20, 350, getHeight() - 40);
        }
    }

    private JPanel createSingleToast(String message, String type, String toastKey) {
        JPanel toastWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo transparente com sombra
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
                
                // Sombra
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
                // Fundo principal
                Color backgroundColor = getToastColor(type);
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 15, 15);
                
                // Borda sutil
                g2d.setColor(backgroundColor.brighter());
                g2d.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 15, 15);
            }
        };
        
        toastWrapper.setLayout(new BorderLayout());
        toastWrapper.setOpaque(false);
        toastWrapper.setPreferredSize(new Dimension(350, 60));
        toastWrapper.setMaximumSize(new Dimension(350, 60));
        toastWrapper.setBorder(new EmptyBorder(12, 20, 12, 20));
        
        // Conteúdo do toast
        JPanel contentPanel = new JPanel(new BorderLayout(12, 0));
        contentPanel.setOpaque(false);
        
        // Ícone
        String iconText = getToastIcon(type);
        JLabel iconLabel = new JLabel(iconText);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        iconLabel.setForeground(Color.WHITE);
        contentPanel.add(iconLabel, BorderLayout.WEST);
        
        // Mensagem
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        messageLabel.setForeground(Color.WHITE);
        contentPanel.add(messageLabel, BorderLayout.CENTER);
        
        // Botão fechar
        JLabel closeButton = new JLabel("X");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> removeToast(toastWrapper, toastKey));
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.LIGHT_GRAY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.WHITE);
            }
        });
        contentPanel.add(closeButton, BorderLayout.EAST);
        
        toastWrapper.add(contentPanel, BorderLayout.CENTER);
        
        return toastWrapper;
    }

    private void removeToast(JPanel toastWrapper, String toastKey) {
        SwingUtilities.invokeLater(() -> {
            if (toastOverlay != null && toastWrapper.getParent() != null) {
                // Remover da lista de toasts ativos
                activeToasts.remove(toastKey);
                
                // Remover o toast
                toastOverlay.remove(toastWrapper);
                
                // Se não há mais toasts, remover o overlay
                if (toastOverlay.getComponentCount() == 0) {
                    getLayeredPane().remove(toastOverlay);
                    toastOverlay = null;
                }
                
                // Revalidar apenas se necessário
                if (toastOverlay != null) {
                    toastOverlay.revalidate();
                    toastOverlay.repaint();
                } else {
                    getLayeredPane().repaint();
                }
            }
        });
    }

    private Color getToastColor(String type) {
        switch (type) {
            case "success": return Color.decode("#2ECC71");
            case "error": return Color.decode("#E74C3C");
            case "warning": return Color.decode("#F1C40F");
            case "info":
            default: return Color.decode("#3498DB");
        }
    }

    private String getToastIcon(String type) {
        switch (type) {
            case "success": return "✅";
            case "error": return "❌";
            case "warning": return "⚠️";
            case "info":
            default: return "ℹ️";
        }
    }
}
