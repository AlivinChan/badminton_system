package com.badminton.ui.swing;

import com.badminton.persistence.InMemoryDB;
import com.badminton.service.*;
import com.badminton.util.DefaultFeePolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 主窗口
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private InMemoryDB db;
    private UserService userService;
    private AdminService adminService;
    private BookingService bookingService;
    private CourtService courtService;
    private StatisticsService statisticsService;

    // 面板
    private LoginPanel loginPanel;
    private StudentDashboardPanel studentDashboardPanel;
    private AdminDashboardPanel adminDashboardPanel;

    public MainFrame() {
        initializeServices();
        initializeUI();
    }

    private void initializeServices() {
        db = InMemoryDB.loadFromFile();
        DefaultFeePolicy feePolicy = new DefaultFeePolicy();
        userService = new UserService(db);
        bookingService = new BookingService(db, feePolicy);
        courtService = new CourtService(db, bookingService);
        adminService = new AdminService(db);
        statisticsService = new StatisticsService(db);

        // 初始化默认数据
        initializeDefaultData();
    }

    private void initializeDefaultData() {
        if (db.getCourtCount() == 0) {
            db.addCourt(new com.badminton.model.Court("C001", 
                com.badminton.model.CourtType.SINGLES, 
                com.badminton.model.CourtStatus.AVAILABLE, 0));
            db.addCourt(new com.badminton.model.Court("C002", 
                com.badminton.model.CourtType.SINGLES, 
                com.badminton.model.CourtStatus.AVAILABLE, 0));
            db.addCourt(new com.badminton.model.Court("C003", 
                com.badminton.model.CourtType.DOUBLES, 
                com.badminton.model.CourtStatus.AVAILABLE, 0));
            db.addCourt(new com.badminton.model.Court("C004", 
                com.badminton.model.CourtType.DOUBLES, 
                com.badminton.model.CourtStatus.AVAILABLE, 0));
            db.saveToFile();
        }

        if (db.getAdminCount() == 0) {
            db.addAdmin(new com.badminton.model.Admin("admin", "管理员", "13800000000", "admin123"));
            db.saveToFile();
        }
    }

    private void initializeUI() {
        setTitle("校园羽毛球馆场地预约管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));

        // 创建菜单栏
        createMenuBar();

        // 主面板使用CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(new Color(245, 245, 250));

        // 创建各个面板
        loginPanel = new LoginPanel(this, userService, adminService);
        studentDashboardPanel = new StudentDashboardPanel(this, userService, bookingService, courtService);
        adminDashboardPanel = new AdminDashboardPanel(this, adminService, bookingService, 
                courtService, statisticsService);

        mainPanel.add(loginPanel, "LOGIN");
        mainPanel.add(studentDashboardPanel, "STUDENT");
        mainPanel.add(adminDashboardPanel, "ADMIN");

        add(mainPanel);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("文件");
        JMenuItem exitItem = new JMenuItem("退出");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("帮助");
        JMenuItem aboutItem = new JMenuItem("关于");
        aboutItem.addActionListener(e -> 
            JOptionPane.showMessageDialog(this, 
                "校园羽毛球馆场地预约管理系统\n版本 1.0", 
                "关于", 
                JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);
    }

    public void showLoginPanel() {
        cardLayout.show(mainPanel, "LOGIN");
    }

    public void showStudentDashboard(com.badminton.model.Student student) {
        studentDashboardPanel.setCurrentStudent(student);
        cardLayout.show(mainPanel, "STUDENT");
    }

    public void showAdminDashboard(com.badminton.model.Admin admin) {
        adminDashboardPanel.setCurrentAdmin(admin);
        cardLayout.show(mainPanel, "ADMIN");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new MainFrame().setVisible(true);
        });
    }
}

