package org.jeecg.modules.mzx.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizProjectChangeDetail;

import java.util.List;

/**
 * 项目变更明细
 *
 * @author xcom
 * @date 2024/8/5
 */

public interface BizProjectChangeDetailMapper extends BaseMapper<BizProjectChangeDetail> {

    /**
     * 新增或更新项目变更信息
     *
     * @param projectId
     * @param triggerPeriod
     * @return
     */
    void insertOrUpdateData(@Param("projectId") String projectId, @Param("triggerPeriod") Integer triggerPeriod);

}
