package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectScheduleLog;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleLogMapper;
import org.jeecg.modules.mzx.service.IBizProjectScheduleLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizProjectScheduleLogService extends ServiceImpl<BizProjectScheduleLogMapper, BizProjectScheduleLog> implements IBizProjectScheduleLogService {

    @Autowired
    private BizProjectScheduleLogMapper projectScheduleLogMapper;

}
