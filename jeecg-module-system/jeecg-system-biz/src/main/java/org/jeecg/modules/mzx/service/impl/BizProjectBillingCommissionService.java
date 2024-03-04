package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectBillingCommission;
import org.jeecg.modules.mzx.mapper.BizProjectBillingCommissionMapper;
import org.jeecg.modules.mzx.service.IBizProjectBillingCommissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目结算提成
 *
 * @author xcom
 * @date 2024/3/4
 */

@Service
@Slf4j
public class BizProjectBillingCommissionService extends ServiceImpl<BizProjectBillingCommissionMapper, BizProjectBillingCommission> implements IBizProjectBillingCommissionService {

    @Autowired
    private BizProjectBillingCommissionMapper projectBillingCommissionMapper;

}
