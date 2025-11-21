package com.badminton.util;

import com.badminton.model.CourtType;
import com.badminton.model.TimeSlot;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * 默认费用策略实现
 * 工作日白天：10元/小时（单打）、15元/小时（双打）
 * 工作日晚间/周末：15元/小时（单打）、20元/小时（双打）
 * 晚间定义：18:00之后
 */
public class DefaultFeePolicy implements FeePolicy {
    // 工作日白天费率（元/小时）
    private static final double WEEKDAY_DAY_SINGLES = 10.0;
    private static final double WEEKDAY_DAY_DOUBLES = 15.0;
    
    // 工作日晚间/周末费率（元/小时）
    private static final double WEEKDAY_NIGHT_WEEKEND_SINGLES = 15.0;
    private static final double WEEKDAY_NIGHT_WEEKEND_DOUBLES = 20.0;
    
    private static final LocalTime EVENING_START = LocalTime.of(18, 0);

    @Override
    public double computeFee(CourtType type, TimeSlot slot) {
        DayOfWeek dayOfWeek = slot.getDate().getDayOfWeek();
        boolean isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
        boolean isEvening = slot.getStart().compareTo(EVENING_START) >= 0;
        
        double ratePerHour;
        if (isWeekend || isEvening) {
            // 周末或工作日晚间
            ratePerHour = (type == CourtType.SINGLES) ? 
                WEEKDAY_NIGHT_WEEKEND_SINGLES : WEEKDAY_NIGHT_WEEKEND_DOUBLES;
        } else {
            // 工作日白天
            ratePerHour = (type == CourtType.SINGLES) ? 
                WEEKDAY_DAY_SINGLES : WEEKDAY_DAY_DOUBLES;
        }
        
        // 按分钟计算精确费用
        long durationMinutes = slot.getDurationMinutes();
        return ratePerHour * durationMinutes / 60.0;
    }
}

