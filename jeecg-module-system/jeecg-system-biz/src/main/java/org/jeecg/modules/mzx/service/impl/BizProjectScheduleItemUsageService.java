package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemUsage;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleItemUsageMapper;
import org.jeecg.modules.mzx.service.IBizProjectScheduleItemUsageService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BizProjectScheduleItemUsageService extends ServiceImpl<BizProjectScheduleItemUsageMapper, BizProjectScheduleItemUsage> implements IBizProjectScheduleItemUsageService {
}
