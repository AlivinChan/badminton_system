package com.badminton.ui.swing;

import com.badminton.model.*;
import com.badminton.service.BookingService;
import com.badminton.service.CourtService;
import com.badminton.service.UserService;
import com.badminton.util.BusinessException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * 学生功能面板
 */
public class StudentDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    private BookingService bookingService;
    private CourtService courtService;
    private Student currentStudent;

    private JTable availableCourtsTable;
    private JTable myBookingsTable;
    private DefaultTableModel availableCourtsModel;
    private DefaultTableModel myBookingsModel;

    private JTextField dateField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField courtIdField;
    private JLabel userLabel;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    /**
     * 规范化时间字符串，将中文冒号转换为英文冒号
     */
    private String normalizeTimeString(String timeStr) {
        if (timeStr == null) return null;
        return timeStr.replace("：", ":"); // 将中文冒号（：）替换为英文冒号（:）
    }

    public StudentDashboardPanel(MainFrame mainFrame, UserService userService, 
                                 BookingService bookingService, CourtService courtService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        this.bookingService = bookingService;
        this.courtService = courtService;
        initializeUI();
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        if (userLabel != null && student != null) {
            userLabel.setText("欢迎，" + student.getName() + "（学号：" + student.getStudentId() + "）");
        }
        refreshMyBookings();
    }
    
    /**
     * 设置表格样式：淡蓝色背景，白色文字，无悬停效果
     */
    private void setupTableStyle(JTable table) {
        // 设置默认单元格渲染器
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // 始终使用淡蓝色背景和白色文字
                setBackground(new Color(173, 216, 230)); // 淡蓝色
                setForeground(Color.WHITE);
                setOpaque(true);
                return this;
            }
        });
        
        // 设置表头渲染器，确保始终显示蓝色背景和白色文字
        table.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                // 始终使用蓝色背景和白色文字，不受悬停影响
                setBackground(new Color(70, 130, 180));
                setForeground(Color.WHITE);
                setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return this;
            }
        });
        
        // 禁用选择效果
        table.setSelectionBackground(new Color(173, 216, 230));
        table.setSelectionForeground(Color.WHITE);
        table.setRowSelectionAllowed(true);
    }
    
    /**
     * 创建样式化的按钮
     */
    private JButton createStyledButton(String text, int width, int height, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getWidth() <= 0 || getHeight() <= 0) return;
                
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    Color color = isEnabled() ? 
                        (getModel().isRollover() ? bgColor.brighter() : bgColor) :
                        new Color(180, 180, 180);
                    g2.setColor(color);
                    int arc = 6;
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                    
                    if (getText() != null && !getText().isEmpty()) {
                        g2.setColor(Color.WHITE);
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
        button.setFont(new Font("微软雅黑", Font.BOLD, 13));
        button.setPreferredSize(new Dimension(width, height));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setRolloverEnabled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 252));

        // 顶部：返回按钮和用户信息
        JPanel topPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), 
                    0, getHeight(), new Color(60, 120, 170));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        
        JButton backButton = createStyledButton("注销", 90, 36, new Color(220, 100, 100));
        backButton.addActionListener(e -> mainFrame.showLoginPanel());
        topPanel.add(backButton, BorderLayout.WEST);
        
        userLabel = new JLabel("", JLabel.CENTER);
        userLabel.setFont(new Font("微软雅黑", Font.BOLD, 17));
        userLabel.setForeground(Color.WHITE);
        topPanel.add(userLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 使用选项卡
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        tabbedPane.setBackground(new Color(248, 249, 252));

        // 查询和预约面板
        JPanel queryPanel = createQueryPanel();
        tabbedPane.addTab("查询与预约", queryPanel);

        // 我的预约面板
        JPanel bookingsPanel = createMyBookingsPanel();
        tabbedPane.addTab("我的预约", bookingsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createQueryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(248, 249, 252));

        // 顶部提示和刷新按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(255, 255, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel infoLabel = new JLabel("所有场地信息：");
        infoLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
        infoLabel.setForeground(new Color(60, 60, 60));
        topPanel.add(infoLabel, BorderLayout.WEST);
        JButton refreshButton = createStyledButton("刷新", 100, 35, new Color(100, 150, 100));
        refreshButton.addActionListener(e -> showAllCourts());
        topPanel.add(refreshButton, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // 中间区域：使用JSplitPane分割表格和预约面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        splitPane.setBackground(new Color(248, 249, 252));
        splitPane.setResizeWeight(0.6); // 表格占60%，预约面板占40%
        
        // 可用场地表格
        String[] columns = {"场地编号", "类型", "状态"};
        availableCourtsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        availableCourtsTable = new JTable(availableCourtsModel);
        availableCourtsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        availableCourtsTable.setRowHeight(32);
        availableCourtsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        availableCourtsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        availableCourtsTable.getTableHeader().setForeground(Color.WHITE);
        availableCourtsTable.setGridColor(new Color(200, 220, 240));
        availableCourtsTable.setShowGrid(true);
        availableCourtsTable.setOpaque(true);
        setupTableStyle(availableCourtsTable);
        JScrollPane scrollPane = new JScrollPane(availableCourtsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        splitPane.setTopComponent(scrollPane);

        // 预约面板 - 使用更明显的样式
        JPanel bookingPanel = new JPanel(new BorderLayout());
        bookingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
                "预约场地", 0, 0, new Font("微软雅黑", Font.BOLD, 15)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        bookingPanel.setBackground(new Color(255, 255, 255));
        bookingPanel.setPreferredSize(new Dimension(0, 200)); // 确保预约面板有足够高度
        
        // 使用GridBagLayout来更好地控制布局
        JPanel bookingInputPanel = new JPanel(new GridBagLayout());
        bookingInputPanel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // 第一行
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel courtLabel = new JLabel("场地编号：");
        courtLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        courtLabel.setForeground(new Color(60, 60, 60));
        bookingInputPanel.add(courtLabel, gbc);
        gbc.gridx = 1;
        courtIdField = new JTextField(12);
        courtIdField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        courtIdField.setPreferredSize(new Dimension(120, 32));
        courtIdField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        bookingInputPanel.add(courtIdField, gbc);
        
        gbc.gridx = 2;
        gbc.insets = new Insets(8, 20, 8, 10);
        JLabel dateLabel = new JLabel("日期（yyyy-MM-dd）：");
        dateLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(60, 60, 60));
        bookingInputPanel.add(dateLabel, gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(8, 10, 8, 10);
        dateField = new JTextField(15);
        dateField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        dateField.setPreferredSize(new Dimension(140, 32));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // 添加监听器，实时更新场地表格
        dateField.addActionListener(e -> showAllCourts());
        dateField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
        });
        bookingInputPanel.add(dateField, gbc);
        
        // 第二行
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.insets = new Insets(8, 10, 8, 10);
        JLabel startLabel = new JLabel("开始时间（HH:00）：");
        startLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        startLabel.setForeground(new Color(60, 60, 60));
        bookingInputPanel.add(startLabel, gbc);
        gbc.gridx = 1;
        startTimeField = new JTextField(10);
        startTimeField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        startTimeField.setPreferredSize(new Dimension(100, 32));
        startTimeField.setToolTipText("请输入整点时间，格式：HH:00，例如：09:00, 10:00, 15:00");
        startTimeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // 添加监听器，实时更新场地表格
        startTimeField.addActionListener(e -> showAllCourts());
        startTimeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
        });
        bookingInputPanel.add(startTimeField, gbc);
        
        gbc.gridx = 2;
        gbc.insets = new Insets(8, 20, 8, 10);
        JLabel endLabel = new JLabel("结束时间（HH:00）：");
        endLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        endLabel.setForeground(new Color(60, 60, 60));
        bookingInputPanel.add(endLabel, gbc);
        gbc.gridx = 3;
        gbc.insets = new Insets(8, 10, 8, 10);
        endTimeField = new JTextField(10);
        endTimeField.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        endTimeField.setPreferredSize(new Dimension(100, 32));
        endTimeField.setToolTipText("请输入整点时间，格式：HH:00，例如：09:00, 10:00, 15:00");
        endTimeField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        // 添加监听器，实时更新场地表格
        endTimeField.addActionListener(e -> showAllCourts());
        endTimeField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { showAllCourts(); }
        });
        bookingInputPanel.add(endTimeField, gbc);
        
        // 添加提示标签
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 5, 10);
        JLabel timeHintLabel = new JLabel("提示：时间只能选择整点（格式：HH:00，例如：09:00, 10:00, 15:00）");
        timeHintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        timeHintLabel.setForeground(new Color(100, 100, 100));
        bookingInputPanel.add(timeHintLabel, gbc);
        
        // 第四行：预约按钮居中
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 10, 10, 10);
        JButton bookButton = createStyledButton("预约", 150, 45, new Color(70, 130, 180));
        bookButton.setFont(new Font("微软雅黑", Font.BOLD, 16));
        bookButton.addActionListener(e -> createBooking());
        bookingInputPanel.add(bookButton, gbc);
        
        bookingPanel.add(bookingInputPanel, BorderLayout.CENTER);
        splitPane.setBottomComponent(bookingPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);

        // 初始化时显示所有场地
        showAllCourts();

        return panel;
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"预约编号", "场地", "日期", "时段", "状态", "费用", "评分"};
        myBookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        myBookingsTable = new JTable(myBookingsModel);
        myBookingsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        myBookingsTable.setRowHeight(32);
        myBookingsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        myBookingsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        myBookingsTable.getTableHeader().setForeground(Color.WHITE);
        myBookingsTable.setGridColor(new Color(200, 220, 240));
        myBookingsTable.setShowGrid(true);
        myBookingsTable.setOpaque(true);
        setupTableStyle(myBookingsTable);
        JScrollPane scrollPane = new JScrollPane(myBookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(248, 249, 252));
        
        JButton cancelButton = createStyledButton("取消预约", 130, 40, new Color(220, 100, 100));
        cancelButton.addActionListener(e -> cancelBooking());
        buttonPanel.add(cancelButton);

        JButton rateButton = createStyledButton("评分", 110, 40, new Color(70, 130, 180));
        rateButton.addActionListener(e -> rateBooking());
        buttonPanel.add(rateButton);

        JButton refreshButton = createStyledButton("刷新", 110, 40, new Color(100, 150, 100));
        refreshButton.addActionListener(e -> refreshMyBookings());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 显示所有场地信息，检查时间段冲突并标红
     */
    private void showAllCourts() {
        availableCourtsModel.setRowCount(0);
        Court[] courts = courtService.getAllCourts();
        
        // 尝试获取用户输入的时间段
        TimeSlot tempTimeSlot = null;
        try {
            String dateStr = dateField.getText().trim();
            String startTimeStr = normalizeTimeString(startTimeField.getText().trim());
            String endTimeStr = normalizeTimeString(endTimeField.getText().trim());
            
            if (!dateStr.isEmpty() && !startTimeStr.isEmpty() && !endTimeStr.isEmpty()) {
                LocalDate date = LocalDate.parse(dateStr, dateFormatter);
                LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
                tempTimeSlot = new TimeSlot(date, startTime, endTime);
            }
        } catch (Exception e) {
            // 如果时间格式不正确，忽略错误，不进行冲突检查
            tempTimeSlot = null;
        }
        final TimeSlot userTimeSlot = tempTimeSlot;
        
        // 设置自定义渲染器，用于标红冲突的场地和维护中的场地
        availableCourtsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // 获取该行的场地编号和状态
                String courtId = (String) table.getValueAt(row, 0);
                Object statusObj = table.getValueAt(row, 2);
                
                // 检查场地是否维护中
                boolean isMaintenance = statusObj != null && 
                    (statusObj.toString().equals("MAINTENANCE") || statusObj.toString().equals("UNAVAILABLE"));
                
                // 检查是否有时间段冲突
                boolean hasConflict = false;
                if (userTimeSlot != null && courtId != null && !isMaintenance) {
                    hasConflict = bookingService.isConflict(courtId, userTimeSlot);
                }
                
                // 如果维护中或有冲突，标红显示
                if (isMaintenance || hasConflict) {
                    setBackground(new Color(255, 200, 200)); // 淡红色背景
                    setForeground(new Color(180, 0, 0)); // 深红色文字
                } else {
                    setBackground(new Color(173, 216, 230)); // 淡蓝色背景
                    setForeground(Color.WHITE); // 白色文字
                }
                setOpaque(true);
                return this;
            }
        });
        
        for (Court court : courts) {
            // 检查场地是否维护中
            boolean isMaintenance = court.getStatus() == CourtStatus.MAINTENANCE;
            
            // 检查是否有时间段冲突（仅在场地可用时检查）
            boolean hasConflict = false;
            if (!isMaintenance && userTimeSlot != null) {
                hasConflict = bookingService.isConflict(court.getCourtId(), userTimeSlot);
            }
            
            // 如果维护中或有冲突，状态显示为 UNAVAILABLE；否则显示实际状态
            Object statusDisplay;
            if (isMaintenance || hasConflict) {
                statusDisplay = "UNAVAILABLE";
            } else {
                statusDisplay = court.getStatus();
            }
            
            availableCourtsModel.addRow(new Object[]{
                court.getCourtId(),
                court.getType(),
                statusDisplay
            });
        }
    }

    private void createBooking() {
        if (currentStudent == null) {
            JOptionPane.showMessageDialog(this, "请先登录", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String courtId = courtIdField.getText().trim();
            if (courtId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入场地编号", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate date = LocalDate.parse(dateField.getText(), dateFormatter);
            
            // 验证时间格式和整点要求
            String startTimeStr = normalizeTimeString(startTimeField.getText().trim());
            String endTimeStr = normalizeTimeString(endTimeField.getText().trim());
            
            // 检查时间格式是否为 HH:00 或 HH：00（支持中英文冒号）
            if (!startTimeStr.matches("\\d{1,2}:00")) {
                JOptionPane.showMessageDialog(this, 
                    "开始时间必须为整点格式（HH:00 或 HH：00），例如：09:00, 10：00, 15:00", 
                    "格式错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (!endTimeStr.matches("\\d{1,2}:00")) {
                JOptionPane.showMessageDialog(this, 
                    "结束时间必须为整点格式（HH:00 或 HH：00），例如：09:00, 10：00, 15:00", 
                    "格式错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            LocalTime start = LocalTime.parse(startTimeStr, timeFormatter);
            LocalTime end = LocalTime.parse(endTimeStr, timeFormatter);
            
            // 验证分钟是否为00
            if (start.getMinute() != 0) {
                JOptionPane.showMessageDialog(this, 
                    "开始时间必须为整点（分钟必须为00），例如：09:00, 10:00, 15:00", 
                    "格式错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (end.getMinute() != 0) {
                JOptionPane.showMessageDialog(this, 
                    "结束时间必须为整点（分钟必须为00），例如：09:00, 10:00, 15:00", 
                    "格式错误", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            TimeSlot slot = new TimeSlot(date, start, end);

            Booking booking = bookingService.createBooking(currentStudent.getStudentId(), courtId, slot);
            JOptionPane.showMessageDialog(this, 
                "预约成功！\n预约编号：" + booking.getBookingId() + 
                "\n费用：" + String.format("%.2f", booking.getFee()) + "元", 
                "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshMyBookings();
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "预约失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "预约失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        int row = myBookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要取消的预约", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) myBookingsModel.getValueAt(row, 0);
        try {
            bookingService.cancelBooking(currentStudent.getStudentId(), bookingId);
            JOptionPane.showMessageDialog(this, "取消成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshMyBookings();
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "取消失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rateBooking() {
        int row = myBookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要评分的预约", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) myBookingsModel.getValueAt(row, 0);
        BookingState state = (BookingState) myBookingsModel.getValueAt(row, 4);
        if (state != BookingState.COMPLETED) {
            JOptionPane.showMessageDialog(this, "只能对已完成的预约进行评分", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ratingStr = JOptionPane.showInputDialog(this, "请输入评分（1-5）：", "评分", JOptionPane.QUESTION_MESSAGE);
        if (ratingStr == null || ratingStr.trim().isEmpty()) {
            return;
        }

        try {
            int rating = Integer.parseInt(ratingStr.trim());
            bookingService.rateBooking(currentStudent.getStudentId(), bookingId, rating);
            JOptionPane.showMessageDialog(this, "评分成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshMyBookings();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "评分格式错误，请输入1-5之间的数字", 
                "错误", JOptionPane.ERROR_MESSAGE);
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "评分失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshMyBookings() {
        if (currentStudent == null) return;

        myBookingsModel.setRowCount(0);
        Booking[] bookings = bookingService.getBookingsByStudent(currentStudent.getStudentId());
        for (Booking booking : bookings) {
            // 过滤掉已取消的预约
            if (booking.getState() == BookingState.CANCELLED) {
                continue;
            }
            myBookingsModel.addRow(new Object[]{
                booking.getBookingId(),
                booking.getCourtId(),
                booking.getSlot().getDate(),
                booking.getSlot().getStart() + "-" + booking.getSlot().getEnd(),
                booking.getState(),
                String.format("%.2f", booking.getFee()),
                booking.getRating() > 0 ? booking.getRating() : "未评分"
            });
        }
    }
}

