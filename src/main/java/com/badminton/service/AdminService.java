package com.badminton.service;

import com.badminton.model.Admin;
import com.badminton.model.Booking;
import com.badminton.model.BookingState;
import com.badminton.persistence.JsonDB;
import com.badminton.util.BusinessException;

/**
 * 管理员服务类
 */
public class AdminService {
    private JsonDB db;

    public AdminService(JsonDB db) {
        this.db = db;
    }

    /**
     * 管理员登录
     */
    public Admin loginAdmin(String adminId, String password) throws BusinessException {
        if (adminId == null || adminId.trim().isEmpty()) {
            throw new BusinessException("工号不能为空");
        }

        Admin admin = db.findAdminById(adminId);
        if (admin == null) {
            throw new BusinessException("管理员不存在");
        }

        if (password == null || !password.equals(admin.getPassword())) {
            throw new BusinessException("密码错误");
        }

        return admin;
    }

    /**
     * 确认预约完成
     */
    public void confirmBookingCompleted(String bookingId) throws BusinessException {
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }

        if (booking.getState() == BookingState.COMPLETED) {
            throw new BusinessException("该预约已完成");
        }

        if (booking.getState() == BookingState.CANCELLED) {
            throw new BusinessException("该预约已取消，无法确认完成");
        }

        booking.setState(BookingState.COMPLETED);
        db.saveToFile();
    }
}

