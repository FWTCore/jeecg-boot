package org.jeecg.modules.mzx.service;

import java.util.List;

/**
 * 项目结算明细
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface IBizProjectBillingDetailService {


    /**
     * 生成指定项目结算明细
     * @param projectIds
     */
    void generateProjectBillingDetail(List<String> projectIds);
}
