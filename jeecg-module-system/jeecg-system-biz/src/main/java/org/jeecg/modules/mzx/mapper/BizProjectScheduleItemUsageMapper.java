package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemUsage;
import org.jeecg.modules.mzx.vo.ProjectScheduleUsageDTO;

public interface BizProjectScheduleItemUsageMapper extends BaseMapper<BizProjectScheduleItemUsage> {

    Void CreateScheduleItemUsage(@Param("param") ProjectScheduleUsageDTO projectScheduleUsage);



}
