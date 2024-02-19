package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.model.EmployeeProjectCostModel;

import java.util.Date;
import java.util.List;

public interface BizProjectCostMapper extends BaseMapper<BizProjectCost> {

    /**
     * 获取员工项目费用
     * @param startTime
     * @param endTime
     * @return
     */
    List<EmployeeProjectCostModel> listEmployeeProjectCost(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 获取员工每个项目费用
     * @param startTime
     * @param endTime
     * @return
     */
    List<EmployeeProjectCostModel> listEmployeeEachProjectCost(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}

