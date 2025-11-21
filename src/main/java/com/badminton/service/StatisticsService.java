package com.badminton.service;

import com.badminton.model.Booking;
import com.badminton.model.BookingState;
import com.badminton.model.Court;
import com.badminton.persistence.JsonDB;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计服务类
 */
public class StatisticsService {
    private JsonDB db;

    public StatisticsService(JsonDB db) {
        this.db = db;
    }

    /**
     * 计算每个场地的平均评分
     * @return Map<场地ID, 平均评分>
     */
    public Map<String, Double> computeCourtRatings() {
        Map<String, Integer> ratingSums = new HashMap<>();
        Map<String, Integer> ratingCounts = new HashMap<>();
        Map<String, Double> result = new HashMap<>();

        Booking[] bookings = db.getBookings();

        for (int i = 0; i < db.getBookingCount(); i++) {
            Booking booking = bookings[i];
            if (booking == null) continue;

            String courtId = booking.getCourtId();
            int rating = booking.getRating();

            // 只统计已评分的预约（rating > 0）
            if (rating > 0) {
                ratingSums.put(courtId, ratingSums.getOrDefault(courtId, 0) + rating);
                ratingCounts.put(courtId, ratingCounts.getOrDefault(courtId, 0) + 1);
            }
        }

        // 计算平均分
        for (String courtId : ratingSums.keySet()) {
            int sum = ratingSums.get(courtId);
            int count = ratingCounts.get(courtId);
            result.put(courtId, count > 0 ? (double) sum / count : 0.0);
        }

        return result;
    }

    /**
     * 统计指定时间段的预约数量
     */
    public int bookingCountsByPeriod(LocalDate startDate, LocalDate endDate) {
        int count = 0;
        Booking[] bookings = db.getBookings();

        for (int i = 0; i < db.getBookingCount(); i++) {
            Booking booking = bookings[i];
            if (booking == null) continue;

            LocalDate bookingDate = booking.getSlot().getDate();
            if (!bookingDate.isBefore(startDate) && !bookingDate.isAfter(endDate)) {
                count++;
            }
        }

        return count;
    }

    /**
     * 收入统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总收入
     */
    public double earningsReport(LocalDate startDate, LocalDate endDate) {
        double totalEarnings = 0.0;
        Booking[] bookings = db.getBookings();

        for (int i = 0; i < db.getBookingCount(); i++) {
            Booking booking = bookings[i];
            if (booking == null) continue;

            // 只统计已完成的预约
            if (booking.getState() != BookingState.COMPLETED) {
                continue;
            }

            LocalDate bookingDate = booking.getSlot().getDate();
            if (!bookingDate.isBefore(startDate) && !bookingDate.isAfter(endDate)) {
                totalEarnings += booking.getFee();
            }
        }

        return totalEarnings;
    }

    /**
     * 获取所有场地的评分统计（包含场地名称）
     */
    public Map<String, Map<String, Object>> getCourtRatingStatistics() {
        Map<String, Double> ratings = computeCourtRatings();
        Map<String, Map<String, Object>> result = new HashMap<>();

        Court[] courts = db.getCourts();
        for (int i = 0; i < db.getCourtCount(); i++) {
            Court court = courts[i];
            if (court == null) continue;

            Map<String, Object> stats = new HashMap<>();
            stats.put("courtId", court.getCourtId());
            stats.put("type", court.getType());
            stats.put("averageRating", ratings.getOrDefault(court.getCourtId(), 0.0));
            
            // 统计评分次数
            int ratingCount = 0;
            Booking[] bookings = db.getBookings();
            for (int j = 0; j < db.getBookingCount(); j++) {
                Booking booking = bookings[j];
                if (booking != null && booking.getCourtId().equals(court.getCourtId()) 
                    && booking.getRating() > 0) {
                    ratingCount++;
                }
            }
            stats.put("ratingCount", ratingCount);

            result.put(court.getCourtId(), stats);
        }

        return result;
    }
}

