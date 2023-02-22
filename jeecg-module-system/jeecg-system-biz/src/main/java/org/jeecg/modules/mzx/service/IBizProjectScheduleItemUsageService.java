package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemUsage;
import org.jeecg.modules.mzx.vo.ProjectScheduleVO;

import java.util.List;

public interface IBizProjectScheduleItemUsageService extends IService<BizProjectScheduleItemUsage> {

    String getItemFullNameByItemId(String itemId);

    List<ProjectScheduleVO> queryUsageSchedule(String projectId);

}
