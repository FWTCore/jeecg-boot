package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizProjectScheduleTemplate;

import java.util.List;

public interface IBizProjectScheduleTemplateService extends IService<BizProjectScheduleTemplate> {

    /**
     * 删除模板数据
     * @param ids
     */
    void deleteBatchWithChildren(List<String> ids);

}
