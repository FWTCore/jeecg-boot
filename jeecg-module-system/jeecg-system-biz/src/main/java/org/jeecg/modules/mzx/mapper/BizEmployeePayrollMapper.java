package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizEmployeePayroll;

import java.util.List;

/**
 * 员工工资表
 *
 * @author xcom
 * @date 2024/2/6
 */

public interface BizEmployeePayrollMapper extends BaseMapper<BizEmployeePayroll> {

    /**
     * 更新员工工资
     * @param list
     */
    @InterceptorIgnore(tenantLine = "true")
    void updateEmployeePayroll(@Param("list") List<BizEmployeePayroll> list);

}
