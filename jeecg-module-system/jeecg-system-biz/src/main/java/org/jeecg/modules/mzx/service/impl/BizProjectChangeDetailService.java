package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.mzx.entity.BizProjectChangeDetail;
import org.jeecg.modules.mzx.mapper.BizProjectChangeDetailMapper;
import org.jeecg.modules.mzx.service.IBizProjectChangeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

/**
 * 项目变更明细 service
 *
 * @author xcom
 * @date 2024/8/5
 */

@Service
@Slf4j
public class BizProjectChangeDetailService extends ServiceImpl<BizProjectChangeDetailMapper, BizProjectChangeDetail> implements IBizProjectChangeDetailService {

    @Autowired
    private BizProjectChangeDetailMapper projectChangeDetailMapper;

    @Override
    public void insertOrUpdateData(String projectId, Integer triggerPeriod) {
        projectChangeDetailMapper.insertOrUpdateData(projectId, triggerPeriod);
    }

    @Override
    public void insertOrUpdateData(String projectId) {
        if (StringUtils.isBlank(projectId)) {
            return;
        }

        Calendar cal = Calendar.getInstance();
        Integer triggerPeriod = cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
        insertOrUpdateData(projectId, triggerPeriod);

    }

    @Override
    public void insertOrUpdateData(List<String> projectIds) {
        if (CollectionUtils.isEmpty(projectIds)) {
            return;
        }
        Calendar cal = Calendar.getInstance();
        Integer triggerPeriod = cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH);
        for (String projectId : projectIds) {
            insertOrUpdateData(projectId, triggerPeriod);
        }
    }
}
