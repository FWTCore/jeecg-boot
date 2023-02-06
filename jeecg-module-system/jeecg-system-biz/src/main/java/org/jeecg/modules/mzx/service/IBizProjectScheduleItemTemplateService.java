package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemTemplate;

import java.util.List;

    public interface IBizProjectScheduleItemTemplateService extends IService<BizProjectScheduleItemTemplate> {

    /**
     * 删除本下级数据
     * @param ids
     */
    void deleteBatchWithChildren(List<String> ids);

}
