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
     * @param startPeriod
     * @param endPeriod
     * @return
     */
    List<EmployeeProjectCostModel> listEmployeeProjectCost(@Param("startPeriod") Integer startPeriod, @Param("endPeriod") Integer endPeriod);

}

