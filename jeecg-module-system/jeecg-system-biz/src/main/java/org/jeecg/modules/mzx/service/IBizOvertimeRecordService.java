package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizOvertimeRecord;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 加班记录服务接口
 */
public interface IBizOvertimeRecordService extends IService<BizOvertimeRecord> {

    /**
     * 获取员工指定日期范围内的已确认加班总时长
     *
     * @param staffId 员工ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 加班总时长（小时）
     */
    BigDecimal getTotalConfirmedOvertimeHours(String staffId, Date startTime, Date endTime);

    /**
     * 获取员工指定日期的已确认加班时长
     *
     * @param staffId 员工ID
     * @param overtimeDate 加班日期
     * @return 加班时长（小时）
     */
    BigDecimal getConfirmedOvertimeHoursByDate(String staffId, Date overtimeDate);

    /**
     * 提交加班申请
     *
     * @param overtimeRecord 加班记录
     * @return 是否成功
     */
    boolean submitOvertime(BizOvertimeRecord overtimeRecord);

    /**
     * 确认加班申请
     *
     * @param id 加班记录ID
     * @param confirmerId 确认人ID
     * @param confirmerName 确认人姓名
     * @return 是否成功
     */
    boolean confirmOvertime(String id, String confirmerId, String confirmerName);

    /**
     * 校验加班时长是否有效
     *
     * @param overtimeHours 加班时长
     * @return 校验结果消息，null表示校验通过
     */
    String validateOvertimeHours(BigDecimal overtimeHours);

    /**
     * 校验同一员工同一天是否已存在加班记录
     *
     * @param staffId 员工ID
     * @param overtimeDate 加班日期
     * @param projectId 项目ID
     * @param excludeId 排除的记录ID（编辑时排除自身）
     * @return 校验结果消息，null表示校验通过
     */
    String validateOvertimeDate(String staffId, Date overtimeDate, String projectId, String excludeId);
}