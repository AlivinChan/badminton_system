import ui.ConsoleUI;
import ui.SwingUI;

import javax.swing.*;

/**
 * 主程序入口
 */
public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("console")) {
            // 控制台模式
            ConsoleUI consoleUI = new ConsoleUI();
            consoleUI.start();
        } else {
            // 图形界面模式（默认）
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
}

