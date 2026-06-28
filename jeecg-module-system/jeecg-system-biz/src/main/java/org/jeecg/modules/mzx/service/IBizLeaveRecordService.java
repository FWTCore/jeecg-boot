package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizLeaveRecord;
import org.jeecg.modules.mzx.vo.EmployeeLeaveStatisticsVO;

import java.math.BigDecimal;

/**
 * 调休使用记录服务接口
 *
 * @author xcom
 * @date 2024/06/28
 */
public interface IBizLeaveRecordService extends IService<BizLeaveRecord> {

    /**
     * 获取员工调休统计列表（分页）
     *
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @param employeeId 员工ID
     * @param employeeName 员工姓名（模糊搜索）
     * @return 员工调休统计列表
     */
    IPage<EmployeeLeaveStatisticsVO> getLeaveStatisticsPage(Integer pageNo, Integer pageSize, String employeeId, String employeeName);

    /**
     * 获取员工已使用调休时长
     *
     * @param staffId 员工ID
     * @return 已使用调休时长（小时）
     */
    BigDecimal getUsedLeaveHours(String staffId);

    /**
     * 新增调休使用记录
     *
     * @param leaveRecord 调休记录
     * @return 是否成功
     */
    boolean addLeaveRecord(BizLeaveRecord leaveRecord);

    /**
     * 校验调休时长是否有效
     *
     * @param staffId 员工ID
     * @param leaveHours 调休时长
     * @return 校验结果消息，null表示校验通过
     */
    String validateLeaveHours(String staffId, BigDecimal leaveHours);
}