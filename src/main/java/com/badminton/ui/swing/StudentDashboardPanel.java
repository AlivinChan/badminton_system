package com.badminton.ui.swing;

import com.badminton.model.*;
import com.badminton.service.BookingService;
import com.badminton.service.CourtService;
import com.badminton.service.UserService;
import com.badminton.util.BusinessException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private void initializeUI() {
        setLayout(new BorderLayout());

        // 顶部：返回按钮和用户信息
        JPanel topPanel = new JPanel(new BorderLayout());
        JButton backButton = new JButton("注销");
        backButton.addActionListener(e -> mainFrame.showLoginPanel());
        topPanel.add(backButton, BorderLayout.WEST);
        
        userLabel = new JLabel("", JLabel.CENTER);
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        topPanel.add(userLabel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 使用选项卡
        JTabbedPane tabbedPane = new JTabbedPane();

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

        // 顶部提示和刷新按钮
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("所有场地信息："), BorderLayout.WEST);
        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> showAllCourts());
        topPanel.add(refreshButton, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // 可用场地表格
        String[] columns = {"场地编号", "类型", "状态"};
        availableCourtsModel = new DefaultTableModel(columns, 0);
        availableCourtsTable = new JTable(availableCourtsModel);
        JScrollPane scrollPane = new JScrollPane(availableCourtsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 预约面板
        JPanel bookingPanel = new JPanel(new BorderLayout());
        JPanel bookingInputPanel = new JPanel(new FlowLayout());
        bookingInputPanel.add(new JLabel("预约场地："));
        bookingInputPanel.add(new JLabel("场地编号："));
        courtIdField = new JTextField(10);
        bookingInputPanel.add(courtIdField);
        bookingInputPanel.add(new JLabel("日期（yyyy-MM-dd）："));
        dateField = new JTextField(12);
        bookingInputPanel.add(dateField);
        bookingInputPanel.add(new JLabel("开始时间（HH:mm）："));
        startTimeField = new JTextField(8);
        bookingInputPanel.add(startTimeField);
        bookingInputPanel.add(new JLabel("结束时间（HH:mm）："));
        endTimeField = new JTextField(8);
        bookingInputPanel.add(endTimeField);
        JButton bookButton = new JButton("预约");
        bookButton.addActionListener(e -> createBooking());
        bookingInputPanel.add(bookButton);
        bookingPanel.add(bookingInputPanel, BorderLayout.CENTER);
        panel.add(bookingPanel, BorderLayout.SOUTH);

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
        JScrollPane scrollPane = new JScrollPane(myBookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 操作按钮
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton cancelButton = new JButton("取消预约");
        cancelButton.addActionListener(e -> cancelBooking());
        buttonPanel.add(cancelButton);

        JButton rateButton = new JButton("评分");
        rateButton.addActionListener(e -> rateBooking());
        buttonPanel.add(rateButton);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshMyBookings());
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * 显示所有场地信息
     */
    private void showAllCourts() {
        availableCourtsModel.setRowCount(0);
        Court[] courts = courtService.getAllCourts();
        for (Court court : courts) {
            availableCourtsModel.addRow(new Object[]{
                court.getCourtId(),
                court.getType(),
                court.getStatus()
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
            LocalTime start = LocalTime.parse(startTimeField.getText(), timeFormatter);
            LocalTime end = LocalTime.parse(endTimeField.getText(), timeFormatter);
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

