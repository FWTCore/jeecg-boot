package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemUsage;

public interface IBizProjectScheduleItemUsageService extends IService<BizProjectScheduleItemUsage> {

    String getItemFullNameByItemId(String itemId);

}
