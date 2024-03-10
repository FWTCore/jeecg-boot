package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectBillingCommission;

import java.util.Date;
import java.util.List;

/**
 * 项目结算提成
 *
 * @author xcom
 * @date 2024/3/4
 */

public interface BizProjectBillingCommissionMapper extends BaseMapper<BizProjectBillingCommission> {

    /**
     * 生成项目提成
     *
     * @param projectId
     */
    void generateProjectBillingCommission(@Param("projectId") String projectId);

    /**
     * 更新周期和发放时间
     *
     * @param ids
     * @param period
     * @param paymentTime
     * @param userName
     */
    void updateProjectBillingCommissionFinish(@Param("ids") List<String> ids, @Param("period") Integer period, @Param("paymentTime") Date paymentTime, @Param("userName") String userName);
}
