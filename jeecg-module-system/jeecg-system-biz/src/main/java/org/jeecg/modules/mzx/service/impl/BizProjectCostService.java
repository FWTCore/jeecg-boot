package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.mapper.BizProjectCostMapper;
import org.jeecg.modules.mzx.service.IBizProjectCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizProjectCostService extends ServiceImpl<BizProjectCostMapper, BizProjectCost> implements IBizProjectCostService {

    @Autowired
    private BizProjectCostMapper projectCostMapper;

}
