package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectCostDetail;

import java.util.Date;
import java.util.List;

/**
 * 项目人工成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

public interface BizProjectCostDetailMapper extends BaseMapper<BizProjectCostDetail> {


    /**
     * 获取需要计算成本项目id列表
     * 监控表 创建或变动
     * biz_project
     * biz_project_cost
     * biz_project_member
     * biz_project_schedule_item_usage
     * biz_project_schedule_log
     * biz_project_schedule_usage
     * biz_project_cost_detail
     *
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> listMonitorCostProject(@Param("startTime") Date startTime, @Param("endTime") Date endTime, @Param("period") Integer period);

    /**
     * 新增数据
     *
     * @param dataList
     */
    @InterceptorIgnore(tenantLine = "true")
    void insertProjectCostDetail(@Param("dataList") List<BizProjectCostDetail> dataList);

    /**
     * 更新数据
     *
     * @param dataList
     */
    @InterceptorIgnore(tenantLine = "true")
    void updateProjectCostDetail(@Param("dataList") List<BizProjectCostDetail> dataList);

}
