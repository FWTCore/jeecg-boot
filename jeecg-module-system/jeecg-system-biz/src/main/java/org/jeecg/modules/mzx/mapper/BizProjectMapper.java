package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProject;

import java.util.List;

public interface BizProjectMapper extends BaseMapper<BizProject> {

    /**
     * 更新项目状态为回款
     * @param ids
     * @param userName
     */
    void updateProjectPayment(@Param("ids") List<String> ids,@Param("userName") String userName);
    /**
     * 更新项目状态为结算中
     * @param ids
     * @param userName
     */
    void updateProjectBilling(@Param("ids") List<String> ids,@Param("userName") String userName);
    /**
     * 更新项目状态为已完成
     * @param ids
     * @param userName
     */
    void updateProjectFinish(@Param("ids") List<String> ids,@Param("userName") String userName);
}
