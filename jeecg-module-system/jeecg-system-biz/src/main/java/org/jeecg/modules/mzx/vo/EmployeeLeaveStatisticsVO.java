package org.jeecg.modules.mzx.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 员工调休统计VO
 *
 * @author xcom
 * @date 2024/06/28
 */
@Data
public class EmployeeLeaveStatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
     */
    private String employeeId;

    /**
     * 员工姓名
     */
    private String employeeName;

    /**
     * 总调休（小时）- 所有加班记录时长
     */
    private BigDecimal totalOvertimeHours;

    /**
     * 待确认调休（小时）- 确认状态=0的加班时长
     */
    private BigDecimal pendingOvertimeHours;

    /**
     * 已确认调休（小时）- 确认状态=1的加班时长
     */
    private BigDecimal confirmedOvertimeHours;

    /**
     * 已使用调休（小时）- 调休记录时长
     */
    private BigDecimal usedLeaveHours;

    /**
     * 剩余调休（小时）- 已确认 - 已使用
     */
    private BigDecimal remainingLeaveHours;
}