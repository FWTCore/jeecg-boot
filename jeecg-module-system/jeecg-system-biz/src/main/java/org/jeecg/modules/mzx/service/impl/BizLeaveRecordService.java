package org.jeecg.modules.mzx.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.mzx.entity.BizLeaveRecord;
import org.jeecg.modules.mzx.mapper.BizLeaveRecordMapper;
import org.jeecg.modules.mzx.service.IBizLeaveRecordService;
import org.jeecg.modules.mzx.vo.EmployeeLeaveStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 调休使用记录服务实现
 *
 * @author xcom
 * @date 2024/06/28
 */
@Service
@Slf4j
public class BizLeaveRecordService extends ServiceImpl<BizLeaveRecordMapper, BizLeaveRecord>
        implements IBizLeaveRecordService {

    @Autowired
    private BizLeaveRecordMapper leaveRecordMapper;

    @Override
    public IPage<EmployeeLeaveStatisticsVO> getLeaveStatisticsPage(Integer pageNo, Integer pageSize, String employeeId, String employeeName) {
        Page<EmployeeLeaveStatisticsVO> page = new Page<>(pageNo, pageSize);
        IPage<EmployeeLeaveStatisticsVO> resultPage = leaveRecordMapper.getLeaveStatisticsPage(page, employeeId, employeeName);

        // 计算剩余调休 = 已确认 - 已使用
        for (EmployeeLeaveStatisticsVO vo : resultPage.getRecords()) {
            BigDecimal confirmed = vo.getConfirmedOvertimeHours() != null ? vo.getConfirmedOvertimeHours() : BigDecimal.ZERO;
            BigDecimal used = vo.getUsedLeaveHours() != null ? vo.getUsedLeaveHours() : BigDecimal.ZERO;
            vo.setRemainingLeaveHours(confirmed.subtract(used));

            // 确保各字段不为null
            if (vo.getTotalOvertimeHours() == null) {
                vo.setTotalOvertimeHours(BigDecimal.ZERO);
            }
            if (vo.getPendingOvertimeHours() == null) {
                vo.setPendingOvertimeHours(BigDecimal.ZERO);
            }
            if (vo.getConfirmedOvertimeHours() == null) {
                vo.setConfirmedOvertimeHours(BigDecimal.ZERO);
            }
            if (vo.getUsedLeaveHours() == null) {
                vo.setUsedLeaveHours(BigDecimal.ZERO);
            }
        }

        return resultPage;
    }

    @Override
    public BigDecimal getUsedLeaveHours(String staffId) {
        if (StringUtils.isBlank(staffId)) {
            return BigDecimal.ZERO;
        }
        BigDecimal result = leaveRecordMapper.getUsedLeaveHours(staffId);
        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public boolean addLeaveRecord(BizLeaveRecord leaveRecord) {
        // 校验调休时长
        String validateMsg = validateLeaveHours(leaveRecord.getStaffId(), leaveRecord.getLeaveHours());
        if (StringUtils.isNotBlank(validateMsg)) {
            throw new JeecgBootException(validateMsg);
        }

        // 设置初始状态
        leaveRecord.setDelFlag(CommonConstant.DEL_FLAG_0);
        leaveRecord.setCreateTime(new Date());

        return this.save(leaveRecord);
    }

    @Override
    public String validateLeaveHours(String staffId, BigDecimal leaveHours) {
        if (ObjectUtils.isEmpty(leaveHours) || leaveHours.compareTo(BigDecimal.ZERO) <= 0) {
            return "调休时长必须大于0";
        }

        // 校验最多一位小数
        BigDecimal multiplied = leaveHours.multiply(new BigDecimal("10"));
        if (multiplied.stripTrailingZeros().scale() > 0) {
            return "调休时长最多只能一位小数";
        }

        // 校验不能超过剩余调休
        // 获取已确认加班时长（不限时间范围）
        BigDecimal confirmedHours = leaveRecordMapper.getConfirmedOvertimeHours(staffId);
        if (confirmedHours == null) {
            confirmedHours = BigDecimal.ZERO;
        }

        // 获取已使用调休时长
        BigDecimal usedHours = getUsedLeaveHours(staffId);

        // 计算剩余调休
        BigDecimal remaining = confirmedHours.subtract(usedHours);
        if (leaveHours.compareTo(remaining) > 0) {
            return "调休时长不能超过剩余调休时长(" + remaining + "小时)";
        }

        return null; // 校验通过
    }
}