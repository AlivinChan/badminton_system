package entity;

/**
 * 羽毛球场地实体类
 */
public class Court {
    private String courtId;         // 场地编号
    private CourtType type;         // 场地类型(单打/双打)
    private CourtStatus status;     // 状态(可用/维修中)

    public enum CourtType {
        SINGLES("单打"),
        DOUBLES("双打");

        private final String description;

        CourtType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum CourtStatus {
        AVAILABLE("可用"),
        MAINTENANCE("维修中");

        private final String description;

        CourtStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public Court() {
    }

    public Court(String courtId, CourtType type, CourtStatus status) {
        this.courtId = courtId;
        this.type = type;
        this.status = status;
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

    @Override
    public String toString() {
        return "Court{" +
                "courtId='" + courtId + '\'' +
                ", type=" + type.getDescription() +
                ", status=" + status.getDescription() +
                '}';
    }
}

