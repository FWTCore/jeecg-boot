package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectCostCalculate;

import java.util.Date;
import java.util.List;

/**
 * 项目成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

public interface BizProjectCostCalculateMapper extends BaseMapper<BizProjectCostCalculate> {


    /**
     * 获取需要计算成本项目id列表
     * 监控表 创建或变动
     * biz_project_cost_detail
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> listMonitorProjectCost(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}
