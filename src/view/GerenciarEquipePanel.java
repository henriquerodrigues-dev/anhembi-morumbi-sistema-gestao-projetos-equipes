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
    private JButton criarEquipeButton;
    private JButton salvarEquipeButton;
    private JButton limparEquipeButton;
    
    public GerenciarEquipePanel(Consumer<String> toastCallback) {
        this.showToast = toastCallback;
        initializeComponents();
        setupLayout();
        refreshTeamList();
    }
    
    private void initializeComponents() {
        setBackground(Color.decode("#ECF0F1"));
        setLayout(new BorderLayout());
        
        // Inicializar campos
        equipeIdField = createStyledTextField();
        equipeIdField.setEditable(false);
        equipeIdField.setBackground(Color.decode("#F8F9FA"));
        equipeIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        equipeIdField.setPreferredSize(new Dimension(0, 35));

        equipeNomeField = createStyledTextField();
        equipeNomeField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        equipeNomeField.setPreferredSize(new Dimension(0, 35));

        equipeDescricaoField = createStyledTextField();
        equipeDescricaoField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        equipeDescricaoField.setPreferredSize(new Dimension(0, 35));
        
        // Configurar tabela
        String[] colunas = {"ID", "Nome", "Descrição", "Membros"};
        equipeTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        equipeTable = new JTable(equipeTableModel);
        equipeTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        equipeTable.setRowHeight(25);
        equipeTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        equipeTable.getTableHeader().setBackground(Color.decode("#34495E"));
        equipeTable.getTableHeader().setForeground(Color.WHITE);
        equipeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Ocultar coluna ID
        equipeTable.getColumnModel().getColumn(0).setMinWidth(0);
        equipeTable.getColumnModel().getColumn(0).setMaxWidth(0);
        equipeTable.getColumnModel().getColumn(0).setPreferredWidth(0);
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
        
        JPanel tablePanel = createTablePanel();
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.USERS_COG, 24, Color.decode("#2C3E50"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciar Equipes");
        titleText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleText.setForeground(Color.decode("#2C3E50"));
        
        titlePanel.add(iconLabel);
        titlePanel.add(titleText);
        titlePanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        return titlePanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JLabel formTitle = new JLabel("Cadastro/Edição de Equipe");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(Color.decode("#2C3E50"));
        formTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        formTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        formPanel.add(formTitle);
        formPanel.add(createFieldLabel("ID da Equipe:", FontAwesomeSolid.HASHTAG));
        formPanel.add(equipeIdField);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(createFieldLabel("Nome da Equipe:", FontAwesomeSolid.USERS));
        formPanel.add(equipeNomeField);
        formPanel.add(Box.createVerticalStrut(10));

        formPanel.add(createFieldLabel("Descrição:", FontAwesomeSolid.ALIGN_LEFT));
        formPanel.add(equipeDescricaoField);
        formPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        criarEquipeButton = createActionButton("Criar", FontAwesomeSolid.PLUS, Color.decode("#27AE60"));
        salvarEquipeButton = createActionButton("Salvar", FontAwesomeSolid.SAVE, Color.decode("#2ECC71"));
        limparEquipeButton = createActionButton("Limpar", FontAwesomeSolid.ERASER, Color.decode("#95A5A6"));

        salvarEquipeButton.setVisible(false);

        criarEquipeButton.addActionListener(e -> criarEquipe());
        salvarEquipeButton.addActionListener(e -> salvarEquipe());
        limparEquipeButton.addActionListener(e -> limparCampos());

        buttonPanel.add(criarEquipeButton);
        buttonPanel.add(salvarEquipeButton);
        buttonPanel.add(limparEquipeButton);

        formPanel.add(buttonPanel);

        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel tableTitle = new JLabel("Lista de Equipes");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(Color.decode("#2C3E50"));
        tableTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        tableTitle.setBorder(new EmptyBorder(0, 0, 20, 0));

        JScrollPane scrollPane = new JScrollPane(equipeTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1));

        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        tableButtonPanel.setOpaque(false);
        tableButtonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton editarBtn = createActionButton("Editar", FontAwesomeSolid.EDIT, Color.decode("#F39C12"));
        JButton excluirBtn = createActionButton("Excluir", FontAwesomeSolid.TRASH, Color.decode("#E74C3C"));
        JButton atualizarBtn = createActionButton("Atualizar", FontAwesomeSolid.SYNC_ALT, Color.decode("#3498DB"));

        editarBtn.addActionListener(e -> editarEquipe());
        excluirBtn.addActionListener(e -> excluirEquipes());
        atualizarBtn.addActionListener(e -> refreshTeamList());

        tableButtonPanel.add(editarBtn);
        tableButtonPanel.add(excluirBtn);
        tableButtonPanel.add(atualizarBtn);

        contentPanel.add(tableTitle);
        contentPanel.add(scrollPane);
        contentPanel.add(tableButtonPanel);

        tableWrapper.add(contentPanel, BorderLayout.CENTER);

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
        button.setPreferredSize(new Dimension(150, 50));
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);

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
        
        if (criarEquipeButton != null && salvarEquipeButton != null) {
            criarEquipeButton.setVisible(false);
            salvarEquipeButton.setVisible(true);
        }
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
            if (criarEquipeButton != null && salvarEquipeButton != null) {
                criarEquipeButton.setVisible(true);
                salvarEquipeButton.setVisible(false);
            }
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