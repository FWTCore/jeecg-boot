package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectChangeDetail;

import java.util.Date;
import java.util.List;

/**
 * 项目变更明细
 *
 * @author xcom
 * @date 2024/8/5
 */

public interface IBizProjectChangeDetailService extends IService<BizProjectChangeDetail> {

    /**
     * 新增或更新项目变更信息
     *
     * @param projectId
     * @param triggerPeriod
     * @return
     */
    void insertOrUpdateData(String projectId, Integer triggerPeriod);

    /**
     * 新增或更新项目变更信息
     *
     * @param projectId
     * @return
     */
    void insertOrUpdateData(String projectId);

    /**
     * 新增或更新项目变更信息
     *
     * @param projectIds
     * @return
     */
    void insertOrUpdateData(List<String> projectIds);

    /**
     * 新增或更新项目变更信息
     * @param projectId
     * @param triggerPeriodDate
     */
    void insertOrUpdateData(String projectId, Date triggerPeriodDate);


}
