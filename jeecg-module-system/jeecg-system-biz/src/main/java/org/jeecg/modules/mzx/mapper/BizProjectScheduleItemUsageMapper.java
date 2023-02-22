package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemUsage;
import org.jeecg.modules.mzx.vo.ProjectScheduleUsageDTO;
import org.jeecg.modules.mzx.vo.ProjectScheduleVO;

import java.util.List;

public interface BizProjectScheduleItemUsageMapper extends BaseMapper<BizProjectScheduleItemUsage> {

    void CreateScheduleItemUsage(@Param("param") ProjectScheduleUsageDTO projectScheduleUsage);

    List<ProjectScheduleVO> queryUsageSchedule(@Param("projectId")String projectId);

}
