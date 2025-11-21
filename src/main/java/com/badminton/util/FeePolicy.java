package com.badminton.util;

import com.badminton.model.CourtType;
import com.badminton.model.TimeSlot;

/**
 * 费用策略接口
 */
public interface FeePolicy {
    /**
     * 计算预约费用
     * @param type 场地类型
     * @param slot 预约时段
     * @return 费用
     */
    double computeFee(CourtType type, TimeSlot slot);
}

