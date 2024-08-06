package org.jeecg.modules.mzx.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.mzx.service.IBizProjectCostCalculateService;
import org.jeecg.modules.mzx.service.IBizProjectCostDetailService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 项目成本job 月维度
 *
 * @author xcom
 * @date 2024/2/18
 */

@Slf4j
@Component
public class ProjectCostMonthJob implements Job {


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

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 上一月 起止时间
        Calendar instance = Calendar.getInstance();
        if (StringUtils.isNotBlank(this.parameter)) {
            instance.setTime(Objects.requireNonNull(DateUtils.parseDatetime(this.parameter)));
        }
        instance.add(Calendar.MONTH, -1);
        Date executeTime = instance.getTime();

        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey());
        log.info(String.format("welcome %s!  带参数定时任务 ProjectCostMonthJob !   时间:" + DateUtils.now(), this.parameter));
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
