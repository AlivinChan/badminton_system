package service;

import dao.DataManager;
import entity.Admin;
import entity.Student;

/**
 * 用户服务类，处理用户相关的业务逻辑
 */
public class UserService {
    private DataManager dataManager;

    public UserService() {
        this.dataManager = DataManager.getInstance();
    }

    /**
     * 学生注册
     */
    public boolean registerStudent(String studentId, String name, String phone) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("姓名不能为空");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("联系电话不能为空");
        }

        // 检查学号是否已存在
        if (dataManager.findStudentById(studentId) != null) {
            throw new IllegalArgumentException("该学号已注册");
        }

        Student student = new Student(studentId, name, phone);
        dataManager.addStudent(student);
        return true;
    }

    /**
     * 学生登录（验证学号是否存在）
     */
    public Student loginStudent(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        return dataManager.findStudentById(studentId);
    }

    /**
     * 管理员登录
     */
    public Admin loginAdmin(String adminId) {
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new IllegalArgumentException("工号不能为空");
        }
        return dataManager.findAdminById(adminId);
    }

    /**
     * 获取学生信息
     */
    public Student getStudentInfo(String studentId) {
        return dataManager.findStudentById(studentId);
    }
}

