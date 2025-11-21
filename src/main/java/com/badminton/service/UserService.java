package com.badminton.service;

import com.badminton.model.Student;
import com.badminton.persistence.InMemoryDB;
import com.badminton.util.BusinessException;

import java.util.regex.Pattern;

/**
 * 用户服务类（学生注册/登录）
 */
public class UserService {
    private InMemoryDB db;
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    public UserService(InMemoryDB db) {
        this.db = db;
    }

    /**
     * 学生注册
     */
    public void registerStudent(Student student) throws BusinessException {
        if (student == null || student.getStudentId() == null || student.getStudentId().trim().isEmpty()) {
            throw new BusinessException("学号不能为空");
        }

        // 检查学号唯一性
        if (db.findStudentById(student.getStudentId()) != null) {
            throw new BusinessException("该学号已注册");
        }

        // 校验手机号格式
        if (student.getPhone() != null && !student.getPhone().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(student.getPhone()).matches()) {
                throw new BusinessException("手机号格式不正确（应为11位数字，以1开头）");
            }
        }

        db.addStudent(student);
        db.saveToFile();
    }

    /**
     * 学生登录（简化：无密码）
     */
    public Student loginStudent(String studentId) throws BusinessException {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new BusinessException("学号不能为空");
        }

        Student student = db.findStudentById(studentId);
        if (student == null) {
            throw new BusinessException("学号不存在，请先注册");
        }

        return student;
    }

    /**
     * 根据学号获取学生信息
     */
    public Student getStudentById(String studentId) {
        return db.findStudentById(studentId);
    }
}

