package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.mapper.BizProjectCostMapper;
import org.jeecg.modules.mzx.model.EmployeeProjectCostModel;
import org.jeecg.modules.mzx.service.IBizProjectCostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BizProjectCostService extends ServiceImpl<BizProjectCostMapper, BizProjectCost> implements IBizProjectCostService {

    @Autowired
    private BizProjectCostMapper projectCostMapper;

    @Override
    public List<EmployeeProjectCostModel> listEmployeeProjectCost(Integer startPeriod, Integer endPeriod) {
        if (ObjectUtils.isEmpty(startPeriod) || ObjectUtils.isEmpty(endPeriod)) {
            return new ArrayList<>();
        }
        return projectCostMapper.listEmployeeProjectCost(startPeriod, endPeriod);
    }

}
