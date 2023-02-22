package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectScheduleLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 项目id
     */
    private String projectId;

    /**
     * 项目名称
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
     * 员工Id
     */
    private String staffId;

    /**
     * 员工
     */
    private String staff;

    /**
     * 服务方式
     */
    @Dict(dicCode = "project_schedule_service_type")
    private String serviceType;

    /**
     * 服务内容
     */
    private String serviceContent;

    /**
     * 工时
     */
    private BigDecimal workHours;

    /**
     * 是否加班，0否，1是
     */
    private Integer overtimeFlag;

    /**
     * 加班时长
     */
    private BigDecimal overtime;

    /**
     * 是否完成，0否，1是
     */
    private Integer doneFlag;
    /**
     * 是否归档，0否，1是
     */
    private Integer archiveFlag;

    /**
     * 下一步计划时间
     */
    private Date nextPlanTime;

    /**
     * 下一步计划内容
     */
    private String nextPlanContent;

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
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;


}
