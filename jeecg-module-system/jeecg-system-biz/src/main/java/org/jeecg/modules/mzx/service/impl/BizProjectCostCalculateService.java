package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectCostCalculate;
import org.jeecg.modules.mzx.mapper.BizProjectCostCalculateMapper;
import org.jeecg.modules.mzx.service.IBizProjectCostCalculateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 项目成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

@Service
@Slf4j
public class BizProjectCostCalculateService extends ServiceImpl<BizProjectCostCalculateMapper, BizProjectCostCalculate> implements IBizProjectCostCalculateService {

    @Autowired
    private BizProjectCostCalculateMapper projectCostCalculateMapper;



}