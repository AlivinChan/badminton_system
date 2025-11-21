package com.badminton;

import com.badminton.ui.console.ConsoleUI;
import com.badminton.ui.swing.MainFrame;

import javax.swing.*;

/**
 * 主程序入口
 */
public class Main {
    public static void main(String[] args) {
        // 如果命令行参数包含 "console"，则启动控制台界面
        if (args.length > 0 && args[0].equals("console")) {
            ConsoleUI ui = new ConsoleUI();
            ui.start();
        } else {
            // 默认启动图形界面
            SwingUtilities.invokeLater(() -> {
                try {
                    // 设置系统外观
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new MainFrame().setVisible(true);
            });
        }
    }
}

