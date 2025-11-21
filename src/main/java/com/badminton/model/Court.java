package com.badminton.model;

/**
 * 场地实体类
 */
public class Court {
    private String courtId; // 场地编号
    private CourtType type;
    private CourtStatus status;
    private double baseScore; // 可用于评分统计基础值

    public Court() {
    }

    public Court(String courtId, CourtType type, CourtStatus status, double baseScore) {
        this.courtId = courtId;
        this.type = type;
        this.status = status;
        this.baseScore = baseScore;
    }

    public String getCourtId() {
        return courtId;
    }

    public void setCourtId(String courtId) {
        this.courtId = courtId;
    }

    public CourtType getType() {
        return type;
    }

    public void setType(CourtType type) {
        this.type = type;
    }

    public CourtStatus getStatus() {
        return status;
    }

    public void setStatus(CourtStatus status) {
        this.status = status;
    }

    public double getBaseScore() {
        return baseScore;
    }

    public void setBaseScore(double baseScore) {
        this.baseScore = baseScore;
    }

    @Override
    public String toString() {
        return "Court{" +
                "courtId='" + courtId + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", baseScore=" + baseScore +
                '}';
    }
}

