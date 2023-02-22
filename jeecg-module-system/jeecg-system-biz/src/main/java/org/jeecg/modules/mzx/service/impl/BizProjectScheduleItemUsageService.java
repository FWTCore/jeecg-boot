package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mchange.v2.lang.StringUtils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.mzx.entity.BizProjectCost;
import org.jeecg.modules.mzx.entity.BizProjectScheduleItemUsage;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleItemUsageMapper;
import org.jeecg.modules.mzx.mapper.BizProjectScheduleLogMapper;
import org.jeecg.modules.mzx.service.IBizProjectScheduleItemUsageService;
import org.jeecg.modules.mzx.vo.ProjectScheduleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BizProjectScheduleItemUsageService extends ServiceImpl<BizProjectScheduleItemUsageMapper, BizProjectScheduleItemUsage> implements IBizProjectScheduleItemUsageService {

    @Autowired
    private BizProjectScheduleItemUsageMapper projectScheduleItemUsageMapper;

    @Override
    public String getItemFullNameByItemId(String itemId) {
        BizProjectScheduleItemUsage entity = this.getById(itemId);
        if (ObjectUtil.isNull(entity)) {
            return null;
        }
        if (StringUtil.isNullOrEmpty(entity.getProjectScheduleItemTemplateParentId())) {
            return entity.getItemName();
        } else {
            QueryWrapper<BizProjectScheduleItemUsage> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("project_schedule_item_template_id", entity.getProjectScheduleItemTemplateParentId());
            queryWrapper.eq("project_id", entity.getProjectId());
            queryWrapper.eq("project_schedule_template_id", entity.getProjectScheduleTemplateId());
            queryWrapper.eq("project_schedule_usage_id", entity.getProjectScheduleUsageId());
            BizProjectScheduleItemUsage parentEntity = this.getOne(queryWrapper);
            return String.format("%s / %s", parentEntity.getItemName(), entity.getItemName());
        }
    }

    @Override
    public List<ProjectScheduleVO> queryUsageSchedule(String projectId) {
        return projectScheduleItemUsageMapper.queryUsageSchedule(projectId);
    }


}
