package entity;

import java.time.LocalDateTime;

/**
 * 预约信息实体类
 */
public class Reservation {
    private String reservationId;       // 预约编号
    private String studentId;          // 学生学号
    private String courtId;            // 场地编号
    private LocalDateTime startTime;   // 预约开始时间
    private LocalDateTime endTime;     // 预约结束时间
    private ReservationStatus status;  // 预约状态(待使用/已完成/已取消)

    public enum ReservationStatus {
        PENDING("待使用"),
        COMPLETED("已完成"),
        CANCELLED("已取消");

        private final String description;

        ReservationStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public Reservation() {
    }

    public Reservation(String reservationId, String studentId, String courtId, 
                      LocalDateTime startTime, LocalDateTime endTime, ReservationStatus status) {
        this.reservationId = reservationId;
        this.studentId = studentId;
        this.courtId = courtId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourtId() {
        return courtId;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "reservationId='" + reservationId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", courtId='" + courtId + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status.getDescription() +
                '}';
    }
}

