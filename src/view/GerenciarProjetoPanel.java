package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.Ikon;

import com.toedter.calendar.JDateChooser;
import controller.ProjetoController;
import dao.UsuarioDAO;
import model.Projeto;
import model.Usuario;

/**
 * Painel para gerenciamento de projetos
 */
public class GerenciarProjetoPanel extends JPanel {
    
    private JTextField projetoIdField;
    private JTextField projetoNomeField;
    private JTextField projetoDescricaoField;
    private JTextField dataInicioField;
    private JTextField dataTerminoField;
    private JComboBox<String> statusComboBox;
    private JTextField gerenteSearchField;
    private JTable projetoTable;
    private DefaultTableModel projetoTableModel;
    private Consumer<String> toastCallback;
    private boolean isEditMode = false;
    
    public GerenciarProjetoPanel(Consumer<String> toastCallback) {
        this.toastCallback = toastCallback;
        initializeComponents();
        setupLayout();
        setupActions();
        refreshProjectList();
    }
    
    private void initializeComponents() {
        setBackground(Color.decode("#ECF0F1"));
        setLayout(new BorderLayout());
        
        // Inicializar campos
        projetoIdField = createStyledTextField();
        projetoIdField.setEditable(false);
        projetoIdField.setBackground(Color.decode("#F8F9FA"));
        
        projetoNomeField = createStyledTextField();
        projetoDescricaoField = createStyledTextField();
        
        // Campos de data simples
        dataInicioField = createStyledTextField();
        dataInicioField.setToolTipText("Digite a data no formato dd/mm/aaaa ou clique no calendário");
        
        dataTerminoField = createStyledTextField();
        dataTerminoField.setToolTipText("Digite a data no formato dd/mm/aaaa ou clique no calendário");
        
        // Status combo box
        String[] statusOptions = {"", "Pendente", "Em Andamento", "Concluído", "Cancelado", "Pausado"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setEditable(true);
        statusComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Campo de busca de gerente
        gerenteSearchField = createStyledTextField();
        gerenteSearchField.setToolTipText("Digite o nome do gerente");
        
        // Configurar tabela
        String[] colunas = {"ID", "Nome", "Descrição", "Data Início", "Data Término", "Status", "Gerente"};
        projetoTableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projetoTable = new JTable(projetoTableModel);
        projetoTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        projetoTable.setRowHeight(25);
        projetoTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        projetoTable.getTableHeader().setBackground(Color.decode("#34495E"));
        projetoTable.getTableHeader().setForeground(Color.WHITE);
        projetoTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        // Ocultar coluna ID
        projetoTable.getColumnModel().getColumn(0).setMinWidth(0);
        projetoTable.getColumnModel().getColumn(0).setMaxWidth(0);
        projetoTable.getColumnModel().getColumn(0).setWidth(0);
    }
    
    private void setupLayout() {
        // Container principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.decode("#ECF0F1"));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Título
        JPanel titlePanel = createTitlePanel();
        
        // Formulário e tabela lado a lado
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        
        JPanel formPanel = createFormPanel();
        JPanel tablePanel = createTablePanel();
        
        contentPanel.add(formPanel);
        contentPanel.add(tablePanel);
        
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        titlePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.PROJECT_DIAGRAM, 24, Color.decode("#2C3E50"));
        iconLabel.setIcon(icon);
        
        JLabel titleText = new JLabel("Gerenciar Projetos");
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
                
                // Sombra
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(12, 12, getWidth() - 24, getHeight() - 24, 20, 20);
                
                // Fundo branco
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        formWrapper.setOpaque(false);
        formWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        formWrapper.setLayout(new BorderLayout());
        
        // Título do formulário
        JLabel formTitle = new JLabel("Cadastro/Edição de Projeto");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        formTitle.setForeground(Color.decode("#2C3E50"));
        formTitle.setHorizontalAlignment(SwingConstants.CENTER);
        formTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Campos do formulário
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        formPanel.add(createFieldLabel("ID do Projeto:", FontAwesomeSolid.HASHTAG));
        formPanel.add(projetoIdField);
        formPanel.add(Box.createVerticalStrut(10));
        
        formPanel.add(createFieldLabel("Nome do Projeto:", FontAwesomeSolid.PROJECT_DIAGRAM));
        formPanel.add(projetoNomeField);
        formPanel.add(Box.createVerticalStrut(10));
        
        formPanel.add(createFieldLabel("Descrição:", FontAwesomeSolid.ALIGN_LEFT));
        formPanel.add(projetoDescricaoField);
        formPanel.add(Box.createVerticalStrut(10));
        
        formPanel.add(createFieldLabel("Data de Início:", FontAwesomeSolid.CALENDAR_ALT));
        formPanel.add(createDatePanel(dataInicioField));
        formPanel.add(Box.createVerticalStrut(10));
        
        formPanel.add(createFieldLabel("Data de Término:", FontAwesomeSolid.CALENDAR_CHECK));
        formPanel.add(createDatePanel(dataTerminoField));
        formPanel.add(Box.createVerticalStrut(10));
        
        formPanel.add(createFieldLabel("Status:", FontAwesomeSolid.TASKS));
        formPanel.add(statusComboBox);
        formPanel.add(Box.createVerticalStrut(10));
        
        formPanel.add(createFieldLabel("Gerente Responsável:", FontAwesomeSolid.USER_TIE));
        formPanel.add(createGerenteSearchPanel());
        
        // Botões
        JPanel buttonPanel = createFormButtonPanel();
        
        formWrapper.add(formTitle, BorderLayout.NORTH);
        formWrapper.add(formPanel, BorderLayout.CENTER);
        formWrapper.add(buttonPanel, BorderLayout.SOUTH);
        
        return formWrapper;
    }
    
    private JPanel createDatePanel(JTextField dateField) {
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setOpaque(false);
        
        JButton calendarBtn = new JButton();
        FontIcon calendarIcon = FontIcon.of(FontAwesomeSolid.CALENDAR_ALT, 16, Color.decode("#3498DB"));
        calendarBtn.setIcon(calendarIcon);
        calendarBtn.setPreferredSize(new Dimension(40, 35));
        calendarBtn.setBackground(Color.decode("#ECF0F1"));
        calendarBtn.setBorder(BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1));
        calendarBtn.setFocusPainted(false);
        calendarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        datePanel.add(dateField, BorderLayout.CENTER);
        datePanel.add(calendarBtn, BorderLayout.EAST);
        
        // Configurar ação do calendário
        calendarBtn.addActionListener(e -> openCalendar(dateField));
        
        return datePanel;
    }
    
    private JPanel createGerenteSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setOpaque(false);
        
        // Configurar busca em tempo real
        gerenteSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchGerente(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchGerente(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchGerente(); }
        });
        
        searchPanel.add(gerenteSearchField, BorderLayout.CENTER);
        
        return searchPanel;
    }
    
    private void searchGerente() {
        String searchTerm = gerenteSearchField.getText().toLowerCase().trim();
        
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
                        gerenteSearchField.setText(usuario.getNomeCompleto() + " (ID: " + usuario.getId() + ")");
                        popup.setVisible(false);
                    });
                    popup.add(item);
                    found = true;
                    
                    // Limitar a 5 resultados
                    if (popup.getComponentCount() >= 5) break;
                }
            }
            
            if (found) {
                popup.show(gerenteSearchField, 0, gerenteSearchField.getHeight());
            }
        } catch (Exception e) {
            // Ignorar erros de busca
        }
    }
    
    private void openCalendar(JTextField dateField) {
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setLocale(Locale.forLanguageTag("pt-BR"));
        
        int result = JOptionPane.showConfirmDialog(
            this,
            dateChooser,
            "Selecionar Data",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION && dateChooser.getDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateField.setText(sdf.format(dateChooser.getDate()));
        }
    }
    
    private JPanel createFormButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton criarBtn = createIconActionButton(FontAwesomeSolid.PLUS, "Criar", Color.decode("#27AE60"));
        JButton salvarBtn = createIconActionButton(FontAwesomeSolid.SAVE, "Salvar", Color.decode("#2ECC71"));
        JButton limparBtn = createIconActionButton(FontAwesomeSolid.ERASER, "Limpar", Color.decode("#95A5A6"));
        
        // Inicialmente mostrar apenas o botão Criar
        salvarBtn.setVisible(false);
        
        buttonPanel.add(criarBtn);
        buttonPanel.add(salvarBtn);
        buttonPanel.add(limparBtn);
        
        return buttonPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tableWrapper = new JPanel() {
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
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        tableWrapper.setLayout(new BorderLayout());
        
        // Título da tabela
        JLabel tableTitle = new JLabel("Lista de Projetos");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(Color.decode("#2C3E50"));
        tableTitle.setHorizontalAlignment(SwingConstants.CENTER);
        tableTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Tabela
        JScrollPane scrollPane = new JScrollPane(projetoTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#BDC3C7"), 1));
        
        // Botões da tabela
        JPanel tableButtonPanel = createTableButtonPanel();
        
        tableWrapper.add(tableTitle, BorderLayout.NORTH);
        tableWrapper.add(scrollPane, BorderLayout.CENTER);
        tableWrapper.add(tableButtonPanel, BorderLayout.SOUTH);
        
        return tableWrapper;
    }
    
    private JPanel createTableButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        JButton editarBtn = createIconActionButton(FontAwesomeSolid.EDIT, "Editar", Color.decode("#F39C12"));
        JButton excluirBtn = createIconActionButton(FontAwesomeSolid.TRASH, "Excluir", Color.decode("#E74C3C"));
        JButton atualizarBtn = createIconActionButton(FontAwesomeSolid.SYNC_ALT, "Atualizar", Color.decode("#3498DB"));
        
        buttonPanel.add(editarBtn);
        buttonPanel.add(excluirBtn);
        buttonPanel.add(atualizarBtn);
        
        return buttonPanel;
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
        
        if (iconCode != null) {
            FontIcon icon = FontIcon.of(iconCode, 16, Color.WHITE);
            button.setIcon(icon);
        }
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
        // Obter referências dos botões
        JPanel formWrapper = (JPanel) ((JPanel) ((JPanel) getComponent(0)).getComponent(1)).getComponent(0);
        JPanel formButtonPanel = (JPanel) formWrapper.getComponent(2);
        JButton criarBtn = (JButton) formButtonPanel.getComponent(0);
        JButton salvarBtn = (JButton) formButtonPanel.getComponent(1);
        JButton limparBtn = (JButton) formButtonPanel.getComponent(2);
        
        JPanel tableWrapper = (JPanel) ((JPanel) ((JPanel) getComponent(0)).getComponent(1)).getComponent(1);
        JPanel tableButtonPanel = (JPanel) tableWrapper.getComponent(2);
        JButton editarBtn = (JButton) tableButtonPanel.getComponent(0);
        JButton excluirBtn = (JButton) tableButtonPanel.getComponent(1);
        JButton atualizarBtn = (JButton) tableButtonPanel.getComponent(2);
        
        // Ações dos botões do formulário
        criarBtn.addActionListener(e -> criarProjeto());
        salvarBtn.addActionListener(e -> salvarProjeto());
        limparBtn.addActionListener(e -> limparCampos());
        
        // Ações dos botões da tabela
        editarBtn.addActionListener(e -> editarProjeto(criarBtn, salvarBtn));
        excluirBtn.addActionListener(e -> excluirProjetos());
        atualizarBtn.addActionListener(e -> refreshProjectList());
    }
    
    private void criarProjeto() {
        if (!validarCampos()) return;
        
        try {
            ProjetoController projetoController = new ProjetoController();
            String dataInicioFormatted = convertDateFormat(dataInicioField.getText().trim());
            String dataTerminoFormatted = dataTerminoField.getText().trim().isEmpty() ? 
                                        "" : convertDateFormat(dataTerminoField.getText().trim());
            
            if (dataInicioFormatted == null) {
                showToast("Data de início inválida! Use o formato dd/mm/aaaa", "error");
                return;
            }
            
            if (!dataTerminoField.getText().trim().isEmpty() && dataTerminoFormatted == null) {
                showToast("Data de término inválida! Use o formato dd/mm/aaaa", "error");
                return;
            }
            
            String status = statusComboBox.getSelectedItem() != null ? 
                           statusComboBox.getSelectedItem().toString().trim() : "";
            String gerenteId = getManagerIdFromSearchField();
            
            String erro = projetoController.criarProjeto(
                projetoNomeField.getText().trim(),
                projetoDescricaoField.getText().trim(),
                dataInicioFormatted,
                dataTerminoFormatted,
                status,
                gerenteId
            );
            
            if (erro == null) {
                showToast("Projeto criado com sucesso!", "success");
                limparCampos();
                refreshProjectList();
            } else {
                showToast(erro, "error");
            }
        } catch (Exception ex) {
            showToast("Erro ao criar projeto: " + ex.getMessage(), "error");
        }
    }
    
    private void salvarProjeto() {
        if (!isEditMode) {
            showToast("Nenhum projeto selecionado para editar", "warning");
            return;
        }
        
        if (!validarCampos()) return;
        
        try {
            ProjetoController projetoController = new ProjetoController();
            String dataInicioFormatted = convertDateFormat(dataInicioField.getText().trim());
            String dataTerminoFormatted = dataTerminoField.getText().trim().isEmpty() ? 
                                        "" : convertDateFormat(dataTerminoField.getText().trim());
            
            if (dataInicioFormatted == null) {
                showToast("Data de início inválida! Use o formato dd/mm/aaaa", "error");
                return;
            }
            
            if (!dataTerminoField.getText().trim().isEmpty() && dataTerminoFormatted == null) {
                showToast("Data de término inválida! Use o formato dd/mm/aaaa", "error");
                return;
            }
            
            String status = statusComboBox.getSelectedItem() != null ? 
                           statusComboBox.getSelectedItem().toString().trim() : "";
            String gerenteId = getManagerIdFromSearchField();
            
            String projetoId = projetoIdField.getText().trim();
            String erro = projetoController.atualizarProjeto(
                projetoId,
                projetoNomeField.getText().trim(),
                projetoDescricaoField.getText().trim(),
                dataInicioFormatted,
                dataTerminoFormatted,
                status,
                gerenteId
            );
            
            if (erro == null) {
                showToast("Projeto atualizado com sucesso!", "success");
                limparCampos();
                refreshProjectList();
            } else {
                showToast(erro, "error");
            }
        } catch (Exception ex) {
            showToast("Erro ao atualizar projeto: " + ex.getMessage(), "error");
        }
    }
    
    private void editarProjeto(JButton criarBtn, JButton salvarBtn) {
        int selectedRow = projetoTable.getSelectedRow();
        if (selectedRow == -1) {
            showToast("Selecione um projeto para editar", "warning");
            return;
        }
        
        if (projetoTable.getSelectedRowCount() > 1) {
            showToast("Selecione apenas um projeto para editar", "warning");
            return;
        }
        
        // Preencher campos com dados do projeto selecionado
        String id = (String) projetoTableModel.getValueAt(selectedRow, 0);
        String nome = (String) projetoTableModel.getValueAt(selectedRow, 1);
        String descricao = (String) projetoTableModel.getValueAt(selectedRow, 2);
        String dataInicio = (String) projetoTableModel.getValueAt(selectedRow, 3);
        String dataTermino = (String) projetoTableModel.getValueAt(selectedRow, 4);
        String status = (String) projetoTableModel.getValueAt(selectedRow, 5);
        String gerente = (String) projetoTableModel.getValueAt(selectedRow, 6);
        
        projetoIdField.setText(id);
        projetoNomeField.setText(nome);
        projetoDescricaoField.setText(descricao);
        dataInicioField.setText(convertDateFormatReverse(dataInicio));
        dataTerminoField.setText(convertDateFormatReverse(dataTermino));
        statusComboBox.setSelectedItem(status);
        gerenteSearchField.setText(gerente);
        
        // Alterar botões para modo edição
        criarBtn.setVisible(false);
        salvarBtn.setVisible(true);
        isEditMode = true;
        
        showToast("Projeto carregado para edição", "info");
    }
    
    private void excluirProjetos() {
        int[] selectedRows = projetoTable.getSelectedRows();
        if (selectedRows.length == 0) {
            showToast("Selecione pelo menos um projeto para excluir", "warning");
            return;
        }
        
        String message = selectedRows.length == 1 ? 
            "Tem certeza que deseja excluir este projeto?" :
            "Tem certeza que deseja excluir " + selectedRows.length + " projetos?";
        
        int confirmacao = JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            try {
                ProjetoController projetoController = new ProjetoController();
                int deletedCount = 0;
                
                for (int row : selectedRows) {
                    String projetoId = (String) projetoTableModel.getValueAt(row, 0);
                    String resultado = projetoController.excluirProjeto(projetoId);
                    if (resultado == null || resultado.toLowerCase().contains("sucesso")) {
                        deletedCount++;
                    }
                }
                
                showToast(deletedCount + " projeto(s) excluído(s) com sucesso!", "success");
                refreshProjectList();
                
            } catch (Exception ex) {
                showToast("Erro ao excluir projeto(s): " + ex.getMessage(), "error");
            }
        }
    }
    
    private void limparCampos() {
        projetoIdField.setText("");
        projetoNomeField.setText("");
        projetoDescricaoField.setText("");
        dataInicioField.setText("");
        dataTerminoField.setText("");
        statusComboBox.setSelectedIndex(0);
        gerenteSearchField.setText("");
        
        // Voltar ao modo criação
        if (isEditMode) {
            JPanel formWrapper = (JPanel) ((JPanel) ((JPanel) getComponent(0)).getComponent(1)).getComponent(0);
            JPanel formButtonPanel = (JPanel) formWrapper.getComponent(2);
            JButton criarBtn = (JButton) formButtonPanel.getComponent(0);
            JButton salvarBtn = (JButton) formButtonPanel.getComponent(1);
            
            criarBtn.setVisible(true);
            salvarBtn.setVisible(false);
            isEditMode = false;
        }
    }
    
    private boolean validarCampos() {
        if (projetoNomeField.getText().trim().isEmpty()) {
            showToast("Nome do projeto é obrigatório!", "error");
            return false;
        }
        
        if (projetoDescricaoField.getText().trim().isEmpty()) {
            showToast("Descrição do projeto é obrigatória!", "error");
            return false;
        }
        
        if (dataInicioField.getText().trim().isEmpty()) {
            showToast("Data de início é obrigatória!", "error");
            return false;
        }
        
        return true;
    }
    
    private String convertDateFormat(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return null;
        }
    }
    
    private String convertDateFormatReverse(String dateStr) {
        try {
            if (dateStr == null || dateStr.trim().isEmpty()) {
                return "";
            }
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
            java.util.Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    private String getManagerIdFromSearchField() {
        String managerText = gerenteSearchField.getText().trim();
        if (managerText.isEmpty()) return "";
        
        try {
            // Se contém "(ID: xxx)", extrair o ID diretamente
            if (managerText.contains("(ID: ")) {
                int start = managerText.lastIndexOf("(ID: ") + 5;
                int end = managerText.lastIndexOf(")");
                if (start > 4 && end > start) {
                    return managerText.substring(start, end);
                }
            }
            
            // Caso contrário, buscar por nome
            UsuarioDAO usuarioDAO = new UsuarioDAO();
            List<Usuario> usuarios = usuarioDAO.findAll();
            
            for (Usuario usuario : usuarios) {
                if (usuario.getNomeCompleto().toLowerCase().contains(managerText.toLowerCase())) {
                    return usuario.getId();
                }
            }
        } catch (Exception e) {
            // Ignorar erro
        }
        
        return "";
    }
    
    private void refreshProjectList() {
        try {
            ProjetoController projetoController = new ProjetoController();
            List<Projeto> projetos = projetoController.listarTodosProjetos();
            
            // Limpar tabela
            projetoTableModel.setRowCount(0);
            
            // Adicionar projetos
            for (Projeto projeto : projetos) {
                // Buscar nome do gerente
                String gerenteNome = "";
                if (projeto.getGerenteResponsavel() != null) {
                    try {
                        Usuario gerente = projeto.getGerenteResponsavel();
                        if (gerente != null) {
                            gerenteNome = gerente.getNomeCompleto();
                        }
                    } catch (Exception e) {
                        // Ignorar erro
                    }
                }
                
                Object[] row = {
                    projeto.getId(),
                    projeto.getNome(),
                    projeto.getDescricao(),
                    projeto.getDataInicio() != null ? projeto.getDataInicio().toString() : "",
                    projeto.getDataTerminoPrevista() != null ? projeto.getDataTerminoPrevista().toString() : "",
                    projeto.getStatus() != null ? projeto.getStatus() : "",
                    gerenteNome
                };
                projetoTableModel.addRow(row);
            }
        } catch (Exception ex) {
            showToast("Erro ao carregar projetos: " + ex.getMessage(), "error");
        }
    }
    
    private void showToast(String message, String type) {
        if (toastCallback != null) {
            toastCallback.accept(type + ":" + message);
        }
    }
    
    public void refresh() {
        refreshProjectList();
    }
}
