package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 加班记录实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizOvertimeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 员工ID
     */
    private String staffId;

    /**
     * 员工姓名（冗余存储）
     */
    private String staffName;

    /**
     * 加班日期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date overtimeDate;

    /**
     * 加班时长（小时）
     */
    private BigDecimal overtimeHours;

    /**
     * 加班原因
     */
    private String overtimeReason;

    /**
     * 关联项目ID
     */
    private String projectId;

    /**
     * 项目名称（冗余存储）
     */
    private String projectName;

    /**
     * 项目进度进度明细id
     */
    private String projectScheduleUsageItemId;

    /**
     * 条目名称
     */
    private String scheduleName;

    /**
     * 服务方式（字典：project_schedule_service_type）
     */
    @Dict(dicCode = "project_schedule_service_type")
    private Integer serviceType;

    /**
     * 服务内容
     */
    private String serviceContent;

    /**
     * 确认状态（字典：overtime_confirm_status）
     */
    @Dict(dicCode = "overtime_confirm_status")
    private Integer confirmStatus;

    /**
     * 确认人ID
     */
    private String confirmerId;

    /**
     * 确认人姓名
     */
    private String confirmerName;

    /**
     * 确认时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date confirmTime;

    /**
     * 删除状态
     */
    @TableLogic
    private Integer delFlag;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}