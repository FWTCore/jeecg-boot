package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectCostDetail;

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
     *
     * @param period
     * @return
     */
    List<String> listMonitorCostProject(@Param("period") Integer period);

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

    /**
     * 数据清零
     *
     * @param projectIds
     * @param period
     */
    void clearProjectCostDetailForProject(@Param("projectIds") List<String> projectIds, @Param("period") Integer period);


    /**
     * 数据清零
     * @param dataList
     * @param projectIds
     * @param period
     */
    @InterceptorIgnore(tenantLine = "true")
    void clearProjectCostDetailForData(@Param("dataList") List<BizProjectCostDetail> dataList, @Param("projectIds") List<String> projectIds, @Param("period") Integer period);

}
