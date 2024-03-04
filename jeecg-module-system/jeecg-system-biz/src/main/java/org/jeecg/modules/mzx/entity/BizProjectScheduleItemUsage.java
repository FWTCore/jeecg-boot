package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectScheduleItemUsage implements Serializable {

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
     * 项目进度模板id
     */
    private String projectScheduleTemplateId;
    /**
     * 项目进度运用id
     */
    private String projectScheduleUsageId;
    /**
     * 项目进度明细模板id
     */
    private String projectScheduleItemTemplateId;
    /**
     * 项目进度明细模板父级id
     */
    private String projectScheduleItemTemplateParentId;
    /**
     * 条目名称
     */
    private String itemName;
    /**
     * 默认文本
     */
    private String defaultText;
    /**
     * 说明
     */
    private String directions;
    /**
     * 排序
     */
    private Integer sortOrder;

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

    /**
     * 提成占比
     */
    private BigDecimal commission;

}
