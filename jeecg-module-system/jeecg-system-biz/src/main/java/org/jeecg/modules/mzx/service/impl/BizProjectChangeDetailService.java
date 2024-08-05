package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectChangeDetail;
import org.jeecg.modules.mzx.mapper.BizProjectChangeDetailMapper;
import org.jeecg.modules.mzx.service.IBizProjectChangeDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
