package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.model.EmployeeProjectCostModel;

import java.util.Date;
import java.util.List;

public interface IBizProjectCostService extends IService<BizProjectCost> {


    /**
     * 获取员工项目费用
     * @param startPeriod
     * @param endPeriod
     * @return
     */
    List<EmployeeProjectCostModel> listEmployeeProjectCost(Integer startPeriod, Integer endPeriod);



}

