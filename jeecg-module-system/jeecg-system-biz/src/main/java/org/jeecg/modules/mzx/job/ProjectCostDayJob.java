package org.jeecg.modules.mzx.job;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.mzx.service.IBizProjectChangeDetailService;
import org.jeecg.modules.mzx.service.IBizProjectCostCalculateService;
import org.jeecg.modules.mzx.service.IBizProjectCostDetailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * 项目成本job 天维度
 *
 * @author xcom
 * @date 2024/2/18
 */

@Slf4j
@Component
public class ProjectCostDayJob implements Job {


    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Autowired
    private IBizProjectCostCalculateService projectCostCalculateService;

    @Autowired
    private IBizProjectCostDetailService projectCostDetailService;

    @Autowired
    private IBizProjectChangeDetailService bizProjectChangeDetailService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 本月时间
        Calendar instance = Calendar.getInstance();
        Date executeTime = instance.getTime();
        if (StringUtils.isNotBlank(this.parameter)) {
            executeTime = DateUtils.parseDatetime(this.parameter);
        }

        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey());
        log.info(String.format("welcome %s!  带参数定时任务 ProjectCostDayJob !   时间:" + DateUtils.now(), this.parameter));
        try {
            log.info(String.format(" 处理时间【%s】", DateUtils.date2Str(instance.getTime(), DateUtils.yyyyMMdd.get())));

            // 项目人工成本核算
            projectCostDetailService.initProjectCostDetail(executeTime);
            // 项目成本核算
            projectCostCalculateService.initProjectCostCalculate(executeTime);
        } catch (Exception exception) {
            log.error("任务 ProjectCostDayJob 异常", exception);
        }
        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey() + "执行完成");
    }
}
