package service;

import dao.DataManager;
import entity.Court;
import entity.Reservation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 预约服务类，处理预约相关的业务逻辑
 */
public class ReservationService {
    private DataManager dataManager;

    public ReservationService() {
        this.dataManager = DataManager.getInstance();
    }

    /**
     * 查询可用场地
     */
    public List<Court> getAvailableCourts() {
        return dataManager.getAvailableCourts();
    }

    /**
     * 查询指定时间段的可用场地
     */
    public List<Court> getAvailableCourts(LocalDateTime startTime, LocalDateTime endTime) {
        List<Court> availableCourts = dataManager.getAvailableCourts();
        return availableCourts.stream()
                .filter(court -> dataManager.isCourtAvailable(court.getCourtId(), startTime, endTime))
                .collect(Collectors.toList());
    }

    /**
     * 预约场地
     */
    public Reservation makeReservation(String studentId, String courtId, 
                                       LocalDateTime startTime, LocalDateTime endTime) {
        // 参数验证
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new IllegalArgumentException("学号不能为空");
        }
        if (courtId == null || courtId.trim().isEmpty()) {
            throw new IllegalArgumentException("场地编号不能为空");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("预约时间不能为空");
        }
        if (startTime.isAfter(endTime) || startTime.isEqual(endTime)) {
            throw new IllegalArgumentException("开始时间必须早于结束时间");
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("不能预约过去的时间");
        }

        // 检查学生是否存在
        if (dataManager.findStudentById(studentId) == null) {
            throw new IllegalArgumentException("学生不存在");
        }

        // 检查场地是否存在且可用
        Court court = dataManager.findCourtById(courtId);
        if (court == null) {
            throw new IllegalArgumentException("场地不存在");
        }
        if (court.getStatus() != Court.CourtStatus.AVAILABLE) {
            throw new IllegalArgumentException("该场地不可用");
        }

        // 检查时间段是否冲突
        if (!dataManager.isCourtAvailable(courtId, startTime, endTime)) {
            throw new IllegalArgumentException("该时间段已被预约");
        }

        // 创建预约
        String reservationId = UUID.randomUUID().toString().substring(0, 8);
        Reservation reservation = new Reservation(
                reservationId,
                studentId,
                courtId,
                startTime,
                endTime,
                Reservation.ReservationStatus.PENDING
        );

        dataManager.addReservation(reservation);
        return reservation;
    }

    /**
     * 取消预约
     */
    public boolean cancelReservation(String reservationId, String studentId) {
        Reservation reservation = dataManager.findReservationById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("预约不存在");
        }
        if (!reservation.getStudentId().equals(studentId)) {
            throw new IllegalArgumentException("无权取消此预约");
        }
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException("只能取消待使用的预约");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);
        dataManager.updateReservation(reservation);
        return true;
    }

    /**
     * 查看我的预约
     */
    public List<Reservation> getMyReservations(String studentId) {
        return dataManager.getReservationsByStudentId(studentId);
    }

    /**
     * 管理员确认预约完成
     */
    public boolean confirmReservationCompleted(String reservationId) {
        Reservation reservation = dataManager.findReservationById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("预约不存在");
        }
        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new IllegalArgumentException("只能确认待使用的预约");
        }

        reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
        dataManager.updateReservation(reservation);
        return true;
    }

    /**
     * 获取所有预约
     */
    public List<Reservation> getAllReservations() {
        return dataManager.getAllReservations();
    }
}

