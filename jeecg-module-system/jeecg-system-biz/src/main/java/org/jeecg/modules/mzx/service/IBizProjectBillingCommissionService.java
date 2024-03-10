package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectBillingCommission;

import java.util.List;

/**
 * 项目结算提成
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface IBizProjectBillingCommissionService extends IService<BizProjectBillingCommission> {
    /**
     * 生成项目提成
     * @param projectId
     */
    void generateProjectBillingCommission(String projectId);

    /**
     * 更新提成发放时间
     * @param projectIds
     */
    void updateProjectBillingCommissionFinish(List<String> projectIds);
}
