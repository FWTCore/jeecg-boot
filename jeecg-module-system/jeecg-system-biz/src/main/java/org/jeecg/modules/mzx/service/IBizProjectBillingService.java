package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
     * 分页项目提成
     * @param page
     * @param query
     * @return
     */
    IPage<BizProjectBillingVO> pageProjectBilling(Page<BizProjectBilling> page, BizProjectBilling query);


    /**
     * 更新项目提成状态为已完成
     * @param ids
     */
    void updateProjectBillingFinish(List<String> ids);
}
