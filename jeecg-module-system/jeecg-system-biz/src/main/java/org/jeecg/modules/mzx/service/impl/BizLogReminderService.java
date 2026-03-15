package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizLogReminder;
import org.jeecg.modules.mzx.mapper.BizLogReminderMapper;
import org.jeecg.modules.mzx.service.IBizLogReminderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 日志提醒 服务
 *
 * @author xcom
 * @date 2024/12/21
 */

@Service
@Slf4j
public class BizLogReminderService extends ServiceImpl<BizLogReminderMapper, BizLogReminder> implements IBizLogReminderService {

    @Resource
    private BizLogReminderMapper bizLogReminderMapper;


}
