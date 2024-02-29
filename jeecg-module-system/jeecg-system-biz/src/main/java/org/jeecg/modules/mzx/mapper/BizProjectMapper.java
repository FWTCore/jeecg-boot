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
}
