package com.badminton.ui.swing;

import com.badminton.model.*;
import com.badminton.service.*;
import com.badminton.util.BusinessException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 管理员功能面板
 */
public class AdminDashboardPanel extends JPanel {
    private MainFrame mainFrame;
    private AdminService adminService;
    private BookingService bookingService;
    private CourtService courtService;
    private StatisticsService statisticsService;
    private Admin currentAdmin;

    private JTable bookingsTable;
    private JTable courtsTable;
    private JTable ratingsTable;
    private DefaultTableModel bookingsModel;
    private DefaultTableModel courtsModel;
    private DefaultTableModel ratingsModel;

    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AdminDashboardPanel(MainFrame mainFrame, AdminService adminService, 
                              BookingService bookingService, CourtService courtService,
                              StatisticsService statisticsService) {
        this.mainFrame = mainFrame;
        this.adminService = adminService;
        this.bookingService = bookingService;
        this.courtService = courtService;
        this.statisticsService = statisticsService;
        initializeUI();
    }

    public void setCurrentAdmin(Admin admin) {
        this.currentAdmin = admin;
        refreshAll();
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

        // 顶部：返回按钮
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
        add(topPanel, BorderLayout.NORTH);

        // 使用选项卡
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        tabbedPane.setBackground(new Color(248, 249, 252));

        // 预约管理面板
        JPanel bookingsPanel = createBookingsPanel();
        tabbedPane.addTab("预约管理", bookingsPanel);

        // 场地管理面板
        JPanel courtsPanel = createCourtsPanel();
        tabbedPane.addTab("场地管理", courtsPanel);

        // 统计面板
        JPanel statisticsPanel = createStatisticsPanel();
        tabbedPane.addTab("统计信息", statisticsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"预约编号", "学号", "场地", "日期", "时段", "状态", "费用"};
        bookingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(bookingsModel);
        bookingsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        bookingsTable.setRowHeight(32);
        bookingsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        bookingsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        bookingsTable.getTableHeader().setForeground(Color.WHITE);
        bookingsTable.setGridColor(new Color(200, 220, 240));
        bookingsTable.setShowGrid(true);
        bookingsTable.setOpaque(true);
        setupTableStyle(bookingsTable);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(248, 249, 252));
        JButton confirmButton = createStyledButton("确认完成", 150, 40, new Color(70, 130, 180));
        confirmButton.addActionListener(e -> confirmBooking());
        buttonPanel.add(confirmButton);

        JButton refreshButton = createStyledButton("刷新", 110, 40, new Color(100, 150, 100));
        refreshButton.addActionListener(e -> refreshBookings());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCourtsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"场地编号", "类型", "状态"};
        courtsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        courtsTable = new JTable(courtsModel);
        courtsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        courtsTable.setRowHeight(32);
        courtsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        courtsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        courtsTable.getTableHeader().setForeground(Color.WHITE);
        courtsTable.setGridColor(new Color(200, 220, 240));
        courtsTable.setShowGrid(true);
        courtsTable.setOpaque(true);
        setupTableStyle(courtsTable);
        JScrollPane scrollPane = new JScrollPane(courtsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(248, 249, 252));
        JButton changeStatusButton = createStyledButton("更改状态", 150, 40, new Color(70, 130, 180));
        changeStatusButton.addActionListener(e -> changeCourtStatus());
        buttonPanel.add(changeStatusButton);

        JButton refreshButton = createStyledButton("刷新", 110, 40, new Color(100, 150, 100));
        refreshButton.addActionListener(e -> refreshCourts());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // 评分统计表格
        String[] columns = {"场地编号", "类型", "平均评分", "评分次数"};
        ratingsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ratingsTable = new JTable(ratingsModel);
        ratingsTable.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        ratingsTable.setRowHeight(32);
        ratingsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 14));
        ratingsTable.getTableHeader().setBackground(new Color(70, 130, 180));
        ratingsTable.getTableHeader().setForeground(Color.WHITE);
        ratingsTable.setGridColor(new Color(200, 220, 240));
        ratingsTable.setShowGrid(true);
        ratingsTable.setOpaque(true);
        setupTableStyle(ratingsTable);
        JScrollPane scrollPane = new JScrollPane(ratingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(248, 249, 252));
        JButton refreshRatingsButton = createStyledButton("刷新评分统计", 170, 40, new Color(100, 150, 100));
        refreshRatingsButton.addActionListener(e -> refreshRatings());
        buttonPanel.add(refreshRatingsButton);

        JButton earningsButton = createStyledButton("收入统计", 150, 40, new Color(70, 130, 180));
        earningsButton.addActionListener(e -> showEarningsReport());
        buttonPanel.add(earningsButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void confirmBooking() {
        int row = bookingsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要确认的预约", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookingId = (String) bookingsModel.getValueAt(row, 0);
        try {
            adminService.confirmBookingCompleted(bookingId);
            JOptionPane.showMessageDialog(this, "确认成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshBookings();
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "确认失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeCourtStatus() {
        int row = courtsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请选择要更改的场地", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courtId = (String) courtsModel.getValueAt(row, 0);
        String[] options = {"可用", "维护中"};
        int choice = JOptionPane.showOptionDialog(this, 
            "请选择新状态", "更改场地状态", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.QUESTION_MESSAGE, 
            null, options, options[0]);

        if (choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        try {
            CourtStatus status = (choice == 0) ? CourtStatus.AVAILABLE : CourtStatus.MAINTENANCE;
            courtService.changeCourtStatus(courtId, status);
            JOptionPane.showMessageDialog(this, "更改成功", "成功", JOptionPane.INFORMATION_MESSAGE);
            refreshCourts();
        } catch (BusinessException e) {
            JOptionPane.showMessageDialog(this, "更改失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showEarningsReport() {
        String startStr = JOptionPane.showInputDialog(this, 
            "请输入开始日期（yyyy-MM-dd，留空表示全部）：", "收入统计", JOptionPane.QUESTION_MESSAGE);
        String endStr = JOptionPane.showInputDialog(this, 
            "请输入结束日期（yyyy-MM-dd，留空表示全部）：", "收入统计", JOptionPane.QUESTION_MESSAGE);

        try {
            LocalDate startDate = startStr == null || startStr.trim().isEmpty() ? 
                LocalDate.of(2000, 1, 1) : LocalDate.parse(startStr, dateFormatter);
            LocalDate endDate = endStr == null || endStr.trim().isEmpty() ? 
                LocalDate.of(2100, 12, 31) : LocalDate.parse(endStr, dateFormatter);

            double earnings = statisticsService.earningsReport(startDate, endDate);
            JOptionPane.showMessageDialog(this, 
                "总收入：" + String.format("%.2f", earnings) + "元", 
                "收入统计", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "统计失败：" + e.getMessage(), 
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshBookings() {
        bookingsModel.setRowCount(0);
        Booking[] bookings = bookingService.getAllBookings();
        for (Booking booking : bookings) {
            // 过滤掉已取消的预约
            if (booking.getState() == BookingState.CANCELLED) {
                continue;
            }
            bookingsModel.addRow(new Object[]{
                booking.getBookingId(),
                booking.getStudent().getStudentId(),
                booking.getCourtId(),
                booking.getSlot().getDate(),
                booking.getSlot().getStart() + "-" + booking.getSlot().getEnd(),
                booking.getState(),
                String.format("%.2f", booking.getFee())
            });
        }
    }

    private void refreshCourts() {
        courtsModel.setRowCount(0);
        Court[] courts = courtService.getAllCourts();
        for (Court court : courts) {
            courtsModel.addRow(new Object[]{
                court.getCourtId(),
                court.getType(),
                court.getStatus()
            });
        }
    }

    private void refreshRatings() {
        ratingsModel.setRowCount(0);
        Map<String, Map<String, Object>> stats = statisticsService.getCourtRatingStatistics();
        for (Map<String, Object> stat : stats.values()) {
            ratingsModel.addRow(new Object[]{
                stat.get("courtId"),
                stat.get("type"),
                String.format("%.2f", stat.get("averageRating")),
                stat.get("ratingCount")
            });
        }
    }

    private void refreshAll() {
        refreshBookings();
        refreshCourts();
        refreshRatings();
    }
}

