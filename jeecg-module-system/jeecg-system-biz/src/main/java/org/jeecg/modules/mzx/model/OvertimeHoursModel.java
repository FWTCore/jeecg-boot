package org.jeecg.modules.mzx.model;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 加班时长统计模型
 *
 * @author xcom
 * @date 2024/2/18
 */
@Data
public class OvertimeHoursModel {

    /**
     * 项目ID
     */
    private String projectId;

    /**
     * 加班时长（小时）
     */
    private BigDecimal overtimeHours;
}
