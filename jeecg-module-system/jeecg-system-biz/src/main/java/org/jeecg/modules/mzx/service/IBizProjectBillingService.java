package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectBilling;
import org.jeecg.modules.mzx.model.BizProjectBillingModel;
import org.jeecg.modules.mzx.model.BizProjectBillingVO;

import java.util.List;

/**
 * 项目结算
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface IBizProjectBillingService extends IService<BizProjectBilling> {


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

    /**
     *
     * @param projectBilling
     * @param pageSize
     * @param pageNo
     * @return
     */
    IPage<BizProjectBillingVO> pageProjectBilling(BizProjectBilling projectBilling, Integer pageSize, Integer pageNo);
}
