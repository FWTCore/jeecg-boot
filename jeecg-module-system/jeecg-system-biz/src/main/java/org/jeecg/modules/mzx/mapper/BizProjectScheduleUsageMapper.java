package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectScheduleUsage;
import org.jeecg.modules.mzx.vo.ProjectScheduleUsageDTO;

public interface BizProjectScheduleUsageMapper extends BaseMapper<BizProjectScheduleUsage> {

    void CreateScheduleUsage(@Param("param") ProjectScheduleUsageDTO projectScheduleUsage);

}
