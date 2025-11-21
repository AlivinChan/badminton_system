package com.badminton.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 预约实体类
 */
public class Booking implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bookingId;
    private Student student;
    private String courtId;
    private TimeSlot slot;
    private BookingState state;
    private double fee; // 预约费用（在创建或确认时计算）
    private int rating; // 学生对场地的评分（0-5），0表示未评分
    private transient LocalDateTime createdAt; // 使用 transient 标记，手动序列化

    public Booking() {
    }

    public Booking(String bookingId, Student student, String courtId, TimeSlot slot, 
                   BookingState state, double fee, Integer rating, LocalDateTime createdAt) {
        this.bookingId = bookingId;
        this.student = student;
        this.courtId = courtId;
        this.slot = slot;
        this.state = state;
        this.fee = fee;
        this.rating = rating != null ? rating : 0;
        this.createdAt = createdAt;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getCourtId() {
        return courtId;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public TimeSlot getSlot() {
        return slot;
    }

    public void setSlot(TimeSlot slot) {
        this.slot = slot;
    }

    public BookingState getState() {
        return state;
    }

    public void setState(BookingState state) {
        this.state = state;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("评分必须在0-5之间");
        }
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId='" + bookingId + '\'' +
                ", student=" + (student != null ? student.getStudentId() : "null") +
                ", courtId='" + courtId + '\'' +
                ", slot=" + slot +
                ", state=" + state +
                ", fee=" + fee +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                '}';
    }

    /**
     * 自定义序列化方法，处理 LocalDateTime 的序列化
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        // 手动序列化 LocalDateTime
        out.writeObject(createdAt != null ? createdAt.toString() : null);
    }

    /**
     * 自定义反序列化方法，处理 LocalDateTime 的反序列化
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // 手动反序列化 LocalDateTime
        String createdAtStr = (String) in.readObject();
        this.createdAt = createdAtStr != null ? LocalDateTime.parse(createdAtStr) : null;
    }
}

