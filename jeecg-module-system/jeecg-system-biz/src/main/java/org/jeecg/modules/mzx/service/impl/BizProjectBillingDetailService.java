package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.mzx.entity.BizProjectBillingDetail;
import org.jeecg.modules.mzx.mapper.BizProjectBillingDetailMapper;
import org.jeecg.modules.mzx.service.IBizProjectBillingDetailService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 项目结算明细
 *
 * @author xcom
 * @date 2024/3/4
 */

public class BizProjectBillingDetailService extends ServiceImpl<BizProjectBillingDetailMapper, BizProjectBillingDetail> implements IBizProjectBillingDetailService {

    @Autowired
    private BizProjectBillingDetailMapper projectBillingDetailMapper;

}
