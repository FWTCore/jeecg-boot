package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizOvertimeRecord;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 加班记录Mapper
 */
public interface BizOvertimeRecordMapper extends BaseMapper<BizOvertimeRecord> {

    /**
     * 获取员工指定日期范围内的加班总时长（已确认的）
     *
     * @param staffId 员工ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 加班总时长（小时）
     */
    BigDecimal getTotalConfirmedOvertimeHours(@Param("staffId") String staffId,
                                               @Param("startTime") Date startTime,
                                               @Param("endTime") Date endTime);

    /**
     * 获取员工指定日期的加班时长（已确认的）
     *
     * @param staffId 员工ID
     * @param overtimeDate 加班日期
     * @return 加班时长（小时）
     */
    BigDecimal getConfirmedOvertimeHoursByDate(@Param("staffId") String staffId,
                                                @Param("overtimeDate") Date overtimeDate);
}