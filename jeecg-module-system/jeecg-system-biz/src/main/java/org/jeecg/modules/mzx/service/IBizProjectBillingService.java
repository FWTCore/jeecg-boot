package org.jeecg.modules.mzx.service;

import org.jeecg.modules.mzx.model.BizProjectBillingModel;

import java.util.List;

/**
 * 项目结算
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface IBizProjectBillingService {


    /**
     * 获取需要结算的项目数据
     * @return
     */
    List<BizProjectBillingModel> listBizProjectBillingModel();

    /**
     * 批量插入项目结算
     * @param data
     */
    void batchInsertBizProjectBilling(List<BizProjectBillingModel> data);


}
