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
import model.Equipe;

/**
 * Painel para gerenciamento de equipes
 */
public class GerenciarEquipePanel extends JPanel {
    
    private Consumer<String> showToast;
    private JTextField equipeIdField;
    private JTextField equipeNomeField;
    private JTextField equipeDescricaoField;
    private DefaultTableModel equipeTableModel;
    private JTable equipeTable;
    private boolean isEditMode = false;
    
    public GerenciarEquipePanel(Consumer<String> toastCallback) {
        this.showToast = toastCallback;
        initializeComponents();
        setupLayout();
        refreshTeamList();
    }
    
    private void initializeComponents() {
        setOpaque(false);
        
        // Inicializar campos
        equipeIdField = createStyledTextField();
        equipeIdField.setEditable(false);
        equipeIdField.setBackground(Color.decode("#F8F9FA"));
        
        equipeNomeField = createStyledTextField();
        equipeDescricaoField = createStyledTextField();
        
        // Configurar tabela
        String[] colunas = {"ID", "Nome", "Descrição", "Membros"};
        equipeTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        equipeTable = new JTable(equipeTableModel);
        equipeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        equipeTable.setRowHeight(30);
        equipeTable.getTableHeader().setReorderingAllowed(false);
        
        // Ocultar coluna ID
        equipeTable.getColumnModel().getColumn(0).setMinWidth(0);
        equipeTable.getColumnModel().getColumn(0).setMaxWidth(0);
        equipeTable.getColumnModel().getColumn(0).setPreferredWidth(0);
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
        
        // Conteúdo principal
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        
        // Formulário
        JPanel formPanel = createFormPanel();
        
        // Tabela
        JPanel tablePanel = createTablePanel();
        
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USERS_COG, 24, Color.decode("#2C3E50"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciamento de Equipes");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#2C3E50"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(15));
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        return titlePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel formTitle = new JLabel("Cadastro/Edição de Equipes");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(Color.decode("#2C3E50"));
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);
        formTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Campos do formulário
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);
        
        // Campo ID
        JPanel idLabel = createFieldLabel("ID da Equipe:", FontAwesomeSolid.HASHTAG);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        equipeIdField.setAlignmentX(Component.LEFT_ALIGNMENT);
        equipeIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        fieldsPanel.add(idLabel);
        fieldsPanel.add(Box.createVerticalStrut(5));
        fieldsPanel.add(equipeIdField);
        fieldsPanel.add(Box.createVerticalStrut(15));
        
        // Campo Nome
        JPanel nomeLabel = createFieldLabel("Nome da Equipe:", FontAwesomeSolid.USERS);
        nomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        equipeNomeField.setAlignmentX(Component.LEFT_ALIGNMENT);
        equipeNomeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        fieldsPanel.add(nomeLabel);
        fieldsPanel.add(Box.createVerticalStrut(5));
        fieldsPanel.add(equipeNomeField);
        fieldsPanel.add(Box.createVerticalStrut(15));
        
        // Campo Descrição
        JPanel descLabel = createFieldLabel("Descrição:", FontAwesomeSolid.ALIGN_LEFT);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        equipeDescricaoField.setAlignmentX(Component.LEFT_ALIGNMENT);
        equipeDescricaoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        
        fieldsPanel.add(descLabel);
        fieldsPanel.add(Box.createVerticalStrut(5));
        fieldsPanel.add(equipeDescricaoField);
        fieldsPanel.add(Box.createVerticalStrut(20));
        
        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton criarBtn = createActionButton("Criar", FontAwesomeSolid.PLUS, Color.decode("#27AE60"));
        JButton salvarBtn = createActionButton("Salvar", FontAwesomeSolid.SAVE, Color.decode("#2ECC71"));
        JButton limparBtn = createActionButton("Limpar", FontAwesomeSolid.ERASER, Color.decode("#95A5A6"));
        
        // Inicialmente mostrar apenas o botão Criar
        salvarBtn.setVisible(false);
        
        criarBtn.addActionListener(e -> criarEquipe());
        salvarBtn.addActionListener(e -> salvarEquipe());
        limparBtn.addActionListener(e -> limparCampos());
        
        buttonPanel.add(criarBtn);
        buttonPanel.add(salvarBtn);
        buttonPanel.add(limparBtn);
        fieldsPanel.add(buttonPanel);
        
        formWrapper.add(formTitle, BorderLayout.NORTH);
        formWrapper.add(fieldsPanel, BorderLayout.CENTER);
        
        return formWrapper;
    }
    
    private JPanel createTablePanel() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        
        JLabel tableTitle = new JLabel("Lista de Equipes");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(Color.decode("#2C3E50"));
        tableTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JScrollPane scrollPane = new JScrollPane(equipeTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1));
        
        // Botões da tabela
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        tableButtonPanel.setOpaque(false);
        tableButtonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        JButton editarBtn = createActionButton("Editar", FontAwesomeSolid.EDIT, Color.decode("#F39C12"));
        JButton excluirBtn = createActionButton("Excluir", FontAwesomeSolid.TRASH, Color.decode("#E74C3C"));
        JButton atualizarBtn = createActionButton("Atualizar", FontAwesomeSolid.SYNC_ALT, Color.decode("#3498DB"));
        
        editarBtn.addActionListener(e -> editarEquipe());
        excluirBtn.addActionListener(e -> excluirEquipes());
        atualizarBtn.addActionListener(e -> refreshTeamList());
        
        tableButtonPanel.add(editarBtn);
        tableButtonPanel.add(excluirBtn);
        tableButtonPanel.add(atualizarBtn);
        
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        tableWrapper.add(tableButtonPanel, BorderLayout.SOUTH);
        
        return tableWrapper;
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
    
    private void criarEquipe() {
        String nome = equipeNomeField.getText().trim();
        String descricao = equipeDescricaoField.getText().trim();
        
        if (nome.isEmpty()) {
            showToast.accept("Nome da equipe é obrigatório!");
            return;
        }
        
        try {
            EquipeController equipeController = new EquipeController();
            String resultado = equipeController.criarEquipe(nome, descricao);
            
            if (resultado == null) {
                showToast.accept("success:Equipe criada com sucesso!");
                limparCampos();
                refreshTeamList();
            } else {
                showToast.accept("error:" + resultado);
            }
        } catch (Exception e) {
            showToast.accept("Erro ao criar equipe: " + e.getMessage());
        }
    }
    
    private void salvarEquipe() {
        if (!isEditMode) {
            showToast.accept("Nenhuma equipe selecionada para editar!");
            return;
        }
        
        String nome = equipeNomeField.getText().trim();
        String descricao = equipeDescricaoField.getText().trim();
        
        if (nome.isEmpty()) {
            showToast.accept("Nome da equipe é obrigatório!");
            return;
        }
        
        try {
            EquipeController equipeController = new EquipeController();
            String id = equipeIdField.getText().trim();
            String resultado = equipeController.atualizarEquipe(id, nome, descricao);
            
            if (resultado == null) {
                showToast.accept("success:Equipe atualizada com sucesso!");
                limparCampos();
                refreshTeamList();
            } else {
                showToast.accept("error:" + resultado);
            }
        } catch (Exception e) {
            showToast.accept("Erro ao atualizar equipe: " + e.getMessage());
        }
    }
    
    private void editarEquipe() {
        int selectedRow = equipeTable.getSelectedRow();
        if (selectedRow == -1) {
            showToast.accept("Selecione uma equipe para editar!");
            return;
        }
        
        if (equipeTable.getSelectedRowCount() > 1) {
            showToast.accept("Selecione apenas uma equipe para editar!");
            return;
        }
        
        String id = (String) equipeTableModel.getValueAt(selectedRow, 0);
        String nome = (String) equipeTableModel.getValueAt(selectedRow, 1);
        String descricao = (String) equipeTableModel.getValueAt(selectedRow, 2);
        
        equipeIdField.setText(id);
        equipeNomeField.setText(nome);
        equipeDescricaoField.setText(descricao);
        
        // Alterar botões para modo edição
        JPanel formWrapper = (JPanel) ((JPanel) getComponent(0)).getComponent(1);
        JPanel fieldsPanel = (JPanel) formWrapper.getComponent(1);
        JPanel buttonPanel = (JPanel) fieldsPanel.getComponent(fieldsPanel.getComponentCount() - 1);
        JButton criarBtn = (JButton) buttonPanel.getComponent(0);
        JButton salvarBtn = (JButton) buttonPanel.getComponent(1);
        
        criarBtn.setVisible(false);
        salvarBtn.setVisible(true);
        isEditMode = true;
        
        showToast.accept("info:Equipe carregada para edição!");
    }
    
    private void excluirEquipes() {
        int[] selectedRows = equipeTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showToast.accept("Selecione pelo menos uma equipe para excluir!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Deseja realmente excluir " + selectedRows.length + " equipe(s)?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                EquipeController equipeController = new EquipeController();
                int sucessos = 0;
                
                for (int row : selectedRows) {
                    String id = (String) equipeTableModel.getValueAt(row, 0);
                    String resultado = equipeController.excluirEquipe(id);
                    if (resultado == null) {
                        sucessos++;
                    }
                }
                
                if (sucessos > 0) {
                    showToast.accept("success:" + sucessos + " equipe(s) excluída(s) com sucesso!");
                    limparCampos();
                    refreshTeamList();
                }
            } catch (Exception e) {
                showToast.accept("Erro ao excluir equipes: " + e.getMessage());
            }
        }
    }
    
    private void limparCampos() {
        equipeIdField.setText("");
        equipeNomeField.setText("");
        equipeDescricaoField.setText("");
        
        // Voltar ao modo criação
        if (isEditMode) {
            JPanel formWrapper = (JPanel) ((JPanel) getComponent(0)).getComponent(1);
            JPanel fieldsPanel = (JPanel) formWrapper.getComponent(1);
            JPanel buttonPanel = (JPanel) fieldsPanel.getComponent(fieldsPanel.getComponentCount() - 1);
            JButton criarBtn = (JButton) buttonPanel.getComponent(0);
            JButton salvarBtn = (JButton) buttonPanel.getComponent(1);
            
            criarBtn.setVisible(true);
            salvarBtn.setVisible(false);
            isEditMode = false;
        }
    }
    
    private void refreshTeamList() {
        try {
            EquipeController equipeController = new EquipeController();
            List<Equipe> equipes = equipeController.listarTodasEquipes();
            
            equipeTableModel.setRowCount(0);
            
            for (Equipe equipe : equipes) {
                Object[] rowData = {
                    equipe.getId(),
                    equipe.getNome(),
                    equipe.getDescricao(),
                    (equipe.getMembros() != null ? equipe.getMembros().size() : 0) + " membros"
                };
                equipeTableModel.addRow(rowData);
            }
        } catch (Exception e) {
            showToast.accept("Erro ao carregar equipes: " + e.getMessage());
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