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
        setOpaque(false);
        
        // ComboBox para seleção de equipe
        equipeComboBox = new JComboBox<>();
        equipeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        equipeComboBox.setPreferredSize(new Dimension(0, 35));
        equipeComboBox.addActionListener(e -> onEquipeSelected());
        
        // Campo de busca de usuário
        usuarioSearchField = createStyledTextField();
        usuarioSearchField.setToolTipText("Digite o nome do usuário para buscar...");
        setupUsuarioSearch();
        
        // Tabela de membros
        String[] colunas = {"ID", "Nome", "Email", "Cargo"};
        membrosTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        membrosTable = new JTable(membrosTableModel);
        membrosTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        membrosTable.setRowHeight(30);
        membrosTable.getTableHeader().setReorderingAllowed(false);
        
        // Ocultar coluna ID
        membrosTable.getColumnModel().getColumn(0).setMinWidth(0);
        membrosTable.getColumnModel().getColumn(0).setMaxWidth(0);
        membrosTable.getColumnModel().getColumn(0).setPreferredWidth(0);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Painel principal com fundo
        JPanel mainPanel = new JPanel(new BorderLayout()) {
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
        mainPanel.setOpaque(false);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Título
        JPanel titlePanel = createTitlePanel();
        
        // Formulário
        JPanel formPanel = createFormPanel();
        
        // Tabela
        JPanel tablePanel = createTablePanel();
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USER_COG, 24, Color.decode("#2C3E50"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciamento de Membros");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#2C3E50"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        return titlePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Seleção de equipe
        JPanel equipeLabel = createFieldLabel("Selecionar Equipe:", FontAwesomeSolid.USERS);
        equipeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        equipeComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        equipeComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        formPanel.add(equipeLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(equipeComboBox);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Busca de usuário
        JPanel usuarioLabel = createFieldLabel("Buscar Usuário:", FontAwesomeSolid.SEARCH);
        usuarioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        usuarioSearchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        usuarioSearchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        formPanel.add(usuarioLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(usuarioSearchField);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton adicionarBtn = createActionButton("Adicionar Membro", FontAwesomeSolid.USER_PLUS, Color.decode("#27AE60"));
        JButton removerBtn = createActionButton("Remover Selecionados", FontAwesomeSolid.USER_MINUS, Color.decode("#E74C3C"));
        JButton atualizarBtn = createActionButton("Atualizar", FontAwesomeSolid.SYNC_ALT, Color.decode("#3498DB"));
        
        adicionarBtn.addActionListener(e -> adicionarMembro());
        removerBtn.addActionListener(e -> removerMembros());
        atualizarBtn.addActionListener(e -> loadEquipes());
        
        buttonPanel.add(adicionarBtn);
        buttonPanel.add(removerBtn);
        buttonPanel.add(atualizarBtn);
        formPanel.add(buttonPanel);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        
        JLabel tableTitle = new JLabel("Membros da Equipe");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(Color.decode("#2C3E50"));
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JScrollPane scrollPane = new JScrollPane(membrosTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1));
        
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JButton createActionButton(String text, FontAwesomeSolid iconCode, Color color) {
        JButton button = new JButton(text);
        FontIcon icon = FontIcon.of(iconCode, 16, Color.WHITE);
        button.setIcon(icon);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
