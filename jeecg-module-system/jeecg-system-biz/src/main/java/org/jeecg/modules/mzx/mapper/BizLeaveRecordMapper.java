package org.jeecg.modules.mzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.mzx.entity.BizLeaveRecord;
import org.jeecg.modules.mzx.vo.EmployeeLeaveStatisticsVO;

import java.math.BigDecimal;

/**
 * 调休使用记录Mapper
 *
 * @author xcom
 * @date 2024/06/28
 */
public interface BizLeaveRecordMapper extends BaseMapper<BizLeaveRecord> {

    /**
     * 获取员工已使用调休时长
     *
     * @param staffId 员工ID
     * @return 已使用调休时长（小时）
     */
    BigDecimal getUsedLeaveHours(@Param("staffId") String staffId);

    /**
     * 获取员工已确认加班时长（不限时间范围）
     *
     * @param staffId 员工ID
     * @return 已确认加班时长（小时）
     */
    BigDecimal getConfirmedOvertimeHours(@Param("staffId") String staffId);

    /**
     * 获取员工调休统计列表（分页）
     *
     * @param page 分页参数
     * @param employeeId 员工ID
     * @param employeeName 员工姓名（模糊搜索）
     * @return 员工调休统计列表
     */
    IPage<EmployeeLeaveStatisticsVO> getLeaveStatisticsPage(Page<EmployeeLeaveStatisticsVO> page,
                                                            @Param("employeeId") String employeeId,
                                                            @Param("employeeName") String employeeName);
}