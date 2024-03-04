package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectBilling;
import org.jeecg.modules.mzx.entity.BizProjectBillingDetail;

import java.util.List;

/**
 * 项目结算明细
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface BizProjectBillingDetailMapper extends BaseMapper<BizProjectBillingDetail> {


    /**
     * 生成指定项目结算明细
     * @param projectIds
     */
    void generateProjectBillingDetail(@Param("projectIds") List<String> projectIds);


}
