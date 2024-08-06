package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectCostDetail;

import java.util.Date;

/**
 * 项目人工成本核算
 *
 * @author xcom
 * @date 2024/2/18
 */

public interface IBizProjectCostDetailService extends IService<BizProjectCostDetail> {


    /**
     * 项目人工成本 生成
     *
     * @param dateTime
     */
    void initProjectCostDetail(Date dateTime);


}

