package org.jeecg.modules.mzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.mzx.entity.BizOvertimeRecord;
import org.jeecg.modules.mzx.model.OvertimeHoursModel;
import org.jeecg.modules.mzx.vo.BatchConfirmResultVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    /**
     * 按项目统计已确认的加班时长（不限时间范围）
     *
     * @param projectIds 项目ID列表
     * @return 项目加班时长汇总列表
     */
    List<OvertimeHoursModel> sumOvertimeHoursByProject(List<String> projectIds);

    /**
     * 删除加班记录（单个删除）
     *
     * @param id 加班记录ID
     * @return 是否成功
     */
    boolean deleteOvertimeRecord(String id);

    /**
     * 批量删除加班记录
     *
     * @param ids 加班记录ID列表
     * @return 是否成功
     */
    boolean deleteOvertimeRecordBatch(List<String> ids);

    /**
     * 批量确认加班记录
     * 自动忽略已确认的记录，只处理待确认状态的记录
     *
     * @param ids 加班记录ID列表
     * @param confirmerId 确认人ID
     * @param confirmerName 确认人姓名
     * @return 批量确认结果（包含成功、跳过、失败的数量和ID列表）
     */
    BatchConfirmResultVO batchConfirmOvertime(List<String> ids, String confirmerId, String confirmerName);
}