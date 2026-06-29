package org.jeecg.modules.mzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.mzx.entity.BizOvertimeRecord;
import org.jeecg.modules.mzx.entity.BizProject;
import org.jeecg.modules.mzx.mapper.BizOvertimeRecordMapper;
import org.jeecg.modules.mzx.model.OvertimeHoursModel;
import org.jeecg.modules.mzx.service.IBizOvertimeRecordService;
import org.jeecg.modules.mzx.service.IBizProjectChangeDetailService;
import org.jeecg.modules.mzx.service.IBizProjectScheduleItemUsageService;
import org.jeecg.modules.mzx.service.IBizProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 加班记录服务实现
 */
@Service
@Slf4j
public class BizOvertimeRecordService extends ServiceImpl<BizOvertimeRecordMapper, BizOvertimeRecord>
        implements IBizOvertimeRecordService {

    @Autowired
    private BizOvertimeRecordMapper overtimeRecordMapper;

    @Autowired
    private IBizProjectService projectService;

    @Autowired
    private IBizProjectScheduleItemUsageService projectScheduleItemUsageService;

    @Autowired
    private IBizProjectChangeDetailService projectChangeDetailService;

    /**
     * 加班时长上限（单次最多8小时）
     */
    private static final BigDecimal MAX_OVERTIME_PER_RECORD = new BigDecimal("8");

    @Override
    public BigDecimal getTotalConfirmedOvertimeHours(String staffId, Date startTime, Date endTime) {
        if (ObjectUtils.isEmpty(startTime) || ObjectUtils.isEmpty(endTime)) {
            return BigDecimal.ZERO;
        }
        return overtimeRecordMapper.getTotalConfirmedOvertimeHours(staffId, startTime, endTime);
    }

    @Override
    public BigDecimal getConfirmedOvertimeHoursByDate(String staffId, Date overtimeDate) {
        if (ObjectUtils.isEmpty(overtimeDate)) {
            return BigDecimal.ZERO;
        }
        return overtimeRecordMapper.getConfirmedOvertimeHoursByDate(staffId, overtimeDate);
    }

    @Override
    public boolean submitOvertime(BizOvertimeRecord overtimeRecord) {
        // 校验加班时长
        String validateMsg = validateOvertimeHours(overtimeRecord.getOvertimeHours());
        if (StringUtils.isNotBlank(validateMsg)) {
            throw new JeecgBootException(validateMsg);
        }

        // 校验同一员工同一项目同一天是否已存在加班记录
        boolean isNew = StringUtils.isBlank(overtimeRecord.getId());
        String excludeId = isNew ? null : overtimeRecord.getId();
        validateMsg = validateOvertimeDate(overtimeRecord.getStaffId(), overtimeRecord.getOvertimeDate(), overtimeRecord.getProjectId(), excludeId);
        if (StringUtils.isNotBlank(validateMsg)) {
            throw new JeecgBootException(validateMsg);
        }

        // 设置项目名称（如果关联了项目）
        if (StringUtils.isNotBlank(overtimeRecord.getProjectId())) {
            BizProject project = projectService.getById(overtimeRecord.getProjectId());
            if (project != null && !project.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
                overtimeRecord.setProjectName(project.getProjectName());
            }
        }

        // 设置进度条目名称（如果关联了进度明细）
        if (StringUtils.isNotBlank(overtimeRecord.getProjectScheduleUsageItemId())) {
            String scheduleName = projectScheduleItemUsageService.getItemFullNameByItemId(
                    overtimeRecord.getProjectScheduleUsageItemId());
            if (StringUtils.isNotBlank(scheduleName)) {
                overtimeRecord.setScheduleName(scheduleName);
            }
        }

        if (isNew) {
            // 新增：设置初始状态
            overtimeRecord.setConfirmStatus(0); // 待确认
            overtimeRecord.setDelFlag(CommonConstant.DEL_FLAG_0);
            overtimeRecord.setCreateTime(new Date());
            boolean result = this.save(overtimeRecord);
            // 记录项目变更（用于成本核算定时任务判断是否有数据变动）
            recordProjectChangeIfNeeded(result, overtimeRecord.getProjectId());
            return result;
        } else {
            // 更新
            boolean result = this.updateById(overtimeRecord);
            // 记录项目变更
            recordProjectChangeIfNeeded(result, overtimeRecord.getProjectId());
            return result;
        }
    }

    @Override
    public boolean confirmOvertime(String id, String confirmerId, String confirmerName) {
        BizOvertimeRecord record = this.getById(id);
        if (record == null || record.getDelFlag().equals(CommonConstant.DEL_FLAG_1)) {
            throw new JeecgBootException("未找到对应的加班记录");
        }
        if (record.getConfirmStatus() != 0) {
            throw new JeecgBootException("该加班记录已确认，不能重复确认");
        }

        record.setConfirmStatus(1); // 已确认
        record.setConfirmerId(confirmerId);
        record.setConfirmerName(confirmerName);
        record.setConfirmTime(new Date());
        record.setUpdateTime(new Date());

        boolean result = this.updateById(record);
        // 记录项目变更（确认后会影响加班时长统计）
        recordProjectChangeIfNeeded(result, record.getProjectId());
        return result;
    }

    @Override
    public String validateOvertimeHours(BigDecimal overtimeHours) {
        if (ObjectUtils.isEmpty(overtimeHours) || overtimeHours.compareTo(BigDecimal.ZERO) <= 0) {
            return "加班时长必须大于0";
        }
        // 校验最多一位小数
        BigDecimal multiplied = overtimeHours.multiply(new BigDecimal("10"));
        if (multiplied.stripTrailingZeros().scale() > 0) {
            return "加班时长最多只能一位小数";
        }
        // 校验单次时长上限
        if (overtimeHours.compareTo(MAX_OVERTIME_PER_RECORD) > 0) {
            return "单次加班时长不能超过" + MAX_OVERTIME_PER_RECORD + "小时";
        }
        return null; // 校验通过
    }

    @Override
    public String validateOvertimeDate(String staffId, Date overtimeDate, String projectId, String excludeId) {
        if (StringUtils.isBlank(staffId) || ObjectUtils.isEmpty(overtimeDate)) {
            return null;
        }

        LambdaQueryWrapper<BizOvertimeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizOvertimeRecord::getStaffId, staffId);
        queryWrapper.eq(BizOvertimeRecord::getOvertimeDate, overtimeDate);
        queryWrapper.eq(BizOvertimeRecord::getDelFlag, CommonConstant.DEL_FLAG_0);

        // 同一项目的判断
        if (StringUtils.isNotBlank(projectId)) {
            queryWrapper.eq(BizOvertimeRecord::getProjectId, projectId);
        }

        // 编辑时排除自身
        if (StringUtils.isNotBlank(excludeId)) {
            queryWrapper.ne(BizOvertimeRecord::getId, excludeId);
        }

        long count = this.count(queryWrapper);
        if (count > 0) {
            if (StringUtils.isNotBlank(projectId)) {
                return "该员工在同一项目的指定日期已存在加班记录，同一天同一项目只能填写一次加班";
            } else {
                return "该员工在指定日期已存在加班记录，同一天只能填写一次加班";
            }
        }

        return null; // 校验通过
    }

    @Override
    public List<OvertimeHoursModel> sumOvertimeHoursByProject(List<String> projectIds) {
        if (CollectionUtil.isEmpty(projectIds)) {
            return new ArrayList<>();
        }
        return overtimeRecordMapper.sumOvertimeHoursByProject(projectIds);
    }

    @Override
    public boolean deleteOvertimeRecord(String id) {
        BizOvertimeRecord record = this.getById(id);
        if (record == null) {
            return false;
        }
        boolean result = this.removeById(id);
        // 记录项目变更（删除后会影响加班时长统计）
        recordProjectChangeIfNeeded(result, record.getProjectId());
        return result;
    }

    @Override
    public boolean deleteOvertimeRecordBatch(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        // 先查询所有要删除的记录，获取关联的项目ID
        List<BizOvertimeRecord> records = this.listByIds(ids);
        if (CollectionUtil.isEmpty(records)) {
            return false;
        }

        boolean result = this.removeByIds(ids);

        // 批量记录项目变更（去重）
        if (result) {
            List<String> projectIds = records.stream()
                    .map(BizOvertimeRecord::getProjectId)
                    .filter(StringUtils::isNotBlank)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
            for (String projectId : projectIds) {
                recordProjectChangeIfNeeded(true, projectId);
            }
        }
        return result;
    }

    /**
     * 记录项目变更（用于成本核算定时任务判断是否有数据变动）
     * 使用当前时间触发变更，因为可能编辑/确认历史数据
     *
     * @param result    数据库操作结果
     * @param projectId 项目ID
     */
    private void recordProjectChangeIfNeeded(boolean result, String projectId) {
        if (result && StringUtils.isNotBlank(projectId)) {
            projectChangeDetailService.insertOrUpdateData(projectId);
        }
    }
}