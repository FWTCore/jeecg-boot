package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectBilling;
import org.jeecg.modules.mzx.mapper.BizProjectBillingMapper;
import org.jeecg.modules.mzx.service.IBizProjectBillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目结算
 *
 * @author xcom
 * @date 2024/3/4
 */

@Service
@Slf4j
public class BizProjectBillingService extends ServiceImpl<BizProjectBillingMapper, BizProjectBilling> implements IBizProjectBillingService {

    @Autowired
    private BizProjectBillingMapper projectBillingMapper;



}
