package com.badminton.ui.swing;

import com.badminton.model.Admin;
import com.badminton.model.Student;
import com.badminton.service.AdminService;
import com.badminton.service.UserService;
import com.badminton.util.BusinessException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录面板
 */
public class LoginPanel extends JPanel {
    private MainFrame mainFrame;
    private UserService userService;
    private AdminService adminService;
    
    private JTabbedPane tabbedPane;
    private JPanel studentLoginPanel;
    private JPanel studentRegisterPanel;
    private JPanel adminLoginPanel;

    public LoginPanel(MainFrame mainFrame, UserService userService, AdminService adminService) {
        this.mainFrame = mainFrame;
        this.userService = userService;
        this.adminService = adminService;
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // 标题
        JLabel titleLabel = new JLabel("校园羽毛球馆场地预约管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 选项卡
        tabbedPane = new JTabbedPane();
        
        // 学生登录面板
        studentLoginPanel = createStudentLoginPanel();
        tabbedPane.addTab("学生登录", studentLoginPanel);
        
        // 学生注册面板
        studentRegisterPanel = createStudentRegisterPanel();
        tabbedPane.addTab("学生注册", studentRegisterPanel);
        
        // 管理员登录面板
        adminLoginPanel = createAdminLoginPanel();
        tabbedPane.addTab("管理员登录", adminLoginPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStudentLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel studentIdLabel = new JLabel("学号：");
        JTextField studentIdField = new JTextField(20);
        JButton loginButton = new JButton("登录");

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(studentIdLabel, gbc);
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "请输入学号", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Student student = userService.loginStudent(studentId);
                JOptionPane.showMessageDialog(this, "登录成功！欢迎，" + student.getName(), 
                    "成功", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showStudentDashboard(student);
            } catch (BusinessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginButton, gbc);

        return panel;
    }

    private JPanel createStudentRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel studentIdLabel = new JLabel("学号：");
        JTextField studentIdField = new JTextField(20);
        JLabel nameLabel = new JLabel("姓名：");
        JTextField nameField = new JTextField(20);
        JLabel phoneLabel = new JLabel("手机号：");
        JTextField phoneField = new JTextField(20);
        JButton registerButton = new JButton("注册");

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(studentIdLabel, gbc);
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(phoneLabel, gbc);
        gbc.gridx = 1;
        panel.add(phoneField, gbc);

        gbc.gridx = 1; gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        registerButton.addActionListener(e -> {
            String studentId = studentIdField.getText().trim();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();

            if (studentId.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "学号和姓名不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Student student = new Student(studentId, name, phone);
                userService.registerStudent(student);
                JOptionPane.showMessageDialog(this, "注册成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
                studentIdField.setText("");
                nameField.setText("");
                phoneField.setText("");
                tabbedPane.setSelectedIndex(0); // 切换到登录面板
            } catch (BusinessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "注册失败", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(registerButton, gbc);

        return panel;
    }

    private JPanel createAdminLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel adminIdLabel = new JLabel("工号：");
        JTextField adminIdField = new JTextField(20);
        JLabel passwordLabel = new JLabel("密码：");
        JPasswordField passwordField = new JPasswordField(20);
        JButton loginButton = new JButton("登录");

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(adminIdLabel, gbc);
        gbc.gridx = 1;
        panel.add(adminIdField, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy = row++;
        gbc.anchor = GridBagConstraints.CENTER;
        loginButton.addActionListener(e -> {
            String adminId = adminIdField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (adminId.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "工号和密码不能为空", "提示", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Admin admin = adminService.loginAdmin(adminId, password);
                JOptionPane.showMessageDialog(this, "登录成功！欢迎，" + admin.getName(), 
                    "成功", JOptionPane.INFORMATION_MESSAGE);
                mainFrame.showAdminDashboard(admin);
            } catch (BusinessException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginButton, gbc);

        return panel;
    }
}

