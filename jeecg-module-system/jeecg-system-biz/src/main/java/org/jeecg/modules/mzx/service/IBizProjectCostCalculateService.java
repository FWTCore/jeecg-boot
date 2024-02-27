package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectCostCalculate;

import java.util.Date;

/**
 * 项目成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

public interface IBizProjectCostCalculateService extends IService<BizProjectCostCalculate> {


    /**
     * 项目成本 生成 ，开始时间和结束时间 为本月数据
     *
     * @param startTime
     * @param endTime
     */
    void initProjectCostCalculate(Date startTime, Date endTime);
}

