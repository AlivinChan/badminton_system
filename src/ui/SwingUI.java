package ui;

import entity.*;
import service.CourtService;
import service.ReservationService;
import service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Swing图形用户界面
 */
public class SwingUI extends JFrame {
    private UserService userService;
    private ReservationService reservationService;
    private CourtService courtService;
    private Student currentStudent;
    private Admin currentAdmin;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public SwingUI() {
        userService = new UserService();
        reservationService = new ReservationService();
        courtService = new CourtService();
        initializeData();
        initUI();
    }

    /**
     * 初始化测试数据
     */
    private void initializeData() {
        try {
            if (userService.loginAdmin("A001") == null) {
                dao.DataManager.getInstance().addAdmin(new Admin("A001", "管理员", "13800138000"));
            }
            if (courtService.getAllCourts().isEmpty()) {
                courtService.addCourt("C001", Court.CourtType.SINGLES, Court.CourtStatus.AVAILABLE);
                courtService.addCourt("C002", Court.CourtType.SINGLES, Court.CourtStatus.AVAILABLE);
                courtService.addCourt("C003", Court.CourtType.DOUBLES, Court.CourtStatus.AVAILABLE);
                courtService.addCourt("C004", Court.CourtType.DOUBLES, Court.CourtStatus.AVAILABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化界面
     */
    private void initUI() {
        setTitle("校园羽毛球馆场地预约管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 创建各个面板
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createStudentPanel(), "STUDENT");
        mainPanel.add(createAdminPanel(), "ADMIN");

        add(mainPanel);
        cardLayout.show(mainPanel, "LOGIN");
    }

    /**
     * 创建登录面板
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titleLabel = new JLabel("校园羽毛球馆场地预约管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        centerPanel.add(new JLabel("学号/工号:"), gbc);
        JTextField idField = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(idField, gbc);

        JButton studentLoginBtn = new JButton("学生登录") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getWidth() <= 0 || getHeight() <= 0) return;
                
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // 绘制白色背景
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // 绘制黑色边框
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    
                    // 绘制黑色文字
                    if (getText() != null && !getText().isEmpty()) {
                        g2.setColor(Color.BLACK);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(getText())) / 2;
                        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(getText(), x, y);
                    }
                } finally {
                    g2.dispose();
                }
            }
        };
        studentLoginBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        studentLoginBtn.setPreferredSize(new Dimension(120, 35));
        studentLoginBtn.setBackground(Color.WHITE);
        studentLoginBtn.setForeground(Color.BLACK);
        studentLoginBtn.setFocusPainted(false);
        studentLoginBtn.setBorderPainted(false);
        studentLoginBtn.setContentAreaFilled(false);
        studentLoginBtn.setOpaque(true);
        
        JButton adminLoginBtn = new JButton("管理员登录") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getWidth() <= 0 || getHeight() <= 0) return;
                
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // 绘制白色背景
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // 绘制黑色边框
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    
                    // 绘制黑色文字
                    if (getText() != null && !getText().isEmpty()) {
                        g2.setColor(Color.BLACK);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(getText())) / 2;
                        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(getText(), x, y);
                    }
                } finally {
                    g2.dispose();
                }
            }
        };
        adminLoginBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        adminLoginBtn.setPreferredSize(new Dimension(120, 35));
        adminLoginBtn.setBackground(Color.WHITE);
        adminLoginBtn.setForeground(Color.BLACK);
        adminLoginBtn.setFocusPainted(false);
        adminLoginBtn.setBorderPainted(false);
        adminLoginBtn.setContentAreaFilled(false);
        adminLoginBtn.setOpaque(true);
        
        JButton registerBtn = new JButton("学生注册") {
            @Override
            protected void paintComponent(Graphics g) {
                if (getWidth() <= 0 || getHeight() <= 0) return;
                
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // 绘制白色背景
                    g2.setColor(Color.WHITE);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                    
                    // 绘制黑色边框
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.0f));
                    g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                    
                    // 绘制黑色文字
                    if (getText() != null && !getText().isEmpty()) {
                        g2.setColor(Color.BLACK);
                        g2.setFont(getFont());
                        FontMetrics fm = g2.getFontMetrics();
                        int x = (getWidth() - fm.stringWidth(getText())) / 2;
                        int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                        g2.drawString(getText(), x, y);
                    }
                } finally {
                    g2.dispose();
                }
            }
        };
        registerBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerBtn.setPreferredSize(new Dimension(120, 35));
        registerBtn.setBackground(Color.WHITE);
        registerBtn.setForeground(Color.BLACK);
        registerBtn.setFocusPainted(false);
        registerBtn.setBorderPainted(false);
        registerBtn.setContentAreaFilled(false);
        registerBtn.setOpaque(true);

        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(registerBtn, gbc);
        gbc.gridx = 1;
        centerPanel.add(studentLoginBtn, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        centerPanel.add(adminLoginBtn, gbc);

        panel.add(centerPanel, BorderLayout.CENTER);

        // 学生登录
        studentLoginBtn.addActionListener(e -> {
            String id = idField.getText();
            try {
                currentStudent = userService.loginStudent(id);
                if (currentStudent != null) {
                    cardLayout.show(mainPanel, "STUDENT");
                    refreshStudentPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "学号不存在！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 管理员登录
        adminLoginBtn.addActionListener(e -> {
            String id = idField.getText();
            try {
                currentAdmin = userService.loginAdmin(id);
                if (currentAdmin != null) {
                    cardLayout.show(mainPanel, "ADMIN");
                    refreshAdminPanel();
                } else {
                    JOptionPane.showMessageDialog(this, "工号不存在！", "错误", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        // 学生注册
        registerBtn.addActionListener(e -> showRegisterDialog());

        return panel;
    }

    /**
     * 显示注册对话框
     */
    private void showRegisterDialog() {
        JDialog dialog = new JDialog(this, "学生注册", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("学号:"), gbc);
        JTextField studentIdField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("姓名:"), gbc);
        JTextField nameField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("联系电话:"), gbc);
        JTextField phoneField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        JButton registerBtn = new JButton("注册");
        registerBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        registerBtn.setPreferredSize(new Dimension(120, 35));
        registerBtn.setBackground(new Color(100, 150, 100));
        registerBtn.setForeground(Color.WHITE);
        registerBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(registerBtn, gbc);

        registerBtn.addActionListener(e -> {
            try {
                userService.registerStudent(
                        studentIdField.getText(),
                        nameField.getText(),
                        phoneField.getText());
                JOptionPane.showMessageDialog(dialog, "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * 创建学生面板
     */
    private JPanel createStudentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        topPanel.add(welcomeLabel);
        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logoutBtn.setPreferredSize(new Dimension(100, 30));
        logoutBtn.setBackground(new Color(200, 80, 80));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            currentStudent = null;
            cardLayout.show(mainPanel, "LOGIN");
        });
        topPanel.add(logoutBtn);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 个人信息标签页
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel[] infoLabels = {new JLabel(), new JLabel(), new JLabel()};
        for (int i = 0; i < infoLabels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            infoPanel.add(infoLabels[i], gbc);
        }
        tabbedPane.addTab("个人信息", infoPanel);

        // 可用场地标签页
        String[] courtColumns = {"场地编号", "场地类型", "状态"};
        DefaultTableModel courtModel = new DefaultTableModel(courtColumns, 0);
        JTable courtTable = new JTable(courtModel);
        JScrollPane courtScrollPane = new JScrollPane(courtTable);
        tabbedPane.addTab("可用场地", courtScrollPane);

        // 我的预约标签页
        String[] reservationColumns = {"预约编号", "场地编号", "开始时间", "结束时间", "状态"};
        DefaultTableModel reservationModel = new DefaultTableModel(reservationColumns, 0);
        JTable reservationTable = new JTable(reservationModel);
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        tabbedPane.addTab("我的预约", reservationScrollPane);

        // 预约场地标签页
        JPanel reservePanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        reservePanel.add(new JLabel("场地编号:"), gbc);
        JTextField courtIdField = new JTextField(15);
        gbc.gridx = 1;
        reservePanel.add(courtIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        reservePanel.add(new JLabel("开始时间 (yyyy-MM-dd HH:mm):"), gbc);
        JTextField startTimeField = new JTextField(15);
        gbc.gridx = 1;
        reservePanel.add(startTimeField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        reservePanel.add(new JLabel("结束时间 (yyyy-MM-dd HH:mm):"), gbc);
        JTextField endTimeField = new JTextField(15);
        gbc.gridx = 1;
        reservePanel.add(endTimeField, gbc);

        JButton reserveBtn = new JButton("预约");
        reserveBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        reserveBtn.setPreferredSize(new Dimension(120, 35));
        reserveBtn.setBackground(new Color(70, 130, 180));
        reserveBtn.setForeground(Color.WHITE);
        reserveBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        reservePanel.add(reserveBtn, gbc);

        JButton cancelReserveBtn = new JButton("取消预约");
        cancelReserveBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        cancelReserveBtn.setPreferredSize(new Dimension(120, 35));
        cancelReserveBtn.setBackground(new Color(200, 80, 80));
        cancelReserveBtn.setForeground(Color.WHITE);
        cancelReserveBtn.setFocusPainted(false);
        gbc.gridy = 4;
        reservePanel.add(cancelReserveBtn, gbc);

        reserveBtn.addActionListener(e -> {
            try {
                LocalDateTime startTime = LocalDateTime.parse(startTimeField.getText(), formatter);
                LocalDateTime endTime = LocalDateTime.parse(endTimeField.getText(), formatter);
                Reservation reservation = reservationService.makeReservation(
                        currentStudent.getStudentId(), courtIdField.getText(), startTime, endTime);
                JOptionPane.showMessageDialog(this, "预约成功！预约编号: " + reservation.getReservationId(),
                        "成功", JOptionPane.INFORMATION_MESSAGE);
                refreshStudentPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelReserveBtn.addActionListener(e -> {
            String reservationId = JOptionPane.showInputDialog(this, "请输入预约编号:");
            if (reservationId != null && !reservationId.trim().isEmpty()) {
                try {
                    reservationService.cancelReservation(reservationId, currentStudent.getStudentId());
                    JOptionPane.showMessageDialog(this, "取消预约成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    refreshStudentPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        tabbedPane.addTab("预约场地", reservePanel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        // 保存引用以便刷新
        panel.putClientProperty("welcomeLabel", welcomeLabel);
        panel.putClientProperty("infoLabels", infoLabels);
        panel.putClientProperty("courtModel", courtModel);
        panel.putClientProperty("reservationModel", reservationModel);

        return panel;
    }

    /**
     * 刷新学生面板
     */
    private void refreshStudentPanel() {
        JPanel studentPanel = (JPanel) mainPanel.getComponent(1);
        JLabel welcomeLabel = (JLabel) studentPanel.getClientProperty("welcomeLabel");
        JLabel[] infoLabels = (JLabel[]) studentPanel.getClientProperty("infoLabels");
        DefaultTableModel courtModel = (DefaultTableModel) studentPanel.getClientProperty("courtModel");
        DefaultTableModel reservationModel = (DefaultTableModel) studentPanel.getClientProperty("reservationModel");

        if (currentStudent != null) {
            welcomeLabel.setText("欢迎, " + currentStudent.getName() + " (" + currentStudent.getStudentId() + ")");
            infoLabels[0].setText("学号: " + currentStudent.getStudentId());
            infoLabels[1].setText("姓名: " + currentStudent.getName());
            infoLabels[2].setText("联系电话: " + currentStudent.getPhone());

            // 刷新可用场地
            courtModel.setRowCount(0);
            List<Court> courts = reservationService.getAvailableCourts();
            for (Court court : courts) {
                courtModel.addRow(new Object[]{
                        court.getCourtId(),
                        court.getType().getDescription(),
                        court.getStatus().getDescription()
                });
            }

            // 刷新我的预约
            reservationModel.setRowCount(0);
            List<Reservation> reservations = reservationService.getMyReservations(currentStudent.getStudentId());
            for (Reservation r : reservations) {
                reservationModel.addRow(new Object[]{
                        r.getReservationId(),
                        r.getCourtId(),
                        r.getStartTime().format(formatter),
                        r.getEndTime().format(formatter),
                        r.getStatus().getDescription()
                });
            }
        }
    }

    /**
     * 创建管理员面板
     */
    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel welcomeLabel = new JLabel();
        welcomeLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        topPanel.add(welcomeLabel);
        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logoutBtn.setPreferredSize(new Dimension(100, 30));
        logoutBtn.setBackground(new Color(200, 80, 80));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            currentAdmin = null;
            cardLayout.show(mainPanel, "LOGIN");
        });
        topPanel.add(logoutBtn);

        JTabbedPane tabbedPane = new JTabbedPane();

        // 场地管理标签页
        String[] courtColumns = {"场地编号", "场地类型", "状态"};
        DefaultTableModel courtModel = new DefaultTableModel(courtColumns, 0);
        JTable courtTable = new JTable(courtModel);
        JScrollPane courtScrollPane = new JScrollPane(courtTable);
        JPanel courtPanel = new JPanel(new BorderLayout());
        courtPanel.add(courtScrollPane, BorderLayout.CENTER);

        JPanel courtButtonPanel = new JPanel(new FlowLayout());
        JButton addCourtBtn = new JButton("添加场地");
        addCourtBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addCourtBtn.setPreferredSize(new Dimension(120, 35));
        addCourtBtn.setBackground(new Color(70, 130, 180));
        addCourtBtn.setForeground(Color.WHITE);
        addCourtBtn.setFocusPainted(false);
        
        JButton updateCourtStatusBtn = new JButton("修改场地状态");
        updateCourtStatusBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        updateCourtStatusBtn.setPreferredSize(new Dimension(140, 35));
        updateCourtStatusBtn.setBackground(new Color(100, 150, 100));
        updateCourtStatusBtn.setForeground(Color.WHITE);
        updateCourtStatusBtn.setFocusPainted(false);
        
        courtButtonPanel.add(addCourtBtn);
        courtButtonPanel.add(updateCourtStatusBtn);
        courtPanel.add(courtButtonPanel, BorderLayout.SOUTH);

        addCourtBtn.addActionListener(e -> showAddCourtDialog());
        updateCourtStatusBtn.addActionListener(e -> {
            int row = courtTable.getSelectedRow();
            if (row >= 0) {
                String courtId = (String) courtModel.getValueAt(row, 0);
                showUpdateCourtStatusDialog(courtId);
            } else {
                JOptionPane.showMessageDialog(this, "请先选择场地", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        tabbedPane.addTab("场地管理", courtPanel);

        // 预约管理标签页
        String[] reservationColumns = {"预约编号", "学号", "场地编号", "开始时间", "结束时间", "状态"};
        DefaultTableModel reservationModel = new DefaultTableModel(reservationColumns, 0);
        JTable reservationTable = new JTable(reservationModel);
        JScrollPane reservationScrollPane = new JScrollPane(reservationTable);
        JPanel reservationPanel = new JPanel(new BorderLayout());
        reservationPanel.add(reservationScrollPane, BorderLayout.CENTER);

        JPanel reservationButtonPanel = new JPanel(new FlowLayout());
        JButton confirmBtn = new JButton("确认完成");
        confirmBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        confirmBtn.setPreferredSize(new Dimension(120, 35));
        confirmBtn.setBackground(new Color(70, 130, 180));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setFocusPainted(false);
        reservationButtonPanel.add(confirmBtn);
        reservationPanel.add(reservationButtonPanel, BorderLayout.SOUTH);

        confirmBtn.addActionListener(e -> {
            int row = reservationTable.getSelectedRow();
            if (row >= 0) {
                String reservationId = (String) reservationModel.getValueAt(row, 0);
                try {
                    reservationService.confirmReservationCompleted(reservationId);
                    JOptionPane.showMessageDialog(this, "确认完成成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                    refreshAdminPanel();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "请先选择预约", "提示", JOptionPane.WARNING_MESSAGE);
            }
        });

        tabbedPane.addTab("预约管理", reservationPanel);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        // 保存引用以便刷新
        panel.putClientProperty("welcomeLabel", welcomeLabel);
        panel.putClientProperty("courtModel", courtModel);
        panel.putClientProperty("reservationModel", reservationModel);

        return panel;
    }

    /**
     * 刷新管理员面板
     */
    private void refreshAdminPanel() {
        JPanel adminPanel = (JPanel) mainPanel.getComponent(2);
        JLabel welcomeLabel = (JLabel) adminPanel.getClientProperty("welcomeLabel");
        DefaultTableModel courtModel = (DefaultTableModel) adminPanel.getClientProperty("courtModel");
        DefaultTableModel reservationModel = (DefaultTableModel) adminPanel.getClientProperty("reservationModel");

        if (currentAdmin != null) {
            welcomeLabel.setText("欢迎管理员, " + currentAdmin.getName() + " (" + currentAdmin.getAdminId() + ")");

            // 刷新场地列表
            courtModel.setRowCount(0);
            List<Court> courts = courtService.getAllCourts();
            for (Court court : courts) {
                courtModel.addRow(new Object[]{
                        court.getCourtId(),
                        court.getType().getDescription(),
                        court.getStatus().getDescription()
                });
            }

            // 刷新预约列表
            reservationModel.setRowCount(0);
            List<Reservation> reservations = reservationService.getAllReservations();
            for (Reservation r : reservations) {
                reservationModel.addRow(new Object[]{
                        r.getReservationId(),
                        r.getStudentId(),
                        r.getCourtId(),
                        r.getStartTime().format(formatter),
                        r.getEndTime().format(formatter),
                        r.getStatus().getDescription()
                });
            }
        }
    }

    /**
     * 显示添加场地对话框
     */
    private void showAddCourtDialog() {
        JDialog dialog = new JDialog(this, "添加场地", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("场地编号:"), gbc);
        JTextField courtIdField = new JTextField(15);
        gbc.gridx = 1;
        panel.add(courtIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("场地类型:"), gbc);
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"单打", "双打"});
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("状态:"), gbc);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"可用", "维修中"});
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        JButton addBtn = new JButton("添加");
        addBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        addBtn.setPreferredSize(new Dimension(120, 35));
        addBtn.setBackground(new Color(70, 130, 180));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        panel.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            try {
                Court.CourtType type = typeCombo.getSelectedIndex() == 0 ?
                        Court.CourtType.SINGLES : Court.CourtType.DOUBLES;
                Court.CourtStatus status = statusCombo.getSelectedIndex() == 0 ?
                        Court.CourtStatus.AVAILABLE : Court.CourtStatus.MAINTENANCE;
                courtService.addCourt(courtIdField.getText(), type, status);
                JOptionPane.showMessageDialog(dialog, "添加成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAdminPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * 显示修改场地状态对话框
     */
    private void showUpdateCourtStatusDialog(String courtId) {
        JDialog dialog = new JDialog(this, "修改场地状态", true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("场地编号: " + courtId), gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("新状态:"), gbc);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"可用", "维修中"});
        gbc.gridx = 1;
        panel.add(statusCombo, gbc);

        JButton updateBtn = new JButton("修改");
        updateBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        updateBtn.setPreferredSize(new Dimension(120, 35));
        updateBtn.setBackground(new Color(70, 130, 180));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        panel.add(updateBtn, gbc);

        updateBtn.addActionListener(e -> {
            try {
                Court.CourtStatus status = statusCombo.getSelectedIndex() == 0 ?
                        Court.CourtStatus.AVAILABLE : Court.CourtStatus.MAINTENANCE;
                courtService.updateCourtStatus(courtId, status);
                JOptionPane.showMessageDialog(dialog, "修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshAdminPanel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(panel);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new SwingUI().setVisible(true);
        });
    }
}

