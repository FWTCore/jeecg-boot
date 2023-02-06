package org.jeecg.modules.mzx.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemTemplate;
import org.jeecg.modules.mzx.entity.BizProjectScheduleTemplate;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleItemTemplateMapper;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleTemplateMapper;
import org.jeecg.modules.mzx.service.IBizProjectScheduleTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class BizProjectScheduleTemplateService extends ServiceImpl<BizProjectScheduleTemplateMapper, BizProjectScheduleTemplate> implements IBizProjectScheduleTemplateService {

    @Autowired
    private BizProjectScheduleItemTemplateMapper projectScheduleItemTemplateMapper;

    @Override
    public void deleteBatchWithChildren(List<String> ids) {
        this.removeByIds(ids);
        projectScheduleItemTemplateMapper.delete (new LambdaQueryWrapper<BizProjectScheduleItemTemplate>().in(BizProjectScheduleItemTemplate::getProjectScheduleTemplateId,ids));
    }
}
