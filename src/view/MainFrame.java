package view;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.BoxLayout;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Usuario;
import model.Projeto;
import model.Equipe;
import dao.UsuarioDAO;
import dao.ProjetoDAO;
import dao.EquipeDAO;
import util.ValidadorUtil;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JComboBox;
import com.toedter.calendar.JDateChooser;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.awt.Cursor;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
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
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.Ikon;
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
    private JPasswordField senhaField;
    private JButton toggleSenhaButton;
    
    // Campos específicos para projetos
    private JDateChooser dataInicioChooser;
    private JDateChooser dataTerminoChooser;
    private JComboBox<String> statusComboBox;
    private JTextField gerenteSearchField;
    private JPopupMenu gerentePopup;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTable projetoTable;
    private DefaultTableModel projetoTableModel;
    private JPanel contentPanel;
    private JPanel toastOverlay;
    private java.util.List<String> activeToasts = new java.util.ArrayList<>();
    private JPanel cadastroPanel;
    private JPanel listaPanel;
    private JPanel projetosPanel;
    private JPanel equipesPanel;
    private JPanel homePanel;
    private JPanel ajudaPanel;
    private JPanel membrosPanel;
    private UsuarioDAO usuarioDAO;
    private ProjetoDAO projetoDAO;
    private EquipeDAO equipeDAO;

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

        // Initial view - mostrar tela inicial
        homePanel = createHomePanel();
        showPanel(homePanel);

        // Setup button actions
        setupSidebarActions(sidebarPanel);
    }

    private void initializeFields() {
        // Inicializar DAOs
        usuarioDAO = new UsuarioDAO();
        projetoDAO = new ProjetoDAO();
        equipeDAO = new EquipeDAO();
        
        // Inicializar campos
        idField = createStyledTextField();
        nomeField = createStyledTextField();
        cpfField = createStyledTextField();
        emailField = createStyledTextField();
        cargoField = createStyledTextField();
        loginField = createStyledTextField();
        senhaField = createStyledPasswordField();
        
        // Configurar formatação automática do CPF
        configurarFormatacaoCPF();
    }

    private void configurarFormatacaoCPF() {
        cpfField.getDocument().addDocumentListener(new DocumentListener() {
            private boolean isUpdating = false;
            
            @Override
            public void insertUpdate(DocumentEvent e) {
                formatarCPF();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                formatarCPF();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                formatarCPF();
            }
            
            private void formatarCPF() {
                if (isUpdating) return;
                
                SwingUtilities.invokeLater(() -> {
                    isUpdating = true;
                    String text = cpfField.getText();
                    String numbersOnly = text.replaceAll("[^0-9]", "");
                    
                    if (numbersOnly.length() > 11) {
                        numbersOnly = numbersOnly.substring(0, 11);
                    }
                    
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < numbersOnly.length(); i++) {
                        if (i == 3 || i == 6) {
                            formatted.append(".");
                        } else if (i == 9) {
                            formatted.append("-");
                        }
                        formatted.append(numbersOnly.charAt(i));
                    }
                    
                    int caretPosition = cpfField.getCaretPosition();
                    cpfField.setText(formatted.toString());
                    
                    // Ajustar posição do cursor
                    try {
                        cpfField.setCaretPosition(Math.min(caretPosition, formatted.length()));
                    } catch (IllegalArgumentException ex) {
                        // Ignorar erro de posição inválida
                    }
                    
                    isUpdating = false;
                });
            }
        });
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

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        field.setForeground(Color.decode("#0B192C"));
        field.setBorder(new CompoundBorder(
            new LineBorder(Color.decode("#BDC3C7"), 1, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        field.setEchoChar('*');
        return field;
    }

    private JPanel createSenhaPanel() {
        JPanel senhaPanel = new JPanel(new BorderLayout());
        senhaPanel.setOpaque(false);
        
        // Criar botão toggle
        toggleSenhaButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                Color bgColor = Color.decode("#BDC3C7");
                if (getModel().isRollover()) {
                    bgColor = Color.decode("#95A5A6");
                }
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                
                // Ícone
                FontIcon icon = senhaField.getEchoChar() == 0 ? 
                    FontIcon.of(FontAwesomeSolid.EYE_SLASH, 14, Color.decode("#0B192C")) :
                    FontIcon.of(FontAwesomeSolid.EYE, 14, Color.decode("#0B192C"));
                
                int iconX = (getWidth() - 14) / 2;
                int iconY = (getHeight() - 14) / 2;
                icon.paintIcon(this, g2d, iconX, iconY);
            }
        };
        
        toggleSenhaButton.setPreferredSize(new Dimension(40, 40));
        toggleSenhaButton.setContentAreaFilled(false);
        toggleSenhaButton.setBorderPainted(false);
        toggleSenhaButton.setFocusPainted(false);
        toggleSenhaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleSenhaButton.setToolTipText("Mostrar/Ocultar senha");
        
        // Ação do botão
        toggleSenhaButton.addActionListener(e -> {
            if (senhaField.getEchoChar() == 0) {
                senhaField.setEchoChar('*');
            } else {
                senhaField.setEchoChar((char) 0);
            }
            toggleSenhaButton.repaint();
        });
        
        senhaPanel.add(senhaField, BorderLayout.CENTER);
        senhaPanel.add(toggleSenhaButton, BorderLayout.EAST);
        
        return senhaPanel;
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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Solid dark background
                g2d.setColor(Color.decode("#1E3E62"));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Subtle border
                g2d.setColor(Color.decode("#0B192C"));
                g2d.fillRect(getWidth() - 1, 0, 1, getHeight());
            }
        };
        
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        sidebarPanel.setOpaque(false);

        // Compact title
        JLabel titleLabel = new JLabel("SISTEMA DE GESTÃO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(20, 10, 20, 10));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(titleLabel);

        // Navigation buttons with Ikonli icons
        JButton homeButton = createCompactSidebarButton(FontAwesomeSolid.HOME, "Início");
        JButton cadastroButton = createCompactSidebarButton(FontAwesomeSolid.USER_PLUS, "Cadastro de Usuário");
        JButton gerenciarUsuariosButton = createCompactSidebarButton(FontAwesomeSolid.USERS, "Gerenciar Usuários");
        JButton gerenciarProjetosButton = createCompactSidebarButton(FontAwesomeSolid.TASKS, "Gerenciar Projetos");
        JButton gerenciarEquipesButton = createCompactSidebarButton(FontAwesomeSolid.USERS_COG, "Gerenciar Equipes");
        JButton gerenciarMembrosButton = createCompactSidebarButton(FontAwesomeSolid.USER_COG, "Gerenciar Membros");
        JButton ajudaButton = createCompactSidebarButton(FontAwesomeSolid.QUESTION_CIRCLE, "Ajuda");

        sidebarPanel.add(homeButton);
        sidebarPanel.add(cadastroButton);
        sidebarPanel.add(gerenciarUsuariosButton);
        sidebarPanel.add(gerenciarProjetosButton);
        sidebarPanel.add(gerenciarEquipesButton);
        sidebarPanel.add(gerenciarMembrosButton);
        sidebarPanel.add(ajudaButton);
        
        // Add flexible space at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Store buttons for action setup
        homeButton.setName("home");
        cadastroButton.setName("cadastro");
        gerenciarUsuariosButton.setName("usuarios");
        gerenciarProjetosButton.setName("projetos");
        gerenciarEquipesButton.setName("equipes");
        gerenciarMembrosButton.setName("membros");
        ajudaButton.setName("ajuda");

        return sidebarPanel;
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
                        case "home":
                            button.addActionListener(e -> {
                                if (homePanel == null) {
                                    homePanel = createHomePanel();
                                }
                                showPanel(homePanel);
                            });
                            break;
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
                            button.addActionListener(e -> {
                                if (projetosPanel == null) {
                                    projetosPanel = createProjetosPanel();
                                }
                                showPanel(projetosPanel);
                            });
                            break;
                        case "equipes":
                            button.addActionListener(e -> {
                                if (equipesPanel == null) {
                                    equipesPanel = createEquipesPanel();
                                }
                                showPanel(equipesPanel);
                            });
                            break;
                        case "membros":
                            button.addActionListener(e -> {
                                if (membrosPanel == null) {
                                    membrosPanel = createMembrosPanel();
                                }
                                showPanel(membrosPanel);
                            });
                            break;
                        case "ajuda":
                            button.addActionListener(e -> {
                                if (ajudaPanel == null) {
                                    ajudaPanel = createAjudaPanel();
                                }
                                showPanel(ajudaPanel);
                            });
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

    private JButton createCompactSidebarButton(Ikon iconCode, String text) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getModel().isPressed()) {
                    g2d.setColor(Color.decode("#FF6500"));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 101, 0, 80));
                } else {
                    g2d.setColor(new Color(255, 255, 255, 0));
                }
                g2d.fillRoundRect(5, 2, getWidth() - 10, getHeight() - 4, 8, 8);
                
                // Icon
                FontIcon icon = FontIcon.of(iconCode, 16, 
                    getModel().isRollover() ? Color.decode("#FF6500") : Color.WHITE);
                icon.paintIcon(this, g2d, 15, (getHeight() - 16) / 2);
                
                // Text
                g2d.setColor(getModel().isRollover() ? Color.decode("#FF6500") : Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 13));
                FontMetrics fm = g2d.getFontMetrics();
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(text, 40, textY);
            }
        };
        
        // Style button
        button.setPreferredSize(new Dimension(230, 40));
        button.setMaximumSize(new Dimension(230, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        
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

        // Create title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel formIconLabel = new JLabel();
        FontIcon formIcon = FontIcon.of(FontAwesomeSolid.USER_EDIT, 24, Color.decode("#0B192C"));
        formIconLabel.setIcon(formIcon);
        
        JLabel titleText = new JLabel("Formulário de Cadastro");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#0B192C"));
        
        titlePanel.add(formIconLabel);
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        cadastroWrapper.add(titlePanel, BorderLayout.NORTH);
        
        formPanel.add(createFieldLabel("Nome Completo:", FontAwesomeSolid.USER));
        formPanel.add(nomeField);
        formPanel.add(createFieldLabel("CPF:", FontAwesomeSolid.ID_BADGE));
        formPanel.add(cpfField);
        formPanel.add(createFieldLabel("Email:", FontAwesomeSolid.ENVELOPE));
        formPanel.add(emailField);
        formPanel.add(createFieldLabel("Cargo:", FontAwesomeSolid.BRIEFCASE));
        formPanel.add(cargoField);
        formPanel.add(createFieldLabel("Login:", FontAwesomeSolid.USER_CIRCLE));
        formPanel.add(loginField);
        formPanel.add(createFieldLabel("Senha:", FontAwesomeSolid.LOCK));
        formPanel.add(createSenhaPanel());

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton saveButton = createIconActionButton(FontAwesomeSolid.SAVE, "Salvar", Color.decode("#2ECC71"));
        JButton findButton = createIconActionButton(FontAwesomeSolid.SEARCH, "Buscar", Color.decode("#3498DB"));
        JButton updateButton = createIconActionButton(FontAwesomeSolid.EDIT, "Atualizar", Color.decode("#F1C40F"));
        JButton deleteButton = createIconActionButton(FontAwesomeSolid.TRASH, "Excluir", Color.decode("#E74C3C"));
        
        buttonPanel.add(saveButton);
        buttonPanel.add(findButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        
        cadastroWrapper.add(formPanel, BorderLayout.CENTER);
        cadastroWrapper.add(buttonPanel, BorderLayout.SOUTH);

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        saveButton.addActionListener(e -> {
            // Validações básicas
            String nome = nomeField.getText().trim();
            String cpf = cpfField.getText().trim();
            String email = emailField.getText().trim();
            String cargo = cargoField.getText().trim();
            String login = loginField.getText().trim();
            String senha = new String(senhaField.getPassword()).trim();

            if (!ValidadorUtil.validarCampoObrigatorio(nome)) {
                showToast("Nome completo é obrigatório!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarNomeCompleto(nome)) {
                showToast("Nome deve conter pelo menos nome e sobrenome!", "error");
                return;
            }

            if (!ValidadorUtil.validarCampoObrigatorio(cpf)) {
                showToast("CPF é obrigatório!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarCPF(cpf)) {
                showToast("CPF inválido!", "error");
                return;
            }

            if (!ValidadorUtil.validarCampoObrigatorio(email)) {
                showToast("Email é obrigatório!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarEmail(email)) {
                showToast("Email inválido!", "error");
                return;
            }

            if (!ValidadorUtil.validarCampoObrigatorio(cargo)) {
                showToast("Cargo é obrigatório!", "error");
                return;
            }

            if (!ValidadorUtil.validarCampoObrigatorio(login)) {
                showToast("Login é obrigatório!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarLogin(login)) {
                showToast("Login deve ter 3-20 caracteres (letras, números e underscore)!", "error");
                return;
            }

            if (!ValidadorUtil.validarCampoObrigatorio(senha)) {
                showToast("Senha é obrigatória!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarSenha(senha)) {
                showToast("Senha deve ter pelo menos 6 caracteres, com letras e números!", "error");
                return;
            }

            try {
                // Limpar e formatar dados
                String cpfLimpo = ValidadorUtil.limparCPF(cpf);
                
                Usuario novoUsuario = new Usuario(
                    nome,
                    cpfLimpo,
                    email.toLowerCase(),
                    cargo,
                    login.toLowerCase(),
                    senha
                );
                usuarioDAO.create(novoUsuario);
                showToast("Usuário salvo com sucesso!", "success");
                clearFields();
                refreshUserListRealTime();
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
                refreshUserListRealTime();
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
            refreshUserListRealTime();
        });
        
        // Centraliza o painel de cadastro
        JPanel centeringPanel = new JPanel();
        centeringPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centeringPanel.setBackground(Color.decode("#ECF0F1"));
        centeringPanel.add(cadastroWrapper);
        
        return centeringPanel;
    }

    private JLabel createFieldLabel(String text, Ikon iconCode) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setOpaque(false);
        
        // Create icon
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(iconCode, 14, Color.decode("#0B192C"));
        iconLabel.setIcon(icon);
        
        // Create text
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textLabel.setForeground(Color.decode("#0B192C"));
        
        labelPanel.add(iconLabel);
        labelPanel.add(textLabel);
        
        // Return a wrapper label that contains the panel
        JLabel wrapperLabel = new JLabel();
        wrapperLabel.setLayout(new BorderLayout());
        wrapperLabel.add(labelPanel, BorderLayout.WEST);
        wrapperLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        
        return wrapperLabel;
    }

    private JButton createIconActionButton(Ikon iconCode, String text, Color baseColor) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                Color bgColor = baseColor;
                if (getModel().isPressed()) {
                    bgColor = baseColor.darker();
                } else if (getModel().isRollover()) {
                    bgColor = baseColor.brighter();
                }
                
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                
                // Icon
                FontIcon icon = FontIcon.of(iconCode, 14, Color.WHITE);
                int iconX = 15;
                int iconY = (getHeight() - 14) / 2;
                icon.paintIcon(this, g2d, iconX, iconY);
                
                // Text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = iconX + 20;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(text, textX, textY);
            }
        };
        
        // Style button
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
        
        // Modern title for the list with icon
        JPanel listTitlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        listTitlePanel.setOpaque(false);
        
        JLabel listIconLabel = new JLabel();
        FontIcon listIcon = FontIcon.of(FontAwesomeSolid.USERS, 20, Color.decode("#0B192C"));
        listIconLabel.setIcon(listIcon);
        
        JLabel listTitleText = new JLabel("Lista de Usuários");
        listTitleText.setFont(new Font("Segoe UI", Font.BOLD, 24));
        listTitleText.setForeground(Color.decode("#0B192C"));
        
        listTitlePanel.add(listIconLabel);
        listTitlePanel.add(listTitleText);
        listTitlePanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Painel de busca por ID
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel searchLabel = new JLabel("Buscar usuário:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(Color.decode("#0B192C"));
        
        JTextField searchField = createStyledTextField();
        searchField.setPreferredSize(new Dimension(200, 35));
        searchField.setToolTipText("Digite nome, email, CPF ou login para buscar...");
        
        // Adicionar busca em tempo real
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { 
                filtrarUsuariosEmTempoReal(searchField.getText().trim()); 
            }
            @Override
            public void removeUpdate(DocumentEvent e) { 
                filtrarUsuariosEmTempoReal(searchField.getText().trim()); 
            }
            @Override
            public void changedUpdate(DocumentEvent e) { 
                filtrarUsuariosEmTempoReal(searchField.getText().trim()); 
            }
        });
        
        JButton searchButton = createIconActionButton(FontAwesomeSolid.SEARCH, "Buscar", Color.decode("#3498DB"));
        JButton editButton = createIconActionButton(FontAwesomeSolid.EDIT, "Editar", Color.decode("#F39C12"));
        JButton deleteButton = createIconActionButton(FontAwesomeSolid.TRASH, "Excluir", Color.decode("#E74C3C"));
        
        searchPanel.add(searchLabel);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(editButton);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(deleteButton);
        
        // Ações dos botões
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            if (searchTerm.isEmpty()) {
                showToast("Digite algo para buscar", "warning");
                return;
            }
            
            // Apenas destacar na tabela sem carregar para edição
            filtrarUsuariosEmTempoReal(searchTerm);
            showToast("Busca realizada! Use o botão Editar para carregar no formulário.", "info");
        });
        
        // Editar usuário selecionado na tabela (apenas um por vez)
        editButton.addActionListener(e -> editarUsuarioSelecionado());
        
        // Excluir usuários selecionados na tabela (permite múltiplos)
        deleteButton.addActionListener(e -> excluirUsuariosSelecionados());
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(listTitlePanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        
        listaPanel.add(headerPanel, BorderLayout.NORTH);
        
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
                    
                    JMenuItem editItem = new JMenuItem("Editar Usuário");
                    editItem.setIcon(FontIcon.of(FontAwesomeSolid.EDIT, 14, Color.decode("#0B192C")));
                    
                    JMenuItem deleteItem = new JMenuItem("Excluir Usuário");
                    deleteItem.setIcon(FontIcon.of(FontAwesomeSolid.TRASH, 14, Color.decode("#E74C3C")));
                    
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
                            
                            // Diálogo de confirmação
                            String[] options = {"Sim, Excluir", "Cancelar"};
                            int confirm = JOptionPane.showOptionDialog(
                                MainFrame.this,
                                "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
                                "Deseja realmente excluir o usuário?\n\n" +
                                "Nome: " + nome + "\n" +
                                "ID: " + id,
                                "Confirmar Exclusão de Usuário",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE,
                                null,
                                options,
                                options[1]
                            );
                            
                            if (confirm == JOptionPane.YES_OPTION) {
                                UsuarioDAO usuarioDAO = new UsuarioDAO();
                                usuarioDAO.delete(id);
                                refreshUserListRealTime();
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
        
        // Atualização automática em tempo real - botão removido

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
        
        // Ícone usando Ikonli
        JLabel toastIconLabel = new JLabel();
        FontIcon toastIcon = getToastFontIcon(type);
        toastIconLabel.setIcon(toastIcon);
        contentPanel.add(toastIconLabel, BorderLayout.WEST);
        
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

    private FontIcon getToastFontIcon(String type) {
        switch (type) {
            case "success":
                return FontIcon.of(FontAwesomeSolid.CHECK_CIRCLE, 16, Color.WHITE);
            case "error":
                return FontIcon.of(FontAwesomeSolid.TIMES_CIRCLE, 16, Color.WHITE);
            case "warning":
                return FontIcon.of(FontAwesomeSolid.EXCLAMATION_TRIANGLE, 16, Color.WHITE);
            case "info":
            default:
                return FontIcon.of(FontAwesomeSolid.INFO_CIRCLE, 16, Color.WHITE);
        }
    }

    private JPanel createProjetosPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background with shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 15, 15);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.TASKS, 24, Color.decode("#0B192C"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciamento de Projetos");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#0B192C"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Main content panel with two sections
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        
        // Form section
        JPanel formSection = createProjetoFormSection();
        
        // Table section
        JPanel tableSection = createProjetoTableSection();
        
        mainContent.add(formSection, BorderLayout.NORTH);
        mainContent.add(tableSection, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createProjetoFormSection() {
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Form title
        JLabel formTitle = new JLabel("Cadastro/Edição de Projetos");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(Color.decode("#0B192C"));
        formTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        formPanel.setOpaque(false);
        
        // Project fields
        JTextField projetoIdField = createStyledTextField();
        JTextField projetoNomeField = createStyledTextField();
        JTextField projetoDescricaoField = createStyledTextField();
        
        // Date choosers
        dataInicioChooser = new JDateChooser();
        dataInicioChooser.setDateFormatString("dd/MM/yyyy");
        dataInicioChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataInicioChooser.setPreferredSize(new Dimension(0, 35));
        dataInicioChooser.setLocale(new Locale("pt", "BR"));
        dataInicioChooser.getJCalendar().setLocale(new Locale("pt", "BR"));
        
        // Configurar abertura e fechamento automático do calendário
        setupCalendarAutoClose(dataInicioChooser);
        
        dataTerminoChooser = new JDateChooser();
        dataTerminoChooser.setDateFormatString("dd/MM/yyyy");
        dataTerminoChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dataTerminoChooser.setPreferredSize(new Dimension(0, 35));
        dataTerminoChooser.setLocale(new Locale("pt", "BR"));
        dataTerminoChooser.getJCalendar().setLocale(new Locale("pt", "BR"));
        
        // Configurar abertura e fechamento automático do calendário
        setupCalendarAutoClose(dataTerminoChooser);
        
        // Status combo box with default options
        String[] statusOptions = {"", "Pendente", "Em Andamento", "Concluído", "Cancelado", "Pausado"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setEditable(true);
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusComboBox.setPreferredSize(new Dimension(0, 35));
        
        // Manager search field
        gerenteSearchField = createStyledTextField();
        gerenteSearchField.setPreferredSize(new Dimension(0, 35));
        gerenteSearchField.setToolTipText("Digite o nome do gerente para buscar...");
        setupGerenteSearch();
        
        formPanel.add(createFieldLabel("ID do Projeto (para edição):", FontAwesomeSolid.ID_CARD));
        formPanel.add(projetoIdField);
        formPanel.add(createFieldLabel("Nome do Projeto:", FontAwesomeSolid.PROJECT_DIAGRAM));
        formPanel.add(projetoNomeField);
        formPanel.add(createFieldLabel("Descrição:", FontAwesomeSolid.ALIGN_LEFT));
        formPanel.add(projetoDescricaoField);
        formPanel.add(createFieldLabel("Data de Início:", FontAwesomeSolid.CALENDAR_ALT));
        formPanel.add(dataInicioChooser);
        formPanel.add(createFieldLabel("Data de Término:", FontAwesomeSolid.CALENDAR_CHECK));
        formPanel.add(dataTerminoChooser);
        formPanel.add(createFieldLabel("Status:", FontAwesomeSolid.TASKS));
        formPanel.add(statusComboBox);
        formPanel.add(createFieldLabel("Gerente Responsável:", FontAwesomeSolid.USER_TIE));
        formPanel.add(gerenteSearchField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        // Botões com funções separadas e claras
        JButton criarProjetoBtn = createIconActionButton(FontAwesomeSolid.PLUS, "Criar Novo", Color.decode("#27AE60"));
        JButton salvarProjetoBtn = createIconActionButton(FontAwesomeSolid.SAVE, "Salvar", Color.decode("#27AE60"));
        JButton editarProjetoBtn = createIconActionButton(FontAwesomeSolid.EDIT, "Editar", Color.decode("#F39C12"));
        JButton excluirProjetoBtn = createIconActionButton(FontAwesomeSolid.TRASH, "Excluir", Color.decode("#E74C3C"));
        JButton limparProjetoBtn = createIconActionButton(FontAwesomeSolid.ERASER, "Limpar", Color.decode("#95A5A6"));

        // Inicialmente, mostrar apenas Criar e Editar (Salvar aparece após editar)
        buttonPanel.add(criarProjetoBtn);
        buttonPanel.add(editarProjetoBtn);
        buttonPanel.add(excluirProjetoBtn);
        buttonPanel.add(limparProjetoBtn);

        /**
         * Ações dos botões com funções bem definidas
         */
        
        // Criar novo projeto (limpa campos e permite inserção)
        criarProjetoBtn.addActionListener(e -> {
            salvarProjetoModerno(projetoNomeField, projetoDescricaoField);
            refreshProjetoListRealTime();
        });

        // Salvar alterações de um projeto em edição
        salvarProjetoBtn.addActionListener(e -> {
            atualizarProjetoModerno(projetoIdField, projetoNomeField, projetoDescricaoField);
            refreshProjetoListRealTime();
            
            // Voltar ao modo criar após salvar
            buttonPanel.remove(salvarProjetoBtn);
            buttonPanel.add(criarProjetoBtn, 0);
            buttonPanel.revalidate();
            buttonPanel.repaint();
            
            // Limpar campos
            projetoIdField.setText("");
            projetoNomeField.setText("");
            projetoDescricaoField.setText("");
            dataInicioChooser.setDate(null);
            dataTerminoChooser.setDate(null);
            statusComboBox.setSelectedIndex(0);
            gerenteSearchField.setText("");
        });

        // Editar projeto selecionado na tabela
        editarProjetoBtn.addActionListener(e -> editarProjetoSelecionado(
            buttonPanel, criarProjetoBtn, salvarProjetoBtn, 
            projetoIdField, projetoNomeField, projetoDescricaoField
        ));

        // Excluir projetos selecionados na tabela (permite múltiplos)
        excluirProjetoBtn.addActionListener(e -> excluirProjetosSelecionados());

        limparProjetoBtn.addActionListener(e -> {
            projetoIdField.setText("");
            projetoNomeField.setText("");
            projetoDescricaoField.setText("");
            dataInicioChooser.setDate(null);
            dataTerminoChooser.setDate(null);
            statusComboBox.setSelectedIndex(0);
            gerenteSearchField.setText("");
            refreshProjetoListRealTime();
        });
        
        formWrapper.add(formTitle, BorderLayout.NORTH);
        formWrapper.add(formPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);
        
        return formWrapper;
    }
    
    private JPanel createProjetoTableSection() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        
        // Table title
        JLabel tableTitle = new JLabel("Lista de Projetos");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(Color.decode("#0B192C"));
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Create table (usar variáveis de instância)
        String[] colunasProjeto = {"ID", "Nome", "Descrição", "Data Início", "Data Término", "Status", "Gerente"};
        this.projetoTableModel = new DefaultTableModel(colunasProjeto, 0);
        this.projetoTable = new JTable(this.projetoTableModel);
        
        // Style table
        projetoTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        projetoTable.setRowHeight(30);
        projetoTable.setGridColor(Color.decode("#E8EAED"));
        projetoTable.setSelectionBackground(Color.decode("#FF6500"));
        projetoTable.setSelectionForeground(Color.WHITE);
        
        // Header styling
        projetoTable.getTableHeader().setBackground(Color.decode("#1E3E62"));
        projetoTable.getTableHeader().setForeground(Color.decode("#ECF0F1"));
        projetoTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        projetoTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        JScrollPane projetoScrollPane = new JScrollPane(projetoTable);
        projetoScrollPane.setBorder(new LineBorder(Color.decode("#BDC3C7"), 1));
        projetoScrollPane.setPreferredSize(new Dimension(800, 250));
        
        // Add context menu to table
        addProjetoContextMenu(projetoTable, projetoTableModel);
        
        // Refresh button
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshPanel.setOpaque(false);
        refreshPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton refreshProjetosBtn = createIconActionButton(FontAwesomeSolid.SYNC_ALT, "Atualizar Lista", Color.decode("#3498DB"));
        refreshPanel.add(refreshProjetosBtn);
        
        refreshProjetosBtn.addActionListener(e -> refreshProjetoList(projetoTableModel));
        
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(projetoScrollPane, BorderLayout.CENTER);
        tableWrapper.add(refreshPanel, BorderLayout.SOUTH);
        
        // Load initial data
        refreshProjetoList(projetoTableModel);
        
        return tableWrapper;
    }


    private void addProjetoContextMenu(JTable table, DefaultTableModel tableModel) {
        JPopupMenu contextMenu = new JPopupMenu();
        
        JMenuItem editItem = new JMenuItem("Editar Projeto");
        editItem.setIcon(FontIcon.of(FontAwesomeSolid.EDIT, 12, Color.decode("#F39C12")));
        editItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String projetoId = (String) tableModel.getValueAt(selectedRow, 0);
                // Buscar e carregar projeto nos campos preenchendo o ID também
                buscarProjetoParaEdicao(projetoId);
            }
        });
        
        JMenuItem deleteItem = new JMenuItem("Excluir Projeto");
        deleteItem.setIcon(FontIcon.of(FontAwesomeSolid.TRASH, 12, Color.decode("#E74C3C")));
        deleteItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String projetoId = (String) tableModel.getValueAt(selectedRow, 0);
                excluirProjetoModerno(createTextField(projetoId));
            }
        });
        
        contextMenu.add(editItem);
        contextMenu.add(deleteItem);
        
        table.setComponentPopupMenu(contextMenu);
    }

    private void addEquipeContextMenu(JTable table, DefaultTableModel tableModel) {
        JPopupMenu contextMenu = new JPopupMenu();
        
        JMenuItem editItem = new JMenuItem("Editar Equipe");
        editItem.setIcon(FontIcon.of(FontAwesomeSolid.EDIT, 12, Color.decode("#F39C12")));
        editItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String equipeId = (String) tableModel.getValueAt(selectedRow, 0);
                // Buscar e carregar equipe nos campos
                buscarEquipeParaEdicao(equipeId);
            }
        });
        
        JMenuItem deleteItem = new JMenuItem("Excluir Equipe");
        deleteItem.setIcon(FontIcon.of(FontAwesomeSolid.TRASH, 12, Color.decode("#E74C3C")));
        deleteItem.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String equipeId = (String) tableModel.getValueAt(selectedRow, 0);
                excluirEquipeModerno(createTextField(equipeId));
            }
        });
        
        contextMenu.add(editItem);
        contextMenu.add(deleteItem);
        
        table.setComponentPopupMenu(contextMenu);
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        return field;
    }

    // Métodos modernos de lógica para Projetos
    private void salvarProjetoModerno(JTextField nome, JTextField descricao) {
        if (nome.getText().trim().isEmpty()) {
            showToast("Nome do projeto é obrigatório!", "error");
            return;
        }
        
        if (descricao.getText().trim().isEmpty()) {
            showToast("Descrição do projeto é obrigatória!", "error");
            return;
        }
        
        if (dataInicioChooser.getDate() == null) {
            showToast("Data de início é obrigatória!", "error");
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataInicio = sdf.format(dataInicioChooser.getDate());
        String dataTermino = dataTerminoChooser.getDate() != null ? 
                            sdf.format(dataTerminoChooser.getDate()) : "";
        
        String status = statusComboBox.getSelectedItem() != null ? 
                       statusComboBox.getSelectedItem().toString().trim() : "";
        
        if (status.isEmpty()) {
            showToast("Status do projeto é obrigatório!", "error");
            return;
        }
        
        String gerenteId = getManagerIdFromSearchField();
        
        controller.ProjetoController projetoController = new controller.ProjetoController();
        String erro = projetoController.criarProjeto(
            nome.getText().trim(), descricao.getText().trim(), dataInicio,
            dataTermino, status, gerenteId
        );
        
        if (erro == null) {
            showToast("Projeto salvo com sucesso!", "success");
            // Limpar campos
            nome.setText("");
            descricao.setText("");
            dataInicioChooser.setDate(null);
            dataTerminoChooser.setDate(null);
            statusComboBox.setSelectedIndex(0);
            gerenteSearchField.setText("");
            // Atualizar lista em tempo real
            refreshProjetoListRealTime();
        } else {
            showToast(erro, "error");
        }
    }

    private void buscarProjetoModerno(JTextField id, JTextField nome, JTextField descricao) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para busca!", "error");
            return;
        }
        
        controller.ProjetoController projetoController = new controller.ProjetoController();
        Projeto projeto = projetoController.buscarProjeto(id.getText());
        if (projeto != null) {
            if (nome != null) nome.setText(projeto.getNome());
            if (descricao != null) descricao.setText(projeto.getDescricao());
            
            // Set dates
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dataInicioChooser.setDate(sdf.parse(projeto.getDataInicio().toString()));
                if (projeto.getDataTerminoPrevista() != null) {
                    dataTerminoChooser.setDate(sdf.parse(projeto.getDataTerminoPrevista().toString()));
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
            
            // Set status
            statusComboBox.setSelectedItem(projeto.getStatus());
            
            // Set manager
            if (projeto.getGerenteResponsavel() != null) {
                setManagerInSearchField(projeto.getGerenteResponsavel().getId());
            }
            
            showToast("Projeto encontrado!", "success");
        } else {
            showToast("Projeto não encontrado!", "error");
        }
    }

    private void atualizarProjetoModerno(JTextField id, JTextField nome, JTextField descricao) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para atualização!", "error");
            return;
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataInicio = dataInicioChooser.getDate() != null ? 
                           sdf.format(dataInicioChooser.getDate()) : "";
        String dataTermino = dataTerminoChooser.getDate() != null ? 
                            sdf.format(dataTerminoChooser.getDate()) : "";
        
        String status = statusComboBox.getSelectedItem() != null ? 
                       statusComboBox.getSelectedItem().toString().trim() : "";
        
        String gerenteId = getManagerIdFromSearchField();
        
        controller.ProjetoController projetoController = new controller.ProjetoController();
        String erro = projetoController.atualizarProjeto(
            id.getText(), nome.getText(), descricao.getText(), dataInicio,
            dataTermino, status, gerenteId
        );
        
        if (erro == null) {
            showToast("Projeto atualizado com sucesso!", "success");
            refreshProjetoListRealTime();
        } else {
            showToast(erro, "error");
        }
    }

    private void excluirProjetoModerno(JTextField id) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para exclusão!", "error");
            return;
        }
        
        String[] options = {"Sim, Excluir", "Cancelar"};
        int result = JOptionPane.showOptionDialog(
            this,
            "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
            "Deseja realmente excluir este projeto?\n\n" +
            "ID: " + id.getText(),
            "Confirmar Exclusão de Projeto",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]
        );
        
        if (result == JOptionPane.YES_OPTION) {
            controller.ProjetoController projetoController = new controller.ProjetoController();
            String erro = projetoController.excluirProjeto(id.getText());
            if (erro == null) {
                showToast("Projeto excluído com sucesso!", "success");
                id.setText("");
                refreshProjetoListRealTime();
            } else {
                showToast(erro, "error");
            }
        }
    }

    private void refreshProjetoListRealTime() {
        // Find the project table model (assuming it's stored somewhere accessible)
        if (projetosPanel != null) {
            SwingUtilities.invokeLater(() -> {
                // Refresh project list automatically
                Component[] components = findTableModelInPanel(projetosPanel);
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) comp;
                        if (scrollPane.getViewport().getView() instanceof JTable) {
                            JTable table = (JTable) scrollPane.getViewport().getView();
                            if (table.getModel() instanceof DefaultTableModel) {
                                DefaultTableModel model = (DefaultTableModel) table.getModel();
                                refreshProjetoList(model);
                break;
                            }
                        }
                    }
                }
            });
        }
    }

    private Component[] findTableModelInPanel(Container panel) {
        return panel.getComponents();
    }

    // Métodos de lógica para Projetos (antigos - manter para compatibilidade)
    private void salvarProjeto(JTextField nome, JTextField descricao, JTextField dataInicio, 
                              JTextField dataTermino, JTextField status, JTextField gerenteId) {
        controller.ProjetoController projetoController = new controller.ProjetoController();
        String erro = projetoController.criarProjeto(
            nome.getText(), descricao.getText(), dataInicio.getText(),
            dataTermino.getText(), status.getText(), gerenteId.getText()
        );
        
        if (erro == null) {
            showToast("Projeto salvo com sucesso!", "success");
            // Limpar campos
            nome.setText("");
            descricao.setText("");
            dataInicio.setText("");
            dataTermino.setText("");
            status.setText("");
            gerenteId.setText("");
        } else {
            showToast(erro, "error");
        }
    }

    private void buscarProjeto(JTextField id, JTextField nome, JTextField descricao, 
                              JTextField dataInicio, JTextField dataTermino, JTextField status, 
                              JTextField gerenteId) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para busca!", "error");
            return;
        }
        
        controller.ProjetoController projetoController = new controller.ProjetoController();
        Projeto projeto = projetoController.buscarProjeto(id.getText());
        if (projeto != null) {
            nome.setText(projeto.getNome());
            descricao.setText(projeto.getDescricao());
            dataInicio.setText(projeto.getDataInicio().toString());
            dataTermino.setText(projeto.getDataTerminoPrevista() != null ? 
                              projeto.getDataTerminoPrevista().toString() : "");
            status.setText(projeto.getStatus());
            gerenteId.setText(projeto.getGerenteResponsavel() != null ? 
                            projeto.getGerenteResponsavel().getId() : "");
            showToast("Projeto encontrado!", "success");
        } else {
            showToast("Projeto não encontrado!", "error");
        }
    }

    private void atualizarProjeto(JTextField id, JTextField nome, JTextField descricao, 
                                 JTextField dataInicio, JTextField dataTermino, JTextField status, 
                                 JTextField gerenteId) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para atualização!", "error");
            return;
        }
        
        controller.ProjetoController projetoController = new controller.ProjetoController();
        String erro = projetoController.atualizarProjeto(
            id.getText(), nome.getText(), descricao.getText(), dataInicio.getText(),
            dataTermino.getText(), status.getText(), gerenteId.getText()
        );
        
        if (erro == null) {
            showToast("Projeto atualizado com sucesso!", "success");
        } else {
            showToast(erro, "error");
        }
    }

    private void excluirProjeto(JTextField id) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para exclusão!", "error");
            return;
        }
        
        String[] options = {"Sim, Excluir", "Cancelar"};
        int result = JOptionPane.showOptionDialog(
            this,
            "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
            "Deseja realmente excluir este projeto?\n\n" +
            "ID: " + id.getText(),
            "Confirmar Exclusão de Projeto",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]
        );
        
        if (result == JOptionPane.YES_OPTION) {
            controller.ProjetoController projetoController = new controller.ProjetoController();
            String erro = projetoController.excluirProjeto(id.getText());
            if (erro == null) {
                showToast("Projeto excluído com sucesso!", "success");
                id.setText("");
            } else {
                showToast(erro, "error");
            }
        }
    }

    private void refreshProjetoList(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        controller.ProjetoController projetoController = new controller.ProjetoController();
        List<Projeto> projetos = projetoController.listarTodosProjetos();
        
        for (Projeto projeto : projetos) {
            Vector<String> row = new Vector<>();
            row.add(projeto.getId());
            row.add(projeto.getNome());
            row.add(projeto.getDescricao());
            row.add(projeto.getDataInicio().toString());
            row.add(projeto.getDataTerminoPrevista() != null ? 
                   projeto.getDataTerminoPrevista().toString() : "");
            row.add(projeto.getStatus());
            row.add(projeto.getGerenteResponsavel() != null ? 
                   projeto.getGerenteResponsavel().getNomeCompleto() : "");
            tableModel.addRow(row);
        }
    }

    private JPanel createEquipesPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background with shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 15, 15);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USERS_COG, 24, Color.decode("#0B192C"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciamento de Equipes");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#0B192C"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Main content panel with three sections
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        
        // Form section
        JPanel formSection = createEquipeFormSection();
        
        // Member management section
        JPanel memberSection = createMemberManagementPanel();
        
        // Table section
        JPanel tableSection = createEquipeTableSection();
        
        // Top panel with form and member management
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(formSection, BorderLayout.NORTH);
        topPanel.add(memberSection, BorderLayout.CENTER);
        
        mainContent.add(topPanel, BorderLayout.NORTH);
        mainContent.add(tableSection, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(mainContent, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createEquipeFormSection() {
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Form title
        JLabel formTitle = new JLabel("Cadastro/Edição de Equipes");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(Color.decode("#0B192C"));
        formTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Form fields
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        formPanel.setOpaque(false);
        
        // Team fields
        JTextField equipeIdField = createStyledTextField();
        JTextField equipeNomeField = createStyledTextField();
        JTextField equipeDescricaoField = createStyledTextField();
        
        formPanel.add(createFieldLabel("ID da Equipe (para edição):", FontAwesomeSolid.ID_CARD));
        formPanel.add(equipeIdField);
        formPanel.add(createFieldLabel("Nome da Equipe:", FontAwesomeSolid.USERS));
        formPanel.add(equipeNomeField);
        formPanel.add(createFieldLabel("Descrição:", FontAwesomeSolid.ALIGN_LEFT));
        formPanel.add(equipeDescricaoField);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JButton salvarEquipeBtn = createIconActionButton(FontAwesomeSolid.PLUS, "Criar Novo", Color.decode("#27AE60"));
        JButton buscarEquipeBtn = createIconActionButton(FontAwesomeSolid.SEARCH, "Buscar", Color.decode("#3498DB"));
        JButton atualizarEquipeBtn = createIconActionButton(FontAwesomeSolid.EDIT, "Editar/Salvar", Color.decode("#F39C12"));
        JButton excluirEquipeBtn = createIconActionButton(FontAwesomeSolid.TRASH, "Excluir", Color.decode("#E74C3C"));
        JButton limparEquipeBtn = createIconActionButton(FontAwesomeSolid.ERASER, "Limpar", Color.decode("#95A5A6"));

        buttonPanel.add(salvarEquipeBtn);
        buttonPanel.add(buscarEquipeBtn);
        buttonPanel.add(atualizarEquipeBtn);
        buttonPanel.add(excluirEquipeBtn);
        buttonPanel.add(limparEquipeBtn);

        // Button actions
        salvarEquipeBtn.addActionListener(e -> {
            salvarEquipe(equipeNomeField, equipeDescricaoField);
            refreshEquipeListRealTime();
        });

        buscarEquipeBtn.addActionListener(e -> {
            buscarEquipe(equipeIdField, equipeNomeField, equipeDescricaoField);
            refreshEquipeListRealTime();
        });

        atualizarEquipeBtn.addActionListener(e -> {
            atualizarEquipe(equipeIdField, equipeNomeField, equipeDescricaoField);
            refreshEquipeListRealTime();
        });

        excluirEquipeBtn.addActionListener(e -> {
            excluirEquipe(equipeIdField);
            refreshEquipeListRealTime();
        });

        limparEquipeBtn.addActionListener(e -> {
            equipeIdField.setText("");
            equipeNomeField.setText("");
            equipeDescricaoField.setText("");
            refreshEquipeListRealTime();
        });
        
        formWrapper.add(formTitle, BorderLayout.NORTH);
        formWrapper.add(formPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);
        
        return formWrapper;
    }
    
    private JPanel createEquipeTableSection() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        
        // Table title
        JLabel tableTitle = new JLabel("Lista de Equipes");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(Color.decode("#0B192C"));
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Create table
        String[] colunasEquipe = {"ID", "Nome", "Descrição", "Membros"};
        DefaultTableModel equipeTableModel = new DefaultTableModel(colunasEquipe, 0);
        JTable equipeTable = new JTable(equipeTableModel);
        
        // Style table
        equipeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        equipeTable.setRowHeight(30);
        equipeTable.setGridColor(Color.decode("#E8EAED"));
        equipeTable.setSelectionBackground(Color.decode("#FF6500"));
        equipeTable.setSelectionForeground(Color.WHITE);
        
        // Header styling
        equipeTable.getTableHeader().setBackground(Color.decode("#1E3E62"));
        equipeTable.getTableHeader().setForeground(Color.decode("#ECF0F1"));
        equipeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        equipeTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        JScrollPane equipeScrollPane = new JScrollPane(equipeTable);
        equipeScrollPane.setBorder(new LineBorder(Color.decode("#BDC3C7"), 1));
        equipeScrollPane.setPreferredSize(new Dimension(800, 250));
        
        // Add context menu to table
        addEquipeContextMenu(equipeTable, equipeTableModel);
        
        // Refresh button
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        refreshPanel.setOpaque(false);
        refreshPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton refreshEquipesBtn = createIconActionButton(FontAwesomeSolid.SYNC_ALT, "Atualizar Lista", Color.decode("#3498DB"));
        refreshPanel.add(refreshEquipesBtn);
        
        refreshEquipesBtn.addActionListener(e -> refreshEquipeList(equipeTableModel));
        
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(equipeScrollPane, BorderLayout.CENTER);
        tableWrapper.add(refreshPanel, BorderLayout.SOUTH);
        
        // Load initial data
        refreshEquipeList(equipeTableModel);
        
        return tableWrapper;
    }
    
    /**
     * Cria painel melhorado para gerenciamento de membros
     * Interface mais organizada e intuitiva
     */
    private JPanel createMemberManagementPanel() {
        JPanel memberPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background with shadow
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 12, 12);
                
                g2d.setColor(Color.decode("#F8F9FA"));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
            }
        };
        memberPanel.setOpaque(false);
        memberPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USERS_COG, 20, Color.decode("#0B192C"));
        iconLabel.setIcon(icon);
        
        JLabel memberTitle = new JLabel("Gerenciamento de Membros");
        memberTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        memberTitle.setForeground(Color.decode("#0B192C"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(memberTitle);
        titlePanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Form organizado em grid
        JPanel memberForm = new JPanel(new GridLayout(3, 2, 15, 10));
        memberForm.setOpaque(false);
        memberForm.setBorder(new EmptyBorder(10, 0, 15, 0));
        
        JLabel equipeIdLabel = new JLabel("Buscar Equipe:");
        equipeIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JTextField memberEquipeSearchField = createStyledTextField();
        memberEquipeSearchField.setPreferredSize(new Dimension(200, 30));
        memberEquipeSearchField.setToolTipText("Digite o nome da equipe...");
        
        // Popup para sugestões de equipes
        JPopupMenu equipePopup = new JPopupMenu();
        setupEquipeSearchPopup(memberEquipeSearchField, equipePopup);
        
        JLabel usuarioIdLabel = new JLabel("Buscar Usuário:");
        usuarioIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JTextField memberUsuarioSearchField = createStyledTextField();
        memberUsuarioSearchField.setPreferredSize(new Dimension(200, 30));
        memberUsuarioSearchField.setToolTipText("Digite o nome do usuário...");
        
        // Popup para sugestões de usuários
        JPopupMenu usuarioPopup = new JPopupMenu();
        setupUsuarioSearchPopup(memberUsuarioSearchField, usuarioPopup);
        
        JButton adicionarMembroBtn = createIconActionButton(FontAwesomeSolid.USER_PLUS, "Adicionar", Color.decode("#27AE60"));
        JButton removerMembroBtn = createIconActionButton(FontAwesomeSolid.USER_MINUS, "Remover", Color.decode("#E74C3C"));
        
        // Adicionar campos ao grid de forma organizada
        memberForm.add(createFieldLabel("Selecionar Equipe:", FontAwesomeSolid.USERS));
        memberForm.add(memberEquipeSearchField);
        memberForm.add(createFieldLabel("Selecionar Usuário:", FontAwesomeSolid.USER));
        memberForm.add(memberUsuarioSearchField);
        
        // Painel de botões centralizado
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(adicionarMembroBtn);
        buttonPanel.add(removerMembroBtn);
        
        memberForm.add(new JLabel()); // Espaço vazio
        memberForm.add(buttonPanel);
        
        // Button actions
        adicionarMembroBtn.addActionListener(e -> {
            String equipeId = getEquipeIdFromMemberSearch(memberEquipeSearchField.getText());
            String usuarioId = getUserIdFromMemberSearch(memberUsuarioSearchField.getText());
            
            if (equipeId.isEmpty()) {
                showToast("Equipe não encontrada! Digite um nome válido.", "error");
                return;
            }
            
            if (usuarioId.isEmpty()) {
                showToast("Usuário não encontrado! Digite um nome válido.", "error");
                return;
            }
            
            controller.EquipeController equipeController = new controller.EquipeController();
            String erro = equipeController.adicionarMembro(equipeId, usuarioId);
            if (erro == null) {
                showToast("Membro adicionado com sucesso!", "success");
                memberEquipeSearchField.setText("");
                memberUsuarioSearchField.setText("");
                refreshEquipeListRealTime();
                } else {
                showToast(erro, "error");
            }
        });
        
        removerMembroBtn.addActionListener(e -> {
            String equipeId = getEquipeIdFromMemberSearch(memberEquipeSearchField.getText());
            String usuarioId = getUserIdFromMemberSearch(memberUsuarioSearchField.getText());
            
            if (equipeId.isEmpty()) {
                showToast("Equipe não encontrada! Digite um nome válido.", "error");
                return;
            }
            
            if (usuarioId.isEmpty()) {
                showToast("Usuário não encontrado! Digite um nome válido.", "error");
                return;
            }
            
            controller.EquipeController equipeController = new controller.EquipeController();
            String erro = equipeController.removerMembro(equipeId, usuarioId);
            if (erro == null) {
                showToast("Membro removido com sucesso!", "success");
                memberEquipeSearchField.setText("");
                memberUsuarioSearchField.setText("");
                refreshEquipeListRealTime();
            } else {
                showToast(erro, "error");
            }
        });
        
        // Montar painel final
        memberPanel.add(titlePanel, BorderLayout.NORTH);
        memberPanel.add(memberForm, BorderLayout.CENTER);
        
        return memberPanel;
    }

    // Métodos de lógica para Equipes
    private void salvarEquipe(JTextField nome, JTextField descricao) {
        controller.EquipeController equipeController = new controller.EquipeController();
        String erro = equipeController.criarEquipe(nome.getText(), descricao.getText());
        
        if (erro == null) {
            showToast("Equipe salva com sucesso!", "success");
            // Limpar campos
            nome.setText("");
            descricao.setText("");
            // Atualizar lista em tempo real
            refreshEquipeListRealTime();
        } else {
            showToast(erro, "error");
        }
    }

    private void buscarEquipe(JTextField id, JTextField nome, JTextField descricao) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para busca!", "error");
            return;
        }
        
        controller.EquipeController equipeController = new controller.EquipeController();
        Equipe equipe = equipeController.buscarEquipe(id.getText());
        if (equipe != null) {
            nome.setText(equipe.getNome());
            descricao.setText(equipe.getDescricao());
            showToast("Equipe encontrada!", "success");
        } else {
            showToast("Equipe não encontrada!", "error");
        }
    }

    private void atualizarEquipe(JTextField id, JTextField nome, JTextField descricao) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para atualização!", "error");
            return;
        }
        
        controller.EquipeController equipeController = new controller.EquipeController();
        String erro = equipeController.atualizarEquipe(id.getText(), nome.getText(), descricao.getText());
        
        if (erro == null) {
            showToast("Equipe atualizada com sucesso!", "success");
            refreshEquipeListRealTime();
        } else {
            showToast(erro, "error");
        }
    }

    private void excluirEquipe(JTextField id) {
        if (id.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para exclusão!", "error");
            return;
        }
        
        String[] options = {"Sim, Excluir", "Cancelar"};
        int result = JOptionPane.showOptionDialog(
            this,
            "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
            "Deseja realmente excluir esta equipe?\n\n" +
            "ID: " + id.getText(),
            "Confirmar Exclusão de Equipe",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]
        );
        
        if (result == JOptionPane.YES_OPTION) {
            controller.EquipeController equipeController = new controller.EquipeController();
            String erro = equipeController.excluirEquipe(id.getText());
            if (erro == null) {
                showToast("Equipe excluída com sucesso!", "success");
                id.setText("");
                refreshEquipeListRealTime();
            } else {
                showToast(erro, "error");
            }
        }
    }

    private void refreshEquipeList(DefaultTableModel tableModel) {
        tableModel.setRowCount(0);
        controller.EquipeController equipeController = new controller.EquipeController();
        List<Equipe> equipes = equipeController.listarTodasEquipes();
        
        for (Equipe equipe : equipes) {
            Vector<String> row = new Vector<>();
            row.add(equipe.getId());
            row.add(equipe.getNome());
            row.add(equipe.getDescricao());
            row.add(String.valueOf(equipe.getMembros().size()) + " membros");
            tableModel.addRow(row);
        }
    }

    private void refreshEquipeListRealTime() {
        if (equipesPanel != null) {
            SwingUtilities.invokeLater(() -> {
                Component[] components = findTableModelInPanel(equipesPanel);
                for (Component comp : components) {
                    if (comp instanceof JScrollPane) {
                        JScrollPane scrollPane = (JScrollPane) comp;
                        if (scrollPane.getViewport().getView() instanceof JTable) {
                            JTable table = (JTable) scrollPane.getViewport().getView();
                            if (table.getModel() instanceof DefaultTableModel) {
                                DefaultTableModel model = (DefaultTableModel) table.getModel();
                                refreshEquipeList(model);
                                break;
                            }
                        }
                    }
                }
            });
        }
    }

    private void refreshUserListRealTime() {
        SwingUtilities.invokeLater(() -> {
            refreshUserList();
        });
    }

    // Método de busca universal de usuários
    private Usuario buscarUsuarioUniversal(String searchTerm) {
        List<Usuario> usuarios = usuarioDAO.findAll();
        
        for (Usuario usuario : usuarios) {
            // Busca por ID exato
            if (usuario.getId().equals(searchTerm)) {
                return usuario;
            }
            
            // Busca aproximada em nome, email, CPF e login
            String termo = searchTerm.toLowerCase();
            if (usuario.getNomeCompleto().toLowerCase().contains(termo) ||
                usuario.getEmail().toLowerCase().contains(termo) ||
                usuario.getCpf().replaceAll("[^0-9]", "").contains(termo.replaceAll("[^0-9]", "")) ||
                usuario.getLogin().toLowerCase().contains(termo)) {
                return usuario;
            }
        }
        
        return null;
    }

    // Método para buscar equipe para edição
    private void buscarEquipeParaEdicao(String equipeId) {
        controller.EquipeController equipeController = new controller.EquipeController();
        Equipe equipe = equipeController.buscarEquipe(equipeId);
        
        if (equipe != null) {
            // Encontrar os campos do formulário de equipe
            try {
                // Procurar por campos de texto que possam ser de equipe
                buscarEPreencherCamposEquipe(equipeId, equipe.getNome(), equipe.getDescricao());
                showToast("Equipe carregada para edição!", "success");
            } catch (Exception e) {
                showToast("Erro ao carregar equipe: " + e.getMessage(), "error");
            }
        } else {
            showToast("Equipe não encontrada!", "error");
        }
    }

    private void buscarEPreencherCamposEquipe(String id, String nome, String descricao) {
        // Este método será chamado quando o formulário de equipe estiver visível
        // Por enquanto, vamos simular a busca
        SwingUtilities.invokeLater(() -> {
            showToast("ID: " + id + " | Nome: " + nome + " | Descrição: " + descricao, "info");
        });
    }

    // Método moderno para excluir equipe
    private void excluirEquipeModerno(JTextField idField) {
        if (idField.getText().trim().isEmpty()) {
            showToast("ID é obrigatório para exclusão!", "error");
            return;
        }
        
        String[] options = {"Sim, Excluir", "Cancelar"};
        int result = JOptionPane.showOptionDialog(
            this,
            "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" +
            "Deseja realmente excluir esta equipe?\n\n" +
            "ID: " + idField.getText(),
            "Confirmar Exclusão de Equipe",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]
        );
        
        if (result == JOptionPane.YES_OPTION) {
            controller.EquipeController equipeController = new controller.EquipeController();
            String erro = equipeController.excluirEquipe(idField.getText());
            if (erro == null) {
                showToast("Equipe excluída com sucesso!", "success");
                idField.setText("");
                refreshEquipeListRealTime();
            } else {
                showToast(erro, "error");
            }
        }
    }

    // Configurar busca de gerente em tempo real
    private void setupGerenteSearch() {
        gerentePopup = new JPopupMenu();
        
        gerenteSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchGerente(); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchGerente(); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchGerente(); }
        });
    }

    private void searchGerente() {
        String searchTerm = gerenteSearchField.getText().toLowerCase().trim();
        
        gerentePopup.removeAll();
        
        if (searchTerm.length() < 2) {
            gerentePopup.setVisible(false);
            return;
        }
        
        List<Usuario> usuarios = usuarioDAO.findAll();
        boolean found = false;
        
        for (Usuario usuario : usuarios) {
            if (usuario.getNomeCompleto().toLowerCase().contains(searchTerm)) {
                JMenuItem item = new JMenuItem(usuario.getNomeCompleto() + " (ID: " + usuario.getId() + ")");
                item.addActionListener(e -> {
                    gerenteSearchField.setText(usuario.getNomeCompleto() + " (ID: " + usuario.getId() + ")");
                    gerentePopup.setVisible(false);
                });
                gerentePopup.add(item);
                found = true;
                
                // Limitar a 5 resultados
                if (gerentePopup.getComponentCount() >= 5) break;
            }
        }
        
        if (found) {
            gerentePopup.show(gerenteSearchField, 0, gerenteSearchField.getHeight());
                } else {
            gerentePopup.setVisible(false);
        }
    }

    private String getManagerIdFromSearchField() {
        String text = gerenteSearchField.getText();
        if (text.contains("(ID: ")) {
            int start = text.lastIndexOf("(ID: ") + 5;
            int end = text.lastIndexOf(")");
            if (start > 4 && end > start) {
                return text.substring(start, end);
            }
        }
        return "";
    }

    private void setManagerInSearchField(String managerId) {
        if (managerId == null || managerId.trim().isEmpty()) {
            gerenteSearchField.setText("");
            return;
        }
        
        List<Usuario> usuarios = usuarioDAO.findAll();
        for (Usuario usuario : usuarios) {
            if (usuario.getId().equals(managerId)) {
                gerenteSearchField.setText(usuario.getNomeCompleto() + " (ID: " + usuario.getId() + ")");
                return;
            }
        }
    }

    private String getUserIdFromMemberSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return "";
        }
        
        // Buscar usuário pelo nome
        Usuario usuario = buscarUsuarioUniversal(searchText.trim());
        if (usuario != null) {
            return usuario.getId();
        }
        
        return "";
    }

    // Método para buscar projeto e preencher todos os campos incluindo ID
    private void buscarProjetoParaEdicao(String projetoId) {
        controller.ProjetoController projetoController = new controller.ProjetoController();
        Projeto projeto = projetoController.buscarProjeto(projetoId);
        
        if (projeto != null) {
            // Criar campos temporários para preencher
            JTextField tempIdField = createTextField(projetoId);
            JTextField tempNomeField = createTextField("");
            JTextField tempDescricaoField = createTextField("");
            
            // Buscar e preencher os dados
            buscarProjetoModerno(tempIdField, tempNomeField, tempDescricaoField);
            
            // Agora temos que encontrar os campos reais no formulário
            preencherCamposProjetoReal(projetoId, projeto);
            
            showToast("Projeto carregado para edição!", "success");
        } else {
            showToast("Projeto não encontrado!", "error");
        }
    }

    private void preencherCamposProjetoReal(String projetoId, Projeto projeto) {
        SwingUtilities.invokeLater(() -> {
            // Simular preenchimento dos campos - o ID deve ser preenchido nos campos reais
            showToast("Projeto ID " + projetoId + " pronto para edição", "info");
        });
    }

    // Método para buscar equipe por nome
    private String getEquipeIdFromMemberSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            return "";
        }
        
        // Buscar equipe pelo nome
        controller.EquipeController equipeController = new controller.EquipeController();
        List<Equipe> equipes = equipeController.listarTodasEquipes();
        
        for (Equipe equipe : equipes) {
            if (equipe.getNome().toLowerCase().contains(searchText.toLowerCase().trim())) {
                return equipe.getId();
            }
        }
        
        return "";
    }

    /**
     * Filtra usuários na tabela em tempo real conforme o usuário digita
     * Busca por: nome, email, CPF (apenas números) e login
     */
    private void filtrarUsuariosEmTempoReal(String searchTerm) {
        SwingUtilities.invokeLater(() -> {
            // Limpar tabela
            tableModel.setRowCount(0);
            
            // Se campo vazio, mostrar todos os usuários
            if (searchTerm.isEmpty()) {
                refreshUserList();
                return;
            }
            
            // Buscar usuários que correspondem ao termo
            List<Usuario> usuarios = usuarioDAO.findAll();
            List<Usuario> usuariosEncontrados = buscarUsuariosPorTermo(usuarios, searchTerm);
            
            // Adicionar usuários encontrados à tabela
            if (usuariosEncontrados.isEmpty()) {
                adicionarLinhaUsuarioNaoEncontrado();
            } else {
                for (Usuario usuario : usuariosEncontrados) {
                    adicionarUsuarioNaTabela(usuario);
                }
            }
        });
    }

    /**
     * Busca usuários que correspondem ao termo de pesquisa
     */
    private List<Usuario> buscarUsuariosPorTermo(List<Usuario> usuarios, String searchTerm) {
        List<Usuario> encontrados = new ArrayList<>();
        String termo = searchTerm.toLowerCase();
        
        for (Usuario usuario : usuarios) {
            if (usuarioCorrespondeAoTermo(usuario, termo)) {
                encontrados.add(usuario);
            }
        }
        return encontrados;
    }

    /**
     * Verifica se o usuário corresponde ao termo de busca
     */
    private boolean usuarioCorrespondeAoTermo(Usuario usuario, String termo) {
        return usuario.getNomeCompleto().toLowerCase().contains(termo) ||
               usuario.getEmail().toLowerCase().contains(termo) ||
               usuario.getCpf().replaceAll("[^0-9]", "").contains(termo.replaceAll("[^0-9]", "")) ||
               usuario.getLogin().toLowerCase().contains(termo);
    }

    /**
     * Adiciona um usuário na tabela
     */
    private void adicionarUsuarioNaTabela(Usuario usuario) {
        Vector<Object> row = new Vector<>();
        row.add(usuario.getId());
        row.add(usuario.getNomeCompleto());
        row.add(usuario.getEmail());
        row.add(usuario.getCpf());
        row.add(usuario.getLogin());
        tableModel.addRow(row);
    }

    /**
     * Adiciona linha informativa quando nenhum usuário é encontrado
     */
    private void adicionarLinhaUsuarioNaoEncontrado() {
        Vector<Object> row = new Vector<>();
        row.add("-");
        row.add("Nenhum usuário encontrado");
        row.add("-");
        row.add("-");
        row.add("-");
        tableModel.addRow(row);
    }

    /**
     * Configura busca em tempo real no campo de pesquisa
     */
    private void setupBuscaTempoReal(JTextField searchField) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { 
                filtrarUsuariosEmTempoReal(searchField.getText()); 
            }
            @Override
            public void removeUpdate(DocumentEvent e) { 
                filtrarUsuariosEmTempoReal(searchField.getText()); 
            }
            @Override
            public void changedUpdate(DocumentEvent e) { 
                filtrarUsuariosEmTempoReal(searchField.getText()); 
            }
        });
    }

    /**
     * Edita o usuário selecionado na tabela (apenas um por vez)
     */
    private void editarUsuarioSelecionado() {
        int selectedRow = userTable.getSelectedRow();
        
        if (selectedRow == -1) {
            showToast("Selecione um usuário na tabela para editar", "warning");
            return;
        }
        
        // Verificar se é uma linha válida (não é a linha "nenhum usuário encontrado")
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        if (userId.equals("-")) {
            showToast("Selecione um usuário válido para editar", "warning");
            return;
        }
        
        // Buscar usuário pelo ID
        Usuario usuario = usuarioDAO.findById(userId);
        if (usuario != null) {
            // Preencher campos no formulário de cadastro
            idField.setText(usuario.getId());
            nomeField.setText(usuario.getNomeCompleto());
            cpfField.setText(ValidadorUtil.formatarCPF(usuario.getCpf()));
            emailField.setText(usuario.getEmail());
            cargoField.setText(usuario.getCargo());
            loginField.setText(usuario.getLogin());
            senhaField.setText(usuario.getSenha());
            
            // Ir para o painel de cadastro
            showPanel(cadastroPanel);
            showToast("Usuário carregado para edição!", "success");
        } else {
            showToast("Erro ao carregar usuário!", "error");
        }
    }

    /**
     * Exclui os usuários selecionados na tabela (permite múltiplos)
     */
    private void excluirUsuariosSelecionados() {
        int[] selectedRows = userTable.getSelectedRows();
        
        if (selectedRows.length == 0) {
            showToast("Selecione um ou mais usuários na tabela para excluir", "warning");
            return;
        }
        
        // Coletar IDs válidos dos usuários selecionados
        List<String> idsParaExcluir = new ArrayList<>();
        List<String> nomesParaExcluir = new ArrayList<>();
        
        for (int row : selectedRows) {
            String userId = (String) tableModel.getValueAt(row, 0);
            String userName = (String) tableModel.getValueAt(row, 1);
            
            // Verificar se é uma linha válida
            if (!userId.equals("-")) {
                idsParaExcluir.add(userId);
                nomesParaExcluir.add(userName);
            }
        }
        
        if (idsParaExcluir.isEmpty()) {
            showToast("Nenhum usuário válido selecionado", "warning");
            return;
        }
        
        // Confirmar exclusão
        String mensagem = idsParaExcluir.size() == 1 ? 
            "Deseja realmente excluir o usuário?\n\n" + nomesParaExcluir.get(0) :
            "Deseja realmente excluir " + idsParaExcluir.size() + " usuários?\n\n" + 
            String.join("\n", nomesParaExcluir);
        
        String[] options = {"Sim, Excluir", "Cancelar"};
        int result = JOptionPane.showOptionDialog(
            this,
            "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" + mensagem,
            "Confirmar Exclusão de Usuário(s)",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Excluir usuários
            int excluidos = 0;
            for (String userId : idsParaExcluir) {
                try {
                    usuarioDAO.delete(userId);
                    excluidos++;
                } catch (Exception e) {
                    showToast("Erro ao excluir usuário ID: " + userId, "error");
                }
            }
            
            // Atualizar lista e mostrar resultado
            refreshUserListRealTime();
            if (excluidos > 0) {
                String mensagemSucesso = excluidos == 1 ? 
                    "Usuário excluído com sucesso!" :
                    excluidos + " usuários excluídos com sucesso!";
                showToast(mensagemSucesso, "success");
            }
        }
    }

    /**
     * Edita o projeto selecionado na tabela (apenas um por vez)
     */
    private void editarProjetoSelecionado(JPanel buttonPanel, JButton criarBtn, JButton salvarBtn,
                                         JTextField idField, JTextField nomeField, JTextField descricaoField) {
        // Verificar se há uma tabela de projetos e se há seleção
        if (projetoTable == null) {
            showToast("Tabela de projetos não encontrada", "error");
            return;
        }
        
        int selectedRow = projetoTable.getSelectedRow();
        if (selectedRow == -1) {
            showToast("Selecione um projeto na tabela para editar", "warning");
            return;
        }
        
        // Obter dados do projeto selecionado
        String projetoId = (String) projetoTableModel.getValueAt(selectedRow, 0);
        String projetoNome = (String) projetoTableModel.getValueAt(selectedRow, 1);
        String projetoDescricao = (String) projetoTableModel.getValueAt(selectedRow, 2);
        
        // Verificar se é uma linha válida
        if (projetoId == null || projetoId.equals("-")) {
            showToast("Selecione um projeto válido para editar", "warning");
            return;
        }
        
        // Preencher campos do formulário
        idField.setText(projetoId);
        nomeField.setText(projetoNome);
        descricaoField.setText(projetoDescricao);
        
        // Buscar dados completos do projeto
        controller.ProjetoController projetoController = new controller.ProjetoController();
        Projeto projeto = projetoController.buscarProjeto(projetoId);
        
        if (projeto != null) {
            // Preencher campos de data e outros
            if (projeto.getDataInicio() != null) {
                // Converter LocalDate para Date
                java.util.Date dataInicio = java.sql.Date.valueOf(projeto.getDataInicio());
                dataInicioChooser.setDate(dataInicio);
            }
            if (projeto.getDataTerminoPrevista() != null) {
                // Converter LocalDate para Date
                java.util.Date dataTermino = java.sql.Date.valueOf(projeto.getDataTerminoPrevista());
                dataTerminoChooser.setDate(dataTermino);
            }
            if (projeto.getStatus() != null) {
                statusComboBox.setSelectedItem(projeto.getStatus());
            }
            if (projeto.getGerenteResponsavel() != null) {
                // Usar o gerente responsável diretamente
                gerenteSearchField.setText(projeto.getGerenteResponsavel().getNomeCompleto());
            }
        }
        
        // Trocar botão Criar por Salvar
        buttonPanel.remove(criarBtn);
        buttonPanel.add(salvarBtn, 0);
        buttonPanel.revalidate();
        buttonPanel.repaint();
        
        showToast("Projeto carregado para edição! Use 'Salvar' para confirmar alterações.", "success");
    }

    /**
     * Exclui os projetos selecionados na tabela (permite múltiplos)
     */
    private void excluirProjetosSelecionados() {
        if (projetoTable == null) {
            showToast("Tabela de projetos não encontrada", "error");
            return;
        }
        
        int[] selectedRows = projetoTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showToast("Selecione um ou mais projetos na tabela para excluir", "warning");
            return;
        }
        
        // Coletar IDs e nomes dos projetos selecionados
        List<String> idsParaExcluir = new ArrayList<>();
        List<String> nomesParaExcluir = new ArrayList<>();
        
        for (int row : selectedRows) {
            String projetoId = (String) projetoTableModel.getValueAt(row, 0);
            String projetoNome = (String) projetoTableModel.getValueAt(row, 1);
            
            if (projetoId != null && !projetoId.equals("-")) {
                idsParaExcluir.add(projetoId);
                nomesParaExcluir.add(projetoNome);
            }
        }
        
        if (idsParaExcluir.isEmpty()) {
            showToast("Nenhum projeto válido selecionado", "warning");
            return;
        }
        
        // Confirmar exclusão
        String mensagem = idsParaExcluir.size() == 1 ? 
            "Deseja realmente excluir o projeto?\n\n" + nomesParaExcluir.get(0) :
            "Deseja realmente excluir " + idsParaExcluir.size() + " projetos?\n\n" + 
            String.join("\n", nomesParaExcluir);
        
        String[] options = {"Sim, Excluir", "Cancelar"};
        int result = JOptionPane.showOptionDialog(
            this,
            "ATENÇÃO: Esta ação não pode ser desfeita!\n\n" + mensagem,
            "Confirmar Exclusão de Projeto(s)",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE,
            null,
            options,
            options[1]
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // Excluir projetos
            controller.ProjetoController projetoController = new controller.ProjetoController();
            int excluidos = 0;
            
            for (String projetoId : idsParaExcluir) {
                try {
                    projetoController.excluirProjeto(projetoId);
                    excluidos++;
                } catch (Exception e) {
                    showToast("Erro ao excluir projeto ID: " + projetoId, "error");
                }
            }
            
            // Atualizar lista e mostrar resultado
            refreshProjetoListRealTime();
            if (excluidos > 0) {
                String mensagemSucesso = excluidos == 1 ? 
                    "Projeto excluído com sucesso!" :
                    excluidos + " projetos excluídos com sucesso!";
                showToast(mensagemSucesso, "success");
            }
        }
    }

    /**
     * Configura abertura e fechamento automático do calendário
     */
    private void setupCalendarAutoClose(JDateChooser dateChooser) {
        // Abrir calendário automaticamente ao ganhar foco
        dateChooser.getDateEditor().getUiComponent().addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    dateChooser.getCalendarButton().doClick();
                });
            }
            @Override
            public void focusLost(FocusEvent e) {
                // Fechar calendário ao perder foco
                SwingUtilities.invokeLater(() -> {
                    if (dateChooser.getJCalendar().isVisible()) {
                        dateChooser.getJCalendar().setVisible(false);
                    }
                });
            }
        });
        
        // Adicionar listener para fechar ao clicar fora
        dateChooser.getJCalendar().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                // Verificar se o mouse saiu da área do calendário
                if (!dateChooser.getJCalendar().getBounds().contains(e.getPoint())) {
                    SwingUtilities.invokeLater(() -> {
                        dateChooser.getJCalendar().setVisible(false);
                    });
                }
            }
        });
    }

    // Configurar busca de equipe em tempo real para membros
    private void setupEquipeSearchPopup(JTextField searchField, JPopupMenu popup) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchEquipeForMember(searchField, popup); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchEquipeForMember(searchField, popup); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchEquipeForMember(searchField, popup); }
        });
    }

    private void searchEquipeForMember(JTextField searchField, JPopupMenu popup) {
        String searchTerm = searchField.getText().toLowerCase().trim();
        
        popup.removeAll();
        
        if (searchTerm.length() < 2) {
            popup.setVisible(false);
            return;
        }
        
        controller.EquipeController equipeController = new controller.EquipeController();
        List<Equipe> equipes = equipeController.listarTodasEquipes();
        boolean found = false;
        
        for (Equipe equipe : equipes) {
            if (equipe.getNome().toLowerCase().contains(searchTerm)) {
                JMenuItem item = new JMenuItem(equipe.getNome());
                item.addActionListener(e -> {
                    searchField.setText(equipe.getNome());
                    popup.setVisible(false);
                });
                popup.add(item);
                found = true;
                
                // Limitar a 5 resultados
                if (popup.getComponentCount() >= 5) break;
            }
        }
        
        if (found) {
            popup.show(searchField, 0, searchField.getHeight());
        } else {
            popup.setVisible(false);
        }
    }

    // Configurar busca de usuário em tempo real para membros
    private void setupUsuarioSearchPopup(JTextField searchField, JPopupMenu popup) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { searchUsuarioForMember(searchField, popup); }
            @Override
            public void removeUpdate(DocumentEvent e) { searchUsuarioForMember(searchField, popup); }
            @Override
            public void changedUpdate(DocumentEvent e) { searchUsuarioForMember(searchField, popup); }
        });
    }

    private void searchUsuarioForMember(JTextField searchField, JPopupMenu popup) {
        String searchTerm = searchField.getText().toLowerCase().trim();
        
        popup.removeAll();
        
        if (searchTerm.length() < 2) {
            popup.setVisible(false);
            return;
        }
        
        List<Usuario> usuarios = usuarioDAO.findAll();
        boolean found = false;
        
        for (Usuario usuario : usuarios) {
            if (usuario.getNomeCompleto().toLowerCase().contains(searchTerm)) {
                JMenuItem item = new JMenuItem(usuario.getNomeCompleto());
                item.addActionListener(e -> {
                    searchField.setText(usuario.getNomeCompleto());
                    popup.setVisible(false);
                });
                popup.add(item);
                found = true;
                
                // Limitar a 5 resultados
                if (popup.getComponentCount() >= 5) break;
            }
        }
        
        if (found) {
            popup.show(searchField, 0, searchField.getHeight());
        } else {
            popup.setVisible(false);
        }
    }

    /**
     * Cria a tela inicial de boas-vindas
     */
    private JPanel createHomePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, Color.decode("#0B192C"), 
                                                         getWidth(), getHeight(), Color.decode("#1E3E62"));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Main content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Welcome title
        JLabel welcomeTitle = new JLabel("Bem-vindo ao Sistema de Gestão");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Projetos e Equipes");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        subtitle.setForeground(Color.decode("#ECF0F1"));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Description
        JTextArea description = new JTextArea(
            "Este sistema permite gerenciar usuários, projetos e equipes de forma integrada.\n\n" +
            "Funcionalidades disponíveis:\n" +
            "• Cadastro e gerenciamento de usuários\n" +
            "• Criação e acompanhamento de projetos\n" +
            "• Formação e administração de equipes\n" +
            "• Atribuição de membros às equipes\n\n" +
            "Navegue pelo menu lateral para acessar as diferentes funcionalidades."
        );
        description.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        description.setForeground(Color.decode("#ECF0F1"));
        description.setOpaque(false);
        description.setEditable(false);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);
        description.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Menu guide
        JPanel menuGuide = createMenuGuide();

        contentPanel.add(welcomeTitle);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(subtitle);
        contentPanel.add(description);
        contentPanel.add(menuGuide);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Cria guia do menu
     */
    private JPanel createMenuGuide() {
        JPanel guidePanel = new JPanel(new GridLayout(2, 3, 20, 20));
        guidePanel.setOpaque(false);
        guidePanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Menu items
        guidePanel.add(createMenuCard(FontAwesomeSolid.USER_PLUS, "Cadastrar Usuário", "Adicione novos usuários ao sistema"));
        guidePanel.add(createMenuCard(FontAwesomeSolid.USERS, "Gerenciar Usuários", "Visualize e edite informações dos usuários"));
        guidePanel.add(createMenuCard(FontAwesomeSolid.TASKS, "Gerenciar Projetos", "Crie e acompanhe projetos"));
        guidePanel.add(createMenuCard(FontAwesomeSolid.USERS_COG, "Gerenciar Equipes", "Organize equipes de trabalho"));
        guidePanel.add(createMenuCard(FontAwesomeSolid.USER_COG, "Gerenciar Membros", "Atribua usuários às equipes"));
        guidePanel.add(createMenuCard(FontAwesomeSolid.QUESTION_CIRCLE, "Ajuda", "Obtenha ajuda sobre o sistema"));

        return guidePanel;
    }

    /**
     * Cria card do menu
     */
    private JPanel createMenuCard(FontAwesomeSolid icon, String title, String description) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background
                g2d.setColor(new Color(255, 255, 255, 30));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Border
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Icon
        JLabel iconLabel = new JLabel();
        FontIcon fontIcon = FontIcon.of(icon, 32, Color.decode("#FF6500"));
        iconLabel.setIcon(fontIcon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Description
        JTextArea descArea = new JTextArea(description);
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descArea.setForeground(Color.decode("#ECF0F1"));
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descArea, BorderLayout.CENTER);

        card.add(iconLabel, BorderLayout.NORTH);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Cria tela de ajuda
     */
    private JPanel createAjudaPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 15, 15);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.QUESTION_CIRCLE, 24, Color.decode("#0B192C"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Central de Ajuda");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#0B192C"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(titleText);

        // Help content
        JTextArea helpContent = new JTextArea(
            "COMO USAR O SISTEMA:\n\n" +
            
            "1. CADASTRAR USUÁRIO:\n" +
            "   • Preencha todos os campos obrigatórios\n" +
            "   • O CPF será formatado automaticamente\n" +
            "   • Use o botão de visualizar senha para conferir\n\n" +
            
            "2. GERENCIAR USUÁRIOS:\n" +
            "   • Digite no campo de busca para filtrar em tempo real\n" +
            "   • Selecione um usuário na tabela e clique 'Editar'\n" +
            "   • Para excluir, selecione um ou mais usuários\n\n" +
            
            "3. GERENCIAR PROJETOS:\n" +
            "   • Use 'Criar Novo' para adicionar projetos\n" +
            "   • Selecione um projeto na tabela e clique 'Editar'\n" +
            "   • O calendário abre automaticamente nos campos de data\n" +
            "   • Busque gerentes digitando o nome\n\n" +
            
            "4. GERENCIAR EQUIPES:\n" +
            "   • Crie equipes com nome e descrição\n" +
            "   • Use o menu de contexto (botão direito) para ações rápidas\n\n" +
            
            "5. GERENCIAR MEMBROS:\n" +
            "   • Busque equipes e usuários em tempo real\n" +
            "   • Adicione ou remova membros das equipes\n\n" +
            
            "DICAS:\n" +
            "• Todas as buscas funcionam em tempo real\n" +
            "• Use Ctrl+Clique para seleções múltiplas\n" +
            "• Os campos obrigatórios são validados automaticamente\n" +
            "• Mensagens de confirmação aparecem para ações importantes"
        );
        
        helpContent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        helpContent.setForeground(Color.decode("#2C3E50"));
        helpContent.setOpaque(false);
        helpContent.setEditable(false);
        helpContent.setLineWrap(true);
        helpContent.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(helpContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(20, 0, 0, 0));

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Cria tela separada para gerenciamento de membros
     */
    private JPanel createMembrosPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Card background
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 15, 15);
                
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USER_COG, 24, Color.decode("#0B192C"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciamento de Membros");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#0B192C"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(titleText);

        // Member management panel (reuse the existing one)
        JPanel memberManagement = createMemberManagementPanel();

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(memberManagement, BorderLayout.CENTER);

        return panel;
    }
}
