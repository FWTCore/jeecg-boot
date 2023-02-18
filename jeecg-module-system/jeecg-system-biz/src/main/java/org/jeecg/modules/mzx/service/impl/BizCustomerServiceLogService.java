package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizCustomerServiceLog;
import org.jeecg.modules.mzx.mapper.BizCustomerServiceLogMapper;
import org.jeecg.modules.mzx.service.IBizCustomerServiceLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BizCustomerServiceLogService extends ServiceImpl<BizCustomerServiceLogMapper, BizCustomerServiceLog> implements IBizCustomerServiceLogService {

    @Autowired
    private BizCustomerServiceLogMapper customerServiceLogMapper;

}