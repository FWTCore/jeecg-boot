package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizEmployeePayroll;
import org.jeecg.modules.mzx.mapper.BizEmployeePayrollMapper;
import org.jeecg.modules.mzx.service.IBizEmployeePayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 员工工资表
 *
 * @author xcom
 * @date 2024/2/6
 */

@Service
@Slf4j
public class BizEmployeePayrollService extends ServiceImpl<BizEmployeePayrollMapper, BizEmployeePayroll> implements IBizEmployeePayrollService {

    @Autowired
    private BizEmployeePayrollMapper employeePayrollMapper;



}