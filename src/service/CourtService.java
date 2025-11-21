package service;

import dao.DataManager;
import entity.Court;

import java.util.List;

/**
 * 场地服务类，处理场地相关的业务逻辑
 */
public class CourtService {
    private DataManager dataManager;

    public CourtService() {
        this.dataManager = DataManager.getInstance();
    }

    /**
     * 获取所有场地
     */
    public List<Court> getAllCourts() {
        return dataManager.getAllCourts();
    }

    /**
     * 添加场地
     */
    public void addCourt(String courtId, Court.CourtType type, Court.CourtStatus status) {
        if (courtId == null || courtId.trim().isEmpty()) {
            throw new IllegalArgumentException("场地编号不能为空");
        }
        if (dataManager.findCourtById(courtId) != null) {
            throw new IllegalArgumentException("场地编号已存在");
        }
        Court court = new Court(courtId, type, status);
        dataManager.addCourt(court);
    }

    /**
     * 更新场地状态
     */
    public void updateCourtStatus(String courtId, Court.CourtStatus status) {
        Court court = dataManager.findCourtById(courtId);
        if (court == null) {
            throw new IllegalArgumentException("场地不存在");
        }
        court.setStatus(status);
        dataManager.updateCourt(court);
    }
}

