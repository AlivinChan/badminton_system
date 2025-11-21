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

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 250));

        // 顶部：返回按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 180));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JButton backButton = new JButton("注销");
        backButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(80, 30));
        backButton.setBackground(new Color(220, 220, 220));
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> mainFrame.showLoginPanel());
        topPanel.add(backButton, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);

        // 使用选项卡
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        tabbedPane.setBackground(new Color(245, 245, 250));

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
        bookingsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        bookingsTable.setRowHeight(25);
        bookingsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        bookingsTable.setSelectionBackground(new Color(230, 240, 255));
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 250));
        JButton confirmButton = new JButton("确认完成");
        confirmButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        confirmButton.setPreferredSize(new Dimension(120, 32));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.addActionListener(e -> confirmBooking());
        buttonPanel.add(confirmButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        refreshButton.setPreferredSize(new Dimension(100, 32));
        refreshButton.setBackground(new Color(100, 150, 100));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
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
        courtsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        courtsTable.setRowHeight(25);
        courtsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        courtsTable.setSelectionBackground(new Color(230, 240, 255));
        JScrollPane scrollPane = new JScrollPane(courtsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 250));
        JButton changeStatusButton = new JButton("更改状态");
        changeStatusButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        changeStatusButton.setPreferredSize(new Dimension(120, 32));
        changeStatusButton.setBackground(new Color(70, 130, 180));
        changeStatusButton.setForeground(Color.WHITE);
        changeStatusButton.setFocusPainted(false);
        changeStatusButton.addActionListener(e -> changeCourtStatus());
        buttonPanel.add(changeStatusButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        refreshButton.setPreferredSize(new Dimension(100, 32));
        refreshButton.setBackground(new Color(100, 150, 100));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
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
        ratingsTable.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        ratingsTable.setRowHeight(25);
        ratingsTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 13));
        ratingsTable.setSelectionBackground(new Color(230, 240, 255));
        JScrollPane scrollPane = new JScrollPane(ratingsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(new Color(245, 245, 250));
        JButton refreshRatingsButton = new JButton("刷新评分统计");
        refreshRatingsButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        refreshRatingsButton.setPreferredSize(new Dimension(140, 32));
        refreshRatingsButton.setBackground(new Color(100, 150, 100));
        refreshRatingsButton.setForeground(Color.WHITE);
        refreshRatingsButton.setFocusPainted(false);
        refreshRatingsButton.addActionListener(e -> refreshRatings());
        buttonPanel.add(refreshRatingsButton);

        JButton earningsButton = new JButton("收入统计");
        earningsButton.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        earningsButton.setPreferredSize(new Dimension(120, 32));
        earningsButton.setBackground(new Color(70, 130, 180));
        earningsButton.setForeground(Color.WHITE);
        earningsButton.setFocusPainted(false);
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

