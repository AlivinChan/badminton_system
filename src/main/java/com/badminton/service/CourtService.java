package com.badminton.service;

import com.badminton.model.Court;
import com.badminton.model.CourtStatus;
import com.badminton.model.CourtType;
import com.badminton.model.TimeSlot;
import com.badminton.persistence.InMemoryDB;
import com.badminton.util.BusinessException;

import java.util.ArrayList;
import java.util.List;

/**
 * 场地服务类
 */
public class CourtService {
    private InMemoryDB db;
    private BookingService bookingService;

    public CourtService(InMemoryDB db, BookingService bookingService) {
        this.db = db;
        this.bookingService = bookingService;
    }

    /**
     * 列出指定时段可用的场地
     */
    public List<Court> listAvailableCourts(TimeSlot slot, CourtType type) {
        List<Court> availableCourts = new ArrayList<>();
        Court[] courts = db.getCourts();

        for (int i = 0; i < db.getCourtCount(); i++) {
            Court court = courts[i];
            if (court == null) continue;

            // 检查场地类型
            if (type != null && court.getType() != type) {
                continue;
            }

            // 检查场地状态
            if (court.getStatus() != CourtStatus.AVAILABLE) {
                continue;
            }

            // 检查是否有冲突
            if (!bookingService.isConflict(court.getCourtId(), slot)) {
                availableCourts.add(court);
            }
        }

        return availableCourts;
    }

    /**
     * 更改场地状态
     */
    public void changeCourtStatus(String courtId, CourtStatus status) throws BusinessException {
        Court court = db.findCourtById(courtId);
        if (court == null) {
            throw new BusinessException("场地不存在");
        }

        court.setStatus(status);
        db.saveToFile();
    }

    /**
     * 获取所有场地
     */
    public Court[] getAllCourts() {
        return db.getCourts();
    }

    /**
     * 根据ID获取场地
     */
    public Court getCourtById(String courtId) {
        return db.findCourtById(courtId);
    }
}

