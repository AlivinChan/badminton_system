package com.badminton.service;

import com.badminton.model.*;
import com.badminton.persistence.InMemoryDB;
import com.badminton.util.BusinessException;
import com.badminton.util.FeePolicy;

import java.time.LocalDateTime;

/**
 * 预约服务类
 */
public class BookingService {
    private InMemoryDB db;
    private FeePolicy feePolicy;

    public BookingService(InMemoryDB db, FeePolicy feePolicy) {
        this.db = db;
        this.feePolicy = feePolicy;
    }

    /**
     * 创建预约
     */
    public Booking createBooking(String studentId, String courtId, TimeSlot slot) throws BusinessException {
        Student student = db.findStudentById(studentId);
        if (student == null) {
            throw new BusinessException("学生不存在");
        }

        Court court = db.findCourtById(courtId);
        if (court == null) {
            throw new BusinessException("场地不存在");
        }

        if (court.getStatus() == CourtStatus.MAINTENANCE) {
            throw new BusinessException("该场地正在维护中，不可预约");
        }

        if (isConflict(courtId, slot)) {
            throw new BusinessException("该时段场地已被预约");
        }

        // 计算费用
        double fee = feePolicy.computeFee(court.getType(), slot);

        // 创建预约
        Booking booking = new Booking(
                db.generateBookingId(),
                student,
                courtId,
                slot,
                BookingState.PENDING,
                fee,
                0,
                LocalDateTime.now()
        );

        db.addBooking(booking);
        db.saveToFile();

        return booking;
    }

    /**
     * 取消预约
     */
    public void cancelBooking(String studentId, String bookingId) throws BusinessException {
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }

        // 检查权限
        if (!booking.getStudent().getStudentId().equals(studentId)) {
            throw new BusinessException("无权取消该预约");
        }

        // 检查状态
        if (booking.getState() == BookingState.CANCELLED) {
            throw new BusinessException("该预约已取消");
        }

        if (booking.getState() == BookingState.COMPLETED) {
            throw new BusinessException("该预约已完成，无法取消");
        }

        booking.setState(BookingState.CANCELLED);
        db.saveToFile();
    }

    /**
     * 获取学生的所有预约
     */
    public Booking[] getBookingsByStudent(String studentId) {
        java.util.List<Booking> result = new java.util.ArrayList<>();
        Booking[] bookings = db.getBookings();

        for (int i = 0; i < db.getBookingCount(); i++) {
            Booking booking = bookings[i];
            if (booking != null && booking.getStudent().getStudentId().equals(studentId)) {
                result.add(booking);
            }
        }

        return result.toArray(new Booking[0]);
    }

    /**
     * 检测预约冲突
     */
    public boolean isConflict(String courtId, TimeSlot slot) {
        Booking[] bookings = db.getBookings();

        for (int i = 0; i < db.getBookingCount(); i++) {
            Booking booking = bookings[i];
            if (booking == null) continue;

            if (!booking.getCourtId().equals(courtId)) continue;
            if (booking.getState() == BookingState.CANCELLED) continue;

            if (booking.getSlot().overlaps(slot)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 对预约进行评分
     */
    public void rateBooking(String studentId, String bookingId, int rating) throws BusinessException {
        Booking booking = db.findBookingById(bookingId);
        if (booking == null) {
            throw new BusinessException("预约不存在");
        }

        // 检查权限
        if (!booking.getStudent().getStudentId().equals(studentId)) {
            throw new BusinessException("无权对该预约评分");
        }

        // 检查状态
        if (booking.getState() != BookingState.COMPLETED) {
            throw new BusinessException("只能对已完成的预约进行评分");
        }

        booking.setRating(rating);
        db.saveToFile();
    }

    /**
     * 获取所有预约
     */
    public Booking[] getAllBookings() {
        return db.getBookings();
    }
}

