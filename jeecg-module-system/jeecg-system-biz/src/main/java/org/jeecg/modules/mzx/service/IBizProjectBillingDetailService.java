package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectBillingDetail;

import java.util.List;

/**
 * 项目结算明细
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface IBizProjectBillingDetailService extends IService<BizProjectBillingDetail> {


    /**
     * 生成指定项目结算明细
     * @param projectIds
     */
    void generateProjectBillingDetail(List<String> projectIds);
}
