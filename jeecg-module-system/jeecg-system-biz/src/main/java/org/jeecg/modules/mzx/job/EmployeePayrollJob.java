package org.jeecg.modules.mzx.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.mzx.service.IBizEmployeePayrollService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;

/**
 * 员工工资job
 *
 * @author xcom
 * @date 2024/2/7
 */

@Slf4j
@Component
public class EmployeePayrollJob implements Job {


    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Autowired
    private IBizEmployeePayrollService bizEmployeePayrollService;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey());
        log.info(String.format("welcome %s!  带参数定时任务 EmployeePayrollJob !   时间:" + DateUtils.now(), this.parameter));

        bizEmployeePayrollService.initLastMonthEmployeePayroll(this.parameter);
        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey() + "执行完成");

    }
}

