package org.jeecg.modules.mzx.service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工时服务
 *
 * @author xcom
 * @date 2024/2/18
 */

public interface IBizWorkHoursService {


    /**
     * 获取指定人总工时
     * @param staffId
     * @param startTime
     * @param endTime
     * @return
     */
    BigDecimal getTotalWorkHours(String staffId, Date startTime, Date endTime);


}
