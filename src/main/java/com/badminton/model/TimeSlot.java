package com.badminton.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约时段类
 */
public class TimeSlot {
    private LocalDate date;
    private LocalTime start; // 包含分钟
    private LocalTime end;

    public TimeSlot() {
    }

    public TimeSlot(LocalDate date, LocalTime start, LocalTime end) {
        this.date = date;
        this.start = start;
        this.end = end;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStart() {
        return start;
    }

    public void setStart(LocalTime start) {
        this.start = start;
    }

    public LocalTime getEnd() {
        return end;
    }

    public void setEnd(LocalTime end) {
        this.end = end;
    }

    /**
     * 检查两个时段是否重叠
     * 两时段不重叠当且仅当 this.end <= other.start || other.end <= this.start
     * 所以重叠条件是取反
     */
    public boolean overlaps(TimeSlot other) {
        if (other == null || !this.date.equals(other.date)) {
            return false; // 不同日期不重叠
        }
        // 重叠条件：!(this.end <= other.start || other.end <= this.start)
        return !(this.end.compareTo(other.start) <= 0 || other.end.compareTo(this.start) <= 0);
    }

    /**
     * 获取时段时长（分钟）
     */
    public long getDurationMinutes() {
        return java.time.Duration.between(start, end).toMinutes();
    }

    @Override
    public String toString() {
        return date + " " + start + "-" + end;
    }
}

