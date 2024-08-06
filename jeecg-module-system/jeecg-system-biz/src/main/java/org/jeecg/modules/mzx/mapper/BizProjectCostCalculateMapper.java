package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectCostCalculate;
import org.jeecg.modules.mzx.model.ProjectCostModel;

import java.math.BigDecimal;
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
     * 统计 指定项目的项目人工成本核算 累计
     *
     * @param projectIds
     * @return
     */
    List<ProjectCostModel> listProjectCostDetailSum(@Param("projectIds") List<String> projectIds);


    /**
     * 新增数据
     *
     * @param dataList
     */
    @InterceptorIgnore(tenantLine = "true")
    void insertProjectCostCalculate(@Param("dataList") List<BizProjectCostCalculate> dataList);

    /**
     * 更新数据
     *
     * @param dataList
     */
    @InterceptorIgnore(tenantLine = "true")
    void updateProjectCostCalculate(@Param("dataList") List<BizProjectCostCalculate> dataList);

}
