package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizEmployeeSalary;
import org.jeecg.modules.mzx.mapper.BizEmployeeSalaryMapper;
import org.jeecg.modules.mzx.service.IBizEmployeeSalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 员工薪资管理
 *
 * @author xcom
 * @date 2024/2/6
 */

@Service
@Slf4j
public class BizEmployeeSalaryService extends ServiceImpl<BizEmployeeSalaryMapper, BizEmployeeSalary> implements IBizEmployeeSalaryService {

    @Autowired
    private BizEmployeeSalaryMapper employeeSalaryMapper;



}