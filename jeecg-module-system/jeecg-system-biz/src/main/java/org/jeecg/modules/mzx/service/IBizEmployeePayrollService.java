package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.jeecg.modules.mzx.entity.BizEmployeePayroll;

/**
 * 员工工资表
 *
 * @author xcom
 * @date 2024/2/6
 */

public interface IBizEmployeePayrollService extends IService<BizEmployeePayroll> {


    /**
     * 初始化上一个月工资
     * @return
     */
    void initLastMonthEmployeePayroll();

}

