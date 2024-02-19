package org.jeecg.modules.mzx.mapper;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工时mapper
 *
 * @author xcom
 * @date 2024/2/18
 */

public interface BizWorkHoursMapper {


    /**
     * 获取总工时
     *
     * @param staffId
     * @param startTime
     * @param endTime
     * @return
     */
    BigDecimal getTotalWorkHours(@Param("staffId") String staffId, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

}
