package org.jeecg.modules.mzx.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 项目结算明细
 *
 * @author xcom
 * @date 2024/3/4
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizProjectBillingDetail implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 提成占比
     */
    private BigDecimal commission;
    /**
     * 服务标记
     */
    private Boolean serviceFlag;
    /**
     * 是否删除
     */
    @TableLogic
    private Boolean delFlag;
    /**
     * 创建人
     */
    private String createBy;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 更新人
     */
    private String updateBy;
    /**
     * 更新时间
     */
    private Date updateTime;

}
