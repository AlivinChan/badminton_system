package com.badminton.ui.console;

import com.badminton.model.*;
import com.badminton.persistence.JsonDB;
import com.badminton.service.*;
import com.badminton.util.BusinessException;
import com.badminton.util.FeePolicy;
import com.badminton.util.DefaultFeePolicy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 控制台用户界面
 */
public class ConsoleUI {
    private Scanner scanner;
    private JsonDB db;
    private UserService userService;
    private AdminService adminService;
    private BookingService bookingService;
    private CourtService courtService;
    private StatisticsService statisticsService;
    private Student currentStudent;
    private Admin currentAdmin;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public ConsoleUI() {
        scanner = new Scanner(System.in);
        db = JsonDB.loadFromFile();
        FeePolicy feePolicy = new DefaultFeePolicy();
        userService = new UserService(db);
        bookingService = new BookingService(db, feePolicy);
        courtService = new CourtService(db, bookingService);
        adminService = new AdminService(db);
        statisticsService = new StatisticsService(db);
        
        // 初始化默认数据（如果数据库为空）
        initializeDefaultData();
    }

    /**
     * 初始化默认数据
     */
    private void initializeDefaultData() {
        if (db.getCourtCount() == 0) {
            // 单打场地
            db.addCourt(new Court("C001", CourtType.SINGLES, CourtStatus.AVAILABLE, 0));
            db.addCourt(new Court("C002", CourtType.SINGLES, CourtStatus.AVAILABLE, 0));
            db.addCourt(new Court("C003", CourtType.SINGLES, CourtStatus.AVAILABLE, 0));
            db.addCourt(new Court("C004", CourtType.SINGLES, CourtStatus.AVAILABLE, 0));
            // 双打场地
            db.addCourt(new Court("C005", CourtType.DOUBLES, CourtStatus.AVAILABLE, 0));
            db.addCourt(new Court("C006", CourtType.DOUBLES, CourtStatus.AVAILABLE, 0));
            db.addCourt(new Court("C007", CourtType.DOUBLES, CourtStatus.AVAILABLE, 0));
            db.addCourt(new Court("C008", CourtType.DOUBLES, CourtStatus.AVAILABLE, 0));
            db.saveToFile();
        }

        if (db.getAdminCount() == 0) {
            // 创建默认管理员（工号：admin，密码：admin123）
            db.addAdmin(new Admin("admin", "管理员", "13800000000", "admin123"));
            db.saveToFile();
        }
    }

    /**
     * 启动主菜单
     */
    public void start() {
        while (true) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleStudentRegister();
                    break;
                case "2":
                    handleStudentLogin();
                    break;
                case "3":
                    handleAdminLogin();
                    break;
                case "4":
                    System.out.println("感谢使用，再见！");
                    return;
                default:
                    System.out.println("无效选项，请重新选择！");
            }
        }
    }

    private void printMainMenu() {
        System.out.println("\n================= 羽毛球馆预约系统 =================");
        System.out.println("1. 学生注册");
        System.out.println("2. 学生登录");
        System.out.println("3. 管理员登录");
        System.out.println("4. 退出");
        System.out.print("请输入选项：");
    }

    /**
     * 处理学生注册
     */
    private void handleStudentRegister() {
        System.out.println("\n--- 学生注册 ---");
        System.out.print("请输入学号：");
        String studentId = scanner.nextLine().trim();
        System.out.print("请输入姓名：");
        String name = scanner.nextLine().trim();
        System.out.print("请输入手机号：");
        String phone = scanner.nextLine().trim();

        try {
            Student student = new Student(studentId, name, phone);
            userService.registerStudent(student);
            System.out.println("注册成功！");
        } catch (BusinessException e) {
            System.out.println("注册失败：" + e.getMessage());
        }
    }

    /**
     * 处理学生登录
     */
    private void handleStudentLogin() {
        System.out.println("\n--- 学生登录 ---");
        System.out.print("请输入学号：");
        String studentId = scanner.nextLine().trim();

        try {
            currentStudent = userService.loginStudent(studentId);
            System.out.println("登录成功！欢迎，" + currentStudent.getName());
            studentMenu();
        } catch (BusinessException e) {
            System.out.println("登录失败：" + e.getMessage());
        }
    }

    /**
     * 学生菜单
     */
    private void studentMenu() {
        while (true) {
            System.out.println("\n--- 学生功能菜单 ---");
            System.out.println("1. 查看个人信息");
            System.out.println("2. 查询可用场地");
            System.out.println("3. 预约场地");
            System.out.println("4. 取消预约");
            System.out.println("5. 查看我的预约");
            System.out.println("6. 对已完成预约评分");
            System.out.println("7. 注销/返回");
            System.out.print("请输入选项：");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showStudentInfo();
                    break;
                case "2":
                    queryAvailableCourts();
                    break;
                case "3":
                    createBooking();
                    break;
                case "4":
                    cancelBooking();
                    break;
                case "5":
                    showMyBookings();
                    break;
                case "6":
                    rateBooking();
                    break;
                case "7":
                    currentStudent = null;
                    return;
                default:
                    System.out.println("无效选项，请重新选择！");
            }
        }
    }

    private void showStudentInfo() {
        System.out.println("\n--- 个人信息 ---");
        System.out.println("学号：" + currentStudent.getStudentId());
        System.out.println("姓名：" + currentStudent.getName());
        System.out.println("手机号：" + currentStudent.getPhone());
    }

    private void queryAvailableCourts() {
        try {
            System.out.println("\n--- 查询可用场地 ---");
            System.out.println("当天所有场地信息：");
            System.out.println();
            
            Court[] allCourts = courtService.getAllCourts();
            if (allCourts.length == 0) {
                System.out.println("暂无场地信息");
            } else {
                System.out.printf("%-12s %-10s %-12s%n", "场地编号", "类型", "状态");
                System.out.println("----------------------------------------");
                for (Court court : allCourts) {
                    System.out.printf("%-12s %-10s %-12s%n", 
                        court.getCourtId(), 
                        court.getType(), 
                        court.getStatus());
                }
                System.out.println();
                System.out.println("提示：场地状态为" + CourtStatus.AVAILABLE + "的场地可以预约");
            }
        } catch (Exception e) {
            System.out.println("查询失败：" + e.getMessage());
        }
    }

    private void createBooking() {
        try {
            System.out.println("\n--- 预约场地 ---");
            System.out.print("请输入场地编号：");
            String courtId = scanner.nextLine().trim();
            TimeSlot slot = inputTimeSlot();

            Booking booking = bookingService.createBooking(
                    currentStudent.getStudentId(), courtId, slot);
            System.out.println("预约成功！");
            System.out.println("预约编号：" + booking.getBookingId());
            System.out.println("费用：" + String.format("%.2f", booking.getFee()) + "元");
        } catch (BusinessException e) {
            System.out.println("预约失败：" + e.getMessage());
        } catch (Exception e) {
            System.out.println("预约失败：" + e.getMessage());
        }
    }

    private void cancelBooking() {
        try {
            System.out.println("\n--- 取消预约 ---");
            showMyBookings();
            System.out.print("请输入要取消的预约编号：");
            String bookingId = scanner.nextLine().trim();

            bookingService.cancelBooking(currentStudent.getStudentId(), bookingId);
            System.out.println("取消成功！");
        } catch (BusinessException e) {
            System.out.println("取消失败：" + e.getMessage());
        }
    }

    private void showMyBookings() {
        System.out.println("\n--- 我的预约 ---");
        Booking[] bookings = bookingService.getBookingsByStudent(currentStudent.getStudentId());
        if (bookings.length == 0) {
            System.out.println("暂无预约记录");
        } else {
            System.out.printf("%-12s %-8s %-15s %-20s %-10s %-8s %-6s%n",
                    "预约编号", "场地", "日期", "时段", "状态", "费用", "评分");
            for (Booking booking : bookings) {
                System.out.printf("%-12s %-8s %-15s %-20s %-10s %-8.2f %-6s%n",
                        booking.getBookingId(),
                        booking.getCourtId(),
                        booking.getSlot().getDate(),
                        booking.getSlot().getStart() + "-" + booking.getSlot().getEnd(),
                        booking.getState(),
                        booking.getFee(),
                        booking.getRating() > 0 ? booking.getRating() : "未评分");
            }
        }
    }

    private void rateBooking() {
        try {
            System.out.println("\n--- 对预约评分 ---");
            Booking[] completedBookings = bookingService.getBookingsByStudent(currentStudent.getStudentId());
            boolean hasCompleted = false;
            for (Booking booking : completedBookings) {
                if (booking.getState() == BookingState.COMPLETED) {
                    hasCompleted = true;
                    break;
                }
            }

            if (!hasCompleted) {
                System.out.println("暂无已完成的预约");
                return;
            }

            showMyBookings();
            System.out.print("请输入要评分的预约编号：");
            String bookingId = scanner.nextLine().trim();
            System.out.print("请输入评分（1-5）：");
            int rating = Integer.parseInt(scanner.nextLine().trim());

            bookingService.rateBooking(currentStudent.getStudentId(), bookingId, rating);
            System.out.println("评分成功！");
        } catch (BusinessException e) {
            System.out.println("评分失败：" + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("评分格式错误，请输入1-5之间的数字");
        } catch (Exception e) {
            System.out.println("评分失败：" + e.getMessage());
        }
    }

    /**
     * 处理管理员登录
     */
    private void handleAdminLogin() {
        System.out.println("\n--- 管理员登录 ---");
        System.out.print("请输入工号：");
        String adminId = scanner.nextLine().trim();
        System.out.print("请输入密码：");
        String password = scanner.nextLine().trim();

        try {
            currentAdmin = adminService.loginAdmin(adminId, password);
            System.out.println("登录成功！欢迎，" + currentAdmin.getName());
            adminMenu();
        } catch (BusinessException e) {
            System.out.println("登录失败：" + e.getMessage());
        }
    }

    /**
     * 管理员菜单
     */
    private void adminMenu() {
        while (true) {
            System.out.println("\n--- 管理员功能菜单 ---");
            System.out.println("1. 查看所有预约");
            System.out.println("2. 确认预约完成");
            System.out.println("3. 更改场地状态");
            System.out.println("4. 场地评分统计");
            System.out.println("5. 收入统计");
            System.out.println("6. 注销/返回");
            System.out.print("请输入选项：");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    showAllBookings();
                    break;
                case "2":
                    confirmBookingCompleted();
                    break;
                case "3":
                    changeCourtStatus();
                    break;
                case "4":
                    showCourtRatings();
                    break;
                case "5":
                    showEarningsReport();
                    break;
                case "6":
                    currentAdmin = null;
                    return;
                default:
                    System.out.println("无效选项，请重新选择！");
            }
        }
    }

    private void showAllBookings() {
        System.out.println("\n--- 所有预约 ---");
        Booking[] bookings = bookingService.getAllBookings();
        if (bookings.length == 0) {
            System.out.println("暂无预约记录");
        } else {
            System.out.printf("%-12s %-12s %-8s %-15s %-20s %-10s %-8s%n",
                    "预约编号", "学号", "场地", "日期", "时段", "状态", "费用");
            for (Booking booking : bookings) {
                System.out.printf("%-12s %-12s %-8s %-15s %-20s %-10s %-8.2f%n",
                        booking.getBookingId(),
                        booking.getStudent().getStudentId(),
                        booking.getCourtId(),
                        booking.getSlot().getDate(),
                        booking.getSlot().getStart() + "-" + booking.getSlot().getEnd(),
                        booking.getState(),
                        booking.getFee());
            }
        }
    }

    private void confirmBookingCompleted() {
        try {
            System.out.println("\n--- 确认预约完成 ---");
            showAllBookings();
            System.out.print("请输入预约编号：");
            String bookingId = scanner.nextLine().trim();

            adminService.confirmBookingCompleted(bookingId);
            System.out.println("确认成功！");
        } catch (BusinessException e) {
            System.out.println("确认失败：" + e.getMessage());
        }
    }

    private void changeCourtStatus() {
        try {
            System.out.println("\n--- 更改场地状态 ---");
            Court[] courts = courtService.getAllCourts();
            System.out.println("场地列表：");
            for (Court court : courts) {
                System.out.println("  " + court.getCourtId() + " - " + 
                                 court.getType() + " - " + court.getStatus());
            }

            System.out.print("请输入场地编号：");
            String courtId = scanner.nextLine().trim();
            System.out.print("请输入新状态（1-可用，2-维护中）：");
            String statusChoice = scanner.nextLine().trim();

            CourtStatus status = "1".equals(statusChoice) ? 
                    CourtStatus.AVAILABLE : CourtStatus.MAINTENANCE;

            courtService.changeCourtStatus(courtId, status);
            System.out.println("更改成功！");
        } catch (BusinessException e) {
            System.out.println("更改失败：" + e.getMessage());
        }
    }

    private void showCourtRatings() {
        System.out.println("\n--- 场地评分统计 ---");
        Map<String, Map<String, Object>> stats = statisticsService.getCourtRatingStatistics();
        if (stats.isEmpty()) {
            System.out.println("暂无评分数据");
        } else {
            System.out.printf("%-8s %-10s %-12s %-10s%n", "场地", "类型", "平均评分", "评分次数");
            for (Map.Entry<String, Map<String, Object>> entry : stats.entrySet()) {
                Map<String, Object> stat = entry.getValue();
                System.out.printf("%-8s %-10s %-12.2f %-10d%n",
                        stat.get("courtId"),
                        stat.get("type"),
                        stat.get("averageRating"),
                        stat.get("ratingCount"));
            }
        }
    }

    private void showEarningsReport() {
        System.out.println("\n--- 收入统计 ---");
        System.out.print("请输入开始日期（yyyy-MM-dd，留空表示全部）：");
        String startStr = scanner.nextLine().trim();
        System.out.print("请输入结束日期（yyyy-MM-dd，留空表示全部）：");
        String endStr = scanner.nextLine().trim();

        try {
            LocalDate startDate = startStr.isEmpty() ? 
                    LocalDate.of(2000, 1, 1) : LocalDate.parse(startStr, dateFormatter);
            LocalDate endDate = endStr.isEmpty() ? 
                    LocalDate.of(2100, 12, 31) : LocalDate.parse(endStr, dateFormatter);

            double earnings = statisticsService.earningsReport(startDate, endDate);
            System.out.println("总收入：" + String.format("%.2f", earnings) + "元");
        } catch (DateTimeParseException e) {
            System.out.println("日期格式错误，请使用 yyyy-MM-dd 格式");
        }
    }

    /**
     * 输入时段
     */
    private TimeSlot inputTimeSlot() throws DateTimeParseException {
        System.out.print("请输入日期（yyyy-MM-dd）：");
        String dateStr = scanner.nextLine().trim();
        LocalDate date = LocalDate.parse(dateStr, dateFormatter);

        System.out.print("请输入开始时间（HH:mm）：");
        String startStr = scanner.nextLine().trim();
        LocalTime start = LocalTime.parse(startStr, timeFormatter);

        System.out.print("请输入结束时间（HH:mm）：");
        String endStr = scanner.nextLine().trim();
        LocalTime end = LocalTime.parse(endStr, timeFormatter);

        return new TimeSlot(date, start, end);
    }
}

