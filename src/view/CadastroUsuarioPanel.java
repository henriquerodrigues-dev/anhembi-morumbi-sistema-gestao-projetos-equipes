package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.Ikon;

import dao.UsuarioDAO;
import model.Usuario;
import util.ValidadorUtil;

/**
 * Painel para cadastro de usuários
 */
public class CadastroUsuarioPanel extends JPanel {
    
    private JTextField nomeField;
    private JTextField cpfField;
    private JTextField emailField;
    private JTextField cargoField;
    private JTextField loginField;
    private JPasswordField senhaField;
    private JButton toggleSenhaButton;
    private Consumer<String> toastCallback;
    private Runnable refreshCallback;
    private Runnable navigateCallback;
    private String usuarioEditandoId; // ID do usuário sendo editado (null para novo usuário)
    
    public CadastroUsuarioPanel(Consumer<String> toastCallback, Runnable refreshCallback, Runnable navigateCallback) {
        this.toastCallback = toastCallback;
        this.refreshCallback = refreshCallback;
        this.navigateCallback = navigateCallback;
        initializeComponents();
        setupLayout();
        setupActions();
    }
    
    private void initializeComponents() {
        setBackground(Color.decode("#ECF0F1"));
        setLayout(new BorderLayout());
        
        // Inicializar campos
        nomeField = createStyledTextField();
        cpfField = createStyledTextField();
        emailField = createStyledTextField();
        cargoField = createStyledTextField();
        loginField = createStyledTextField();
        senhaField = new JPasswordField();
        stylePasswordField(senhaField);
        
        // Configurar formatação automática do CPF
        setupCPFFormatting();
    }
    
    private void setupLayout() {
        // Container principal com sombra
        JPanel cadastroWrapper = new JPanel() {
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
        cadastroWrapper.setOpaque(false);
        cadastroWrapper.setPreferredSize(new Dimension(900, 800));
        cadastroWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        cadastroWrapper.setLayout(new BorderLayout());

        // Título
        JPanel titlePanel = createTitlePanel();
        
        // Formulário
        JPanel formPanel = createFormPanel();
        
        // Botões
        JPanel buttonPanel = createButtonPanel();
        
        cadastroWrapper.add(titlePanel, BorderLayout.NORTH);
        cadastroWrapper.add(formPanel, BorderLayout.CENTER);
        cadastroWrapper.add(buttonPanel, BorderLayout.SOUTH);

        // Centralizar o painel
        JPanel centeringPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centeringPanel.setBackground(Color.decode("#ECF0F1"));
        centeringPanel.add(cadastroWrapper);
        
        add(centeringPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USER_PLUS, 24, Color.decode("#2C3E50"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Cadastro de Usuário");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#2C3E50"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        return titlePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 20, 15));
        formPanel.setOpaque(false);

        // Adicionar campos com labels e requisitos
        formPanel.add(createFieldWithRequirement("Nome Completo:", FontAwesomeSolid.USER, nomeField, "Mínimo 2 caracteres"));
        formPanel.add(new JLabel()); // Espaço vazio
        
        formPanel.add(createFieldWithRequirement("CPF:", FontAwesomeSolid.ID_CARD, cpfField, "Formato: 000.000.000-00"));
        formPanel.add(new JLabel()); // Espaço vazio
        
        formPanel.add(createFieldWithRequirement("E-mail:", FontAwesomeSolid.ENVELOPE, emailField, "Formato válido de e-mail"));
        formPanel.add(new JLabel()); // Espaço vazio
        
        formPanel.add(createFieldWithRequirement("Cargo:", FontAwesomeSolid.BRIEFCASE, cargoField, "Função na empresa"));
        formPanel.add(new JLabel()); // Espaço vazio
        
        formPanel.add(createFieldWithRequirement("Login:", FontAwesomeSolid.USER_CIRCLE, loginField, "Mínimo 3 caracteres"));
        formPanel.add(new JLabel()); // Espaço vazio
        
        formPanel.add(createFieldWithRequirement("Senha:", FontAwesomeSolid.LOCK, createSenhaPanel(), "Mínimo 6 caracteres, deve conter pelo menos 1 letra e 1 número"));
        formPanel.add(new JLabel()); // Espaço vazio

        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 100, 0, 100));

        JButton saveButton = createIconActionButton(FontAwesomeSolid.SAVE, "Salvar", Color.decode("#2ECC71"));
        JButton clearButton = createIconActionButton(FontAwesomeSolid.ERASER, "Limpar", Color.decode("#95A5A6"));
        
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        
        return buttonPanel;
    }
    
    private JPanel createFieldWithRequirement(String labelText, Ikon iconCode, Component fieldComponent, String requirement) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setOpaque(false);
        fieldPanel.setPreferredSize(new Dimension(350, 90));
        
        // Label com ícone
        JPanel fieldLabel = createFieldLabel(labelText, iconCode);
        fieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.add(fieldLabel);
        
        // Espaço pequeno
        fieldPanel.add(Box.createVerticalStrut(5));
        
        // Campo (aumentar altura)
        if (fieldComponent instanceof JTextField) {
            JTextField textField = (JTextField) fieldComponent;
            textField.setPreferredSize(new Dimension(0, 35));
            textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        } else if (fieldComponent instanceof JPanel) {
            fieldComponent.setPreferredSize(new Dimension(0, 35));
            fieldComponent.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        }
        if (fieldComponent instanceof javax.swing.JComponent) {
            ((javax.swing.JComponent) fieldComponent).setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        fieldPanel.add(fieldComponent);
        
        // Espaço pequeno
        fieldPanel.add(Box.createVerticalStrut(3));
        
        // Panel inferior com requisito
        JLabel reqLabel = new JLabel("<html><i>" + requirement + "</i></html>");
        reqLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        reqLabel.setForeground(Color.decode("#7F8C8D"));
        reqLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldPanel.add(reqLabel);
        
        return fieldPanel;
    }
    
    private JPanel createFieldLabel(String text, Ikon iconCode) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setOpaque(false);
        labelPanel.setPreferredSize(new Dimension(300, 25));
        labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(iconCode, 16, Color.decode("#34495E"));
        iconLabel.setIcon(icon);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textLabel.setForeground(Color.decode("#2C3E50"));
        
        labelPanel.add(iconLabel);
        labelPanel.add(Box.createHorizontalStrut(8));
        labelPanel.add(textLabel);
        
        return labelPanel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private void stylePasswordField(JPasswordField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setEchoChar('*');
    }
    
    private JPanel createSenhaPanel() {
        JPanel senhaPanel = new JPanel(new BorderLayout());
        senhaPanel.setOpaque(false);
        
        toggleSenhaButton = new JButton();
        FontIcon lockIcon = FontIcon.of(FontAwesomeSolid.EYE_SLASH, 16, Color.decode("#7F8C8D"));
        toggleSenhaButton.setIcon(lockIcon);
        toggleSenhaButton.setBorder(new EmptyBorder(5, 8, 5, 8));
        toggleSenhaButton.setBackground(Color.decode("#ECF0F1"));
        toggleSenhaButton.setFocusPainted(false);
        toggleSenhaButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        senhaPanel.add(senhaField, BorderLayout.CENTER);
        senhaPanel.add(toggleSenhaButton, BorderLayout.EAST);
        
        return senhaPanel;
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
    
    private void setupCPFFormatting() {
        cpfField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = cpfField.getText().replaceAll("[^0-9]", "");
                if (text.length() <= 11) {
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < text.length(); i++) {
                        if (i == 3 || i == 6) formatted.append(".");
                        if (i == 9) formatted.append("-");
                        formatted.append(text.charAt(i));
                    }
                    cpfField.setText(formatted.toString());
                }
            }
        });
    }
    
    private void setupActions() {
        // Ação do botão toggle senha
        toggleSenhaButton.addActionListener(e -> {
            if (senhaField.getEchoChar() == '*') {
                senhaField.setEchoChar((char) 0);
                FontIcon eyeIcon = FontIcon.of(FontAwesomeSolid.EYE, 16, Color.decode("#7F8C8D"));
                toggleSenhaButton.setIcon(eyeIcon);
            } else {
                senhaField.setEchoChar('*');
                FontIcon eyeSlashIcon = FontIcon.of(FontAwesomeSolid.EYE_SLASH, 16, Color.decode("#7F8C8D"));
                toggleSenhaButton.setIcon(eyeSlashIcon);
            }
        });
        
        // Ação do botão salvar
        Component[] components = ((JPanel) ((JPanel) getComponent(0)).getComponent(0)).getComponents();
        JPanel buttonPanel = (JPanel) components[2];
        JButton saveButton = (JButton) buttonPanel.getComponent(0);
        JButton clearButton = (JButton) buttonPanel.getComponent(1);
        
        saveButton.addActionListener(e -> salvarUsuario());
        clearButton.addActionListener(e -> limparCampos());
    }
    
    private void salvarUsuario() {
        try {
            // Validações
            if (!ValidadorUtil.validarCampoObrigatorio(nomeField.getText())) {
                showToast("Nome é obrigatório!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarCPF(cpfField.getText())) {
                showToast("CPF inválido!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarEmail(emailField.getText())) {
                showToast("E-mail inválido!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarCampoObrigatorio(cargoField.getText())) {
                showToast("Cargo é obrigatório!", "error");
                return;
            }
            
            if (!ValidadorUtil.validarLogin(loginField.getText())) {
                showToast("Login inválido! Mínimo 3 caracteres.", "error");
                return;
            }
            
            // Validar senha apenas se não estiver editando ou se senha foi preenchida
            String senha = new String(senhaField.getPassword());
            if (usuarioEditandoId == null || !senha.isEmpty()) {
                if (!ValidadorUtil.validarSenha(senha)) {
                    showToast("Senha inválida! Mínimo 6 caracteres com letra e número.", "error");
                    return;
                }
            }
            
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            
            if (usuarioEditandoId != null) {
                // Editando usuário existente
                Usuario usuarioExistente = usuarioDAO.findById(usuarioEditandoId);
                if (usuarioExistente == null) {
                    showToast("Usuário não encontrado!", "error");
                    return;
                }
                
                Usuario usuario = new Usuario(
                    usuarioEditandoId,
                    nomeField.getText().trim(),
                    cpfField.getText().trim(),
                    emailField.getText().trim(),
                    cargoField.getText().trim(),
                    loginField.getText().trim(),
                    senha.isEmpty() ? usuarioExistente.getSenha() : senha // Manter senha atual se não foi alterada
                );
                
                usuarioDAO.update(usuario);
                showToast("Usuário atualizado com sucesso!", "success");
                
                // Atualizar lista se callback disponível
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                // Criando novo usuário (usando construtor que gera UUID automaticamente)
                Usuario usuario = new Usuario(
                    nomeField.getText().trim(),
                    cpfField.getText().trim(),
                    emailField.getText().trim(),
                    cargoField.getText().trim(),
                    loginField.getText().trim(),
                    senha
                );
                
                usuarioDAO.create(usuario);
                showToast("Usuário cadastrado com sucesso!", "success");
                
                // Navegar para gerenciar usuários
                if (navigateCallback != null) {
                    navigateCallback.run();
                }
            }
            
            limparCampos();
            
        } catch (Exception ex) {
            showToast("Erro ao salvar usuário: " + ex.getMessage(), "error");
        }
    }
    
    private void limparCampos() {
        nomeField.setText("");
        cpfField.setText("");
        emailField.setText("");
        cargoField.setText("");
        loginField.setText("");
        senhaField.setText("");
        usuarioEditandoId = null;
    }
    
    public void carregarUsuarioParaEdicao(Usuario usuario) {
        if (usuario != null) {
            usuarioEditandoId = usuario.getId();
            nomeField.setText(usuario.getNomeCompleto());
            cpfField.setText(usuario.getCpf());
            emailField.setText(usuario.getEmail());
            cargoField.setText(usuario.getCargo());
            loginField.setText(usuario.getLogin());
            senhaField.setText(""); // Deixar vazio para não mostrar senha atual
        }
    }
    
    private void showToast(String message, String type) {
        if (toastCallback != null) {
            toastCallback.accept(type + ":" + message);
        }
    }
}
