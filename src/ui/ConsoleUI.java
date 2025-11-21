package ui;

import entity.*;
import service.CourtService;
import service.ReservationService;
import service.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * 控制台交互界面
 */
public class ConsoleUI {
    private Scanner scanner;
    private UserService userService;
    private ReservationService reservationService;
    private CourtService courtService;
    private Student currentStudent;
    private Admin currentAdmin;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ConsoleUI() {
        scanner = new Scanner(System.in);
        userService = new UserService();
        reservationService = new ReservationService();
        courtService = new CourtService();
        initializeData();
    }

    /**
     * 初始化一些测试数据
     */
    private void initializeData() {
        try {
            // 初始化管理员
            if (userService.loginAdmin("A001") == null) {
                dao.DataManager.getInstance().addAdmin(new Admin("A001", "管理员", "13800138000"));
            }
            // 初始化场地
            if (courtService.getAllCourts().isEmpty()) {
                courtService.addCourt("C001", Court.CourtType.SINGLES, Court.CourtStatus.AVAILABLE);
                courtService.addCourt("C002", Court.CourtType.SINGLES, Court.CourtStatus.AVAILABLE);
                courtService.addCourt("C003", Court.CourtType.DOUBLES, Court.CourtStatus.AVAILABLE);
                courtService.addCourt("C004", Court.CourtType.DOUBLES, Court.CourtStatus.AVAILABLE);
            }
        } catch (Exception e) {
            System.out.println("初始化数据失败: " + e.getMessage());
        }
    }

    /**
     * 主菜单
     */
    public void start() {
        while (true) {
            System.out.println("\n========== 校园羽毛球馆场地预约管理系统 ==========");
            System.out.println("1. 学生注册");
            System.out.println("2. 学生登录");
            System.out.println("3. 管理员登录");
            System.out.println("0. 退出系统");
            System.out.print("请选择: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    studentRegister();
                    break;
                case 2:
                    studentLogin();
                    break;
                case 3:
                    adminLogin();
                    break;
                case 0:
                    System.out.println("感谢使用！");
                    return;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    /**
     * 学生注册
     */
    private void studentRegister() {
        System.out.println("\n========== 学生注册 ==========");
        System.out.print("请输入学号: ");
        String studentId = scanner.nextLine();
        System.out.print("请输入姓名: ");
        String name = scanner.nextLine();
        System.out.print("请输入联系电话: ");
        String phone = scanner.nextLine();

        try {
            userService.registerStudent(studentId, name, phone);
            System.out.println("注册成功！");
        } catch (Exception e) {
            System.out.println("注册失败: " + e.getMessage());
        }
    }

    /**
     * 学生登录
     */
    private void studentLogin() {
        System.out.println("\n========== 学生登录 ==========");
        System.out.print("请输入学号: ");
        String studentId = scanner.nextLine();

        try {
            currentStudent = userService.loginStudent(studentId);
            if (currentStudent == null) {
                System.out.println("学号不存在，请先注册！");
                return;
            }
            System.out.println("登录成功！欢迎 " + currentStudent.getName());
            studentMenu();
        } catch (Exception e) {
            System.out.println("登录失败: " + e.getMessage());
        }
    }

    /**
     * 学生菜单
     */
    private void studentMenu() {
        while (true) {
            System.out.println("\n========== 学生功能菜单 ==========");
            System.out.println("1. 查看个人信息");
            System.out.println("2. 查询可用场地");
            System.out.println("3. 预约场地");
            System.out.println("4. 查看我的预约");
            System.out.println("5. 取消预约");
            System.out.println("0. 退出登录");
            System.out.print("请选择: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    showStudentInfo();
                    break;
                case 2:
                    queryAvailableCourts();
                    break;
                case 3:
                    makeReservation();
                    break;
                case 4:
                    showMyReservations();
                    break;
                case 5:
                    cancelReservation();
                    break;
                case 0:
                    currentStudent = null;
                    return;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    /**
     * 显示学生信息
     */
    private void showStudentInfo() {
        System.out.println("\n========== 个人信息 ==========");
        System.out.println("学号: " + currentStudent.getStudentId());
        System.out.println("姓名: " + currentStudent.getName());
        System.out.println("联系电话: " + currentStudent.getPhone());
    }

    /**
     * 查询可用场地
     */
    private void queryAvailableCourts() {
        System.out.println("\n========== 可用场地列表 ==========");
        List<Court> courts = reservationService.getAvailableCourts();
        if (courts.isEmpty()) {
            System.out.println("暂无可用场地");
            return;
        }
        System.out.printf("%-10s %-10s %-10s\n", "场地编号", "场地类型", "状态");
        System.out.println("-----------------------------------");
        for (Court court : courts) {
            System.out.printf("%-10s %-10s %-10s\n",
                    court.getCourtId(),
                    court.getType().getDescription(),
                    court.getStatus().getDescription());
        }
    }

    /**
     * 预约场地
     */
    private void makeReservation() {
        System.out.println("\n========== 预约场地 ==========");
        System.out.print("请输入场地编号: ");
        String courtId = scanner.nextLine();
        System.out.print("请输入开始时间 (格式: yyyy-MM-dd HH:mm): ");
        String startTimeStr = scanner.nextLine();
        System.out.print("请输入结束时间 (格式: yyyy-MM-dd HH:mm): ");
        String endTimeStr = scanner.nextLine();

        try {
            LocalDateTime startTime = LocalDateTime.parse(startTimeStr, formatter);
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr, formatter);
            Reservation reservation = reservationService.makeReservation(
                    currentStudent.getStudentId(), courtId, startTime, endTime);
            System.out.println("预约成功！预约编号: " + reservation.getReservationId());
        } catch (DateTimeParseException e) {
            System.out.println("时间格式错误，请使用 yyyy-MM-dd HH:mm 格式");
        } catch (Exception e) {
            System.out.println("预约失败: " + e.getMessage());
        }
    }

    /**
     * 查看我的预约
     */
    private void showMyReservations() {
        System.out.println("\n========== 我的预约 ==========");
        List<Reservation> reservations = reservationService.getMyReservations(currentStudent.getStudentId());
        if (reservations.isEmpty()) {
            System.out.println("暂无预约记录");
            return;
        }
        System.out.printf("%-12s %-10s %-20s %-20s %-10s\n",
                "预约编号", "场地编号", "开始时间", "结束时间", "状态");
        System.out.println("--------------------------------------------------------------------------------");
        for (Reservation r : reservations) {
            System.out.printf("%-12s %-10s %-20s %-20s %-10s\n",
                    r.getReservationId(),
                    r.getCourtId(),
                    r.getStartTime().format(formatter),
                    r.getEndTime().format(formatter),
                    r.getStatus().getDescription());
        }
    }

    /**
     * 取消预约
     */
    private void cancelReservation() {
        System.out.println("\n========== 取消预约 ==========");
        System.out.print("请输入预约编号: ");
        String reservationId = scanner.nextLine();

        try {
            reservationService.cancelReservation(reservationId, currentStudent.getStudentId());
            System.out.println("取消预约成功！");
        } catch (Exception e) {
            System.out.println("取消预约失败: " + e.getMessage());
        }
    }

    /**
     * 管理员登录
     */
    private void adminLogin() {
        System.out.println("\n========== 管理员登录 ==========");
        System.out.print("请输入工号: ");
        String adminId = scanner.nextLine();

        try {
            currentAdmin = userService.loginAdmin(adminId);
            if (currentAdmin == null) {
                System.out.println("工号不存在！");
                return;
            }
            System.out.println("登录成功！欢迎管理员 " + currentAdmin.getName());
            adminMenu();
        } catch (Exception e) {
            System.out.println("登录失败: " + e.getMessage());
        }
    }

    /**
     * 管理员菜单
     */
    private void adminMenu() {
        while (true) {
            System.out.println("\n========== 管理员功能菜单 ==========");
            System.out.println("1. 查看所有场地");
            System.out.println("2. 添加场地");
            System.out.println("3. 修改场地状态");
            System.out.println("4. 查看所有预约");
            System.out.println("5. 确认预约完成");
            System.out.println("0. 退出登录");
            System.out.print("请选择: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    showAllCourts();
                    break;
                case 2:
                    addCourt();
                    break;
                case 3:
                    updateCourtStatus();
                    break;
                case 4:
                    showAllReservations();
                    break;
                case 5:
                    confirmReservation();
                    break;
                case 0:
                    currentAdmin = null;
                    return;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    /**
     * 显示所有场地
     */
    private void showAllCourts() {
        System.out.println("\n========== 所有场地列表 ==========");
        List<Court> courts = courtService.getAllCourts();
        if (courts.isEmpty()) {
            System.out.println("暂无场地");
            return;
        }
        System.out.printf("%-10s %-10s %-10s\n", "场地编号", "场地类型", "状态");
        System.out.println("-----------------------------------");
        for (Court court : courts) {
            System.out.printf("%-10s %-10s %-10s\n",
                    court.getCourtId(),
                    court.getType().getDescription(),
                    court.getStatus().getDescription());
        }
    }

    /**
     * 添加场地
     */
    private void addCourt() {
        System.out.println("\n========== 添加场地 ==========");
        System.out.print("请输入场地编号: ");
        String courtId = scanner.nextLine();
        System.out.print("请输入场地类型 (1-单打, 2-双打): ");
        int typeChoice = getIntInput();
        Court.CourtType type = (typeChoice == 1) ? Court.CourtType.SINGLES : Court.CourtType.DOUBLES;
        System.out.print("请输入场地状态 (1-可用, 2-维修中): ");
        int statusChoice = getIntInput();
        Court.CourtStatus status = (statusChoice == 1) ? Court.CourtStatus.AVAILABLE : Court.CourtStatus.MAINTENANCE;

        try {
            courtService.addCourt(courtId, type, status);
            System.out.println("添加场地成功！");
        } catch (Exception e) {
            System.out.println("添加场地失败: " + e.getMessage());
        }
    }

    /**
     * 修改场地状态
     */
    private void updateCourtStatus() {
        System.out.println("\n========== 修改场地状态 ==========");
        System.out.print("请输入场地编号: ");
        String courtId = scanner.nextLine();
        System.out.print("请输入新状态 (1-可用, 2-维修中): ");
        int statusChoice = getIntInput();
        Court.CourtStatus status = (statusChoice == 1) ? Court.CourtStatus.AVAILABLE : Court.CourtStatus.MAINTENANCE;

        try {
            courtService.updateCourtStatus(courtId, status);
            System.out.println("修改场地状态成功！");
        } catch (Exception e) {
            System.out.println("修改场地状态失败: " + e.getMessage());
        }
    }

    /**
     * 显示所有预约
     */
    private void showAllReservations() {
        System.out.println("\n========== 所有预约列表 ==========");
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("暂无预约记录");
            return;
        }
        System.out.printf("%-12s %-12s %-10s %-20s %-20s %-10s\n",
                "预约编号", "学号", "场地编号", "开始时间", "结束时间", "状态");
        System.out.println("----------------------------------------------------------------------------------------");
        for (Reservation r : reservations) {
            System.out.printf("%-12s %-12s %-10s %-20s %-20s %-10s\n",
                    r.getReservationId(),
                    r.getStudentId(),
                    r.getCourtId(),
                    r.getStartTime().format(formatter),
                    r.getEndTime().format(formatter),
                    r.getStatus().getDescription());
        }
    }

    /**
     * 确认预约完成
     */
    private void confirmReservation() {
        System.out.println("\n========== 确认预约完成 ==========");
        System.out.print("请输入预约编号: ");
        String reservationId = scanner.nextLine();

        try {
            reservationService.confirmReservationCompleted(reservationId);
            System.out.println("确认预约完成成功！");
        } catch (Exception e) {
            System.out.println("确认预约完成失败: " + e.getMessage());
        }
    }

    /**
     * 获取整数输入
     */
    private int getIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}

