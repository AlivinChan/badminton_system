package com.badminton.ui.swing;

import com.badminton.model.Admin;
import com.badminton.model.Student;
import com.badminton.service.AdminService;
import com.badminton.service.UserService;
import com.badminton.util.BusinessException;

import javax.swing.*;
import java.awt.*;

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

    /**
     * 创建样式化的文本输入框
     */
    private JTextField createStyledTextField() {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制白色背景
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                // 绘制边框
                if (hasFocus()) {
                    g2.setColor(new Color(70, 130, 180));
                    g2.setStroke(new BasicStroke(2.0f));
                } else {
                    g2.setColor(new Color(220, 225, 230));
                    g2.setStroke(new BasicStroke(1.0f));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        field.setOpaque(false);
        return field;
    }
    
    /**
     * 创建样式化的按钮
     */
    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (getWidth() <= 0 || getHeight() <= 0) return;
                
                Graphics2D g2 = (Graphics2D) g.create();
                try {
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // 绘制圆角蓝色背景
                    Color bgColor = isEnabled() ? 
                        (getModel().isRollover() ? new Color(80, 140, 190) : new Color(70, 130, 180)) :
                        new Color(180, 180, 180);
                    g2.setColor(bgColor);
                    int arc = 8; // 圆角半径
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                    
                    // 绘制白色文字
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
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(width, height));
        button.setMinimumSize(new Dimension(width, height));
        button.setMaximumSize(new Dimension(width, height));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        button.setRolloverEnabled(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * 创建样式化的密码输入框
     */
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // 绘制白色背景
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                
                // 绘制边框
                if (hasFocus()) {
                    g2.setColor(new Color(70, 130, 180));
                    g2.setStroke(new BasicStroke(2.0f));
                } else {
                    g2.setColor(new Color(220, 225, 230));
                    g2.setStroke(new BasicStroke(1.0f));
                }
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        field.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        field.setOpaque(false);
        return field;
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 252));
        
        // 标题面板（带背景色和渐变效果）
        JPanel titlePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // 绘制渐变背景
                GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), 
                    0, getHeight(), new Color(60, 120, 170));
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(35, 0, 35, 0));
        JLabel titleLabel = new JLabel("校园羽毛球馆场地预约管理系统", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        add(titlePanel, BorderLayout.NORTH);

        // 选项卡 - 美化样式
        tabbedPane = new JTabbedPane(JTabbedPane.TOP) {
            @Override
            protected void paintComponent(Graphics g) {
                // 绘制与主背景一致的颜色
                g.setColor(new Color(248, 249, 252));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
            
            @Override
            public void paintChildren(Graphics g) {
                super.paintChildren(g);
            }
        };
        tabbedPane.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        tabbedPane.setBackground(new Color(248, 249, 252));
        tabbedPane.setOpaque(true);
        // 设置选项卡样式
        UIManager.put("TabbedPane.selected", new Color(255, 255, 255));
        UIManager.put("TabbedPane.tabAreaBackground", new Color(248, 249, 252));
        UIManager.put("TabbedPane.contentAreaColor", new Color(255, 255, 255));
        
        // 学生登录面板 - 使用包装面板确保背景正确
        studentLoginPanel = createStudentLoginPanel();
        JPanel studentWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // 绘制背景
                g.setColor(new Color(255, 255, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        studentWrapper.setBackground(new Color(255, 255, 255));
        studentWrapper.setOpaque(true);
        studentWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        studentWrapper.add(studentLoginPanel, BorderLayout.CENTER);
        tabbedPane.addTab("学生登录", studentWrapper);
        
        // 学生注册面板 - 使用包装面板确保背景正确
        studentRegisterPanel = createStudentRegisterPanel();
        JPanel registerWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // 绘制背景
                g.setColor(new Color(255, 255, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        registerWrapper.setBackground(new Color(255, 255, 255));
        registerWrapper.setOpaque(true);
        registerWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        registerWrapper.add(studentRegisterPanel, BorderLayout.CENTER);
        tabbedPane.addTab("学生注册", registerWrapper);
        
        // 管理员登录面板 - 使用包装面板确保背景正确
        adminLoginPanel = createAdminLoginPanel();
        JPanel adminWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                // 绘制背景
                g.setColor(new Color(255, 255, 255));
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        adminWrapper.setBackground(new Color(255, 255, 255));
        adminWrapper.setOpaque(true);
        adminWrapper.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 235, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        adminWrapper.add(adminLoginPanel, BorderLayout.CENTER);
        tabbedPane.addTab("管理员登录", adminWrapper);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 80, 30, 80));
        centerPanel.setBackground(new Color(248, 249, 252));
        centerPanel.setOpaque(true);
        centerPanel.add(tabbedPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JPanel createStudentLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 10, 15);  // 减少按钮上方的间距
        gbc.anchor = GridBagConstraints.WEST;

        JLabel studentIdLabel = new JLabel("学号：");
        studentIdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        studentIdLabel.setForeground(new Color(60, 60, 60));
        JTextField studentIdField = createStyledTextField();
        studentIdField.setPreferredSize(new Dimension(280, 38));
        JButton loginButton = createStyledButton("登录", 220, 48);

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(studentIdLabel, gbc);
        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 15, 15);  // 按钮上方使用更大的间距，但确保背景透明
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
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 10, 15);  // 减少按钮上方的间距
        gbc.anchor = GridBagConstraints.WEST;

        JLabel studentIdLabel = new JLabel("学号：");
        studentIdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        studentIdLabel.setForeground(new Color(60, 60, 60));
        JTextField studentIdField = createStyledTextField();
        studentIdField.setPreferredSize(new Dimension(280, 38));
        
        JLabel nameLabel = new JLabel("姓名：");
        nameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        nameLabel.setForeground(new Color(60, 60, 60));
        JTextField nameField = createStyledTextField();
        nameField.setPreferredSize(new Dimension(280, 38));
        
        JLabel phoneLabel = new JLabel("手机号：");
        phoneLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        phoneLabel.setForeground(new Color(60, 60, 60));
        JTextField phoneField = createStyledTextField();
        phoneField.setPreferredSize(new Dimension(280, 38));
        
        JButton registerButton = createStyledButton("注册", 220, 48);

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

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 15, 15);  // 按钮上方使用更大的间距，但确保背景透明
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
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 10, 15);  // 减少按钮上方的间距
        gbc.anchor = GridBagConstraints.WEST;

        JLabel adminIdLabel = new JLabel("工号：");
        adminIdLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        adminIdLabel.setForeground(new Color(60, 60, 60));
        JTextField adminIdField = createStyledTextField();
        adminIdField.setPreferredSize(new Dimension(280, 38));
        
        JLabel passwordLabel = new JLabel("密码：");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passwordLabel.setForeground(new Color(60, 60, 60));
        JPasswordField passwordField = createStyledPasswordField();
        passwordField.setPreferredSize(new Dimension(280, 38));
        
        JButton loginButton = createStyledButton("登录", 220, 48);

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(adminIdLabel, gbc);
        gbc.gridx = 1;
        panel.add(adminIdField, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        panel.add(passwordLabel, gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 15, 15, 15);  // 按钮上方使用更大的间距，但确保背景透明
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

