package org.jeecg.modules.mzx.job;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.mzx.model.BizProjectBillingModel;
import org.jeecg.modules.mzx.service.IBizProjectBillingDetailService;
import org.jeecg.modules.mzx.service.IBizProjectBillingService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 项目结算job
 *
 * @author xcom
 * @date 2024/3/4
 */

@Slf4j
@Component
public class ProjectBillingJob implements Job {


    /**
     * 若参数变量名修改 QuartzJobController中也需对应修改
     */
    private String parameter;

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    @Autowired
    private IBizProjectBillingService bizProjectBillingService;
    @Autowired
    private IBizProjectBillingDetailService bizProjectBillingDetailService;
    @Autowired
    private IBizProjectService projectService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey());
        log.info(String.format("welcome %s!  带参数定时任务 ProjectBillingJob !   时间:" + DateUtils.now(), this.parameter));
        try {

            List<BizProjectBillingModel> bizProjectBillingModels = bizProjectBillingService.listBizProjectBillingModel();
            if (CollectionUtil.isEmpty(bizProjectBillingModels)) {
                log.info("ProjectBillingJob 无数据处理 !   时间:" + DateUtils.now());
            }
            else {
                // 生成项目结算数据
                bizProjectBillingService.batchInsertBizProjectBilling(bizProjectBillingModels);
                // 获取项目id
                List<String> projectIdCollect = bizProjectBillingModels.stream().map(BizProjectBillingModel::getProjectId).collect(Collectors.toList());
                // 生成项目结算明细
                bizProjectBillingDetailService.generateProjectBillingDetail(projectIdCollect);
                // 扭转项目状态为结算中	25
                projectService.updateProjectBilling(projectIdCollect);
                // TODO 推送消息
            }
        } catch (Exception exception) {
            log.error("任务 ProjectCostDayJob 异常", exception);
        }
        log.info(" Job Execution key：" + jobExecutionContext.getJobDetail().getKey() + "执行完成");
    }
}
