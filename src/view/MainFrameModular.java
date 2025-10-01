package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

import model.Usuario;

/**
 * MainFrame modular usando os novos painéis separados
 */
public class MainFrameModular extends JFrame {
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private CadastroUsuarioPanel cadastroUsuarioPanel;
    private GerenciarUsuarioPanel gerenciarUsuarioPanel;
    private GerenciarProjetoPanel gerenciarProjetoPanel;
    private GerenciarEquipePanel gerenciarEquipePanel;
    private GerenciarMembrosPanel gerenciarMembrosPanel;
    private JPanel homePanel;
    private JPanel helpPanel;
    private JPanel sidebar;
    
    // Toast system
    private JLayeredPane layeredPane;
    private JPanel contentWrapper;
    private static class ToastEntry {
        JPanel toastPanel;
        Timer autoCloseTimer;
        Timer progressTimer;
        JProgressBar progressBar;
        long startTime;
        long duration;
    }

    private static class ToastRequest {
        final String message;
        final Color background;
        final FontAwesomeSolid iconCode;

        ToastRequest(String message, Color background, FontAwesomeSolid iconCode) {
            this.message = message;
            this.background = background;
            this.iconCode = iconCode;
        }
    }

    private static final int MAX_VISIBLE_TOASTS = 5;
    private final List<ToastEntry> activeToasts = new ArrayList<>();
    private final Deque<ToastRequest> toastQueue = new ArrayDeque<>();
    
    public MainFrameModular() {
        initializeFrame();
        createToastSystem();
        createPanels();
        createSidebar();
        createHomePanel();
        createHelpPanel();
        setupLayout();
        
        // Mostrar tela inicial
        cardLayout.show(contentPanel, "home");
    }
    
    private void initializeFrame() {
        setTitle("Sistema de Gestão de Projetos e Equipes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Ícone da aplicação (removido por compatibilidade)
    }
    
    private void createToastSystem() {
        layeredPane = new JLayeredPane();
        layeredPane.setOpaque(false);
        layeredPane.setLayout(null);
    }
    
    private void createPanels() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        // Criar callback para toasts
        Consumer<String> toastCallback = (message) -> {
            String[] parts = message.split(":", 2);
            if (parts.length == 2) {
                showToast(parts[1], parts[0]);
            } else {
                showToast(message, "info");
            }
        };
        
        // Criar callback para refresh entre painéis
        Runnable refreshUserListCallback = () -> {
            if (gerenciarUsuarioPanel != null) {
                gerenciarUsuarioPanel.refresh();
            }
        };
        
        // Criar callback para navegar para gerenciar usuários
        Runnable navigateToUserManagementCallback = () -> {
            cardLayout.show(contentPanel, "usuarios");
            if (gerenciarUsuarioPanel != null) {
                gerenciarUsuarioPanel.refresh();
            }
        };
        
        // Criar callback para edição de usuário
        Consumer<Usuario> editUserCallback = (usuario) -> {
            if (cadastroUsuarioPanel != null) {
                cadastroUsuarioPanel.carregarUsuarioParaEdicao(usuario);
                cardLayout.show(contentPanel, "cadastro");
            }
        };
        
        // Inicializar painéis
        cadastroUsuarioPanel = new CadastroUsuarioPanel(toastCallback, refreshUserListCallback, navigateToUserManagementCallback);
        gerenciarUsuarioPanel = new GerenciarUsuarioPanel(toastCallback, editUserCallback);
        gerenciarProjetoPanel = new GerenciarProjetoPanel(toastCallback);
        gerenciarEquipePanel = new GerenciarEquipePanel(toastCallback);
        gerenciarMembrosPanel = new GerenciarMembrosPanel(toastCallback);
        
        // Adicionar painéis ao CardLayout
        contentPanel.add(cadastroUsuarioPanel, "cadastro");
        contentPanel.add(gerenciarUsuarioPanel, "usuarios");
        contentPanel.add(gerenciarProjetoPanel, "projetos");
        contentPanel.add(gerenciarEquipePanel, "equipes");
        contentPanel.add(gerenciarMembrosPanel, "membros");
    }
    
    private void createHomePanel() {
        homePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#ECF0F1"),
                    0, getHeight(), Color.decode("#D5DBDB")
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        homePanel.setLayout(new BorderLayout());
        
        // Conteúdo da home
        JPanel welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(new EmptyBorder(100, 50, 100, 50));
        
        // Título principal
        JLabel titleLabel = new JLabel("Sistema de Gestão de Projetos e Equipes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(Color.decode("#2C3E50"));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Subtítulo
        JLabel subtitleLabel = new JLabel("Bem-vindo ao sistema completo de gerenciamento");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(Color.decode("#34495E"));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Descrição
        JTextArea descriptionArea = new JTextArea(
            "Gerencie sua operação ponta a ponta com um hub único.\n\n" +
            "• Cadastre usuários rapidamente e mantenha os dados sempre atualizados.\n" +
            "• Crie projetos com status, prazos e responsáveis em poucos cliques.\n" +
            "• Monte equipes, distribua membros e acompanhe a evolução do time.\n" +
            "• Receba notificações inteligentes com feedback visual instantâneo.\n\n" +
            "Dica: use o menu lateral para navegar nos módulos e o botão Atualizar para listas sincronizadas."
        );
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descriptionArea.setForeground(Color.decode("#5D6D7E"));
        descriptionArea.setOpaque(false);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionArea.setMaximumSize(new Dimension(720, 260));

        JPanel cardsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setMaximumSize(new Dimension(1000, 200));
        cardsPanel.add(createHomeCard("Usuários", "Cadastre, edite e aplique buscas inteligentes com validações em tempo real."));
        cardsPanel.add(createHomeCard("Projetos", "Controle status, prazos e responsáveis com alertas visuais instantâneos."));
        cardsPanel.add(createHomeCard("Equipes", "Monte squads, distribua membros e acompanhe a evolução do time."));

        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createVerticalStrut(15));
        welcomePanel.add(subtitleLabel);
        welcomePanel.add(Box.createVerticalStrut(25));
        welcomePanel.add(descriptionArea);
        welcomePanel.add(Box.createVerticalStrut(35));
        welcomePanel.add(cardsPanel);

        homePanel.add(welcomePanel, BorderLayout.CENTER);
        contentPanel.add(homePanel, "home");
    }

    private JPanel createHomeCard(String title, String description) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(255, 255, 255, 230));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            }
        };
        card.setOpaque(false);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel cardTitle = new JLabel(title);
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(Color.decode("#2C3E50"));

        JTextArea cardDescription = new JTextArea(description);
        cardDescription.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cardDescription.setForeground(Color.decode("#5D6D7E"));
        cardDescription.setOpaque(false);
        cardDescription.setEditable(false);
        cardDescription.setLineWrap(true);
        cardDescription.setWrapStyleWord(true);

        card.add(cardTitle, BorderLayout.NORTH);
        card.add(cardDescription, BorderLayout.CENTER);

        return card;
    }

    private void createHelpPanel() {
        helpPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#ECF0F1"),
                    0, getHeight(), Color.decode("#D5DBDB")
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        helpPanel.setLayout(new BorderLayout());

        JPanel helpContent = new JPanel();
        helpContent.setOpaque(false);
        helpContent.setLayout(new BoxLayout(helpContent, BoxLayout.Y_AXIS));
        helpContent.setBorder(new EmptyBorder(50, 60, 50, 60));

        JLabel helpTitle = new JLabel("Central de Ajuda");
        helpTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        helpTitle.setForeground(Color.decode("#2C3E50"));
        helpTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea helpIntro = new JTextArea(
            "Explore cada módulo com confiança:\n\n" +
            "• Início: visão geral com os principais atalhos do dia a dia.\n" +
            "• Usuários: cadastre e mantenha a base da organização sempre atualizada.\n" +
            "• Projetos: acompanhe o ciclo completo com status, prazos e responsáveis.\n" +
            "• Equipes: estruture squads, organize membros e garanta colaboração contínua.\n" +
            "• Membros: integre usuários às equipes e gerencie alocações em segundos."
        );
        helpIntro.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        helpIntro.setForeground(Color.decode("#5D6D7E"));
        helpIntro.setOpaque(false);
        helpIntro.setEditable(false);
        helpIntro.setLineWrap(true);
        helpIntro.setWrapStyleWord(true);
        helpIntro.setAlignmentX(Component.CENTER_ALIGNMENT);
        helpIntro.setMaximumSize(new Dimension(800, 160));

        JTextArea helpSteps = new JTextArea(
            "PASSO A PASSO RÁPIDO:\n\n" +
            "1. Cadastro de Usuário\n" +
            "   • Informe nome, CPF, e-mail, cargo e credenciais.\n" +
            "   • Use o ícone do olho para alternar a visibilidade da senha.\n" +
            "   • Após salvar, você pode navegar automaticamente para a lista de usuários.\n\n" +
            "2. Gerenciar Usuários\n" +
            "   • Utilize a busca em tempo real para localizar registros.\n" +
            "   • Selecione múltiplos usuários para exclusão em lote.\n" +
            "   • Clique em Editar para enviar o usuário ao formulário de cadastro.\n\n" +
            "3. Gerenciar Projetos\n" +
            "   • Informe datas no formato dd/mm/aaaa ou use o seletor de calendário.\n" +
            "   • Escolha o status ou digite um novo conforme a sua metodologia.\n" +
            "   • Busque o gerente por nome e selecione na lista suspensa.\n\n" +
            "4. Gerenciar Equipes\n" +
            "   • Crie ou edite equipes com nome e descrição alinhados à área.\n" +
            "   • Limpe o formulário com um clique para iniciar novo cadastro.\n" +
            "   • Use Atualizar para garantir que a tabela reflita o estado mais recente.\n\n" +
            "5. Gerenciar Membros\n" +
            "   • Selecione a equipe desejada e insira usuários por nome ou ID.\n" +
            "   • Evite duplicidades: o sistema verifica automaticamente.\n" +
            "   • Utilize os botões superiores para adicionar, remover e atualizar rapidamente.\n\n" +
            "DICAS FINAIS:\n" +
            "• Receba feedback instantâneo através dos toasts com temporizador visual.\n" +
            "• Liste dados atualizados com os botões Atualizar presentes em cada tela.\n" +
            "• Combine filtros de busca para localizar usuários, projetos ou equipes em segundos."
        );
        helpSteps.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        helpSteps.setForeground(Color.decode("#34495E"));
        helpSteps.setOpaque(false);
        helpSteps.setEditable(false);
        helpSteps.setLineWrap(true);
        helpSteps.setWrapStyleWord(true);
        helpSteps.setAlignmentX(Component.CENTER_ALIGNMENT);

        JScrollPane helpScrollPane = new JScrollPane(helpSteps);
        helpScrollPane.setOpaque(false);
        helpScrollPane.getViewport().setOpaque(false);
        helpScrollPane.setBorder(null);
        helpScrollPane.setPreferredSize(new Dimension(820, 420));

        helpContent.add(helpTitle);
        helpContent.add(Box.createVerticalStrut(20));
        helpContent.add(helpIntro);
        helpContent.add(Box.createVerticalStrut(15));
        helpContent.add(helpScrollPane);

        helpPanel.add(helpContent, BorderLayout.CENTER);
        contentPanel.add(helpPanel, "help");
    }
    
    private void createSidebar() {
        sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, Color.decode("#2C3E50"),
                    0, getHeight(), Color.decode("#34495E")
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Logo/Título do sistema
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(250, 80));
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        
        JLabel logoIcon = new JLabel();
        FontIcon icon = FontIcon.of(FontAwesomeSolid.PROJECT_DIAGRAM, 32, Color.WHITE);
        logoIcon.setIcon(icon);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel logoText = new JLabel("SGP&E");
        logoText.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logoText.setForeground(Color.WHITE);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoPanel.add(logoIcon);
        logoPanel.add(Box.createVerticalStrut(5));
        logoPanel.add(logoText);
        
        sidebar.add(logoPanel);
        sidebar.add(Box.createVerticalStrut(30));
        
        // Botões do menu
        sidebar.add(createMenuButton("Início", FontAwesomeSolid.HOME, "home"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("Cadastrar Usuário", FontAwesomeSolid.USER_PLUS, "cadastro"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("Gerenciar Usuários", FontAwesomeSolid.USERS, "usuarios"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("Gerenciar Projetos", FontAwesomeSolid.PROJECT_DIAGRAM, "projetos"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("Gerenciar Equipes", FontAwesomeSolid.USERS_COG, "equipes"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("Gerenciar Membros", FontAwesomeSolid.USER_COG, "membros"));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuButton("Ajuda", FontAwesomeSolid.QUESTION_CIRCLE, "help"));
        
        sidebar.add(Box.createVerticalGlue());
        
        // Não adicionar aqui - será adicionado no setupLayout()
    }
    
    private JButton createMenuButton(String text, FontAwesomeSolid iconCode, String panelName) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(255, 255, 255, 40));
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 255, 20));
                } else {
                    g2d.setColor(new Color(0, 0, 0, 0));
                }
                
                g2d.fillRoundRect(5, 0, getWidth() - 10, getHeight(), 10, 10);
                
                // Ícone e texto
                FontIcon icon = FontIcon.of(iconCode, 16, Color.WHITE);
                int iconX = 20;
                int iconY = (getHeight() - 16) / 2;
                icon.paintIcon(this, g2d, iconX, iconY);
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = iconX + 25;
                int textY = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(240, 45));
        button.setMaximumSize(new Dimension(240, 45));
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, panelName);
            
            // Refresh do painel se necessário
            switch (panelName) {
                case "usuarios":
                    gerenciarUsuarioPanel.refresh();
                    break;
                case "projetos":
                    // gerenciarProjetoPanel.refresh(); // Método não existe
                    break;
                case "equipes":
                    // gerenciarEquipePanel.refresh(); // Método não existe
                    break;
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Adicionar layered pane como content pane
        contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        layeredPane.add(contentWrapper, JLayeredPane.DEFAULT_LAYER);
        contentWrapper.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                contentWrapper.setBounds(0, 0, layeredPane.getWidth(), layeredPane.getHeight());
                repositionToasts();
            }
        });

        // Adicionar sidebar e content panel
        add(sidebar, BorderLayout.WEST);
        add(layeredPane, BorderLayout.CENTER);
    }
    
    /**
     * Sistema de Toast Notifications
     */
    private void showToast(String message, String type) {
        // Parse do tipo e mensagem
        String[] parts = message.split(":", 2);
        if (parts.length == 2) {
            type = parts[0];
            message = parts[1];
        }
        
        Color bgColor;
        FontAwesomeSolid iconCode;
        
        switch (type.toLowerCase()) {
            case "success":
                bgColor = Color.decode("#27AE60");
                iconCode = FontAwesomeSolid.CHECK_CIRCLE;
                break;
            case "error":
                bgColor = Color.decode("#E74C3C");
                iconCode = FontAwesomeSolid.EXCLAMATION_CIRCLE;
                break;
            case "warning":
                bgColor = Color.decode("#F39C12");
                iconCode = FontAwesomeSolid.EXCLAMATION_TRIANGLE;
                break;
            case "info":
            default:
                bgColor = Color.decode("#3498DB");
                iconCode = FontAwesomeSolid.INFO_CIRCLE;
                break;
        }
        
        enqueueToast(message, bgColor, iconCode);
    }
    
    private void enqueueToast(String message, Color bgColor, FontAwesomeSolid iconCode) {
        ToastRequest request = new ToastRequest(message, bgColor, iconCode);
        toastQueue.offer(request);
        processToastQueue();
    }

    private void processToastQueue() {
        while (activeToasts.size() < MAX_VISIBLE_TOASTS && !toastQueue.isEmpty()) {
            ToastRequest request = toastQueue.poll();
            if (request != null) {
                createSingleToast(request);
            }
        }
        repositionToasts();
    }

    private void createSingleToast(ToastRequest request) {
        String message = request.message;
        Color bgColor = request.background;
        FontAwesomeSolid iconCode = request.iconCode;

        final ToastEntry entry = new ToastEntry();

        JPanel toast = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Sombra
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                
                // Fundo
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
            }
        };
        toast.setOpaque(false);
        toast.setLayout(new BorderLayout(5, 0));
        
        // Ícone
        JLabel iconLabel = new JLabel();
        FontIcon icon = FontIcon.of(iconCode, 20, Color.WHITE);
        iconLabel.setIcon(icon);
        iconLabel.setBorder(new EmptyBorder(10, 15, 10, 10));
        
        // Mensagem com quebra de linha automática
        JTextArea messageArea = new JTextArea(message);
        messageArea.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageArea.setForeground(Color.WHITE);
        messageArea.setOpaque(false);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBorder(new EmptyBorder(10, 0, 10, 10));
        
        // Barra de progresso
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(100);
        progressBar.setForeground(Color.WHITE);
        progressBar.setBackground(new Color(255, 255, 255, 50));
        progressBar.setBorder(new EmptyBorder(0, 0, 0, 0));
        progressBar.setPreferredSize(new Dimension(0, 4));
        progressBar.setStringPainted(false);
        
        // Conteúdo central com botão fechar e barra
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.add(messageArea, BorderLayout.CENTER);
        contentPanel.add(progressBar, BorderLayout.SOUTH);
        
        // Botão fechar
        JButton closeButton = new JButton("×");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(30, 30));
        closeButton.addActionListener(e -> removeToast(entry));
        
        // Adicionar componentes
        toast.add(iconLabel, BorderLayout.WEST);
        toast.add(contentPanel, BorderLayout.CENTER);
        toast.add(closeButton, BorderLayout.EAST);
        
        // Calcular tamanho dinâmico
        int toastHeight = calculateToastHeight(message);
        toast.setSize(380, toastHeight);
        
        // Criar entrada de toast
        entry.toastPanel = toast;
        entry.progressBar = progressBar;
        entry.startTime = System.currentTimeMillis();
        entry.duration = 3500L; // 3.5 segundos
        
        // Timer de progresso (atualiza a barra a cada 50ms)
        entry.progressTimer = new Timer(50, e -> updateToastProgress(entry));
        entry.progressTimer.start();
        
        // Timer de auto-fechamento
        entry.autoCloseTimer = new Timer((int) entry.duration, e -> removeToast(entry));
        entry.autoCloseTimer.setRepeats(false);
        entry.autoCloseTimer.start();
        
        // Adicionar à lista de toasts ativos
        activeToasts.add(entry);
        layeredPane.add(toast, JLayeredPane.POPUP_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }
    
    private void updateToastProgress(ToastEntry entry) {
        long elapsed = System.currentTimeMillis() - entry.startTime;
        float remaining = Math.max(0f, 1f - (float) elapsed / (float) entry.duration);
        int percentage = Math.max(0, Math.min(100, Math.round(remaining * 100)));
        entry.progressBar.setValue(percentage);
        entry.progressBar.repaint();
    }

    private void removeToast(ToastEntry entry) {
        if (entry == null) return;

        if (entry.autoCloseTimer != null) {
            entry.autoCloseTimer.stop();
        }
        if (entry.progressTimer != null) {
            entry.progressTimer.stop();
        }
        activeToasts.remove(entry);
        layeredPane.remove(entry.toastPanel);
        layeredPane.revalidate();
        layeredPane.repaint();

        processToastQueue();
        repositionToasts();
    }
    
    private void repositionToasts() {
        int yOffset = 20;
        Rectangle bounds = contentWrapper.getBounds();
        int rightEdge = bounds.x + bounds.width;
        for (ToastEntry entry : activeToasts) {
            JPanel toast = entry.toastPanel;
            toast.setLocation(rightEdge - toast.getWidth() - 20, yOffset);
            yOffset += toast.getHeight() + 10;
        }
        layeredPane.repaint();
    }
    
    private int calculateToastHeight(String message) {
        int baseHeight = 50;
        int charsPerLine = 40; // Aproximadamente
        int lines = Math.max(1, (message.length() / charsPerLine) + 1);
        return baseHeight + (lines - 1) * 20;
    }
}
