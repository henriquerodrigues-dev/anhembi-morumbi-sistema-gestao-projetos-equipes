package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.Ikon;
import controller.EquipeController;
import dao.UsuarioDAO;
import model.Equipe;
import model.Usuario;

/**
 * Painel para gerenciamento de membros das equipes
 */
public class GerenciarMembrosPanel extends JPanel {
    
    private Consumer<String> showToast;
    private JComboBox<String> equipeComboBox;
    private JTextField usuarioSearchField;
    private DefaultTableModel membrosTableModel;
    private JTable membrosTable;
    private String selectedEquipeId = "";
    
    public GerenciarMembrosPanel(Consumer<String> toastCallback) {
        this.showToast = toastCallback;
        initializeComponents();
        setupLayout();
        loadEquipes();
    }
    
    private void initializeComponents() {
        setBackground(Color.decode("#ECF0F1"));
        setLayout(new BorderLayout());

        equipeComboBox = new JComboBox<>();
        equipeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        equipeComboBox.setPreferredSize(new Dimension(0, 35));
        equipeComboBox.addActionListener(e -> onEquipeSelected());

        usuarioSearchField = createStyledTextField();
        usuarioSearchField.setToolTipText("Digite o nome do usuário para buscar...");
        setupUsuarioSearch();

        String[] colunas = {"ID", "Nome", "Email", "Cargo"};
        membrosTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        membrosTable = new JTable(membrosTableModel);
        membrosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        membrosTable.setRowHeight(25);
        membrosTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        membrosTable.getTableHeader().setBackground(Color.decode("#34495E"));
        membrosTable.getTableHeader().setForeground(Color.WHITE);
        membrosTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        membrosTable.getColumnModel().getColumn(0).setMinWidth(0);
        membrosTable.getColumnModel().getColumn(0).setMaxWidth(0);
        membrosTable.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    private void setupLayout() {
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#ECF0F1"));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(createTitlePanel(), BorderLayout.NORTH);
        topPanel.add(createFormPanel(), BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);

        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USER_COG, 24, Color.decode("#2C3E50"));
        iconLabel.setIcon(icon);

        JLabel titleText = new JLabel("Gerenciar Membros");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#2C3E50"));

        titlePanel.add(iconLabel);
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        return titlePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 20, 20);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        formWrapper.setLayout(new BorderLayout());

        JLabel formTitle = new JLabel("Gerenciar Membros da Equipe");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(Color.decode("#2C3E50"));
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);
        formTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);

        fieldsPanel.add(createFieldLabel("Selecionar Equipe:", FontAwesomeSolid.USERS));
        fieldsPanel.add(equipeComboBox);
        fieldsPanel.add(Box.createVerticalStrut(10));

        fieldsPanel.add(createFieldLabel("Buscar Usuário:", FontAwesomeSolid.SEARCH));
        fieldsPanel.add(usuarioSearchField);
        fieldsPanel.add(Box.createVerticalStrut(10));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton adicionarBtn = createActionButton("Adicionar Membro", FontAwesomeSolid.USER_PLUS, Color.decode("#27AE60"));
        JButton removerBtn = createActionButton("Remover Selecionados", FontAwesomeSolid.USER_MINUS, Color.decode("#E74C3C"));
        JButton atualizarBtn = createActionButton("Atualizar", FontAwesomeSolid.SYNC_ALT, Color.decode("#3498DB"));

        adicionarBtn.addActionListener(e -> adicionarMembro());
        removerBtn.addActionListener(e -> removerMembros());
        atualizarBtn.addActionListener(e -> loadEquipes());

        adicionarBtn.setPreferredSize(new Dimension(180, 45));
        removerBtn.setPreferredSize(new Dimension(200, 45));
        atualizarBtn.setPreferredSize(new Dimension(140, 45));

        buttonPanel.add(adicionarBtn);
        buttonPanel.add(removerBtn);
        buttonPanel.add(atualizarBtn);

        formWrapper.add(formTitle, BorderLayout.NORTH);
        formWrapper.add(fieldsPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);

        return formWrapper;
    }
    
    private JPanel createTablePanel() {
        JPanel tableWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 20, 20);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        tableWrapper.setLayout(new BorderLayout());

        JLabel tableTitle = new JLabel("Membros da Equipe");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(Color.decode("#2C3E50"));
        tableTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tableTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        JScrollPane scrollPane = new JScrollPane(membrosTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1));

        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        return tableWrapper;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(0, 35));
        return field;
    }

    private JButton createActionButton(String text, FontAwesomeSolid iconCode, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color baseColor = color;
                if (getModel().isPressed()) {
                    baseColor = color.darker();
                } else if (getModel().isRollover()) {
                    baseColor = color.brighter();
                }

                g2d.setColor(baseColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

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
    
    private void setupUsuarioSearch() {
        usuarioSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchUsuario(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchUsuario(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchUsuario(); }
        });
    }
    
    private void searchUsuario() {
        String searchTerm = usuarioSearchField.getText().toLowerCase().trim();
        
        if (searchTerm.length() < 2) {
            return;
        }
        
        try {
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.findAll();
            
            // Criar popup com sugestões
            JPopupMenu popup = new JPopupMenu();
            boolean found = false;
            
            for (Usuario usuario : usuarios) {
                if (usuario.getNomeCompleto().toLowerCase().contains(searchTerm)) {
                    JMenuItem item = new JMenuItem(usuario.getNomeCompleto() + " (ID: " + usuario.getId() + ")");
                    item.addActionListener(e -> {
                        usuarioSearchField.setText(usuario.getNomeCompleto() + " (ID: " + usuario.getId() + ")");
                        popup.setVisible(false);
                    });
                    popup.add(item);
                    found = true;
                    
                    // Limitar a 5 resultados
                    if (popup.getComponentCount() >= 5) break;
                }
            }
            
            if (found) {
                popup.show(usuarioSearchField, 0, usuarioSearchField.getHeight());
            }
        } catch (Exception e) {
            // Ignorar erros de busca
        }
    }
    
    private void loadEquipes() {
        try {
            EquipeController equipeController = new EquipeController();
            List<Equipe> equipes = equipeController.listarTodasEquipes();
            
            equipeComboBox.removeAllItems();
            equipeComboBox.addItem("Selecione uma equipe...");
            
            for (Equipe equipe : equipes) {
                equipeComboBox.addItem(equipe.getNome() + " (ID: " + equipe.getId() + ")");
            }
        } catch (Exception e) {
            showToast.accept("Erro ao carregar equipes: " + e.getMessage());
        }
    }
    
    private void onEquipeSelected() {
        int selectedIndex = equipeComboBox.getSelectedIndex();
        if (selectedIndex <= 0) {
            selectedEquipeId = "";
            membrosTableModel.setRowCount(0);
            return;
        }
        
        String selectedItem = (String) equipeComboBox.getSelectedItem();
        // Extrair ID da string "Nome (ID: xxx)"
        if (selectedItem.contains("(ID: ")) {
            int start = selectedItem.lastIndexOf("(ID: ") + 5;
            int end = selectedItem.lastIndexOf(")");
            selectedEquipeId = selectedItem.substring(start, end);
            loadMembros();
        }
    }
    
    private void loadMembros() {
        if (selectedEquipeId.isEmpty()) {
            return;
        }
        
        try {
            EquipeController equipeController = new EquipeController();
            Equipe equipe = equipeController.buscarEquipe(selectedEquipeId);
            
            membrosTableModel.setRowCount(0);
            
            if (equipe != null && equipe.getMembros() != null) {
                for (Usuario membro : equipe.getMembros()) {
                    Object[] rowData = {
                        membro.getId(),
                        membro.getNomeCompleto(),
                        membro.getEmail(),
                        membro.getCargo()
                    };
                    membrosTableModel.addRow(rowData);
                }
            }
        } catch (Exception e) {
            showToast.accept("Erro ao carregar membros: " + e.getMessage());
        }
    }
    
    private void adicionarMembro() {
        if (selectedEquipeId.isEmpty()) {
            showToast.accept("Selecione uma equipe primeiro!");
            return;
        }
        
        String usuarioText = usuarioSearchField.getText().trim();
        if (usuarioText.isEmpty()) {
            showToast.accept("Digite o nome do usuário!");
            return;
        }
        
        // Extrair ID do usuário
        String usuarioId = "";
        if (usuarioText.contains("(ID: ")) {
            int start = usuarioText.lastIndexOf("(ID: ") + 5;
            int end = usuarioText.lastIndexOf(")");
            usuarioId = usuarioText.substring(start, end);
        } else {
            // Buscar usuário pelo nome
            try {
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                List<Usuario> usuarios = usuarioDAO.findAll();
                for (Usuario usuario : usuarios) {
                    if (usuario.getNomeCompleto().toLowerCase().contains(usuarioText.toLowerCase())) {
                        usuarioId = usuario.getId();
                        break;
                    }
                }
            } catch (Exception e) {
                showToast.accept("Erro ao buscar usuário: " + e.getMessage());
                return;
            }
        }
        
        if (usuarioId.isEmpty()) {
            showToast.accept("Usuário não encontrado!");
            return;
        }
        
        // Verificar se já é membro
        for (int i = 0; i < membrosTableModel.getRowCount(); i++) {
            if (membrosTableModel.getValueAt(i, 0).equals(usuarioId)) {
                showToast.accept("Este usuário já é membro desta equipe!");
                return;
            }
        }
        
        try {
            EquipeController equipeController = new EquipeController();
            String resultado = equipeController.adicionarMembro(selectedEquipeId, usuarioId);
            
            if (resultado == null) {
                showToast.accept("success:Membro adicionado com sucesso!");
                usuarioSearchField.setText("");
                loadMembros();
                loadEquipes(); // Atualizar lista de equipes também
            } else {
                showToast.accept("error:" + resultado);
            }
        } catch (Exception e) {
            showToast.accept("Erro ao adicionar membro: " + e.getMessage());
        }
    }
    
    private void removerMembros() {
        if (selectedEquipeId.isEmpty()) {
            showToast.accept("Selecione uma equipe primeiro!");
            return;
        }
        
        int[] selectedRows = membrosTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showToast.accept("Selecione pelo menos um membro para remover!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente remover " + selectedRows.length + " membro(s) da equipe?",
            "Confirmar Remoção",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                EquipeController equipeController = new EquipeController();
                int sucessos = 0;
                
                for (int row : selectedRows) {
                    String usuarioId = (String) membrosTableModel.getValueAt(row, 0);
                    String resultado = equipeController.removerMembro(selectedEquipeId, usuarioId);
                    if (resultado == null) {
                        sucessos++;
                    }
                }
                
                if (sucessos > 0) {
                    showToast.accept("success:" + sucessos + " membro(s) removido(s) com sucesso!");
                    loadMembros();
                    loadEquipes(); // Atualizar lista de equipes também
                }
            } catch (Exception e) {
                showToast.accept("Erro ao remover membros: " + e.getMessage());
            }
        }
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
}
